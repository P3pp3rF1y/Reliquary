package reliquary.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public record SetCodec<E>(Codec<E> elementCodec) implements Codec<Set<E>> {
	@Override
	public <T> DataResult<T> encode(final Set<E> input, final DynamicOps<T> ops, final T prefix) {
		final ListBuilder<T> builder = ops.listBuilder();
		for (final E element : input) {
			builder.add(elementCodec.encodeStart(ops, element));
		}
		return builder.build(prefix);
	}

	@Override
	public <T> DataResult<Pair<Set<E>, T>> decode(final DynamicOps<T> ops, final T input) {
		return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
			final DecoderState<T> decoder = new DecoderState<>(ops);
			stream.accept(decoder::accept);
			return decoder.build();
		});
	}

	@Override
	public String toString() {
		return "SetCodec[" + elementCodec + ']';
	}

	private class DecoderState<T> {
		private static final DataResult<Unit> INITIAL_RESULT = DataResult.success(Unit.INSTANCE, Lifecycle.stable());

		private final DynamicOps<T> ops;
		private final Set<E> elements = new HashSet<>();
		private final Stream.Builder<T> failed = Stream.builder();
		private DataResult<Unit> result = INITIAL_RESULT;

		private DecoderState(final DynamicOps<T> ops) {
			this.ops = ops;
		}

		public void accept(final T value) {
			final DataResult<Pair<E, T>> elementResult = elementCodec.decode(ops, value);
			elementResult.error().ifPresent(error -> failed.add(value));
			elementResult.resultOrPartial().ifPresent(pair -> elements.add(pair.getFirst()));
			result = result.apply2stable((result, element) -> result, elementResult);
		}

		public DataResult<Pair<Set<E>, T>> build() {
			final T errors = ops.createList(failed.build());
			final Pair<Set<E>, T> pair = Pair.of(Set.copyOf(elements), errors);
			return result.map(ignored -> pair).setPartial(pair);
		}
	}
}
