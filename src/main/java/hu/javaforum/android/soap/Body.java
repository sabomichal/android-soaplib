/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

/**
 * This class holds a generic body in the SOAP envelope.
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public abstract class Body
{

  /**
   * The parent envelope.
   */
  private Envelope envelope;

  /**
   * Gets the body in the SOAP envelope.
   *
   * Example:
   * &lt;soapenv:Body&gt;&lt;/soapenv:Body&gt;
   *
   * @return The body
   */
  protected abstract String getBody();

  /**
   * Gets the envelope.
   *
   * @return The envelope
   */
  final Envelope getEnvelope()
  {
    return this.envelope;
  }

  /**
   * Sets the envelope.
   *
   * @param envelope The envelope
   */
  final void setEnvelope(final Envelope envelope)
  {
    this.envelope = envelope;
  }

  /**
   * Gets the body in the SOAP envelope in XML.
   *
   * @return The XML
   */
  @Override
  public final String toString()
  {
    return this.getBody();
  }
}
