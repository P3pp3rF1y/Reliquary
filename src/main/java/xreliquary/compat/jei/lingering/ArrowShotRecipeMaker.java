package xreliquary.compat.jei.lingering;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;

public class ArrowShotRecipeMaker {
	private ArrowShotRecipeMaker() {}

	public static List<ShapelessRecipe> getRecipes(ItemStack output, String itemName) {
		return getRecipes(output, output, 0.2F, itemName);
	}

	public static List<ShapelessRecipe> getRecipes(ItemStack output, ItemStack itemStack, float durationFactor, String itemName) {
		ArrayList<ShapelessRecipe> recipes = new ArrayList<>();

		String group = "xreliquary.potion." + itemName;
		for (PotionEssence essence : PotionMap.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.LINGERING_POTION);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());

			ItemStack outputCopy = output.copy();
			outputCopy.setCount(8);
			XRPotionHelper.addPotionEffectsToStack(outputCopy, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), durationFactor));

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "xreliquary.potion.magazine." + output.getTranslationKey());
			NonNullList<Ingredient> ingredients = NonNullList.withSize(8, Ingredient.fromStacks(itemStack));
			ingredients.add(Ingredient.fromStacks(potion));

			recipes.add(new ShapelessRecipe(id, group, outputCopy, ingredients));
		}

		return recipes;
	}
}
