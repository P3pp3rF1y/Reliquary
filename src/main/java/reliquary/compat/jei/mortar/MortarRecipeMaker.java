package reliquary.compat.jei.mortar;

import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionIngredient;
import reliquary.util.potions.PotionMap;

import java.util.ArrayList;
import java.util.List;

public class MortarRecipeMaker {
	private MortarRecipeMaker() {
	}

	public static List<MortarRecipeJEI> getRecipes() {
		ArrayList<MortarRecipeJEI> recipes = new ArrayList<>();

		for (PotionEssence essence : PotionMap.potionCombinations) {

			List<ItemStack> inputs = essence.getIngredients().stream().map(PotionIngredient::getItem).toList();

			ItemStack output = new ItemStack(ModItems.POTION_ESSENCE.get(), 1);
			PotionHelper.addPotionContentsToStack(output, essence.getPotionContents());

			recipes.add(new MortarRecipeJEI(inputs, output));
		}

		return recipes;
	}
}
