package com.neptunedreams.functions;

import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class to support functional programming. This is a collection of classes to make Optional obsolete.
 * Maybe the new class to replace it will be called Elective. And maybe the class to replace Stream will be called 
 * Brook, Creek, Rill, Runnel, Flow, Pipe, or Slurry.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/4/18
 * <p>Time: 11:43 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum FunctionUtility {
	;

	/**
	 * Change your object to an Optional. This is intended for functional programming when you need an Optional
	 * value. Use this method for brevity. (You could also write {@code Optional.ofNullable(widget)} but
	 * {@code opt(widget)}
	 * is quicker.
	 *
	 * @param t   The (possibly null) value to wrap in an Optional
	 * @param <T> The type of the value to wrap
	 * @return An Optional that wraps the parameter
	 * @author Miguel Muñoz SwingGuy1024@yahoo.com
	 * @see #optFun(Function)
	 */
	public static <T> Optional<T> opt(T t) { return Optional.ofNullable(t); }

	/**
	 * optFun is short for OptionalFunction method. Wraps your ordinary getter in a method that returns Optional.
	 * This is intended for functional programming when you need to specify a getter that returns an Optional,
	 * but the getter you need to use doesn't. So, for example, if your getter method is
	 * {@code String Widget.getName()}, and you need to pass it to {@code Optional.flatMap()}, you can
	 * say this:<br>
	 * &nbsp;&nbsp;{@code flatMap(optFun(Widget::getName))}
	 * <p>
	 * <strong>Example:</strong>
	 * <pre>
	 * return opt(computer)
	 *   .flatMap(optFun(Computer2::getSoundCard)) // getSoundCard() doesn't return Optional
	 *   .flatMap(optFun(SoundCard2::getUSB))      // flatMap() needs a method that returns Optional
	 *   .map(USB2::getVersion)
	 *   .orElse("UNKNOWN");
	 * </pre>
	 *
	 * @param function The getter or other Function to wrap, usually expressed as a function reference
	 * @param <T>      The input type of the function (This would be Widget in the example above)
	 * @param <U>      The return type of the function (This would be String in the example above)
	 * @return A function that returns a type of {@code Optional<U>}
	 * @author Miguel Muñoz SwingGuy1024@yahoo.com
	 * @see #opt(Object)
	 */
	public static <T, U> Function<? super T, Optional<U>> optFun(Function<T, U> function) {
		return t -> Optional.ofNullable(function.apply(t));
	}
}
