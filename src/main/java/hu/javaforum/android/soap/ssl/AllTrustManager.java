/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager of the trust all SSL mechanism.
 *
 * Changelog:
 * ANDROIDSOAP-7
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class AllTrustManager implements X509TrustManager
{

  /**
   * The TRACER instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(AllTrustManager.class);

  /**
   * Check client is trusted.
   *
   * @param certificates The certificate chain
   * @param authType The type of auth
   * @throws CertificateException On wrong certificate
   */
  public final void checkClientTrusted(final X509Certificate[] certificates, final String authType)
          throws CertificateException
  {
    LOGGER.info("Certificates: {}, authType: {}", certificates, authType);
  }

  /**
   * Check server is trusted.
   *
   * @param certificates The certificate chain
   * @param authType The type of auth
   * @throws CertificateException On wrong certificate
   */
  public final void checkServerTrusted(final X509Certificate[] certificates, final String authType)
          throws CertificateException
  {
    LOGGER.info("Certificates: {}, authType: {}", certificates, authType);
  }

  /**
   * Returns with the list of accepted issuers.
   *
   * @return The list
   */
  public final X509Certificate[] getAcceptedIssuers()
  {
    return new X509Certificate[0];
  }
}
