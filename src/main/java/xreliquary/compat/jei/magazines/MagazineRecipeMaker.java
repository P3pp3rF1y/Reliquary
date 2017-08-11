package xreliquary.compat.jei.magazines;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import xreliquary.init.ModItems;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MagazineRecipeMaker {
	@Nonnull
	public static List<MagazineRecipeJEI> getRecipes() {
		ArrayList<MagazineRecipeJEI> recipes = new ArrayList<>();

		//add basic set of magazines
		for(int meta = 1; meta <= 9; meta++) {
			List<ItemStack> inputs = new ArrayList<>();
			inputs.add(new ItemStack(ModItems.magazine));
			inputs = addShots(inputs, meta, null);

			recipes.add(new MagazineRecipeJEI(inputs, new ItemStack(ModItems.magazine, 1, meta)));
		}

		//now add potion variants for the neutral one
		for(PotionEssence essence : PotionMap.uniquePotions) {
			List<PotionEffect> effects = XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F);

			List<ItemStack> inputs = new ArrayList<>();
			inputs.add(new ItemStack(ModItems.magazine, 1, 0));
			inputs = addShots(inputs, 1, effects);

			ItemStack output = new ItemStack(ModItems.magazine, 1, 1);
			XRPotionHelper.addPotionEffectsToStack(output, effects);

			recipes.add(new MagazineRecipeJEI(inputs, output));
		}

		return recipes;
	}

	private static List<ItemStack> addShots(List<ItemStack> inputs, int meta, List<PotionEffect> effects) {
		ItemStack shot = new ItemStack(ModItems.bullet, 1, meta);

		if(effects != null && !effects.isEmpty()) {
			XRPotionHelper.addPotionEffectsToStack(shot, effects);
		}

		for(int i = 0; i < 8; i++) {
			inputs.add(shot);
		}

		return inputs;
	}
}
