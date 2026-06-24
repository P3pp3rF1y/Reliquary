package reliquary.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import reliquary.item.InfernalTearItem;

import javax.annotation.Nullable;

public record InfernalTearEmpty() implements ConditionalItemModelProperty {
	public static final MapCodec<InfernalTearEmpty> MAP_CODEC = MapCodec.unit(new InfernalTearEmpty());

	@Override
	public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i,
			ItemDisplayContext itemDisplayContext) {
		return InfernalTearItem.getStackFromTear(itemStack).isEmpty();
	}

	@Override
	public MapCodec<? extends ConditionalItemModelProperty> type() {
		return MAP_CODEC;
	}
}
