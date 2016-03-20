package xreliquary.compat.jei.cauldron;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CauldronRecipeMaker {
	@Nonnull
	public static List<CauldronRecipeJEI> getRecipes() {
		ArrayList<CauldronRecipeJEI> recipes = new ArrayList<>();

		for(PotionEssence essence : Settings.Potions.uniquePotions) {

			List<ItemStack> inputs = new ArrayList<>();

			ItemStack potionEssence = new ItemStack(ModItems.potionEssence, 1);
			potionEssence.setTagCompound(essence.getPreAugmentationNBT());

			inputs.add(potionEssence);

			if(essence.getRedstoneCount() > 0)
				inputs.add(new ItemStack(Items.redstone, essence.getRedstoneCount()));

			if(essence.getGlowstoneCount() > 0)
				inputs.add(new ItemStack(Items.glowstone_dust, essence.getGlowstoneCount()));

			List<ItemStack> splashInputs = new ArrayList<>(inputs);

			splashInputs.add(new ItemStack(Items.gunpowder));

			inputs.add(new ItemStack(Items.nether_wart));
			splashInputs.add(new ItemStack(Items.nether_wart));

			inputs.add(new ItemStack(ModItems.potion, 3));
			splashInputs.add(new ItemStack(ModItems.potion, 3));

			ItemStack output = new ItemStack(ModItems.potion, 3);
			output.setTagCompound(essence.writeToNBT());
			NBTHelper.setBoolean("hasPotion", output, true);

			ItemStack outputSplash = output.copy();
			NBTHelper.setBoolean("splash", outputSplash, true);

			recipes.add(new CauldronRecipeJEI(inputs, output));
			recipes.add(new CauldronRecipeJEI(splashInputs, outputSplash));
		}

		return recipes;
	}
}
