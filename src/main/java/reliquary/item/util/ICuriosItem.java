package reliquary.item.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICuriosItem {

	Type getCuriosType();

	default void onWornTick(ItemStack stack, LivingEntity player) {
		if (player.level() instanceof ServerLevel serverLevel) {
			onWornServerTick(stack, serverLevel, player);
		} else {
			onWornClientTick(stack, player);
		}
	}

	default void onWornServerTick(ItemStack stack, ServerLevel serverLevel, LivingEntity player) {
		//noop
	}

	default void onWornClientTick(ItemStack stack, LivingEntity player) {
		//noop
	}

	default void onEquipped(String identifier, LivingEntity player) {
		//noop
	}

	enum Type {
		BELT("belt"),
		NECKLACE("necklace"),
		BODY("body"),
		CHARM("charm"),
		RING("ring"),
		NONE("none");

		private final String identifier;

		Type(String identifier) {
			this.identifier = identifier;
		}

		public String getIdentifier() {
			return identifier;
		}
	}
}
