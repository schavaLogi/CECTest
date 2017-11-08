package com.logitech.lip.rules;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Repeat is used to run upon success or failure
 * 
 * Note: Repeat and {@link Retry} can't be mixed
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Repeat {
	/**
	 * <p>
	 * Indicate the number of time a test method should be repeated.
	 * </p>
	 *
	 * @return number of repeats
	 **/
	int count();
}
