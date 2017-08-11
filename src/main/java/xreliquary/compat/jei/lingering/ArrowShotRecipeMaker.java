package xreliquary.compat.jei.lingering;

import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrowShotRecipeMaker {

	@Nonnull
	public static List<ArrowShotRecipeJEI> getRecipes(ItemStack output) {
		return getRecipes(output, output, 0.2F);
	}

	@Nonnull
	public static List<ArrowShotRecipeJEI> getRecipes(ItemStack output, ItemStack itemStack, float durationFactor) {
		ArrayList<ArrowShotRecipeJEI> recipes = new ArrayList<>();

		for(PotionEssence essence : PotionMap.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.potion);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
			//noinspection ConstantConditions
			potion.getTagCompound().setBoolean("lingering", true);
			potion.getTagCompound().setBoolean("hasPotion", true);

			ItemStack outputCopy = output.copy();
			outputCopy.setCount(8);
			XRPotionHelper.addPotionEffectsToStack(outputCopy, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), durationFactor));

			recipes.add(new ArrowShotRecipeJEI(Arrays.asList(itemStack, itemStack, itemStack, itemStack, potion, itemStack, itemStack, itemStack, itemStack), outputCopy));
		}

		return recipes;
	}
}
