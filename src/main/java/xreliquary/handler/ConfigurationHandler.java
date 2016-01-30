package xreliquary.handler;


import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.io.File;
import java.util.*;


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
	}

	public static void postInit() {
		List<String> entityNames = new ArrayList<String>();
		for (Object o : EntityList.stringToClassMapping.values()) {
			Class c = (Class)o;
			if (EntityLiving.class.isAssignableFrom(c)) {
				entityNames.add( EntityList.classToStringMapping.get(o) );
			}
		}
		List<String> projectileNames = new ArrayList<String>();
		for (Object o : EntityList.stringToClassMapping.values()) {
			Class c = (Class)o;
			if (IProjectile.class.isAssignableFrom(c)) {
				projectileNames.add( EntityList.classToStringMapping.get(o) );
			}
		}

		Settings.InterdictionTorch.entitiesThatCanBePushed = getStringList("entities_that_can_be_pushed", Names.item_and_block_settings + "." + Names.interdiction_torch, entityNames );
		Settings.InterdictionTorch.projectilesThatCanBePushed = getStringList("projectiles_that_can_be_pushed", Names.item_and_block_settings + "." + Names.interdiction_torch, projectileNames);

		Settings.RendingGale.entitiesThatCanBePushed = getStringList("entities_that_can_be_pushed", Names.item_and_block_settings + "." + Names.rending_gale, entityNames );
		Settings.RendingGale.projectilesThatCanBePushed = getStringList( "projectiles_that_can_be_pushed", Names.item_and_block_settings + "." + Names.rending_gale, projectileNames );

		Settings.SeekerShot.entitiesThatCanBeHunted = getStringList( "entities_that_can_be_hunted", Names.item_and_block_settings + "." + Names.seeker_shot, entityNames);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.seeker_shot, true);

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
		Settings.AlkahestryTome.redstoneLimit = getInt("redstone_limit", Names.item_and_block_settings + "." + Names.alkahestry_tome, 250, 0, itemCap);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome, true);

		//altar configs
		Settings.Altar.redstoneCost = getInt("redstone_cost", Names.item_and_block_settings + "." + Names.altar, 3, 0, 10);
		Settings.Altar.timeInMinutes = getInt("time_in_minutes", Names.item_and_block_settings + "." + Names.altar, 20, 0, 60);
		Settings.Altar.maximumTimeVarianceInMinutes = getInt("maximum_time_variance_in_minutes", Names.item_and_block_settings + "." + Names.altar, 5, 0, 15);
		Settings.Altar.outputLightLevelWhileActive = getInt("output_light_level_while_active", Names.item_and_block_settings + "." + Names.altar, 16, 16, 0);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.altar, true);

		//angelic feather configs
		Settings.AngelicFeather.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.angelic_feather, 50, 0, 100);
		Settings.AngelicFeather.leapingPotency = getInt("leaping_potency", Names.item_and_block_settings + "." + Names.angelic_feather, 1, 0, 5);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.angelic_feather, true);

		//angelheart vial configs
		Settings.AngelHeartVial.healPercentageOfMaxLife = getInt("heal_percentage_of_max_life", Names.item_and_block_settings + "." + Names.angelheart_vial, 25, 0, 100);
		Settings.AngelHeartVial.removeNegativeStatus = getBoolean("remove_negative_status", Names.item_and_block_settings + "." + Names.angelheart_vial, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.angelheart_vial, true);

		//apothecary cauldron configs
		List<String> heatSources = ImmutableList.of();
		Settings.ApothecaryCauldron.redstoneLimit = getInt("redstone_limit", Names.item_and_block_settings + "." + Names.apothecary_cauldron, 5, 0, 100);
		Settings.ApothecaryCauldron.cookTime = getInt("cook_time", Names.item_and_block_settings + "." + Names.apothecary_cauldron, 160, 20, 32000);
		Settings.ApothecaryCauldron.heatSources = getStringList("heat_sources", Names.item_and_block_settings + "." + Names.apothecary_cauldron, heatSources);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.apothecary_cauldron, true);

		//destruction catalyst configs
		Settings.DestructionCatalyst.mundaneBlocks = getStringList("mundane_blocks", Names.item_and_block_settings + "." + Names.destruction_catalyst, new ArrayList<String>(ItemDestructionCatalyst.ids));
		Settings.DestructionCatalyst.gunpowderCost = getInt("gunpowder_cost", Names.item_and_block_settings + "." + Names.destruction_catalyst, 3, 0, 10);
		Settings.DestructionCatalyst.gunpowderWorth = getInt("gunpowder_worth", Names.item_and_block_settings + "." + Names.destruction_catalyst, 1, 0, 3);
		Settings.DestructionCatalyst.gunpowderLimit = getInt("gunpowder_limit", Names.item_and_block_settings + "." + Names.destruction_catalyst, 250, 0, itemCap);
		Settings.DestructionCatalyst.explosionRadius = getInt("explosion_radius", Names.item_and_block_settings + "." + Names.destruction_catalyst, 1, 1, 5);
		Settings.DestructionCatalyst.centeredExplosion = getBoolean("centered_explosion", Names.item_and_block_settings + "." + Names.destruction_catalyst, false);
		Settings.DestructionCatalyst.perfectCube = getBoolean("perfect_cube", Names.item_and_block_settings + "." + Names.destruction_catalyst, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.destruction_catalyst, true);

		//emperor's chalice configs
		Settings.EmperorChalice.hungerSatiationMultiplier = getInt("hunger_satiation_multiplier", Names.item_and_block_settings + "." + Names.emperor_chalice, 4, 0, 10);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.emperor_chalice, true);

		//ender staff configs
		Settings.EnderStaff.enderPearlCastCost = getInt("ender_pearl_cast_cost", Names.item_and_block_settings + "." + Names.ender_staff, 1, 0, 3);
		Settings.EnderStaff.enderPearlNodeWarpCost = getInt("ender_pearl_node_warp_cost",Names.item_and_block_settings + "." + Names.ender_staff,  1, 0, 3);
		Settings.EnderStaff.enderPearlWorth = getInt("ender_pearl_worth", Names.item_and_block_settings + "." + Names.ender_staff, 1, 0, 10);
		Settings.EnderStaff.enderPearlLimit = getInt("ender_pearl_limit", Names.item_and_block_settings + "." + Names.ender_staff, 250, 0, itemCap);
		Settings.EnderStaff.nodeWarpCastTime = getInt("node_warp_cast_time", Names.item_and_block_settings + "." + Names.ender_staff, 60, 10, 120);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.ender_staff, true);

		//fortune coin configs
		Settings.FortuneCoin.disableAudio = getBoolean("disable_audio", Names.item_and_block_settings + "." + Names.fortune_coin, false);
		Settings.FortuneCoin.standardPullDistance = getInt("standard_pull_distance", Names.item_and_block_settings + "." + Names.fortune_coin, 5, 3, 10);
		Settings.FortuneCoin.longRangePullDistance = getInt("long_range_pull_distance", Names.item_and_block_settings + "." + Names.fortune_coin, 15, 9, 30);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.fortune_coin, true);

		//glacial staff configs
		Settings.GlacialStaff.snowballLimit = getInt("snowball_limit", Names.item_and_block_settings + "." + Names.glacial_staff, 250, 0, itemCap);
		Settings.GlacialStaff.snowballCost = getInt("snowball_cost", Names.item_and_block_settings + "." + Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballWorth = getInt("snowball_worth", Names.item_and_block_settings + "." + Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballDamage = getInt("snowball_damage", Names.item_and_block_settings + "." + Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusFireImmune = getInt("snowball_damage_bonus_fire_immune", Names.item_and_block_settings + "." + Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusBlaze = getInt("snowball_damage_bonus_blaze", Names.item_and_block_settings + "." + Names.glacial_staff, 6, 0, 12);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.glacial_staff, true);

		//harvest rod configs
		Settings.HarvestRod.bonemealLimit = getInt("bonemeal_limit", Names.item_and_block_settings + "." + Names.harvest_rod, 250, 0, itemCap);
		Settings.HarvestRod.bonemealCost = getInt("bonemeal_cost", Names.item_and_block_settings + "." + Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealWorth = getInt("bonemeal_worth", Names.item_and_block_settings + "." + Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealLuckPercentChance = getInt("bonemeal_luck_percent_chance", Names.item_and_block_settings + "." + Names.harvest_rod, 33, 1, 100);
		Settings.HarvestRod.bonemealLuckRolls = getInt("bonemeal_luck_rolls", Names.item_and_block_settings + "." + Names.harvest_rod, 2, 0, 7);
		Settings.HarvestRod.harvestBreakRadius = getInt("harvest_break_radius", Names.item_and_block_settings + "." + Names.harvest_rod, 2, 0, 5);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.harvest_rod, true);

		//hero's medallion config
		Settings.HeroMedallion.experienceLevelMaximum = getInt("experience_level_maximum", Names.item_and_block_settings + "." + Names.hero_medallion, 30, 0, 60);
		Settings.HeroMedallion.experienceLevelMinimum = getInt("experience_level_minimum", Names.item_and_block_settings + "." + Names.hero_medallion, 0, 0, 30);
		Settings.HeroMedallion.experienceLimit =getInt("experience_limit", Names.item_and_block_settings + "." + Names.hero_medallion, cleanIntMax, 0, cleanIntMax);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.hero_medallion, true);

		//ice rod configs
		Settings.IceMagusRod.snowballLimit = getInt("snowball_limit", Names.item_and_block_settings + "." + Names.ice_magus_rod, 250, 0, itemCap);
		Settings.IceMagusRod.snowballCost = getInt("snowball_cost", Names.item_and_block_settings + "." + Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballWorth = getInt("snowball_worth", Names.item_and_block_settings + "." + Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballDamage = getInt("snowball_damage", Names.item_and_block_settings + "." + Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusFireImmune = getInt("snowball_damage_bonus_fire_immune", Names.item_and_block_settings + "." + Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusBlaze = getInt("snowball_damage_bonus_blaze", Names.item_and_block_settings + "." + Names.ice_magus_rod, 4, 0, 8);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.ice_magus_rod, true);

		//infernal claws configs
		Settings.InfernalClaws.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.infernal_claws, 10, 0, 30);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.infernal_claws, true);

		//infernal chalice configs
		Settings.InfernalChalice.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.infernal_chalice, 5, 0, 10);
		Settings.InfernalChalice.fluidLimit = getInt("fluid_limit", Names.item_and_block_settings + "." + Names.infernal_chalice, 500000, 0, cleanIntMax);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.infernal_chalice, true);

		//interdiction torch configs
		//see post init for entity configs
		Settings.InterdictionTorch.pushRadius = getInt("push_radius", Names.item_and_block_settings + "." + Names.interdiction_torch, 5, 1, 15);
		Settings.InterdictionTorch.canPushProjectiles = getBoolean("can_push_projectiles", Names.item_and_block_settings + "." + Names.interdiction_torch, false);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.interdiction_torch, true);

		//kraken shell configs
		Settings.KrakenShell.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.kraken_shell, 25, 0, 50);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.kraken_shell, true);

		//lantern of paranoia configs
		Settings.LanternOfParanoia.minLightLevel = getInt("min_light_level", Names.item_and_block_settings + "." + Names.lantern_of_paranoia, 8, 0, 15);
		Settings.LanternOfParanoia.placementScanRadius = getInt("placement_scan_radius", Names.item_and_block_settings + "." + Names.lantern_of_paranoia, 6, 1, 15);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.lantern_of_paranoia, true);

		//fertile_lilypad of fertility configs
		Settings.FertileLilypad.secondsBetweenGrowthTicks = getInt("seconds_between_growth_ticks", Names.item_and_block_settings + "." + Names.fertile_lilypad, 47, 1, 150);
		Settings.FertileLilypad.tileRange = getInt("tile_range", Names.item_and_block_settings + "." + Names.fertile_lilypad, 4, 1, 15);
		Settings.FertileLilypad.fullPotencyRange = getInt("full_potency_range", Names.item_and_block_settings + "." + Names.fertile_lilypad, 1, 1, 15);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.fertile_lilypad, true);

		//midas touchstone configs
		List<String> goldItems = ImmutableList.of();
		Settings.MidasTouchstone.goldItems = getStringList("gold_items", Names.item_and_block_settings + "." + Names.midas_touchstone, goldItems);
		Settings.MidasTouchstone.ticksBetweenRepairTicks = getInt("ticks_between_repair_ticks", Names.item_and_block_settings + "." + Names.midas_touchstone, 4, 1, cleanShortMax);
		Settings.MidasTouchstone.glowstoneCost = getInt("glowstone_cost", Names.item_and_block_settings + "." + Names.midas_touchstone, 1, 0, 3);
		Settings.MidasTouchstone.glowstoneWorth = getInt("glowstone_worth", Names.item_and_block_settings + "." + Names.midas_touchstone, 4, 0, 12);
		Settings.MidasTouchstone.glowstoneLimit = getInt("glowstone_limit", Names.item_and_block_settings + "." + Names.midas_touchstone, 250, 0, itemCap);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.midas_touchstone, true);

		//phoenix down configs
		Settings.PhoenixDown.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.phoenix_down, 25, 0, 50);
		Settings.PhoenixDown.leapingPotency = getInt("leaping_potency", Names.item_and_block_settings + "." + Names.phoenix_down, 1, 0, 5);
		Settings.PhoenixDown.healPercentageOfMaxLife = getInt("heal_percentage_of_max_life", Names.item_and_block_settings + "." + Names.phoenix_down, 100, 0, 100);
		Settings.PhoenixDown.removeNegativeStatus = getBoolean("remove_negative_status", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryDamageResistance = getBoolean("give_temporary_damage_resistance", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryRegeneration = getBoolean("give_temporary_regeneration", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou = getBoolean("give_temporary_fire_resistance_if_fire_damage_killed_you", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou = getBoolean("give_temporary_water_breathing_if_drowning_killed_you", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.phoenix_down, true);

		//pyromancer staff configs
		Settings.PyromancerStaff.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.pyromancer_staff, 5, 0, 10);
		Settings.PyromancerStaff.fireChargeLimit = getInt("fire_charge_limit", Names.item_and_block_settings + "." + Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.fireChargeCost = getInt("fire_charge_cost", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.fireChargeWorth = getInt("fire_charge_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.ghastAbsorbWorth = getInt("ghast_absorb_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderLimit = getInt("blaze_powder_limit", Names.item_and_block_settings + "." + Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.blazePowderCost = getInt("blaze_powder_cost", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderWorth = getInt("blaze_powder_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazeAbsorbWorth = getInt("blaze_absorb_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.pyromancer_staff, true);

		//rending gale configs
		Settings.RendingGale.chargeLimit = getInt("charge_limit", Names.item_and_block_settings + "." + Names.rending_gale, cleanShortMax, 0, cleanIntMax);
		Settings.RendingGale.castChargeCost = getInt("cast_charge_cost", Names.item_and_block_settings + "." + Names.rending_gale, 1, 0, 3);
		Settings.RendingGale.boltChargeCost = getInt("bolt_charge_cost", Names.item_and_block_settings + "." + Names.rending_gale, 100, 0, 250);
		Settings.RendingGale.chargeFeatherWorth = getInt("charge_feather_worth", Names.item_and_block_settings + "." + Names.rending_gale, 100, 1, 250);
		Settings.RendingGale.blockTargetRange = getInt("block_target_range", Names.item_and_block_settings + "." + Names.rending_gale, 12, 5, 15);
		Settings.RendingGale.pushPullRadius = getInt("push_pull_radius", Names.item_and_block_settings + "." + Names.rending_gale, 10, 1, 20);
		Settings.RendingGale.canPushProjectiles = getBoolean("can_push_projectiles", Names.item_and_block_settings + "." + Names.rending_gale, false);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.rending_gale, true);

		//rod of lyssa configs
		Settings.RodOfLyssa.useLeveledFailureRate = getBoolean("use_leveled_failure_rate", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.levelCapForLeveledFormula = getInt("level_cap_for_leveled_formula", Names.item_and_block_settings + "." + Names.rod_of_lyssa, 100, 1, 900);
		Settings.RodOfLyssa.flatStealFailurePercentRate = getInt("flat_steal_failure_percent_rate", Names.item_and_block_settings + "." + Names.rod_of_lyssa, 10, 0, 100);
		Settings.RodOfLyssa.stealFromVacantSlots = getBoolean("steal_from_vacant_slots", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.failStealFromVacantSlots = getBoolean("fail_steal_from_vacant_slots", Names.item_and_block_settings + "." + Names.rod_of_lyssa, false);
		Settings.RodOfLyssa.angerOnStealFailure = getBoolean("anger_on_steal_failure", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);

		//sojourners staff configs
		List<String> torches = ImmutableList.of();
		Settings.SojournerStaff.torches = getStringList("torches", Names.item_and_block_settings + "." + Names.sojourner_staff, torches);
		Settings.SojournerStaff.maxCapacityPerItemType = getInt("max_capacity_per_item_type", Names.item_and_block_settings + "." + Names.sojourner_staff, 1500, 1, itemCap);
		Settings.SojournerStaff.maxRange = getInt("max_range", Names.item_and_block_settings + "." + Names.sojourner_staff, 30, 1, 30);
		Settings.SojournerStaff.tilePerCostMultiplier = getInt("tile_per_cost_multiplier", Names.item_and_block_settings + "." + Names.sojourner_staff, 6, 6, 30);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.sojourner_staff, true);

		//twilight cloak configs
		Settings.TwilightCloak.maxLightLevel = getInt("max_light_level", Names.item_and_block_settings + "." + Names.twilight_cloak, 4, 0, 15);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.twilight_cloak, true);

		//void tear configs
		Settings.VoidTear.itemLimit = getInt("item_limit", Names.item_and_block_settings + "." + Names.void_tear, 2000000000, 0, cleanIntMax);
		Settings.VoidTear.absorbWhenCreated = getBoolean("absorb_when_created", Names.item_and_block_settings + "." + Names.void_tear, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.void_tear, true);

		setCategoryTranslations(Names.item_and_block_settings, true);
	}

	private static void loadMobDropProbabilities()
	{
		HashMap<String, Integer> drops = new HashMap<>(  );

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
		setCategoryTranslations(Names.mob_drop_probability, true);
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
		Settings.EasyModeRecipes.iceMagusRod = getBoolean(Names.ice_magus_rod, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalClaws = getBoolean(Names.infernal_claws, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.destructionCatalyst = getBoolean(Names.destruction_catalyst, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.interdictionTorch = getBoolean(Names.interdiction_torch, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.voidTear = getBoolean(Names.void_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalTear = getBoolean(Names.infernal_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.fertileEssence = getBoolean(Names.fertile_essence, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.seekerShot = getBoolean(Names.seeker_shot, Names.easy_mode_recipes, easyModeDefault);

		setCategoryTranslations(Names.easy_mode_recipes, true);
	}

	private static void loadHudPositions()
	{
		Settings.HudPositions.sojournerStaff = getInt( Names.sojourner_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.handgun = getInt(Names.handgun, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.alkahestryTome = getInt(Names.alkahestry_tome, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.destructionCatalyst = getInt(Names.destruction_catalyst, Names.hud_positions, 3, 0, 3);
		//Settings.HudPositions.elsewhereFlask = getInt(Names.elsewhere_flask, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.enderStaff = getInt(Names.ender_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.iceMagusRod = getInt(Names.ice_magus_rod, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.glacialStaff = getInt(Names.glacial_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.voidTear = getInt(Names.void_tear, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.midasTouchstone = getInt(Names.midas_touchstone, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.harvestRod = getInt(Names.harvest_rod, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.infernalChalice = getInt(Names.infernal_chalice, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.heroMedallion = getInt(Names.hero_medallion, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.pyromancerStaff = getInt(Names.pyromancer_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.rendingGale = getInt(Names.rending_gale, Names.hud_positions, 3, 0, 3);

		setCategoryTranslations(Names.hud_positions, true);
	}

	private static List<String> getStringList(String name, String category, List<String> defaultValue) {
		return Arrays.asList(configuration.getStringList(name, category, defaultValue.toArray(new String[defaultValue.size()]), getTranslatedComment(category, name), new String[]{}, getLabelLangRef(category, name)));
	}

	private static boolean getBoolean(String name, String category, boolean defaultValue) {
		return configuration.getBoolean(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef( category, name));
	}

	private static int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
		return configuration.getInt(name, category, defaultValue, minValue, maxValue, getTranslatedComment(category, name) , getLabelLangRef(category, name));
	}


	private static String getTranslatedComment(String category, String config) {
		return StatCollector.translateToLocal("config." + category + "." + config + ".comment");
	}

	private static String getLabelLangRef(String category, String config) {
		return "config." + category + "." + config + ".label";
	}

	private static void setCategoryTranslations(String categoryName, boolean setComment) {
		ConfigCategory category = configuration.getCategory(categoryName);

		category.setLanguageKey("config." + categoryName + ".label");
		if (setComment) {
			category.setComment( StatCollector.translateToLocal("config." + categoryName + ".comment"));
		}
	}


	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase( Reference.MOD_ID))
		{
			loadConfiguration();
			postInit();
		}
	}
}
