package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;
import org.opentest4j.TestAbortedException;

import static org.apiguardian.api.API.Status.*;

/**
 * Represents the result of calling a property method with a list of parameters.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public interface TryExecutionResult {

	/**
	 * Status of running a single try.
	 */
	enum Status {
		/**
		 * Current try does not falsify the property
		 */
		SATISFIED,

		/**
		 * Current try does falsify the property
		 */
		FALSIFIED,

		/**
		 * Current try has invalid parameters
		 */
		INVALID
	}

	/**
	 * Create a result that satisfies the current property with the current parameter set.
	 * All remaining tries to be executed.
	 *
	 * @return result instance
	 */
	static TryExecutionResult satisfied() {
		return satisfied(false);
	}

	/**
	 * Create a result that satisfies the current property and will immediately
	 * finish this property as successful.
	 *
	 * @return result instance
	 */
	static TryExecutionResult satisfied(boolean shouldPropertyFinishEarly) {
		return new TryExecutionResult() {
			@Override
			public Status status() {
				return Status.SATISFIED;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.empty();
			}

			@Override
			public boolean shouldPropertyFinishEarly() {
				return shouldPropertyFinishEarly;
			}

			@Override
			public String toString() {
				return String.format("TryExecutionResult(%s)", status().name());
			}
		};
	}

	/**
	 * Create a result that falsified the current property and will immediately
	 * finish this property as failed.
	 *
	 * @param throwable The throwable to describe the reason of falsification
	 * @return result instance
	 */
	static TryExecutionResult falsified(Throwable throwable) {
		return new TryExecutionResult() {
			@Override
			public Status status() {
				return Status.FALSIFIED;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.ofNullable(throwable);
			}

			@Override
			public boolean shouldPropertyFinishEarly() {
				return false;
			}

			@Override
			public String toString() {
				return String.format("TryExecutionResult(%s): %s", status().name(), throwable().map(Throwable::getMessage).orElse("null"));
			}
		};
	}

	/**
	 * Create a result that calls out the current parameter list as invalid.
	 * All remaining tries will be executed.
	 *
	 * @param throwable A (potentially null) exception. Usually of type {@linkplain TestAbortedException}.
	 * @return result instance
	 */
	@API(status = EXPERIMENTAL, since = "1.3.7")
	static TryExecutionResult invalid(Throwable throwable) {
		return new TryExecutionResult() {
			@Override
			public Status status() {
				return Status.INVALID;
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.ofNullable(throwable);
			}

			@Override
			public boolean shouldPropertyFinishEarly() {
				return false;
			}

			@Override
			public String toString() {
				return String.format("TryExecutionResult(%s)", status().name());
			}

		};
	}

	/**
	 * Create a result that calls out the current parameter list as invalid.
	 * All remaining tries will be executed.
	 *
	 * @return result instance
	 */
	static TryExecutionResult invalid() {
		return invalid(null);
	}

	/**
	 * @return true if status is satisfied
	 */
	default boolean isSatisfied() {
		return this.status() == Status.SATISFIED;
	}

	/**
	 * @return true if status is falsified
	 */
	@API(status = EXPERIMENTAL, since = "1.3.3")
	default boolean isFalsified() {
		return this.status() == Status.FALSIFIED;
	}


	/**
	 * @return true if status is invalid
	 */
	@API(status = EXPERIMENTAL, since = "1.3.3")
	default boolean isInvalid() {
		return this.status() == Status.INVALID;
	}

	/**
	 * @return Status enum
	 */
	Status status();

	/**
	 * Will return {@code Optional.empty()} if status is anything but {@linkplain Status#FALSIFIED}.
	 *
	 * @return instance of Throwable or subtype
	 */
	Optional<Throwable> throwable();

	/**
	 * @return true if status is satisfied and remaining tries shall be skipped
	 */
	boolean shouldPropertyFinishEarly();
}
