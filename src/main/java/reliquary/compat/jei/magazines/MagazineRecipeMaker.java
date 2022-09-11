package reliquary.compat.jei.magazines;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import reliquary.items.BulletItem;
import reliquary.items.MagazineItem;
import reliquary.reference.Settings;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionMap;
import reliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static reliquary.init.ModItems.*;

public class MagazineRecipeMaker {

	private MagazineRecipeMaker() {
	}

	public static List<CraftingRecipe> getRecipes() {
		ArrayList<CraftingRecipe> recipes = new ArrayList<>();

		addRegularMagazines(recipes);
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			addPotionMagazines(recipes);
		}

		return recipes;
	}

	private static void addRegularMagazines(ArrayList<CraftingRecipe> recipes) {
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

		for (Map.Entry<BulletItem, MagazineItem> bulletMagazine : bulletMagazines.entrySet()) {
			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, bulletMagazine.getKey());
			inputs.add(Ingredient.of(new ItemStack(EMPTY_MAGAZINE.get())));
			addShots(inputs, bulletMagazine.getKey());

			ItemStack output = new ItemStack(bulletMagazine.getValue());

			recipes.add(new ShapedRecipe(RegistryHelper.getRegistryName(output.getItem()), "reliquary.magazine", 3, 3, inputs, output));
		}
	}

	private static void addPotionMagazines(ArrayList<CraftingRecipe> recipes) {
		for (PotionEssence essence : PotionMap.uniquePotions) {
			List<MobEffectInstance> effects = XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F);

			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, effects);
			inputs.add(Ingredient.of(new ItemStack(EMPTY_MAGAZINE.get())));
			addShots(inputs, effects);

			ItemStack output = new ItemStack(NEUTRAL_MAGAZINE.get());
			XRPotionHelper.addPotionEffectsToStack(output, effects);

			recipes.add(new ShapedRecipe(RegistryHelper.getRegistryName(output.getItem()), "reliquary.potion.magazine", 3, 3, inputs, output));
		}
	}

	private static void addShots(List<Ingredient> inputs, List<MobEffectInstance> effects) {
		addShots(inputs, effects, NEUTRAL_BULLET.get());
	}

	private static void addShots(List<Ingredient> inputs, BulletItem shotType) {
		addShots(inputs, Collections.emptyList(), shotType);
	}

	private static void addShots(List<Ingredient> inputs, List<MobEffectInstance> effects, BulletItem shotType) {
		ItemStack shot = new ItemStack(shotType);
		if (!effects.isEmpty()) {
			XRPotionHelper.addPotionEffectsToStack(shot, effects);
		}
		for (int i = 0; i < 4; i++) {
			inputs.add(Ingredient.of(shot));
		}
	}
}
