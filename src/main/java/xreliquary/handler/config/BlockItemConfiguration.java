package xreliquary.handler.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraftforge.common.config.ConfigCategory;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

import java.util.ArrayList;
import java.util.List;

public class BlockItemConfiguration {
	public static void loadEntitiesSettings() {
		List<String> entityNames = new ArrayList<>();
		for(Object o : EntityList.NAME_TO_CLASS.values()) {
			Class c = (Class) o;
			if(EntityLiving.class.isAssignableFrom(c)) {
				entityNames.add(EntityList.CLASS_TO_NAME.get(o));
			}
		}
		List<String> projectileNames = new ArrayList<>();
		for(Object o : EntityList.NAME_TO_CLASS.values()) {
			Class c = (Class) o;
			if(IProjectile.class.isAssignableFrom(c)) {
				projectileNames.add(EntityList.CLASS_TO_NAME.get(o));
			}
		}

		Settings.InterdictionTorch.entitiesThatCanBePushed = ConfigurationHandler.getStringList("entities_that_can_be_pushed", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.INTERDICTION_TORCH, entityNames, "List of entities that can be pushed by the torch");
		Settings.InterdictionTorch.projectilesThatCanBePushed = ConfigurationHandler.getStringList("projectiles_that_can_be_pushed", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.INTERDICTION_TORCH, projectileNames, "List of projectiles that can be pushed by the torch");

		Settings.RendingGale.entitiesThatCanBePushed = ConfigurationHandler.getStringList("entities_that_can_be_pushed", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.RENDING_GALE, entityNames, "List of entities that can be pushed by Rending Gale");
		Settings.RendingGale.projectilesThatCanBePushed = ConfigurationHandler.getStringList("projectiles_that_can_be_pushed", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.RENDING_GALE, projectileNames, "List of projectiles that can be pushed by Rending Gale");

		Settings.SeekerShot.entitiesThatCanBeHunted = ConfigurationHandler.getStringList("entities_that_can_be_hunted", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.SEEKER_SHOT, entityNames, "Entities that can be tracked by seeker shot");

		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.SEEKER_SHOT);

		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.SEEKER_SHOT));
		category.setComment("Seeker Shot settings");
	}

	public static void loadBlockAndItemSettings() {
		int itemCap = 9999;
		int cleanShortMax = 30000;
		int cleanIntMax = 2000000000;
		ConfigCategory category;
		String categoryKey;

		//alkahestry tome configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ALKAHESTRY_TOME;
		Settings.AlkahestryTome.chargeLimit = ConfigurationHandler.getInt("charge_limit", categoryKey, 1000, 0, itemCap, "Charge limit of the tome");
		ConfigurationHandler.configuration.getCategory(categoryKey).get("charge_limit").setRequiresMcRestart(true);
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Alkahestry Tome settings");

		//altar configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.ALTAR;
		Settings.Altar.redstoneCost = ConfigurationHandler.getInt("redstone_cost", categoryKey, 3, 0, 10, "Number of redstone it costs to activate altar");
		Settings.Altar.timeInMinutes = ConfigurationHandler.getInt("time_in_minutes", categoryKey, 20, 0, 60, "Time in minutes it takes for the altar to create glowstone block");
		Settings.Altar.maximumTimeVarianceInMinutes = ConfigurationHandler.getInt("maximum_time_variance_in_minutes", categoryKey, 5, 0, 15, "Maximum time variance in minutes. A random part of it gets added to the Time in minutes.");
		Settings.Altar.outputLightLevelWhileActive = ConfigurationHandler.getInt("output_light_level_while_active", categoryKey, 16, 16, 0, "Light level that the altar outputs while active");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Altar of Light settings");

		//angelic feather configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ANGELIC_FEATHER;
		Settings.AngelicFeather.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", categoryKey, 50, 0, 100, "Percent hunger used to heal player per 1 damage that would be taken otherwise.");
		Settings.AngelicFeather.leapingPotency = ConfigurationHandler.getInt("leaping_potency", categoryKey, 1, 0, 5, "Potency of the leaping effect");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Angelic Feather settings");

		//angelheart vial configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ANGELHEART_VIAL;
		Settings.AngelHeartVial.healPercentageOfMaxLife = ConfigurationHandler.getInt("heal_percentage_of_max_life", categoryKey, 25, 0, 100, "Percent of life that gets healed when the player would die");
		Settings.AngelHeartVial.removeNegativeStatus = ConfigurationHandler.getBoolean("remove_negative_status", categoryKey, true, "Whether the player gets negative statuses removed");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Angelheart Vial settings");

		//apothecary cauldron configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.APOTHECARY_CAULDRON;
		List<String> heatSources = ImmutableList.of();
		Settings.ApothecaryCauldron.redstoneLimit = ConfigurationHandler.getInt("redstone_limit", categoryKey, 3, 0, 5, "Limit of redstone that can be used in cauldron to make potion last longer");
		Settings.ApothecaryCauldron.glowstoneLimit = ConfigurationHandler.getInt("glowstone_limit", categoryKey, 2, 0, 4, "Limit of glowstone that can be used in cauldron to make potion more potent");
		Settings.ApothecaryCauldron.cookTime = ConfigurationHandler.getInt("cook_time", categoryKey, 160, 20, 32000, "Time it takes to cook potion");
		Settings.ApothecaryCauldron.heatSources = ConfigurationHandler.getStringList("heat_sources", categoryKey, heatSources, "List of acceptable heat sources");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Apothecary Cauldron settings");

		//destruction catalyst configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.DESTRUCTION_CATALYST;
		Settings.DestructionCatalyst.mundaneBlocks = ConfigurationHandler.getStringList("mundane_blocks", categoryKey, new ArrayList<>(ItemDestructionCatalyst.ids), "List of mundane blocks the catalyst will break");
		Settings.DestructionCatalyst.gunpowderCost = ConfigurationHandler.getInt("gunpowder_cost", categoryKey, 3, 0, 10, "Number of gunpowder it costs per catalyst use");
		Settings.DestructionCatalyst.gunpowderWorth = ConfigurationHandler.getInt("gunpowder_worth", categoryKey, 1, 0, 3, "Number of gunpowder that gets added to catalyst per one that's consumed from players inventory");
		Settings.DestructionCatalyst.gunpowderLimit = ConfigurationHandler.getInt("gunpowder_limit", categoryKey, 250, 0, itemCap, "Number of gunpowder that can be stored in destruction catalyst");
		Settings.DestructionCatalyst.explosionRadius = ConfigurationHandler.getInt("explosion_radius", categoryKey, 1, 1, 5, "Radius of the explosion");
		Settings.DestructionCatalyst.centeredExplosion = ConfigurationHandler.getBoolean("centered_explosion", categoryKey, false, "Whether the explosion is centered on the block that gets clicked");
		Settings.DestructionCatalyst.perfectCube = ConfigurationHandler.getBoolean("perfect_cube", categoryKey, true, "Whether the explosion makes a perfect cube hole");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Destruction Catalyst settings");

		//emperor's chalice configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.EMPEROR_CHALICE;
		Settings.EmperorChalice.hungerSatiationMultiplier = ConfigurationHandler.getInt("hunger_satiation_multiplier", categoryKey, 4, 0, 10, "How much saturation is added in addition to filling the hunger");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Emperor Chalice settings");

		//ender staff configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ENDER_STAFF;
		Settings.EnderStaff.enderPearlCastCost = ConfigurationHandler.getInt("ender_pearl_cast_cost", categoryKey, 1, 0, 3, "Number of ender pearls per use");
		Settings.EnderStaff.enderPearlNodeWarpCost = ConfigurationHandler.getInt("ender_pearl_node_warp_cost", categoryKey, 1, 0, 3, "Number of ender pearls per teleportation to the wraith node");
		Settings.EnderStaff.enderPearlWorth = ConfigurationHandler.getInt("ender_pearl_worth", categoryKey, 1, 0, 10, "Number of ender pearls that get added to the staff per one that's consumed from players inventory");
		Settings.EnderStaff.enderPearlLimit = ConfigurationHandler.getInt("ender_pearl_limit", categoryKey, 250, 0, itemCap, "Number of ender pearls that the ender staff can store");
		Settings.EnderStaff.nodeWarpCastTime = ConfigurationHandler.getInt("node_warp_cast_time", categoryKey, 60, 10, 120, "Time it takes to teleport to the wraith node");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Ender Staff settings");

		//fortune coin configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.FORTUNE_COIN;
		Settings.FortuneCoin.disableAudio = ConfigurationHandler.getBoolean("disable_audio", categoryKey, false, "Disables the sound of fortune coin teleporting stuff");
		Settings.FortuneCoin.standardPullDistance = ConfigurationHandler.getInt("standard_pull_distance", categoryKey, 5, 3, 10, "The distance that it pulls from when activated");
		Settings.FortuneCoin.longRangePullDistance = ConfigurationHandler.getInt("long_range_pull_distance", categoryKey, 15, 9, 30, "The distance that it pulls from when right click is held");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Fortune Coin settings");

		//glacial staff configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.GLACIAL_STAFF;
		Settings.GlacialStaff.snowballLimit = ConfigurationHandler.getInt("snowball_limit", categoryKey, 250, 0, itemCap, "Number of snowballs the staff can hold");
		Settings.GlacialStaff.snowballCost = ConfigurationHandler.getInt("snowball_cost", categoryKey, 1, 0, 3, "Number of snowballs it costs when the staff is used");
		Settings.GlacialStaff.snowballWorth = ConfigurationHandler.getInt("snowball_worth", categoryKey, 1, 0, 3, "Number of snowballs that get added to the staff per one that's consumed from player's inventory");
		Settings.GlacialStaff.snowballDamage = ConfigurationHandler.getInt("snowball_damage", categoryKey, 3, 0, 6, "The damage that snowballs cause");
		Settings.GlacialStaff.snowballDamageBonusFireImmune = ConfigurationHandler.getInt("snowball_damage_bonus_fire_immune", categoryKey, 3, 0, 6, "The damage bonus against entities that are immune to fire");
		Settings.GlacialStaff.snowballDamageBonusBlaze = ConfigurationHandler.getInt("snowball_damage_bonus_blaze", categoryKey, 6, 0, 12, "The damage bonus against blaze");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Glacial Staff settings");

		//harvest rod configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.HARVEST_ROD;
		Settings.HarvestRod.boneMealLimit = ConfigurationHandler.getInt("bonemeal_limit", categoryKey, 250, 0, itemCap, "Number of bonemeal the rod can hold");
		Settings.HarvestRod.boneMealCost = ConfigurationHandler.getInt("bonemeal_cost", categoryKey, 1, 0, 3, "Number of bonemeal consumed per use");
		Settings.HarvestRod.boneMealWorth = ConfigurationHandler.getInt("bonemeal_worth", categoryKey, 1, 0, 3, "Number of bonemeal that gets added to the rod per one that's consumed from player's inventory");
		Settings.HarvestRod.boneMealLuckPercentChance = ConfigurationHandler.getInt("bonemeal_luck_percent_chance", categoryKey, 33, 1, 100, "Percent chance that a bonemeal will get applied during a luck roll");
		Settings.HarvestRod.boneMealLuckRolls = ConfigurationHandler.getInt("bonemeal_luck_rolls", categoryKey, 2, 0, 7, "Number of times that a rod may apply additional luck based bonemeal");
		Settings.HarvestRod.AOERadius = ConfigurationHandler.getInt("aoe_radius", categoryKey, 2, 0, 5, "Radius in which harvest rod breaks crops, bonemeals/plants/hoes blocks");
		Settings.HarvestRod.AOECooldown = ConfigurationHandler.getInt("aoe_cooldown", categoryKey, 3, 1, 20, "Ticks in between bonemealing/planting/hoeing blocks when player is using one of these AOE actions");
		Settings.HarvestRod.maxCapacityPerPlantable = ConfigurationHandler.getInt("max_capacity_per_plantable", categoryKey, 250, 0, itemCap, "Maximum number of units harvest rod can hold per plantable item");
		Settings.HarvestRod.pedestalRange = ConfigurationHandler.getInt("pedestal_range", categoryKey, 4, 1, 20, "Range at which harvest rod will automatically hoe/plant/bonemeal/break crops around pedestal");
		Settings.HarvestRod.pedestalCooldown = (byte) ConfigurationHandler.getInt("pedestal_cooldown", categoryKey, 5, 1, 20, "Ticks in between harvest rod actions when in pedestal");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Harvest Rod settings");

		//hero's medallion config
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.HERO_MEDALLION;
		Settings.HeroMedallion.experienceLevelMaximum = ConfigurationHandler.getInt("experience_level_maximum", categoryKey, 200, 0, 1000, "A player's experience level at which pulling from the medallion to player will stop");
		Settings.HeroMedallion.experienceLevelMinimum = ConfigurationHandler.getInt("experience_level_minimum", categoryKey, 0, 0, 30, "A player's experience level at which the medallion will stop pulling from the player");
		Settings.HeroMedallion.experienceLimit = ConfigurationHandler.getInt("experience_limit", categoryKey, cleanIntMax, 0, cleanIntMax, "Experience level that the medallion can hold");
		Settings.HeroMedallion.experienceDrop = ConfigurationHandler.getInt("experience_drop", categoryKey, 9, 0, 100, "How much experience gets dropped on ground when hero's medallion is right clicked on it (9 is the first level of player xp)");
		Settings.HeroMedallion.pedestalCoolDown = ConfigurationHandler.getInt("pedestal_cooldown", categoryKey, 20, 1, 100, "Cooldown between hero medallion tries to fix mending items in nearby pedestals");
		Settings.HeroMedallion.pedestalRange = ConfigurationHandler.getInt("pedestal_range", categoryKey, 5, 1, 20, "Range in which pedestals are checked for items with mending enchant that need fixing");
		Settings.HeroMedallion.pedestalRepairStepXP = ConfigurationHandler.getInt("pedestal_repair_step_xp", categoryKey, 5, 1, 20, "Maximum amount of xp that is used each time medallion repairs items");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Hero Medallion settings");

		//ice rod configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ICE_MAGUS_ROD;
		Settings.IceMagusRod.snowballLimit = ConfigurationHandler.getInt("snowball_limit", categoryKey, 250, 0, itemCap, "Number of snowballs the rod can hold");
		Settings.IceMagusRod.snowballCost = ConfigurationHandler.getInt("snowball_cost", categoryKey, 1, 0, 3, "Number of snowballs it costs when the rod is used");
		Settings.IceMagusRod.snowballWorth = ConfigurationHandler.getInt("snowball_worth", categoryKey, 1, 0, 3, "Number of snowballs that get added to the rod per one that's consumed from player's inventory");
		Settings.IceMagusRod.snowballDamage = ConfigurationHandler.getInt("snowball_damage", categoryKey, 2, 0, 4, "The damage that snowballs cause");
		Settings.IceMagusRod.snowballDamageBonusFireImmune = ConfigurationHandler.getInt("snowball_damage_bonus_fire_immune", categoryKey, 2, 0, 4, "Damage bonus against fire immune mobs");
		Settings.IceMagusRod.snowballDamageBonusBlaze = ConfigurationHandler.getInt("snowball_damage_bonus_blaze", categoryKey, 4, 0, 8, "Damage bonus against blaze");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Ice Magus Rod settings");

		//infernal claws configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.INFERNAL_CLAWS;
		Settings.InfernalClaws.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", categoryKey, 5, 0, 30, "Percent hunger used to heal player per 1 damage that would be taken otherwise.");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Infernal Claws settings");

		//infernal chalice configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.INFERNAL_CHALICE;
		Settings.InfernalChalice.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", categoryKey, 1, 0, 10, "Percent hunger used to heal player per 1 damage that would be taken otherwise.");
		Settings.InfernalChalice.fluidLimit = ConfigurationHandler.getInt("fluid_limit", categoryKey, 500000, 0, cleanIntMax, "Millibuckets of lava that the chalice can hold");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Infernal Chalice settings");

		//infernal tear
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.INFERNAL_TEAR;
		Settings.InfernalTear.absorbWhenCreated = ConfigurationHandler.getBoolean("absorb_when_created", categoryKey, false, "Whether the infernal tear starts absorbing immediately after it is set to item type");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Infernal Tear settings");

		//interdiction torch configs
		//see post init for entity configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.INTERDICTION_TORCH;
		Settings.InterdictionTorch.pushRadius = ConfigurationHandler.getInt("push_radius", categoryKey, 5, 1, 15, "Radius in which the torch can push out mobs");
		Settings.InterdictionTorch.canPushProjectiles = ConfigurationHandler.getBoolean("can_push_projectiles", categoryKey, false, "Whether the torch can push projectiles");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Interdiction Torch settings");

		//kraken shell configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.KRAKEN_SHELL;
		Settings.KrakenShell.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", categoryKey, 25, 0, 50, "Percent hunger used to heal player per 1 damage that would be taken otherwise.");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Kraken Shell settings");

		//lantern of paranoia configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.LANTERN_OF_PARANOIA;
		Settings.LanternOfParanoia.minLightLevel = ConfigurationHandler.getInt("min_light_level", categoryKey, 8, 0, 15, "Minimum light level below which the lantern will place torches");
		Settings.LanternOfParanoia.placementScanRadius = ConfigurationHandler.getInt("placement_scan_radius", categoryKey, 6, 1, 15, "Radius in which the lantern checks for light levels and places torches");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Lantern of Paranoia settings");

		//fertile_lilypad of fertility configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.FERTILE_LILYPAD;
		Settings.FertileLilypad.secondsBetweenGrowthTicks = ConfigurationHandler.getInt("seconds_between_growth_ticks", categoryKey, 47, 1, 150, "Interval in seconds at which the lilypad causes growth tick updates");
		Settings.FertileLilypad.tileRange = ConfigurationHandler.getInt("tile_range", categoryKey, 4, 1, 15, "Radius in which lilypad causes growh ticks");
		Settings.FertileLilypad.fullPotencyRange = ConfigurationHandler.getInt("full_potency_range", categoryKey, 1, 1, 15, "Radius around lilypad where the growth ticks occur the most often");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Lilypad of Fertility settings");

		//midas touchstone configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.MIDAS_TOUCHSTONE;
		List<String> goldItems = ImmutableList.of();
		Settings.MidasTouchstone.goldItems = ConfigurationHandler.getStringList("gold_items", categoryKey, goldItems, "Gold items that can be repaired by the touchstone");
		Settings.MidasTouchstone.ticksBetweenRepairTicks = ConfigurationHandler.getInt("ticks_between_repair_ticks", categoryKey, 4, 1, cleanShortMax, "Number of ticks between repairs");
		Settings.MidasTouchstone.glowstoneCost = ConfigurationHandler.getInt("glowstone_cost", categoryKey, 1, 0, 3, "Number of glowstone that the repair costs");
		Settings.MidasTouchstone.glowstoneWorth = ConfigurationHandler.getInt("glowstone_worth", categoryKey, 4, 0, 12, "Number of glowstone that gets added to the touchstone per one in player's inventory");
		Settings.MidasTouchstone.glowstoneLimit = ConfigurationHandler.getInt("glowstone_limit", categoryKey, 250, 0, itemCap, "Number of glowstone the touchstone can hold");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Midas Touchstone settings");

		//mob charm configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.MOB_CHARM;
		Settings.MobCharm.durability = ConfigurationHandler.getInt("durability", categoryKey, 80, 20, 1000, "Total durability of Mob Charm");
		Settings.MobCharm.damagePerKill = ConfigurationHandler.getInt("damage_per_kill", categoryKey, 1, 0, 40, "Damage that Mob Charm takes when player kills mob it protects them from");
		Settings.MobCharm.dropDurabilityRepair = ConfigurationHandler.getInt("drop_durability_repair", categoryKey, 20, 1, 200, "Sets how much durability of Mob Charm gets repaired per special drop");
		Settings.MobCharm.maxCharmsToDisplay = ConfigurationHandler.getInt("max_charms_to_display", categoryKey, 6, 1, 20, "Maximum charms that will get displayed in HUD");
		Settings.MobCharm.displayPosition = ConfigurationHandler.getInt("display_position", categoryKey, 1, 1, 3, "Display position 1-right, 2-top, 3-left");
		Settings.MobCharm.keepAlmostDestroyedDisplayed = ConfigurationHandler.getBoolean("keep_almost_destroyed_displayed", categoryKey, true, "Determines if almost destroyed charms stay displayed in the hud");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Mob Charm settings");

		//phoenix down configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.PHOENIX_DOWN;
		Settings.PhoenixDown.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", categoryKey, 25, 0, 50, "Percent hunger used to heal player per 1 damage that would be taken otherwise");
		Settings.PhoenixDown.leapingPotency = ConfigurationHandler.getInt("leaping_potency", categoryKey, 1, 0, 5, "Potency of the leaping effect");
		Settings.PhoenixDown.healPercentageOfMaxLife = ConfigurationHandler.getInt("heal_percentage_of_max_life", categoryKey, 100, 0, 100, "Percent of life that gets healed when the player would die");
		Settings.PhoenixDown.removeNegativeStatus = ConfigurationHandler.getBoolean("remove_negative_status", categoryKey, true, "Whether the player gets negative statuses removed when they were saved by Phoenix Down");
		Settings.PhoenixDown.giveTemporaryDamageResistance = ConfigurationHandler.getBoolean("give_temporary_damage_resistance", categoryKey, true, "Whether to give temporary damage resistance when the player would die");
		Settings.PhoenixDown.giveTemporaryRegeneration = ConfigurationHandler.getBoolean("give_temporary_regeneration", categoryKey, true, "Whether to give temporary regeneration when the player would die");
		Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou = ConfigurationHandler.getBoolean("give_temporary_fire_resistance_if_fire_damage_killed_you", categoryKey, true, "Whether to give temporary fire resistance when the player would die. Applies only when the player is being hurt by fire damage.");
		Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou = ConfigurationHandler.getBoolean("give_temporary_water_breathing_if_drowning_killed_you", categoryKey, true, "Whether to give temporary damage resistance when the player would die. Applies only when the player is drowning.");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Phoenix Down settings");

		//pyromancer staff configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.PYROMANCER_STAFF;
		Settings.PyromancerStaff.hungerCostPercent = ConfigurationHandler.getInt("hunger_cost_percent", categoryKey, 5, 0, 10, "Percent hunger used to heal player per 1 damage that would be taken otherwise");
		Settings.PyromancerStaff.fireChargeLimit = ConfigurationHandler.getInt("fire_charge_limit", categoryKey, 250, 0, itemCap, "Number of fire charges the staff can hold");
		Settings.PyromancerStaff.fireChargeCost = ConfigurationHandler.getInt("fire_charge_cost", categoryKey, 1, 0, 3, "Number of fire charges used when the staff is fired");
		Settings.PyromancerStaff.fireChargeWorth = ConfigurationHandler.getInt("fire_charge_worth", categoryKey, 1, 0, 3, "Number of fire charges that get added to the staff per one that's consumed from player's inventory");
		Settings.PyromancerStaff.ghastAbsorbWorth = ConfigurationHandler.getInt("ghast_absorb_worth", categoryKey, 1, 0, 3, "Number of fire charges added to the staff per one that was shot by ghast and gets absorbed by the staff");
		Settings.PyromancerStaff.blazePowderLimit = ConfigurationHandler.getInt("blaze_powder_limit", categoryKey, 250, 0, itemCap, "Number of blaze powder the staff can hold");
		Settings.PyromancerStaff.blazePowderCost = ConfigurationHandler.getInt("blaze_powder_cost", categoryKey, 1, 0, 3, "Number of blaze powder used when staff is fired");
		Settings.PyromancerStaff.blazePowderWorth = ConfigurationHandler.getInt("blaze_powder_worth", categoryKey, 1, 0, 3, "Number of blaze powder that gets added to the staff per one that's consumed from player's inventory");
		Settings.PyromancerStaff.blazeAbsorbWorth = ConfigurationHandler.getInt("blaze_absorb_worth", categoryKey, 1, 0, 3, "Number of blaze powder added to the staff per one fireball that was shot by blaze and gets absorbed by the staff");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Pyromancer Staff settings");

		//rending gale configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.RENDING_GALE;
		Settings.RendingGale.chargeLimit = ConfigurationHandler.getInt("charge_limit", categoryKey, cleanShortMax, 0, cleanIntMax, "Number of feathers the rending gale can hold");
		Settings.RendingGale.castChargeCost = ConfigurationHandler.getInt("cast_charge_cost", categoryKey, 1, 0, 3, "Number of feathers used when the rending gale is cast in flight mode");
		Settings.RendingGale.boltChargeCost = ConfigurationHandler.getInt("bolt_charge_cost", categoryKey, 100, 0, 250, "Number of feathers used to cast a lightning bolt");
		Settings.RendingGale.chargeFeatherWorth = ConfigurationHandler.getInt("charge_feather_worth", categoryKey, 100, 1, 250, "Number of feathers that get added to the rending gale per one that's consumed from player's inventory");
		Settings.RendingGale.blockTargetRange = ConfigurationHandler.getInt("block_target_range", categoryKey, 12, 5, 15, "How far a lightning block can be cast");
		Settings.RendingGale.pushPullRadius = ConfigurationHandler.getInt("push_pull_radius", categoryKey, 10, 1, 20, "Radius in which entities can be pushed/pulled");
		Settings.RendingGale.canPushProjectiles = ConfigurationHandler.getBoolean("can_push_projectiles", categoryKey, false, "Whether the rending gale can push projectiles");
		Settings.RendingGale.pedestalFlightRange = ConfigurationHandler.getInt("pedestal_flight_range", categoryKey, 30, 10, 100, "Range from pedestal at which players will get buffed with flight");
		Settings.RendingGale.pedestalCostPerSecond = ConfigurationHandler.getInt("pedestal_cost_per_second", categoryKey, 5, 1, 20, "Cost per second of buffing players with flight");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Rending Gale settings");

		//rod of lyssa configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ROD_OF_LYSSA;
		Settings.RodOfLyssa.useLeveledFailureRate = ConfigurationHandler.getBoolean("use_leveled_failure_rate", categoryKey, true, "Whether level influences stealing failure rate of the rod");
		Settings.RodOfLyssa.levelCapForLeveledFormula = ConfigurationHandler.getInt("level_cap_for_leveled_formula", categoryKey, 100, 1, 900, "The experience level cap after which the failure rate is at a minimum and doesn't get better");
		Settings.RodOfLyssa.flatStealFailurePercentRate = ConfigurationHandler.getInt("flat_steal_failure_percent_rate", categoryKey, 10, 0, 100, "The flat failure rate in case failure rate isn't influenced by player's level");
		Settings.RodOfLyssa.stealFromVacantSlots = ConfigurationHandler.getBoolean("steal_from_vacant_slots", categoryKey, true, "If set to false it goes through additional 4 accessible slots and looks for items in case the one selected randomly was empty");
		Settings.RodOfLyssa.failStealFromVacantSlots = ConfigurationHandler.getBoolean("fail_steal_from_vacant_slots", categoryKey, false, "Whether stealing from an empty slot triggers failure even if otherwise it would be successful");
		Settings.RodOfLyssa.angerOnStealFailure = ConfigurationHandler.getBoolean("anger_on_steal_failure", categoryKey, true, "Whether entities get angry at player if stealing fails");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Rod of Lyssa settings");

		//sojourners staff configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.SOJOURNER_STAFF;
		List<String> torches = ImmutableList.of();
		Settings.SojournerStaff.torches = ConfigurationHandler.getStringList("torches", categoryKey, torches, "List of torches that are supported by the staff in addition to the default minecraft torch");
		Settings.SojournerStaff.maxCapacityPerItemType = ConfigurationHandler.getInt("max_capacity_per_item_type", categoryKey, 1500, 1, itemCap, "Number of items the staff can store per item type");
		Settings.SojournerStaff.maxRange = ConfigurationHandler.getInt("max_range", categoryKey, 30, 1, 30, "Maximum range at which torches can be placed");
		Settings.SojournerStaff.tilePerCostMultiplier = ConfigurationHandler.getInt("tile_per_cost_multiplier", categoryKey, 6, 6, 30, "Distance after which there is an additional cost for torch placement. The additional cost is the number of times this distance fits in the distance at which the torch is being placed.");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Sojourner Staff settings");

		//twilight cloak configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.TWILIGHT_CLOAK;
		Settings.TwilightCloak.maxLightLevel = ConfigurationHandler.getInt("max_light_level", categoryKey, 4, 0, 15, "Maximum light level at which the player is still invisible to the mobs");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Twilight Cloak settings");

		//void tear configs
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.VOID_TEAR;
		Settings.VoidTear.itemLimit = ConfigurationHandler.getInt("item_limit", categoryKey, 2000000000, 0, cleanIntMax, "Number of items the tear can hold of the item type it is set to");
		Settings.VoidTear.absorbWhenCreated = ConfigurationHandler.getBoolean("absorb_when_created", categoryKey, true, "Whether the void tear starts absorbing immediately after it is set to item type");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Void Tear settings");

		//potions
		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Configs.POTIONS;
		Settings.Potions.maxEffectCount = ConfigurationHandler.getInt(Names.Configs.MAX_EFFECT_COUNT, categoryKey, 1, 1, 6, "Maximum number of effects a potion can have to appear in creative tabs / JEI");
		Settings.Potions.threeIngredients = ConfigurationHandler.getBoolean(Names.Configs.THREE_INGREDIENTS, categoryKey, false, "Whether potions that are made out of three base ingredients appear in creative tabs / JEI");
		Settings.Potions.differentDurations = ConfigurationHandler.getBoolean(Names.Configs.DIFFERENT_DURATIONS, categoryKey, false, "Whether potions augmented with Redstone and Glowstone appear in creative tabs / JEI");
		Settings.Potions.redstoneAndGlowstone = ConfigurationHandler.getBoolean(Names.Configs.REDSTONE_AND_GLOWSTONE, categoryKey, false, "Whether potions with the same effect combination, but different duration appear in creative tabs / JEI");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setRequiresMcRestart(true);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Potions related settings");

		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Blocks.PEDESTAL;
		Settings.Pedestal.meleeWrapperRange = ConfigurationHandler.getInt(Names.Configs.MELEE_WRAPPER_RANGE, categoryKey, 5, 1, 10, "Range of the melee weapons in which these will attack when in pedestal");
		Settings.Pedestal.meleeWrapperCooldown = (byte) ConfigurationHandler.getInt(Names.Configs.MELEE_WRAPPER_COOLDOWN, categoryKey, 5, 1, 200, "How long it takes after a melee weapon swing before it can swing again (in ticks)");
		Settings.Pedestal.bucketWrapperRange = ConfigurationHandler.getInt(Names.Configs.BUCKET_WRAPPER_RANGE, categoryKey, 4, 1, 10, "Range at which bucket will pickup liquid blocks or milk cows");
		Settings.Pedestal.bucketWrapperCooldown = (byte) ConfigurationHandler.getInt(Names.Configs.BUCKET_WRAPPER_COOLDOWN, categoryKey, 40, 1, 200, "How long it takes in between bucket actions (in ticks)");
		Settings.Pedestal.shearsWrapperRange = ConfigurationHandler.getInt(Names.Configs.SHEARS_WRAPPER_RANGE, categoryKey, 4, 1, 10, "How long it takes between shearing actions (in ticks)");
		Settings.Pedestal.shearsWrapperCooldown = (byte) ConfigurationHandler.getInt(Names.Configs.SHEARS_WRAPPER_COOLDOWN, categoryKey, 10, 1, 200, "Range at which shears will shear sheep or shearable blocks");
		Settings.Pedestal.redstoneWrapperRange = ConfigurationHandler.getInt(Names.Configs.REDSTONE_WRAPPER_RANGE, categoryKey, 10, 1, 200, "Range at which pedestal will get turned on if either redstone block gets put in or redstone dust and transmitting pedestal is powered");
		Settings.Pedestal.fishingWrapperSuccessRate = ConfigurationHandler.getInt(Names.Configs.FISHING_WRAPPER_SUCCESS_RATE, categoryKey, 0, 80, 100, "Success rate of fishing in percent. When unsuccessful it will pull the hook too late to catch a fish.");
		Settings.Pedestal.fishingWrapperRetractDelay = ConfigurationHandler.getInt(Names.Configs.FISHING_WRAPPER_RETRACT_DELAY, categoryKey, 1, 2, 20, "Delay in seconds before it would start fishing again after retracting the hook.");
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Pedestal related settings");

		categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS;
		category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("Various settings for Reliquary items and blocks");
	}
}
