/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.ssl;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Factory of the trust all SSL mechanism.
 *
 * Changelog:
 * ANDROIDSOAP-7
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class AllTrustSSLSocketFactory extends SSLSocketFactory
{

  /**
   * The SSL context.
   */
  private final SSLContext sslContext = SSLContext.getInstance("TLS");

  /**
   * Constructor for AllTrustSSLSocketFactory.
   *
   * @param trustStore The truststore
   * @throws KeyManagementException KeyManagementException
   * @throws KeyStoreException KeyStoreException
   * @throws NoSuchAlgorithmException NoSuchAlgorithmException
   * @throws UnrecoverableKeyException UnrecoverableKeyException
   */
  public AllTrustSSLSocketFactory(final KeyStore trustStore)
          throws NoSuchAlgorithmException, KeyManagementException,
          KeyStoreException, UnrecoverableKeyException
  {
    super(trustStore);
    this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);

    TrustManager[] tm = new TrustManager[1];
    tm[0] = new AllTrustManager();
    sslContext.init(null, tm, null);
  }

  /**
   * Create a new socket.
   *
   * @param socket The sockeet
   * @param host The host
   * @param port The port
   * @param autoClose True when auto close socket
   *
   * @return The socket
   * @throws IOException IOException
   */
  @Override
  public final Socket createSocket(final Socket socket, final String host,
          final int port, final boolean autoClose) throws IOException
  {
    return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
  }

  /**
   * Create a new socket.
   *
   * @return The Socket
   * @throws IOException IOException
   */
  @Override
  public final Socket createSocket() throws IOException
  {
    return sslContext.getSocketFactory().createSocket();
  }
}
