package xreliquary.handler;


import com.google.common.collect.ImmutableList;
import lib.enderwizards.sandstone.mod.config.ConfigReference;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.Reliquary;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.io.File;
import java.util.ArrayList;
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

		if (configuration.hasChanged())
		{
			configuration.save();
		}
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
	int itemCap = 9999;
	int cleanShortMax = 30000;
	int cleanIntMax = 2000000000;

	//global HUD positions

	//easy mode recipes


	//alkahestry tome configs
	Reliquary.CONFIG.require(Names.alkahestry_tome, "redstone_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));

	//altar configs
	Reliquary.CONFIG.require(Names.altar, "redstone_cost", new ConfigReference(3).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.altar, "time_in_minutes", new ConfigReference(20).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.altar, "maximum_time_variance_in_minutes", new ConfigReference(5).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.altar, "output_light_level_while_active", new ConfigReference(16).setMaximumValue(16).setMinimumValue(0));

	//angelic feather configs
	Reliquary.CONFIG.require(Names.angelic_feather, "hunger_cost_percent", new ConfigReference(50).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.angelic_feather, "leaping_potency", new ConfigReference(1).setMinimumValue(0).setMaximumValue(5));

	//angelheart vial configs
	Reliquary.CONFIG.require(Names.angelheart_vial, "heal_percentage_of_max_life", new ConfigReference(25));
	Reliquary.CONFIG.require(Names.angelheart_vial, "remove_negative_status", new ConfigReference(true));

	//apothecary cauldron configs
	List<String> heatSources = ImmutableList.of();
	Reliquary.CONFIG.require(Names.apothecary_cauldron, "redstone_limit", new ConfigReference(5).setMinimumValue(0).setMaximumValue(100));
	Reliquary.CONFIG.require(Names.apothecary_cauldron, "cook_time", new ConfigReference(160).setMinimumValue(20).setMaximumValue(32000));
	Reliquary.CONFIG.require(Names.apothecary_cauldron, "heat_sources", new ConfigReference(heatSources));

	//destruction catalyst configs
	Reliquary.CONFIG.require(Names.destruction_catalyst, "mundane_blocks", new ConfigReference(new ArrayList<String>(ItemDestructionCatalyst.ids)));
	Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_cost", new ConfigReference(3).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.destruction_catalyst, "explosion_radius", new ConfigReference(1).setMinimumValue(1).setMaximumValue(5));
	Reliquary.CONFIG.require(Names.destruction_catalyst, "centered_explosion", new ConfigReference(false));
	Reliquary.CONFIG.require(Names.destruction_catalyst, "perfect_cube", new ConfigReference(true));

	//emperor's chalice configs
	Reliquary.CONFIG.require(Names.emperor_chalice, "hunger_satiation_multiplier", new ConfigReference(4).setMinimumValue(0));

	//ender staff configs
	Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_cast_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_node_warp_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.ender_staff, "node_warp_cast_time", new ConfigReference(60).setMinimumValue(10));

	//fortune coin configs
	Reliquary.CONFIG.require(Names.fortune_coin, "disable_audio", new ConfigReference(false));
	Reliquary.CONFIG.require(Names.fortune_coin, "standard_pull_distance", new ConfigReference(5));
	Reliquary.CONFIG.require(Names.fortune_coin, "long_range_pull_distance", new ConfigReference(15));

	//glacial staff configs
	Reliquary.CONFIG.require(Names.glacial_staff, "snowball_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.glacial_staff, "snowball_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.glacial_staff, "snowball_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.glacial_staff, "snowball_damage", new ConfigReference(3).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.glacial_staff, "snowball_damage_bonus_fire_immune", new ConfigReference(3).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.glacial_staff, "snowball_damage_bonus_blaze", new ConfigReference(6).setMinimumValue(0));

	//harvest rod configs
	Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_luck_percent_chance", new ConfigReference(33).setMinimumValue(1).setMaximumValue(100));
	Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_luck_rolls", new ConfigReference(2).setMinimumValue(0).setMaximumValue(7));
	Reliquary.CONFIG.require(Names.harvest_rod, "harvest_break_radius", new ConfigReference(2).setMinimumValue(0).setMaximumValue(5));

	//hero's medallion config
	Reliquary.CONFIG.require(Names.hero_medallion, "experience_level_maximum", new ConfigReference(30).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.hero_medallion, "experience_level_minimum", new ConfigReference(0).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.hero_medallion, "experience_limit", new ConfigReference(cleanIntMax).setMinimumValue(0).setMaximumValue(cleanIntMax));

	//ice rod configs
	Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_damage", new ConfigReference(2).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_damage_bonus_fire_immune", new ConfigReference(2).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_damage_bonus_blaze", new ConfigReference(4).setMinimumValue(0));

	//infernal claws configs
	Reliquary.CONFIG.require(Names.infernal_claws, "hunger_cost_percent", new ConfigReference(10).setMinimumValue(0));

	//infernal chalice configs
	Reliquary.CONFIG.require(Names.infernal_chalice, "hunger_cost_percent", new ConfigReference(5).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.infernal_chalice, "fluid_limit", new ConfigReference(500000).setMinimumValue(0).setMaximumValue(cleanIntMax));

	//interdiction torch configs
	//see post init for entity configs
	Reliquary.CONFIG.require(Names.interdiction_torch, "push_radius", new ConfigReference(5).setMinimumValue(1).setMaximumValue(15));
	Reliquary.CONFIG.require(Names.interdiction_torch, "can_push_projectiles", new ConfigReference(false));

	//kraken shell configs
	Reliquary.CONFIG.require(Names.kraken_shell, "hunger_cost_percent", new ConfigReference(25).setMinimumValue(0));

	//lantern of paranoia configs
	Reliquary.CONFIG.require(Names.lantern_of_paranoia, "min_light_level", new ConfigReference(8).setMinimumValue(0).setMaximumValue(15));
	Reliquary.CONFIG.require(Names.lantern_of_paranoia, "placement_scan_radius", new ConfigReference(6).setMinimumValue(1).setMaximumValue(15));
	//Reliquary.CONFIG.require(Names.lantern_of_paranoia, "only_place_on_visible_blocks", new ConfigReference(false));

	//fertile_lilypad of fertility configs
	Reliquary.CONFIG.require(Names.fertile_lilypad, "seconds_between_growth_ticks", new ConfigReference(47).setMinimumValue(1));
	Reliquary.CONFIG.require(Names.fertile_lilypad, "tile_range", new ConfigReference(4).setMinimumValue(1).setMaximumValue(15));
	Reliquary.CONFIG.require(Names.fertile_lilypad, "full_potency_range", new ConfigReference(1).setMinimumValue(1).setMaximumValue(15));

	//midas touchstone configs
	List<String> goldItems = ImmutableList.of();
	Reliquary.CONFIG.require(Names.midas_touchstone, "gold_items", new ConfigReference(goldItems));
	Reliquary.CONFIG.require(Names.midas_touchstone, "ticks_between_repair_ticks", new ConfigReference(4).setMinimumValue(1).setMaximumValue(cleanShortMax));
	Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_worth", new ConfigReference(4).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));

	//phoenix down configs
	Reliquary.CONFIG.require(Names.phoenix_down, "hunger_cost_percent", new ConfigReference(25).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.phoenix_down, "leaping_potency", new ConfigReference(1).setMinimumValue(0).setMaximumValue(5));
	Reliquary.CONFIG.require(Names.phoenix_down, "heal_percentage_of_max_life", new ConfigReference(100));
	Reliquary.CONFIG.require(Names.phoenix_down, "remove_negative_status", new ConfigReference(true));
	Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_damage_resistance", new ConfigReference(true));
	Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_regeneration", new ConfigReference(true));
	Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_fire_resistance_if_fire_damage_killed_you", new ConfigReference(true));
	Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_water_breathing_if_drowning_killed_you", new ConfigReference(true));

	//pyromancer staff configs
	Reliquary.CONFIG.require(Names.pyromancer_staff, "hunger_cost_percent", new ConfigReference(5).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "fire_charge_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "fire_charge_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "fire_charge_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "ghast_absorb_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_powder_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_powder_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_powder_worth", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_absorb_worth", new ConfigReference(1).setMinimumValue(0));

	//rending gale configs
	Reliquary.CONFIG.require(Names.rending_gale, "charge_limit", new ConfigReference(cleanShortMax).setMinimumValue(0).setMaximumValue(cleanIntMax));
	Reliquary.CONFIG.require(Names.rending_gale, "cast_charge_cost", new ConfigReference(1).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.rending_gale, "bolt_charge_cost", new ConfigReference(100).setMinimumValue(0));
	Reliquary.CONFIG.require(Names.rending_gale, "charge_feather_worth", new ConfigReference(100).setMinimumValue(1));
	Reliquary.CONFIG.require(Names.rending_gale, "block_target_range", new ConfigReference(12).setMaximumValue(15));
	Reliquary.CONFIG.require(Names.rending_gale, "push_pull_radius", new ConfigReference(10).setMinimumValue(1));
	Reliquary.CONFIG.require(Names.rending_gale, "can_push_projectiles", new ConfigReference(false));

	//rod of lyssa configs
	Reliquary.CONFIG.require(Names.rod_of_lyssa, "use_leveled_failure_rate", new ConfigReference(true));
	Reliquary.CONFIG.require(Names.rod_of_lyssa, "level_cap_for_leveled_formula", new ConfigReference(100).setMinimumValue(1).setMaximumValue(900));
	Reliquary.CONFIG.require(Names.rod_of_lyssa, "flat_steal_failure_percent_rate", new ConfigReference(10).setMinimumValue(0).setMaximumValue(100));
	Reliquary.CONFIG.require(Names.rod_of_lyssa, "steal_from_vacant_slots", new ConfigReference(true));
	Reliquary.CONFIG.require(Names.rod_of_lyssa, "fail_steal_from_vacant_slots", new ConfigReference(false));
	Reliquary.CONFIG.require(Names.rod_of_lyssa, "anger_on_steal_failure", new ConfigReference(true));

	//sojourners staff configs
	List<String> torches = ImmutableList.of();
	Reliquary.CONFIG.require(Names.sojourner_staff, "torches", new ConfigReference(torches));
	Reliquary.CONFIG.require(Names.sojourner_staff, "max_capacity_per_item_type", new ConfigReference(1500).setMinimumValue(1).setMaximumValue(itemCap));
	Reliquary.CONFIG.require(Names.sojourner_staff, "max_range", new ConfigReference(30).setMinimumValue(1).setMaximumValue(30));
	Reliquary.CONFIG.require(Names.sojourner_staff, "tile_per_cost_multiplier", new ConfigReference(6).setMinimumValue(6).setMaximumValue(30));

	//twilight cloak configs
	Reliquary.CONFIG.require(Names.twilight_cloak, "max_light_level", new ConfigReference(4).setMinimumValue(0).setMaximumValue(15));
	//Reliquary.CONFIG.require(Names.twilight_cloak, "only_works_at_night", new ConfigReference(false));

	//void tear configs
	Reliquary.CONFIG.require(Names.void_tear, "item_limit", new ConfigReference(2000000000).setMinimumValue(0).setMaximumValue(cleanIntMax));
	Reliquary.CONFIG.require(Names.void_tear, "absorb_when_created", new ConfigReference(true));
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

		Reliquary.CONFIG.require(Names.rending_gale, "entities_that_can_be_pushed", new ConfigReference(entityNames));
		Reliquary.CONFIG.require(Names.rending_gale, "projectiles_that_can_be_pushed", new ConfigReference(projectileNames));

		Reliquary.CONFIG.require(Names.seeker_shot, "entities_that_can_be_hunted", new ConfigReference(entityNames));

	}
*/

}
