/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates the fields of the CommonBean descendants.
 *
 * Changelog:
 * JFPORTAL-94 (2011-07-31)
 * First implementation
 *
 * @author Gábor AUTH <gabor.auth@javaforum.hu>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrintField
{

  /**
   * Hide the value of the field.
   */
  boolean hidden() default false;

  /**
   * Maximum elements of the byte arrays.
   */
  int maximumHexDump() default Integer.MAX_VALUE;

  /**
   * Maximum element of the lists or arrays.
   */
  int maximumItemDump() default Integer.MAX_VALUE;
}
