package reliquary.compat.jei.lingering;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import reliquary.init.ModItems;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArrowShotRecipeMaker {
	private ArrowShotRecipeMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes(ItemStack output, String itemName) {
		return getRecipes(output, output, 0.2F, itemName);
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes(ItemStack output, ItemStack itemStack, float durationFactor, String itemName) {
		ArrayList<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();

		String group = "reliquary.potion." + itemName;
		for (PotionEssence essence : PotionMap.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.LINGERING_POTION.get());
			PotionHelper.addPotionContentsToStack(potion, essence.getPotionContents());

			ItemStack outputCopy = output.copy();
			outputCopy.setCount(8);
			PotionHelper.addPotionContentsToStack(outputCopy, PotionHelper.changePotionEffectsDuration(essence.getPotionContents(), durationFactor));

			NonNullList<Ingredient> ingredients = NonNullList.create();
			ingredients.addAll(Collections.nCopies(4, Ingredient.of(itemStack)));
			ingredients.add(Ingredient.of(potion));
			ingredients.addAll(Collections.nCopies(4, Ingredient.of(itemStack)));

			ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, ingredients, Optional.empty());
			recipes.add(new RecipeHolder<>(RegistryHelper.getRegistryName(output.getItem()),
					new ShapedRecipe(group, CraftingBookCategory.MISC, pattern, outputCopy)));
		}

		return recipes;
	}
}
