/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class is parent of all request and response bean.
 *
 * Changelog:
 * JFPORTAL-94 (2011-07-31)
 * First implementation (2011-06-22)
 *
 * @author Gábor AUTH <gabor.auth@javaforum.hu>
 */
@XmlTransient
public abstract class CommonBean extends CommonBeanHelper
{

  /**
   * Byte base number.
   */
  protected static final int BYTE_BASE = 256;
  /**
   * Hash base number.
   */
  protected static final int HASH_BASE = 7;
  /**
   * Hex base number.
   */
  protected static final int HEX_BASE = 16;
  /**
   * Hash multiplier number.
   */
  protected static final int HASH_MULTIPLIER = 71;
  /**
   * The " of " constant.
   */
  private static final String CONSTANT_OF = " of ";

  /**
   * This method prints recursive the bean fields in XML format. This XML is
   * usable for WebService stacks, like AndroidSOAP... :)
   *
   * @return The XML value
   */
  @Override
  public final String toString()
  {
    return dumpXml(this, null, null).toString();
  }

  /**
   * Entry point of the reflection based content dump method.
   *
   * @param object The object
   * @param firstName The first enclosing element name
   * @param nsPrefixName The namespace prefix
   * @return The XML value
   */
  public static StringBuilder dumpXml(final Object object,
          final String firstName, final String nsPrefixName)
  {
    final StringBuilder sb = new StringBuilder();

    if (object == null)
    {
      return sb;
    }

    /**
     * The method suppress the first enclosing element, if the `firstName`
     * equals with '--'.
     */
    final int startLevel = "--".equals(firstName) ? -1 : 0;

    /**
     * Starts a recursive dump... :)
     */
    dumpXml(sb, NameHelper.createNsPrefix(nsPrefixName), object,
            NameHelper.createFirstName(firstName, object), startLevel);

    return sb;
  }

  /**
   * Reflection based content dump method.
   *
   * @param sb The StringBuilder instance
   * @param nsPrefix The namespace prefix
   * @param object The object
   * @param fieldName The name of the field
   * @param level The level of the recursion
   */
  protected static void dumpXml(final StringBuilder sb,
          final char[] nsPrefix, final Object object,
          final char[] fieldName, final int level)
  {
    /**
     * Processes the usable fields in the object.
     */
    final FieldsMetaData fmd = new FieldsMetaData(object);

    if (level >= 0)
    {
      sb.append(getIndentString(level)).append("<").append(nsPrefix);
      sb.append(fieldName).append(">");
      sb.append("\n");
    }

    /**
     * Iterate through the fields.
     */
    for (int count = 0; count < fmd.getFields().length; count++)
    {
      final Field field = fmd.getFields()[count];
      Object value = getFieldValue(field, object);

      if (value == null)
      {
        continue;
      }

      if (object instanceof CommonBean)
      {
        if (!fmd.getPrintFields()[count])
        {
          continue;
        }
        if (fmd.getHiddenFields()[count])
        {
          value = "***hidden***";
        }
      }

      appendSubClass(sb, nsPrefix, value, fmd, count, ReflectionHelper.getFieldName(field),
              level + 1);
    }

    if (level >= 0)
    {
      sb.append(getIndentString(level)).append("</").append(nsPrefix);
      sb.append(fieldName).append(">");
      sb.append("\n");
    }
  }

  /**
   * Append the XML content of sub-class to the StringBuilder instance.
   *
   * @param sb The StringBuilder instance
   * @param nsPrefix The namespace prefix
   * @param value The sub-class instance
   * @param fmd FieldMetaData instance of the parent class
   * @param metaDataIndex The current index of FieldsMetaData instance
   * @param fieldName The name of the current field
   * @param level The level of the recursion
   */
  protected static void appendSubClass(final StringBuilder sb,
          final char[] nsPrefix, final Object value,
          final FieldsMetaData fmd, final int metaDataIndex,
          final char[] fieldName, final int level)
  {
    if (value instanceof Object[])
    {
      appendObjectArray(sb, nsPrefix, value, fmd, metaDataIndex, fieldName, level);
    } else if (value.getClass().isArray())
    {
      appendPrimitiveArray(sb, nsPrefix, value, fmd, metaDataIndex, fieldName, level);
    } else if (value instanceof Collection)
    {
      appendCollection(sb, nsPrefix, value, fmd, metaDataIndex, fieldName, level);
    } else
    {
      appendValue(sb, nsPrefix, value, fieldName, level);
    }
  }

  /**
   * Dumps an Object array.
   *
   * @param sb The StringBuilder instance
   * @param nsPrefix The namespace prefix
   * @param value The sub-class instance
   * @param fmd The FieldMetaData instance of the parent class
   * @param metaDataIndex The current index of FieldsMetaData instance
   * @param fieldName THe name of the current field
   * @param level The level of the recursion
   */
  protected static void appendObjectArray(final StringBuilder sb,
          final char[] nsPrefix, final Object value,
          final FieldsMetaData fmd, final int metaDataIndex,
          final char[] fieldName, final int level)
  {
    Object[] array = (Object[]) value;
    for (int arrayCount = 0; arrayCount < array.length; arrayCount++)
    {
      if (arrayCount >= fmd.getMaximumItemDumpFields()[metaDataIndex])
      {
        sb.append(getIndentString(level)).append("<!--").append(nsPrefix);
        sb.append(fieldName).append(">");
        sb.append("LIMIT REACHED (").append(arrayCount).append(CONSTANT_OF).append(array.length).append(")");
        sb.append("</").append(nsPrefix);
        sb.append(fieldName).append("-->").append("\n");
        break;
      }
      appendValue(sb, nsPrefix, array[arrayCount], fieldName, level);
    }
  }

  /**
   * Dumps a primitive array.
   *
   * @param sb The StringBuilder instance
   * @param nsPrefix The namespace prefix
   * @param value The sub-class instance
   * @param fmd The FieldMetaData instance of the parent class
   * @param metaDataIndex The current index of FieldsMetaData instance
   * @param fieldName THe name of the current field
   * @param level The level of the recursion
   */
  protected static void appendPrimitiveArray(final StringBuilder sb,
          final char[] nsPrefix, final Object value,
          final FieldsMetaData fmd, final int metaDataIndex,
          final char[] fieldName, final int level)
  {
    int arrayLength = Array.getLength(value);
    if (byte.class.equals(value.getClass().getComponentType()))
    {
      sb.append(getIndentString(level)).append("<").append(nsPrefix);
      sb.append(fieldName).append("><!-- HEX VALUE -->");
      byte[] byteArray = (byte[]) value;
      for (int arrayCount = 0; arrayCount < arrayLength; arrayCount++)
      {
        if (arrayCount == fmd.getMaximumHexDumpFields()[metaDataIndex])
        {
          sb.append("<!--LIMIT REACHED (").append(arrayCount).append(CONSTANT_OF).append(arrayLength).append(")-->");
          break;
        }
        if (byteArray[arrayCount] >= 0 && byteArray[arrayCount] < HEX_BASE)
        {
          sb.append('0');
        }
        int unsignedValue = byteArray[arrayCount] < 0 ? byteArray[arrayCount] + BYTE_BASE : byteArray[arrayCount];
        sb.append(Integer.toHexString(unsignedValue));
      }
      sb.append("</").append(nsPrefix);
      sb.append(fieldName).append(">").append("\n");
    } else
    {
      for (int arrayCount = 0; arrayCount < arrayLength; arrayCount++)
      {
        if (arrayCount == fmd.getMaximumItemDumpFields()[metaDataIndex])
        {
          sb.append(getIndentString(level)).append("<!--").append(nsPrefix);
          sb.append(fieldName).append(">");
          sb.append("LIMIT REACHED (").append(arrayCount).append(CONSTANT_OF).append(arrayLength).append(")");
          sb.append("</").append(nsPrefix);
          sb.append(fieldName).append("-->").append("\n");
          break;
        }
        appendValue(sb, nsPrefix, Array.get(value, arrayCount), fieldName, level);
      }
    }
  }

  /**
   * Dumps a collection.
   *
   * @param sb The StringBuilder instance
   * @param nsPrefix The namespace prefix
   * @param value The sub-class instance
   * @param fmd The FieldMetaData instance of the parent class
   * @param metaDataIndex The current index of FieldsMetaData instance
   * @param fieldName THe name of the current field
   * @param level The level of the recursion
   */
  protected static void appendCollection(final StringBuilder sb,
          final char[] nsPrefix, final Object value,
          final FieldsMetaData fmd, final int metaDataIndex,
          final char[] fieldName, final int level)
  {
    Collection collection = (Collection) value;
    Iterator collectionIterator = collection.iterator();
    for (int collectionCount = 0; collectionIterator.hasNext(); collectionCount++)
    {
      if (collectionCount == fmd.getMaximumItemDumpFields()[metaDataIndex])
      {
        sb.append(getIndentString(level)).append("<!--").append(nsPrefix);
        sb.append(fieldName).append(">");
        sb.append("LIMIT REACHED (").append(collectionCount).append(CONSTANT_OF).append(collection.size()).append(")");
        sb.append("</").append(nsPrefix);
        sb.append(fieldName).append("-->").append("\n");
        break;
      }
      appendValue(sb, nsPrefix, collectionIterator.next(), fieldName, level);
    }
  }

  /**
   * Append value to the StringBuilder instance.
   *
   * @param sb The StringBuilder instance
   * @param nsPrefix The namespace prefix
   * @param itemValue The item value
   * @param fieldName The name of the field
   * @param level The level of recursion
   */
  protected static void appendValue(final StringBuilder sb,
          final char[] nsPrefix, final Object itemValue,
          final char[] fieldName, final int level)
  {
    if (itemValue == null)
    {
      return;
    }

    Object value = itemValue;
    if (value.getClass().getName().startsWith("java.") || value.getClass().isEnum())
    {
      sb.append(getIndentString(level));
      sb.append("<").append(nsPrefix);
      sb.append(fieldName).append(">");

      if (value instanceof Date)
      {
        value = ((SimpleDateFormat) DATETIME_FORMAT.get()).format((Date) value);
      } else if (value instanceof Calendar)
      {
        value = ((SimpleDateFormat) DATETIME_TIMEZONE_FORMAT.get()).format(((Calendar) value).getTime());
      }
      sb.append(quoteXMLValue(value));

      sb.append("</").append(nsPrefix);
      sb.append(fieldName).append(">");
      sb.append("\n");
    } else
    {
      dumpXml(sb, nsPrefix, value, fieldName, level);
    }
  }
}
