package reliquary.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import reliquary.item.RodOfLyssaItem;

import javax.annotation.Nullable;

public record LyssaRodCast() implements ConditionalItemModelProperty {
	public static final MapCodec<LyssaRodCast> MAP_CODEC = MapCodec.unit(new LyssaRodCast());

	@Override
	public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i,
			ItemDisplayContext itemDisplayContext) {
		if (livingEntity == null) {
			return false;
		}
		if (clientLevel == null) {
			return false;
		}
		int entityId = RodOfLyssaItem.getHookEntityId(itemStack);
		return (livingEntity.getMainHandItem() == itemStack || livingEntity.getOffhandItem() == itemStack) && entityId > 0
				&& clientLevel.getEntity(entityId) != null;
	}

	@Override
	public MapCodec<? extends ConditionalItemModelProperty> type() {
		return MAP_CODEC;
	}
}
