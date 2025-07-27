package reliquary.compat.jei.magazines;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import reliquary.item.BulletItem;
import reliquary.item.MagazineItem;
import reliquary.reference.Config;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static reliquary.init.ModItems.*;

public class MagazineRecipeMaker {

	private MagazineRecipeMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();

		addRegularMagazines(recipes);
		if (Boolean.FALSE.equals(Config.COMMON.disable.disablePotions.get())) {
			addPotionMagazines(recipes);
		}

		return recipes;
	}

	private static void addRegularMagazines(List<RecipeHolder<CraftingRecipe>> recipes) {
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

			ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, inputs, Optional.empty());

			recipes.add(new RecipeHolder<>(RegistryHelper.getRegistryName(output.getItem()), new ShapedRecipe("reliquary.magazine", CraftingBookCategory.MISC, pattern, output)));
		}
	}

	private static void addPotionMagazines(List<RecipeHolder<CraftingRecipe>> recipes) {
		for (PotionEssence essence : PotionMap.uniquePotions) {
			PotionContents potionContents = PotionHelper.changePotionEffectsDuration(essence.getPotionContents(), 0.2F);

			NonNullList<Ingredient> inputs = NonNullList.create();
			addShots(inputs, potionContents);
			inputs.add(Ingredient.of(new ItemStack(EMPTY_MAGAZINE.get())));
			addShots(inputs, potionContents);

			ItemStack output = new ItemStack(NEUTRAL_MAGAZINE.get());
			PotionHelper.addPotionContentsToStack(output, potionContents);

			ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, inputs, Optional.empty());

			recipes.add(new RecipeHolder<>(RegistryHelper.getRegistryName(output.getItem()), new ShapedRecipe("reliquary.potion.magazine", CraftingBookCategory.MISC, pattern, output)));
		}
	}

	private static void addShots(List<Ingredient> inputs, PotionContents potionContents) {
		addShots(inputs, potionContents, NEUTRAL_BULLET.get());
	}

	private static void addShots(List<Ingredient> inputs, BulletItem shotType) {
		addShots(inputs, PotionContents.EMPTY, shotType);
	}

	private static void addShots(List<Ingredient> inputs, PotionContents potionContents, BulletItem shotType) {
		ItemStack shot = new ItemStack(shotType);
		if (potionContents.hasEffects()) {
			PotionHelper.addPotionContentsToStack(shot, potionContents);
		}
		for (int i = 0; i < 4; i++) {
			inputs.add(Ingredient.of(shot));
		}
	}
}
