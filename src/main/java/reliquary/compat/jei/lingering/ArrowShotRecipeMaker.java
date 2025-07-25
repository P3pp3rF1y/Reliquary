package reliquary.compat.jei.lingering;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import reliquary.init.ModItems;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.ArrayList;
import java.util.List;

public class ArrowShotRecipeMaker {
	private ArrowShotRecipeMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes(IJeiHelpers jeiHelpers, Item resultItem, String itemName) {
		return getRecipes(jeiHelpers, resultItem, resultItem, 0.2F, itemName);
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes(IJeiHelpers jeiHelpers, Item resultItem, Item inputItem, float durationFactor, String itemName) {
		ArrayList<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();

		String group = "reliquary.potion." + itemName;
		for (PotionEssence essence : PotionMap.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.LINGERING_POTION.get());
			PotionHelper.addPotionContentsToStack(potion, essence.getPotionContents());

			ItemStack result = new ItemStack(resultItem);
			result.setCount(8);
			PotionHelper.addPotionContentsToStack(result, PotionHelper.changePotionEffectsDuration(essence.getPotionContents(), durationFactor));

			IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
			CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, new SlotDisplay.ItemStackSlotDisplay(result))
					.group(group)
					.define('a', Ingredient.of(inputItem))
					.define('p', Ingredient.of(potion.getItem()), new SlotDisplay.ItemStackSlotDisplay(potion))
					.pattern("aaa")
					.pattern("apa")
					.pattern("aaa")
					.build();

			recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, RegistryHelper.getRegistryName(resultItem)), recipe));
		}

		return recipes;
	}
}
