/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.codec.binary.Base64;

/**
 * The ReflectionHelper is provides static methods to invoke getter and setter
 * methods in JavaBean instances.
 *
 * Changelog:
 * JFPORTAL-94 (2011-07-31)
 * ANDROIDSOAP-6 (2011-01-08)
 * ANDROIDSOAP-1 (2011-01-06)
 * JFPORTAL-94 (2010-02-24)
 * JFPORTAL-94 (2009-11-01)
 * JFPORTAL-79 (2009-10-11)
 * JFPORTAL-79 (2009-09-12)
 * JFPORTAL-78 (2009-09-03)
 * First implementation (2009-05-26)
 *
 * @author Auth Gábor <auth.gabor@javaforum.hu>
 */
@SuppressWarnings("restriction")
public abstract class ReflectionHelper implements Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/**
   * It contains the regex patterns and the date formatting patterns.
   */
  private static final Map<String, String> DATE_FORMAT_PATTERNS;
  /**
   * Constant of "get".
   */
  protected static final String GET_WORD = "get";
  /**
   * It contains the primitive classes.
   */
  private static final Set<Class> PRIMITIVE_WRAPPER_CLASSES;
  /**
   * Constant of "set".
   */
  protected static final String SET_WORD = "set";
  /**
   * True, if the "javax.xml.bind.annotation.XmlElement" is on classpath.
   */
  protected static final Boolean XML_ELEMENT_LOADED;

  static
  {
    Map<String, String> patterns = new HashMap<String, String>();
    patterns.put("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9][0-9][0-9].*",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    patterns.put("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]",
            "yyyy-MM-dd HH:mm:ss");
    patterns.put("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]",
            "yyyy-MM-dd HH:mm");
    patterns.put("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]",
            "yyyy-MM-dd");

    DATE_FORMAT_PATTERNS = Collections.unmodifiableMap(patterns);

    Set<Class> classes = new HashSet<Class>();
    classes.add(Boolean.class);
    classes.add(Byte.class);
    classes.add(Double.class);
    classes.add(Float.class);
    classes.add(Integer.class);
    classes.add(Long.class);
    classes.add(Short.class);

    PRIMITIVE_WRAPPER_CLASSES = Collections.unmodifiableSet(classes);

    /**
     * Some platform (like Android) isn't contains XmlElement annotation,
     * and the Reflection don't ignore them (like a normal JDK/JRE), but
     * throws ClassNotFoundException.
     */
    Boolean xmlElementLoaded = Boolean.TRUE;
    try
    {
      Class.forName("javax.xml.bind.annotation.XmlElement");
    } catch (ClassNotFoundException except)
    {
      xmlElementLoaded = Boolean.FALSE;
    }
    XML_ELEMENT_LOADED = xmlElementLoaded;
  }

  /**
   * Protected constructor, because all methods are static.
   */
  protected ReflectionHelper()
  {
    super();
  }

  /**
   * Gets the the field.
   *
   * @param objectClass The object
   * @param fieldName The field name of the object
   * @return The class
   * @exception NoSuchFieldException Throws, when the field isn't exists
   */
  public static Field getField(final Class objectClass, final String fieldName) throws NoSuchFieldException
  {
    Field field;
    if ("return".equals(fieldName))
    {
      field = objectClass.getDeclaredField("_" + fieldName);
    } else
    {
      field = objectClass.getDeclaredField(fieldName);
    }

    return field;
  }

  /**
   * Returns with the name of the field.
   * Returns:
   * - with annotated name, if the Field has XmlElement annotation
   * - otherwise with the name of the field
   *
   * @param field The Field instance
   * @return Field name as char array
   */
  public static char[] getFieldName(final Field field)
  {
    if (field == null)
    {
      return new char[0];
    }

    if (XML_ELEMENT_LOADED)
    {
      final Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations)
      {
        if (XmlElement.class.equals(annotation.annotationType()))
        {
          return ((XmlElement) annotation).name().toCharArray();
        }
      }
    }

    return field.getName().toCharArray();
  }

  /**
   * Returns 'true', if the field is exists in the bean instance.
   *
   * @param instanceClass The bean class
   * @param instance The bean instance
   * @param fieldName The field name
   * @return True, if the field is exists
   */
  protected static Boolean isGetterExists(final Class instanceClass,
          final Object instance, final String fieldName)
  {
    return getGetterMethod(instanceClass, instance, fieldName) != null;
  }

  /**
   * Returns the getter method of the field. It is recursive method.
   *
   * @param instanceClass The bean class
   * @param instance The bean instance
   * @param fieldName The field name
   * @return The getter method, if it is exists
   * null, if the getter method is not exists
   */
  protected static Method getGetterMethod(final Class instanceClass,
          final Object instance, final String fieldName)
  {
    if ("java.lang.Object".equals(instanceClass.getName()))
    {
      return null;
    }

    try
    {
      StringBuilder sb = new StringBuilder(fieldName.length() + GET_WORD.length());
      sb.append(GET_WORD);
      sb.append(fieldName);
      sb.setCharAt(GET_WORD.length(), Character.toUpperCase(sb.charAt(GET_WORD.length())));
      return instanceClass.getDeclaredMethod(sb.toString());
    } catch (NoSuchMethodException except)
    {
      return getGetterMethod(instanceClass.getSuperclass(), instance, fieldName);
    }
  }

  /**
   * Creates a parameter from the value. If the value instance of String then
   * this method calls the converter methods.
   *
   * @param fieldClass The class of field
   * @param value The value
   * @return The converted instance
   * @throws ClassNotFoundException If class not found
   * @throws ParseException If the pattern cannot be parseable
   * @throws UnsupportedEncodingException If the Base64 stream contains non UTF-8 chars
   */
  protected static Object createParameterFromValue(final Class fieldClass,
          final Object value) throws ClassNotFoundException, ParseException, UnsupportedEncodingException
  {
    Object parameter = value;

    if (!value.getClass().equals(fieldClass) && !PRIMITIVE_WRAPPER_CLASSES.contains(value.getClass()))
    {
      if (fieldClass.isArray() && value instanceof List)
      {
        String arrayElementTypeName = fieldClass.getName().substring(2, fieldClass.getName().length() - 1);
        Class arrayElementType = Class.forName(arrayElementTypeName);
        Object[] at = (Object[]) Array.newInstance(arrayElementType, 0);
        parameter = ((List<Object>) value).toArray(at);
      } else
      {
        String stringValue = createStringFromValue(value);
        if (stringValue != null)
        {
          parameter = convertToPrimitive(fieldClass, stringValue);
          parameter = parameter == null ? convertToPrimitiveWrapper(fieldClass, stringValue) : parameter;
          parameter = parameter == null ? convertToBigNumbers(fieldClass, stringValue) : parameter;
          parameter = parameter == null ? convertToOthers(fieldClass, stringValue) : parameter;
        }
      }
    }

    return parameter;
  }

  /**
   * Returns a value, if the class of the value is String or String[].
   *
   * @param value The value
   * @return The text value
   */
  private static String createStringFromValue(final Object value)
  {
    String stringValue = null;
    if (value instanceof String)
    {
      stringValue = (String) value;
    } else if (value instanceof String[])
    {
      String[] stringArrayValue = (String[]) value;
      if (stringArrayValue.length == 0)
      {
        return null;
      }
      if (stringArrayValue.length > 1)
      {
        return null;
      }

      stringValue = stringArrayValue[0];
    }

    return stringValue;
  }

  /**
   * Converts the string value to instance of primitive wrapper.
   *
   * @param fieldClass The field class
   * @param stringValue The string value
   * @return The instance of the field class
   * @throws ParseException Throws when the string isn't parseable
   */
  private static Object convertToPrimitive(final Class fieldClass, final String stringValue)
          throws ParseException
  {
    Object parameter = null;

    if (fieldClass.equals(boolean.class))
    {
      parameter = Boolean.parseBoolean(stringValue);
    } else if (fieldClass.equals(byte.class))
    {
      parameter = Byte.parseByte(stringValue);
    } else if (fieldClass.equals(double.class))
    {
      parameter = Double.parseDouble(stringValue);
    } else if (fieldClass.equals(float.class))
    {
      parameter = Float.parseFloat(stringValue);
    } else if (fieldClass.equals(int.class))
    {
      parameter = Integer.parseInt(stringValue);
    } else if (fieldClass.equals(long.class))
    {
      parameter = Long.parseLong(stringValue);
    } else if (fieldClass.equals(short.class))
    {
      parameter = Short.parseShort(stringValue);
    }

    return parameter;
  }

  /**
   * Converts the string value to instance of primitive wrapper.
   *
   * @param fieldClass The field class
   * @param stringValue The string value
   * @return The instance of the field class
   * @throws ParseException Throws when the string isn't parseable
   */
  private static Object convertToPrimitiveWrapper(final Class fieldClass, final String stringValue)
          throws ParseException
  {
    Object parameter = null;

    if (fieldClass.equals(Boolean.class))
    {
      parameter = Boolean.parseBoolean(stringValue);
    } else if (fieldClass.equals(Byte.class))
    {
      parameter = Byte.parseByte(stringValue);
    } else if (fieldClass.equals(Double.class))
    {
      parameter = Double.parseDouble(stringValue);
    } else if (fieldClass.equals(Float.class))
    {
      parameter = Float.parseFloat(stringValue);
    } else if (fieldClass.equals(Integer.class))
    {
      parameter = Integer.parseInt(stringValue);
    } else if (fieldClass.equals(Long.class))
    {
      parameter = Long.parseLong(stringValue);
    } else if (fieldClass.equals(Short.class))
    {
      parameter = Short.parseShort(stringValue);
    }

    return parameter;
  }

  /**
   * Converts the string value to instance of BigDecimal or BigInteger.
   *
   * @param fieldClass The field class
   * @param stringValue The string value
   * @return The instance of the field class
   * @throws ParseException Throws when the string isn't parseable
   */
  private static Object convertToBigNumbers(final Class fieldClass, final String stringValue)
          throws ParseException
  {
    Object parameter = null;

    if (fieldClass.equals(BigDecimal.class))
    {
      parameter = new BigDecimal(stringValue);
    } else if (fieldClass.equals(BigInteger.class))
    {
      parameter = new BigInteger(stringValue);
    }

    return parameter;
  }

  /**
   * Converts the string value to instance of the field class.
   *
   * @param fieldClass The field class
   * @param stringValue The string value
   * @return The instance of the field class
   * @throws ParseException Throws when the string isn't parseable
   * @throws UnsupportedEncodingException If the Base64 stream contains non UTF-8 chars
   */
  private static Object convertToOthers(final Class fieldClass, final String stringValue)
          throws ParseException, UnsupportedEncodingException
  {
    Object parameter = null;

    if (fieldClass.isEnum())
    {
      parameter = Enum.valueOf((Class<Enum>) fieldClass, stringValue);
    } else if (fieldClass.equals(byte[].class))
    {
      parameter = Base64.decodeBase64(stringValue.getBytes("UTF-8"));
    } else if (fieldClass.equals(Date.class))
    {
      parameter = stringToDate(stringValue);
    } else if (fieldClass.equals(Calendar.class))
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(stringToDate(stringValue));
      parameter = cal;
    }

    return parameter;
  }

  /**
   * Converts String to Date.
   *
   * @param stringValue The string value
   * @return The Date instance
   * @throws ParseException If the string is not parseable
   */
  private static Date stringToDate(final String stringValue) throws ParseException
  {
    try
    {
      String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
      for (Map.Entry<String, String> entry : DATE_FORMAT_PATTERNS.entrySet())
      {
        if (stringValue.matches(entry.getKey()))
        {
          pattern = entry.getValue();
          break;
        }
      }

      DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
      return format.parse(stringValue);
    } catch (ParseException except)
    {
      throw except;
    }
  }
}
