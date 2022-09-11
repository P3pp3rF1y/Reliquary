package reliquary.compat.jei.lingering;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import reliquary.init.ModItems;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionMap;
import reliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrowShotRecipeMaker {
	private ArrowShotRecipeMaker() {
	}

	public static List<CraftingRecipe> getRecipes(ItemStack output, String itemName) {
		return getRecipes(output, output, 0.2F, itemName);
	}

	public static List<CraftingRecipe> getRecipes(ItemStack output, ItemStack itemStack, float durationFactor, String itemName) {
		ArrayList<CraftingRecipe> recipes = new ArrayList<>();

		String group = "reliquary.potion." + itemName;
		for (PotionEssence essence : PotionMap.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.LINGERING_POTION.get());
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());

			ItemStack outputCopy = output.copy();
			outputCopy.setCount(8);
			XRPotionHelper.addPotionEffectsToStack(outputCopy, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), durationFactor));

			NonNullList<Ingredient> ingredients = NonNullList.create();
			ingredients.addAll(Collections.nCopies(4, Ingredient.of(itemStack)));
			ingredients.add(Ingredient.of(potion));
			ingredients.addAll(Collections.nCopies(4, Ingredient.of(itemStack)));

			recipes.add(new ShapedRecipe(RegistryHelper.getRegistryName(output.getItem()), group, 3, 3, ingredients, outputCopy));
		}

		return recipes;
	}
}
