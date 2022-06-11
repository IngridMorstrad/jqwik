package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

// TODO: Move to package of use
public class Memoize {

	private static Store<Map<Tuple3<Arbitrary<?>, Integer, Boolean>, RandomGenerator<?>>> generatorStore() {
		return Store.getOrCreate(Memoize.class, Lifespan.PROPERTY, () -> new LruCache<>(500));
	}

	// TODO: Is generator supplier really needed or is the same one used everywhere?
	@SuppressWarnings("unchecked")
	public static <U> RandomGenerator<U> memoizedGenerator(
			Arbitrary<U> arbitrary,
			int genSize,
			boolean withEdgeCases,
			Supplier<RandomGenerator<U>> generatorSupplier
	) {
		Tuple3<Arbitrary<?>, Integer, Boolean> key = Tuple.of(arbitrary, genSize, withEdgeCases);

		RandomGenerator<?> generator = computeIfAbsent(
				generatorStore().get(),
				key,
				ignore -> generatorSupplier.get()
		);
		return (RandomGenerator<U>) generator;
	}

	// Had to roll my on computeIfAbsent because HashMap.computeIfAbsent()
	// does not allow modifications of the map within the mapping function
	private static <K, V> V computeIfAbsent(
			Map<K, V> cache,
			K key,
			Function<? super K, ? extends V> mappingFunction
	) {
		V result = cache.get(key);

		if (result == null) {
			result = mappingFunction.apply(key);
			cache.put(key, result);
		}

		return result;
	}

}
