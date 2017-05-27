package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public abstract class NNullableArbitrary<T> implements Arbitrary<T> {

	protected final Class targetClass;
	private double nullProbability = 0.0;

	protected NNullableArbitrary(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		if (nullProbability > 0.0)
			return baseGenerator(tries).injectNull(nullProbability);
		return baseGenerator(tries);
	}

	protected abstract RandomGenerator<T> baseGenerator(int tries);

	public void configure(WithNull withNull) {
		if (withNull.target().isAssignableFrom(targetClass))
			nullProbability = withNull.value();
	}

	public double getNullProbability() {
		return nullProbability;
	}
}
