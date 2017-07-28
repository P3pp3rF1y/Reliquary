package xreliquary.init;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xreliquary.crafting.AlkahestryChargingRecipe;
import xreliquary.crafting.AlkahestryCraftingRecipe;
import xreliquary.crafting.AlkahestryDrainRecipe;
import xreliquary.crafting.MagazineRecipe;
import xreliquary.crafting.MobCharmRepairRecipe;
import xreliquary.crafting.PotionBulletsRecipe;
import xreliquary.crafting.XRTippedArrowsRecipe;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class XRRecipes {

	private static ItemStack ingredient(int m) {
		return new ItemStack(ModItems.mobIngredient, 1, m);
	}

	public static ItemStack NEBULOUS_HEART;
	public static ItemStack CREEPER_GLAND;
	public static ItemStack SLIME_PEARL;
	public static ItemStack BAT_WING;
	public static ItemStack RIB_BONE;
	public static ItemStack WITHER_RIB;
	public static ItemStack STORM_EYE;
	public static ItemStack FERTILE_ESSENCE;
	public static ItemStack FROZEN_CORE;
	public static ItemStack MOLTEN_CORE;
	public static ItemStack ZOMBIE_HEART;
	public static ItemStack INFERNAL_CLAW;
	public static ItemStack SHELL_FRAGMENT;
	public static ItemStack SQUID_BEAK;
	public static ItemStack CHELICERAE;
	public static ItemStack GUARDIAN_SPIKE;
	private static ItemStack WITHER_SKULL;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<IRecipe> event) {
		initConstants();

		registerCustomRecipes();

		addRecipe(new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage(ItemStack.EMPTY)), MOLTEN_CORE, new ItemStack(ModItems.witchHat), STORM_EYE, CREEPER_GLAND, new ItemStack(Items.BOOK), SLIME_PEARL, CHELICERAE, WITHER_SKULL, NEBULOUS_HEART);
	}

	private static void registerCustomRecipes() {
		ForgeRegistries.RECIPES.register(new AlkahestryDrainRecipe());
		ForgeRegistries.RECIPES.register(new AlkahestryChargingRecipe());
		ForgeRegistries.RECIPES.register(new AlkahestryCraftingRecipe());
		ForgeRegistries.RECIPES.register(new MobCharmRepairRecipe());
		ForgeRegistries.RECIPES.register(new XRTippedArrowsRecipe());
		ForgeRegistries.RECIPES.register(new PotionBulletsRecipe());
		ForgeRegistries.RECIPES.register(new MagazineRecipe());
	}

	private static void initConstants() {
		NEBULOUS_HEART = ingredient(Reference.ENDER_INGREDIENT_META);
		CREEPER_GLAND = ingredient(Reference.CREEPER_INGREDIENT_META);
		SLIME_PEARL = ingredient(Reference.SLIME_INGREDIENT_META);
		BAT_WING = ingredient(Reference.BAT_INGREDIENT_META);
		RIB_BONE = ingredient(Reference.SKELETON_INGREDIENT_META);
		WITHER_RIB = ingredient(Reference.WITHER_INGREDIENT_META);
		STORM_EYE = ingredient(Reference.STORM_INGREDIENT_META);
		FERTILE_ESSENCE = ingredient(Reference.FERTILE_INGREDIENT_META);
		FROZEN_CORE = ingredient(Reference.FROZEN_INGREDIENT_META);
		MOLTEN_CORE = ingredient(Reference.MOLTEN_INGREDIENT_META);
		ZOMBIE_HEART = ingredient(Reference.ZOMBIE_INGREDIENT_META);
		INFERNAL_CLAW = ingredient(Reference.CLAW_INGREDIENT_META);
		SHELL_FRAGMENT = ingredient(Reference.SHELL_INGREDIENT_META);
		SQUID_BEAK = ingredient(Reference.SQUID_INGREDIENT_META);
		CHELICERAE = ingredient(Reference.SPIDER_INGREDIENT_META);
		GUARDIAN_SPIKE = ingredient(Reference.GUARDIAN_INGREDIENT_META);
		WITHER_SKULL = new ItemStack(Items.SKULL, 1, 1);
	}

	private static void addRecipe(ItemStack result, ItemStack... stacks) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for(ItemStack stack : stacks) {
			ingredients.add(Ingredient.fromStacks(stack));
		}

		//noinspection ConstantConditions
		ForgeRegistries.RECIPES.register(new ShapelessRecipes(Reference.MOD_NAME, result, ingredients).setRegistryName(result.getItem().getRegistryName()));
	}
}
