package xreliquary.compat.jei.lingering;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrowShotRecipeMaker {
	@Nonnull
	public static List<ArrowShotRecipeJEI> getRecipes(ItemStack itemStack, float durationFactor) {
		ArrayList<ArrowShotRecipeJEI> recipes = new ArrayList<>();

		for(PotionEssence essence : Settings.Potions.uniquePotions) {

			ItemStack potion = new ItemStack(ModItems.potion);
			potion.setTagCompound(essence.writeToNBT());
			potion.getTagCompound().setBoolean("lingering", true);

			ItemStack output = itemStack.copy();
			output.stackSize = 8;
			PotionUtils.appendEffects(output, XRPotionHelper.changeDuration(essence.getEffects(), durationFactor));

			recipes.add(new ArrowShotRecipeJEI(Arrays.asList(itemStack, itemStack, itemStack, itemStack, potion, itemStack, itemStack, itemStack, itemStack), output));
		}

		return recipes;
	}
}
