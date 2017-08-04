package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmptyNBTIngredient implements IIngredientFactory {
	@Nonnull
	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		return new EmptyNBTIngredientData(CraftingHelper.getItemStack(json, context)) {

		};
	}

	private class EmptyNBTIngredientData extends Ingredient {
		private ItemStack itemStack;

		EmptyNBTIngredientData(ItemStack itemStack) {
			super(itemStack);

			this.itemStack = itemStack;
		}

		@Override
		public boolean apply(@Nullable ItemStack itemStack) {
			//noinspection ConstantConditions
			return super.apply(itemStack) && (itemStack.getTagCompound() == null || itemStack.getTagCompound().getKeySet().isEmpty());
		}
	}
}
