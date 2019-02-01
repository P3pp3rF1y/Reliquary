package xreliquary.reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.client.gui.hud.HUDPosition;
import xreliquary.util.potions.PotionMap;

@Config(modid = Reference.MOD_ID)
public class Settings {
	private static final int ITEM_CAP = 9999;
	private static final int CLEAN_SHORT_MAX = 30000;
	private static final int CLEAN_INT_MAX = 2000000000;

	@SuppressWarnings("unused")
	@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(Reference.MOD_ID)) {
				ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}

	@Config.Name("chest_loot_enabled")
	@Config.Comment("Determines whether Reliquary items will be generated in chest loot (mostly mob drops, very rarely some lower level items)")
	@Config.RequiresMcRestart()
	public static boolean chestLootEnabled = true;
	@Config.Name("waila_shift_for_info")
	@Config.Comment("Whether player has to sneak to see additional info in waila")
	public static boolean wailaShiftForInfo = false;
	@Config.Name("mob_drop_crafting_recipes_enabled")
	@Config.Comment("Determines wheter Reliquary mob drops have crafting recipes")
	public static boolean dropCraftingRecipesEnabled = false;
	@Config.Name("mob_drops_enabled")
	@Config.Comment("Whether mobs drop the Reliquary mob drops. This won't remove mob drop items from registry and replace them with something else, but allows to turn off the additional drops when mobs are killed by player. If this is turned off the mob drop crafting recipes turned on by the other setting can be used.")
	@Config.RequiresMcRestart
	public static boolean mobDropsEnabled = true;

  @Config.Name("disable")
  @Config.Comment("Disable sections of the mod")
  public static final DisableSettings Disable = new DisableSettings();

  public static class DisableSettings {
    @Config.Name(Names.Items.ALKAHESTRY_TOME)
    @Config.Comment("Setting to false will remove this tome, altar, and recipes")
    @Config.RequiresMcRestart()
    public boolean enableAlkahestry = true;
  
  }
  
	@Config.Name("potions")
	@Config.Comment("Potions related settings")
	public static final PotionSettings Potions = new PotionSettings();

	public static class PotionSettings {
		@Config.Name("potion_map")
		@Config.Comment("Map of potion ingredients and their effects")
		public String[] potionMap = PotionMap.getDefaultConfigPotionMap();
		@Config.Name("max_effect_count")
		@Config.Comment("Maximum number of effects a potion can have to appear in creative tabs / JEI")
		@Config.RangeInt(min = 1, max = 6)
		public int maxEffectCount = 1;
		@Config.Name("three_ingredients")
		@Config.Comment("Whether potions that are made out of three base ingredients appear in creative tabs / JEI")
		public boolean threeIngredients = false;
		@Config.Name("different_durations")
		@Config.Comment("Whether potions augmented with Redstone and Glowstone appear in creative tabs / JEI")
		public boolean differentDurations = false;
		@Config.Name("redstone_and_glowstone")
		@Config.Comment("Whether potions with the same effect combination, but different duration appear in creative tabs / JEI")
		public boolean redstoneAndGlowstone = false;
	}

	@Config.Name("hud_positions")
	@Config.Comment("Position of mode and/or item display on the screen - used by some of the tools and weapons.")
	public static final HudPos HudPositions = new HudPos();

	public static class HudPos {
		@Config.Name(Names.Items.SOJOURNER_STAFF)
		@Config.Comment("Position of Sojouner Staff HUD")
		public HUDPosition sojournerStaff = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.HANDGUN)
		@Config.Comment("Position of Handgun HUD")
		public HUDPosition handgun = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.ALKAHESTRY_TOME)
		@Config.Comment("Position of Alkahestry Tome HUD")
		public HUDPosition alkahestryTome = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.DESTRUCTION_CATALYST)
		@Config.Comment("Position of Destruction Catalyst HUD")
		public HUDPosition destructionCatalyst = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.ENDER_STAFF)
		@Config.Comment("Position of Ender Staff HUD")
		public HUDPosition enderStaff = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.ICE_MAGUS_ROD)
		@Config.Comment("Position of Ice Magus Rod HUD")
		public HUDPosition iceMagusRod = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.GLACIAL_STAFF)
		@Config.Comment("Position of Glacial Staff HUD")
		public HUDPosition glacialStaff = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.VOID_TEAR)
		@Config.Comment("Position of Void Tear HUD")
		public HUDPosition voidTear = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.MIDAS_TOUCHSTONE)
		@Config.Comment("Position of Midas Touchstone HUD")
		public HUDPosition midasTouchstone = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.HARVEST_ROD)
		@Config.Comment("Position of Harvest Rod HUD")
		public HUDPosition harvestRod = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.INFERNAL_CHALICE)
		@Config.Comment("Position of Infernal Chalice HUD")
		public HUDPosition infernalChalice = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.HERO_MEDALLION)
		@Config.Comment("Position of Hero Medallion HUD")
		public HUDPosition heroMedallion = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.PYROMANCER_STAFF)
		@Config.Comment("Position of Pyromancer Staff HUD")
		public HUDPosition pyromancerStaff = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.RENDING_GALE)
		@Config.Comment("Position of Rending Gale HUD")
		public HUDPosition rendingGale = HUDPosition.BOTTOM_RIGHT;
		@Config.Name(Names.Items.MOB_CHARM)
		@Config.Comment("Position of Mob Charm HUD")
		public HUDPosition mobCharm = HUDPosition.RIGHT;
	}

	@Config.Name("item_settings")
	public static final ItemSettings Items = new ItemSettings();

	public static class ItemSettings {
		@Config.Name("alkahestry_tome")
		@Config.Comment("Alkahestry Tome settings")
		public final AlkahestryTomeSettings AlkahestryTome = new AlkahestryTomeSettings();

		public class AlkahestryTomeSettings {
			@Config.Name("charge_limit")
			@Config.Comment("Charge limit of the tome")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int chargeLimit = 1000;
		}

		@Config.Name("angelic_feather")
		@Config.Comment("Angelic Feather settings")
		public final AngelicFeatherSettings AngelicFeather = new AngelicFeatherSettings();

		public class AngelicFeatherSettings {
			@Config.Name("hunger_cost_percent")
			@Config.Comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
			@Config.RangeInt(min = 0, max = 100)
			public int hungerCostPercent = 50;
			@Config.Name("leaping_potency")
			@Config.Comment("Potency of the leaping effect")
			@Config.RangeInt(min = 0, max = 5)
			public int leapingPotency = 1;
		}

		@Config.Name("angelheart_vial")
		@Config.Comment("Angelheart Vial settings")
		public final AngelHeartVialSettings AngelHeartVial = new AngelHeartVialSettings();

		public class AngelHeartVialSettings {
			@Config.Name("heal_percentage_of_max_life")
			@Config.Comment("Percent of life that gets healed when the player would die")
			@Config.RangeInt(min = 0, max = 100)
			public int healPercentageOfMaxLife = 25;
			@Config.Name("remove_negative_status")
			@Config.Comment("Whether the player gets negative statuses removed")
			public boolean removeNegativeStatus = true;
		}

		@Config.Name("destruction_catalyst")
		@Config.Comment("Destruction Catalyst settings")
		public final DestructionCatalystSettings DestructionCatalyst = new DestructionCatalystSettings();

		public class DestructionCatalystSettings {
			@Config.Name("mundane_blocks")
			@Config.Comment("List of mundane blocks the catalyst will break")
			public String[] mundaneBlocks = new String[] {"minecraft:dirt", "minecraft:grass", "minecraft:gravel", "minecraft:cobblestone", "minecraft:stone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow", "minecraft:soul_sand", "minecraft:netherrack", "minecraft:end_stone"};
			@Config.Name("gunpowder_cost")
			@Config.Comment("Number of gunpowder it costs per catalyst use")
			@Config.RangeInt(min = 0, max = 10)
			public int gunpowderCost = 3;
			@Config.Name("gunpowder_worth")
			@Config.Comment("Number of gunpowder that gets added to catalyst per one that's consumed from players inventory")
			@Config.RangeInt(min = 0, max = 3)
			public int gunpowderWorth = 1;
			@Config.Name("gunpowder_limit")
			@Config.Comment("Number of gunpowder that can be stored in destruction catalyst")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int gunpowderLimit = 250;
			@Config.Name("explosion_radius")
			@Config.Comment("Radius of the explosion")
			@Config.RangeInt(min = 1, max = 5)
			public int explosionRadius = 1;
			@Config.Name("centered_explosion")
			@Config.Comment("Whether the explosion is centered on the block that gets clicked")
			public boolean centeredExplosion = false;
			@Config.Name("perfect_cube")
			@Config.Comment("Whether the explosion makes a perfect cube hole")
			public boolean perfectCube = true;
		}

		@Config.Name("emperor_chalice")
		@Config.Comment("Emperor Chalice settings")
		public final EmperorChaliceSettings EmperorChalice = new EmperorChaliceSettings();

		public class EmperorChaliceSettings {
			@Config.Name("hunger_satiation_multiplier")
			@Config.Comment("How much saturation is added in addition to filling the hunger")
			@Config.RangeInt(min = 0, max = 10)
			public int hungerSatiationMultiplier = 4;
		}

		@Config.Name("ender_staff")
		@Config.Comment("Ender Staff settings")
		public final EnderStaffSettings EnderStaff = new EnderStaffSettings();

		public class EnderStaffSettings {
			@Config.Name("ender_pearl_cast_cost")
			@Config.Comment("Number of ender pearls per use")
			@Config.RangeInt(min = 0, max = 3)
			public int enderPearlCastCost = 1;
			@Config.Name("ender_pearl_node_warp_cost")
			@Config.Comment("Number of ender pearls per teleportation to the wraith node")
			@Config.RangeInt(min = 0, max = 3)
			public int enderPearlNodeWarpCost = 1;
			@Config.Name("ender_pearl_worth")
			@Config.Comment("Number of ender pearls that get added to the staff per one that's consumed from players inventory")
			@Config.RangeInt(min = 0, max = 10)
			public int enderPearlWorth = 1;
			@Config.Name("ender_pearl_limit")
			@Config.Comment("Number of ender pearls that the ender staff can store")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int enderPearlLimit = 250;
			@Config.Name("node_warp_cast_time")
			@Config.Comment("Time it takes to teleport to the wraith node")
			@Config.RangeInt(min = 10, max = 120)
			public int nodeWarpCastTime = 60;
		}

		@Config.Name("fortune_coin")
		@Config.Comment("Fortune Coin settings")
		public final FortuneCoinSettings FortuneCoin = new FortuneCoinSettings();

		public class FortuneCoinSettings {
			@Config.Name("disable_audio")
			@Config.Comment("Disables the sound of fortune coin teleporting stuff")
			public boolean disableAudio = false;
			@Config.Name("standard_pull_distance")
			@Config.Comment("The distance that it pulls from when activated")
			@Config.RangeInt(min = 3, max = 10)
			public int standardPullDistance = 5;
			@Config.Name("long_range_pull_distance")
			@Config.Comment("The distance that it pulls from when right click is held")
			@Config.RangeInt(min = 9, max = 30)
			public int longRangePullDistance = 15;
		}

		@Config.Name("glacial_staff")
		@Config.Comment("Glacial Staff settings")
		public final GlacialStaffSettings GlacialStaff = new GlacialStaffSettings();

		public class GlacialStaffSettings {
			@Config.Name("snowball_limit")
			@Config.Comment("Number of snowballs the staff can hold")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int snowballLimit = 250;
			@Config.Name("snowball_cost")
			@Config.Comment("Number of snowballs it costs when the staff is used")
			@Config.RangeInt(min = 0, max = 3)
			public int snowballCost = 1;
			@Config.Name("snowball_worth")
			@Config.Comment("Number of snowballs that get added to the staff per one that's consumed from player's inventory")
			@Config.RangeInt(min = 0, max = 3)
			public int snowballWorth = 1;
			@Config.Name("snowball_damage")
			@Config.Comment("The damage that snowballs cause")
			@Config.RangeInt(min = 0, max = 6)
			public int snowballDamage = 3;
			@Config.Name("snowball_damage_bonus_fire_immune")
			@Config.Comment("The damage bonus against entities that are immune to fire")
			@Config.RangeInt(min = 0, max = 6)
			public int snowballDamageBonusFireImmune = 3;
			@Config.Name("snowball_damage_bonus_blaze")
			@Config.Comment("The damage bonus against blaze")
			@Config.RangeInt(min = 0, max = 12)
			public int snowballDamageBonusBlaze = 6;
		}

		@Config.Name("harvest_rod")
		@Config.Comment("Harvest Rod settings")
		public final HarvestRodSettings HarvestRod = new HarvestRodSettings();

		public class HarvestRodSettings {
			@Config.Name("bonemeal_limit")
			@Config.Comment("Number of bonemeal the rod can hold")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int boneMealLimit = 250;
			@Config.Name("bonemeal_cost")
			@Config.Comment("Number of bonemeal consumed per use")
			@Config.RangeInt(min = 0, max = 3)
			public int boneMealCost = 1;
			@Config.Name("bonemeal_worth")
			@Config.Comment("Number of bonemeal that gets added to the rod per one that's consumed from player's inventory")
			@Config.RangeInt(min = 0, max = 3)
			public int boneMealWorth = 1;
			@Config.Name("bonemeal_luck_percent_chance")
			@Config.Comment("Percent chance that a bonemeal will get applied during a luck roll")
			@Config.RangeInt(min = 1, max = 100)
			public int boneMealLuckPercentChance = 33;
			@Config.Name("bonemeal_luck_rolls")
			@Config.Comment("Number of times that a rod may apply additional luck based bonemeal")
			@Config.RangeInt(min = 0, max = 7)
			public int boneMealLuckRolls = 2;
			@Config.Name("aoe_radius")
			@Config.Comment("Radius in which harvest rod breaks crops, bonemeals/plants/hoes blocks")
			@Config.RangeInt(min = 0, max = 5)
			public int AOERadius = 2;
			@Config.Name("aoe_cooldown")
			@Config.Comment("Ticks in between bonemealing/planting/hoeing blocks when player is using one of these AOE actions")
			@Config.RangeInt(min = 1, max = 20)
			public int AOECooldown = 3;
			@Config.Name("max_capacity_per_plantable")
			@Config.Comment("Maximum number of units harvest rod can hold per plantable item")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int maxCapacityPerPlantable = 250;
			@Config.Name("pedestal_range")
			@Config.Comment("Range at which harvest rod will automatically hoe/plant/bonemeal/break crops around pedestal")
			@Config.RangeInt(min = 1, max = 20)
			public int pedestalRange = 4;
			@Config.Name("pedestal_cooldown")
			@Config.Comment("Ticks in between harvest rod actions when in pedestal")
			@Config.RangeInt(min = 1, max = 20)
			public byte pedestalCooldown = 5;
		}

		@Config.Name("hero_medallion")
		@Config.Comment("Hero Medallion settings")
		public final HeroMedallionSettings HeroMedallion = new HeroMedallionSettings();

		public class HeroMedallionSettings {
			@Config.Name("experience_level_maximum")
			@Config.Comment("A player's experience level at which pulling from the medallion to player will stop")
			@Config.RangeInt(min = 0, max = 1000)
			public int experienceLevelMaximum = 200;
			@Config.Name("experience_level_minimum")
			@Config.Comment("A player's experience level at which the medallion will stop pulling from the player")
			@Config.RangeInt(min = 0, max = 30)
			public int experienceLevelMinimum = 0;
			@Config.Name("experience_limit")
			@Config.Comment("Experience level that the medallion can hold")
			@Config.RangeInt(min = 0, max = CLEAN_INT_MAX)
			public int experienceLimit = CLEAN_INT_MAX;
			@Config.Name("experience_drop")
			@Config.Comment("How much experience gets dropped on ground when hero's medallion is right clicked on it (9 is the first level of player xp)")
			@Config.RangeInt(min = 1, max = 100)
			public int experienceDrop = 9;
			@Config.Name("pedestal_cooldown")
			@Config.Comment("Cooldown between hero medallion tries to fix mending items in nearby pedestals")
			@Config.RangeInt(min = 1, max = 100)
			public int pedestalCoolDown = 20;
			@Config.Name("pedestal_range")
			@Config.Comment("Range in which pedestals are checked for items with mending enchant that need fixing")
			@Config.RangeInt(min = 1, max = 20)
			public int pedestalRange = 5;
			@Config.Name("pedestal_repair_step_xp")
			@Config.Comment("Maximum amount of xp that is used each time medallion repairs items")
			@Config.RangeInt(min = 1, max = 20)
			public int pedestalRepairStepXP = 5;
		}

		@Config.Name("ice_magus_rod")
		@Config.Comment("Ice Magus Rod settings")
		public final IceMagusRodSettings IceMagusRod = new IceMagusRodSettings();

		public class IceMagusRodSettings {
			@Config.Name("snowball_limit")
			@Config.Comment("Number of snowballs the rod can hold")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int snowballLimit = 250;
			@Config.Name("snowball_cost")
			@Config.Comment("Number of snowballs it costs when the rod is used")
			@Config.RangeInt(min = 0, max = 3)
			public int snowballCost = 1;
			@Config.Name("snowball_worth")
			@Config.Comment("Number of snowballs that get added to the rod per one that's consumed from player's inventory")
			@Config.RangeInt(min = 0, max = 3)
			public int snowballWorth = 1;
			@Config.Name("snowball_damage")
			@Config.Comment("The damage that snowballs cause")
			@Config.RangeInt(min = 0, max = 4)
			public int snowballDamage = 2;
			@Config.Name("snowball_damage_bonus_fire_immune")
			@Config.Comment("Damage bonus against fire immune mobs")
			@Config.RangeInt(min = 0, max = 4)
			public int snowballDamageBonusFireImmune = 2;
			@Config.Name("snowball_damage_bonus_blaze")
			@Config.Comment("Damage bonus against blaze")
			@Config.RangeInt(min = 0, max = 8)
			public int snowballDamageBonusBlaze = 4;
		}

		@Config.Name("infernal_chalice")
		@Config.Comment("Infernal Chalice settings")
		public final InfernalChaliceSettings InfernalChalice = new InfernalChaliceSettings();

		public class InfernalChaliceSettings {
			@Config.Name("hunger_cost_percent")
			@Config.Comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
			@Config.RangeInt(min = 0, max = 10)
			public int hungerCostPercent = 1;
			@Config.Name("fluid_limit")
			@Config.Comment("Millibuckets of lava that the chalice can hold")
			@Config.RangeInt(min = 0, max = CLEAN_INT_MAX)
			public int fluidLimit = 500000;
		}

		@Config.Name("infernal_claws")
		@Config.Comment("Infernal Chalice settings")
		public final InfernalClawsSettings InfernalClaws = new InfernalClawsSettings();

		public class InfernalClawsSettings {
			@Config.Name("hunger_cost_percent")
			@Config.Comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
			@Config.RangeInt(min = 0, max = 30)
			public int hungerCostPercent = 5;
		}

		@Config.Name("infernal_tear")
		@Config.Comment("Infernal Tear settings")
		public final InfernalTearSettings InfernalTear = new InfernalTearSettings();

		public class InfernalTearSettings {
			@Config.Name("absorb_when_created")
			@Config.Comment("Whether the infernal tear starts absorbing immediately after it is set to item type")
			public boolean absorbWhenCreated = false;
		}

		@Config.Name("kraken_shell")
		@Config.Comment("Kraken Shell settings")
		public final KrakenShellSettings KrakenShell = new KrakenShellSettings();

		public class KrakenShellSettings {
			@Config.Name("hunger_cost_percent")
			@Config.Comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
			@Config.RangeInt(min = 0, max = 50)
			public int hungerCostPercent = 25;
		}

		@Config.Name("lantern_of_paranoia")
		@Config.Comment("Lantern of Paranoia settings")
		public final LanternOfParanoiaSettings LanternOfParanoia = new LanternOfParanoiaSettings();

		public class LanternOfParanoiaSettings {
			@Config.Name("min_light_level")
			@Config.Comment("Minimum light level below which the lantern will place torches")
			@Config.RangeInt(min = 0, max = 15)
			public int minLightLevel = 8;
			@Config.Name("placement_scan_radius")
			@Config.Comment("Radius in which the lantern checks for light levels and places torches")
			@Config.RangeInt(min = 1, max = 15)
			public int placementScanRadius = 6;
		}

		@Config.Name("midas_touchstone")
		@Config.Comment("Midas Touchstone settings")
		public final MidasTouchstoneSettings MidasTouchstone = new MidasTouchstoneSettings();

		public class MidasTouchstoneSettings {
			@Config.Name("gold_items")
			@Config.Comment("Gold items that can be repaired by the touchstone")
			public String[] goldItems = new String[0];
			@Config.Name("glowstone_cost")
			@Config.Comment("Number of glowstone that the repair costs")
			@Config.RangeInt(min = 0, max = 3)
			public int glowstoneCost = 1;
			@Config.Name("glowstone_worth")
			@Config.Comment("Number of glowstone that gets added to the touchstone per one in player's inventory")
			@Config.RangeInt(min = 0, max = 12)
			public int glowstoneWorth = 4;
			@Config.Name("glowstone_limit")
			@Config.Comment("Number of glowstone the touchstone can hold")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int glowstoneLimit = 250;
		}

		@Config.Name("mob_charm")
		@Config.Comment("Mob Charm settings")
		public final MobCharmSettings MobCharm = new MobCharmSettings();

		public class MobCharmSettings {
			@Config.Name("durability")
			@Config.Comment("Total durability of Mob Charm")
			@Config.RangeInt(min = 20, max = 1000)
			public int durability = 80;
			@Config.Name("damage_per_kill")
			@Config.Comment("Damage that Mob Charm takes when player kills mob it protects them from")
			@Config.RangeInt(min = 0, max = 40)
			public int damagePerKill = 1;
			@Config.Name("drop_durability_repair")
			@Config.Comment("Sets how much durability of Mob Charm gets repaired per special drop")
			@Config.RangeInt(min = 1, max = 200)
			public int dropDurabilityRepair = 20;
			@Config.Name("max_charms_to_display")
			@Config.Comment("Maximum charms that will get displayed in HUD")
			@Config.RangeInt(min = 1, max = 20)
			public int maxCharmsToDisplay = 6;
			@Config.Name("pedestal_range")
			@Config.Comment("Range in which mob charm or belt in pedestal will keep monsters from attacking players")
			@Config.RangeInt(min = 10, max = 100)
			public int pedestalRange = 21;
			@Config.Name("keep_almost_destroyed_displayed")
			@Config.Comment("Determines if almost destroyed charms stay displayed in the hud")
			public boolean keepAlmostDestroyedDisplayed = true;
		}

		@Config.Name("phoenix_down")
		@Config.Comment("Phoenix Down settings")
		public final PhoenixDownSettings PhoenixDown = new PhoenixDownSettings();

		public class PhoenixDownSettings {
			@Config.Name("hunger_cost_percent")
			@Config.Comment("Percent hunger used to heal player per 1 damage that would be taken otherwise")
			@Config.RangeInt(min = 0, max = 50)
			public int hungerCostPercent = 25;
			@Config.Name("leaping_potency")
			@Config.Comment("Potency of the leaping effect")
			@Config.RangeInt(min = 0, max = 5)
			public int leapingPotency = 1;
			@Config.Name("heal_percentage_of_max_life")
			@Config.Comment("Percent of life that gets healed when the player would die")
			@Config.RangeInt(min = 0, max = 100)
			public int healPercentageOfMaxLife = 100;
			@Config.Name("remove_negative_status")
			@Config.Comment("Whether the player gets negative statuses removed when they were saved by Phoenix Down")
			public boolean removeNegativeStatus = true;
			@Config.Name("give_temporary_damage_resistance")
			@Config.Comment("Whether to give temporary damage resistance when the player would die")
			public boolean giveTemporaryDamageResistance = true;
			@Config.Name("give_temporary_regeneration")
			@Config.Comment("Whether to give temporary regeneration when the player would die")
			public boolean giveTemporaryRegeneration = true;
			@Config.Name("give_temporary_fire_resistance_if_fire_damage_killed_you")
			@Config.Comment("Whether to give temporary fire resistance when the player would die. Applies only when the player is being hurt by fire damage.")
			public boolean giveTemporaryFireResistanceIfFireDamageKilledYou = true;
			@Config.Name("give_temporary_water_breathing_if_drowning_killed_you")
			@Config.Comment("Whether to give temporary damage resistance when the player would die. Applies only when the player is drowning.")
			public boolean giveTemporaryWaterBreathingIfDrowningKilledYou = true;
		}

		@Config.Name("pyromancer_staff")
		@Config.Comment("Pyromancer Staff settings")
		public final PyromancerStaffSettings PyromancerStaff = new PyromancerStaffSettings();

		public class PyromancerStaffSettings {
			@Config.Name("fire_charge_limit")
			@Config.Comment("Number of fire charges the staff can hold")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int fireChargeLimit = 250;
			@Config.Name("fire_charge_cost")
			@Config.Comment("Number of fire charges used when the staff is fired")
			@Config.RangeInt(min = 0, max = 3)
			public int fireChargeCost = 1;
			@Config.Name("fire_charge_worth")
			@Config.Comment("Number of fire charges that get added to the staff per one that's consumed from player's inventory")
			@Config.RangeInt(min = 0, max = 3)
			public int fireChargeWorth = 1;
			@Config.Name("ghast_absorb_worth")
			@Config.Comment("Number of fire charges added to the staff per one that was shot by ghast and gets absorbed by the staff")
			@Config.RangeInt(min = 0, max = 3)
			public int ghastAbsorbWorth = 1;
			@Config.Name("blaze_powder_limit")
			@Config.Comment("Number of blaze powder the staff can hold")
			@Config.RangeInt(min = 0, max = ITEM_CAP)
			public int blazePowderLimit = 250;
			@Config.Name("blaze_powder_cost")
			@Config.Comment("Number of blaze powder used when staff is fired")
			@Config.RangeInt(min = 0, max = 3)
			public int blazePowderCost = 1;
			@Config.Name("blaze_powder_worth")
			@Config.Comment("Number of blaze powder that gets added to the staff per one that's consumed from player's inventory")
			@Config.RangeInt(min = 0, max = 3)
			public int blazePowderWorth = 1;
			@Config.Name("blaze_absorb_worth")
			@Config.Comment("Number of blaze powder added to the staff per one fireball that was shot by blaze and gets absorbed by the staff")
			@Config.RangeInt(min = 0, max = 3)
			public int blazeAbsorbWorth = 1;
		}

		@Config.Name("rending_gale")
		@Config.Comment("Rending Gale settings")
		public final RendingGaleSettings RendingGale = new RendingGaleSettings();

		public class RendingGaleSettings {
			@Config.Name("charge_limit")
			@Config.Comment("Number of feathers the rending gale can hold")
			@Config.RangeInt(min = 0, max = CLEAN_INT_MAX)
			public int chargeLimit = CLEAN_SHORT_MAX;
			@Config.Name("cast_charge_cost")
			@Config.Comment("Number of feathers used when the rending gale is cast in flight mode")
			@Config.RangeInt(min = 0, max = 3)
			public int castChargeCost = 1;
			@Config.Name("bolt_charge_cost")
			@Config.Comment("Number of feathers used to cast a lightning bolt")
			@Config.RangeInt(min = 0, max = 250)
			public int boltChargeCost = 100;
			@Config.Name("charge_feather_worth")
			@Config.Comment("Number of feathers that get added to the rending gale per one that's consumed from player's inventory")
			@Config.RangeInt(min = 1, max = 250)
			public int chargeFeatherWorth = 100;
			@Config.Name("block_target_range")
			@Config.Comment("How far a lightning block can be cast")
			@Config.RangeInt(min = 5, max = 15)
			public int blockTargetRange = 12;
			@Config.Name("push_pull_radius")
			@Config.Comment("Radius in which entities can be pushed/pulled")
			@Config.RangeInt(min = 1, max = 20)
			public int pushPullRadius = 10;
			@Config.Name("can_push_projectiles")
			@Config.Comment("Whether the rending gale can push projectiles")
			public boolean canPushProjectiles = false;
			@Config.Name("pedestal_flight_range")
			@Config.Comment("Range from pedestal at which players will get buffed with flight")
			@Config.RangeInt(min = 10, max = 100)
			public int pedestalFlightRange = 30;
			@Config.Name("pedestal_cost_per_second")
			@Config.Comment("Cost per second of buffing players with flight")
			@Config.RangeInt(min = 1, max = 20)
			public int pedestalCostPerSecond = 5;
			@Config.Name("pushable_entities_blacklist")
			@Config.Comment("List of entities that are banned from being pushed by the Rending Gale")
			public String[] pushableEntitiesBlacklist = new String[0];
			@Config.Name("pushable_projectiles_blacklist")
			@Config.Comment("List of projectiles that are banned from being pushed by the Rending Gale")
			public String[] pushableProjectilesBlacklist = new String[0];
		}

		@Config.Name("rod_of_lyssa")
		@Config.Comment("Rod of Lyssa settings")
		public final RodOfLyssaSettings RodOfLyssa = new RodOfLyssaSettings();

		public class RodOfLyssaSettings {
			@Config.Name("use_leveled_failure_rate")
			@Config.Comment("Whether level influences stealing failure rate of the rod")
			public boolean useLeveledFailureRate = true;
			@Config.Name("level_cap_for_leveled_formula")
			@Config.Comment("The experience level cap after which the failure rate is at a minimum and doesn't get better")
			@Config.RangeInt(min = 1, max = 900)
			public int levelCapForLeveledFormula = 100;
			@Config.Name("flat_steal_failure_percent_rate")
			@Config.Comment("The flat failure rate in case failure rate isn't influenced by player's level")
			@Config.RangeInt(min = 0, max = 100)
			public int flatStealFailurePercentRate = 10;
			@Config.Name("steal_from_vacant_slots")
			@Config.Comment("If set to false it goes through additional 4 accessible slots and looks for items in case the one selected randomly was empty")
			public boolean stealFromVacantSlots = true;
			@Config.Name("fail_steal_from_vacant_slots")
			@Config.Comment("Whether stealing from an empty slot triggers failure even if otherwise it would be successful")
			public boolean failStealFromVacantSlots = false;
			@Config.Name("anger_on_steal_failure")
			@Config.Comment("Whether entities get angry at player if stealing fails")
			public boolean angerOnStealFailure = true;
		}

		@Config.Name("seeker_shot")
		@Config.Comment("Seeker Shot settings")
		public final SeekerShotSettings SeekerShot = new SeekerShotSettings();

		public class SeekerShotSettings {
			@Config.Name("huntable_entities_blacklist")
			@Config.Comment("Entities that are banned from being tracked by seeker shot")
			public String[] huntableEntitiesBlacklist = new String[0];
		}

		@Config.Name("sojourner_staff")
		@Config.Comment("Sojourner Staff settings")
		public final SojournerStaffSettings SojournerStaff = new SojournerStaffSettings();

		public class SojournerStaffSettings {
			@Config.Name("torches")
			@Config.Comment("List of torches that are supported by the staff in addition to the default minecraft torch")
			public String[] torches = new String[]{"minecraft:torch"};
			@Config.Name("max_capacity_per_item_type")
			@Config.Comment("Number of items the staff can store per item type")
			@Config.RangeInt(min = 1, max = ITEM_CAP)
			public int maxCapacityPerItemType = 1500;
			@Config.Name("max_range")
			@Config.Comment("Maximum range at which torches can be placed")
			@Config.RangeInt(min = 1, max = 30)
			public int maxRange = 30;
			@Config.Name("tile_per_cost_multiplier")
			@Config.Comment("Distance after which there is an additional cost for torch placement. The additional cost is the number of times this distance fits in the distance at which the torch is being placed.")
			@Config.RangeInt(min = 6, max = 30)
			public int tilePerCostMultiplier = 6;
		}

		@Config.Name("twilight_cloak")
		@Config.Comment("Twilight Cloak settings")
		public final TwilightCloakSettings TwilightCloak = new TwilightCloakSettings();

		public class TwilightCloakSettings {
			@Config.Name("max_light_level")
			@Config.Comment("Maximum light level at which the player is still invisible to the mobs")
			@Config.RangeInt(min = 0, max = 15)
			public int maxLightLevel = 4;
		}

		@Config.Name("void_tear")
		@Config.Comment("Void Tear settings")
		public final VoidTearSettings VoidTear = new VoidTearSettings();

		public class VoidTearSettings {
			@Config.Name("item_limit")
			@Config.Comment("Number of items the tear can hold of the item type it is set to")
			@Config.RangeInt(min = 0, max = CLEAN_INT_MAX)
			public int itemLimit = 2000000000;
			@Config.Name("absorb_when_created")
			@Config.Comment("Whether the void tear starts absorbing immediately after it is set to item type")
			public boolean absorbWhenCreated = true;
		}
	}

	@Config.Name("block_settings")
	public static final BlockSettings Blocks = new BlockSettings();

	public static class BlockSettings {

		@Config.Name("altar")
		@Config.Comment("Altar of Light settings")
		public final AltarSettings Altar = new AltarSettings();

		public class AltarSettings {
			@Config.Name("redstone_cost")
			@Config.Comment("Number of redstone it costs to activate altar")
			@Config.RangeInt(min = 0, max = 10)
			public int redstoneCost = 3;
			@Config.Name("time_in_minutes")
			@Config.Comment("Time in minutes it takes for the altar to create glowstone block")
			@Config.RangeInt(min = 0, max = 60)
			public int timeInMinutes = 20;
			@Config.Name("maximum_time_variance_in_minutes")
			@Config.Comment("Maximum time variance in minutes. A random part of it gets added to the Time in minutes.")
			@Config.RangeInt(min = 0, max = 15)
			public int maximumTimeVarianceInMinutes = 5;
			@Config.Name("output_light_level_while_active")
			@Config.Comment("Light level that the altar outputs while active")
			@Config.RangeInt(min = 16, max = 0)
			public int outputLightLevelWhileActive = 16;
		}

		@Config.Name("apothecary_cauldron")
		@Config.Comment("Apothecary Cauldron settings")
		public final ApothecaryCauldronSettings ApothecaryCauldron = new ApothecaryCauldronSettings();

		public class ApothecaryCauldronSettings {
			@Config.Name("redstone_limit")
			@Config.Comment("Limit of redstone that can be used in cauldron to make potion last longer")
			@Config.RangeInt(min = 0, max = 5)
			public int redstoneLimit = 3;

			@Config.Name("cook_time")
			@Config.Comment("Time it takes to cook potion")
			@Config.RangeInt(min = 20, max = 32000)
			public int cookTime = 160;
			@Config.Name("heat_sources")
			@Config.Comment("List of acceptable heat sources")
			public String[] heatSources = new String[] {};
			@Config.Name("glowstone_limit")
			@Config.Comment("Limit of glowstone that can be used in cauldron to make potion more potent")
			@Config.RangeInt(min = 0, max = 4)
			public int glowstoneLimit = 2;
		}

		@Config.Name("fertile_lilypad")
		@Config.Comment("Lilypad of Fertility settings")
		public final FertileLilypadSettings FertileLilypad = new FertileLilypadSettings();

		public class FertileLilypadSettings {
			@Config.Name("seconds_between_growth_ticks")
			@Config.Comment("Interval in seconds at which the lilypad causes growth tick updates")
			@Config.RangeInt(min = 1, max = 150)
			public int secondsBetweenGrowthTicks = 10;
			@Config.Name("tile_range")
			@Config.Comment("Radius in which lilypad causes growh ticks")
			@Config.RangeInt(min = 1, max = 15)
			public int tileRange = 4;
			@Config.Name("full_potency_range")
			@Config.Comment("Radius around lilypad where the growth ticks occur the most often")
			@Config.RangeInt(min = 1, max = 15)
			public int fullPotencyRange = 1;
		}

		@Config.Name("interdiction_torch")
		@Config.Comment("Interdiction Torch settings")
		public final InterdictionTorchSettings InterdictionTorch = new InterdictionTorchSettings();

		public class InterdictionTorchSettings {
			@Config.Name("push_radius")
			@Config.Comment("Radius in which the torch can push out mobs")
			@Config.RangeInt(min = 1, max = 15)
			public int pushRadius = 5;
			@Config.Name("can_push_projectiles")
			@Config.Comment("Whether the torch can push projectiles")
			public boolean canPushProjectiles = false;
			@Config.Name("pushable_entities_blacklist")
			@Config.Comment("List of entities that are banned from being pushed by the torch")
			public String[] pushableEntitiesBlacklist = new String[0];
			@Config.Name("pushable_projectiles_blacklist")
			@Config.Comment("List of projectiles that are banned from being pushed by the torch")
			public String[] pushableProjectilesBlacklist = new String[0];
		}

		@Config.Name("pedestal")
		@Config.Comment("Pedestal related settings")
		public final PedestalSettings Pedestal = new PedestalSettings();

		public class PedestalSettings {
			@Config.Name("melee_wrapper_range")
			@Config.Comment("Range of the melee weapons in which these will attack when in pedestal")
			@Config.RangeInt(min = 1, max = 10)
			public int meleeWrapperRange = 5;
			@Config.Name("melee_wrapper_cooldown")
			@Config.Comment("How long it takes after a melee weapon swing before it can swing again (in ticks)")
			@Config.RangeInt(min = 1, max = 200)
			public byte meleeWrapperCooldown = 5;
			@Config.Name("bucket_wrapper_range")
			@Config.Comment("Range at which bucket will pickup liquid blocks or milk cows")
			@Config.RangeInt(min = 1, max = 10)
			public int bucketWrapperRange = 4;
			@Config.Name("bucket_wrapper_cooldown")
			@Config.Comment("How long it takes in between bucket actions (in ticks)")
			@Config.RangeInt(min = 1, max = 200)
			public byte bucketWrapperCooldown = 40;
			@Config.Name("shears_wrapper_range")
			@Config.Comment("How long it takes between shearing actions (in ticks)")
			@Config.RangeInt(min = 1, max = 10)
			public int shearsWrapperRange = 4;
			@Config.Name("shears_wrapper_cooldown")
			@Config.Comment("Range at which shears will shear sheep or shearable blocks")
			@Config.RangeInt(min = 1, max = 200)
			public byte shearsWrapperCooldown = 10;
			@Config.Name("redstone_wrapper_range")
			@Config.Comment("Range at which pedestal will get turned on if either redstone block gets put in or redstone dust and transmitting pedestal is powered")
			@Config.RangeInt(min = 1, max = 200)
			public int redstoneWrapperRange = 10;
			@Config.Name("fishing_wrapper_success_rate")
			@Config.Comment("Success rate of fishing in percent. When unsuccessful it will pull the hook too late to catch a fish.")
			@Config.RangeInt(min = 0, max = 100)
			public int fishingWrapperSuccessRate = 80;
			@Config.Name("fishing_wrapper_retract_delay")
			@Config.Comment("Delay in seconds before it would start fishing again after retracting the hook.")
			@Config.RangeInt(min = 1, max = 20)
			public int fishingWrapperRetractDelay = 2;
		}
	}
	
}
