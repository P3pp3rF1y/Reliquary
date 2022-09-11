package reliquary.data;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import reliquary.crafting.MobCharmRecipeBuilder;
import reliquary.crafting.NbtShapedRecipeBuilder;
import reliquary.crafting.PotionEffectsRecipeBuilder;
import reliquary.crafting.SpawnEggRecipeBuilder;
import reliquary.crafting.alkahestry.ChargingRecipeBuilder;
import reliquary.crafting.alkahestry.CraftingRecipeBuilder;
import reliquary.crafting.alkahestry.DrainRecipeBuilder;
import reliquary.crafting.conditions.AlkahestryEnabledCondition;
import reliquary.crafting.conditions.HandgunEnabledCondition;
import reliquary.crafting.conditions.MobDropsCraftableCondition;
import reliquary.crafting.conditions.PassivePedestalEnabledCondition;
import reliquary.crafting.conditions.PedestalEnabledCondition;
import reliquary.crafting.conditions.PotionsEnabledCondition;
import reliquary.crafting.conditions.SpawnEggEnabledCondition;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.items.BulletItem;
import reliquary.items.ItemBase;
import reliquary.items.MagazineItem;
import reliquary.reference.Reference;
import reliquary.util.RegistryHelper;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
	private static final TagKey<Item> INGOTS_COPPER = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:ingots/copper"));
	private static final TagKey<Item> INGOTS_STEEL = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:ingots/steel"));
	private static final TagKey<Item> INGOTS_SILVER = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:ingots/silver"));
	private static final TagKey<Item> INGOTS_TIN = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:ingots/tin"));
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
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		registerHandgunRecipes(consumer);
		registerAlkahestryRecipes(consumer);
		registerPotionRecipes(consumer);
		registerPedestalRecipes(consumer);

		ShapelessRecipeBuilder.shapeless(ModBlocks.ALKAHESTRY_ALTAR_ITEM.get())
				.requires(Tags.Items.OBSIDIAN)
				.requires(Items.REDSTONE_LAMP)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModBlocks.FERTILE_LILY_PAD_ITEM.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.requires(Items.LILY_PAD)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModBlocks.INTERDICTION_TORCH_ITEM.get())
				.requires(ModItems.BAT_WING.get())
				.requires(Tags.Items.RODS_BLAZE)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModBlocks.WRAITH_NODE_ITEM.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Tags.Items.GEMS_EMERALD)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		registerCraftableMobDropRecipes(consumer);
		registerCharmFragmentRecipes(consumer);
		registerIngredientRecipes(consumer);
		registerUncraftingRecipes(consumer);

		ShapedRecipeBuilder.shaped(ModItems.ANGELHEART_VIAL.get())
				.pattern("GBG")
				.pattern("GCG")
				.pattern("FGF")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.MILK_BUCKET)
				.define('C', ModItems.INFERNAL_CLAW.get())
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.ANGELIC_FEATHER.get())
				.requires(Tags.Items.FEATHERS)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.BAT_WING.get())
				.requires(ModItems.FERTILE_ESSENCE.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.DESTRUCTION_CATALYST.get())
				.requires(Items.FLINT_AND_STEEL)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.EMPEROR_CHALICE.get())
				.requires(Tags.Items.GEMS_EMERALD)
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Items.BUCKET)
				.requires(instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.ENDER_STAFF.get())
				.pattern(" BE")
				.pattern("NVB")
				.pattern("SN ")
				.define('B', ModItems.BAT_WING.get())
				.define('S', Items.STICK)
				.define('E', Items.ENDER_EYE)
				.define('V', instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.define('N', ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FORTUNE_COIN.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(ModItems.SLIME_PEARL.get())
				.requires(ModItems.BAT_WING.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.GLACIAL_STAFF.get())
				.requires(ModItems.ICE_MAGUS_ROD.get())
				.requires(instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.requires(ModItems.FROZEN_CORE.get())
				.requires(ModItems.SHEARS_OF_WINTER.get())
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.GLOWING_BREAD.get(), 3)
				.requires(Items.BREAD)
				.requires(Items.BREAD)
				.requires(Items.BREAD)
				.requires(ModItems.GLOWING_WATER.get())
				.unlockedBy("has_glowing_water", has(ModItems.GLOWING_WATER.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.GLOWING_WATER.get())
				.pattern("GBG")
				.pattern("GDG")
				.pattern("NGP")
				.define('G', Tags.Items.GLASS_PANES)
				.define('B', Items.WATER_BUCKET)
				.define('D', Tags.Items.DUSTS_GLOWSTONE)
				.define('P', Tags.Items.GUNPOWDER)
				.define('N', Tags.Items.CROPS_NETHER_WART)
				.unlockedBy("has_nether_wart", hasTag(Tags.Items.CROPS_NETHER_WART))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(Items.GOLD_NUGGET)
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.requires(ModItems.EMPTY_BULLET.get())
				.unlockedBy("has_empty_bullet", has(ModItems.EMPTY_BULLET.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, "gold_nugget"));

		ShapedRecipeBuilder.shaped(ModItems.HARVEST_ROD.get())
				.pattern(" RF")
				.pattern("VTR")
				.pattern("SV ")
				.define('R', Items.ROSE_BUSH)
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('V', Items.VINE)
				.define('T', instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.define('S', Items.STICK)
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.HERO_MEDALLION.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.FORTUNE_COIN.get())
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.unlockedBy("has_infernal_tear", has(ModItems.INFERNAL_TEAR.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.HOLY_HAND_GRENADE.get(), 4)
				.requires(ModItems.GLOWING_WATER.get())
				.requires(Tags.Items.NUGGETS_GOLD)
				.requires(Items.TNT)
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy("has_glowing_water", has(ModItems.GLOWING_WATER.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.ICE_MAGUS_ROD.get())
				.pattern(" DF")
				.pattern(" VD")
				.pattern("I  ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('F', ModItems.FROZEN_CORE.get())
				.define('V', instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.INFERNAL_CHALICE.get())
				.requires(ModItems.INFERNAL_CLAWS.get())
				.requires(ModItems.EMPEROR_CHALICE.get())
				.requires(ModItems.INFERNAL_TEAR.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy("has_emperor_chalice", has(ModItems.EMPEROR_CHALICE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.INFERNAL_CLAWS.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.INFERNAL_TEAR.get())
				.requires(instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.requires(ModItems.WITCH_HAT.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.KRAKEN_SHELL.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy("has_kraken_shell_fragment", has(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.LANTERN_OF_PARANOIA.get())
				.pattern("ISI")
				.pattern("GMG")
				.pattern(" I ")
				.define('S', ModItems.SLIME_PEARL.get())
				.define('G', Tags.Items.GLASS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('M', ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.MAGICBANE.get())
				.pattern("NG")
				.pattern("IN")
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('N', ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.MERCY_CROSS.get())
				.pattern("WGR")
				.pattern("GLG")
				.pattern("SGZ")
				.define('W', ModItems.WITHERED_RIB.get())
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', ModItems.RIB_BONE.get())
				.define('L', Tags.Items.LEATHER)
				.define('S', Items.WITHER_SKELETON_SKULL)
				.define('Z', ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.MIDAS_TOUCHSTONE.get())
				.requires(Items.ANVIL)
				.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
				.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer);

		MobCharmRecipeBuilder.charmRecipe()
				.patternLine("FLF")
				.patternLine("FSF")
				.patternLine("F F")
				.key('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.key('L', Tags.Items.LEATHER)
				.key('S', Tags.Items.STRING)
				.addCriterion(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.build(consumer);

		ShapedRecipeBuilder.shaped(ModItems.MOB_CHARM_BELT.get())
				.pattern("LLL")
				.pattern("F F")
				.pattern("FFF")
				.define('L', Tags.Items.LEATHER)
				.define('F', ModItems.MOB_CHARM_FRAGMENT.get())
				.unlockedBy(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.PHOENIX_DOWN.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELHEART_VIAL.get())
				.requires(ModItems.ANGELIC_FEATHER.get())
				.unlockedBy("has_angelic_feather", has(ModItems.ANGELIC_FEATHER.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.PYROMANCER_STAFF.get())
				.requires(ModItems.INFERNAL_CLAWS.get())
				.requires(Tags.Items.RODS_BLAZE)
				.requires(ModItems.INFERNAL_TEAR.get())
				.requires(ModItems.SALAMANDER_EYE.get())
				.unlockedBy("has_infernal_claws", has(ModItems.INFERNAL_CLAWS.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.RENDING_GALE.get())
				.pattern(" BE")
				.pattern("GVB")
				.pattern("SG ")
				.define('B', ModItems.BAT_WING.get())
				.define('S', Items.STICK)
				.define('E', ModItems.EYE_OF_THE_STORM.get())
				.define('V', instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_eye_of_the_storm", has(ModItems.EYE_OF_THE_STORM.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.ROD_OF_LYSSA.get())
				.requires(ModItems.INFERNAL_CLAW.get())
				.requires(ModItems.BAT_WING.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(Items.FISHING_ROD)
				.unlockedBy(HAS_INFERNAL_CLAW_CRITERION, has(ModItems.INFERNAL_CLAW.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.SALAMANDER_EYE.get())
				.requires(Items.ENDER_EYE)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.SERPENT_STAFF.get())
				.pattern(" CE")
				.pattern(" KC")
				.pattern("S  ")
				.define('S', Items.STICK)
				.define('C', ModItems.CHELICERAE.get())
				.define('E', Items.ENDER_EYE)
				.define('K', ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.unlockedBy("has_kraken_shell_fragment", has(ModItems.KRAKEN_SHELL_FRAGMENT.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.SHEARS_OF_WINTER.get())
				.requires(ModItems.FROZEN_CORE.get())
				.requires(Items.SHEARS)
				.requires(Tags.Items.GEMS_DIAMOND)
				.requires(Tags.Items.GEMS_DIAMOND)
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.SOJOURNER_STAFF.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Tags.Items.RODS_BLAZE)
				.requires(instantiateNBTIngredient(new ItemStack(ModItems.VOID_TEAR.get())))
				.unlockedBy(HAS_VOID_TEAR_CRITERION, has(ModItems.VOID_TEAR.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.TWILIGHT_CLOAK.get())
				.pattern("ICI")
				.pattern("BCB")
				.pattern("BCB")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Items.BLACK_WOOL)
				.define('C', ModItems.CRIMSON_CLOTH.get())
				.unlockedBy("has_crimson_cloth", has(ModItems.CRIMSON_CLOTH.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.VOID_TEAR.get())
				.requires(Items.GHAST_TEAR)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.SLIME_PEARL.get())
				.requires(Tags.Items.GEMS_LAPIS)
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.WITHERLESS_ROSE.get())
				.pattern("FNF")
				.pattern("NRN")
				.pattern("FNF")
				.define('F', ModItems.FERTILE_ESSENCE.get())
				.define('N', Tags.Items.NETHER_STARS)
				.define('R', Items.ROSE_BUSH)
				.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
				.save(consumer);
	}

	private void registerUncraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(Items.BLAZE_ROD, 4)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "blaze_rod"));

		ShapelessRecipeBuilder.shapeless(Items.BONE, 5)
				.requires(ModItems.RIB_BONE.get())
				.unlockedBy("has_rib_bone", has(ModItems.RIB_BONE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "bone"));

		ShapelessRecipeBuilder.shapeless(Items.ENDER_PEARL, 3)
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "ender_pearl"));

		ShapelessRecipeBuilder.shapeless(Items.GHAST_TEAR)
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "ghast_tear"));

		ShapedRecipeBuilder.shaped(Items.GLASS_BOTTLE, 6)
				.pattern("W W")
				.pattern(" W ")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "glass_bottle"));

		ShapedRecipeBuilder.shaped(Items.GLOWSTONE_DUST, 6)
				.pattern("W  ")
				.pattern("W  ")
				.pattern(" W ")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "glowstone_dust"));

		ShapelessRecipeBuilder.shapeless(Items.GOLD_NUGGET, 6)
				.requires(ModItems.ZOMBIE_HEART.get())
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gold_nugget"));

		ShapelessRecipeBuilder.shapeless(Items.GUNPOWDER, 6)
				.requires(ModItems.CATALYZING_GLAND.get())
				.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gunpowder_creeper_gland"));

		ShapelessRecipeBuilder.shapeless(Items.GUNPOWDER, 10)
				.requires(ModItems.EYE_OF_THE_STORM.get())
				.unlockedBy("has_eye_of_the_storm", has(ModItems.EYE_OF_THE_STORM.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gunpowder_storm_eye"));

		ShapedRecipeBuilder.shaped(Items.GUNPOWDER, 6)
				.pattern("W  ")
				.pattern(" W ")
				.pattern("  W")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "gunpowder_witch_hat"));

		ShapelessRecipeBuilder.shapeless(Items.INK_SAC, 6)
				.requires(ModItems.SQUID_BEAK.get())
				.unlockedBy("has_squid_beak", has(ModItems.SQUID_BEAK.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "ink_sac"));

		ShapelessRecipeBuilder.shapeless(Items.MAGMA_CREAM, 3)
				.requires(ModItems.MOLTEN_CORE.get())
				.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "magma_cream"));

		ShapedRecipeBuilder.shaped(Items.PACKED_ICE)
				.pattern("III")
				.pattern("ICI")
				.pattern("III")
				.define('I', Items.ICE)
				.define('C', ModItems.FROZEN_CORE.get())
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "packed_ice"));

		ShapelessRecipeBuilder.shapeless(Items.PRISMARINE_CRYSTALS, 10)
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "prismarine_crystals"));

		ShapelessRecipeBuilder.shapeless(Items.PRISMARINE_SHARD, 5)
				.requires(ModItems.GUARDIAN_SPIKE.get())
				.unlockedBy(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "prismarine_shard"));

		ShapedRecipeBuilder.shaped(Items.REDSTONE, 6)
				.pattern("W")
				.pattern("W")
				.pattern("W")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "redstone"));

		ShapelessRecipeBuilder.shapeless(Items.ROTTEN_FLESH, 6)
				.requires(ModItems.ZOMBIE_HEART.get())
				.unlockedBy(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "rotten_flesh"));

		ShapelessRecipeBuilder.shapeless(Items.SLIME_BALL, 6)
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "slime_ball"));

		ShapelessRecipeBuilder.shapeless(Items.SNOWBALL, 5)
				.requires(ModItems.FROZEN_CORE.get())
				.unlockedBy(HAS_FROZEN_CORE_CRITERION, has(ModItems.FROZEN_CORE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "snowball"));

		ShapelessRecipeBuilder.shapeless(Items.SPIDER_EYE, 2)
				.requires(ModItems.CHELICERAE.get())
				.requires(ModItems.CHELICERAE.get())
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "spider_eye"));

		ShapelessRecipeBuilder.shapeless(Items.STICK, 4)
				.requires(ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "stick"));

		ShapelessRecipeBuilder.shapeless(Items.STRING, 6)
				.requires(ModItems.CHELICERAE.get())
				.unlockedBy(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "string"));

		ShapedRecipeBuilder.shaped(Items.SUGAR, 6)
				.pattern("WWW")
				.define('W', ModItems.WITCH_HAT.get())
				.unlockedBy(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "sugar"));

		ShapelessRecipeBuilder.shapeless(Items.WITHER_SKELETON_SKULL)
				.requires(ModItems.WITHERED_RIB.get())
				.requires(ModItems.WITHERED_RIB.get())
				.requires(ModItems.WITHERED_RIB.get())
				.requires(Items.SKELETON_SKULL)
				.unlockedBy(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.save(consumer, new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "wither_skeleton_skull"));

		ResourceLocation spawnEggId = new ResourceLocation(Reference.MOD_ID, UNCRAFTING_FOLDER + "spawn_egg");
		ConditionalRecipe.builder()
				.addCondition(new SpawnEggEnabledCondition())
				.addRecipe(conditionalConsumer ->
						SpawnEggRecipeBuilder.spawnEggRecipe()
								.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
								.addIngredient(ModItems.MOB_CHARM_FRAGMENT.get())
								.addIngredient(Items.EGG)
								.addCriterion(HAS_MOB_CHARM_FRAGMENT_CRITERION, has(ModItems.MOB_CHARM_FRAGMENT.get()))
								.build(conditionalConsumer, spawnEggId))
				.build(consumer, spawnEggId);
	}

	private void registerHandgunRecipes(Consumer<FinishedRecipe> consumer) {
		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.HANDGUN.get())
								.pattern("BIM")
								.pattern("ISI")
								.pattern("IGI")
								.define('I', Tags.Items.INGOTS_IRON)
								.define('B', ModItems.BARREL_ASSEMBLY.get())
								.define('M', ModItems.HAMMER_ASSEMBLY.get())
								.define('G', ModItems.GRIP_ASSEMBLY.get())
								.define('S', ModItems.SLIME_PEARL.get())
								.unlockedBy("has_barrel_assembly", has(ModItems.BARREL_ASSEMBLY.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.HANDGUN.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.BARREL_ASSEMBLY.get())
								.pattern("III")
								.pattern("EME")
								.pattern("III")
								.define('I', Tags.Items.INGOTS_IRON)
								.define('E', ModItems.NEBULOUS_HEART.get())
								.define('M', Items.MAGMA_CREAM)
								.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.BARREL_ASSEMBLY.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.GRIP_ASSEMBLY.get())
								.pattern("III")
								.pattern("IMI")
								.pattern("ICI")
								.define('I', Tags.Items.INGOTS_IRON)
								.define('C', ModItems.EMPTY_MAGAZINE.get())
								.define('M', Items.MAGMA_CREAM)
								.unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.GRIP_ASSEMBLY.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.EMPTY_MAGAZINE.get())
								.pattern("I I")
								.pattern("IGI")
								.pattern("SIS")
								.define('S', Tags.Items.STONE)
								.define('I', Tags.Items.INGOTS_IRON)
								.define('G', Tags.Items.GLASS)
								.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.EMPTY_MAGAZINE.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.HAMMER_ASSEMBLY.get())
								.pattern("IIB")
								.pattern("RMI")
								.pattern("III")
								.define('I', Tags.Items.INGOTS_IRON)
								.define('B', Items.STONE_BUTTON)
								.define('R', Tags.Items.RODS_BLAZE)
								.define('M', ModItems.MOLTEN_CORE.get())
								.unlockedBy(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.HAMMER_ASSEMBLY.get()));

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.BLAZE_BULLET.get(), 8)
								.requires(Items.BLAZE_POWDER)
								.requires(Tags.Items.RODS_BLAZE)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.NUGGETS_GOLD)
								.unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.BLAZE_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.BLAZE_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.BUSTER_BULLET.get(), 8)
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
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.BUSTER_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.BUSTER_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.CONCUSSIVE_BULLET.get(), 8)
								.requires(Tags.Items.SLIMEBALLS)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.GUNPOWDER)
								.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.CONCUSSIVE_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.CONCUSSIVE_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.ENDER_BULLET.get(), 8)
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
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.ENDER_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.ENDER_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.EXORCISM_BULLET.get(), 8)
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
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.EXORCISM_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.EXORCISM_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.NEUTRAL_BULLET.get(), 8)
								.requires(Items.FLINT)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.GUNPOWDER)
								.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.NEUTRAL_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.NEUTRAL_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.SAND_BULLET.get(), 8)
								.requires(Tags.Items.SANDSTONE)
								.requires(Tags.Items.SLIMEBALLS)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.GUNPOWDER)
								.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.SAND_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.SAND_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.SEEKER_BULLET.get(), 8)
								.requires(Tags.Items.GEMS_LAPIS)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.GUNPOWDER)
								.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.SEEKER_BULLET.get()));

		addBulletPotionRecipe(consumer, ModItems.SEEKER_BULLET.get());

		ConditionalRecipe.builder()
				.addCondition(new HandgunEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.STORM_BULLET.get(), 8)
								.requires(ModItems.CATALYZING_GLAND.get())
								.requires(ModItems.CATALYZING_GLAND.get())
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.NUGGETS_GOLD)
								.requires(Tags.Items.GUNPOWDER)
								.unlockedBy(HAS_GUNPOWDER_CRITERION, has(Items.GUNPOWDER))
								.save(conditionalConsumer))
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

	private void registerAlkahestryRecipes(Consumer<FinishedRecipe> consumer) {
		ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapelessRecipeBuilder.shapeless(ModItems.ALKAHESTRY_TOME.get())
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
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.ALKAHESTRY_TOME.get()));

		ChargingRecipeBuilder.chargingRecipe(Items.GLOWSTONE_DUST, 1).build(consumer, RegistryHelper.getRegistryName(Items.GLOWSTONE_DUST));
		ChargingRecipeBuilder.chargingRecipe(Items.GLOWSTONE, 4).build(consumer, RegistryHelper.getRegistryName(Items.GLOWSTONE));
		ChargingRecipeBuilder.chargingRecipe(Items.REDSTONE, 1).build(consumer, RegistryHelper.getRegistryName(Items.REDSTONE));
		ChargingRecipeBuilder.chargingRecipe(Items.REDSTONE_BLOCK, 9).build(consumer, RegistryHelper.getRegistryName(Items.REDSTONE_BLOCK));

		DrainRecipeBuilder.drainRecipe(Items.REDSTONE, 1).build(consumer, RegistryHelper.getRegistryName(Items.REDSTONE));

		CraftingRecipeBuilder.craftingRecipe(Items.CHARCOAL, 4, 5).build(consumer, RegistryHelper.getRegistryName(Items.CHARCOAL));
		CraftingRecipeBuilder.craftingRecipe(Items.CLAY, 4, 3).build(consumer, RegistryHelper.getRegistryName(Items.CLAY));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_COPPER, 8, 5)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_COPPER.location())))
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
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_SILVER.location())))
				.build(consumer, new ResourceLocation("silver_ingot"));
		CraftingRecipeBuilder.craftingRecipe(Items.SOUL_SAND, 8, 9).build(consumer, RegistryHelper.getRegistryName(Items.SOUL_SAND));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_STEEL, 32, 2)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_STEEL.location())))
				.build(consumer, new ResourceLocation("steel_ingot"));
		CraftingRecipeBuilder.craftingRecipe(INGOTS_TIN, 32, 2)
				.addCondition(new NotCondition(new TagEmptyCondition(INGOTS_TIN.location())))
				.build(consumer, new ResourceLocation("tin_ingot"));
	}

	private void registerPotionRecipes(Consumer<FinishedRecipe> consumer) {
		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.EMPTY_POTION_VIAL.get())
								.pattern("G G")
								.pattern("G G")
								.pattern(" G ")
								.define('G', Tags.Items.GLASS_PANES)
								.unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.EMPTY_POTION_VIAL.get()));

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.APHRODITE_POTION.get())
								.pattern("GBG")
								.pattern("GFG")
								.pattern("RGC")
								.define('G', Tags.Items.GLASS_PANES)
								.define('B', Items.WATER_BUCKET)
								.define('F', ModItems.FERTILE_ESSENCE.get())
								.define('R', Tags.Items.DYES_RED)
								.define('C', Items.COCOA_BEANS)
								.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.APHRODITE_POTION.get()));

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModItems.FERTILE_POTION.get())
								.pattern("GBG")
								.pattern("GFG")
								.pattern("CGY")
								.define('G', Tags.Items.GLASS_PANES)
								.define('B', Items.WATER_BUCKET)
								.define('F', ModItems.FERTILE_ESSENCE.get())
								.define('C', Tags.Items.DYES_GREEN)
								.define('Y', Tags.Items.DYES_YELLOW)
								.unlockedBy(HAS_FERTILE_ESSENCE_CRITERION, has(ModItems.FERTILE_ESSENCE.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModItems.FERTILE_POTION.get()));

		addItemPotionRecipe(consumer, Items.ARROW, ModItems.TIPPED_ARROW.get(), 0.125f, 'A', false);

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModBlocks.APOTHECARY_CAULDRON_ITEM.get())
								.pattern("GNG")
								.pattern("ICI")
								.pattern("NMN")
								.define('G', ModItems.CATALYZING_GLAND.get())
								.define('N', ModItems.NEBULOUS_HEART.get())
								.define('I', ModItems.INFERNAL_CLAW.get())
								.define('C', Items.CAULDRON)
								.define('M', ModItems.MOLTEN_CORE.get())
								.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModBlocks.APOTHECARY_CAULDRON_ITEM.get()));

		ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(ModBlocks.APOTHECARY_MORTAR_ITEM.get())
								.pattern("GNG")
								.pattern("NGN")
								.pattern("NNN")
								.define('G', ModItems.CATALYZING_GLAND.get())
								.define('N', Tags.Items.STORAGE_BLOCKS_QUARTZ)
								.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(ModBlocks.APOTHECARY_MORTAR_ITEM.get()));
	}

	private void registerPedestalRecipes(Consumer<FinishedRecipe> consumer) {
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

	private void registerCraftableMobDropRecipes(Consumer<FinishedRecipe> consumer) {
		addCraftableMobDropRecipe(consumer, ModItems.BAT_WING.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GFG")
						.pattern("GGG")
						.define('F', Tags.Items.FEATHERS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_feather", hasTag(Tags.Items.FEATHERS))
		);

		addCraftableMobDropRecipe(consumer, ModItems.CATALYZING_GLAND.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GPG")
						.pattern("GGG")
						.define('P', Tags.Items.GUNPOWDER)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy(HAS_GUNPOWDER_CRITERION, hasTag(Tags.Items.GUNPOWDER))
		);

		addCraftableMobDropRecipe(consumer, ModItems.CHELICERAE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GSG")
						.pattern("GGG")
						.define('S', Tags.Items.STRING)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_string", hasTag(Tags.Items.STRING))
		);

		addCraftableMobDropRecipe(consumer, ModItems.FROZEN_CORE.get(), builder ->
				builder
						.pattern("GPG")
						.pattern("GSG")
						.pattern("GSG")
						.define('P', Items.PUMPKIN)
						.define('S', Items.SNOW)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_snow", has(Items.SNOW))
		);

		addCraftableMobDropRecipe(consumer, ModItems.GUARDIAN_SPIKE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GPG")
						.pattern("GGG")
						.define('P', Tags.Items.DUSTS_PRISMARINE)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_prismarine", hasTag(Tags.Items.DUSTS_PRISMARINE))
		);

		addCraftableMobDropRecipe(consumer, ModItems.MOLTEN_CORE.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GMG")
						.pattern("GGG")
						.define('M', Items.MAGMA_CREAM)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
		);

		addCraftableMobDropRecipe(consumer, ModItems.NEBULOUS_HEART.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GEG")
						.pattern("GGG")
						.define('E', Tags.Items.ENDER_PEARLS)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_ender_pearl", hasTag(Tags.Items.ENDER_PEARLS))
		);

		addCraftableMobDropRecipe(consumer, ModItems.RIB_BONE.get(), builder ->
				builder
						.pattern("III")
						.pattern("IBI")
						.pattern("III")
						.define('B', Tags.Items.BONES)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_bone", hasTag(Tags.Items.BONES))
		);

		addCraftableMobDropRecipe(consumer, ModItems.SLIME_PEARL.get(), builder ->
				builder
						.pattern("III")
						.pattern("ISI")
						.pattern("III")
						.define('S', Tags.Items.SLIMEBALLS)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_slimeball", hasTag(Tags.Items.SLIMEBALLS))
		);

		addCraftableMobDropRecipe(consumer, ModItems.SQUID_BEAK.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GIG")
						.pattern("GGG")
						.define('I', Items.INK_SAC)
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy("has_ink_sac", has(Items.INK_SAC))
		);

		addCraftableMobDropRecipe(consumer, ModItems.EYE_OF_THE_STORM.get(), builder ->
				builder
						.pattern("GGG")
						.pattern("GCG")
						.pattern("GGG")
						.define('C', ModItems.CATALYZING_GLAND.get())
						.define('G', Tags.Items.INGOTS_GOLD)
						.unlockedBy(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
		);

		addCraftableMobDropRecipe(consumer, ModItems.WITHERED_RIB.get(), builder ->
				builder
						.pattern("D D")
						.pattern(" S ")
						.pattern("D D")
						.define('S', Items.SKELETON_SKULL)
						.define('D', Tags.Items.GEMS_DIAMOND)
						.unlockedBy("has_skeleton_skull", has(Items.SKELETON_SKULL))
		);

		addCraftableMobDropRecipe(consumer, ModItems.ZOMBIE_HEART.get(), builder ->
				builder
						.pattern("III")
						.pattern("IFI")
						.pattern("III")
						.define('F', Items.ROTTEN_FLESH)
						.define('I', Tags.Items.INGOTS_IRON)
						.unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
		);
	}

	private void registerIngredientRecipes(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(ModItems.CRIMSON_CLOTH.get())
				.requires(Items.RED_WOOL)
				.requires(Items.BLACK_WOOL)
				.requires(ModItems.NEBULOUS_HEART.get())
				.requires(ModItems.NEBULOUS_HEART.get())
				.unlockedBy(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.FERTILE_ESSENCE.get())
				.requires(ModItems.RIB_BONE.get())
				.requires(ModItems.CATALYZING_GLAND.get())
				.requires(Tags.Items.DYES_GREEN)
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.INFERNAL_CLAW.get())
				.requires(Tags.Items.LEATHER)
				.requires(ModItems.MOLTEN_CORE.get())
				.requires(ModItems.RIB_BONE.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(ModItems.KRAKEN_SHELL_FRAGMENT.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SQUID_BEAK.get())
				.requires(ModItems.SLIME_PEARL.get())
				.unlockedBy("has_squid_beak", has(ModItems.SQUID_BEAK.get()))
				.save(consumer);
	}

	private void registerCharmFragmentRecipes(Consumer<FinishedRecipe> consumer) {
		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:blaze"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.MOLTEN_CORE.get())
				.key('S', Tags.Items.RODS_BLAZE)
				.key('T', Items.BLAZE_POWDER)
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "blaze"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:cave_spider"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.CHELICERAE.get())
				.key('S', Tags.Items.STRING)
				.key('T', instantiateNBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON)))
				.addCriterion(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "cave_spider"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:creeper"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.CATALYZING_GLAND.get())
				.key('S', Tags.Items.GUNPOWDER)
				.key('T', Items.BONE)
				.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "creeper"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:enderman"))
				.patternLine("PPP")
				.patternLine("SPS")
				.patternLine("PPP")
				.key('P', ModItems.NEBULOUS_HEART.get())
				.key('S', Tags.Items.ENDER_PEARLS)
				.addCriterion(HAS_NEBULOUS_HEART_CRITERION, has(ModItems.NEBULOUS_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "enderman"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:ghast"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', Items.GHAST_TEAR)
				.key('S', Tags.Items.GUNPOWDER)
				.key('T', ModItems.CATALYZING_GLAND.get())
				.addCriterion(HAS_CATALYZING_GLAND_CRITERIION, has(ModItems.CATALYZING_GLAND.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "ghast"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:guardian"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.GUARDIAN_SPIKE.get())
				.key('S', Tags.Items.DUSTS_PRISMARINE)
				.key('T', Items.COD)
				.addCriterion(HAS_GUARDIAN_SPIKE_CRITERION, has(ModItems.GUARDIAN_SPIKE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "guardian"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:magma_cube"))
				.patternLine("PPP")
				.patternLine("SSS")
				.patternLine("PPP")
				.key('P', ModItems.MOLTEN_CORE.get())
				.key('S', Items.MAGMA_CREAM)
				.addCriterion(HAS_MOLTEN_CORE_CRITERION, has(ModItems.MOLTEN_CORE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "magma_cube"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:skeleton"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.RIB_BONE.get())
				.key('S', Items.BONE)
				.key('T', Items.FLINT)
				.addCriterion("has_rib_bone", has(ModItems.RIB_BONE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "skeleton"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:slime"))
				.patternLine("PPP")
				.patternLine("SSS")
				.patternLine("PPP")
				.key('P', ModItems.SLIME_PEARL.get())
				.key('S', Tags.Items.SLIMEBALLS)
				.addCriterion(HAS_SLIME_PEARL_CRITERION, has(ModItems.SLIME_PEARL.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "slime"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:spider"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.CHELICERAE.get())
				.key('S', Tags.Items.STRING)
				.key('T', Items.SPIDER_EYE)
				.addCriterion(HAS_CHELICERAE_CRITERION, has(ModItems.CHELICERAE.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "spider"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:witch"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.WITCH_HAT.get())
				.key('S', Items.GLASS_BOTTLE)
				.key('T', Items.SPIDER_EYE)
				.addCriterion(HAS_WITCH_HAT_CRITERION, has(ModItems.WITCH_HAT.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "witch"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:wither_skeleton"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.WITHERED_RIB.get())
				.key('S', Items.BONE)
				.key('T', Items.WITHER_SKELETON_SKULL)
				.addCriterion(HAS_WITHERED_RIB_CRITERION, has(ModItems.WITHERED_RIB.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "wither_skeleton"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:zombie"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.ZOMBIE_HEART.get())
				.key('S', Items.ROTTEN_FLESH)
				.key('T', Items.BONE)
				.addCriterion(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "zombie"));

		NbtShapedRecipeBuilder.shapedRecipe(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor("minecraft:zombified_piglin"))
				.patternLine("PPP")
				.patternLine("STS")
				.patternLine("PPP")
				.key('P', ModItems.ZOMBIE_HEART.get())
				.key('S', Items.ROTTEN_FLESH)
				.key('T', Items.GOLDEN_SWORD)
				.addCriterion(HAS_ZOMBIE_HEART_CRITERION, has(ModItems.ZOMBIE_HEART.get()))
				.build(consumer, new ResourceLocation(Reference.MOD_ID, MOB_CHARM_FRAGMENTS_FOLDER + "zombified_piglin"));
	}

	private InventoryChangeTrigger.TriggerInstance hasTag(TagKey<Item> tag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
	}

	private StrictNBTIngredient instantiateNBTIngredient(ItemStack stack) {
		return StrictNBTIngredient.of(stack);
	}

	private void addCraftableMobDropRecipe(Consumer<FinishedRecipe> consumer, ItemBase item, Consumer<ShapedRecipeBuilder> setRecipe) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(item);
		setRecipe.accept(builder);

		ConditionalRecipe.builder()
				.addCondition(new MobDropsCraftableCondition())
				.addRecipe(builder::save)
				.build(consumer, RegistryHelper.getRegistryName(item));
	}

	private void addPedestalRecipe(Consumer<FinishedRecipe> consumer, BlockItem passivePedestalItem, BlockItem pedestalItem) {
		ConditionalRecipe.builder()
				.addCondition(new PedestalEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(pedestalItem)
								.pattern("D D")
								.pattern(" P ")
								.pattern("D D")
								.define('D', Tags.Items.GEMS_DIAMOND)
								.define('P', passivePedestalItem)
								.unlockedBy("has_passive_pedestal", has(ModBlocks.PASSIVE_PEDESTAL_ITEMS.get(DyeColor.WHITE).get()))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(pedestalItem));
	}

	private void addPassivePedestalRecipe(Consumer<FinishedRecipe> consumer, Item carpetItem, BlockItem pedestalItem) {
		ConditionalRecipe.builder()
				.addCondition(new PassivePedestalEnabledCondition())
				.addRecipe(conditionalConsumer ->
						ShapedRecipeBuilder.shaped(pedestalItem)
								.pattern(" C ")
								.pattern("GQG")
								.pattern("SSS")
								.define('C', carpetItem)
								.define('G', Tags.Items.NUGGETS_GOLD)
								.define('Q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
								.define('S', Items.QUARTZ_SLAB)
								.unlockedBy("has_quartz_block", has(Items.QUARTZ_BLOCK))
								.save(conditionalConsumer))
				.build(consumer, RegistryHelper.getRegistryName(pedestalItem));
	}

	private void addBulletPotionRecipe(Consumer<FinishedRecipe> consumer, Item item) {
		addItemPotionRecipe(consumer, item, item, (float) 0.2, 'B', true);
	}

	private void addItemPotionRecipe(Consumer<FinishedRecipe> consumer, Item itemIngredient, Item item, float durationFactor, char itemKey, boolean includeSuffix) {
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
				.addCriterion("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), has(item))
				.build(consumer, id);
	}

	private void addMagazineRecipe(Consumer<FinishedRecipe> consumer, BulletItem bulletItem, MagazineItem magazineItem) {
		String path = RegistryHelper.getRegistryName(bulletItem).getPath();
		PotionEffectsRecipeBuilder.potionEffectsRecipe(magazineItem, 1, 1)
				.addCondition(new HandgunEnabledCondition())
				.patternLine("BBB")
				.patternLine("BMB")
				.patternLine("BBB")
				.key('B', bulletItem)
				.key('M', ModItems.EMPTY_MAGAZINE.get())
				.addCriterion("has_" + (path.lastIndexOf('/') > -1 ? path.substring(path.indexOf('/') + 1) : path), has(bulletItem))
				.build(consumer, RegistryHelper.getRegistryName(magazineItem));
	}
}
