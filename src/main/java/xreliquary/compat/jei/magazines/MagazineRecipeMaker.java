package xreliquary.compat.jei.magazines;

import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import xreliquary.items.BulletItem;
import xreliquary.items.MagazineItem;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;
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
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			addPotionMagazines(recipes);
		}

		return recipes;
	}

	private static void addRegularMagazines(ArrayList<ShapedRecipe> recipes) {
		Map<BulletItem, MagazineItem> bulletMagazines = new ImmutableMap.Builder<BulletItem, MagazineItem>()
				.put(NEUTRAL_BULLET.get(), NEUTRAL_MAGAZINE.get())
				.put(EXORCISM_BULLET.get(), EXORCISM_MAGAZINE.get())
				.put(BLAZE_BULLET.get(), BLAZE_MAGAZINE.get())
				.put(ENDER_BULLET.get(), ENDER_MAGAZINE.get())
				.put(CONCUSSIVE_BULLET.get(), CONCUSSIVE_MAGAZINE.get())
				.put(BUSTER_BULLET.get(), BUSTER_MAGAZINE.get())
				.put(SEEKER_BULLET.get(), SEEKER_MAGAZINE.get())
				.put(SAND_BULLET.get(), SAND_MAGAZINE.get())
				.put(STORM_BULLET.get(), STORM_MAGAZINE.get())
				.build();

		for(Map.Entry<BulletItem, MagazineItem> bulletMagazine : bulletMagazines.entrySet()) {
			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, bulletMagazine.getKey());
			inputs.add(Ingredient.fromStacks(new ItemStack(EMPTY_MAGAZINE.get())));
			addShots(inputs, bulletMagazine.getKey());

			ItemStack output = new ItemStack(bulletMagazine.getValue());

			recipes.add(new ShapedRecipe(RegistryHelper.getRegistryName(output.getItem()), "xreliquary.magazine", 3, 3, inputs, output));
		}
	}

	private static void addPotionMagazines(ArrayList<ShapedRecipe> recipes) {
		for (PotionEssence essence : PotionMap.uniquePotions) {
			List<EffectInstance> effects = XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F);

			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, effects);
			inputs.add(Ingredient.fromStacks(new ItemStack(EMPTY_MAGAZINE.get())));
			addShots(inputs, effects);

			ItemStack output = new ItemStack(NEUTRAL_MAGAZINE.get());
			XRPotionHelper.addPotionEffectsToStack(output, effects);

			recipes.add(new ShapedRecipe(RegistryHelper.getRegistryName(output.getItem()), "xreliquary.potion.magazine", 3, 3, inputs, output));
		}
	}

	private static void addShots(List<Ingredient> inputs, List<EffectInstance> effects) {
		addShots(inputs, effects, NEUTRAL_BULLET.get());
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
