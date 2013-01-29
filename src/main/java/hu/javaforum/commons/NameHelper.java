/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

/**
 * Helper class for some name releated method.
 *
 * Changelog:
 * The first implementation (2012-04-30)
 *
 * @author Gábor AUTH <auth.gabor@javaforum.hu>
 */
public final class NameHelper
{

  /**
   * The singleton pattern.
   */
  public static final NameHelper INSTANCE = new NameHelper();

  /**
   * The 'bean pattern' constructor.
   */
  private NameHelper()
  {
    super();
  }

  /**
   * Converts a namespace prefix name to char array. The namespace prefix
   * converted to char array for better performance.
   *
   * @param nsPrefixName The namespace prefix name
   * @return The char array
   */
  public static char[] createNsPrefix(final String nsPrefixName)
  {
    return nsPrefixName == null ? new char[0] : (nsPrefixName + ":").toCharArray();
  }

  /**
   * Creates a name of the bean. It is uses a `firstName`, if it is not null,
   * or it is uses a simple name of the object. The name of the bean
   * converted to char array for better performance.
   *
   * @param firstName The value of the first name
   * @param object The object
   * @return The name of the first XML tag
   */
  public static char[] createFirstName(final String firstName, final Object object)
  {
    return firstName == null ? firstToLowerCase(object.getClass().getSimpleName()) : firstName.toCharArray();
  }

  /**
   * Returns with lower case first letter.
   *
   * @param text The text
   * @return Text with lower case first letter
   */
  public static char[] firstToLowerCase(final String text)
  {
    if (text == null || text.length() < 1)
    {
      return new char[0];
    }

    return modifyFirstLetter(false, text.toCharArray(), true);
  }

  /**
   * Returns with lower case first letter, if the condition is true.
   *
   * @param text The text
   * @param condition True, if the conversion necessary
   * @return Text with upper case first letter
   */
  public static char[] firstToLowerCase(final char[] text,
          final Boolean condition)
  {
    return modifyFirstLetter(false, text, condition);
  }

  /**
   * Returns with upper case first letter.
   *
   * @param text The text
   * @return Text with upper case first letter
   */
  public static char[] firstToUpperCase(final String text)
  {
    if (text == null || text.length() < 1)
    {
      return new char[0];
    }

    return modifyFirstLetter(true, text.toCharArray(), true);
  }

  /**
   * Returns with upper case first letter, if the condition is true.
   *
   * @param text The text
   * @param condition True, if the conversion necessary
   * @return Text with upper case first letter
   */
  public static char[] firstToUpperCase(final char[] text,
          final Boolean condition)
  {
    return modifyFirstLetter(true, text, condition);
  }

  /**
   * Helper method of the firstToLowerCase and firstToUpperCase methods.
   *
   * @param toUpperCase True, if upper case; false if lower case
   * @param text The text
   * @param condition True, if the conversion necessary
   * @return The modified text
   */
  protected static char[] modifyFirstLetter(final Boolean toUpperCase, final char[] text,
          final Boolean condition)
  {
    if (text == null || text.length < 1)
    {
      return new char[0];
    }

    if (condition)
    {
      char[] textAsChars = (char[]) text.clone();
      textAsChars[0] = toUpperCase ? Character.toUpperCase(textAsChars[0])
              : Character.toLowerCase(textAsChars[0]);
      return textAsChars;
    }

    return text;
  }
}
