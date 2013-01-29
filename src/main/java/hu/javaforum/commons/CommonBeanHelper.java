/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class is helps the CommonBean class.
 *
 * Changelog:
 * JFPORTAL-94 (2011-07-31)
 * First implementation (2011-07-31)
 *
 * @author Gábor AUTH <gabor.auth@javaforum.hu>
 */
@XmlTransient
public abstract class CommonBeanHelper implements Serializable
{

  /**
   * ASCII (char) 26 constant.
   */
  private static final int ASCII_26 = 26;
  /**
   * ASCII 'pure' printable limit.
   */
  private static final int ASCII_LIMIT = 192;
  /**
   * Prints the date and time in the local thread, because the SimpleDateFormat is
   * not thread safe.
   */
  protected static final ThreadLocal DATETIME_FORMAT = new ThreadLocal()
  {

    @Override
    protected Object initialValue()
    {
      return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    }
  };
  /**
   * Prints the date and time with timw zone in the local thread, because the
   * SimpleDateFormat is not thread safe.
   */
  protected static final ThreadLocal DATETIME_TIMEZONE_FORMAT = new ThreadLocal()
  {

    @Override
    protected Object initialValue()
    {
      return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
    }
  };
  /**
   * The default indent prefix.
   */
  protected static final String INDENT = "  ";
  /**
   * The indent lookup list holds an indent texts.
   */
  protected static final List<String> INDENT_LOOKUP;
  /**
   * The size of the lookup list.
   */
  protected static final int INDENT_LOOKUP_SIZE = 20;

  /**
   * Creates an unmodifiable indent text lookup list.
   */
  static
  {
    List<String> internalIndentLookup = new ArrayList<String>(INDENT_LOOKUP_SIZE);

    StringBuilder sb = new StringBuilder();
    for (int count = 0; count < INDENT_LOOKUP_SIZE; count++)
    {
      internalIndentLookup.add(sb.toString());
      sb.append(INDENT);
    }

    INDENT_LOOKUP = Collections.unmodifiableList(internalIndentLookup);
  }

  /**
   * The 'bean pattern' constructor.
   */
  protected CommonBeanHelper()
  {
    super();
  }

  /**
   * Convert the object into XML safe string.
   *
   * @param o The object
   * @return The string
   */
  public static String quoteXMLValue(final Object o)
  {
    if (o == null)
    {
      return null;
    }

    StringBuilder input = new StringBuilder(o.toString());
    StringBuilder output = new StringBuilder(input.length());
    int seqStart = 0;
    int seqEnd = 0;

    for (int count = 0; count < input.length(); count++)
    {
      char ch = input.charAt(count);
      if (isTransformableEntity(ch))
      {
        if (seqEnd > seqStart)
        {
          output.append(input.substring(seqStart, seqEnd));
        }

        transformEntity(output, ch);
        seqStart = count + 1;
      } else
      {
        seqEnd = count + 1;
      }
    }
    if (seqEnd > seqStart)
    {
      output.append(input.substring(seqStart, seqEnd));
    }

    return output.toString();
  }

  /**
   * Returns with the indent of the specified level. If the level less than
   * the INDENT_LOOKUP_SIZE the indent will be returned from the INDENT_LOOKUP
   * 'cache'.
   *
   * @param level The level
   * @return The indent text
   */
  protected static String getIndentString(final int level)
  {
    if (level < INDENT_LOOKUP_SIZE)
    {
      return INDENT_LOOKUP.get(level);
    }

    StringBuilder sb = new StringBuilder(INDENT.length() * level);
    for (int count = 0; count < level; count++)
    {
      sb.append(INDENT);
    }

    return sb.toString();
  }

  /**
   * Returns true if the char is transformable.
   *
   * @param ch The char
   * @return True, if the char is transformable
   */
  protected static boolean isTransformableEntity(final char ch)
  {
    if ('"' == ch)
    {
      return true;
    } else if ('<' == ch)
    {
      return true;
    } else if ('>' == ch)
    {
      return true;
    } else if ('&' == ch)
    {
      return true;
    } else if (ASCII_26 == ch)
    {
      return true;
    } else if (((int) ch) > ASCII_LIMIT)
    {
      return true;
    }

    return false;
  }

  /**
   * Transforms the char and add the transformed entity to the StringBuilder
   * instance.
   *
   * @param sb The StringBuilder instance
   * @param ch The char
   */
  protected static void transformEntity(final StringBuilder sb, final char ch)
  {
    switch (ch)
    {
      case '"':
        sb.append("&quot;");
        break;
      case '<':
        sb.append("&lt;");
        break;
      case '>':
        sb.append("&gt;");
        break;
      case '&':
        sb.append("&amp;");
        break;
      case ASCII_26:
        sb.append("_");
        break;
      default:
        sb.append("&#");
        sb.append(Integer.toString(ch));
        sb.append(';');
    }
  }

  /**
   * Gets the value of the field in the object.
   *
   * @param field The field
   * @param object The object
   * @return The value of the field in the object
   */
  protected static Object getFieldValue(final Field field, final Object object)
  {
    return AccessController.doPrivileged(new PrivilegedAction()
    {

      /**
       * Run the privileged access.
       *
       * @return The value of the field
       */
      public Object run()
      {
        try
        {
          field.setAccessible(true);
          return field.get(object);
        } catch (Exception except)
        {
          return "***" + except.toString() + "***";
        }
      }
    });
  }
}
