package xreliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.crafting.MobCharmRecipeBuilder;
import xreliquary.crafting.NbtShapedRecipeBuilder;
import xreliquary.crafting.PotionEffectsRecipeBuilder;
import xreliquary.crafting.SpawnEggRecipeBuilder;
import xreliquary.crafting.alkahestry.ChargingRecipeBuilder;
import xreliquary.crafting.alkahestry.CraftingRecipeBuilder;
import xreliquary.crafting.alkahestry.DrainRecipeBuilder;
import xreliquary.crafting.conditions.AlkahestryEnabledCondition;
import xreliquary.crafting.conditions.HandgunEnabledCondition;
import xreliquary.crafting.conditions.MobDropsCraftableCondition;
import xreliquary.crafting.conditions.PassivePedestalEnabledCondition;
import xreliquary.crafting.conditions.PedestalEnabledCondition;
import xreliquary.crafting.conditions.PotionsEnabledCondition;
import xreliquary.crafting.conditions.SpawnEggEnabledCondition;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.BulletItem;
import xreliquary.items.ItemBase;
import xreliquary.items.MagazineItem;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;
import xreliquary.util.RegistryHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
	private static final Tags.IOptionalNamedTag<Item> INGOTS_COPPER = ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS, new ResourceLocation("forge:ingots/copper"));
	private static final Tags.IOptionalNamedTag<Item> INGOTS_STEEL = ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS, new ResourceLocation("forge:ingots/steel"));
	private static final Tags.IOptionalNamedTag<Item> INGOTS_SILVER = ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS, new ResourceLocation("forge:ingots/silver"));
	private static final Tags.IOptionalNamedTag<Item> INGOTS_TIN = ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS, new ResourceLocation("forge:ingots/tin"));
	private static final String HAS_GUNPOWDER_CRITERION = "has_gunpowder";
	private static final String HAS_NEBULOUS_HEART_CRITERION = "has_nebulous_heart";
	private static final String HAS_FERTILE_ESSENCE_CRITERION = "has_fertile_essence";
	private static final String HAS_MOLTEN_CORE_CRITERION = "has_molten_core";
	private static final String HAS_CATALYZING_GLAND_CRITERIION = "has_catalyzing_gland";
	private static final String MOB_CHARM_FRAGMENTS_FOLDER = "mob_charm_fragments/";
	private static final String UNCRAFTING_FOLDER = "uncrafting/";
	private static final String HAS_CHELICERAE_CRITERION = "has_chelicerae";
	private static final String HAS_SLIME_PEARL_CRITERION = "has_slime_pearl";
	private static final String HAS_WITCH_HAT_CRITERION = "has_witch_hat";
	private static final String HAS_ZOMBIE_HEART_CRITERION = "has_zombie_heart";
	private static final String HAS_GUARDIAN_SPIKE_CRITERION = "has_guardian_spike";
	private static final String HAS_VOID_TEAR_CRITERION = "has_void_tear";
	private static final String HAS_FROZEN_CORE_CRITERION = "has_frozen_core";
	private static final String HAS_WITHERED_RIB_CRITERION = "has_withered_rib";
	private static final String HAS_MOB_CHARM_FRAGMENT_CRITERION = "has_mob_charm_fragment";
	private static final String HAS_INFERNAL_CLAW_CRITERION = "has_infernal_claw";

	public ModRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		registerHandgunRecipes(consumer);
		registerAlkahestryRecipes(consumer);
		registerPotionRecipes(consumer);
		registerPedestalRecipes(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.ALKAHESTRY_ALTAR_ITEM.get())
				.addIngredient(Tags.Items.OBSIDIAN)
				.addIngredient(Items.REDSTONE_LAMP)
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.FERTILE_LILY_PAD_ITEM.get())
				.addIngredient(ModItems.FERTILE_ESSENCE.get())
				.addIngredient(ModItems.FERTILE_ESSENCE.get())
				.addIngredient(ModItems.FERTILE_ESSENCE.get())
				.addIngredient(Items.LILY_PAD)
				.addCriterion(HAS_FERTILE_ESSENCE_CRITERION, hasItem(ModItems.FERTILE_ESSENCE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.INTERDICTION_TORCH_ITEM.get())
				.addIngredient(ModItems.BAT_WING.get())
				.addIngredient(Tags.Items.RODS_BLAZE)
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.WRAITH_NODE_ITEM.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(Tags.Items.GEMS_EMERALD)
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		registerCraftableMobDropRecipes(consumer);
		registerCharmFragmentRecipes(consumer);
		registerIngredientRecipes(consumer);
		registerUncraftingRecipes(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.ANGELHEART_VIAL.get())
				.patternLine("GBG")
				.patternLine("GCG")
				.patternLine("FGF")
				.key('G', Tags.Items.GLASS_PANES)
				.key('B', Items.MILK_BUCKET)
				.key('C', ModItems.INFERNAL_CLAW.get())
				.key('F', ModItems.FERTILE_ESSENCE.get())
				.addCriterion(HAS_FERTILE_ESSENCE_CRITERION, hasItem(ModItems.FERTILE_ESSENCE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.ANGELIC_FEATHER.get())
				.addIngredient(Tags.Items.FEATHERS)
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(ModItems.BAT_WING.get())
				.addIngredient(ModItems.FERTILE_ESSENCE.get())
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.DESTRUCTION_CATALYST.get())
				.addIngredient(Items.FLINT_AND_STEEL)
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addIngredient(ModItems.INFERNAL_TEAR.get())
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapelessRecipeBuilder.shapelessRecipe(ModItems.EMPEROR_CHALICE.get())
						.addIngredient(Tags.Items.GEMS_EMERALD)
						.addIngredient(Tags.Items.INGOTS_GOLD)
						.addIngredient(Items.BUCKET)
						.addIngredient(voidTear)
						.addCriterion(HAS_VOID_TEAR_CRITERION, hasItem(ModItems.VOID_TEAR.get()))
						.build(consumer));

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapedRecipeBuilder.shapedRecipe(ModItems.ENDER_STAFF.get())
						.patternLine(" BE")
						.patternLine("NVB")
						.patternLine("SN ")
						.key('B', ModItems.BAT_WING.get())
						.key('S', Items.STICK)
						.key('E', Items.ENDER_EYE)
						.key('V', voidTear)
						.key('N', ModItems.NEBULOUS_HEART.get())
						.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
						.build(consumer));

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FORTUNE_COIN.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(Tags.Items.NUGGETS_GOLD)
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addIngredient(ModItems.BAT_WING.get())
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapelessRecipeBuilder.shapelessRecipe(ModItems.GLACIAL_STAFF.get())
						.addIngredient(ModItems.ICE_MAGUS_ROD.get())
						.addIngredient(voidTear)
						.addIngredient(ModItems.FROZEN_CORE.get())
						.addIngredient(ModItems.SHEARS_OF_WINTER.get())
						.addCriterion(HAS_VOID_TEAR_CRITERION, hasItem(ModItems.VOID_TEAR.get()))
						.build(consumer));

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.GLOWING_BREAD.get(), 3)
				.addIngredient(Items.BREAD)
				.addIngredient(Items.BREAD)
				.addIngredient(Items.BREAD)
				.addIngredient(ModItems.GLOWING_WATER.get())
				.addCriterion("has_glowing_water", hasItem(ModItems.GLOWING_WATER.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.GLOWING_WATER.get())
				.patternLine("GBG")
				.patternLine("GDG")
				.patternLine("NGP")
				.key('G', Tags.Items.GLASS_PANES)
				.key('B', Items.WATER_BUCKET)
				.key('D', Tags.Items.DUSTS_GLOWSTONE)
				.key('P', Tags.Items.GUNPOWDER)
				.key('N', Tags.Items.CROPS_NETHER_WART)
				.addCriterion("has_nether_wart", hasItem(Tags.Items.CROPS_NETHER_WART))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(Items.GOLD_NUGGET)
				.addIngredient(ModItems.EMPTY_BULLET.get())
				.addIngredient(ModItems.EMPTY_BULLET.get())
				.addIngredient(ModItems.EMPTY_BULLET.get())
				.addIngredient(ModItems.EMPTY_BULLET.get())
				.addCriterion("has_empty_bullet", hasItem(ModItems.EMPTY_BULLET.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, "gold_nugget"));

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapedRecipeBuilder.shapedRecipe(ModItems.HARVEST_ROD.get())
						.patternLine(" RF")
						.patternLine("VTR")
						.patternLine("SV ")
						.key('R', Items.ROSE_BUSH)
						.key('F', ModItems.FERTILE_ESSENCE.get())
						.key('V', Items.VINE)
						.key('T', voidTear)
						.key('S', Items.STICK)
						.addCriterion(HAS_VOID_TEAR_CRITERION, hasItem(ModItems.VOID_TEAR.get()))
						.build(consumer)
		);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.HERO_MEDALLION.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(ModItems.FORTUNE_COIN.get())
				.addIngredient(ModItems.WITCH_HAT.get())
				.addIngredient(ModItems.INFERNAL_TEAR.get())
				.addCriterion("has_infernal_tear", hasItem(ModItems.INFERNAL_TEAR.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.HOLY_HAND_GRENADE.get(), 4)
				.addIngredient(ModItems.GLOWING_WATER.get())
				.addIngredient(Tags.Items.NUGGETS_GOLD)
				.addIngredient(Items.TNT)
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addCriterion("has_glowing_water", hasItem(ModItems.GLOWING_WATER.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapedRecipeBuilder.shapedRecipe(ModItems.ICE_MAGUS_ROD.get())
						.patternLine(" DF")
						.patternLine(" VD")
						.patternLine("I  ")
						.key('D', Tags.Items.GEMS_DIAMOND)
						.key('F', ModItems.FROZEN_CORE.get())
						.key('V', voidTear)
						.key('I', Tags.Items.INGOTS_IRON)
						.addCriterion(HAS_FROZEN_CORE_CRITERION, hasItem(ModItems.FROZEN_CORE.get()))
						.build(consumer)
		);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.INFERNAL_CHALICE.get())
				.addIngredient(ModItems.INFERNAL_CLAWS.get())
				.addIngredient(ModItems.EMPEROR_CHALICE.get())
				.addIngredient(ModItems.INFERNAL_TEAR.get())
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addCriterion("has_emperor_chalice", hasItem(ModItems.EMPEROR_CHALICE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.INFERNAL_CLAWS.get())
				.addIngredient(ModItems.INFERNAL_CLAW.get())
				.addIngredient(ModItems.INFERNAL_CLAW.get())
				.addIngredient(ModItems.INFERNAL_CLAW.get())
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addCriterion(HAS_INFERNAL_CLAW_CRITERION, hasItem(ModItems.INFERNAL_CLAW.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapelessRecipeBuilder.shapelessRecipe(ModItems.INFERNAL_TEAR.get())
						.addIngredient(voidTear)
						.addIngredient(ModItems.WITCH_HAT.get())
						.addIngredient(ModItems.MOLTEN_CORE.get())
						.addIngredient(ModItems.INFERNAL_CLAW.get())
						.addCriterion(HAS_INFERNAL_CLAW_CRITERION, hasItem(ModItems.INFERNAL_CLAW.get()))
						.build(consumer)
		);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.KRAKEN_SHELL.get())
				.addIngredient(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.addIngredient(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.addIngredient(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addCriterion("has_kraken_shell_fragment", hasItem(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.LANTERN_OF_PARANOIA.get())
				.patternLine("ISI")
				.patternLine("GMG")
				.patternLine(" I ")
				.key('S', ModItems.SLIME_PEARL.get())
				.key('G', Tags.Items.GLASS)
				.key('I', Tags.Items.INGOTS_IRON)
				.key('M', ModItems.MOLTEN_CORE.get())
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.MAGICBANE.get())
				.patternLine("NG")
				.patternLine("IN")
				.key('G', Tags.Items.INGOTS_GOLD)
				.key('I', Tags.Items.INGOTS_IRON)
				.key('N', ModItems.NEBULOUS_HEART.get())
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.MERCY_CROSS.get())
				.patternLine("WGR")
				.patternLine("GLG")
				.patternLine("SGZ")
				.key('W', ModItems.WITHERED_RIB.get())
				.key('G', Tags.Items.INGOTS_GOLD)
				.key('R', ModItems.RIB_BONE.get())
				.key('L', Tags.Items.LEATHER)
				.key('S', Items.WITHER_SKELETON_SKULL)
				.key('Z', ModItems.ZOMBIE_HEART.get())
				.addCriterion(HAS_WITHERED_RIB_CRITERION, hasItem(ModItems.WITHERED_RIB.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapelessRecipeBuilder.shapelessRecipe(ModItems.MIDAS_TOUCHSTONE.get())
						.addIngredient(Items.ANVIL)
						.addIngredient(Tags.Items.STORAGE_BLOCKS_GOLD)
						.addIngredient(Tags.Items.STORAGE_BLOCKS_GOLD)
						.addIngredient(ModItems.MOLTEN_CORE.get())
						.addIngredient(ModItems.MOLTEN_CORE.get())
						.addIngredient(ModItems.MOLTEN_CORE.get())
						.addIngredient(ModItems.CATALYZING_GLAND.get())
						.addIngredient(ModItems.CATALYZING_GLAND.get())
						.addIngredient(voidTear)
						.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
						.build(consumer));

		MobCharmRecipeBuilder.charmRecipe()
				.patternLine("FLF")
				.patternLine("FSF")
				.patternLine("F F")
				.key('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.key('L', Tags.Items.LEATHER)
				.key('S', Tags.Items.STRING)
				.addCriterion(HAS_MOB_CHARM_FRAGMENT_CRITERION, hasItem(ModItems.MOB_CHARM_FRAGMENT.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_BELT.get())
				.patternLine("LLL")
				.patternLine("F F")
				.patternLine("FFF")
				.key('L', Tags.Items.LEATHER)
				.key('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.addCriterion(HAS_MOB_CHARM_FRAGMENT_CRITERION, hasItem(ModItems.MOB_CHARM_FRAGMENT.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.PHOENIX_DOWN.get())
				.addIngredient(ModItems.ANGELHEART_VIAL.get())
				.addIngredient(ModItems.ANGELHEART_VIAL.get())
				.addIngredient(ModItems.ANGELHEART_VIAL.get())
				.addIngredient(ModItems.ANGELIC_FEATHER.get())
				.addCriterion("has_angelic_feather", hasItem(ModItems.ANGELIC_FEATHER.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.PYROMANCER_STAFF.get())
				.addIngredient(ModItems.INFERNAL_CLAWS.get())
				.addIngredient(Tags.Items.RODS_BLAZE)
				.addIngredient(ModItems.INFERNAL_TEAR.get())
				.addIngredient(ModItems.SALAMANDER_EYE.get())
				.addCriterion("has_infernal_claws", hasItem(ModItems.INFERNAL_CLAWS.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapedRecipeBuilder.shapedRecipe(ModItems.RENDING_GALE.get())
						.patternLine(" BE")
						.patternLine("GVB")
						.patternLine("SG ")
						.key('B', ModItems.BAT_WING.get())
						.key('S', Items.STICK)
						.key('E', ModItems.EYE_OF_THE_STORM.get())
						.key('V', voidTear)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_eye_of_the_storm", hasItem(ModItems.EYE_OF_THE_STORM.get()))
						.build(consumer));

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.ROD_OF_LYSSA.get())
				.addIngredient(ModItems.INFERNAL_CLAW.get())
				.addIngredient(ModItems.BAT_WING.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(Items.FISHING_ROD)
				.addCriterion(HAS_INFERNAL_CLAW_CRITERION, hasItem(ModItems.INFERNAL_CLAW.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.SALAMANDER_EYE.get())
				.addIngredient(Items.ENDER_EYE)
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addIngredient(ModItems.FROZEN_CORE.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.SERPENT_STAFF.get())
				.patternLine(" CE")
				.patternLine(" KC")
				.patternLine("S  ")
				.key('S', Items.STICK)
				.key('C', ModItems.CHELICERAE.get())
				.key('E', Items.ENDER_EYE)
				.key('K', ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.addCriterion("has_kraken_shell_fragment", hasItem(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.SHEARS_OF_WINTER.get())
				.addIngredient(ModItems.FROZEN_CORE.get())
				.addIngredient(Items.SHEARS)
				.addIngredient(Tags.Items.GEMS_DIAMOND)
				.addIngredient(Tags.Items.GEMS_DIAMOND)
				.addCriterion(HAS_FROZEN_CORE_CRITERION, hasItem(ModItems.FROZEN_CORE.get()))
				.build(consumer);

		instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())).ifPresent(voidTear ->
				ShapelessRecipeBuilder.shapelessRecipe(ModItems.SOJOURNER_STAFF.get())
						.addIngredient(ModItems.MOLTEN_CORE.get())
						.addIngredient(Tags.Items.INGOTS_GOLD)
						.addIngredient(Tags.Items.RODS_BLAZE)
						.addIngredient(voidTear)
						.addCriterion(HAS_VOID_TEAR_CRITERION, hasItem(ModItems.VOID_TEAR.get()))
						.build(consumer));

		ShapedRecipeBuilder.shapedRecipe(ModItems.TWILIGHT_CLOAK.get())
				.patternLine("ICI")
				.patternLine("BCB")
				.patternLine("BCB")
				.key('I', Tags.Items.INGOTS_IRON)
				.key('B', Items.BLACK_WOOL)
				.key('C', ModItems.CRIMSON_CLOTH.get())
				.addCriterion("has_crimson_cloth", hasItem(ModItems.CRIMSON_CLOTH.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.VOID_TEAR.get())
				.addIngredient(Items.GHAST_TEAR)
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addIngredient(Tags.Items.GEMS_LAPIS)
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.WITHERLESS_ROSE.get())
				.patternLine("FNF")
				.patternLine("NRN")
				.patternLine("FNF")
				.key('F', ModItems.FERTILE_ESSENCE.get())
				.key('N', Tags.Items.NETHER_STARS)
				.key('R', Items.ROSE_BUSH)
				.addCriterion(HAS_FERTILE_ESSENCE_CRITERION, hasItem(ModItems.FERTILE_ESSENCE.get()))
				.build(consumer);
	}

	private void registerUncraftingRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(Items.BLAZE_ROD, 4)
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "blaze_rod"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.BONE, 5)
				.addIngredient(ModItems.RIB_BONE.get())
				.addCriterion("has_rib_bone", hasItem(ModItems.RIB_BONE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "bone"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.ENDER_PEARL, 3)
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "ender_pearl"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.GHAST_TEAR)
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, hasItem(ModItems.CATALYZING_GLAND.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "ghast_tear"));

		ShapedRecipeBuilder.shapedRecipe(Items.GLASS_BOTTLE, 6)
				.patternLine("W W")
				.patternLine(" W ")
				.key('W', ModItems.WITCH_HAT.get())
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "glass_bottle"));

		ShapedRecipeBuilder.shapedRecipe(Items.GLOWSTONE_DUST, 6)
				.patternLine("W  ")
				.patternLine("W  ")
				.patternLine(" W ")
				.key('W', ModItems.WITCH_HAT.get())
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "glowstone_dust"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.GOLD_NUGGET, 6)
				.addIngredient(ModItems.ZOMBIE_HEART.get())
				.addIngredient(ModItems.ZOMBIE_HEART.get())
				.addCriterion(HAS_ZOMBIE_HEART_CRITERION, hasItem(ModItems.ZOMBIE_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gold_nugget"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.GUNPOWDER, 6)
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, hasItem(ModItems.CATALYZING_GLAND.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gunpowder_creeper_gland"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.GUNPOWDER, 10)
				.addIngredient(ModItems.EYE_OF_THE_STORM.get())
				.addCriterion("has_eye_of_the_storm", hasItem(ModItems.EYE_OF_THE_STORM.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gunpowder_storm_eye"));

		ShapedRecipeBuilder.shapedRecipe(Items.GUNPOWDER, 6)
				.patternLine("W  ")
				.patternLine(" W ")
				.patternLine("  W")
				.key('W', ModItems.WITCH_HAT.get())
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gunpowder_witch_hat"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.INK_SAC, 6)
				.addIngredient(ModItems.SQUID_BEAK.get())
				.addCriterion("has_squid_beak", hasItem(ModItems.SQUID_BEAK.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "ink_sac"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.MAGMA_CREAM, 3)
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "magma_cream"));

		ShapedRecipeBuilder.shapedRecipe(Items.PACKED_ICE)
				.patternLine("III")
				.patternLine("ICI")
				.patternLine("III")
				.key('I', Items.ICE)
				.key('C', ModItems.FROZEN_CORE.get())
				.addCriterion(HAS_FROZEN_CORE_CRITERION, hasItem(ModItems.FROZEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "packed_ice"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.PRISMARINE_CRYSTALS, 10)
				.addIngredient(ModItems.GUARDIAN_SPIKE.get())
				.addIngredient(ModItems.GUARDIAN_SPIKE.get())
				.addCriterion(HAS_GUARDIAN_SPIKE_CRITERION, hasItem(ModItems.GUARDIAN_SPIKE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "prismarine_crystals"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.PRISMARINE_SHARD, 5)
				.addIngredient(ModItems.GUARDIAN_SPIKE.get())
				.addCriterion(HAS_GUARDIAN_SPIKE_CRITERION, hasItem(ModItems.GUARDIAN_SPIKE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "prismarine_shard"));

		ShapedRecipeBuilder.shapedRecipe(Items.REDSTONE, 6)
				.patternLine("W")
				.patternLine("W")
				.patternLine("W")
				.key('W', ModItems.WITCH_HAT.get())
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "redstone"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.ROTTEN_FLESH, 6)
				.addIngredient(ModItems.ZOMBIE_HEART.get())
				.addCriterion(HAS_ZOMBIE_HEART_CRITERION, hasItem(ModItems.ZOMBIE_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "rotten_flesh"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.SLIME_BALL, 6)
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addCriterion(HAS_SLIME_PEARL_CRITERION, hasItem(ModItems.SLIME_PEARL.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "slime_ball"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.SNOWBALL, 5)
				.addIngredient(ModItems.FROZEN_CORE.get())
				.addCriterion(HAS_FROZEN_CORE_CRITERION, hasItem(ModItems.FROZEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "snowball"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.SPIDER_EYE, 2)
				.addIngredient(ModItems.CHELICERAE.get())
				.addIngredient(ModItems.CHELICERAE.get())
				.addCriterion(HAS_CHELICERAE_CRITERION, hasItem(ModItems.CHELICERAE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "spider_eye"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.STICK, 4)
				.addIngredient(ModItems.WITCH_HAT.get())
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "stick"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.STRING, 6)
				.addIngredient(ModItems.CHELICERAE.get())
				.addCriterion(HAS_CHELICERAE_CRITERION, hasItem(ModItems.CHELICERAE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "string"));

		ShapedRecipeBuilder.shapedRecipe(Items.SUGAR, 6)
				.patternLine("WWW")
				.key('W', ModItems.WITCH_HAT.get())
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "sugar"));

		ShapelessRecipeBuilder.shapelessRecipe(Items.WITHER_SKELETON_SKULL)
				.addIngredient(ModItems.WITHERED_RIB.get())
				.addIngredient(ModItems.WITHERED_RIB.get())
				.addIngredient(ModItems.WITHERED_RIB.get())
				.addIngredient(Items.SKELETON_SKULL)
				.addCriterion(HAS_WITHERED_RIB_CRITERION, hasItem(ModItems.WITHERED_RIB.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "wither_skeleton_skull"));

		ResourceLocation spawnEggId = new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "spawn_egg");
		ConditionalRecipe.builder()
				.addCondition(new SpawnEggEnabledCondition())
				.addRecipe(conditionalConsumer ->
						SpawnEggRecipeBuilder.spawnEggRecipe()
								.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
								.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
								.addIngredient(Items.EGG)
								.addCriterion(HAS_MOB_CHARM_FRAGMENT_CRITERION, hasItem(ModItems.MOB_CHARM_FRAGMENT.get()))
								.build(conditionalConsumer, spawnEggId))
				.build(consumer, spawnEggId);
	}

	private void registerHandgunRecipes(Consumer<IFinishedRecipe> consumer) {
		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.HANDGUN.get())
								.patternLine("BIM")
								.patternLine("ISI")
								.patternLine("IGI")
								.key('I', Tags.Items.INGOTS_IRON)
								.key('B', ModItems.BARREL_ASSEMBLY.get())
								.key('M', ModItems.HAMMER_ASSEMBLY.get())
								.key('G', ModItems.GRIP_ASSEMBLY.get())
								.key('S', ModItems.SLIME_PEARL.get())
								.addCriterion("has_barrel_assembly", hasItem(ModItems.BARREL_ASSEMBLY.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.HANDGUN.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.BARREL_ASSEMBLY.get())
								.patternLine("III")
								.patternLine("EME")
								.patternLine("III")
								.key('I', Tags.Items.INGOTS_IRON)
								.key('E', ModItems.NEBULOUS_HEART.get())
								.key('M', Items.MAGMA_CREAM)
								.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.BARREL_ASSEMBLY.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.GRIP_ASSEMBLY.get())
								.patternLine("III")
								.patternLine("IMI")
								.patternLine("ICI")
								.key('I', Tags.Items.INGOTS_IRON)
								.key('C', ModItems.EMPTY_MAGAZINE.get())
								.key('M', Items.MAGMA_CREAM)
								.addCriterion("has_magma_cream", hasItem(Items.MAGMA_CREAM))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.GRIP_ASSEMBLY.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.EMPTY_MAGAZINE.get())
								.patternLine("I I")
								.patternLine("IGI")
								.patternLine("SIS")
								.key('S', Tags.Items.STONE)
								.key('I', Tags.Items.INGOTS_IRON)
								.key('G', Tags.Items.GLASS)
								.addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.EMPTY_MAGAZINE.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.HAMMER_ASSEMBLY.get())
								.patternLine("IIB")
								.patternLine("RMI")
								.patternLine("III")
								.key('I', Tags.Items.INGOTS_IRON)
								.key('B', Items.STONE_BUTTON)
								.key('R', Tags.Items.RODS_BLAZE)
								.key('M', ModItems.MOLTEN_CORE.get())
								.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.HAMMER_ASSEMBLY.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.BLAZE_BULLET.get(), 8)
								.addIngredient(Items.BLAZE_POWDER)
								.addIngredient(Tags.Items.RODS_BLAZE)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addCriterion("has_blaze_rod", hasItem(Items.BLAZE_ROD))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.BLAZE_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.BLAZE_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.BUSTER_BULLET.get(), 8)
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CONCUSSIVE_BULLET.get())
								.addIngredient(ModItems.CATALYZING_GLAND.get())
								.addCriterion("has_concussive_bullet", hasItem(ModItems.CONCUSSIVE_BULLET.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.BUSTER_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.BUSTER_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.CONCUSSIVE_BULLET.get(), 8)
								.addIngredient(Tags.Items.SLIMEBALLS)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.GUNPOWDER)
								.addCriterion(HAS_GUNPOWDER_CRITERION, hasItem(Items.GUNPOWDER))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.CONCUSSIVE_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.CONCUSSIVE_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.ENDER_BULLET.get(), 8)
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.SEEKER_BULLET.get())
								.addIngredient(ModItems.NEBULOUS_HEART.get())
								.addCriterion("has_seeker_bullet", hasItem(ModItems.SEEKER_BULLET.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.ENDER_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.ENDER_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.EXORCISM_BULLET.get(), 8)
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.NEUTRAL_BULLET.get())
								.addIngredient(ModItems.ZOMBIE_HEART.get())
								.addCriterion("has_neutral_bullet", hasItem(ModItems.NEUTRAL_BULLET.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.EXORCISM_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.EXORCISM_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.NEUTRAL_BULLET.get(), 8)
								.addIngredient(Items.FLINT)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.GUNPOWDER)
								.addCriterion(HAS_GUNPOWDER_CRITERION, hasItem(Items.GUNPOWDER))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.NEUTRAL_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.NEUTRAL_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.SAND_BULLET.get(), 8)
								.addIngredient(Tags.Items.SANDSTONE)
								.addIngredient(Tags.Items.SLIMEBALLS)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.GUNPOWDER)
								.addCriterion(HAS_GUNPOWDER_CRITERION, hasItem(Items.GUNPOWDER))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.SAND_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.SAND_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.SEEKER_BULLET.get(), 8)
								.addIngredient(Tags.Items.GEMS_LAPIS)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.GUNPOWDER)
								.addCriterion(HAS_GUNPOWDER_CRITERION, hasItem(Items.GUNPOWDER))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.SEEKER_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.SEEKER_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.STORM_BULLET.get(), 8)
								.addIngredient(ModItems.CATALYZING_GLAND.get())
								.addIngredient(ModItems.CATALYZING_GLAND.get())
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.NUGGETS_GOLD)
								.addIngredient(Tags.Items.GUNPOWDER)
								.addCriterion(HAS_GUNPOWDER_CRITERION, hasItem(Items.GUNPOWDER))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.STORM_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.STORM_BULLET.get());

		addMagazineRecipe(consumer, ModItems.BLAZE_BULLET.get(), ModItems.BLAZE_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.BUSTER_BULLET.get(), ModItems.BUSTER_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.CONCUSSIVE_BULLET.get(), ModItems.CONCUSSIVE_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.ENDER_BULLET.get(), ModItems.ENDER_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.EXORCISM_BULLET.get(), ModItems.EXORCISM_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.NEUTRAL_BULLET.get(), ModItems.NEUTRAL_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.SAND_BULLET.get(), ModItems.SAND_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.SEEKER_BULLET.get(), ModItems.SEEKER_MAGAZINE.get());
		addMagazineRecipe(consumer, ModItems.STORM_BULLET.get(), ModItems.STORM_MAGAZINE.get());
	}

	private void registerAlkahestryRecipes(Consumer<IFinishedRecipe> consumer) {
		ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapelessRecipe(ModItems.ALKAHESTRY_TOME.get())
								.addIngredient(ModItems.MOLTEN_CORE.get())
								.addIngredient(ModItems.WITCH_HAT.get())
								.addIngredient(ModItems.EYE_OF_THE_STORM.get())
								.addIngredient(ModItems.CATALYZING_GLAND.get())
								.addIngredient(Items.BOOK)
								.addIngredient(ModItems.SLIME_PEARL.get())
								.addIngredient(ModItems.CHELICERAE.get())
								.addIngredient(Items.WITHER_SKELETON_SKULL)
								.addIngredient(ModItems.NEBULOUS_HEART.get())
								.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.ALKAHESTRY_TOME.get()));

		ChargingRecipeBuilder.chargingRecipe(Items.GLOWSTONE_DUST, 1).build(consumer, RegistryHelper.getRegistryName(Items.GLOWSTONE_DUST));
		ChargingRecipeBuilder.chargingRecipe(Items.GLOWSTONE, 4).build(consumer, RegistryHelper.getRegistryName(Items.GLOWSTONE));
		ChargingRecipeBuilder.chargingRecipe(Items.REDSTONE, 1).build(consumer, RegistryHelper.getRegistryName(Items.REDSTONE));
		ChargingRecipeBuilder.chargingRecipe(Items.REDSTONE_BLOCK, 9).build(consumer, RegistryHelper.getRegistryName(Items.REDSTONE_BLOCK));

		DrainRecipeBuilder.drainRecipe(Items.REDSTONE, 1).build(consumer, RegistryHelper.getRegistryName(Items.REDSTONE));

		CraftingRecipeBuilder.craftingRecipe(Items.CHARCOAL, 4, 5).build(consumer, RegistryHelper.getRegistryName(Items.CHARCOAL));
		CraftingRecipeBuilder.craftingRecipe(Items.CLAY, 4, 3).build(consumer, RegistryHelper.getRegistryName(Items.CLAY));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_COPPER, 8, 5)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_COPPER.getName())))
				.build(consumer, new ResourceLocation("copper_ingot"));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GEMS_DIAMOND, 64, 2).build(consumer, RegistryHelper.getRegistryName(Items.DIAMOND));
		CraftingRecipeBuilder.craftingRecipe(Items.DIRT, 4, 33).build(consumer, RegistryHelper.getRegistryName(Items.DIRT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GEMS_EMERALD, 32, 2).build(consumer, RegistryHelper.getRegistryName(Items.EMERALD));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.END_STONES, 8, 17).build(consumer, RegistryHelper.getRegistryName(Items.END_STONE));
		CraftingRecipeBuilder.craftingRecipe(Items.FLINT, 8, 9).build(consumer, RegistryHelper.getRegistryName(Items.FLINT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.INGOTS_GOLD, 32, 2).build(consumer, RegistryHelper.getRegistryName(Items.GOLD_INGOT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GRAVEL, 4, 17).build(consumer, RegistryHelper.getRegistryName(Items.GRAVEL));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GUNPOWDER, 8, 3).build(consumer, RegistryHelper.getRegistryName(Items.GUNPOWDER));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.INGOTS_IRON, 32, 2).build(consumer, RegistryHelper.getRegistryName(Items.IRON_INGOT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GEMS_LAPIS, 4, 2).build(consumer, RegistryHelper.getRegistryName(Items.LAPIS_LAZULI));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.NETHER_STARS, 256, 2).build(consumer, RegistryHelper.getRegistryName(Items.NETHER_STAR));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.NETHERRACK, 4, 9).build(consumer, RegistryHelper.getRegistryName(Items.NETHERRACK));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.OBSIDIAN, 8, 5).build(consumer, RegistryHelper.getRegistryName(Items.OBSIDIAN));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.SAND, 4, 33).build(consumer, RegistryHelper.getRegistryName(Items.SAND));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.SANDSTONE, 4, 9).build(consumer, RegistryHelper.getRegistryName(Items.SANDSTONE));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_SILVER, 32, 2)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_SILVER.getName())))
				.build(consumer, new ResourceLocation("silver_ingot"));
		CraftingRecipeBuilder.craftingRecipe(Items.SOUL_SAND, 8, 9).build(consumer, RegistryHelper.getRegistryName(Items.SOUL_SAND));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_STEEL, 32, 2)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_STEEL.getName())))
				.build(consumer, new ResourceLocation("steel_ingot"));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_TIN, 32, 2)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_TIN.getName())))
				.build(consumer, new ResourceLocation("tin_ingot"));
	}

	private void registerPotionRecipes(Consumer<IFinishedRecipe> consumer) {
		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.EMPTY_POTION_VIAL.get())
								.patternLine("G G")
								.patternLine("G G")
								.patternLine(" G ")
								.key('G', Tags.Items.GLASS_PANES)
								.addCriterion("has_glass_pane", hasItem(Items.GLASS_PANE))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.EMPTY_POTION_VIAL.get()));

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.ATTRACTION_POTION.get())
								.patternLine("GBG")
								.patternLine("GFG")
								.patternLine("RGC")
								.key('G', Tags.Items.GLASS_PANES)
								.key('B', Items.WATER_BUCKET)
								.key('F', ModItems.FERTILE_ESSENCE.get())
								.key('R', Tags.Items.DYES_RED)
								.key('C', Items.COCOA_BEANS)
								.addCriterion(HAS_FERTILE_ESSENCE_CRITERION, hasItem(ModItems.FERTILE_ESSENCE.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.ATTRACTION_POTION.get()));

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModItems.FERTILE_POTION.get())
								.patternLine("GBG")
								.patternLine("GFG")
								.patternLine("CGY")
								.key('G', Tags.Items.GLASS_PANES)
								.key('B', Items.WATER_BUCKET)
								.key('F', ModItems.FERTILE_ESSENCE.get())
								.key('C', Tags.Items.DYES_GREEN)
								.key('Y', Tags.Items.DYES_YELLOW)
								.addCriterion(HAS_FERTILE_ESSENCE_CRITERION, hasItem(ModItems.FERTILE_ESSENCE.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.FERTILE_POTION.get()));

		addItemPotionRecipe(consumer, Items.ARROW, ModItems.TIPPED_ARROW.get(), 0.125f, 'A', false);

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModBlocks.APOTHECARY_CAULDRON_ITEM.get())
								.patternLine("GNG")
								.patternLine("ICI")
								.patternLine("NMN")
								.key('G', ModItems.CATALYZING_GLAND.get())
								.key('N', ModItems.NEBULOUS_HEART.get())
								.key('I', ModItems.INFERNAL_CLAW.get())
								.key('C', Items.CAULDRON)
								.key('M', ModItems.MOLTEN_CORE.get())
								.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModBlocks.APOTHECARY_CAULDRON_ITEM.get()));

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(ModBlocks.APOTHECARY_MORTAR_ITEM.get())
								.patternLine("GNG")
								.patternLine("NGN")
								.patternLine("NNN")
								.key('G', ModItems.CATALYZING_GLAND.get())
								.key('N', Tags.Items.STORAGE_BLOCKS_QUARTZ)
								.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, hasItem(ModItems.CATALYZING_GLAND.get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModBlocks.APOTHECARY_MORTAR_ITEM.get()));
	}

	private void registerPedestalRecipes(Consumer<IFinishedRecipe> consumer) {
		addPassivePedestalRecipe(consumer, Items.WHITE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.WHITE).get());
		addPassivePedestalRecipe(consumer, Items.ORANGE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.ORANGE).get());
		addPassivePedestalRecipe(consumer, Items.MAGENTA_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.MAGENTA).get());
		addPassivePedestalRecipe(consumer, Items.LIGHT_BLUE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.LIGHT_BLUE).get());
		addPassivePedestalRecipe(consumer, Items.YELLOW_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.YELLOW).get());
		addPassivePedestalRecipe(consumer, Items.LIME_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.LIME).get());
		addPassivePedestalRecipe(consumer, Items.PINK_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.PINK).get());
		addPassivePedestalRecipe(consumer, Items.GRAY_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.GRAY).get());
		addPassivePedestalRecipe(consumer, Items.LIGHT_GRAY_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.LIGHT_GRAY).get());
		addPassivePedestalRecipe(consumer, Items.CYAN_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.CYAN).get());
		addPassivePedestalRecipe(consumer, Items.PURPLE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.PURPLE).get());
		addPassivePedestalRecipe(consumer, Items.BLUE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.BLUE).get());
		addPassivePedestalRecipe(consumer, Items.BROWN_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.BROWN).get());
		addPassivePedestalRecipe(consumer, Items.GREEN_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.GREEN).get());
		addPassivePedestalRecipe(consumer, Items.RED_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.RED).get());
		addPassivePedestalRecipe(consumer, Items.BLACK_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.BLACK).get());

		for (DyeColor color : DyeColor.values()) {
			addPedestalRecipe(consumer, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(color).get(), ModBlocks.PEDESTAL_ITEMS.get(color).get());
		}
	}

	private void registerCraftableMobDropRecipes(Consumer<IFinishedRecipe> consumer) {
		addCraftableMobDropRecipe(consumer, ModItems.BAT_WING.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GFG")
						.patternLine("GGG")
						.key('F', Tags.Items.FEATHERS)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_feather", hasItem(Tags.Items.FEATHERS))
		);

		addCraftableMobDropRecipe(consumer, ModItems.CATALYZING_GLAND.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GPG")
						.patternLine("GGG")
						.key('P', Tags.Items.GUNPOWDER)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion(HAS_GUNPOWDER_CRITERION, hasItem(Tags.Items.GUNPOWDER))
		);

		addCraftableMobDropRecipe(consumer, ModItems.CHELICERAE.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GSG")
						.patternLine("GGG")
						.key('S', Tags.Items.STRING)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_string", hasItem(Tags.Items.STRING))
		);

		addCraftableMobDropRecipe(consumer, ModItems.FROZEN_CORE.get(), builder ->
				builder
						.patternLine("GPG")
						.patternLine("GSG")
						.patternLine("GSG")
						.key('P', Items.PUMPKIN)
						.key('S', Items.SNOW)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_snow", hasItem(Items.SNOW))
		);

		addCraftableMobDropRecipe(consumer, ModItems.GUARDIAN_SPIKE.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GPG")
						.patternLine("GGG")
						.key('P', Tags.Items.DUSTS_PRISMARINE)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_prismarine", hasItem(Tags.Items.DUSTS_PRISMARINE))
		);

		addCraftableMobDropRecipe(consumer, ModItems.MOLTEN_CORE.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GMG")
						.patternLine("GGG")
						.key('M', Items.MAGMA_CREAM)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_magma_cream", hasItem(Items.MAGMA_CREAM))
		);

		addCraftableMobDropRecipe(consumer, ModItems.NEBULOUS_HEART.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GEG")
						.patternLine("GGG")
						.key('E', Tags.Items.ENDER_PEARLS)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_ender_pearl", hasItem(Tags.Items.ENDER_PEARLS))
		);

		addCraftableMobDropRecipe(consumer, ModItems.RIB_BONE.get(), builder ->
				builder
						.patternLine("III")
						.patternLine("IBI")
						.patternLine("III")
						.key('B', Tags.Items.BONES)
						.key('I', Tags.Items.INGOTS_IRON)
						.addCriterion("has_bone", hasItem(Tags.Items.BONES))
		);

		addCraftableMobDropRecipe(consumer, ModItems.SLIME_PEARL.get(), builder ->
				builder
						.patternLine("III")
						.patternLine("ISI")
						.patternLine("III")
						.key('S', Tags.Items.SLIMEBALLS)
						.key('I', Tags.Items.INGOTS_IRON)
						.addCriterion("has_slimeball", hasItem(Tags.Items.SLIMEBALLS))
		);

		addCraftableMobDropRecipe(consumer, ModItems.SQUID_BEAK.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GIG")
						.patternLine("GGG")
						.key('I', Items.INK_SAC)
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion("has_ink_sac", hasItem(Items.INK_SAC))
		);

		addCraftableMobDropRecipe(consumer, ModItems.EYE_OF_THE_STORM.get(), builder ->
				builder
						.patternLine("GGG")
						.patternLine("GCG")
						.patternLine("GGG")
						.key('C', ModItems.CATALYZING_GLAND.get())
						.key('G', Tags.Items.INGOTS_GOLD)
						.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, hasItem(ModItems.CATALYZING_GLAND.get()))
		);

		addCraftableMobDropRecipe(consumer, ModItems.WITHERED_RIB.get(), builder ->
				builder
						.patternLine("D D")
						.patternLine(" S ")
						.patternLine("D D")
						.key('S', Items.SKELETON_SKULL)
						.key('D', Tags.Items.GEMS_DIAMOND)
						.addCriterion("has_skeleton_skull", hasItem(Items.SKELETON_SKULL))
		);

		addCraftableMobDropRecipe(consumer, ModItems.ZOMBIE_HEART.get(), builder ->
				builder
						.patternLine("III")
						.patternLine("IFI")
						.patternLine("III")
						.key('F', Items.ROTTEN_FLESH)
						.key('I', Tags.Items.INGOTS_IRON)
						.addCriterion("has_rotten_flesh", hasItem(Items.ROTTEN_FLESH))
		);
	}

	private void registerIngredientRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.CRIMSON_CLOTH.get())
				.addIngredient(Items.RED_WOOL)
				.addIngredient(Items.BLACK_WOOL)
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addIngredient(ModItems.NEBULOUS_HEART.get())
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FERTILE_ESSENCE.get())
				.addIngredient(ModItems.RIB_BONE.get())
				.addIngredient(ModItems.CATALYZING_GLAND.get())
				.addIngredient(Tags.Items.DYES_GREEN)
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addCriterion(HAS_SLIME_PEARL_CRITERION, hasItem(ModItems.SLIME_PEARL.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.INFERNAL_CLAW.get())
				.addIngredient(Tags.Items.LEATHER)
				.addIngredient(ModItems.MOLTEN_CORE.get())
				.addIngredient(ModItems.RIB_BONE.get())
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addCriterion(HAS_SLIME_PEARL_CRITERION, hasItem(ModItems.SLIME_PEARL.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.addIngredient(ModItems.SQUID_BEAK.get())
				.addIngredient(ModItems.SQUID_BEAK.get())
				.addIngredient(ModItems.SQUID_BEAK.get())
				.addIngredient(ModItems.SLIME_PEARL.get())
				.addCriterion("has_squid_beak", hasItem(ModItems.SQUID_BEAK.get()))
				.build(consumer);
	}

	private void registerCharmFragmentRecipes(Consumer<IFinishedRecipe> consumer) {
		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:blaze"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.MOLTEN_CORE.get())
				.key('S', Tags.Items.RODS_BLAZE)
				.key('T', Items.BLAZE_POWDER)
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "blaze"));

		instantiateNBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.POISON)).ifPresent(poisonPotion ->
				NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:cave_spider"))
						.patternLine("PPP")
						.patternLine("STS")
						.patternLine("PPP")
						.key('P', ModItems.CHELICERAE.get())
						.key('S', Tags.Items.STRING)
						.key('T', poisonPotion)
						.addCriterion(HAS_CHELICERAE_CRITERION, hasItem(ModItems.CHELICERAE.get()))
						.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "cave_spider")));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:creeper"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.CATALYZING_GLAND.get())
				.key('S', Tags.Items.GUNPOWDER)
				.key('T', Items.BONE)
				.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, hasItem(ModItems.CATALYZING_GLAND.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "creeper"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:enderman"))
				.patternLine("PPP")
				.patternLine("SPS")
				.patternLine("PPP")
				.key('P', ModItems.NEBULOUS_HEART.get())
				.key('S', Tags.Items.ENDER_PEARLS)
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, hasItem(ModItems.NEBULOUS_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "enderman"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:ghast"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', Items.GHAST_TEAR)
				.key('S', Tags.Items.GUNPOWDER)
				.key('T', ModItems.CATALYZING_GLAND.get())
				.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, hasItem(ModItems.CATALYZING_GLAND.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "ghast"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:guardian"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.GUARDIAN_SPIKE.get())
				.key('S', Tags.Items.DUSTS_PRISMARINE)
				.key('T', Items.COD)
				.addCriterion(HAS_GUARDIAN_SPIKE_CRITERION, hasItem(ModItems.GUARDIAN_SPIKE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "guardian"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:magma_cube"))
				.patternLine("PPP")
				.patternLine("SSS")
				.patternLine("PPP")
				.key('P', ModItems.MOLTEN_CORE.get())
				.key('S', Items.MAGMA_CREAM)
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, hasItem(ModItems.MOLTEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "magma_cube"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:skeleton"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.RIB_BONE.get())
				.key('S', Items.BONE)
				.key('T', Items.FLINT)
				.addCriterion("has_rib_bone", hasItem(ModItems.RIB_BONE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "skeleton"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:slime"))
				.patternLine("PPP")
				.patternLine("SSS")
				.patternLine("PPP")
				.key('P', ModItems.SLIME_PEARL.get())
				.key('S', Tags.Items.SLIMEBALLS)
				.addCriterion(HAS_SLIME_PEARL_CRITERION, hasItem(ModItems.SLIME_PEARL.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "slime"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:spider"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.CHELICERAE.get())
				.key('S', Tags.Items.STRING)
				.key('T', Items.SPIDER_EYE)
				.addCriterion(HAS_CHELICERAE_CRITERION, hasItem(ModItems.CHELICERAE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "spider"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:witch"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.WITCH_HAT.get())
				.key('S', Items.GLASS_BOTTLE)
				.key('T', Items.SPIDER_EYE)
				.addCriterion(HAS_WITCH_HAT_CRITERION, hasItem(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "witch"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:wither_skeleton"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.WITHERED_RIB.get())
				.key('S', Items.BONE)
				.key('T', Items.WITHER_SKELETON_SKULL)
				.addCriterion(HAS_WITHERED_RIB_CRITERION, hasItem(ModItems.WITHERED_RIB.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "wither_skeleton"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:zombie"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.ZOMBIE_HEART.get())
				.key('S', Items.ROTTEN_FLESH)
				.key('T', Items.BONE)
				.addCriterion(HAS_ZOMBIE_HEART_CRITERION, hasItem(ModItems.ZOMBIE_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "zombie"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:zombified_piglin"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.ZOMBIE_HEART.get())
				.key('S', Items.ROTTEN_FLESH)
				.key('T', Items.GOLDEN_SWORD)
				.addCriterion(HAS_ZOMBIE_HEART_CRITERION, hasItem(ModItems.ZOMBIE_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "zombified_piglin"));
	}

	private Optional<NBTIngredient> instantiateNBTIngredient(ItemStack stack) {
		try {
			return Optional.of(ObfuscationReflectionHelper.findConstructor(NBTIngredient.class, ItemStack.class).newInstance(stack));
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			LogHelper.error("Error instantiating NBTIngredient ", e);
		}
		return Optional.empty();
	}

	private void addCraftableMobDropRecipe(Consumer<IFinishedRecipe> consumer, ItemBase item, Consumer<ShapedRecipeBuilder> setRecipe) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(item);
		setRecipe.accept(builder);

		ConditionalRecipe.builder()
				.addCondition(new MobDropsCraftableCondition())
				.addRecipe(builder::build)
				.build(consumer, RegistryHelper.getRegistryName(item));
	}

	private void addPedestalRecipe(Consumer<IFinishedRecipe> consumer, BlockItem passivePedestalItem, BlockItem pedestalItem) {
		ConditionalRecipe.builder()
				.addCondition(new PedestalEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(pedestalItem)
								.patternLine("D D")
								.patternLine(" P ")
								.patternLine("D D")
								.key('D', Tags.Items.GEMS_DIAMOND)
								.key('P', passivePedestalItem)
								.addCriterion("has_passive_pedestal", hasItem(ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.WHITE).get()))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(pedestalItem));
	}

	private void addPassivePedestalRecipe(Consumer<IFinishedRecipe> consumer, Item carpetItem, BlockItem pedestalItem) {
		ConditionalRecipe.builder()
				.addCondition(new PassivePedestalEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shapedRecipe(pedestalItem)
								.patternLine(" C ")
								.patternLine("GQG")
								.patternLine("SSS")
								.key('C', carpetItem)
								.key('G', Tags.Items.NUGGETS_GOLD)
								.key('Q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
								.key('S', Items.QUARTZ_SLAB)
								.addCriterion("has_quartz_block", hasItem(Items.QUARTZ_BLOCK))
								.build(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(pedestalItem));
	}

	private void addBulletPotionRecipe(Consumer<IFinishedRecipe> consumer, Item item) {
		addItemPotionRecipe(consumer, item, item, (float) 0.2, 'B', true);
	}

	private void addItemPotionRecipe(Consumer<IFinishedRecipe> consumer, Item itemIngredient, Item item, float durationFactor, char itemKey, boolean includeSuffix) {
		ResourceLocation registryName = RegistryHelper.getRegistryName(item);
		String path = registryName.getPath();
		ResourceLocation id = includeSuffix ? new ResourceLocation(registryName.getNamespace(), registryName.getPath() + "_potion") : registryName;
		PotionEffectsRecipeBuilder.potionEffectsRecipe(item, 8, durationFactor)
				.addCondition(new HandgunEnabledCondition())
				.patternLine(String.valueOf(itemKey) + itemKey + itemKey)
				.patternLine(itemKey + "P" + itemKey)
				.patternLine(String.valueOf(itemKey) + itemKey + itemKey)
				.key(itemKey, itemIngredient)
				.key('P', ModItems.LINGERING_POTION.get())
				.addCriterion("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), hasItem(item))
				.build(consumer, id);
	}

	private void addMagazineRecipe(Consumer<IFinishedRecipe> consumer, BulletItem bulletItem, MagazineItem magazineItem) {
		String path = RegistryHelper.getRegistryName(bulletItem).getPath();
		PotionEffectsRecipeBuilder.potionEffectsRecipe(magazineItem, 1, 1)
				.addCondition(new HandgunEnabledCondition())
				.patternLine("BBB")
				.patternLine("BMB")
				.patternLine("BBB")
				.key('B', bulletItem)
				.key('M', ModItems.EMPTY_MAGAZINE.get())
				.addCriterion("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), hasItem(bulletItem))
				.build(consumer, RegistryHelper.getRegistryName(magazineItem));
	}
}
