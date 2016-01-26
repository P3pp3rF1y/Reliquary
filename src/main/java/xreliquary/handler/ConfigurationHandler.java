package xreliquary.handler;


import com.google.common.collect.ImmutableList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ConfigurationHandler
{
	public static Configuration configuration;

	public static void init(File configFile)
	{
		if (configuration == null)
		{
			configuration = new Configuration(configFile, true);
			loadConfiguration();
		}
	}

	private static void loadConfiguration()
	{
		loadHudPositions();
		loadEasyModeSettings();
		loadMobDropProbabilities();
		loadBlockAndItemSettings();

		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}

	private static void loadBlockAndItemSettings() {
		int itemCap = 9999;
		int cleanShortMax = 30000;
		int cleanIntMax = 2000000000;

		//alkahestry tome configs
		Settings.AlkahestryTome.redstoneLimit = getInt("redstone_limit", Names.alkahestry_tome, 250, 0, itemCap);

		//altar configs
		Settings.Altar.redstoneCost = getInt("redstone_cost", Names.altar, 3, 0, 10);
		Settings.Altar.timeInMinutes = getInt("time_in_minutes", Names.altar, 20, 0, 60);
		Settings.Altar.maximumTimeVarianceInMinutes = getInt("maximum_time_variance_in_minutes", Names.altar, 5, 0, 15);
		Settings.Altar.outputLightLevelWhileActive = getInt("output_light_level_while_active", Names.altar, 16, 16, 0);

		//angelic feather configs
		Settings.AngelicFeather.hungerCostPercent = getInt("hunger_cost_percent", Names.angelic_feather, 50, 0, 100);
		Settings.AngelicFeather.leapingPotency = getInt("leaping_potency", Names.angelic_feather, 1, 0, 5);

		//angelheart vial configs
		Settings.AngelHeartVial.healPercentageOfMaxLife = getInt("heal_percentage_of_max_life", Names.angelheart_vial, 25, 0, 100);
		Settings.AngelHeartVial.removeNegativeStatus = getBoolean("remove_negative_status", Names.angelheart_vial, true);

		//apothecary cauldron configs
		List<String> heatSources = ImmutableList.of();
		Settings.ApothecaryCauldron.redstoneLimit = getInt("redstone_limit", Names.apothecary_cauldron, 5, 0, 100);
		Settings.ApothecaryCauldron.cookTime = getInt("cook_time", Names.apothecary_cauldron, 160, 20, 32000);
		Settings.ApothecaryCauldron.heatSources = getStringList("heat_sources", Names.apothecary_cauldron, heatSources);

		//destruction catalyst configs
		Settings.DestructionCatalyst.mundaneBlocks = getStringList("mundane_blocks", Names.destruction_catalyst, new ArrayList<String>(ItemDestructionCatalyst.ids));
		Settings.DestructionCatalyst.gunpowderCost = getInt("gunpowder_cost", Names.destruction_catalyst, 3, 0, 10);
		Settings.DestructionCatalyst.gunpowderWorth = getInt("gunpowder_worth", Names.destruction_catalyst, 1, 0, 3);
		Settings.DestructionCatalyst.gunpowderLimit = getInt("gunpowder_limit", Names.destruction_catalyst, 250, 0, itemCap);
		Settings.DestructionCatalyst.explosionRadius = getInt("explosion_radius", Names.destruction_catalyst, 1, 1, 5);
		Settings.DestructionCatalyst.centeredExplosion = getBoolean("centered_explosion", Names.destruction_catalyst, false);
		Settings.DestructionCatalyst.perfectCube = getBoolean("perfect_cube", Names.destruction_catalyst, true);

		//emperor's chalice configs
		Settings.EmperorChalice.hungerSatiationMultiplier = getInt("hunger_satiation_multiplier", Names.emperor_chalice, 4, 0, 10);

		//ender staff configs
		Settings.EnderStaff.enderPearlCastCost = getInt("ender_pearl_cast_cost", Names.ender_staff, 1, 0, 3);
		Settings.EnderStaff.enderPearlNodeWarpCost = getInt("ender_pearl_node_warp_cost",Names.ender_staff,  1, 0, 3);
		Settings.EnderStaff.enderPearlWorth = getInt("ender_pearl_worth", Names.ender_staff, 1, 0, 10);
		Settings.EnderStaff.enderPearlLimit = getInt("ender_pearl_limit", Names.ender_staff, 250, 0, itemCap);
		Settings.EnderStaff.nodeWarpCastTime = getInt("node_warp_cast_time", Names.ender_staff, 60, 10, 120);

		//fortune coin configs
		Settings.FortuneCoin.disableAudio = getBoolean("disable_audio", Names.fortune_coin, false);
		Settings.FortuneCoin.standardPullDistance = getInt("standard_pull_distance", Names.fortune_coin, 5, 3, 10);
		Settings.FortuneCoin.longRangePullDistance = getInt("long_range_pull_distance", Names.fortune_coin, 15, 9, 30);

		//glacial staff configs
		Settings.GlacialStaff.snowballLimit = getInt("snowball_limit", Names.glacial_staff, 250, 0, itemCap);
		Settings.GlacialStaff.snowballCost = getInt("snowball_cost", Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballWorth = getInt("snowball_worth", Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballDamage = getInt("snowball_damage", Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusFireImmune = getInt("snowball_damage_bonus_fire_immune", Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusBlaze = getInt("snowball_damage_bonus_blaze", Names.glacial_staff, 6, 0, 12);

		//harvest rod configs
		Settings.HarvestRod.bonemealLimit = getInt("bonemeal_limit", Names.harvest_rod, 250, 0, itemCap);
		Settings.HarvestRod.bonemealCost = getInt("bonemeal_cost", Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealWorth = getInt("bonemeal_worth", Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealLuckPercentChance = getInt("bonemeal_luck_percent_chance", Names.harvest_rod, 33, 1, 100);
		Settings.HarvestRod.bonemealLuckRolls = getInt("bonemeal_luck_rolls", Names.harvest_rod, 2, 0, 7);
		Settings.HarvestRod.harvestBreakRadius = getInt("harvest_break_radius", Names.harvest_rod, 2, 0, 5);

		//hero's medallion config
		Settings.HeroMedallion.experienceLevelMaximum = getInt("experience_level_maximum", Names.hero_medallion, 30, 0, 60);
		Settings.HeroMedallion.experienceLevelMinimum = getInt("experience_level_minimum", Names.hero_medallion, 0, 0, 30);
		Settings.HeroMedallion.experienceLimit =getInt("experience_limit", Names.hero_medallion, cleanIntMax, 0, cleanIntMax);

		//ice rod configs
		Settings.IceMagusRod.snowballLimit = getInt("snowball_limit", Names.ice_magus_rod, 250, 0, itemCap);
		Settings.IceMagusRod.snowballCost = getInt("snowball_cost", Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballWorth = getInt("snowball_worth", Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballDamage = getInt("snowball_damage", Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusFireImmune = getInt("snowball_damage_bonus_fire_immune", Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusBlaze = getInt("snowball_damage_bonus_blaze", Names.ice_magus_rod, 4, 0, 8);

		//infernal claws configs
		Settings.InfernalClaws.hungerCostPercent = getInt("hunger_cost_percent", Names.infernal_claws, 10, 0, 30);

		//infernal chalice configs
		Settings.InfernalChalice.hungerCostPercent = getInt("hunger_cost_percent", Names.infernal_chalice, 5, 0, 10);
		Settings.InfernalChalice.fluidLimit = getInt("fluid_limit", Names.infernal_chalice, 500000, 0, cleanIntMax);

		//interdiction torch configs
		//see post init for entity configs
		Settings.InterdictionTorch.pushRadius = getInt("push_radius", Names.interdiction_torch, 5, 1, 15);
		Settings.InterdictionTorch.canPushProjectiles = getBoolean("can_push_projectiles", Names.interdiction_torch, false);
		Settings.InterdictionTorch.entitiesThatCanBePushed = getStringList("entities_that_can_be_pushed", Names.interdiction_torch, ImmutableList.of());
		Settings.InterdictionTorch.projectilesThatCanBePushed = getStringList("projectiles_that_can_be_pushed", Names.interdiction_torch, ImmutableList.of());

		//kraken shell configs
		Settings.KrakenShell.hungerCostPercent = getInt("hunger_cost_percent", Names.kraken_shell, 25, 0, 50);

		//lantern of paranoia configs
		Settings.LanternOfParanoia.minLightLevel = getInt("min_light_level", Names.lantern_of_paranoia, 8, 0, 15);
		Settings.LanternOfParanoia.placementScanRadius = getInt("placement_scan_radius", Names.lantern_of_paranoia, 6, 1, 15);
		//Reliquary.CONFIG.require("only_place_on_visible_blocks", Names.lantern_of_paranoia, false);

		//fertile_lilypad of fertility configs
		Settings.FertileLilypad.secondsBetweenGrowthTicks = getInt("seconds_between_growth_ticks", Names.fertile_lilypad, 47, 1, 150);
		Settings.FertileLilypad.tileRange = getInt("tile_range", Names.fertile_lilypad, 4, 1, 15);
		Settings.FertileLilypad.fullPotencyRange = getInt("full_potency_range", Names.fertile_lilypad, 1, 1, 15);

		//midas touchstone configs
		List<String> goldItems = ImmutableList.of();
		Settings.MidasTouchstone.goldItems = getStringList("gold_items", Names.midas_touchstone, goldItems);
		Settings.MidasTouchstone.ticksBetweenRepairTicks = getInt("ticks_between_repair_ticks", Names.midas_touchstone, 4, 1, cleanShortMax);
		Settings.MidasTouchstone.glowstoneCost = getInt("glowstone_cost", Names.midas_touchstone, 1, 0, 3);
		Settings.MidasTouchstone.glowstoneWorth = getInt("glowstone_worth", Names.midas_touchstone, 4, 0, 12);
		Settings.MidasTouchstone.glowstoneLimit = getInt("glowstone_limit", Names.midas_touchstone, 250, 0, itemCap);

		//phoenix down configs
		Settings.PhoenixDown.hungerCostPercent = getInt("hunger_cost_percent", Names.phoenix_down, 25, 0, 50);
		Settings.PhoenixDown.leapingPotency = getInt("leaping_potency", Names.phoenix_down, 1, 0, 5);
		Settings.PhoenixDown.healPercentageOfMaxLife = getInt("heal_percentage_of_max_life", Names.phoenix_down, 100, 0, 100);
		Settings.PhoenixDown.removeNegativeStatus = getBoolean("remove_negative_status", Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryDamageResistance = getBoolean("give_temporary_damage_resistance", Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryRegeneration = getBoolean("give_temporary_regeneration", Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou = getBoolean("give_temporary_fire_resistance_if_fire_damage_killed_you", Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou = getBoolean("give_temporary_water_breathing_if_drowning_killed_you", Names.phoenix_down, true);

		//pyromancer staff configs
		Settings.PyromancerStaff.hungerCostPercent = getInt("hunger_cost_percent", Names.pyromancer_staff, 5, 0, 10);
		Settings.PyromancerStaff.fireChargeLimit = getInt("fire_charge_limit", Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.fireChargeCost = getInt("fire_charge_cost", Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.fireChargeWorth = getInt("fire_charge_worth", Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.ghastAbsorbWorth = getInt("ghast_absorb_worth", Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderLimit = getInt("blaze_powder_limit", Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.blazePowderCost = getInt("blaze_powder_cost", Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderWorth = getInt("blaze_powder_worth", Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazeAbsorbWorth = getInt("blaze_absorb_worth", Names.pyromancer_staff, 1, 0, 3);

		//rending gale configs
		Settings.RendingGale.chargeLimit = getInt("charge_limit", Names.rending_gale, cleanShortMax, 0, cleanIntMax);
		Settings.RendingGale.castChargeCost = getInt("cast_charge_cost", Names.rending_gale, 1, 0, 3);
		Settings.RendingGale.boltChargeCost = getInt("bolt_charge_cost", Names.rending_gale, 100, 0, 250);
		Settings.RendingGale.chargeFeatherWorth = getInt("charge_feather_worth", Names.rending_gale, 100, 1, 250);
		Settings.RendingGale.blockTargetRange = getInt("block_target_range", Names.rending_gale, 12, 5, 15);
		Settings.RendingGale.pushPullRadius = getInt("push_pull_radius", Names.rending_gale, 10, 1, 20);
		Settings.RendingGale.canPushProjectiles = getBoolean("can_push_projectiles", Names.rending_gale, false);
		Settings.RendingGale.entitiesThatCanBePushed = getStringList("entities_that_can_be_pushed", Names.rending_gale, ImmutableList.of());
		Settings.RendingGale.projectilesThatCanBePushed = getStringList("projectiles_that_can_be_pushed", Names.rending_gale, ImmutableList.of());

		//rod of lyssa configs
		Settings.RodOfLyssa.useLeveledFailureRate = getBoolean("use_leveled_failure_rate", Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.levelCapForLeveledFormula = getInt("level_cap_for_leveled_formula", Names.rod_of_lyssa, 100, 1, 900);
		Settings.RodOfLyssa.flatStealFailurePercentRate = getInt("flat_steal_failure_percent_rate", Names.rod_of_lyssa, 10, 0, 100);
		Settings.RodOfLyssa.stealFromVacantSlots = getBoolean("steal_from_vacant_slots", Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.failStealFromVacantSlots = getBoolean("fail_steal_from_vacant_slots", Names.rod_of_lyssa, false);
		Settings.RodOfLyssa.angerOnStealFailure = getBoolean("anger_on_steal_failure", Names.rod_of_lyssa, true);

		Settings.SeekerShot.entitiesThatCanBeHunted = getStringList("entities_that_can_be_hunted", Names.seeker_shot, ImmutableList.of());

		//sojourners staff configs
		List<String> torches = ImmutableList.of();
		Settings.SojournerStaff.torches = getStringList("torches", Names.sojourner_staff, torches);
		Settings.SojournerStaff.maxCapacityPerItemType = getInt("max_capacity_per_item_type", Names.sojourner_staff, 1500, 1, itemCap);
		Settings.SojournerStaff.maxRange = getInt("max_range", Names.sojourner_staff, 30, 1, 30);
		Settings.SojournerStaff.tilePerCostMultiplier = getInt("tile_per_cost_multiplier", Names.sojourner_staff, 6, 6, 30);

		//twilight cloak configs
		Settings.TwilightCloak.maxLightLevel = getInt("max_light_level", Names.twilight_cloak, 4, 0, 15);
		//Reliquary.CONFIG.require(Names.twilight_cloak, "only_works_at_night", false);

		//void tear configs
		Settings.VoidTear.itemLimit = getInt("item_limit", Names.void_tear, 2000000000, 0, cleanIntMax);
		Settings.VoidTear.absorbWhenCreated = getBoolean("absorb_when_created", Names.void_tear, true);
	}

	private static void loadMobDropProbabilities()
	{
		HashMap<String, Integer> drops = new HashMap<>(  );

		drops.put(Names.zombie_heart + "_base", getInt(Names.zombie_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.zombie_heart + "_base", getInt(Names.zombie_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.zombie_heart + "_looting", getInt(Names.zombie_heart + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.pigman_heart + "_base", getInt(Names.pigman_heart + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.pigman_heart + "_looting", getInt(Names.pigman_heart + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.rib_bone + "_base", getInt(Names.rib_bone + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.rib_bone + "_looting", getInt(Names.rib_bone + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.withered_rib + "_base", getInt(Names.withered_rib + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.withered_rib + "_looting", getInt(Names.withered_rib + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.spider_fangs + "_base", getInt(Names.spider_fangs + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.spider_fangs + "_looting", getInt(Names.spider_fangs + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.cave_spider_fangs + "_base", getInt(Names.cave_spider_fangs + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.cave_spider_fangs + "_looting", getInt(Names.cave_spider_fangs + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.blaze_molten_core + "_base", getInt(Names.blaze_molten_core + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.blaze_molten_core + "_looting", getInt(Names.blaze_molten_core + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.magma_cube_molten_core + "_base", getInt(Names.magma_cube_molten_core + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.magma_cube_molten_core + "_looting", getInt(Names.magma_cube_molten_core + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.frozen_core + "_base", getInt(Names.frozen_core + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.frozen_core + "_looting", getInt(Names.frozen_core + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.eye_of_the_storm + "_base", getInt(Names.eye_of_the_storm + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.eye_of_the_storm + "_looting", getInt(Names.eye_of_the_storm + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.bat_wing + "_base", getInt(Names.bat_wing + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.bat_wing + "_looting", getInt(Names.bat_wing + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.creeper_gland + "_base", getInt(Names.creeper_gland + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.creeper_gland + "_looting", getInt(Names.creeper_gland + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.ghast_gland + "_base", getInt(Names.ghast_gland + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.ghast_gland + "_looting", getInt(Names.ghast_gland + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.witch_hat + "_base", getInt(Names.witch_hat + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.witch_hat + "_looting", getInt(Names.witch_hat + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.squid_beak + "_base", getInt(Names.squid_beak + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.squid_beak + "_looting", getInt(Names.squid_beak + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.slime_pearl + "_base", getInt(Names.slime_pearl + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.slime_pearl + "_looting", getInt(Names.slime_pearl + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.ender_heart + "_base", getInt(Names.ender_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.ender_heart + "_looting", getInt(Names.ender_heart + "_looting", Names.mob_drop_probability, 5, 0, 100));

		Settings.mobDropProbabilities = drops;
	}

	private static void loadEasyModeSettings()
	{
		boolean easyModeDefault = true;

		Settings.EasyModeRecipes.fortuneCoin = getBoolean( Names.fortune_coin, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.altar = getBoolean(Names.altar, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalChalice = getBoolean(Names.infernal_chalice, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.enderStaff = getBoolean(Names.ender_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.salamanderEye = getBoolean(Names.salamander_eye, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.rodOfLyssa = getBoolean(Names.rod_of_lyssa, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.serpentStaff = getBoolean(Names.serpent_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.rendingGale = getBoolean(Names.rending_gale, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.pyromancerStaff = getBoolean(Names.pyromancer_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.magicBane = getBoolean(Names.magicbane, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.lanternOfParanoia = getBoolean(Names.lantern_of_paranoia, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.alkahestryTome = getBoolean(Names.alkahestry_tome, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.wraithNode = getBoolean(Names.wraith_node, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.glacialStaff = getBoolean(Names.glacial_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.sojournerStaff = getBoolean(Names.sojourner_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.krakenShell = getBoolean(Names.kraken_shell, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.angelicFeather = getBoolean(Names.angelic_feather, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.emperorChalice = getBoolean(Names.emperor_chalice, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.heroMedallion = getBoolean(Names.hero_medallion, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.iceMagnusRod = getBoolean(Names.ice_magus_rod, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalClaws = getBoolean(Names.infernal_claws, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.destructionCatalyst = getBoolean(Names.destruction_catalyst, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.interdictionTorch = getBoolean(Names.interdiction_torch, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.voidTear = getBoolean(Names.void_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalTear = getBoolean(Names.infernal_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.fertileEssence = getBoolean(Names.fertile_essence, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.seekerShot = getBoolean(Names.seeker_shot, Names.easy_mode_recipes, easyModeDefault);
	}

	private static void loadHudPositions()
	{
		Settings.HudPositions.sojournerStaff = getInt( Names.sojourner_staff, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.handgun = getInt(Names.handgun, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.alkahestryTome = getInt(Names.alkahestry_tome, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.destructionCatalyst = getInt(Names.destruction_catalyst, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.elsewhereFlask = getInt(Names.elsewhere_flask, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.enderStaff = getInt(Names.ender_staff, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.iceMagnusRod = getInt(Names.ice_magus_rod, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.glacialStaff = getInt(Names.glacial_staff, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.voidTear = getInt(Names.void_tear, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.midasTouchstone = getInt(Names.midas_touchstone, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.harvestRod = getInt(Names.harvest_rod, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.infernalChalice = getInt(Names.infernal_chalice, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.heroMedallion = getInt(Names.hero_medallion, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.pyromanceStaff = getInt(Names.pyromancer_staff, Names.hud_positions, 3, 1, 4);
		Settings.HudPositions.rendingGale = getInt(Names.rending_gale, Names.hud_positions, 3, 1, 4);
	}

	private static List<String> getStringList(String name, String category, List<String> defaultValue) {
		return Arrays.asList(configuration.getStringList(name, Names.hud_positions, defaultValue.toArray(new String[defaultValue.size()]), getTranslatedComment(category, name), new String[]{}, getLabelLangRef(category, name)));
	}

	private static boolean getBoolean(String name, String category, boolean defaultValue) {
		return configuration.getBoolean( name, Names.hud_positions, defaultValue, getTranslatedComment( category, name ), getLabelLangRef( category, name));
	}

	private static int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
		return configuration.getInt(name, Names.hud_positions, defaultValue, minValue, maxValue, getTranslatedComment(category, name) , getLabelLangRef(category, name));
	}


	private static String getTranslatedComment(String category, String config) {
		return StatCollector.translateToLocal(category + "." + config + ".comment");
	}

	private static String getLabelLangRef(String category, String config) {
		return category + "." + config + ".label";
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase( Reference.MOD_ID))
		{
			loadConfiguration();
		}
	}

/*


	public void postInit() {
		List<String> entityNames = new ArrayList<String>();
		for (Object o : EntityList.stringToClassMapping.values()) {
			Class c = (Class)o;
			if (EntityLiving.class.isAssignableFrom(c)) {
				entityNames.add((String)EntityList.classToStringMapping.get(o));
			}
		}
		List<String> projectileNames = new ArrayList<String>();
		for (Object o : EntityList.stringToClassMapping.values()) {
			Class c = (Class)o;
			if (IProjectile.class.isAssignableFrom(c)) {
				projectileNames.add((String)EntityList.classToStringMapping.get(o));
			}
		}

		Reliquary.CONFIG.require(Names.interdiction_torch, "entities_that_can_be_pushed", new ConfigReference(entityNames));
		Reliquary.CONFIG.require(Names.interdiction_torch, "projectiles_that_can_be_pushed", new ConfigReference(projectileNames));

		Reliquary.CONFIG.require("entities_that_can_be_pushed", Names.rending_gale, new ConfigReference(entityNames));
		Reliquary.CONFIG.require("projectiles_that_can_be_pushed", Names.rending_gale, new ConfigReference(projectileNames));

		Reliquary.CONFIG.require(Names.seeker_shot, "entities_that_can_be_hunted", entityNames));

	}
*/

}
