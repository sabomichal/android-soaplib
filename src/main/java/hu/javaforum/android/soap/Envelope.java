/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

/**
 * This class holds a generic SOAP envelope.
 *
 * @author Gábor AUTH <gabor.auth@javaforum.hu>
 */
public abstract class Envelope
{

  /**
   * The namespace of the SOAP envelope.
   */
  private final String namespace;
  /**
   * The header in the SOAP envelope.
   */
  private Header header;
  /**
   * The body in the SOAP envelope.
   */
  private Body body;

  /**
   * Construct a new envelope with the specified namespace.
   *
   * @param namespace The namespace
   */
  public Envelope(final String namespace)
  {
    this.namespace = namespace;
    this.header = null;
    this.body = null;
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
  protected abstract String getStartTag();

  /**
   * Gets the end tag of the SOAP envelope.
   *
   * Example:
   * &lt;/soapenv:Envelope&gt;
   *
   * @return The start tag
   */
  protected abstract String getEndTag();

  /**
   * Gets the namespace of the SOAP envelope.
   *
   * @return The namespace
   */
  public final String getNamespace()
  {
    return namespace;
  }

  /**
   * Gets the body in the SOAP envelope.
   *
   * @return The body
   */
  public final Body getBody()
  {
    return body;
  }

  /**
   * Sets the body in the SOAP envelope.
   *
   * @param body The body
   */
  public final void setBody(final Body body)
  {
    this.body = body;
    if (body != null)
    {
      this.body.setEnvelope(this);
    }
  }

  /**
   * Gets the header in the SOAP envelope.
   *
   * @return The header
   */
  public final Header getHeader()
  {
    return header;
  }

  /**
   * Sets the header in the SOAP envelope.
   *
   * @param header The header
   */
  public final void setHeader(final Header header)
  {
    this.header = header;
  }

  /**
   * Gets the SOAP Envelope in XML.
   *
   * @return The XML
   */
  @Override
  public final String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getStartTag());
    sb.append(this.header.toString());
    sb.append(this.body.toString());
    sb.append(this.getEndTag());

    return sb.toString();
  }
}
