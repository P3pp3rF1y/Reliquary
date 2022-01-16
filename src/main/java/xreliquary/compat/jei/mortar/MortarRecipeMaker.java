package xreliquary.compat.jei.mortar;

import net.minecraft.world.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MortarRecipeMaker {
	private MortarRecipeMaker() {}

	public static List<MortarRecipeJEI> getRecipes() {
		ArrayList<MortarRecipeJEI> recipes = new ArrayList<>();

		for(PotionEssence essence : PotionMap.potionCombinations) {

			List<ItemStack> inputs = essence.getIngredients().stream().map(PotionIngredient::getItem).collect(Collectors.toList());

			ItemStack output = new ItemStack(ModItems.POTION_ESSENCE.get(), 1);
			XRPotionHelper.addPotionEffectsToStack(output, essence.getEffects());

			recipes.add(new MortarRecipeJEI(inputs, output));
		}

		return recipes;
	}
}
