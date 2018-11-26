package com;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An Observable Reference is essentially a variable that notifies listeners when it's value has changed. This is 
 * particularly useful for user interfaces, but it has a lot of uses.
 * <p>
 * This is not intended for multi-threaded use. All the notifications take place on the same Thread that changes
 * the value. For reliable and ordered messaging among threads, consider using one of the concurrent data
 * structures in the {@link java.util.concurrent} package.
 * 
 * TODO: Figure out what methods this could use to aid in functional programming.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/21/18
 * <p>Time: 3:10 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public final class ObservableReference<T> {
	private @Nullable T value;
	
	private final List<Consumer<@NotNull T>> operations = new LinkedList<>();

	/**
	 * Construct an Observable Reference holding the specified value, which may be null 
	 * @param initialValue The initial value, or null
	 */
	public ObservableReference(@Nullable T initialValue) {
		value = initialValue;
	}

	/**
	 * Change the value of the object, and if the new value is not null, and if the value has changed, notify 
	 * listeners of the change.
	 * @param object the new object.
	 */
	public void setObject(@Nullable T object) {
		//noinspection EqualsReplaceableByObjectsCall
		boolean changed = (object != null) && !object.equals(this.value);
		this.value = object;
		if (changed) {
			for (Consumer<T> operation : operations) {
				operation.accept(object);
			}
		}
	}

	/**
	 * Change the value of the object, and if the value has changed, notify listeners of the change. This will notify
	 * listeners even if the new object is null. If using this object, be sure that all listeners accept null values. 
	 * @param object The new object
	 */
	public void setObjectNotifyAlways(@Nullable T object) {
		boolean changed = !Objects.equals(value, object);
		this.value = object;
		if (changed) {
			for (Consumer<T> operation : operations) {
				operation.accept(object);
			}
		}
	}

	/**
	 * Return the object, which may be null.
	 * @return The possibly null object
	 */
	public @Nullable T getObject() { return value; }

	/**
	 * Return the object if it is not null. Throw a NoSuchElementException if it is null
	 * @return The object if it is not null
	 * @throws NoSuchElementException if the object is null
	 */
	public @NotNull T getValidObject() {
		if (value == null) {
			//noinspection NewExceptionWithoutArguments,ProhibitedExceptionThrown
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	/**
	 * Returns the object wrapped in a Nullable.
	 * @return An Optional that wraps the contained object.
	 */
	public Optional<T> get() { return Optional.ofNullable(value); }

	/**
	 * If a value is present, performs the given action with the value,
	 * otherwise does nothing.
	 *
	 * @param action the action to be performed, if a value is present
	 * @throws NullPointerException if value is present and the given action is
	 *                              {@code null}
	 */
	public void ifPresent(Consumer<? super T> action) {
		if (value != null) {
			action.accept(value);
		}
	}

	/**
	 * Add a listener to respond to changes in the object's value
	 * @param listener The listener.
	 */
	public void addListener(Consumer<@NotNull T> listener) {
		operations.add(listener);
	}

	/**
	 * Remove a listener.
	 * @param listener The listener to remove.
	 * @return true if the listener was found and removed.
	 */
	public boolean removeListener(Consumer<@NotNull T> listener) {
		return operations.remove(listener);
	}

	/**
	 * Removes all listeners.
	 */
	public void removeAllListeners() {
		operations.clear();
	}

	/**
	 * Return true if the object is null.
	 * @return true if the object is null, false otherwise.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isNull() { return value == null; }

	/**
	 * Return true if a non-null object is present.
	 * @return true if the object is not null, false otherwise.
	 */
	public boolean isPresent() { return value != null; }
}
