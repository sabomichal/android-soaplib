/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.impl;

import hu.javaforum.android.soap.Header;

/**
 * WS-Security header.
 *
 * Changelog:
 * ANDROIDSOAP-7 - 2012-04-16
 *
 * @author sabo
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class WSSecurityHeader extends Header
{

  /**
   * Username.
   */
  private final String username;
  /**
   * Password.
   */
  private final String password;

  /**
   * Creates a new WS-Security header.
   *
   * @param username The username
   * @param password The password
   */
  public WSSecurityHeader(final String username, final String password)
  {
    this.username = username;
    this.password = password;
  }

  /**
   * Returns with the WS-Security header.
   *
   * @return The header
   */
  @Override
  protected final String getHeader()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("<soapenv:Header>");
    sb.append("<Security xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">");
    sb.append("<UsernameToken>");
    sb.append("<Username>");
    sb.append(username);
    sb.append("</Username>");
    sb.append("<Password>");
    sb.append(password);
    sb.append("</Password>");
    sb.append("</UsernameToken>");
    sb.append("</Security>");
    sb.append("</soapenv:Header>");

    return sb.toString();
  }
}
