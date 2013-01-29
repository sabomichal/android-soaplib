/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * This class provides a generic transport interface.
 *
 * Changelog:
 * ANDROIDSOAP-14 - 2012-09-08
 * ANDROIDSOAP-6 - 2011-01-08
 * ANDROIDSOAP-5 - 2011-01-07
 * ANDROIDSOAP-1 - 2011-01-06
 *
 * @author Gábor AUTH <gabor.auth@javaforum.hu>
 * @author Chris Wolf
 * @author Kamil Bartoszek
 */
public abstract class Transport
{

  /**
   * The LOGGER instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Transport.class.getSimpleName());
  /**
   * The HTTP200 status code.
   */
  private static final int HTTP_STATUS_OK = 200;
  /**
   * The HTTP500 status code.
   */
  private static final int HTTP_STATUS_ERROR = 500;
  /**
   * Default connection timeout.
   */
  private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
  /**
   * The custom connection timeout.
   */
  private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
  /**
   * Default socket timeout.
   */
  private static final int DEFAULT_TIMEOUT = 6000;
  /**
   * The custom socket timeout.
   */
  private int socketTimeout = DEFAULT_TIMEOUT;
  /**
   * The default encoding.
   */
  private static final String DEFAULT_ENCODING = "UTF-8";
  /**
   * The password, it can be null.
   */
  private final String password;
  /**
   * The URL of the SOAP service.
   */
  private final String url;
  /**
   * The username, it can be null.
   */
  private final String username;

  /**
   * Creates a new instance.
   *
   * @param url The URL
   */
  public Transport(final String url)
  {
    this(url, null, null);
  }

  /**
   * Creates a new instance with authorization.
   *
   * @param url The URL
   * @param username The username
   * @param password The password
   */
  public Transport(final String url, final String username, final String password)
  {
    this.url = url;
    this.username = username;
    this.password = password;
  }

  /**
   * Call the service.
   *
   * @param <T> The return type
   * @param envelope The request envelope
   * @param resultClass The class of the result in the response
   * @return The response
   * @throws IOException An exception from SOAP message
   */
  public final <T, E> T call(final Envelope envelope, final Class<T> resultClass, final Class<E> exceptionClass) throws IOException
  {
    return call(envelope, resultClass, exceptionClass, null);
  }

  /**
   * Call the service.
   *
   * @param <T> The return type
   * @param envelope The request envelope
   * @param resultClass The class of the result in the response
   * @param httpHeaders The custom Http headers
   * @return The response
   * @throws IOException An exception from SOAP message
   */
  public final <T, E> T call(final Envelope envelope, final Class<T> resultClass, final Class<E> exceptionClass,
          final Map<String, String> httpHeaders) throws IOException
  {
    try
    {
      HttpPost post = createHttpPost(envelope, httpHeaders);
      HttpParams httpParameters = createHttpParams();
      HttpClient client = createHttpClient(httpParameters);

      HttpResponse response = client.execute(post);
      int statusCode = response.getStatusLine().getStatusCode();
      LOGGER.info("Status code is: {}", statusCode);
      InputStream is = debugResponseStream(response.getEntity().getContent());

      if (statusCode == HTTP_STATUS_OK)
      {
        GenericHandler responseHandler = new GenericHandler(resultClass);
        responseHandler.parseWithPullParser(is);
        LOGGER.info("The reply has been parsed");
        return (T) responseHandler.getObject();
      } else if (statusCode == HTTP_STATUS_ERROR)
      {
        /**
         * TODO: http://traq.javaforum.hu/browse/ANDROIDSOAP-8
         * Assumed that status code 500 is returned in case of SOAP exception,
         * this has to change in the future
         */
        GenericHandler exceptionHandler = new GenericHandler(resultClass, Boolean.TRUE);
        exceptionHandler.parseWithPullParser(is);
        LOGGER.info("The reply has been parsed");
        throw encapsulateIOException((Exception) exceptionHandler.getObject());
      } else
      {
        throw new IOException("Can't parse the response, status: " + statusCode);
      }
    } catch (XmlPullParserException ex)
    {
      throw encapsulateIOException(ex);
    } catch (NoSuchFieldException ex)
    {
      throw encapsulateIOException(ex);
    } catch (InstantiationException ex)
    {
      throw encapsulateIOException(ex);
    } catch (IllegalAccessException ex)
    {
      throw encapsulateIOException(ex);
    } finally
    {
    }
  }

  /**
   * Creates a HttpPost instance.
   *
   * @param envelope The envelope
   * @param httpHeaders The Http headers
   * @return The instance
   * @throws UnsupportedEncodingException UnsupportedEncodingException
   */
  protected final HttpPost createHttpPost(final Envelope envelope,
          final Map<String, String> httpHeaders) throws UnsupportedEncodingException
  {
    try
    {
      final String envelopeXml = envelope.toString();
      LOGGER.debug("Request is:\n{}", envelopeXml);

      HttpPost post = new HttpPost(this.getUrl());
      post.setEntity(new StringEntity(envelopeXml));
      post.setHeader("Content-type", "text/xml; charset=" + DEFAULT_ENCODING);
      if (httpHeaders != null)
      {
        for (Map.Entry<String, String> entry : httpHeaders.entrySet())
        {
          post.setHeader(entry.getKey(), entry.getValue());
          LOGGER.debug("setHeader('{}', '{}')", entry.getKey(), entry.getValue());
        }
      }
      if (this.getUsername() != null && this.getPassword() != null)
      {
        String basic = this.getUsername() + ":" + this.getPassword();
        String authorizationHeader = "Basic "
                + new String(Base64.encodeBase64(basic.getBytes(DEFAULT_ENCODING)), DEFAULT_ENCODING);
        post.addHeader("Authorization", authorizationHeader);
        LOGGER.debug("addHeader('Authorization', '%1$s')", authorizationHeader);
      }

      return post;
    } finally
    {
    }
  }

  /**
   * Creates a HttpParams instance.
   *
   * @return The instance
   */
  protected final HttpParams createHttpParams()
  {
    try
    {
      HttpParams httpParameters = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
      LOGGER.debug("Connection timeout: {}", connectionTimeout);
      HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
      LOGGER.debug("Socket timeout: {}", socketTimeout);

      return httpParameters;
    } finally
    {
    }
  }

  /**
   * Creates a HttpClient implementation instance.
   *
   * @param params The HttpParams
   * @return The instance
   * @throws IOException IOException
   */
  protected abstract HttpClient createHttpClient(HttpParams params)
          throws IOException;

  /**
   * Prints out the reply of the server when the loglevel is DEBUG.
   *
   * @param stream The stream
   * @return The reply stream
   * @throws IOException When IO error occurred
   */
  protected final InputStream debugResponseStream(final InputStream stream) throws IOException
  {
    InputStream replyStream = stream;
    try
    {
      if (LOGGER.isDebugEnabled())
      {
        InputStreamReader isr = new InputStreamReader(stream, DEFAULT_ENCODING);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine())
        {
          sb.append(line);
          sb.append("\n");
        }
        br.close();
        isr.close();
        stream.close();

        replyStream = new ByteArrayInputStream(sb.toString().getBytes(DEFAULT_ENCODING));
        LOGGER.debug("Response:\n{}", sb.toString());
      }
    } finally
    {
    }

    return replyStream;
  }

  /**
   * Gets the password.
   *
   * @return The password
   */
  public final String getPassword()
  {
    return password;
  }

  /**
   * Gets the URL.
   *
   * @return The URL
   */
  public final String getUrl()
  {
    return this.url;
  }

  /**
   * Gets the username.
   *
   * @return The username
   */
  public final String getUsername()
  {
    return username;
  }

  /**
   * Sets the HTTP wait for data timeout.
   *
   * @param timeout The timeout
   */
  public final void setSocketTimeout(final int timeout)
  {
    this.socketTimeout = timeout;
  }

  /**
   * Sets the HTTP wait for connection timeout.
   *
   * @param timeout The timeout
   */
  public final void setConnectionTimeout(final int timeout)
  {
    this.connectionTimeout = timeout;
  }

  /**
   * Encapsulate Exception to IOException with cause (ANDROIDSOAP-14).
   *
   * @param except The Exception instance
   * @return The IOException instance
   */
  protected static IOException encapsulateIOException(final Exception except)
  {
    final IOException ioException = except == null
            ? new IOException() : new IOException(except.getMessage());
    ioException.initCause(except);
    return ioException;
}
}
