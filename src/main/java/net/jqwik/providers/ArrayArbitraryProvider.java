package net.jqwik.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isArray();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return subtypeProvider.apply(targetType.getComponentType()) //
				.map(elementArbitrary -> Arbitraries.arrayOf(targetType.getRawType(), elementArbitrary)) //
				.orElse(null);
	}

	public SizableArbitrary<?> configure(SizableArbitrary<?> arbitrary, Size size) {
		return arbitrary.withMinSize(size.min()).withMaxSize(size.max());
	}

}
