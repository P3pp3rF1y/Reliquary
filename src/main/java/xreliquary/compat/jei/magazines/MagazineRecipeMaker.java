package xreliquary.compat.jei.magazines;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;

public class MagazineRecipeMaker {
	private MagazineRecipeMaker() {}

	public static List<ShapedRecipe> getRecipes() {
		ArrayList<ShapedRecipe> recipes = new ArrayList<>();

		String group = "xreliquary.potion.magazine";

		for (PotionEssence essence : PotionMap.uniquePotions) {
			List<EffectInstance> effects = XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F);

			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, effects);
			inputs.add(Ingredient.fromStacks(new ItemStack(ModItems.EMPTY_MAGAZINE)));
			addShots(inputs, effects);

			ItemStack output = new ItemStack(ModItems.NEUTRAL_MAGAZINE);
			XRPotionHelper.addPotionEffectsToStack(output, effects);

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "xreliquary.potion.magazine." + output.getTranslationKey());
			recipes.add(new ShapedRecipe(id, group, 3, 3, inputs, output));
		}

		return recipes;
	}

	private static void addShots(List<Ingredient> inputs, List<EffectInstance> effects) {
		ItemStack shot = new ItemStack(ModItems.NEUTRAL_BULLET);
		XRPotionHelper.addPotionEffectsToStack(shot, effects);
		for (int i = 0; i < 4; i++) {
			inputs.add(Ingredient.fromStacks(shot));
		}
	}
}
