/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap.impl;

import hu.javaforum.android.soap.Body;
import hu.javaforum.commons.CommonBean;

import java.util.Map;

/**
 * Simple body implementation in the SOAP envelope.
 *
 * Changelog:
 * ANDROIDSOAP-6 - 2011-01-08
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 */
public class SimpleBody extends Body
{

  /**
   * The name of the operation.
   */
  private final String operationName;
  /**
   * The parameters array.
   */
  private final Map<String, Object> parameters;

  /**
   * Construct a new body with the operation name, and the parameters.
   *
   * @param operationName The name of the operation
   * @param parameters The parameters array
   */
  public SimpleBody(final String operationName, final Map<String, Object> parameters)
  {
    this.operationName = operationName;
    this.parameters = parameters;
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
    sb.append("<ns:");
    sb.append(operationName);
    sb.append(">");
    if (parameters != null)
    {
      for (Map.Entry<String, Object> entry : parameters.entrySet())
      {
        sb.append(CommonBean.dumpXml(entry.getValue(), entry.getKey(), null));
      }
    }
    sb.append("</ns:");
    sb.append(operationName);
    sb.append(">");
    sb.append("</soapenv:Body>");

    return sb.toString();
  }
}
