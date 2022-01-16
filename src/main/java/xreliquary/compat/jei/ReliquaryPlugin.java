package xreliquary.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Blocks;
import xreliquary.Reliquary;
import xreliquary.compat.jei.alkahestry.AlkahestryChargingRecipeCategory;
import xreliquary.compat.jei.alkahestry.AlkahestryCraftingRecipeCategory;
import xreliquary.compat.jei.cauldron.CauldronRecipeCategory;
import xreliquary.compat.jei.cauldron.CauldronRecipeMaker;
import xreliquary.compat.jei.infernaltear.InfernalTearRecipeCategory;
import xreliquary.compat.jei.infernaltear.InfernalTearRecipeMaker;
import xreliquary.compat.jei.lingering.ArrowShotRecipeMaker;
import xreliquary.compat.jei.magazines.MagazineRecipeMaker;
import xreliquary.compat.jei.mortar.MortarRecipeCategory;
import xreliquary.compat.jei.mortar.MortarRecipeMaker;
import xreliquary.crafting.AlkahestryRecipeRegistry;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.Collections;

@SuppressWarnings("unused") //plugin class is used by JEI's reflection
@JeiPlugin
public class ReliquaryPlugin implements IModPlugin {
	private static final String EFFECTS_TAG = "effects";

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registerNbtSubtypeInterpreter(registration, ModItems.MOB_CHARM_FRAGMENT.get(), "entity");
		registerNbtSubtypeInterpreter(registration, ModItems.MOB_CHARM.get(), "entity");
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			registerNbtSubtypeInterpreter(registration, ModItems.POTION_ESSENCE.get(), EFFECTS_TAG);
			registerNbtSubtypeInterpreter(registration, ModItems.POTION.get(), EFFECTS_TAG);
			registerNbtSubtypeInterpreter(registration, ModItems.SPLASH_POTION.get(), EFFECTS_TAG);
			registerNbtSubtypeInterpreter(registration, ModItems.LINGERING_POTION.get(), EFFECTS_TAG);
			registerNbtSubtypeInterpreter(registration, ModItems.TIPPED_ARROW.get(), EFFECTS_TAG);
			registerNbtSubtypeInterpreter(registration, ModItems.NEUTRAL_BULLET.get(), EFFECTS_TAG);
			registerNbtSubtypeInterpreter(registration, ModItems.NEUTRAL_MAGAZINE.get(), EFFECTS_TAG);
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableAlkahestry.get())) {
			registration.addRecipeCategories(new AlkahestryCraftingRecipeCategory(guiHelper));
			registration.addRecipeCategories(new AlkahestryChargingRecipeCategory(guiHelper));
		}

		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			registration.addRecipeCategories(new MortarRecipeCategory(guiHelper));
			registration.addRecipeCategories(new CauldronRecipeCategory(guiHelper));
		}
		registration.addRecipeCategories(new InfernalTearRecipeCategory(guiHelper));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), AlkahestryChargingRecipeCategory.UID, AlkahestryCraftingRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()), MortarRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()), CauldronRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.INFERNAL_TEAR.get()), InfernalTearRecipeCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableAlkahestry.get())) {
			registration.addRecipes(AlkahestryRecipeRegistry.getCraftingRecipes(), AlkahestryCraftingRecipeCategory.UID);
			registration.addRecipes(AlkahestryRecipeRegistry.getChargingRecipes(), AlkahestryChargingRecipeCategory.UID);
		}
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			registration.addRecipes(MortarRecipeMaker.getRecipes(), MortarRecipeCategory.UID);
			registration.addRecipes(CauldronRecipeMaker.getRecipes(), CauldronRecipeCategory.UID);
			registration.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.TIPPED_ARROW.get()), new ItemStack(Items.ARROW), 0.125F, "arrow"), VanillaRecipeCategoryUid.CRAFTING);
			registration.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.NEUTRAL_BULLET.get()), "bullet"), VanillaRecipeCategoryUid.CRAFTING);
		}
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableHandgun.get())) {
			registration.addRecipes(MagazineRecipeMaker.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		}
		registration.addRecipes(MobCharmRecipeMaker.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableSpawnEggRecipes.get())) {
			registration.addRecipes(SpawnEggRecipeMaker.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		}
		registration.addRecipes(InfernalTearRecipeMaker.getRecipes(), InfernalTearRecipeCategory.UID);
		registerMobCharmBeltRecipe(registration);

		ItemDescriptionBuilder.addIngredientInfo(registration);
	}

	private void registerMobCharmBeltRecipe(IRecipeRegistration registration) {
		NonNullList<ItemStack> fragments = NonNullList.create();
		ModItems.MOB_CHARM_FRAGMENT.get().fillItemCategory(Reliquary.ITEM_GROUP, fragments);
		ItemStack[] fragmentStacks = fragments.toArray(new ItemStack[0]);

		registration.addRecipes(Collections.singleton(new ShapedRecipe(new ResourceLocation(Reference.MOD_ID, "items/mob_charm_belt"), "", 3, 3,
				NonNullList.of(Ingredient.EMPTY,
						Ingredient.of(() -> Items.LEATHER),
						Ingredient.of(() -> Items.LEATHER),
						Ingredient.of(() -> Items.LEATHER),
						Ingredient.of(fragmentStacks),
						Ingredient.EMPTY,
						Ingredient.of(fragmentStacks),
						Ingredient.of(fragmentStacks),
						Ingredient.of(fragmentStacks),
						Ingredient.of(fragmentStacks)
				), new ItemStack(ModItems.MOB_CHARM_BELT.get()))), VanillaRecipeCategoryUid.CRAFTING);
	}

	private void registerNbtSubtypeInterpreter(ISubtypeRegistration registration, Item item, String... keys) {
		registration.registerSubtypeInterpreter(item, new SortedNbtSubtypeInterpreter(keys));
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Reference.MOD_ID, "default");
	}
}
