package reliquary.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class CodecHelper {
	public static final Codec<ItemStack> OVERSIZED_ITEM_STACK_CODEC = Codec.lazyInitialized(
			() -> RecordCodecBuilder.create(
					instance -> instance.group(
									Item.CODEC.fieldOf("id").forGetter(ItemStack::typeHolder),
									Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
									DataComponentPatch.CODEC
											.optionalFieldOf("components", DataComponentPatch.EMPTY)
											.forGetter(ItemStack::getComponentsPatch)
							)
							.apply(instance, ItemStack::new)));


	public static <T> Codec<Set<T>> setOf(Codec<T> elementCodec) {
		return new SetCodec<>(elementCodec);
	}
}
