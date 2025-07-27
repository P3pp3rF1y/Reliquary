package reliquary.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import reliquary.Reliquary;
import reliquary.crafting.MobCharmRecipeBuilder;
import reliquary.crafting.PotionEffectsRecipeBuilder;
import reliquary.crafting.SpawnEggRecipeBuilder;
import reliquary.crafting.alkahestry.ChargingRecipeBuilder;
import reliquary.crafting.alkahestry.CraftingRecipeBuilder;
import reliquary.crafting.alkahestry.DrainRecipeBuilder;
import reliquary.crafting.conditions.*;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.item.BulletItem;
import reliquary.item.MagazineItem;
import reliquary.util.RegistryHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
	private static final TagKey<Item> INGOTS_COPPER = TagKey.create(Registries.ITEM, ResourceLocation.parse("forge:ingots/copper"));
	private static final TagKey<Item> INGOTS_STEEL = TagKey.create(Registries.ITEM, ResourceLocation.parse("forge:ingots/steel"));
	private static final TagKey<Item> INGOTS_SILVER = TagKey.create(Registries.ITEM, ResourceLocation.parse("forge:ingots/silver"));
	private static final TagKey<Item> INGOTS_TIN = TagKey.create(Registries.ITEM, ResourceLocation.parse("forge:ingots/tin"));
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

	public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		registerHandgunRecipes(recipeOutput);
		registerAlkahestryRecipes(recipeOutput);
		registerPotionRecipes(recipeOutput);
		registerPedestalRecipes(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.ALKAHESTRY_ALTAR_ITEM.get())
				.requires(Tags.Items.OBSIDIANS)
				.requires(Items.REDSTONE_LAMP)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.FERTILE_LILY_PAD_ITEM.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(Items.LILY_PAD)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.INTERDICTION_TORCH_ITEM.get())
				.requires(ModItems.BAT_WING.get())
				.requires(Tags.Items.RODS_BLAZE)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.WRAITH_NODE_ITEM.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Tags.Items.GEMS_EMERALD)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		registerCraftableMobDropRecipes(recipeOutput);
		registerCharmFragmentRecipes(recipeOutput);
		registerIngredientRecipes(recipeOutput);
		registerUncraftingRecipes(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANGELHEART_VIAL.get())
				.pattern("GBG")
				.pattern("GCG")
				.pattern("FGF")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.MILK_BUCKET)
				.define('C', ModItems.INFERNAL_CLAW.get())
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANGELIC_FEATHER.get())
				.requires(Tags.Items.FEATHERS)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.BAT_WING.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DESTRUCTION_CATALYST.get())
				.requires(Items.FLINT_AND_STEEL)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EMPEROR_CHALICE.get())
				.requires(Tags.Items.GEMS_EMERALD)
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Items.BUCKET)
				.requires(DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_STAFF.get())
				.pattern(" BE")
				.pattern("NVB")
				.pattern("SN ")
				.define('B', ModItems.BAT_WING.get())
				.define('S', Items.STICK)
				.define('E', Items.ENDER_EYE)
				.define('V', DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.define('N', ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FORTUNE_COIN.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(ModItems.SLIME_PEARL.get())
				.requires(ModItems.BAT_WING.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GLACIAL_STAFF.get())
				.requires(ModItems.ICE_MAGUS_ROD.get())
				.requires(DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.requires(ModItems.FROZEN_CORE.get())
				.requires(ModItems.SHEARS_OF_WINTER.get())
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GLOWING_BREAD.get(), 3)
				.requires(Items.BREAD)
				.requires(Items.BREAD)
				.requires(Items.BREAD)
				.requires(ModItems.GLOWING_WATER.get())
				.unlockedBy("has_glowing_water", has(ModItems.GLOWING_WATER.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GLOWING_WATER.get())
				.pattern("GBG")
				.pattern("GDG")
				.pattern("NGP")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.WATER_BUCKET)
				.define('D', Tags.Items.DUSTS_GLOWSTONE)
				.define('P', Tags.Items.GUNPOWDERS)
				.define('N', Tags.Items.CROPS_NETHER_WART)
				.unlockedBy("has_nether_wart", hasTag(Tags.Items.CROPS_NETHER_WART))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GLOWING_WATER.get())
				.requires(ModItems.EMPTY_POTION_VIAL.get())
				.requires(Items.WATER_BUCKET)
				.requires(Tags.Items.DUSTS_GLOWSTONE)
				.requires(Tags.Items.GUNPOWDERS)
				.requires(Tags.Items.CROPS_NETHER_WART)
				.unlockedBy("has_empty_potion_vial", has(ModItems.EMPTY_POTION_VIAL.get()))
				.save(recipeOutput, Reliquary.getRL("glowing_water_from_potion_vial"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_NUGGET)
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.unlockedBy("has_empty_bullet", has(ModItems.EMPTY_BULLET.get()))
				.save(recipeOutput, Reliquary.getRL("gold_nugget"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HARVEST_ROD.get())
				.pattern(" RF")
				.pattern("VTR")
				.pattern("SV ")
				.define('R', Items.ROSE_BUSH)
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('V', Items.VINE)
				.define('T', DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.define('S', Items.STICK)
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HERO_MEDALLION.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.FORTUNE_COIN.get())
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.unlockedBy("has_infernal_tear", has(ModItems.INFERNAL_TEAR.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HOLY_HAND_GRENADE.get(), 4)
				.requires(ModItems.GLOWING_WATER.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Items.TNT)
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy("has_glowing_water", has(ModItems.GLOWING_WATER.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ICE_MAGUS_ROD.get())
				.pattern(" DF")
				.pattern(" VD")
				.pattern("I  ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('F', ModItems.FROZEN_CORE.get())
				.define('V', DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFERNAL_CHALICE.get())
				.requires(ModItems.INFERNAL_CLAWS.get())
				.requires(ModItems.EMPEROR_CHALICE.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy("has_emperor_chalice", has(ModItems.EMPEROR_CHALICE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFERNAL_CLAWS.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFERNAL_TEAR.get())
				.requires(DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.KRAKEN_SHELL.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy("has_kraken_shell_fragment", has(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LANTERN_OF_PARANOIA.get())
				.pattern("ISI")
				.pattern("GMG")
				.pattern(" I ")
				.define('S', ModItems.SLIME_PEARL.get())
				.define('G', Tags.Items.GLASS_BLOCKS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('M', ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGICBANE.get())
				.pattern("NG")
				.pattern("IN")
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('N', ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MERCY_CROSS.get())
				.pattern("WGR")
				.pattern("GLG")
				.pattern("SGZ")
				.define('W', ModItems.WITHERED_RIB.get())
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', ModItems.RIB_BONE.get())
				.define('L', Tags.Items.LEATHERS)
				.define('S', Items.WITHER_SKELETON_SKULL)
				.define('Z', ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MIDAS_TOUCHSTONE.get())
				.requires(Items.ANVIL)
				.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
				.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput);

		MobCharmRecipeBuilder.charmRecipe()
				.pattern("FLF")
				.pattern("FSF")
				.pattern("F F")
				.define('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.define('L', Tags.Items.LEATHERS)
				.define('S', Tags.Items.STRINGS)
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.save(recipeOutput.withConditions(new CharmEnabledCondition()));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_BELT.get())
				.pattern("LLL")
				.pattern("F F")
				.pattern("FFF")
				.define('L', Tags.Items.LEATHERS)
				.define('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.save(recipeOutput.withConditions(new CharmEnabledCondition()));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PHOENIX_DOWN.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELIC_FEATHER.get())
				.unlockedBy("has_angelic_feather", has(ModItems.ANGELIC_FEATHER.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PYROMANCER_STAFF.get())
				.requires(ModItems.INFERNAL_CLAWS.get())
				.requires(Tags.Items.RODS_BLAZE)
				.requires(ModItems.INFERNAL_TEAR.get())
				.requires(ModItems.SALAMANDER_EYE.get())
				.unlockedBy("has_infernal_claws", has(ModItems.INFERNAL_CLAWS.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RENDING_GALE.get())
				.pattern(" BE")
				.pattern("GVB")
				.pattern("SG ")
				.define('B', ModItems.BAT_WING.get())
				.define('S', Items.STICK)
				.define('E', ModItems.EYE_OF_THE_STORM.get())
				.define('V', DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_eye_of_the_storm", has(ModItems.EYE_OF_THE_STORM.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ROD_OF_LYSSA.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.BAT_WING.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Items.FISHING_ROD)
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SALAMANDER_EYE.get())
				.requires(Items.ENDER_EYE)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SERPENT_STAFF.get())
				.pattern(" CE")
				.pattern(" KC")
				.pattern("S  ")
				.define('S', Items.STICK)
				.define('C', ModItems.CHELICERAE.get())
				.define('E', Items.ENDER_EYE)
				.define('K', ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.unlockedBy("has_kraken_shell_fragment", has(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SHEARS_OF_WINTER.get())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(Items.SHEARS)
				.requires(Tags.Items.GEMS_DIAMOND)
				.requires(Tags.Items.GEMS_DIAMOND)
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOJOURNER_STAFF.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Tags.Items.RODS_BLAZE)
				.requires(DataComponentIngredient.of(true, new ItemStack(ModItems.VOID_TEAR.get())))
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TWILIGHT_CLOAK.get())
				.pattern("ICI")
				.pattern("BCB")
				.pattern("BCB")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Items.BLACK_WOOL)
				.define('C', ModItems.CRIMSON_CLOTH.get())
				.unlockedBy("has_crimson_cloth", has(ModItems.CRIMSON_CLOTH.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.VOID_TEAR.get())
				.requires(Items.GHAST_TEAR)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.SLIME_PEARL.get())
				.requires(Tags.Items.GEMS_LAPIS)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WITHERLESS_ROSE.get())
				.pattern("FNF")
				.pattern("NRN")
				.pattern("FNF")
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('N', Tags.Items.NETHER_STARS)
				.define('R', Items.ROSE_BUSH)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(recipeOutput);
	}

	private void registerUncraftingRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLAZE_ROD, 4)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "blaze_rod"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE, 5)
				.requires(ModItems.RIB_BONE.get())
				.unlockedBy("has_rib_bone", has(ModItems.RIB_BONE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "bone"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ENDER_PEARL, 3)
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "ender_pearl"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GHAST_TEAR)
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "ghast_tear"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GLASS_BOTTLE, 6)
				.pattern("W W")
				.pattern(" W ")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "glass_bottle"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GLOWSTONE_DUST, 6)
				.pattern("W  ")
				.pattern("W  ")
				.pattern(" W ")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "glowstone_dust"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_NUGGET, 6)
				.requires(ModItems.ZOMBIE_HEART.get())
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "gold_nugget"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 6)
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "gunpowder_creeper_gland"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 10)
				.requires(ModItems.EYE_OF_THE_STORM.get())
				.unlockedBy("has_eye_of_the_storm", has(ModItems.EYE_OF_THE_STORM.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "gunpowder_storm_eye"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GUNPOWDER, 6)
				.pattern("W  ")
				.pattern(" W ")
				.pattern("  W")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "gunpowder_witch_hat"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.INK_SAC, 6)
				.requires(ModItems.SQUID_BEAK.get())
				.unlockedBy("has_squid_beak", has(ModItems.SQUID_BEAK.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "ink_sac"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MAGMA_CREAM, 3)
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "magma_cream"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PACKED_ICE)
				.pattern("III")
				.pattern("ICI")
				.pattern("III")
				.define('I', Items.ICE)
				.define('C', ModItems.FROZEN_CORE.get())
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "packed_ice"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PRISMARINE_CRYSTALS, 10)
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "prismarine_crystals"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PRISMARINE_SHARD, 5)
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "prismarine_shard"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.REDSTONE, 6)
				.pattern("W")
				.pattern("W")
				.pattern("W")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "redstone"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ROTTEN_FLESH, 6)
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "rotten_flesh"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SLIME_BALL, 6)
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "slime_ball"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SNOWBALL, 5)
				.requires(ModItems.FROZEN_CORE.get())
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "snowball"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SPIDER_EYE, 2)
				.requires(ModItems.CHELICERAE.get())
				.requires(ModItems.CHELICERAE.get())
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "spider_eye"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STICK, 4)
				.requires(ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "stick"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STRING, 6)
				.requires(ModItems.CHELICERAE.get())
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "string"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SUGAR, 6)
				.pattern("WWW")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "sugar"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WITHER_SKELETON_SKULL)
				.requires(ModItems.WITHERED_RIB.get())
				.requires(ModItems.WITHERED_RIB.get())
				.requires(ModItems.WITHERED_RIB.get())
				.requires(Items.SKELETON_SKULL)
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(recipeOutput, Reliquary.getRL(UNCRAFTING_FOLDER + "wither_skeleton_skull"));

		ResourceLocation spawnEggId = Reliquary.getRL(UNCRAFTING_FOLDER + "spawn_egg");
		SpawnEggRecipeBuilder.spawnEggRecipe()
				.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
				.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
				.addIngredient(Items.EGG)
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.build(recipeOutput.withConditions(new SpawnEggEnabledCondition(), new CharmEnabledCondition()), spawnEggId);
	}

	private void registerHandgunRecipes(RecipeOutput recipeOutput) {
		RecipeOutput conditionalHandgunRecipeOutput = recipeOutput.withConditions(new HandgunEnabledCondition());
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HANDGUN.get())
				.pattern("BIM")
				.pattern("ISI")
				.pattern("IGI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', ModItems.BARREL_ASSEMBLY.get())
				.define('M', ModItems.HAMMER_ASSEMBLY.get())
				.define('G', ModItems.GRIP_ASSEMBLY.get())
				.define('S', ModItems.SLIME_PEARL.get())
				.unlockedBy("has_barrel_assembly", has(ModItems.BARREL_ASSEMBLY.get()))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BARREL_ASSEMBLY.get())
				.pattern("III")
				.pattern("EME")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('E', ModItems.NEBULOUS_HEART.get())
				.define('M', Items.MAGMA_CREAM)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GRIP_ASSEMBLY.get())
				.pattern("III")
				.pattern("IMI")
				.pattern("ICI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('C', ModItems.EMPTY_MAGAZINE.get())
				.define('M', Items.MAGMA_CREAM)
				.unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_MAGAZINE.get())
				.pattern("I I")
				.pattern("IGI")
				.pattern("SIS")
				.define('S', Tags.Items.STONES)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('G', Tags.Items.GLASS_BLOCKS)
				.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HAMMER_ASSEMBLY.get())
				.pattern("IIB")
				.pattern("RMI")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Items.STONE_BUTTON)
				.define('R', Tags.Items.RODS_BLAZE)
				.define('M', ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(conditionalHandgunRecipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLAZE_BULLET.get(), 8)
				.requires(Items.BLAZE_POWDER)
				.requires(Tags.Items.RODS_BLAZE)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(conditionalHandgunRecipeOutput, ModItems.BLAZE_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BUSTER_BULLET.get(), 8)
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CONCUSSIVE_BULLET.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy("has_concussive_bullet", has(ModItems.CONCUSSIVE_BULLET.get()))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.BUSTER_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CONCUSSIVE_BULLET.get(), 8)
				.requires(Tags.Items.SLIMEBALLS)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.CONCUSSIVE_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ENDER_BULLET.get(), 8)
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.SEEKER_BULLET.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy("has_seeker_bullet", has(ModItems.SEEKER_BULLET.get()))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.ENDER_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EXORCISM_BULLET.get(), 8)
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.NEUTRAL_BULLET.get())
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy("has_neutral_bullet", has(ModItems.NEUTRAL_BULLET.get()))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.EXORCISM_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.NEUTRAL_BULLET.get(), 8)
				.requires(Items.FLINT)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(conditionalHandgunRecipeOutput, ModItems.NEUTRAL_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SAND_BULLET.get(), 8)
				.requires(Tags.Items.SANDSTONE_BLOCKS)
				.requires(Tags.Items.SLIMEBALLS)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.SAND_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SEEKER_BULLET.get(), 8)
				.requires(Tags.Items.GEMS_LAPIS)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(conditionalHandgunRecipeOutput, ModItems.SEEKER_BULLET.get());

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.STORM_BULLET.get(), 8)
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.STORM_BULLET.get());

		addMagazineRecipe(recipeOutput, ModItems.BLAZE_BULLET.get(), ModItems.BLAZE_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.BUSTER_BULLET.get(), ModItems.BUSTER_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.CONCUSSIVE_BULLET.get(), ModItems.CONCUSSIVE_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.ENDER_BULLET.get(), ModItems.ENDER_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.EXORCISM_BULLET.get(), ModItems.EXORCISM_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.NEUTRAL_BULLET.get(), ModItems.NEUTRAL_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.SAND_BULLET.get(), ModItems.SAND_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.SEEKER_BULLET.get(), ModItems.SEEKER_MAGAZINE.get());
		addMagazineRecipe(recipeOutput, ModItems.STORM_BULLET.get(), ModItems.STORM_MAGAZINE.get());
	}

	private void registerAlkahestryRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ALKAHESTRY_TOME.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.EYE_OF_THE_STORM.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(Items.BOOK)
				.requires(ModItems.SLIME_PEARL.get())
				.requires(ModItems.CHELICERAE.get())
				.requires(Items.WITHER_SKELETON_SKULL)
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(recipeOutput.withConditions(new AlkahestryEnabledCondition()));

		ChargingRecipeBuilder.chargingRecipe(Items.GLOWSTONE_DUST, 1).build(recipeOutput, RegistryHelper.getRegistryName(Items.GLOWSTONE_DUST));
		ChargingRecipeBuilder.chargingRecipe(Items.GLOWSTONE, 4).build(recipeOutput, RegistryHelper.getRegistryName(Items.GLOWSTONE));
		ChargingRecipeBuilder.chargingRecipe(Items.REDSTONE, 1).build(recipeOutput, RegistryHelper.getRegistryName(Items.REDSTONE));
		ChargingRecipeBuilder.chargingRecipe(Items.REDSTONE_BLOCK, 9).build(recipeOutput, RegistryHelper.getRegistryName(Items.REDSTONE_BLOCK));

		DrainRecipeBuilder.drainRecipe(Items.REDSTONE, 1).build(recipeOutput, RegistryHelper.getRegistryName(Items.REDSTONE));

		CraftingRecipeBuilder.craftingRecipe(Items.CHARCOAL, 4, 5).save(recipeOutput, RegistryHelper.getRegistryName(Items.CHARCOAL));
		CraftingRecipeBuilder.craftingRecipe(Items.CLAY, 4, 3).save(recipeOutput, RegistryHelper.getRegistryName(Items.CLAY));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_COPPER, 8, 5)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition(INGOTS_COPPER.location()))), ResourceLocation.parse("copper_ingot"));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GEMS_DIAMOND, 64, 2).save(recipeOutput, RegistryHelper.getRegistryName(Items.DIAMOND));
		CraftingRecipeBuilder.craftingRecipe(Items.DIRT, 4, 33).save(recipeOutput, RegistryHelper.getRegistryName(Items.DIRT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GEMS_EMERALD, 32, 2).save(recipeOutput, RegistryHelper.getRegistryName(Items.EMERALD));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.END_STONES, 8, 17).save(recipeOutput, RegistryHelper.getRegistryName(Items.END_STONE));
		CraftingRecipeBuilder.craftingRecipe(Items.FLINT, 8, 9).save(recipeOutput, RegistryHelper.getRegistryName(Items.FLINT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.INGOTS_GOLD, 32, 2).save(recipeOutput, RegistryHelper.getRegistryName(Items.GOLD_INGOT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GRAVELS, 4, 17).save(recipeOutput, RegistryHelper.getRegistryName(Items.GRAVEL));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GUNPOWDERS, 8, 3).save(recipeOutput, RegistryHelper.getRegistryName(Items.GUNPOWDER));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.INGOTS_IRON, 32, 2).save(recipeOutput, RegistryHelper.getRegistryName(Items.IRON_INGOT));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.GEMS_LAPIS, 4, 2).save(recipeOutput, RegistryHelper.getRegistryName(Items.LAPIS_LAZULI));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.NETHER_STARS, 256, 2).save(recipeOutput, RegistryHelper.getRegistryName(Items.NETHER_STAR));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.NETHERRACKS, 4, 9).save(recipeOutput, RegistryHelper.getRegistryName(Items.NETHERRACK));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.OBSIDIANS, 8, 5).save(recipeOutput, RegistryHelper.getRegistryName(Items.OBSIDIAN));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.SANDS, 4, 33).save(recipeOutput, RegistryHelper.getRegistryName(Items.SAND));
		CraftingRecipeBuilder.craftingRecipe(Tags.Items.SANDSTONE_BLOCKS, 4, 9).save(recipeOutput, RegistryHelper.getRegistryName(Items.SANDSTONE));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_SILVER, 32, 2)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition(INGOTS_SILVER.location()))), ResourceLocation.parse("silver_ingot"));
		CraftingRecipeBuilder.craftingRecipe(Items.SOUL_SAND, 8, 9).save(recipeOutput, RegistryHelper.getRegistryName(Items.SOUL_SAND));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_STEEL, 32, 2)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition(INGOTS_STEEL.location()))), ResourceLocation.parse("steel_ingot"));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_TIN, 32, 2)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition(INGOTS_TIN.location()))), ResourceLocation.parse("tin_ingot"));
	}

	private void registerPotionRecipes(RecipeOutput recipeOutput) {
		RecipeOutput potionsEnabledRecipeOutput = recipeOutput.withConditions(new PotionsEnabledCondition());

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_POTION_VIAL.get())
				.pattern("G G")
				.pattern("G G")
				.pattern(" G ")
				.define('G', Tags.Items.GLASS_PANES)
				.unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
				.save(potionsEnabledRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.APHRODITE_POTION.get())
				.pattern("GBG")
				.pattern("GFG")
				.pattern("RGC")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.WATER_BUCKET)
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('R', Tags.Items.DYES_RED)
				.define('C', Items.COCOA_BEANS)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(potionsEnabledRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FERTILE_POTION.get())
				.pattern("GBG")
				.pattern("GFG")
				.pattern("CGY")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.WATER_BUCKET)
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('C', Tags.Items.DYES_GREEN)
				.define('Y', Tags.Items.DYES_YELLOW)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(potionsEnabledRecipeOutput);

		addItemPotionRecipe(potionsEnabledRecipeOutput, Items.ARROW, ModItems.TIPPED_ARROW.get(), 0.125f, 'A', false);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.APOTHECARY_CAULDRON_ITEM.get())
				.pattern("GNG")
				.pattern("ICI")
				.pattern("NMN")
				.define('G', ModItems.CATALYZING_GLAND.get())
				.define('N', ModItems.NEBULOUS_HEART.get())
				.define('I', ModItems.INFERNAL_CLAW.get())
				.define('C', Items.CAULDRON)
				.define('M', ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(potionsEnabledRecipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.APOTHECARY_MORTAR_ITEM.get())
				.pattern("GNG")
				.pattern("NGN")
				.pattern("NNN")
				.define('G', ModItems.CATALYZING_GLAND.get())
				.define('N', Items.QUARTZ_BLOCK)
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(potionsEnabledRecipeOutput);
	}

	private void registerPedestalRecipes(RecipeOutput recipeOutput) {
		addPassivePedestalRecipe(recipeOutput, Items.WHITE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.WHITE).get());
		addPassivePedestalRecipe(recipeOutput, Items.ORANGE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.ORANGE).get());
		addPassivePedestalRecipe(recipeOutput, Items.MAGENTA_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.MAGENTA).get());
		addPassivePedestalRecipe(recipeOutput, Items.LIGHT_BLUE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.LIGHT_BLUE).get());
		addPassivePedestalRecipe(recipeOutput, Items.YELLOW_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.YELLOW).get());
		addPassivePedestalRecipe(recipeOutput, Items.LIME_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.LIME).get());
		addPassivePedestalRecipe(recipeOutput, Items.PINK_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.PINK).get());
		addPassivePedestalRecipe(recipeOutput, Items.GRAY_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.GRAY).get());
		addPassivePedestalRecipe(recipeOutput, Items.LIGHT_GRAY_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.LIGHT_GRAY).get());
		addPassivePedestalRecipe(recipeOutput, Items.CYAN_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.CYAN).get());
		addPassivePedestalRecipe(recipeOutput, Items.PURPLE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.PURPLE).get());
		addPassivePedestalRecipe(recipeOutput, Items.BLUE_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.BLUE).get());
		addPassivePedestalRecipe(recipeOutput, Items.BROWN_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.BROWN).get());
		addPassivePedestalRecipe(recipeOutput, Items.GREEN_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.GREEN).get());
		addPassivePedestalRecipe(recipeOutput, Items.RED_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.RED).get());
		addPassivePedestalRecipe(recipeOutput, Items.BLACK_CARPET, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.BLACK).get());

		for (DyeColor color : DyeColor.values()) {
			addPedestalRecipe(recipeOutput, ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(color).get(), ModBlocks.PEDESTAL_ITEMS.get(color).get());
		}
	}

	private void registerCraftableMobDropRecipes(RecipeOutput recipeOutput) {
		addCraftableMobDropRecipe(recipeOutput, ModItems.BAT_WING.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GFG")
						.pattern("GGG")
						.define('F', Tags.Items.FEATHERS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_feather", hasTag(Tags.Items.FEATHERS))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.CATALYZING_GLAND.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GPG")
						.pattern("GGG")
						.define('P', Tags.Items.GUNPOWDERS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy(HAS_GUNPOWDER_CRITERION, hasTag(Tags.Items.GUNPOWDERS))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.CHELICERAE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GSG")
						.pattern("GGG")
						.define('S', Tags.Items.STRINGS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_string", hasTag(Tags.Items.STRINGS))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.FROZEN_CORE.get(), builder ->
				builder
						.pattern("GPG")
						.pattern("GSG")
						.pattern("GSG")
						.define('P', Items.PUMPKIN)
						.define('S', Items.SNOW)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_snow", has(Items.SNOW))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.GUARDIAN_SPIKE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GPG")
						.pattern("GGG")
						.define('P', Items.PRISMARINE_SHARD)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_prismarine", has(Items.PRISMARINE_SHARD))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.MOLTEN_CORE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GMG")
						.pattern("GGG")
						.define('M', Items.MAGMA_CREAM)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.NEBULOUS_HEART.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GEG")
						.pattern("GGG")
						.define('E', Tags.Items.ENDER_PEARLS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_ender_pearl", hasTag(Tags.Items.ENDER_PEARLS))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.RIB_BONE.get(), builder ->
				builder
						.pattern("III")
						.pattern("IBI")
						.pattern("III")
						.define('B', Tags.Items.BONES)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_bone", hasTag(Tags.Items.BONES))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.SLIME_PEARL.get(), builder ->
				builder
						.pattern("III")
						.pattern("ISI")
						.pattern("III")
						.define('S', Tags.Items.SLIMEBALLS)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_slimeball", hasTag(Tags.Items.SLIMEBALLS))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.SQUID_BEAK.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GIG")
						.pattern("GGG")
						.define('I', Items.INK_SAC)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_ink_sac", has(Items.INK_SAC))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.EYE_OF_THE_STORM.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GCG")
						.pattern("GGG")
						.define('C', ModItems.CATALYZING_GLAND.get())
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.WITHERED_RIB.get(), builder ->
				builder
						.pattern("D D")
						.pattern(" S ")
						.pattern("D D")
						.define('S', Items.SKELETON_SKULL)
						.define('D', Tags.Items.GEMS_DIAMOND)
						.unlockedBy("has_skeleton_skull", has(Items.SKELETON_SKULL))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.ZOMBIE_HEART.get(), builder ->
				builder
						.pattern("III")
						.pattern("IFI")
						.pattern("III")
						.define('F', Items.ROTTEN_FLESH)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
		);

		addCraftableMobDropRecipe(recipeOutput, ModItems.WITCH_HAT.get(), builder ->
				builder
						.pattern("SLS")
						.pattern("RGR")
						.pattern("TLT")
						.define('S', Items.SUGAR)
						.define('L', Tags.Items.DUSTS_GLOWSTONE)
						.define('R', Tags.Items.DUSTS_REDSTONE)
						.define('T', Items.STICK)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE))
		);
	}

	private void registerIngredientRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CRIMSON_CLOTH.get())
				.requires(Items.RED_WOOL)
				.requires(Items.BLACK_WOOL)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.RIB_BONE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(Tags.Items.DYES_GREEN)
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFERNAL_CLAW.get())
				.requires(Tags.Items.LEATHERS)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.RIB_BONE.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy("has_squid_beak", has(ModItems.SQUID_BEAK.get()))
				.save(recipeOutput);
	}

	private void registerCharmFragmentRecipes(RecipeOutput recipeOutput) {
		RecipeOutput conditionalRecipeOutput = recipeOutput.withConditions(new CharmEnabledCondition());
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.BLAZE))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.MOLTEN_CORE.get())
				.define('S', Tags.Items.RODS_BLAZE)
				.define('T', Items.BLAZE_POWDER)
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "blaze"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.CAVE_SPIDER))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.CHELICERAE.get())
				.define('S', Tags.Items.STRINGS)
				.define('T', DataComponentIngredient.of(true, PotionContents.createItemStack(Items.POTION, Potions.POISON)))
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "cave_spider"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.CREEPER))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.CATALYZING_GLAND.get())
				.define('S', Tags.Items.GUNPOWDERS)
				.define('T', Items.BONE)
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "creeper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.ENDERMAN))
				.pattern("PPP")
				.pattern("SPS")
				.pattern("PPP")
				.define('P', ModItems.NEBULOUS_HEART.get())
				.define('S', Tags.Items.ENDER_PEARLS)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "enderman"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.GHAST))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', Items.GHAST_TEAR)
				.define('S', Tags.Items.GUNPOWDERS)
				.define('T', ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "ghast"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.GUARDIAN))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.GUARDIAN_SPIKE.get())
				.define('S', Items.PRISMARINE_SHARD)
				.define('T', Items.COD)
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "guardian"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.MAGMA_CUBE))
				.pattern("PPP")
				.pattern("SSS")
				.pattern("PPP")
				.define('P', ModItems.MOLTEN_CORE.get())
				.define('S', Items.MAGMA_CREAM)
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "magma_cube"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.SKELETON))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.RIB_BONE.get())
				.define('S', Items.BONE)
				.define('T', Items.FLINT)
				.unlockedBy("has_rib_bone", has(ModItems.RIB_BONE.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "skeleton"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.SLIME))
				.pattern("PPP")
				.pattern("SSS")
				.pattern("PPP")
				.define('P', ModItems.SLIME_PEARL.get())
				.define('S', Tags.Items.SLIMEBALLS)
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "slime"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.SPIDER))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.CHELICERAE.get())
				.define('S', Tags.Items.STRINGS)
				.define('T', Items.SPIDER_EYE)
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "spider"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.WITCH))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.WITCH_HAT.get())
				.define('S', Items.GLASS_BOTTLE)
				.define('T', Items.SPIDER_EYE)
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "witch"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.WITHER_SKELETON))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.WITHERED_RIB.get())
				.define('S', Items.BONE)
				.define('T', Items.WITHER_SKELETON_SKULL)
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "wither_skeleton"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.ZOMBIE))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.ZOMBIE_HEART.get())
				.define('S', Items.ROTTEN_FLESH)
				.define('T', Items.BONE)
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "zombie"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(EntityType.ZOMBIFIED_PIGLIN))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.ZOMBIE_HEART.get())
				.define('S', Items.ROTTEN_FLESH)
				.define('T', Items.GOLDEN_SWORD)
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(conditionalRecipeOutput, Reliquary.getRL(MOB_CHARM_FRAGMENTS_FOLDER + "zombified_piglin"));
	}

	private Criterion<?> hasTag(TagKey<Item> tag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
	}

	private void addCraftableMobDropRecipe(RecipeOutput recipeOutput, Item item, Consumer<ShapedRecipeBuilder> setRecipe) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item);
		setRecipe.accept(builder);
		builder.save(recipeOutput.withConditions(new MobDropsCraftableCondition()), RegistryHelper.getRegistryName(item));
	}

	private void addPedestalRecipe(RecipeOutput recipeOutput, BlockItem passivePedestalItem, BlockItem pedestalItem) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, pedestalItem)
				.pattern("D D")
				.pattern(" P ")
				.pattern("D D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('P', passivePedestalItem)
				.unlockedBy("has_passive_pedestal", has(ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.WHITE).get()))
				.save(recipeOutput.withConditions(new PedestalEnabledCondition()));
	}

	private void addPassivePedestalRecipe(RecipeOutput recipeOutput, Item carpetItem, BlockItem pedestalItem) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, pedestalItem)
				.pattern(" C ")
				.pattern("GQG")
				.pattern("SSS")
				.define('C', carpetItem)
				.define('G', Tags.Items.NUGGETS_GOLD)
				.define('Q', Items.QUARTZ_BLOCK)
				.define('S', Items.QUARTZ_SLAB)
				.unlockedBy("has_quartz_block", has(Items.QUARTZ_BLOCK))
				.save(recipeOutput.withConditions(new PassivePedestalEnabledCondition()));
	}

	private void addBulletPotionRecipe(RecipeOutput recipeOutput, Item item) {
		addItemPotionRecipe(recipeOutput, item, item, (float) 0.2, 'B', true);
	}

	private void addItemPotionRecipe(RecipeOutput recipeOutput, Item itemIngredient, Item item, float durationFactor, char itemKey, boolean includeSuffix) {
		ResourceLocation registryName = RegistryHelper.getRegistryName(item);
		String path = registryName.getPath();
		ResourceLocation id = includeSuffix ? ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), registryName.getPath() + "_potion") : registryName;
		PotionEffectsRecipeBuilder.potionEffectsRecipe(item, 8, durationFactor)
				.pattern(String.valueOf(itemKey) + itemKey + itemKey)
				.pattern(itemKey + "P" + itemKey)
				.pattern(String.valueOf(itemKey) + itemKey + itemKey)
				.define(itemKey, itemIngredient)
				.define('P', ModItems.LINGERING_POTION.get())
				.unlockedBy("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), has(item))
				.save(recipeOutput, id);
	}

	private void addMagazineRecipe(RecipeOutput recipeOutput, BulletItem bulletItem, MagazineItem magazineItem) {
		String path = RegistryHelper.getRegistryName(bulletItem).getPath();
		PotionEffectsRecipeBuilder.potionEffectsRecipe(magazineItem, 1, 1)
				.pattern("BBB")
				.pattern("BMB")
				.pattern("BBB")
				.define('B', bulletItem)
				.define('M', ModItems.EMPTY_MAGAZINE.get())
				.unlockedBy("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), has(bulletItem))
				.save(recipeOutput, RegistryHelper.getRegistryName(magazineItem));
	}
}
