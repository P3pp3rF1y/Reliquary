package reliquary.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
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
import reliquary.Reliquary;
import reliquary.compat.jei.alkahestry.AlkahestryChargingRecipeCategory;
import reliquary.compat.jei.alkahestry.AlkahestryCraftingRecipeCategory;
import reliquary.compat.jei.cauldron.CauldronRecipeCategory;
import reliquary.compat.jei.cauldron.CauldronRecipeMaker;
import reliquary.compat.jei.infernaltear.InfernalTearRecipeCategory;
import reliquary.compat.jei.infernaltear.InfernalTearRecipeMaker;
import reliquary.compat.jei.lingering.ArrowShotRecipeMaker;
import reliquary.compat.jei.magazines.MagazineRecipeMaker;
import reliquary.compat.jei.mortar.MortarRecipeCategory;
import reliquary.compat.jei.mortar.MortarRecipeMaker;
import reliquary.crafting.AlkahestryRecipeRegistry;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

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
		registration.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), AlkahestryChargingRecipeCategory.TYPE, AlkahestryCraftingRecipeCategory.TYPE);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()), MortarRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()), CauldronRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.INFERNAL_TEAR.get()), InfernalTearRecipeCategory.TYPE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableAlkahestry.get())) {
			registration.addRecipes(AlkahestryCraftingRecipeCategory.TYPE, AlkahestryRecipeRegistry.getCraftingRecipes());
			registration.addRecipes(AlkahestryChargingRecipeCategory.TYPE, AlkahestryRecipeRegistry.getChargingRecipes());
		}
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			registration.addRecipes(MortarRecipeCategory.TYPE, MortarRecipeMaker.getRecipes());
			registration.addRecipes(CauldronRecipeCategory.TYPE, CauldronRecipeMaker.getRecipes());
			registration.addRecipes(RecipeTypes.CRAFTING, ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.TIPPED_ARROW.get()), new ItemStack(Items.ARROW), 0.125F, "arrow"));
			registration.addRecipes(RecipeTypes.CRAFTING, ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.NEUTRAL_BULLET.get()), "bullet"));
		}
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableHandgun.get())) {
			registration.addRecipes(RecipeTypes.CRAFTING, MagazineRecipeMaker.getRecipes());
		}
		registration.addRecipes(RecipeTypes.CRAFTING, MobCharmRecipeMaker.getRecipes());
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableSpawnEggRecipes.get())) {
			registration.addRecipes(RecipeTypes.CRAFTING, SpawnEggRecipeMaker.getRecipes());
		}
		registration.addRecipes(InfernalTearRecipeCategory.TYPE, InfernalTearRecipeMaker.getRecipes());
		registerMobCharmBeltRecipe(registration);

		ItemDescriptionBuilder.addIngredientInfo(registration);
	}

	private void registerMobCharmBeltRecipe(IRecipeRegistration registration) {
		NonNullList<ItemStack> fragments = NonNullList.create();
		ModItems.MOB_CHARM_FRAGMENT.get().fillItemCategory(Reliquary.ITEM_GROUP, fragments);
		ItemStack[] fragmentStacks = fragments.toArray(new ItemStack[0]);

		registration.addRecipes(RecipeTypes.CRAFTING, Collections.singletonList(new ShapedRecipe(new ResourceLocation(Reference.MOD_ID, "items/mob_charm_belt"), "", 3, 3,
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
				), new ItemStack(ModItems.MOB_CHARM_BELT.get()))));
	}

	private void registerNbtSubtypeInterpreter(ISubtypeRegistration registration, Item item, String... keys) {
		registration.registerSubtypeInterpreter(item, new SortedNbtSubtypeInterpreter(keys));
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Reference.MOD_ID, "default");
	}
}
