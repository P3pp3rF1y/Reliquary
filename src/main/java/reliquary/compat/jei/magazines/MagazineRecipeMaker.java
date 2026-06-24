package reliquary.compat.jei.magazines;

import com.google.common.collect.ImmutableMap;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
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

import static reliquary.init.ModItems.*;

public class MagazineRecipeMaker {

	private MagazineRecipeMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes(IJeiHelpers jeiHelpers) {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();

		addRegularMagazines(jeiHelpers, recipes);
		if (Boolean.FALSE.equals(Config.COMMON.disable.disablePotions.get())) {
			addPotionMagazines(jeiHelpers, recipes);
		}

		return recipes;
	}

	private static void addRegularMagazines(IJeiHelpers jeiHelpers, List<RecipeHolder<CraftingRecipe>> recipes) {
		Map<BulletItem, MagazineItem> bulletMagazines = new ImmutableMap.Builder<BulletItem, MagazineItem>().put(NEUTRAL_BULLET.get(), NEUTRAL_MAGAZINE.get())
				.put(EXORCISM_BULLET.get(), EXORCISM_MAGAZINE.get()).put(BLAZE_BULLET.get(), BLAZE_MAGAZINE.get()).put(ENDER_BULLET.get(), ENDER_MAGAZINE.get())
				.put(CONCUSSIVE_BULLET.get(), CONCUSSIVE_MAGAZINE.get()).put(BUSTER_BULLET.get(), BUSTER_MAGAZINE.get())
				.put(SEEKER_BULLET.get(), SEEKER_MAGAZINE.get()).put(SAND_BULLET.get(), SAND_MAGAZINE.get()).put(STORM_BULLET.get(), STORM_MAGAZINE.get())
				.build();

		for (Map.Entry<BulletItem, MagazineItem> bulletMagazine : bulletMagazines.entrySet()) {
			ItemStack output = new ItemStack(bulletMagazine.getValue());

			IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
			CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, new SlotDisplay.ItemStackSlotDisplay(output))
					.group("reliquary.magazine").define('b', Ingredient.of(bulletMagazine.getKey())).define('m', Ingredient.of(EMPTY_MAGAZINE.get()))
					.pattern("bbb").pattern("bmb").pattern("bbb").build();

			recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, RegistryHelper.getRegistryName(output.getItem())), recipe));
		}
	}

	private static void addPotionMagazines(IJeiHelpers jeiHelpers, List<RecipeHolder<CraftingRecipe>> recipes) {
		for (PotionEssence essence : PotionMap.uniquePotions) {
			PotionContents potionContents = PotionHelper.changePotionEffectsDuration(essence.getPotionContents(), 0.2F);

			ItemStack output = new ItemStack(NEUTRAL_MAGAZINE.get());
			PotionHelper.addPotionContentsToStack(output, potionContents);

			ItemStack potionBullet = new ItemStack(NEUTRAL_BULLET.get());
			if (potionContents.hasEffects()) {
				PotionHelper.addPotionContentsToStack(potionBullet, potionContents);
			}
			IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
			CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, new SlotDisplay.ItemStackSlotDisplay(output))
					.group("reliquary.potion.magazine").define('b', Ingredient.of(potionBullet.getItem()), new SlotDisplay.ItemStackSlotDisplay(potionBullet))
					.define('m', Ingredient.of(EMPTY_MAGAZINE.get())).pattern("bbb").pattern("bmb").pattern("bbb").build();

			recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, RegistryHelper.getRegistryName(output.getItem())), recipe));
		}
	}
}
