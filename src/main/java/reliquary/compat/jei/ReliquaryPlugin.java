package reliquary.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
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
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.reference.Config;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // plugin class is used by JEI's reflection
@JeiPlugin
public class ReliquaryPlugin implements IModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registerNbtSubtypeInterpreter(registration, ModItems.MOB_CHARM_FRAGMENT.get(), ModDataComponents.ENTITY_NAME.get());
		registerNbtSubtypeInterpreter(registration, ModItems.MOB_CHARM.get(), ModDataComponents.ENTITY_NAME.get());
		if (!Config.COMMON.disable.disablePotions.get()) {
			registerNbtSubtypeInterpreter(registration, ModItems.POTION_ESSENCE.get(), DataComponents.POTION_CONTENTS);
			registerNbtSubtypeInterpreter(registration, ModItems.POTION.get(), DataComponents.POTION_CONTENTS);
			registerNbtSubtypeInterpreter(registration, ModItems.SPLASH_POTION.get(), DataComponents.POTION_CONTENTS);
			registerNbtSubtypeInterpreter(registration, ModItems.LINGERING_POTION.get(), DataComponents.POTION_CONTENTS);
			registerNbtSubtypeInterpreter(registration, ModItems.TIPPED_ARROW.get(), DataComponents.POTION_CONTENTS);
			registerNbtSubtypeInterpreter(registration, ModItems.NEUTRAL_BULLET.get(), DataComponents.POTION_CONTENTS);
			registerNbtSubtypeInterpreter(registration, ModItems.NEUTRAL_MAGAZINE.get(), DataComponents.POTION_CONTENTS);
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		if (Boolean.FALSE.equals(Config.COMMON.disable.disableAlkahestry.get())) {
			registration.addRecipeCategories(new AlkahestryCraftingRecipeCategory(guiHelper));
			registration.addRecipeCategories(new AlkahestryChargingRecipeCategory(guiHelper));
		}

		if (Boolean.FALSE.equals(Config.COMMON.disable.disablePotions.get())) {
			registration.addRecipeCategories(new MortarRecipeCategory(guiHelper));
			registration.addRecipeCategories(new CauldronRecipeCategory(guiHelper));
		}
		registration.addRecipeCategories(new InfernalTearRecipeCategory(guiHelper));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(AlkahestryChargingRecipeCategory.TYPE, new ItemStack(Blocks.CRAFTING_TABLE));
		registration.addCraftingStation(AlkahestryCraftingRecipeCategory.TYPE, new ItemStack(Blocks.CRAFTING_TABLE));
		registration.addCraftingStation(MortarRecipeCategory.TYPE, new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()));
		registration.addCraftingStation(CauldronRecipeCategory.TYPE, new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()));
		registration.addCraftingStation(InfernalTearRecipeCategory.TYPE, new ItemStack(ModItems.INFERNAL_TEAR.get()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		if (Boolean.FALSE.equals(Config.COMMON.disable.disableAlkahestry.get())) {
			registration.addRecipes(AlkahestryCraftingRecipeCategory.TYPE, AlkahestryRecipeRegistry.getCraftingRecipes());
			registration.addRecipes(AlkahestryChargingRecipeCategory.TYPE, AlkahestryRecipeRegistry.getChargingRecipes());
		}
		if (Boolean.FALSE.equals(Config.COMMON.disable.disablePotions.get())) {
			registration.addRecipes(MortarRecipeCategory.TYPE, MortarRecipeMaker.getRecipes());
			registration.addRecipes(CauldronRecipeCategory.TYPE, CauldronRecipeMaker.getRecipes());
			registration.addRecipes(RecipeTypes.CRAFTING,
					ArrowShotRecipeMaker.getRecipes(jeiHelpers, ModItems.TIPPED_ARROW.get(), Items.ARROW, 0.125F, "arrow"));
			registration.addRecipes(RecipeTypes.CRAFTING, ArrowShotRecipeMaker.getRecipes(jeiHelpers, ModItems.NEUTRAL_BULLET.get(), "bullet"));
		}
		if (Boolean.FALSE.equals(Config.COMMON.disable.disableHandgun.get())) {
			registration.addRecipes(RecipeTypes.CRAFTING, MagazineRecipeMaker.getRecipes(jeiHelpers));
		}
		if (Boolean.FALSE.equals(Config.COMMON.disable.disableCharms.get())) {
			registration.addRecipes(RecipeTypes.CRAFTING, MobCharmRecipeMaker.getRecipes());
			registerMobCharmBeltRecipe(registration);
		}
		if (Boolean.FALSE.equals(Config.COMMON.disable.disableSpawnEggRecipes.get())) {
			registration.addRecipes(RecipeTypes.CRAFTING, SpawnEggRecipeMaker.getRecipes());
		}
		registration.addRecipes(InfernalTearRecipeCategory.TYPE, InfernalTearRecipeMaker.getRecipes());

		ItemDescriptionBuilder.addIngredientInfo(registration);
	}

	private void registerMobCharmBeltRecipe(IRecipeRegistration registration) {
		List<SlotDisplay> fragments = NonNullList.create();
		ModItems.MOB_CHARM_FRAGMENT.get()
				.addCreativeTabItems(stack -> fragments.add(new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack))));
		SlotDisplay.Composite fragmentSlotDisplay = new SlotDisplay.Composite(fragments);;
		IVanillaRecipeFactory vanillaRecipeFactory = registration.getJeiHelpers().getVanillaRecipeFactory();
		CraftingRecipe recipe = vanillaRecipeFactory
				.createShapedRecipeBuilder(CraftingBookCategory.MISC,
						new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(new ItemStack(ModItems.MOB_CHARM_BELT.get()))))
				.group("reliquary.mob_charm").define('l', Ingredient.of(RegistryUtil.getRegistry(Registries.ITEM).getOrThrow(Tags.Items.LEATHERS)))
				.define('f', Ingredient.of(ModItems.MOB_CHARM_FRAGMENT.get()), fragmentSlotDisplay).pattern("lll").pattern("f f").pattern("fff").build();

		registration.addRecipes(RecipeTypes.CRAFTING,
				Collections.singletonList(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, Reliquary.getIdentifier("items/mob_charm_belt")), recipe)));
	}

	private void registerNbtSubtypeInterpreter(ISubtypeRegistration registration, Item item, DataComponentType<?> component) {
		registration.registerSubtypeInterpreter(item, new ComponentSubtypeInterpreter(component));
	}

	@Override
	public Identifier getPluginUid() {
		return Reliquary.getIdentifier("default");
	}
}
