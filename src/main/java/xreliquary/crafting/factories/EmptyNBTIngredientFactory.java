package xreliquary.crafting.factories;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmptyNBTIngredientFactory implements IIngredientFactory {
	@Nonnull
	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		return new EmptyNBTIngredient(CraftingHelper.getItemStack(json, context));
	}

	private class EmptyNBTIngredient extends Ingredient {

		EmptyNBTIngredient(ItemStack itemStack) {
			super(itemStack);
		}

		@Override
		public boolean apply(@Nullable ItemStack itemStack) {
			//noinspection ConstantConditions
			return super.apply(itemStack) && (itemStack.getTagCompound() == null || itemStack.getTagCompound().getKeySet().isEmpty());
		}
	}
}
