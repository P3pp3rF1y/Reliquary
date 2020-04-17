package xreliquary.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import xreliquary.Reliquary;
import xreliquary.compat.jei.alkahestry.AlkahestryChargingRecipeCategory;
import xreliquary.compat.jei.alkahestry.AlkahestryCraftingRecipeCategory;
import xreliquary.compat.jei.cauldron.CauldronRecipeCategory;
import xreliquary.compat.jei.cauldron.CauldronRecipeMaker;
import xreliquary.compat.jei.descriptions.DescriptionEntry;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.compat.jei.lingering.ArrowShotRecipeMaker;
import xreliquary.compat.jei.magazines.MagazineRecipeMaker;
import xreliquary.compat.jei.mortar.MortarRecipeCategory;
import xreliquary.compat.jei.mortar.MortarRecipeMaker;
import xreliquary.crafting.AlkahestryRecipeRegistry;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") //plugin class is used by JEI's reflection
@JeiPlugin
public class ReliquaryPlugin implements IModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.useNbtForSubtypes(ModItems.MOB_CHARM_FRAGMENT, ModItems.MOB_CHARM, ModItems.POTION_ESSENCE, ModItems.POTION, ModItems.SPLASH_POTION,
				ModItems.LINGERING_POTION, ModItems.TIPPED_ARROW, ModItems.NEUTRAL_BULLET, ModItems.NEUTRAL_MAGAZINE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		registration.addRecipeCategories(new AlkahestryCraftingRecipeCategory(guiHelper));
		registration.addRecipeCategories(new AlkahestryChargingRecipeCategory(guiHelper));

		registration.addRecipeCategories(new MortarRecipeCategory(guiHelper));
		registration.addRecipeCategories(new CauldronRecipeCategory(guiHelper));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), AlkahestryChargingRecipeCategory.UID, AlkahestryCraftingRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.APOTHECARY_MORTAR), MortarRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.APOTHECARY_CAULDRON), CauldronRecipeCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(AlkahestryRecipeRegistry.getCraftingRecipes(), AlkahestryCraftingRecipeCategory.UID);
		registration.addRecipes(AlkahestryRecipeRegistry.getChargingRecipes(), AlkahestryChargingRecipeCategory.UID);

		registration.addRecipes(MortarRecipeMaker.getRecipes(), MortarRecipeCategory.UID);
		registration.addRecipes(CauldronRecipeMaker.getRecipes(), CauldronRecipeCategory.UID);
		registration.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.NEUTRAL_BULLET), "bullet"), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipes(ArrowShotRecipeMaker.getRecipes(new ItemStack(ModItems.TIPPED_ARROW), new ItemStack(Items.ARROW), 0.125F, "arrow"), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipes(MagazineRecipeMaker.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		registerMobCharmBeltRecipe(registration);

		for (DescriptionEntry entry : JEIDescriptionRegistry.getEntries()) {
			List<ItemStack> itemStacks = entry.getItemStacks();
			if (!itemStacks.isEmpty()) {
				registration.addIngredientInfo(itemStacks, VanillaTypes.ITEM, entry.langKeys());
			}
		}
	}

	private void registerMobCharmBeltRecipe(IRecipeRegistration registration) {
		NonNullList<ItemStack> fragments = NonNullList.create();
		ModItems.MOB_CHARM_FRAGMENT.fillItemGroup(Reliquary.ITEM_GROUP, fragments);
		ItemStack[] fragmentStacks = fragments.toArray(new ItemStack[0]);

		registration.addRecipes(Collections.singleton(new ShapedRecipe(new ResourceLocation(Reference.MOD_ID, "items/mob_charm_belt"), "", 3, 3,
				NonNullList.from(Ingredient.EMPTY,
						Ingredient.fromItems(() -> Items.LEATHER),
						Ingredient.fromItems(() -> Items.LEATHER),
						Ingredient.fromItems(() -> Items.LEATHER),
						Ingredient.fromStacks(fragmentStacks),
						Ingredient.EMPTY,
						Ingredient.fromStacks(fragmentStacks),
						Ingredient.fromStacks(fragmentStacks),
						Ingredient.fromStacks(fragmentStacks),
						Ingredient.fromStacks(fragmentStacks)
				), new ItemStack(ModItems.MOB_CHARM_BELT))), VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Reference.MOD_ID, "default");
	}
}
