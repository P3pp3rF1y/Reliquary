package xreliquary.compat.jei.magazines;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import xreliquary.items.BulletItem;
import xreliquary.items.MagazineItem;
import xreliquary.reference.Reference;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static xreliquary.init.ModItems.*;

public class MagazineRecipeMaker {

	private MagazineRecipeMaker() {}

	public static List<ShapedRecipe> getRecipes() {
		ArrayList<ShapedRecipe> recipes = new ArrayList<>();

		addRegularMagazines(recipes);
		addPotionMagazines(recipes);

		return recipes;
	}

	private static void addRegularMagazines(ArrayList<ShapedRecipe> recipes) {
		Map<BulletItem, MagazineItem> bulletMagazines = new ImmutableMap.Builder<BulletItem, MagazineItem>()
				.put(NEUTRAL_BULLET, NEUTRAL_MAGAZINE)
				.put(EXORCISM_BULLET, EXORCISM_MAGAZINE)
				.put(BLAZE_BULLET, BLAZE_MAGAZINE)
				.put(ENDER_BULLET, ENDER_MAGAZINE)
				.put(CONCUSSIVE_BULLET, CONCUSSIVE_MAGAZINE)
				.put(BUSTER_BULLET, BUSTER_MAGAZINE)
				.put(SEEKER_BULLET, SEEKER_MAGAZINE)
				.put(SAND_BULLET, SAND_MAGAZINE)
				.put(STORM_BULLET, STORM_MAGAZINE)
				.build();

		for(Map.Entry<BulletItem, MagazineItem> bulletMagazine : bulletMagazines.entrySet()) {
			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, bulletMagazine.getKey());
			inputs.add(Ingredient.fromStacks(new ItemStack(EMPTY_MAGAZINE)));
			addShots(inputs, bulletMagazine.getKey());

			ItemStack output = new ItemStack(bulletMagazine.getValue());

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "xreliquary.magazine." + output.getTranslationKey());
			recipes.add(new ShapedRecipe(id, "xreliquary.magazine", 3, 3, inputs, output));
		}
	}

	private static void addPotionMagazines(ArrayList<ShapedRecipe> recipes) {
		for (PotionEssence essence : PotionMap.uniquePotions) {
			List<EffectInstance> effects = XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F);

			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, effects);
			inputs.add(Ingredient.fromStacks(new ItemStack(EMPTY_MAGAZINE)));
			addShots(inputs, effects);

			ItemStack output = new ItemStack(NEUTRAL_MAGAZINE);
			XRPotionHelper.addPotionEffectsToStack(output, effects);

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "xreliquary.potion.magazine." + output.getTranslationKey());
			recipes.add(new ShapedRecipe(id, "xreliquary.potion.magazine", 3, 3, inputs, output));
		}
	}

	private static void addShots(List<Ingredient> inputs, List<EffectInstance> effects) {
		addShots(inputs, effects, NEUTRAL_BULLET);
	}

	private static void addShots(List<Ingredient> inputs, BulletItem shotType) {
		addShots(inputs, Collections.emptyList(), shotType);
	}

	private static void addShots(List<Ingredient> inputs, List<EffectInstance> effects, BulletItem shotType) {
		ItemStack shot = new ItemStack(shotType);
		if (!effects.isEmpty()) {
			XRPotionHelper.addPotionEffectsToStack(shot, effects);
		}
		for (int i = 0; i < 4; i++) {
			inputs.add(Ingredient.fromStacks(shot));
		}
	}
}
