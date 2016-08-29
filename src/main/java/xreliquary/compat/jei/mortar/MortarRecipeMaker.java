package xreliquary.compat.jei.mortar;

import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MortarRecipeMaker {
	@Nonnull
	public static List<MortarRecipeJEI> getRecipes() {
		ArrayList<MortarRecipeJEI> recipes = new ArrayList<>();

		for(PotionEssence essence : Settings.Potions.potionCombinations) {

			List<ItemStack> inputs = essence.ingredients.stream().map(ingredient -> ingredient.item).collect(Collectors.toList());

			ItemStack output = new ItemStack(ModItems.potionEssence, 1);
			output.setTagCompound(essence.writeToNBT());

			recipes.add(new MortarRecipeJEI(inputs, output));
		}

		return recipes;
	}
}
