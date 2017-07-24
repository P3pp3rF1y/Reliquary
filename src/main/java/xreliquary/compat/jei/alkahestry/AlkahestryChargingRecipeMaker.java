package xreliquary.compat.jei.alkahestry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.AlkahestRecipeType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlkahestryChargingRecipeMaker {
	@Nonnull
	public static List<AlkahestryChargingRecipeJEI> getRecipes() {
		Map<String, AlkahestChargeRecipe> alkahestryChargingRecipes = Settings.AlkahestryTome.chargingRecipes;

		ArrayList<AlkahestryChargingRecipeJEI> recipes = new ArrayList<>();

		for(AlkahestChargeRecipe recipe : alkahestryChargingRecipes.values()) {
			ItemStack inputTome = new ItemStack(ModItems.alkahestryTome, 1, Settings.AlkahestryTome.chargeLimit);

			ItemStack outputTome = new ItemStack(ModItems.alkahestryTome, 1, Settings.AlkahestryTome.chargeLimit - recipe.charge);
			NBTHelper.setInteger("charge", outputTome, recipe.charge);

			if(recipe.type == AlkahestRecipeType.OREDICT) {
				if(OreDictionary.getOres(recipe.name).size() > 0) {
					for(ItemStack ore : OreDictionary.getOres(recipe.name)) {
						recipes.add(new AlkahestryChargingRecipeJEI(ore, inputTome, outputTome));
					}
				}
			} else {
				Item item = Item.REGISTRY.getObject(new ResourceLocation(recipe.name));
				if(item != null) {
					if (recipe.type == AlkahestRecipeType.WILDCARD) {
						NonNullList<ItemStack> subItems = NonNullList.create();

						//noinspection ConstantConditions
						item.getSubItems(item.getCreativeTab(), subItems);
						for (ItemStack input : subItems) {
							recipes.add(new AlkahestryChargingRecipeJEI(input, inputTome, outputTome));
						}
					} else {
						ItemStack input = new ItemStack(item, 1, recipe.meta);
						recipes.add(new AlkahestryChargingRecipeJEI(input, inputTome, outputTome));
					}
				}
			}
		}

		return recipes;
	}
}
