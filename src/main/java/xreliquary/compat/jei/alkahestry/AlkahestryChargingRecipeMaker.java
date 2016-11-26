package xreliquary.compat.jei.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlkahestryChargingRecipeMaker {
	@Nonnull
	public static List<AlkahestryChargingRecipeJEI> getRecipes() {
		Map<String, AlkahestChargeRecipe> alkahestryChargingRecipes = Settings.AlkahestryTome.chargingRecipes;

		ArrayList<AlkahestryChargingRecipeJEI> recipes = new ArrayList<>();

		for(AlkahestChargeRecipe recipe : alkahestryChargingRecipes.values()) {
			ItemStack inputTome = new ItemStack(ModItems.alkahestryTome, 1, Settings.AlkahestryTome.chargeLimit);

			ItemStack outputTome = new ItemStack(ModItems.alkahestryTome, 1, Settings.AlkahestryTome.chargeLimit - recipe.charge);
			NBTHelper.setInteger("charge", outputTome, recipe.charge);

			if(recipe.dictionaryName != null) {
				recipes.addAll(OreDictionary.getOres(recipe.dictionaryName).stream()
						.map(input -> new AlkahestryChargingRecipeJEI(input, inputTome, outputTome)).collect(Collectors.toList()));
			} else {
				ItemStack input = recipe.item;
				recipes.add(new AlkahestryChargingRecipeJEI(input, inputTome, outputTome));
			}
		}

		return recipes;
	}
}
