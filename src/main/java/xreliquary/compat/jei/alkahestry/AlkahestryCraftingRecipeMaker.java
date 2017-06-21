package xreliquary.compat.jei.alkahestry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.alkahestry.AlkahestRecipeType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlkahestryCraftingRecipeMaker {
	@Nonnull
	public static List<AlkahestryCraftingRecipeJEI> getRecipes() {
		Map<String, AlkahestCraftRecipe> alkahestryCraftingRecipes = Settings.AlkahestryTome.craftingRecipes;

		ArrayList<AlkahestryCraftingRecipeJEI> recipes = new ArrayList<>();

		for(AlkahestCraftRecipe recipe : alkahestryCraftingRecipes.values()) {
			ItemStack inputTome = new ItemStack(ModItems.alkahestryTome, 1, 0);
			NBTHelper.setInteger("charge", inputTome, Settings.AlkahestryTome.chargeLimit);
			ItemStack outputTome = new ItemStack(ModItems.alkahestryTome, 1, recipe.cost);
			NBTHelper.setInteger("charge", outputTome, Settings.AlkahestryTome.chargeLimit - recipe.cost);

			if(recipe.type == AlkahestRecipeType.OREDICT) {
				if(OreDictionary.getOres(recipe.name).size() > 0) {
					for(ItemStack ore : OreDictionary.getOres(recipe.name)) {
						ItemStack outputOre = ore.copy();
						outputOre.setCount(recipe.yield + 1);
						recipes.add(new AlkahestryCraftingRecipeJEI(ore, inputTome, outputOre, outputTome));
					}
				}
			} else {
				Item item = Item.REGISTRY.getObject(new ResourceLocation(recipe.name));
				if(item != null) {
					if (recipe.type == AlkahestRecipeType.WILDCARD) {
						NonNullList<ItemStack> subItems = NonNullList.create();

						item.getSubItems(item, null, subItems);
						for (ItemStack input : subItems) {
							ItemStack output = input.copy();
							output.setCount(recipe.yield + 1);

							recipes.add(new AlkahestryCraftingRecipeJEI(input, inputTome, output, outputTome));
						}
					} else {
						ItemStack input = new ItemStack(item, 1, recipe.meta);
						ItemStack output = input.copy();
						output.setCount(recipe.yield + 1);
						recipes.add(new AlkahestryCraftingRecipeJEI(input, inputTome, output, outputTome));
					}
				}

			}
		}

		return recipes;
	}
}
