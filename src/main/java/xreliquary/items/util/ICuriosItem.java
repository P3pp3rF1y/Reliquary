package xreliquary.items.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICuriosItem {

	Type getCuriosType();

	void onWornTick(ItemStack stack, LivingEntity player);

	default void onEquipped(String identifier, LivingEntity player) {
		//noop
	}

	enum Type {
		BELT("belt"),
		NECKLACE("necklace"),
		BODY("body"),
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
