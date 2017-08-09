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

		registry.addRecipeCategories(new AlkahestryCraftingRecipeCategory(guiHelper));
		registry.addRecipeCategories(new AlkahestryChargingRecipeCategory(guiHelper));

		registry.addRecipeCategories(new MortarRecipeCategory(guiHelper));
		registry.addRecipeCategories(new CauldronRecipeCategory(guiHelper));
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.addRecipes(AlkahestryCraftingRecipeMaker.getRecipes(registry.getJeiHelpers().getStackHelper()), JEICategory.ALKAHESTRY_CRAFTING.getUid());
		registry.addRecipes(AlkahestryChargingRecipeMaker.getRecipes(registry.getJeiHelpers().getStackHelper()), JEICategory.ALKAHESTRY_CHARGING.getUid());

		registry.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), JEICategory.ALKAHESTRY_CHARGING.getUid(), JEICategory.ALKAHESTRY_CRAFTING.getUid());

		registry.addRecipeCatalyst(new ItemStack(ModBlocks.apothecaryMortar), JEICategory.MORTAR.getUid());
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.apothecaryCauldron), JEICategory.CAULDRON.getUid());

		registry.addRecipes(MortarRecipeMaker.getRecipes(), JEICategory.MORTAR.getUid());
		registry.addRecipes(CauldronRecipeMaker.getRecipes(), JEICategory.CAULDRON.getUid());
		registry.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.bullet, 1, 1)), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.tippedArrow), new ItemStack(Items.ARROW), 0.125F), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipes(MagazineRecipeMaker.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);

		for(DescriptionEntry entry : JEIDescriptionRegistry.entrySet())
			registry.addIngredientInfo(entry.itemStacks(), ItemStack.class, entry.langKey());
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {

	}
}
