package xreliquary.reference;

public class Reference {
	//TODO remove unused stuff

	// class for all the mod related constants
	public static final String VERSION = "@MOD_VERSION@";

	public static final String MOD_ID = "xreliquary";
	public static final String DOMAIN = MOD_ID.toLowerCase() + ":";
	public static final String MOD_NAME = "Reliquary";
	public static final String GUI_FACTORY_CLASS = "xreliquary.client.gui.GuiFactory";
	public static final String DEPENDENCIES = "after:" + Compatibility.MOD_ID.JER + "@[0.3.4,)";

	public static final String CLIENT_PROXY = "xreliquary.client.ClientProxy";
	public static final String COMMON_PROXY = "xreliquary.common.CommonProxy";

	public static final int WATER_SPRITE = 0;
	public static final int SPLASH_POTION_SPRITE = 1;
	public static final int GRENADE_SPRITE = 12;

	// miscellaneous configurable things
	//public static final int CAPACITY_UPGRADE_INCREMENT = 64;
	public static final int PESTLE_USAGE_MAX = 5; // the number of times you
	// have to use the pestle.

	public static final String ART_PATH_ENTITIES = "textures/entities/";
	public static final String THROWN_ITEM_SPRITES = "thrownItemsSheet.png";

	public static final String MODEL_TEXTURE_PATH = "textures/models/";

	public static class MOB_CHARM {
		public static final byte ZOMBIE_META = 0;
		public static final byte SKELETON_META = 1;
		public static final byte WITHER_SKELETON_META = 2;
		public static final byte CREEPER_META = 3;
		public static final byte WITCH_META = 4;
		public static final byte ZOMBIE_PIGMAN_META = 5;
		public static final byte CAVE_SPIDER_META = 6;
		public static final byte SPIDER_META = 7;
		public static final byte ENDERMAN_META = 8;
		public static final byte GHAST_META = 9;
		public static final byte SLIME_META = 10;
		public static final byte MAGMA_CUBE_META = 11;
		public static final byte BLAZE_META = 12;
	}

	public static final int SKELETON_INGREDIENT_META = 0;
	public static final int WITHER_INGREDIENT_META = 1;
	public static final int SPIDER_INGREDIENT_META = 2;
	public static final int CREEPER_INGREDIENT_META = 3;
	public static final int SLIME_INGREDIENT_META = 4;
	public static final int BAT_INGREDIENT_META = 5;
	public static final int ZOMBIE_INGREDIENT_META = 6;
	public static final int MOLTEN_INGREDIENT_META = 7;
	public static final int STORM_INGREDIENT_META = 8;
	public static final int FERTILE_INGREDIENT_META = 9;
	public static final int FROZEN_INGREDIENT_META = 10;
	public static final int ENDER_INGREDIENT_META = 11;
	public static final int SQUID_INGREDIENT_META = 12;
	public static final int CLAW_INGREDIENT_META = 13;
	public static final int SHELL_INGREDIENT_META = 14;
	public static final int CLOTH_INGREDIENT_META = 15;

	public static final int APHRODITE_META = 0;
	public static final int FERTILIZER_META = 1;

	public static final String LOAD_SOUND = Reference.MOD_ID + ":xload";
	public static final String SHOT_SOUND = Reference.MOD_ID + ":xshot";
	public static final String BOOK_SOUND = Reference.MOD_ID + ":book";
	public static final String GUST_SOUND = Reference.MOD_ID + ":gust";

	//not all of these get used but they're good to keep around.
	public static final int WHITE_WOOL_META = 0;
	public static final int ORAGE_WOOL_META = 1;
	public static final int MAGENTA_WOOL_META = 2;
	public static final int LIGHT_BLUE_WOOL_META = 3;
	public static final int YELLOW_WOOL_META = 4;
	public static final int LIME_WOOL_META = 5;
	public static final int PINK_WOOL_META = 6;
	public static final int GRAY_WOOL_META = 7;
	public static final int LIGHT_GRAY_WOOL_META = 8;
	public static final int CYAN_WOOL_META = 9;
	public static final int PURPLE_WOOL_META = 10;
	public static final int BLUE_WOOL_META = 11;
	public static final int BROWN_WOOL_META = 12;
	public static final int GREEN_WOOL_META = 13;
	public static final int RED_WOOL_META = 14;
	public static final int BLACK_WOOL_META = 15;

	public static final int WHITE_DYE_META = 15;
	public static final int ORAGE_DYE_META = 14;
	public static final int MAGENTA_DYE_META = 13;
	public static final int LIGHT_BLUE_DYE_META = 12;
	public static final int YELLOW_DYE_META = 11;
	public static final int LIME_DYE_META = 10;
	public static final int PINK_DYE_META = 9;
	public static final int GRAY_DYE_META = 8;
	public static final int LIGHT_GRAY_DYE_META = 7;
	public static final int CYAN_DYE_META = 6;
	public static final int PURPLE_DYE_META = 5;
	public static final int BLUE_DYE_META = 4;
	public static final int BROWN_DYE_META = 3;
	public static final int GREEN_DYE_META = 2;
	public static final int RED_DYE_META = 1;
	public static final int BLACK_DYE_META = 0;

	public static final int PLAYER_HANDGUN_SKILL_MAXIMUM = 20;

	public static final int HANDGUN_RELOAD_PITCH_OFFSET = 20;
	public static final int HANDGUN_RELOAD_SKILL_OFFSET = 10;
	// This should never exceed (HANDGUN_RELOAD_SKILL_OFFSET) / 2
	public static final int HANDGUN_RELOAD_ANIMATION_TICKS = 5;

	public static final int HANDGUN_RECOIL_SKILL_OFFSET = 5;
	public static final int HANDGUN_COOLDOWN_SKILL_OFFSET = 5;
	public static final int HANDGUN_KNOCKBACK_SKILL_OFFSET = 0;

	//Packet ID Section for identifying packet types, now deprecated.
	//    public static final int RECOIL_PACKET_ID = 0;
	//    public static final int RECOIL_COMPENSATION_PACKET_ID = 1;
	//    public static final int RELOAD_PACKET_ID = 0;
	//    public static final int RELOAD_COMPENSATION_PACKET_ID = 1;

	//Shot Type Indexes
	public static final int NEUTRAL_SHOT_INDEX = 1;
	public static final int EXORCISM_SHOT_INDEX = 2;
	public static final int BLAZE_SHOT_INDEX = 3;
	public static final int ENDER_SHOT_INDEX = 4;
	public static final int CONCUSSIVE_SHOT_INDEX = 5;
	public static final int BUSTER_SHOT_INDEX = 6;
	public static final int SEEKER_SHOT_INDEX = 7;
	public static final int SAND_SHOT_INDEX = 8;
	public static final int STORM_SHOT_INDEX = 9;

	//minecraft under-the-hood potion names
	public static final String INVIS = "invisibility";
	public static final String ABSORB = "absorption";
	public static final String HBOOST = "health_boost";
	public static final String DBOOST = "strength";
	public static final String HARM = "instant_damage";
	public static final String HEAL = "instant_health";
	public static final String SPEED = "speed";
	public static final String HASTE = "haste";
	public static final String SLOW = "slowness";
	public static final String FATIGUE = "mining_fatigue";
	public static final String BREATH = "water_breathing";
	public static final String VISION = "night_vision";
	public static final String RESIST = "resistance";
	public static final String FRESIST = "fire_resistance";
	public static final String WEAK = "weakness";
	public static final String JUMP = "jump_boost";
	public static final String NAUSEA = "nausea";
	public static final String HUNGER = "hunger";
	public static final String SATURATION = "saturation";
	public static final String REGEN = "regeneration";
	public static final String POISON = "poison";
	public static final String WITHER = "wither";
	public static final String BLIND = "blindness";

	public static final String JEI_CATEGORY_ALKAHESTRY_CRAFTING = DOMAIN + "alkahestryCrafting";
	public static final String JEI_CATEGORY_ALKAHESTRY_CHARGING = DOMAIN + "alkahestryCharging";
	public static final String JEI_CATEGORY_MORTAR = "mortar";
	public static final String JEI_CATEGORY_CAULDRON = "cauldron";
	public static final String JEI_CATEGORY_CAULDRON_SPLASH = "cauldronSplash";
}
