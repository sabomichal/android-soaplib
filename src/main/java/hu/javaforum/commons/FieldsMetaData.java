/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the meta-data of (reflection) fields.
 *
 * Changelog:
 * JFPORTAL-94 (2011-07-31)
 * First implementation (2011-07-31)
 *
 * @author Gábor AUTH <gabor.auth@javaforum.hu>
 */
public final class FieldsMetaData
{

  /**
   * The LOGGER instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(FieldsMetaData.class);
  /**
   * The fields.
   */
  private final Field[] fields;
  /**
   * The fields with PrintField annotation.
   */
  private final boolean[] printFields;
  /**
   * The fields with hidden parameter.
   */
  private final boolean[] hiddenFields;
  /**
   * The fields maximum item dump value.
   */
  private final int[] maximumItemDumpFields;
  /**
   * The fields maximum hex dump value.
   */
  private final int[] maximumHexDumpFields;

  /**
   * Fills up the arrays from the object.
   *
   * @param object The object
   */
  public FieldsMetaData(final Object object)
  {
    int filteredFieldsSize = 0;
    Field[] internalFields = iterateFields(object.getClass());
    boolean[] internalPrintFields = new boolean[internalFields.length];
    boolean[] internalHiddenFields = new boolean[internalFields.length];
    int[] internalMaximumItemDumpFields = new int[internalFields.length];
    int[] internalMaximumHexDumpFields = new int[internalFields.length];

    for (int count = 0; count < internalFields.length; count++)
    {
      Field field = internalFields[count];
      /**
       * Skip Axis generated fields and all static fields.
       */
      if (field.getName().startsWith("__"))
      {
        continue;
      } else if ((field.getModifiers() & Modifier.STATIC) != 0)
      {
        continue;
      }
      internalFields[filteredFieldsSize] = field;
      internalPrintFields[filteredFieldsSize] = false;
      internalHiddenFields[filteredFieldsSize] = false;
      internalMaximumItemDumpFields[filteredFieldsSize] = Integer.MAX_VALUE;
      internalMaximumHexDumpFields[filteredFieldsSize] = Integer.MAX_VALUE;

      /**
       * Query PrintField annotations.
       */
      try
      {
        PrintField printField = field.getAnnotation(PrintField.class);
        if (printField != null)
        {
          internalPrintFields[filteredFieldsSize] = true;
          internalHiddenFields[filteredFieldsSize] = printField.hidden();
          internalMaximumItemDumpFields[filteredFieldsSize] = printField.maximumItemDump();
          internalMaximumHexDumpFields[filteredFieldsSize] = printField.maximumHexDump();
        }
      } catch (Throwable except)
      {
        /**
         * The getAnnotation causes system exception on Android platform, when
         * the class of annotation is not in the class path.
         */
        LOGGER.warn("Warning, getAnnotation returned with %1$s!", except.toString());
      }
      filteredFieldsSize++;
    }

    fields = new Field[filteredFieldsSize];
    printFields = new boolean[filteredFieldsSize];
    hiddenFields = new boolean[filteredFieldsSize];
    maximumHexDumpFields = new int[filteredFieldsSize];
    maximumItemDumpFields = new int[filteredFieldsSize];
    System.arraycopy(internalFields, 0, fields, 0, filteredFieldsSize);
    System.arraycopy(internalPrintFields, 0, printFields, 0, filteredFieldsSize);
    System.arraycopy(internalHiddenFields, 0, hiddenFields, 0, filteredFieldsSize);
    System.arraycopy(internalMaximumHexDumpFields, 0, maximumHexDumpFields, 0, filteredFieldsSize);
    System.arraycopy(internalMaximumItemDumpFields, 0, maximumItemDumpFields, 0, filteredFieldsSize);
  }

  /**
   * Query fields of the class into the array.
   *
   * @param c The class
   * @return The array
   */
  public static Field[] iterateFields(final Class c)
  {
    final Field[] emptyFields = new Field[0];
    if (c == null)
    {
      return emptyFields;
    }

    Field[] parentFields = null;
    String superclassName = c.getSuperclass().getName();
    if (superclassName.equals(Object.class.getName()))
    {
      parentFields = emptyFields;
    } else if (superclassName.equals(CommonBean.class.getName()))
    {
      parentFields = emptyFields;
    } else
    {
      parentFields = iterateFields(c.getSuperclass());
    }

    Field[] declaredFields = c.getDeclaredFields();
    Field[] fields = new Field[declaredFields.length + parentFields.length];
    System.arraycopy(declaredFields, 0, fields, 0, declaredFields.length);
    System.arraycopy(parentFields, 0, fields, declaredFields.length, parentFields.length);

    return fields;
  }

  /**
   * Returns with the fields array.
   *
   * @return The array
   */
  public Field[] getFields()
  {
    return (Field[]) fields.clone();
  }

  /**
   * Returns with the hidden fields array.
   *
   * @return The array
   */
  public boolean[] getHiddenFields()
  {
    return (boolean[]) hiddenFields.clone();
  }

  /**
   * Returns with the maximum hex dump fields array.
   *
   * @return The array
   */
  public int[] getMaximumHexDumpFields()
  {
    return (int[]) maximumHexDumpFields.clone();
  }

  /**
   * Returns with the maximum item dump fields array.
   *
   * @return The array
   */
  public int[] getMaximumItemDumpFields()
  {
    return (int[]) maximumItemDumpFields.clone();
  }

  /**
   * Returns with the print fields array.
   *
   * @return The array
   */
  public boolean[] getPrintFields()
  {
    return (boolean[]) printFields.clone();
  }
}
