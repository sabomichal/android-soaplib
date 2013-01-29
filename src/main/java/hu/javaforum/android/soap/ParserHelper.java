/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.android.soap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Helper methods to GenericHandler class.
 *
 * Changelog:
 * ANDROIDSOAP-6 - 2011-01-07
 * ANDROIDSOAP-5 - 2011-01-07
 * ANDROIDSOAP-3 - 2011-01-07
 * ANDROIDSOAP-1 - 2011-01-06
 *
 * @author Gábor Auth <gabor.auth@javaforum.hu>
 * @author Chris Wolf cw10025 gmail com
 * @author sabo
 */
public abstract class ParserHelper
{

  /**
   * The LOGGER instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ParserHelper.class.getSimpleName());
  /**
   * The String content of the XML leaves.
   */
  private final StringBuilder content = new StringBuilder();
  /**
   * Flag to dirty content.
   */
  private Boolean contentDirty = Boolean.FALSE;

  /**
   * Parser factory to allow parser implementations other then Android's native
   * Expat pull parser. (e.g. when not running code in emulator or device)
   *
   * @return An instance of XmlPullParser
   */
  private static XmlPullParser createParser()
  {
    try
    {
      return Xml.newPullParser();
    } catch (Exception except)
    {
      LOGGER.warn("Cannot initialize Android's XML parser: {}", except.toString());
      try
      {
        return (XmlPullParser) Class.forName("org.xmlpull.mxp1.MXParser").newInstance();
      } catch (Exception ex)
      {
        LOGGER.error("Cannot initialize XML parser: {}", ex.toString());
        throw new IllegalStateException("Cannot initialize XML parser", ex);
      }
    } finally
    {
    }
  }

  /**
   * XmlPullParser based XML processing.
   *
   * @param is The InputStream instance
   * @throws IOException IOException
   * @throws NoSuchFieldException NoSuchFieldException
   * @throws XmlPullParserException XmlPullParserException
   */
  public final void parseWithPullParser(final InputStream is) throws XmlPullParserException,
          IOException, NoSuchFieldException
  {
    try
    {
      XmlPullParser parser = createParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
      parser.setInput(is, null);

      for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next())
      {
        if (XmlPullParser.START_TAG == eventType)
        {
          startElement(getPrefixedTag(parser.getName()));
        } else if (XmlPullParser.TEXT == eventType)
        {
          this.addText(parser.getText());
        } else if (XmlPullParser.END_TAG == eventType)
        {
          this.endElement(getPrefixedTag(parser.getName()));
        }
      }
    } finally
    {
    }
  }

  /**
   * Returns the prefixed tag of the SOAP envelope or the tagName.
   *
   * @param tagName The name of the tag
   * @return The prefix
   */
  private String getPrefixedTag(final String tagName)
  {
    if ("Envelope".equals(tagName) || "Header".equals(tagName)
            || "Body".equals(tagName) || "Fault".equals(tagName))
    {
      /**
       * TODO: http://traq.javaforum.hu/browse/ANDROIDSOAP-9
       */
      return "env:" + ":" + tagName;
    }

    return tagName;
  }

  /**
   * Append text fragment between the XML tags to the content buffer.
   *
   * @param text The text
   */
  public final void addText(final String text)
  {
    if (text != null)
    {
      char[] ch = text.toCharArray();
      content.append(ch, 0, ch.length);
      contentDirty = Boolean.TRUE;
    }
  }

  /**
   * Clear the content buffer.
   */
  public final void clearContent()
  {
    content.delete(0, content.length());
    contentDirty = Boolean.FALSE;
  }

  /**
   * Returns with the content buffer as String.
   *
   * @return The content as String
   */
  public final String getContent()
  {
    return contentDirty ? content.toString() : null;
  }

  /**
   * Start element hook.
   *
   * @param name The name of the element
   */
  public abstract void startElement(String name);

  /**
   * End element hook.
   *
   * @param name The name of the element
   * @throws NoSuchFieldException
   */
  public abstract void endElement(String name);

  /**
   * The array field info, this class holds an information about arrays.
   */
  static class ArrayFieldInfo
  {

    /**
     * The field name.
     */
    private final String fieldName;
    /**
     * The field depth.
     */
    private final int arrayFieldDepth;
    /**
     * The data of array as List.
     */
    private final List<Object> arrayData;

    /**
     * The constructor.
     *
     * @param fieldName The name of the field
     * @param arrayFieldDepth The depth of the field
     */
    public ArrayFieldInfo(final String fieldName, final int arrayFieldDepth)
    {
      this.fieldName = fieldName;
      this.arrayFieldDepth = arrayFieldDepth;
      this.arrayData = new ArrayList<Object>();
    }

    /**
     * Returns array data.
     *
     * @return The array as list
     */
    public List<Object> getArrayData()
    {
      return arrayData;
    }

    /**
     * Returns depth of field.
     *
     * @return The depth
     */
    public int getArrayFieldDepth()
    {
      return arrayFieldDepth;
    }

    /**
     * Returns the name of field.
     *
     * @return The name
     */
    public String getFieldName()
    {
      return fieldName;
    }

    /**
     * Add an element.
     *
     * @param nextElement The element
     */
    public final void addElement(final Object nextElement)
    {
      this.arrayData.add(nextElement);
    }
  }
}
