package com.logitech.lip.rules;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Retry used to run in case if failed Note: Retry and {@link Repeat} can't be
 * mixed
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Retry {
	/**
	 * <p>
	 * Indicate the number of time a test method should be repeated.
	 * </p>
	 *
	 * @return number of repeats
	 **/
	int count();
}