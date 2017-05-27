package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class NLongArbitrary extends NNullableArbitrary<Long> {

	private long min;
	private long max;

	public NLongArbitrary(long min, long max) {
		super(Long.class);
		this.min = min;
		this.max = max;
	}

	public NLongArbitrary() {
		this(0, 0);
	}

	@Override
	protected RandomGenerator<Long> baseGenerator(int tries) {
		if (min == 0 && max == 0) {
			long max = Arbitrary.defaultMaxFromTries(tries);
			return NShrinkableGenerators.choose(-max, max).withSamples(0L, Long.MIN_VALUE, Long.MAX_VALUE);
		}
		return NShrinkableGenerators.choose(min, max);
	}

	public void configure(LongRange longRange) {
		min = longRange.min();
		max = longRange.max();
	}


}
