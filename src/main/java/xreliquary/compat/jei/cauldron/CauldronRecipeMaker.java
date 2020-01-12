package xreliquary.compat.jei.cauldron;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import xreliquary.init.ModItems;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;

public class CauldronRecipeMaker {
	private CauldronRecipeMaker() {}
	public static List<CauldronRecipeJEI> getRecipes() {
		ArrayList<CauldronRecipeJEI> recipes = new ArrayList<>();

		for(PotionEssence essence : PotionMap.uniquePotions) {

			List<ItemStack> inputs = new ArrayList<>();

			ItemStack potionEssence = new ItemStack(ModItems.POTION_ESSENCE, 1);
			XRPotionHelper.addPotionEffectsToStack(potionEssence, essence.getEffects());

			inputs.add(potionEssence);

			if(essence.getRedstoneCount() > 0) {
				inputs.add(new ItemStack(Items.REDSTONE, essence.getRedstoneCount()));
			}

			if(essence.getGlowstoneCount() > 0) {
				inputs.add(new ItemStack(Items.GLOWSTONE_DUST, essence.getGlowstoneCount()));
			}

			List<ItemStack> splashInputs = new ArrayList<>(inputs);
			List<ItemStack> lingeringInputs = new ArrayList<>(inputs);

			splashInputs.add(new ItemStack(Items.GUNPOWDER));
			lingeringInputs.add(new ItemStack(Items.GUNPOWDER));

			lingeringInputs.add(new ItemStack(Items.DRAGON_BREATH));

			inputs.add(new ItemStack(Items.NETHER_WART));
			splashInputs.add(new ItemStack(Items.NETHER_WART));
			lingeringInputs.add(new ItemStack(Items.NETHER_WART));

			inputs.add(new ItemStack(ModItems.POTION, 3));
			splashInputs.add(new ItemStack(ModItems.POTION, 3));
			lingeringInputs.add(new ItemStack(ModItems.POTION, 3));

			ItemStack output = new ItemStack(ModItems.POTION, 3);
			XRPotionHelper.addPotionEffectsToStack(output, essence.getEffects());
			NBTHelper.putBoolean("hasPotion", output, true);

			ItemStack outputSplash = output.copy();
			NBTHelper.putBoolean("splash", outputSplash, true);

			ItemStack outputLingering = output.copy();
			NBTHelper.putBoolean("lingering", outputLingering, true);

			recipes.add(new CauldronRecipeJEI(inputs, output));
			recipes.add(new CauldronRecipeJEI(splashInputs, outputSplash));
			recipes.add(new CauldronRecipeJEI(lingeringInputs, outputLingering));
		}

		return recipes;
	}
}
