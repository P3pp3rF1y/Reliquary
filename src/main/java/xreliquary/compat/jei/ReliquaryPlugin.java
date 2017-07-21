package xreliquary.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import xreliquary.compat.jei.alkahestry.AlkahestryChargingRecipeCategory;
import xreliquary.compat.jei.alkahestry.AlkahestryChargingRecipeMaker;
import xreliquary.compat.jei.alkahestry.AlkahestryCraftingRecipeCategory;
import xreliquary.compat.jei.alkahestry.AlkahestryCraftingRecipeMaker;
import xreliquary.compat.jei.cauldron.CauldronRecipeCategory;
import xreliquary.compat.jei.cauldron.CauldronRecipeMaker;
import xreliquary.compat.jei.descriptions.DescriptionEntry;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.compat.jei.lingering.ArrowShotRecipeMaker;
import xreliquary.compat.jei.magazines.MagazineRecipeMaker;
import xreliquary.compat.jei.mortar.MortarRecipeCategory;
import xreliquary.compat.jei.mortar.MortarRecipeMaker;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;

@JEIPlugin
public class ReliquaryPlugin implements IModPlugin {

	@Override
	public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(ModItems.mobCharm, ModItems.potionEssence, ModItems.potion, ModItems.bullet, ModItems.tippedArrow, ModItems.magazine);
	}

	@Override
	public void registerIngredients(@Nonnull IModIngredientRegistration registry) {
	}

	@Override
	public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		//noinspection ConstantConditions
		boolean tomeEnabled = ModItems.alkahestryTome.getRegistryName() != null && !Settings.disabledItemsBlocks.contains(ModItems.alkahestryTome.getRegistryName().getResourcePath());

		if(tomeEnabled) {
			registry.addRecipeCategories(new AlkahestryCraftingRecipeCategory(guiHelper));
			registry.addRecipeCategories(new AlkahestryChargingRecipeCategory(guiHelper));
		}

		registry.addRecipeCategories(new MortarRecipeCategory(guiHelper));
		registry.addRecipeCategories(new CauldronRecipeCategory(guiHelper));
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		//noinspection ConstantConditions
		boolean tomeEnabled = ModItems.alkahestryTome.getRegistryName() != null && !Settings.disabledItemsBlocks.contains(ModItems.alkahestryTome.getRegistryName().getResourcePath());

		if(tomeEnabled) {
			registry.addRecipes(AlkahestryCraftingRecipeMaker.getRecipes(), Reference.JEI_CATEGORY_ALKAHESTRY_CRAFTING);
			registry.addRecipes(AlkahestryChargingRecipeMaker.getRecipes(), Reference.JEI_CATEGORY_ALKAHESTRY_CHARGING);

			registry.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), Reference.JEI_CATEGORY_ALKAHESTRY_CHARGING, Reference.JEI_CATEGORY_ALKAHESTRY_CRAFTING);
		}

		registry.addRecipeCatalyst(new ItemStack(ModBlocks.apothecaryMortar), Reference.JEI_CATEGORY_MORTAR);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.apothecaryCauldron), Reference.JEI_CATEGORY_CAULDRON);

		registry.addRecipes(MortarRecipeMaker.getRecipes(), Reference.JEI_CATEGORY_MORTAR);
		registry.addRecipes(CauldronRecipeMaker.getRecipes(), Reference.JEI_CATEGORY_CAULDRON);
		registry.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.bullet, 1, 1)), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.tippedArrow), new ItemStack(Items.ARROW), 0.125F), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipes(MagazineRecipeMaker.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);

		for(DescriptionEntry entry : JEIDescriptionRegistry.entrySet())
			registry.addIngredientInfo(entry.itemStacks(), ItemStack.class, entry.langKey());

		//blacklisted items
		registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModItems.filledVoidTear));
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {

	}
}
