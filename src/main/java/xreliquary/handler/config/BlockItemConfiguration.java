package xreliquary.handler.config;


import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

import java.util.ArrayList;
import java.util.List;


public class BlockItemConfiguration
{
	public static void loadEntitiesSettings()
	{
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

		Settings.InterdictionTorch.entitiesThatCanBePushed = ConfigurationHandler.getStringList("entities_that_can_be_pushed", Names.item_and_block_settings + "." + Names.interdiction_torch, entityNames );
		Settings.InterdictionTorch.projectilesThatCanBePushed = ConfigurationHandler.getStringList("projectiles_that_can_be_pushed", Names.item_and_block_settings + "." + Names.interdiction_torch, projectileNames);

		Settings.RendingGale.entitiesThatCanBePushed = ConfigurationHandler.getStringList("entities_that_can_be_pushed", Names.item_and_block_settings + "." + Names.rending_gale, entityNames );
		Settings.RendingGale.projectilesThatCanBePushed = ConfigurationHandler.getStringList( "projectiles_that_can_be_pushed", Names.item_and_block_settings + "." + Names.rending_gale, projectileNames );

		Settings.SeekerShot.entitiesThatCanBeHunted = ConfigurationHandler.getStringList( "entities_that_can_be_hunted", Names.item_and_block_settings + "." + Names.seeker_shot, entityNames);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.seeker_shot, true);
	}

	public static void loadBlockAndItemSettings() {
		int itemCap = 9999;
		int cleanShortMax = 30000;
		int cleanIntMax = 2000000000;

		//alkahestry tome configs
		Settings.AlkahestryTome.chargeLimit = ConfigurationHandler.getInt("charge_limit", Names.item_and_block_settings + "." + Names.alkahestry_tome, 250, 0, itemCap);
		ConfigurationHandler.configuration.getCategory(Names.item_and_block_settings + "." + Names.alkahestry_tome).get("charge_limit").setRequiresMcRestart(true);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome, true);

		//altar configs
		Settings.Altar.redstoneCost = ConfigurationHandler.getInt("redstone_cost", Names.item_and_block_settings + "." + Names.altar, 3, 0, 10);
		Settings.Altar.timeInMinutes = ConfigurationHandler.getInt("time_in_minutes", Names.item_and_block_settings + "." + Names.altar, 20, 0, 60);
		Settings.Altar.maximumTimeVarianceInMinutes = ConfigurationHandler.getInt("maximum_time_variance_in_minutes", Names.item_and_block_settings + "." + Names.altar, 5, 0, 15);
		Settings.Altar.outputLightLevelWhileActive = ConfigurationHandler.getInt("output_light_level_while_active", Names.item_and_block_settings + "." + Names.altar, 16, 16, 0);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.altar, true);

		//angelic feather configs
		Settings.AngelicFeather.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.angelic_feather, 50, 0, 100);
		Settings.AngelicFeather.leapingPotency = ConfigurationHandler.getInt("leaping_potency", Names.item_and_block_settings + "." + Names.angelic_feather, 1, 0, 5);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.angelic_feather, true);

		//angelheart vial configs
		Settings.AngelHeartVial.healPercentageOfMaxLife = ConfigurationHandler.getInt("heal_percentage_of_max_life", Names.item_and_block_settings + "." + Names.angelheart_vial, 25, 0, 100);
		Settings.AngelHeartVial.removeNegativeStatus = ConfigurationHandler.getBoolean("remove_negative_status", Names.item_and_block_settings + "." + Names.angelheart_vial, true);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.angelheart_vial, true);

		//apothecary cauldron configs
		List<String> heatSources = ImmutableList.of();
		Settings.ApothecaryCauldron.redstoneLimit = ConfigurationHandler.getInt("redstone_limit", Names.item_and_block_settings + "." + Names.apothecary_cauldron, 5, 0, 100);
		Settings.ApothecaryCauldron.cookTime = ConfigurationHandler.getInt("cook_time", Names.item_and_block_settings + "." + Names.apothecary_cauldron, 160, 20, 32000);
		Settings.ApothecaryCauldron.heatSources = ConfigurationHandler.getStringList("heat_sources", Names.item_and_block_settings + "." + Names.apothecary_cauldron, heatSources);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.apothecary_cauldron, true);

		//destruction catalyst configs
		Settings.DestructionCatalyst.mundaneBlocks = ConfigurationHandler.getStringList("mundane_blocks", Names.item_and_block_settings + "." + Names.destruction_catalyst, new ArrayList<String>(ItemDestructionCatalyst.ids));
		Settings.DestructionCatalyst.gunpowderCost = ConfigurationHandler.getInt("gunpowder_cost", Names.item_and_block_settings + "." + Names.destruction_catalyst, 3, 0, 10);
		Settings.DestructionCatalyst.gunpowderWorth = ConfigurationHandler.getInt("gunpowder_worth", Names.item_and_block_settings + "." + Names.destruction_catalyst, 1, 0, 3);
		Settings.DestructionCatalyst.gunpowderLimit = ConfigurationHandler.getInt("gunpowder_limit", Names.item_and_block_settings + "." + Names.destruction_catalyst, 250, 0, itemCap);
		Settings.DestructionCatalyst.explosionRadius = ConfigurationHandler.getInt("explosion_radius", Names.item_and_block_settings + "." + Names.destruction_catalyst, 1, 1, 5);
		Settings.DestructionCatalyst.centeredExplosion = ConfigurationHandler.getBoolean("centered_explosion", Names.item_and_block_settings + "." + Names.destruction_catalyst, false);
		Settings.DestructionCatalyst.perfectCube = ConfigurationHandler.getBoolean("perfect_cube", Names.item_and_block_settings + "." + Names.destruction_catalyst, true);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.destruction_catalyst, true);

		//emperor's chalice configs
		Settings.EmperorChalice.hungerSatiationMultiplier = ConfigurationHandler.getInt("hunger_satiation_multiplier", Names.item_and_block_settings + "." + Names.emperor_chalice, 4, 0, 10);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.emperor_chalice, true);

		//ender staff configs
		Settings.EnderStaff.enderPearlCastCost = ConfigurationHandler.getInt("ender_pearl_cast_cost", Names.item_and_block_settings + "." + Names.ender_staff, 1, 0, 3);
		Settings.EnderStaff.enderPearlNodeWarpCost = ConfigurationHandler.getInt("ender_pearl_node_warp_cost",Names.item_and_block_settings + "." + Names.ender_staff,  1, 0, 3);
		Settings.EnderStaff.enderPearlWorth = ConfigurationHandler.getInt("ender_pearl_worth", Names.item_and_block_settings + "." + Names.ender_staff, 1, 0, 10);
		Settings.EnderStaff.enderPearlLimit = ConfigurationHandler.getInt("ender_pearl_limit", Names.item_and_block_settings + "." + Names.ender_staff, 250, 0, itemCap);
		Settings.EnderStaff.nodeWarpCastTime = ConfigurationHandler.getInt("node_warp_cast_time", Names.item_and_block_settings + "." + Names.ender_staff, 60, 10, 120);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.ender_staff, true);

		//fortune coin configs
		Settings.FortuneCoin.disableAudio = ConfigurationHandler.getBoolean("disable_audio", Names.item_and_block_settings + "." + Names.fortune_coin, false);
		Settings.FortuneCoin.standardPullDistance = ConfigurationHandler.getInt("standard_pull_distance", Names.item_and_block_settings + "." + Names.fortune_coin, 5, 3, 10);
		Settings.FortuneCoin.longRangePullDistance = ConfigurationHandler.getInt("long_range_pull_distance", Names.item_and_block_settings + "." + Names.fortune_coin, 15, 9, 30);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.fortune_coin, true);

		//glacial staff configs
		Settings.GlacialStaff.snowballLimit = ConfigurationHandler.getInt("snowball_limit", Names.item_and_block_settings + "." + Names.glacial_staff, 250, 0, itemCap);
		Settings.GlacialStaff.snowballCost = ConfigurationHandler.getInt("snowball_cost", Names.item_and_block_settings + "." + Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballWorth = ConfigurationHandler.getInt("snowball_worth", Names.item_and_block_settings + "." + Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballDamage = ConfigurationHandler.getInt("snowball_damage", Names.item_and_block_settings + "." + Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusFireImmune = ConfigurationHandler.getInt("snowball_damage_bonus_fire_immune", Names.item_and_block_settings + "." + Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusBlaze = ConfigurationHandler.getInt("snowball_damage_bonus_blaze", Names.item_and_block_settings + "." + Names.glacial_staff, 6, 0, 12);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.glacial_staff, true);

		//harvest rod configs
		Settings.HarvestRod.bonemealLimit = ConfigurationHandler.getInt("bonemeal_limit", Names.item_and_block_settings + "." + Names.harvest_rod, 250, 0, itemCap);
		Settings.HarvestRod.bonemealCost = ConfigurationHandler.getInt("bonemeal_cost", Names.item_and_block_settings + "." + Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealWorth = ConfigurationHandler.getInt("bonemeal_worth", Names.item_and_block_settings + "." + Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealLuckPercentChance = ConfigurationHandler.getInt("bonemeal_luck_percent_chance", Names.item_and_block_settings + "." + Names.harvest_rod, 33, 1, 100);
		Settings.HarvestRod.bonemealLuckRolls = ConfigurationHandler.getInt("bonemeal_luck_rolls", Names.item_and_block_settings + "." + Names.harvest_rod, 2, 0, 7);
		Settings.HarvestRod.harvestBreakRadius = ConfigurationHandler.getInt("harvest_break_radius", Names.item_and_block_settings + "." + Names.harvest_rod, 2, 0, 5);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.harvest_rod, true);

		//hero's medallion config
		Settings.HeroMedallion.experienceLevelMaximum = ConfigurationHandler.getInt("experience_level_maximum", Names.item_and_block_settings + "." + Names.hero_medallion, 30, 0, 60);
		Settings.HeroMedallion.experienceLevelMinimum = ConfigurationHandler.getInt("experience_level_minimum", Names.item_and_block_settings + "." + Names.hero_medallion, 0, 0, 30);
		Settings.HeroMedallion.experienceLimit = ConfigurationHandler.getInt("experience_limit", Names.item_and_block_settings + "." + Names.hero_medallion, cleanIntMax, 0, cleanIntMax);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.hero_medallion, true);

		//ice rod configs
		Settings.IceMagusRod.snowballLimit = ConfigurationHandler.getInt("snowball_limit", Names.item_and_block_settings + "." + Names.ice_magus_rod, 250, 0, itemCap);
		Settings.IceMagusRod.snowballCost = ConfigurationHandler.getInt("snowball_cost", Names.item_and_block_settings + "." + Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballWorth = ConfigurationHandler.getInt("snowball_worth", Names.item_and_block_settings + "." + Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballDamage = ConfigurationHandler.getInt("snowball_damage", Names.item_and_block_settings + "." + Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusFireImmune = ConfigurationHandler.getInt("snowball_damage_bonus_fire_immune", Names.item_and_block_settings + "." + Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusBlaze = ConfigurationHandler.getInt("snowball_damage_bonus_blaze", Names.item_and_block_settings + "." + Names.ice_magus_rod, 4, 0, 8);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.ice_magus_rod, true);

		//infernal claws configs
		Settings.InfernalClaws.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.infernal_claws, 10, 0, 30);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.infernal_claws, true);

		//infernal chalice configs
		Settings.InfernalChalice.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.infernal_chalice, 5, 0, 10);
		Settings.InfernalChalice.fluidLimit = ConfigurationHandler.getInt("fluid_limit", Names.item_and_block_settings + "." + Names.infernal_chalice, 500000, 0, cleanIntMax);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.infernal_chalice, true);

		//infernal tear
		Settings.InfernalTear.absorbWhenCreated = ConfigurationHandler.getBoolean("absorb_when_created", Names.item_and_block_settings + "." + Names.infernal_tear, false);

		//interdiction torch configs
		//see post init for entity configs
		Settings.InterdictionTorch.pushRadius = ConfigurationHandler.getInt("push_radius", Names.item_and_block_settings + "." + Names.interdiction_torch, 5, 1, 15);
		Settings.InterdictionTorch.canPushProjectiles = ConfigurationHandler.getBoolean("can_push_projectiles", Names.item_and_block_settings + "." + Names.interdiction_torch, false);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.interdiction_torch, true);

		//kraken shell configs
		Settings.KrakenShell.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.kraken_shell, 25, 0, 50);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.kraken_shell, true);

		//lantern of paranoia configs
		Settings.LanternOfParanoia.minLightLevel = ConfigurationHandler.getInt("min_light_level", Names.item_and_block_settings + "." + Names.lantern_of_paranoia, 8, 0, 15);
		Settings.LanternOfParanoia.placementScanRadius = ConfigurationHandler.getInt("placement_scan_radius", Names.item_and_block_settings + "." + Names.lantern_of_paranoia, 6, 1, 15);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.lantern_of_paranoia, true);

		//fertile_lilypad of fertility configs
		Settings.FertileLilypad.secondsBetweenGrowthTicks = ConfigurationHandler.getInt("seconds_between_growth_ticks", Names.item_and_block_settings + "." + Names.fertile_lilypad, 47, 1, 150);
		Settings.FertileLilypad.tileRange = ConfigurationHandler.getInt("tile_range", Names.item_and_block_settings + "." + Names.fertile_lilypad, 4, 1, 15);
		Settings.FertileLilypad.fullPotencyRange = ConfigurationHandler.getInt("full_potency_range", Names.item_and_block_settings + "." + Names.fertile_lilypad, 1, 1, 15);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.fertile_lilypad, true);

		//midas touchstone configs
		List<String> goldItems = ImmutableList.of();
		Settings.MidasTouchstone.goldItems = ConfigurationHandler.getStringList("gold_items", Names.item_and_block_settings + "." + Names.midas_touchstone, goldItems);
		Settings.MidasTouchstone.ticksBetweenRepairTicks = ConfigurationHandler.getInt("ticks_between_repair_ticks", Names.item_and_block_settings + "." + Names.midas_touchstone, 4, 1, cleanShortMax);
		Settings.MidasTouchstone.glowstoneCost = ConfigurationHandler.getInt("glowstone_cost", Names.item_and_block_settings + "." + Names.midas_touchstone, 1, 0, 3);
		Settings.MidasTouchstone.glowstoneWorth = ConfigurationHandler.getInt("glowstone_worth", Names.item_and_block_settings + "." + Names.midas_touchstone, 4, 0, 12);
		Settings.MidasTouchstone.glowstoneLimit = ConfigurationHandler.getInt("glowstone_limit", Names.item_and_block_settings + "." + Names.midas_touchstone, 250, 0, itemCap);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.midas_touchstone, true);

		//phoenix down configs
		Settings.PhoenixDown.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.phoenix_down, 25, 0, 50);
		Settings.PhoenixDown.leapingPotency = ConfigurationHandler.getInt("leaping_potency", Names.item_and_block_settings + "." + Names.phoenix_down, 1, 0, 5);
		Settings.PhoenixDown.healPercentageOfMaxLife = ConfigurationHandler.getInt("heal_percentage_of_max_life", Names.item_and_block_settings + "." + Names.phoenix_down, 100, 0, 100);
		Settings.PhoenixDown.removeNegativeStatus = ConfigurationHandler.getBoolean("remove_negative_status", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryDamageResistance = ConfigurationHandler.getBoolean("give_temporary_damage_resistance", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryRegeneration = ConfigurationHandler.getBoolean("give_temporary_regeneration", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou = ConfigurationHandler.getBoolean("give_temporary_fire_resistance_if_fire_damage_killed_you", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou = ConfigurationHandler.getBoolean("give_temporary_water_breathing_if_drowning_killed_you", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.phoenix_down, true);

		//pyromancer staff configs
		Settings.PyromancerStaff.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.pyromancer_staff, 5, 0, 10);
		Settings.PyromancerStaff.fireChargeLimit = ConfigurationHandler.getInt("fire_charge_limit", Names.item_and_block_settings + "." + Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.fireChargeCost = ConfigurationHandler.getInt("fire_charge_cost", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.fireChargeWorth = ConfigurationHandler.getInt("fire_charge_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.ghastAbsorbWorth = ConfigurationHandler.getInt("ghast_absorb_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderLimit = ConfigurationHandler.getInt("blaze_powder_limit", Names.item_and_block_settings + "." + Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.blazePowderCost = ConfigurationHandler.getInt("blaze_powder_cost", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderWorth = ConfigurationHandler.getInt("blaze_powder_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazeAbsorbWorth = ConfigurationHandler.getInt("blaze_absorb_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.pyromancer_staff, true);

		//rending gale configs
		Settings.RendingGale.chargeLimit = ConfigurationHandler.getInt("charge_limit", Names.item_and_block_settings + "." + Names.rending_gale, cleanShortMax, 0, cleanIntMax);
		Settings.RendingGale.castChargeCost = ConfigurationHandler.getInt("cast_charge_cost", Names.item_and_block_settings + "." + Names.rending_gale, 1, 0, 3);
		Settings.RendingGale.boltChargeCost = ConfigurationHandler.getInt("bolt_charge_cost", Names.item_and_block_settings + "." + Names.rending_gale, 100, 0, 250);
		Settings.RendingGale.chargeFeatherWorth = ConfigurationHandler.getInt("charge_feather_worth", Names.item_and_block_settings + "." + Names.rending_gale, 100, 1, 250);
		Settings.RendingGale.blockTargetRange = ConfigurationHandler.getInt("block_target_range", Names.item_and_block_settings + "." + Names.rending_gale, 12, 5, 15);
		Settings.RendingGale.pushPullRadius = ConfigurationHandler.getInt("push_pull_radius", Names.item_and_block_settings + "." + Names.rending_gale, 10, 1, 20);
		Settings.RendingGale.canPushProjectiles = ConfigurationHandler.getBoolean("can_push_projectiles", Names.item_and_block_settings + "." + Names.rending_gale, false);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.rending_gale, true);

		//rod of lyssa configs
		Settings.RodOfLyssa.useLeveledFailureRate = ConfigurationHandler.getBoolean("use_leveled_failure_rate", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.levelCapForLeveledFormula = ConfigurationHandler.getInt("level_cap_for_leveled_formula", Names.item_and_block_settings + "." + Names.rod_of_lyssa, 100, 1, 900);
		Settings.RodOfLyssa.flatStealFailurePercentRate = ConfigurationHandler.getInt("flat_steal_failure_percent_rate", Names.item_and_block_settings + "." + Names.rod_of_lyssa, 10, 0, 100);
		Settings.RodOfLyssa.stealFromVacantSlots = ConfigurationHandler.getBoolean("steal_from_vacant_slots", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.failStealFromVacantSlots = ConfigurationHandler.getBoolean("fail_steal_from_vacant_slots", Names.item_and_block_settings + "." + Names.rod_of_lyssa, false);
		Settings.RodOfLyssa.angerOnStealFailure = ConfigurationHandler.getBoolean("anger_on_steal_failure", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);

		//sojourners staff configs
		List<String> torches = ImmutableList.of();
		Settings.SojournerStaff.torches = ConfigurationHandler.getStringList("torches", Names.item_and_block_settings + "." + Names.sojourner_staff, torches);
		Settings.SojournerStaff.maxCapacityPerItemType = ConfigurationHandler.getInt("max_capacity_per_item_type", Names.item_and_block_settings + "." + Names.sojourner_staff, 1500, 1, itemCap);
		Settings.SojournerStaff.maxRange = ConfigurationHandler.getInt("max_range", Names.item_and_block_settings + "." + Names.sojourner_staff, 30, 1, 30);
		Settings.SojournerStaff.tilePerCostMultiplier = ConfigurationHandler.getInt("tile_per_cost_multiplier", Names.item_and_block_settings + "." + Names.sojourner_staff, 6, 6, 30);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.sojourner_staff, true);

		//twilight cloak configs
		Settings.TwilightCloak.maxLightLevel = ConfigurationHandler.getInt("max_light_level", Names.item_and_block_settings + "." + Names.twilight_cloak, 4, 0, 15);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.twilight_cloak, true);

		//void tear configs
		Settings.VoidTear.itemLimit = ConfigurationHandler.getInt("item_limit", Names.item_and_block_settings + "." + Names.void_tear, 2000000000, 0, cleanIntMax);
		Settings.VoidTear.absorbWhenCreated = ConfigurationHandler.getBoolean("absorb_when_created", Names.item_and_block_settings + "." + Names.void_tear, true);
		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.void_tear, true);

		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings, true);
	}
}
