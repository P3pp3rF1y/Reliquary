package xreliquary.compat.jei.lingering;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrowShotRecipeMaker {
	private ArrowShotRecipeMaker() {}

	public static List<ShapedRecipe> getRecipes(ItemStack output, String itemName) {
		return getRecipes(output, output, 0.2F, itemName);
	}

	public static List<ShapedRecipe> getRecipes(ItemStack output, ItemStack itemStack, float durationFactor, String itemName) {
		ArrayList<ShapedRecipe> recipes = new ArrayList<>();

		String group = "xreliquary.potion." + itemName;
		for (PotionEssence essence : PotionMap.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.LINGERING_POTION);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());

			ItemStack outputCopy = output.copy();
			outputCopy.setCount(8);
			XRPotionHelper.addPotionEffectsToStack(outputCopy, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), durationFactor));

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, output.getTranslationKey());
			NonNullList<Ingredient> ingredients = NonNullList.create();
			ingredients.addAll(Collections.nCopies(4, Ingredient.fromStacks(itemStack)));
			ingredients.add(Ingredient.fromStacks(potion));
			ingredients.addAll(Collections.nCopies(4, Ingredient.fromStacks(itemStack)));

			recipes.add(new ShapedRecipe(id, group, 3, 3, ingredients, outputCopy));
		}

		return recipes;
	}
}
