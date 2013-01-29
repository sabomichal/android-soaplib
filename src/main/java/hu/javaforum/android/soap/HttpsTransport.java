/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

import hu.javaforum.android.soap.ssl.HttpsClientFactory;
import java.io.IOException;
import java.security.KeyStore;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;

/**
 * Simple Https transport.
 *
 * Changelog:
 * ANDROIDSOAP-14 - 2012-09-08
 * ANDROIDSOAP-6 - 2011-01-08
 * ANDROIDSOAP-5 - 2011-01-07
 * ANDROIDSOAP-1 - 2011-01-06
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 * @author Chris Wolf
 * @author sabo
 * @author Kamil Bartoszek
 */
public class HttpsTransport extends Transport
{

  /**
   * The custom keystore.
   */
  private KeyStore keyStore;
  /**
   * The custom truststore.
   */
  private KeyStore trustStore;
  /**
   * True, when the lib trusts all certificates.
   */
  private Boolean trustAll = Boolean.FALSE;

  /**
   * Creates a new instance.
   *
   * @param url The URL
   */
  public HttpsTransport(final String url)
  {
    super(url);
  }

  /**
   * Creates a new instance with authorization.
   *
   * @param url The URL
   * @param username The username (optional)
   * @param password The password (optional)
   */
  public HttpsTransport(final String url, final String username, final String password)
  {
    super(url, username, password);
  }

  /**
   * Creates a new instance with authorization.
   *
   * @param url The URL
   * @param username The username (optional)
   * @param password The password (optional)
   * @param keyStore The keystore (optional)
   * @param trustStore The truststore (optional)
   */
  public HttpsTransport(final String url, final String username, final String password,
          final KeyStore keyStore, final KeyStore trustStore)
  {
    super(url, username, password);
    this.keyStore = keyStore;
    this.trustStore = trustStore;
  }

  /**
   * Set the "trustAll" flag.
   *
   * @param trustAll True, when the lib trusts all certificates
   */
  public final void setTrustAll(final Boolean trustAll)
  {
    this.trustAll = trustAll;
  }

  /**
   * Creates a HttpClient implementation instance.
   *
   * @param params The HttpParams
   * @return The instance
   * @throws IOException IOException
   */
  @Override
  protected final HttpClient createHttpClient(final HttpParams params)
          throws IOException
  {
    try
    {
      if (keyStore == null || trustStore == null)
      {
        if (this.trustAll)
        {
          return HttpsClientFactory.createTrustAllInstance(params);
        } else
        {
          return HttpsClientFactory.createDefaultInstance(params);
        }
      } else
      {
        return HttpsClientFactory.createTrustStoreInstance(params, keyStore, trustStore);
      }
    } catch (Exception except)
    {
      throw encapsulateIOException(except);
    }
  }
}
