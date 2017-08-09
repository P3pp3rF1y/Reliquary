package xreliquary.crafting.factories;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nullable;

@MethodsReturnNonnullByDefault
public class UseMetaEffectsIngredientFactory implements IIngredientFactory{
	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		ItemStack stack = CraftingHelper.getItemStack(json, context);

		boolean useMeta = JsonUtils.hasField(json, "use_meta") && JsonUtils.getBoolean(json, "use_meta");
		boolean useEffects = JsonUtils.hasField(json, "use_effects") && JsonUtils.getBoolean(json, "use_effects");
		float factor = JsonUtils.hasField(json, "duration_factor") ? JsonUtils.getFloat(json, "duration_factor") : 1;

		return new UseMetaEffectsIngredient(stack, useMeta, useEffects, factor);
	}

	static class UseMetaEffectsIngredient extends Ingredient {
		private final boolean useMeta;
		private final boolean useEffects;
		private final float factor;

		UseMetaEffectsIngredient(ItemStack itemStack, boolean useMeta, boolean useEffects, float factor) {
			super(itemStack);
			this.useMeta = useMeta;
			this.useEffects = useEffects;
			this.factor = factor;
		}

		boolean isUseMeta() {
			return useMeta;
		}

		boolean isUseEffects() {
			return useEffects;
		}

		public float getFactor() {
			return factor;
		}

		@Override
		public boolean apply(@Nullable ItemStack inventoryStack) {
			if (inventoryStack == null)
			{
				return false;
			}
			else
			{
				for (ItemStack itemstack : getMatchingStacks())
				{
					if (itemstack.getItem() == inventoryStack.getItem())
					{
						int i = itemstack.getMetadata();

						if (useMeta || i == 32767 || i == inventoryStack.getMetadata())
						{
							return true;
						}
					}
				}

				return false;
			}
		}
	}
}
