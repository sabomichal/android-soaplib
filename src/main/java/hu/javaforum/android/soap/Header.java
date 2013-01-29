/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

/**
 * This class holds a generic header in the SOAP envelope.
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public abstract class Header
{

  /**
   * Gets the header in the SOAP envelope.
   *
   * Example:
   * &lt;soapenv:Header&gt;&lt;/soapenv:Header&gt;
   *
   * @return The header
   */
  protected abstract String getHeader();

  /**
   * Gets the header in the SOAP envelope in XML.
   *
   * @return The XML
   */
  @Override
  public final String toString()
  {
    return this.getHeader();
  }
}
