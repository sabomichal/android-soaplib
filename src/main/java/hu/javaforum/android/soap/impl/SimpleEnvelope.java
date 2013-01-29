/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.impl;

import hu.javaforum.android.soap.Envelope;

/**
 * Simple SOAP envelope implementation.
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class SimpleEnvelope extends Envelope
{

  /**
   * Construct a new envelope with the specified namespace.
   *
   * @param namespace The namespace
   */
  public SimpleEnvelope(final String namespace)
  {
    super(namespace);
  }

  /**
   * Gets the start tag of the SOAP envelope.
   *
   * Example:
   * &lt;soapenv:Envelope
   * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
   * xmlns:ns="http://skinpack.pop.javaforum.hu/"&gt;
   *
   * @return The start tag
   */
  @Override
  protected final String getStartTag()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"");
    sb.append(this.getNamespace());
    sb.append("\">");

    return sb.toString();
  }

  /**
   * Gets the end tag of the SOAP envelope.
   *
   * Example:
   * &lt;/soapenv:Envelope&gt;
   *
   * @return The start tag
   */
  @Override
  protected final String getEndTag()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("</soapenv:Envelope>");

    return sb.toString();
  }
}
