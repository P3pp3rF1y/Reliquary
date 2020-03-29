package xreliquary.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IBaubleItem {

	Type getBaubleType();

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
