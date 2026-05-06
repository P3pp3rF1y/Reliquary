package reliquary.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import reliquary.Reliquary;
import reliquary.crafting.InfernalTearValueRecipeBuilder;
import reliquary.crafting.MobCharmRecipeBuilder;
import reliquary.crafting.PotionEffectsRecipeBuilder;
import reliquary.crafting.SpawnEggRecipeBuilder;
import reliquary.crafting.alkahestry.ChargingRecipeBuilder;
import reliquary.crafting.alkahestry.CraftingRecipeBuilder;
import reliquary.crafting.alkahestry.DrainRecipeBuilder;
import reliquary.crafting.conditions.*;
import reliquary.init.ModBlocks;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.item.BulletItem;
import reliquary.item.MagazineItem;
import reliquary.util.RegistryHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ReliquaryRecipeProvider extends RecipeProvider {
	private static final TagKey<Item> INGOTS_STEEL = TagKey.create(Registries.ITEM, Identifier.parse("forge:ingots/steel"));
	private static final TagKey<Item> INGOTS_SILVER = TagKey.create(Registries.ITEM, Identifier.parse("forge:ingots/silver"));
	private static final TagKey<Item> INGOTS_TIN = TagKey.create(Registries.ITEM, Identifier.parse("forge:ingots/tin"));
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

	private final HolderGetter<Item> items;

	public ReliquaryRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		super(provider, recipeOutput);

		items = provider.lookupOrThrow(Registries.ITEM);
	}

	@Override
	protected void buildRecipes() {
		registerHandgunRecipes(output);
		registerAlkahestryRecipes(output);
		registerPotionRecipes(output);
		registerPedestalRecipes(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModBlocks.ALKAHESTRY_ALTAR_ITEM.get())
				.requires(Tags.Items.OBSIDIANS)
				.requires(Items.REDSTONE_LAMP)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModBlocks.FERTILE_LILY_PAD_ITEM.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(Items.LILY_PAD)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModBlocks.INTERDICTION_TORCH_ITEM.get())
				.requires(ModItems.BAT_WING.get())
				.requires(Tags.Items.RODS_BLAZE)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModBlocks.WRAITH_NODE_ITEM.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Tags.Items.GEMS_EMERALD)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		registerCraftableMobDropRecipes();
		registerCharmFragmentRecipes();
		registerInfernalTearRecipes();
		registerIngredientRecipes();
		registerUncraftingRecipes();

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.ANGELHEART_VIAL.get())
				.pattern("GBG")
				.pattern("GCG")
				.pattern("FGF")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.MILK_BUCKET)
				.define('C', ModItems.INFERNAL_CLAW.get())
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.ANGELIC_FEATHER.get())
				.requires(Tags.Items.FEATHERS)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.BAT_WING.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.DESTRUCTION_CATALYST.get())
				.requires(Items.FLINT_AND_STEEL)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.EMPEROR_CHALICE.get())
				.requires(Tags.Items.GEMS_EMERALD)
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Items.BUCKET)
				.requires(emptyVoidTearIngredient())
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.ENDER_STAFF.get())
				.pattern(" BE")
				.pattern("NVB")
				.pattern("SN ")
				.define('B', ModItems.BAT_WING.get())
				.define('S', Items.STICK)
				.define('E', Items.ENDER_EYE)
				.define('V', emptyVoidTearIngredient())
				.define('N', ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.FORTUNE_COIN.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(ModItems.SLIME_PEARL.get())
				.requires(ModItems.BAT_WING.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.GLACIAL_STAFF.get())
				.requires(ModItems.ICE_MAGUS_ROD.get())
				.requires(emptyVoidTearIngredient())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(ModItems.SHEARS_OF_WINTER.get())
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.GLOWING_BREAD.get(), 3)
				.requires(Items.BREAD)
				.requires(Items.BREAD)
				.requires(Items.BREAD)
				.requires(ModItems.GLOWING_WATER.get())
				.unlockedBy("has_glowing_water", has(ModItems.GLOWING_WATER.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.GLOWING_WATER.get())
				.pattern("GBG")
				.pattern("GDG")
				.pattern("NGP")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.WATER_BUCKET)
				.define('D', Tags.Items.DUSTS_GLOWSTONE)
				.define('P', Tags.Items.GUNPOWDERS)
				.define('N', Tags.Items.CROPS_NETHER_WART)
				.unlockedBy("has_nether_wart", hasTag(Tags.Items.CROPS_NETHER_WART))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.GLOWING_WATER.get())
				.requires(ModItems.EMPTY_POTION_VIAL.get())
				.requires(Items.WATER_BUCKET)
				.requires(Tags.Items.DUSTS_GLOWSTONE)
				.requires(Tags.Items.GUNPOWDERS)
				.requires(Tags.Items.CROPS_NETHER_WART)
				.unlockedBy("has_empty_potion_vial", has(ModItems.EMPTY_POTION_VIAL.get()))
				.save(output, getRecipeKey("glowing_water_from_potion_vial"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.GOLD_NUGGET)
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.unlockedBy("has_empty_bullet", has(ModItems.EMPTY_BULLET.get()))
				.save(output, getRecipeKey("gold_nugget"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.HARVEST_ROD.get())
				.pattern(" RF")
				.pattern("VTR")
				.pattern("SV ")
				.define('R', Items.ROSE_BUSH)
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('V', Items.VINE)
				.define('T', emptyVoidTearIngredient())
				.define('S', Items.STICK)
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.HERO_MEDALLION.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.FORTUNE_COIN.get())
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.unlockedBy("has_infernal_tear", has(ModItems.INFERNAL_TEAR.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.HOLY_HAND_GRENADE.get(), 4)
				.requires(ModItems.GLOWING_WATER.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Items.TNT)
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy("has_glowing_water", has(ModItems.GLOWING_WATER.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.ICE_MAGUS_ROD.get())
				.pattern(" DF")
				.pattern(" VD")
				.pattern("I  ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('F', ModItems.FROZEN_CORE.get())
				.define('V', emptyVoidTearIngredient())
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.INFERNAL_CHALICE.get())
				.requires(ModItems.INFERNAL_CLAWS.get())
				.requires(ModItems.EMPEROR_CHALICE.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy("has_emperor_chalice", has(ModItems.EMPEROR_CHALICE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.INFERNAL_CLAWS.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.INFERNAL_TEAR.get())
				.requires(emptyVoidTearIngredient())
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.KRAKEN_SHELL.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy("has_kraken_shell_fragment", has(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.LANTERN_OF_PARANOIA.get())
				.pattern("ISI")
				.pattern("GMG")
				.pattern(" I ")
				.define('S', ModItems.SLIME_PEARL.get())
				.define('G', Tags.Items.GLASS_BLOCKS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('M', ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.MAGICBANE.get())
				.pattern("NG")
				.pattern("IN")
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('N', ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.MERCY_CROSS.get())
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
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.MIDAS_TOUCHSTONE.get())
				.requires(Items.ANVIL)
				.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
				.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(emptyVoidTearIngredient())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output);

		MobCharmRecipeBuilder.charmRecipe(items)
				.pattern("FLF")
				.pattern("FSF")
				.pattern("F F")
				.define('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.define('L', Tags.Items.LEATHERS)
				.define('S', Tags.Items.STRINGS)
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.save(output.withConditions(new CharmEnabledCondition()));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.MOB_CHARM_BELT.get())
				.pattern("LLL")
				.pattern("F F")
				.pattern("FFF")
				.define('L', Tags.Items.LEATHERS)
				.define('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.save(output.withConditions(new CharmEnabledCondition()));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.PHOENIX_DOWN.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELIC_FEATHER.get())
				.unlockedBy("has_angelic_feather", has(ModItems.ANGELIC_FEATHER.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.PYROMANCER_STAFF.get())
				.requires(ModItems.INFERNAL_CLAWS.get())
				.requires(Tags.Items.RODS_BLAZE)
				.requires(ModItems.INFERNAL_TEAR.get())
				.requires(ModItems.SALAMANDER_EYE.get())
				.unlockedBy("has_infernal_claws", has(ModItems.INFERNAL_CLAWS.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.RENDING_GALE.get())
				.pattern(" BE")
				.pattern("GVB")
				.pattern("SG ")
				.define('B', ModItems.BAT_WING.get())
				.define('S', Items.STICK)
				.define('E', ModItems.EYE_OF_THE_STORM.get())
				.define('V', emptyVoidTearIngredient())
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_eye_of_the_storm", has(ModItems.EYE_OF_THE_STORM.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.ROD_OF_LYSSA.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.BAT_WING.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Items.FISHING_ROD)
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SALAMANDER_EYE.get())
				.requires(Items.ENDER_EYE)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.SERPENT_STAFF.get())
				.pattern(" CE")
				.pattern(" KC")
				.pattern("S  ")
				.define('S', Items.STICK)
				.define('C', ModItems.CHELICERAE.get())
				.define('E', Items.ENDER_EYE)
				.define('K', ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.unlockedBy("has_kraken_shell_fragment", has(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SHEARS_OF_WINTER.get())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(Items.SHEARS)
				.requires(Tags.Items.GEMS_DIAMOND)
				.requires(Tags.Items.GEMS_DIAMOND)
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SOJOURNER_STAFF.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Tags.Items.RODS_BLAZE)
				.requires(emptyVoidTearIngredient())
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.TWILIGHT_CLOAK.get())
				.pattern("ICI")
				.pattern("BCB")
				.pattern("BCB")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Items.BLACK_WOOL)
				.define('C', ModItems.CRIMSON_CLOTH.get())
				.unlockedBy("has_crimson_cloth", has(ModItems.CRIMSON_CLOTH.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.VOID_TEAR.get())
				.requires(Items.GHAST_TEAR)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.SLIME_PEARL.get())
				.requires(Tags.Items.GEMS_LAPIS)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.WITHERLESS_ROSE.get())
				.pattern("FNF")
				.pattern("NRN")
				.pattern("FNF")
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('N', Tags.Items.NETHER_STARS)
				.define('R', Items.ROSE_BUSH)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(output);
	}

	private void registerUncraftingRecipes() {
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.BLAZE_ROD, 4)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "blaze_rod"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.BONE, 5)
				.requires(ModItems.RIB_BONE.get())
				.unlockedBy("has_rib_bone", has(ModItems.RIB_BONE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "bone"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.ENDER_PEARL, 3)
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "ender_pearl"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.GHAST_TEAR)
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "ghast_tear"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.GLASS_BOTTLE, 6)
				.pattern("W W")
				.pattern(" W ")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "glass_bottle"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.GLOWSTONE_DUST, 6)
				.pattern("W  ")
				.pattern("W  ")
				.pattern(" W ")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "glowstone_dust"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.GOLD_NUGGET, 6)
				.requires(ModItems.ZOMBIE_HEART.get())
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "gold_nugget"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.GUNPOWDER, 6)
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "gunpowder_creeper_gland"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.GUNPOWDER, 10)
				.requires(ModItems.EYE_OF_THE_STORM.get())
				.unlockedBy("has_eye_of_the_storm", has(ModItems.EYE_OF_THE_STORM.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "gunpowder_storm_eye"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.GUNPOWDER, 6)
				.pattern("W  ")
				.pattern(" W ")
				.pattern("  W")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "gunpowder_witch_hat"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.INK_SAC, 6)
				.requires(ModItems.SQUID_BEAK.get())
				.unlockedBy("has_squid_beak", has(ModItems.SQUID_BEAK.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "ink_sac"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.MAGMA_CREAM, 3)
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "magma_cream"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.PACKED_ICE)
				.pattern("III")
				.pattern("ICI")
				.pattern("III")
				.define('I', Items.ICE)
				.define('C', ModItems.FROZEN_CORE.get())
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "packed_ice"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.PRISMARINE_CRYSTALS, 10)
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "prismarine_crystals"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.PRISMARINE_SHARD, 5)
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "prismarine_shard"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.REDSTONE, 6)
				.pattern("W")
				.pattern("W")
				.pattern("W")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "redstone"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.ROTTEN_FLESH, 6)
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "rotten_flesh"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.SLIME_BALL, 6)
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "slime_ball"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.SNOWBALL, 5)
				.requires(ModItems.FROZEN_CORE.get())
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "snowball"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.SPIDER_EYE, 2)
				.requires(ModItems.CHELICERAE.get())
				.requires(ModItems.CHELICERAE.get())
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "spider_eye"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.STICK, 4)
				.requires(ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "stick"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.STRING, 6)
				.requires(ModItems.CHELICERAE.get())
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "string"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.SUGAR, 6)
				.pattern("WWW")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "sugar"));

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, Items.WITHER_SKELETON_SKULL)
				.requires(ModItems.WITHERED_RIB.get())
				.requires(ModItems.WITHERED_RIB.get())
				.requires(ModItems.WITHERED_RIB.get())
				.requires(Items.SKELETON_SKULL)
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(output, getRecipeKey(UNCRAFTING_FOLDER + "wither_skeleton_skull"));

		ResourceKey<Recipe<?>> spawnEggId = getRecipeKey(UNCRAFTING_FOLDER + "spawn_egg");
		SpawnEggRecipeBuilder.spawnEggRecipe()
				.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
				.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
				.addIngredient(Items.EGG)
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.build(output.withConditions(new SpawnEggEnabledCondition(), new CharmEnabledCondition()), spawnEggId);
	}

	private void registerHandgunRecipes(RecipeOutput recipeOutput) {
		RecipeOutput conditionalHandgunRecipeOutput = recipeOutput.withConditions(new HandgunEnabledCondition());
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.HANDGUN.get())
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

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.BARREL_ASSEMBLY.get())
				.pattern("III")
				.pattern("EME")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('E', ModItems.NEBULOUS_HEART.get())
				.define('M', Items.MAGMA_CREAM)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.GRIP_ASSEMBLY.get())
				.pattern("III")
				.pattern("IMI")
				.pattern("ICI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('C', ModItems.EMPTY_MAGAZINE.get())
				.define('M', Items.MAGMA_CREAM)
				.unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.EMPTY_MAGAZINE.get())
				.pattern("I I")
				.pattern("IGI")
				.pattern("SIS")
				.define('S', Tags.Items.STONES)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('G', Tags.Items.GLASS_BLOCKS)
				.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
				.save(conditionalHandgunRecipeOutput);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.HAMMER_ASSEMBLY.get())
				.pattern("IIB")
				.pattern("RMI")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Items.STONE_BUTTON)
				.define('R', Tags.Items.RODS_BLAZE)
				.define('M', ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(conditionalHandgunRecipeOutput);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.BLAZE_BULLET.get(), 8)
				.requires(Items.BLAZE_POWDER)
				.requires(Tags.Items.RODS_BLAZE)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(conditionalHandgunRecipeOutput, ModItems.BLAZE_BULLET.get());

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.BUSTER_BULLET.get(), 8)
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

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.CONCUSSIVE_BULLET.get(), 8)
				.requires(Tags.Items.SLIME_BALLS)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.CONCUSSIVE_BULLET.get());

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.ENDER_BULLET.get(), 8)
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

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.EXORCISM_BULLET.get(), 8)
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

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.NEUTRAL_BULLET.get(), 8)
				.requires(Items.FLINT)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(conditionalHandgunRecipeOutput, ModItems.NEUTRAL_BULLET.get());

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SAND_BULLET.get(), 8)
				.requires(Tags.Items.SANDSTONE_BLOCKS)
				.requires(Tags.Items.SLIME_BALLS)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(recipeOutput, ModItems.SAND_BULLET.get());

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SEEKER_BULLET.get(), 8)
				.requires(Tags.Items.GEMS_LAPIS)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Tags.Items.GUNPOWDERS)
				.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
				.save(conditionalHandgunRecipeOutput);

		addBulletPotionRecipe(conditionalHandgunRecipeOutput, ModItems.SEEKER_BULLET.get());

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.STORM_BULLET.get(), 8)
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
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.ALKAHESTRY_TOME.get())
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

		CraftingRecipeBuilder.craftingRecipe(Items.CHARCOAL, 4, 5).save(recipeOutput, getRecipeKey(Items.CHARCOAL));
		CraftingRecipeBuilder.craftingRecipe(Items.CLAY, 4, 3).save(recipeOutput, getRecipeKey(Items.CLAY));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.INGOTS_COPPER, 8, 5)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition<>(Tags.Items.INGOTS_COPPER))), ResourceKey.create(Registries.RECIPE, Identifier.parse("copper_ingot")));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.GEMS_DIAMOND, 64, 2).save(recipeOutput, getRecipeKey(Items.DIAMOND));
		CraftingRecipeBuilder.craftingRecipe(Items.DIRT, 4, 33).save(recipeOutput, getRecipeKey(Items.DIRT));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.GEMS_EMERALD, 32, 2).save(recipeOutput, getRecipeKey(Items.EMERALD));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.END_STONES, 8, 17).save(recipeOutput, getRecipeKey(Items.END_STONE));
		CraftingRecipeBuilder.craftingRecipe(Items.FLINT, 8, 9).save(recipeOutput, getRecipeKey(Items.FLINT));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.INGOTS_GOLD, 32, 2).save(recipeOutput, getRecipeKey(Items.GOLD_INGOT));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.GRAVELS, 4, 17).save(recipeOutput, getRecipeKey(Items.GRAVEL));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.GUNPOWDERS, 8, 3).save(recipeOutput, getRecipeKey(Items.GUNPOWDER));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.INGOTS_IRON, 32, 2).save(recipeOutput, getRecipeKey(Items.IRON_INGOT));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.GEMS_LAPIS, 4, 2).save(recipeOutput, getRecipeKey(Items.LAPIS_LAZULI));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.NETHER_STARS, 256, 2).save(recipeOutput, getRecipeKey(Items.NETHER_STAR));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.NETHERRACKS, 4, 9).save(recipeOutput, getRecipeKey(Items.NETHERRACK));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.OBSIDIANS, 8, 5).save(recipeOutput, getRecipeKey(Items.OBSIDIAN));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.SANDS, 4, 33).save(recipeOutput, getRecipeKey(Items.SAND));
		CraftingRecipeBuilder.craftingRecipe(items, Tags.Items.SANDSTONE_BLOCKS, 4, 9).save(recipeOutput, getRecipeKey(Items.SANDSTONE));
		CraftingRecipeBuilder.craftingRecipe(items, INGOTS_SILVER, 32, 2)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition<>(INGOTS_SILVER))), ResourceKey.create(Registries.RECIPE, Identifier.parse("silver_ingot")));
		CraftingRecipeBuilder.craftingRecipe(Items.SOUL_SAND, 8, 9).save(recipeOutput, getRecipeKey(Items.SOUL_SAND));
		CraftingRecipeBuilder.craftingRecipe(items, INGOTS_STEEL, 32, 2)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition<>(INGOTS_STEEL))), ResourceKey.create(Registries.RECIPE, Identifier.parse("steel_ingot")));
		CraftingRecipeBuilder.craftingRecipe(items, INGOTS_TIN, 32, 2)
				.save(recipeOutput.withConditions(new NotCondition(new TagEmptyCondition<>(INGOTS_TIN))), ResourceKey.create(Registries.RECIPE, Identifier.parse("tin_ingot")));
	}

	private ResourceKey<Recipe<?>> getRecipeKey(String name) {
		return ResourceKey.create(Registries.RECIPE, Reliquary.getIdentifier(name));
	}

	private ResourceKey<Recipe<?>> getRecipeKey(Item item) {
		return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(item));
	}

	private void registerInfernalTearRecipes() {
		InfernalTearValueRecipeBuilder.valueRecipe(Items.BLAZE_ROD, 4).save(output, "blaze_rod");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.CHARCOAL, 2).save(output, "charcoal");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.CHORUS_FRUIT, 2).save(output, "chorus_fruit");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.CLAY, 4).save(output, "clay");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.INGOTS_COPPER, 5).save(output, "copper_ingot");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.GEMS_DIAMOND, 32).save(output, "diamond");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.GEMS_EMERALD, 16).save(output, "emerald");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.END_STONES, 1).save(output, "end_stone");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.ENDER_PEARL, 2).save(output, "ender_pearl");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.FLINT, 2).save(output, "flint");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.GHAST_TEAR, 8).save(output, "ghast_tear");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.INGOTS_GOLD, 6).save(output, "gold_ingot");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.GRAVELS, 1).save(output, "gravel");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.GUNPOWDERS, 4).save(output, "gunpowder");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.HONEYCOMB, 3).save(output, "honeycomb");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.INGOTS_IRON, 6).save(output, "iron_ingot");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.GEMS_LAPIS, 4).save(output, "lapis_lazuli");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.MAGMA_CREAM, 3).save(output, "magma_cream");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.NETHER_STARS, 192).save(output, "nether_star");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.NETHERITE_SCRAP, 96).save(output, "netherite_scrap");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.NETHERRACKS, 1).save(output, "netherrack");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.OBSIDIANS, 4).save(output, "obsidian");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.PHANTOM_MEMBRANE, 3).save(output, "phantom_membrane");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.PRISMARINE_CRYSTALS, 4).save(output, "prismarine_crystals");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.PRISMARINE_SHARD, 1).save(output, "prismarine_shard");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.QUARTZ, 5).save(output, "quartz");
		InfernalTearValueRecipeBuilder.valueRecipe(items, Tags.Items.SANDSTONE_BLOCKS, 1).save(output, "sandstone");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.SHULKER_SHELL, 10).save(output, "shulker_shell");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.SLIME_BALL, 1).save(output, "slime_ball");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.SOUL_SAND, 2).save(output, "soul_sand");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.SPIDER_EYE, 1).save(output, "spider_eye");
		InfernalTearValueRecipeBuilder.valueRecipe(Items.WITHER_SKELETON_SKULL, 16).save(output, "wither_skeleton_skull");
	}

	private void registerPotionRecipes(RecipeOutput recipeOutput) {
		RecipeOutput potionsEnabledRecipeOutput = recipeOutput.withConditions(new PotionsEnabledCondition());

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.EMPTY_POTION_VIAL.get())
				.pattern("G G")
				.pattern("G G")
				.pattern(" G ")
				.define('G', Tags.Items.GLASS_PANES)
				.unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
				.save(potionsEnabledRecipeOutput);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.APHRODITE_POTION.get())
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

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.FERTILE_POTION.get())
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

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.APOTHECARY_CAULDRON_ITEM.get())
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

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModBlocks.APOTHECARY_MORTAR_ITEM.get())
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

	private void registerCraftableMobDropRecipes() {
		addCraftableMobDropRecipe(output, ModItems.BAT_WING.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GFG")
						.pattern("GGG")
						.define('F', Tags.Items.FEATHERS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_feather", hasTag(Tags.Items.FEATHERS))
		);

		addCraftableMobDropRecipe(output, ModItems.CATALYZING_GLAND.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GPG")
						.pattern("GGG")
						.define('P', Tags.Items.GUNPOWDERS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy(HAS_GUNPOWDER_CRITERION, hasTag(Tags.Items.GUNPOWDERS))
		);

		addCraftableMobDropRecipe(output, ModItems.CHELICERAE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GSG")
						.pattern("GGG")
						.define('S', Tags.Items.STRINGS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_string", hasTag(Tags.Items.STRINGS))
		);

		addCraftableMobDropRecipe(output, ModItems.FROZEN_CORE.get(), builder ->
				builder
						.pattern("GPG")
						.pattern("GSG")
						.pattern("GSG")
						.define('P', Items.PUMPKIN)
						.define('S', Items.SNOW)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_snow", has(Items.SNOW))
		);

		addCraftableMobDropRecipe(output, ModItems.GUARDIAN_SPIKE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GPG")
						.pattern("GGG")
						.define('P', Items.PRISMARINE_SHARD)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_prismarine", has(Items.PRISMARINE_SHARD))
		);

		addCraftableMobDropRecipe(output, ModItems.MOLTEN_CORE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GMG")
						.pattern("GGG")
						.define('M', Items.MAGMA_CREAM)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
		);

		addCraftableMobDropRecipe(output, ModItems.NEBULOUS_HEART.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GEG")
						.pattern("GGG")
						.define('E', Tags.Items.ENDER_PEARLS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_ender_pearl", hasTag(Tags.Items.ENDER_PEARLS))
		);

		addCraftableMobDropRecipe(output, ModItems.RIB_BONE.get(), builder ->
				builder
						.pattern("III")
						.pattern("IBI")
						.pattern("III")
						.define('B', Tags.Items.BONES)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_bone", hasTag(Tags.Items.BONES))
		);

		addCraftableMobDropRecipe(output, ModItems.SLIME_PEARL.get(), builder ->
				builder
						.pattern("III")
						.pattern("ISI")
						.pattern("III")
						.define('S', Tags.Items.SLIME_BALLS)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_slimeball", hasTag(Tags.Items.SLIME_BALLS))
		);

		addCraftableMobDropRecipe(output, ModItems.SQUID_BEAK.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GIG")
						.pattern("GGG")
						.define('I', Items.INK_SAC)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_ink_sac", has(Items.INK_SAC))
		);

		addCraftableMobDropRecipe(output, ModItems.EYE_OF_THE_STORM.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GCG")
						.pattern("GGG")
						.define('C', ModItems.CATALYZING_GLAND.get())
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
		);

		addCraftableMobDropRecipe(output, ModItems.WITHERED_RIB.get(), builder ->
				builder
						.pattern("D D")
						.pattern(" S ")
						.pattern("D D")
						.define('S', Items.SKELETON_SKULL)
						.define('D', Tags.Items.GEMS_DIAMOND)
						.unlockedBy("has_skeleton_skull", has(Items.SKELETON_SKULL))
		);

		addCraftableMobDropRecipe(output, ModItems.ZOMBIE_HEART.get(), builder ->
				builder
						.pattern("III")
						.pattern("IFI")
						.pattern("III")
						.define('F', Items.ROTTEN_FLESH)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
		);

		addCraftableMobDropRecipe(output, ModItems.WITCH_HAT.get(), builder ->
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

	private void registerIngredientRecipes() {
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.CRIMSON_CLOTH.get())
				.requires(Items.RED_WOOL)
				.requires(Items.BLACK_WOOL)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.RIB_BONE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(Tags.Items.DYES_GREEN)
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.INFERNAL_CLAW.get())
				.requires(Tags.Items.LEATHERS)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.RIB_BONE.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy("has_squid_beak", has(ModItems.SQUID_BEAK.get()))
				.save(output);
	}

	private void registerCharmFragmentRecipes() {
		RecipeOutput conditionalRecipeOutput = output.withConditions(new CharmEnabledCondition());
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.BLAZE))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.MOLTEN_CORE.get())
				.define('S', Tags.Items.RODS_BLAZE)
				.define('T', Items.BLAZE_POWDER)
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "blaze"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.CAVE_SPIDER))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.CHELICERAE.get())
				.define('S', Tags.Items.STRINGS)
				.define('T', DataComponentIngredient.of(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON), Items.POTION))
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "cave_spider"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.CREEPER))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.CATALYZING_GLAND.get())
				.define('S', Tags.Items.GUNPOWDERS)
				.define('T', Items.BONE)
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "creeper"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.ENDERMAN))
				.pattern("PPP")
				.pattern("SPS")
				.pattern("PPP")
				.define('P', ModItems.NEBULOUS_HEART.get())
				.define('S', Tags.Items.ENDER_PEARLS)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "enderman"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.GHAST))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', Items.GHAST_TEAR)
				.define('S', Tags.Items.GUNPOWDERS)
				.define('T', ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "ghast"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.GUARDIAN))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.GUARDIAN_SPIKE.get())
				.define('S', Items.PRISMARINE_SHARD)
				.define('T', Items.COD)
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "guardian"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.MAGMA_CUBE))
				.pattern("PPP")
				.pattern("SSS")
				.pattern("PPP")
				.define('P', ModItems.MOLTEN_CORE.get())
				.define('S', Items.MAGMA_CREAM)
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "magma_cube"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.SKELETON))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.RIB_BONE.get())
				.define('S', Items.BONE)
				.define('T', Items.FLINT)
				.unlockedBy("has_rib_bone", has(ModItems.RIB_BONE.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "skeleton"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.SLIME))
				.pattern("PPP")
				.pattern("SSS")
				.pattern("PPP")
				.define('P', ModItems.SLIME_PEARL.get())
				.define('S', Tags.Items.SLIME_BALLS)
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "slime"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.SPIDER))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.CHELICERAE.get())
				.define('S', Tags.Items.STRINGS)
				.define('T', Items.SPIDER_EYE)
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "spider"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.WITCH))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.WITCH_HAT.get())
				.define('S', Items.GLASS_BOTTLE)
				.define('T', Items.SPIDER_EYE)
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "witch"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.WITHER_SKELETON))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.WITHERED_RIB.get())
				.define('S', Items.BONE)
				.define('T', Items.WITHER_SKELETON_SKULL)
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "wither_skeleton"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.ZOMBIE))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.ZOMBIE_HEART.get())
				.define('S', Items.ROTTEN_FLESH)
				.define('T', Items.BONE)
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "zombie"));

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, mobCharmFragmentTemplate(EntityType.ZOMBIFIED_PIGLIN))
				.pattern("PPP")
				.pattern("STS")
				.pattern("PPP")
				.define('P', ModItems.ZOMBIE_HEART.get())
				.define('S', Items.ROTTEN_FLESH)
				.define('T', Items.GOLDEN_SWORD)
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(conditionalRecipeOutput, getRecipeKey(MOB_CHARM_FRAGMENTS_FOLDER + "zombified_piglin"));
	}

	private static ItemStackTemplate mobCharmFragmentTemplate(EntityType<?> entityType) {
		return new ItemStackTemplate(ModItems.MOB_CHARM_FRAGMENT.get(), DataComponentPatch.builder().set(ModDataComponents.ENTITY_NAME.get(), EntityType.getKey(entityType)).build());
	}

	private static Ingredient emptyVoidTearIngredient() {
		return DataComponentIngredient.of(DataComponentPatch.builder()
				.remove(ModDataComponents.ENABLED.get())
				.remove(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS.get())
				.build(), ModItems.VOID_TEAR.get());
	}

	private Criterion<?> hasTag(TagKey<Item> tag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(items, tag).build());
	}

	private void addCraftableMobDropRecipe(RecipeOutput recipeOutput, Item item, Consumer<ShapedRecipeBuilder> setRecipe) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, item);
		setRecipe.accept(builder);
		builder.save(recipeOutput.withConditions(new MobDropsCraftableCondition()), getRecipeKey(item));
	}

	private void addPedestalRecipe(RecipeOutput recipeOutput, BlockItem passivePedestalItem, BlockItem pedestalItem) {
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, pedestalItem)
				.pattern("D D")
				.pattern(" P ")
				.pattern("D D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('P', passivePedestalItem)
				.unlockedBy("has_passive_pedestal", has(ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.WHITE).get()))
				.save(recipeOutput.withConditions(new PedestalEnabledCondition()));
	}

	private void addPassivePedestalRecipe(RecipeOutput recipeOutput, Item carpetItem, BlockItem pedestalItem) {
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, pedestalItem)
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
		Identifier registryName = RegistryHelper.getRegistryName(item);
		String path = registryName.getPath();
		Identifier id = includeSuffix ? Identifier.fromNamespaceAndPath(registryName.getNamespace(), registryName.getPath() + "_potion") : registryName;
		PotionEffectsRecipeBuilder.potionEffectsRecipe(item, 8, durationFactor)
				.pattern(String.valueOf(itemKey) + itemKey + itemKey)
				.pattern(itemKey + "P" + itemKey)
				.pattern(String.valueOf(itemKey) + itemKey + itemKey)
				.define(itemKey, itemIngredient)
				.define('P', ModItems.LINGERING_POTION.get())
				.unlockedBy("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), has(item))
				.save(recipeOutput, ResourceKey.create(Registries.RECIPE, id));
	}

	private void addMagazineRecipe(RecipeOutput recipeOutput, BulletItem bulletItem, MagazineItem magazineItem) {
		String path = getRecipeKey(bulletItem).identifier().getPath();
		PotionEffectsRecipeBuilder.potionEffectsRecipe(magazineItem, 1, 1)
				.pattern("BBB")
				.pattern("BMB")
				.pattern("BBB")
				.define('B', bulletItem)
				.define('M', ModItems.EMPTY_MAGAZINE.get())
				.unlockedBy("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), has(bulletItem))
				.save(recipeOutput, getRecipeKey(magazineItem));
	}

	public static class Runner extends RecipeProvider.Runner {

		protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
			super(packOutput, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			return new ReliquaryRecipeProvider(provider, recipeOutput);
		}

		@Override
		public String getName() {
			return "Reliquary Recipes";
		}

	}
}
