package xreliquary.crafting.factories;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;
import xreliquary.init.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LingeringPotionIngredientFactory extends UseMetaEffectsIngredientFactory {
	@Nonnull
	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		float factor = JsonUtils.hasField(json, "duration_factor") ? JsonUtils.getFloat(json, "duration_factor") : 1;

		return new LingeringPotionIngredient(factor);
	}

	public static class LingeringPotionIngredient extends UseMetaEffectsIngredient {
		private LingeringPotionIngredient(float factor) {
			super(new ItemStack(ModItems.potion), false, true, factor);
		}

		@Override
		public boolean apply(@Nullable ItemStack inventoryStack) {
			return inventoryStack != null && inventoryStack.getItem() == ModItems.potion && ModItems.potion.isLingering(inventoryStack);
		}
	}
}
