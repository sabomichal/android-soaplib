/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.impl;

import hu.javaforum.android.soap.Body;
import hu.javaforum.commons.CommonBean;

import java.util.Map;

/**
 * Body implementation in the SOAP envelope for .NET servers.
 *
 * Changelog:
 * ANDROIDSOAP-6 - 2011-01-08
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class DotNetBody extends Body
{

  /**
   * The namespace of the operation.
   */
  private final String namespace;
  /**
   * The parameters array.
   */
  private final Map<String, Object> parameters;

  /**
   * Construct a new body with the operation name, and the parameters.
   *
   * @param namespace The namespace of the operation
   * @param parameters The parameters array
   */
  public DotNetBody(final String namespace, final Map<String, Object> parameters)
  {
    this.parameters = parameters;
    this.namespace = namespace;
  }

  /**
   * Gets the body in the SOAP envelope.
   *
   * Example:
   * &lt;soapenv:Body&gt;&lt;/soapenv:Body&gt;
   *
   * @return The body
   */
  @Override
  protected final String getBody()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("<soapenv:Body>");
    if (parameters != null)
    {
      for (Map.Entry<String, Object> entry : parameters.entrySet())
      {
        sb.append(CommonBean.dumpXml(entry.getValue(), entry.getKey(), namespace));
      }
    }
    sb.append("</soapenv:Body>");

    return sb.toString();
  }
}
