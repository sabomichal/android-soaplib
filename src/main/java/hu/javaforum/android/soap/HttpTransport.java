/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

/**
 * Simple Http transport.
 *
 * Changelog:
 * ANDROIDSOAP-6 - 2011-01-08
 * ANDROIDSOAP-5 - 2011-01-07
 * ANDROIDSOAP-1 - 2011-01-06
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 * @author Chris Wolf
 */
public class HttpTransport extends Transport
{

  /**
   * Creates a new instance.
   *
   * @param url The URL
   */
  public HttpTransport(final String url)
  {
    super(url);
  }

  /**
   * Creates a new instance with authorization.
   *
   * @param url The URL
   * @param username The username
   * @param password The password
   */
  public HttpTransport(final String url, final String username, final String password)
  {
    super(url, username, password);
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
    return new DefaultHttpClient(params);
  }
}
