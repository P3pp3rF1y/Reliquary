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
				inputs.add(new ItemStack(Items.REDSTONE, essence.getRedstoneCount()));

			if(essence.getGlowstoneCount() > 0)
				inputs.add(new ItemStack(Items.GLOWSTONE_DUST, essence.getGlowstoneCount()));

			List<ItemStack> splashInputs = new ArrayList<>(inputs);
			List<ItemStack> lingeringInputs = new ArrayList<>(inputs);

			splashInputs.add(new ItemStack(Items.GUNPOWDER));
			lingeringInputs.add(new ItemStack(Items.GUNPOWDER));

			lingeringInputs.add(new ItemStack(Items.DRAGON_BREATH));

			inputs.add(new ItemStack(Items.NETHER_WART));
			splashInputs.add(new ItemStack(Items.NETHER_WART));
			lingeringInputs.add(new ItemStack(Items.NETHER_WART));

			inputs.add(new ItemStack(ModItems.potion, 3));
			splashInputs.add(new ItemStack(ModItems.potion, 3));
			lingeringInputs.add(new ItemStack(ModItems.potion, 3));

			ItemStack output = new ItemStack(ModItems.potion, 3);
			output.setTagCompound(essence.writeToNBT());
			NBTHelper.setBoolean("hasPotion", output, true);

			ItemStack outputSplash = output.copy();
			NBTHelper.setBoolean("splash", outputSplash, true);

			ItemStack outputLingering = output.copy();
			NBTHelper.setBoolean("lingering", outputLingering, true);

			recipes.add(new CauldronRecipeJEI(inputs, output));
			recipes.add(new CauldronRecipeJEI(splashInputs, outputSplash));
			recipes.add(new CauldronRecipeJEI(lingeringInputs, outputLingering));
		}

		return recipes;
	}
}
