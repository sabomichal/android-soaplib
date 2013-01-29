/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.impl;

import hu.javaforum.android.soap.Header;

/**
 * Simple header implementation in the SOAP envelope.
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class SimpleHeader extends Header
{

  /**
   * Returns the header of the SOAP envelope.
   *
   * Example:
   * &lt;soapenv:Header&gt;&lt;/soapenv:Header&gt;
   *
   * @return The header
   */
  @Override
  protected final String getHeader()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("<soapenv:Header/>");

    return sb.toString();
  }
}
