package reliquary.compat.jei.cauldron;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import reliquary.init.ModItems;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.ArrayList;
import java.util.List;

public class CauldronRecipeMaker {
	private CauldronRecipeMaker() {}

	public static List<CauldronRecipeJEI> getRecipes() {
		ArrayList<CauldronRecipeJEI> recipes = new ArrayList<>();

		for (PotionEssence essence : PotionMap.uniquePotions) {

			List<ItemStack> inputs = new ArrayList<>();

			ItemStack potionEssence = new ItemStack(ModItems.POTION_ESSENCE.get(), 1);
			PotionHelper.addPotionContentsToStack(potionEssence, essence.getPotionContents());

			inputs.add(potionEssence);

			if (essence.getRedstoneCount() > 0) {
				inputs.add(new ItemStack(Items.REDSTONE, essence.getRedstoneCount()));
			}

			if (essence.getGlowstoneCount() > 0) {
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

			inputs.add(new ItemStack(ModItems.EMPTY_POTION_VIAL.get(), 3));
			splashInputs.add(new ItemStack(ModItems.EMPTY_POTION_VIAL.get(), 3));
			lingeringInputs.add(new ItemStack(ModItems.EMPTY_POTION_VIAL.get(), 3));

			ItemStack output = new ItemStack(ModItems.POTION.get(), 3);
			PotionHelper.addPotionContentsToStack(output, essence.getPotionContents());

			ItemStack outputSplash = new ItemStack(ModItems.SPLASH_POTION.get(), 3);
			PotionHelper.addPotionContentsToStack(outputSplash, essence.getPotionContents());

			ItemStack outputLingering = new ItemStack(ModItems.LINGERING_POTION.get(), 3);
			PotionHelper.addPotionContentsToStack(outputLingering, essence.getPotionContents());

			recipes.add(new CauldronRecipeJEI(inputs, output));
			recipes.add(new CauldronRecipeJEI(splashInputs, outputSplash));
			recipes.add(new CauldronRecipeJEI(lingeringInputs, outputLingering));
		}

		return recipes;
	}
}
