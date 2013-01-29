/**
 * CC-LGPL 2.1
 * http://creativecommons.org/licenses/LGPL/2.1/
 */
package hu.javaforum.commons;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ReflectionUtil is provides static methods to invoke getter and setter methods in JavaBean instances. It is based on the ReflectionHelper class.
 * 
 * Changelog: JFPORTAL-94 (2011-07-31) ANDROIDSOAP-6 (2011-01-08) ANDROIDSOAP-1 (2011-01-06) JFPORTAL-94 (2010-02-24) JFPORTAL-94 (2009-11-01) JFPORTAL-79 (2009-10-11) JFPORTAL-79
 * (2009-09-12) JFPORTAL-78 (2009-09-03) First implementation (2009-05-26)
 * 
 * @author Auth Gábor <auth.gabor@javaforum.hu>
 */
public final class ReflectionUtil extends ReflectionHelper {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The static instance (singleton pattern).
	 */
	public static final ReflectionUtil INSTANCE = new ReflectionUtil();
	/**
	 * The static instance of the logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

	/**
	 * The private constructor, because all methods are static.
	 */
	private ReflectionUtil() {
		super();
	}

	/**
	 * Returns a value of the field.
	 * 
	 * @param instance
	 *            The bean
	 * @param fieldName
	 *            The field name of the bean
	 * @return The value; null, if the field is not found
	 */
	public static Object invokeGetter(final Object instance, final String fieldName) {
		if (instance == null) {
			return null;
		}
		if (fieldName == null) {
			return null;
		}
		if ("".equals(fieldName.trim())) {
			return null;
		}

		return invokeGetter(instance.getClass(), instance, fieldName);
	}

	/**
	 * Return a value of the field.
	 * 
	 * @param instanceClass
	 *            The class of the instance
	 * @param instance
	 *            The bean
	 * @param fieldName
	 *            The field name of the bean
	 * @return The value; null, if the field is not found
	 */
	private static Object invokeGetter(final Class instanceClass, final Object instance, final String fieldName) {
		try {
			if ("java.lang.Object".equals(instanceClass.getName())) {
				return null;
			}

			Method method = getGetterMethod(instanceClass, instance, fieldName);
			if (method != null) {
				return method.invoke(instance);
			}
		} catch (Exception except) {
			LOGGER.warn(except.toString());
		} finally {
		}

		return null;
	}

	/**
	 * Returns 'true', if the field is exists in the bean instance.
	 * 
	 * @param instance
	 *            The bean instance
	 * @param fieldName
	 *            The field name
	 * @return True, if the field is exists
	 */
	public static Boolean isGetterExists(final Object instance, final String fieldName) {
		if (instance == null) {
			return Boolean.FALSE;
		}
		if (fieldName == null) {
			return Boolean.FALSE;
		}
		if ("".equals(fieldName.trim())) {
			return Boolean.FALSE;
		}

		return isGetterExists(instance.getClass(), instance, fieldName);
	}

	/**
	 * Set the field value in the bean instance.
	 * 
	 * @param instance
	 *            The bean instance
	 * @param fieldName
	 *            The field name
	 * @param value
	 *            The new value
	 * @return true, if the invoke done successfully
	 */
	public static Boolean invokeSetter(final Object instance, final String fieldName, final Object value) {
		if (instance == null) {
			return false;
		}
		if (fieldName == null) {
			return false;
		}
		if ("".equals(fieldName.trim())) {
			return false;
		}

		return invokeSetter(instance.getClass(), instance, fieldName, value);
	}

	/**
	 * Set the field value in the bean instance.
	 * 
	 * @param instanceClass
	 *            The class of the instance
	 * @param instance
	 *            The bean instance
	 * @param fieldName
	 *            The field name
	 * @param value
	 *            The new value
	 * @return true, if the invoke done successfully
	 */
	private static Boolean invokeSetter(final Class instanceClass, final Object instance, final String fieldName, final Object value) {
		if ("java.lang.Object".equals(instanceClass.getName())) {
			return false;
		}

		try {
			if (fieldName.indexOf('.') > -1) {
				return decapsulate(instanceClass, instance, value, fieldName);
			}

			/**
			 * If the field's class and the value's class is not equals, then need to cast or convert the value to the field's class.
			 */
			Class fieldClass = ReflectionUtil.getField(instanceClass, fieldName).getType();

			Object parameter = createParameterFromValue(fieldClass, value);
			if (parameter == null) {
				LOGGER.warn("The '{}' type isn't supported yet (fieldName was '{}')", fieldClass.getName(), fieldName);
				return false;
			}

			StringBuilder sb = new StringBuilder(fieldName.length() + SET_WORD.length());
			sb.append(SET_WORD);
			sb.append(fieldName);
			sb.setCharAt(SET_WORD.length(), Character.toUpperCase(sb.charAt(SET_WORD.length())));

			Method method = instanceClass.getDeclaredMethod(sb.toString(), fieldClass);
			method.invoke(instance, parameter);
			return true;
		} catch (NoSuchMethodException except) {
			LOGGER.debug("Invoking {}.{}(Object object) because {}", new Object[] {instanceClass.getSuperclass().getName(), fieldName, except.getMessage()});
			return invokeSetter(instanceClass.getSuperclass(), instance, fieldName, value);
		} catch (NoSuchFieldException except) {
			LOGGER.debug("Invoking {}.{}(Object object) because {}", new Object[] {instanceClass.getSuperclass().getName(), fieldName, except.getMessage()});
			return invokeSetter(instanceClass.getSuperclass(), instance, fieldName, value);
		} catch (Exception except) {
			LOGGER.warn(except.toString());
		} finally {
		}

		return false;
	}

	/**
	 * Checks the encapsulated fields (dot separated field name), and calls recursively the invokeSetter in the parent's field.
	 * 
	 * @param instanceClass
	 *            The class of instance
	 * @param instance
	 *            The instance
	 * @param value
	 *            The field value
	 * @param fieldName
	 *            The name of the field
	 * @return True, if the setter was called successfully; False, if the setter wasn't ran successfully or not found; null, if the field name is not encapsulated.
	 */
	private static Boolean decapsulate(final Class instanceClass, final Object instance, final Object value, final String fieldName) {
		String lastFieldName = fieldName;
		String parentFieldName = lastFieldName.substring(0, lastFieldName.indexOf('.'));
		lastFieldName = lastFieldName.substring(lastFieldName.indexOf('.') + 1);
		Object parent = invokeGetter(instanceClass, instance, parentFieldName);
		if (parent == null) {
			return false;
		} else {
			LOGGER.debug("Invoking {}.{}(Object object)", parentFieldName, lastFieldName);
			return invokeSetter(parent.getClass(), parent, lastFieldName, value);
		}
	}

	/**
	 * Gets the first generic class of the field.
	 * 
	 * @param objectClass
	 *            The object
	 * @param fieldName
	 *            The name of the field of the object
	 * @return The class
	 */
	public static Class getFieldGenericClass(final Class objectClass, final String fieldName) {
		try {
			Field field = getField(objectClass, fieldName);
			Class fieldClass = field.getType();

			Type returnType = field.getGenericType();
			if (returnType instanceof ParameterizedType) {
				ParameterizedType type = (ParameterizedType) returnType;
				Type[] typeArguments = type.getActualTypeArguments();
				return (Class) typeArguments[0];
			} else if (fieldClass.isArray()) {
				String arrayElementType = fieldClass.getName().substring(2, fieldClass.getName().length() - 1);
				return Class.forName(arrayElementType);
			}
		} catch (Exception except) {
			LOGGER.warn(except.toString());
		} finally {
		}

		return null;
	}
}
