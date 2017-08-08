package xreliquary.reference;

public class Reference {
	//TODO remove unused stuff

	// class for all the mod related constants
	public static final String VERSION = "@MOD_VERSION@";

	public static final String MOD_ID = "xreliquary";
	public static final String DOMAIN = MOD_ID.toLowerCase() + ":";
	public static final String MOD_NAME = "Reliquary";
	public static final String GUI_FACTORY_CLASS = "xreliquary.client.gui.GuiFactory";
	public static final String DEPENDENCIES = "";

	public static final String CLIENT_PROXY = "xreliquary.client.ClientProxy";
	public static final String COMMON_PROXY = "xreliquary.common.CommonProxy";

	public static final String ART_PATH_ENTITIES = "textures/entities/";
	public static final String THROWN_ITEM_SPRITES = "thrown_items_sheet.png";

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
		public static final byte GUARDIAN_META = 13;
		public static final byte COUNT_TYPES = 14;
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
	public static final int GUARDIAN_INGREDIENT_META = 16;

	public static final int RED_WOOL_META = 14;
	public static final int BLACK_WOOL_META = 15;

	public static final int WHITE_DYE_META = 15;
	public static final int YELLOW_DYE_META = 11;
	public static final int BLUE_DYE_META = 4;
	public static final int BROWN_DYE_META = 3;
	public static final int GREEN_DYE_META = 2;
	public static final int RED_DYE_META = 1;
	public static final int BLACK_DYE_META = 0;

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

	//reliquary potion names
	public static final String CURE = MOD_ID + ":cure_potion";
}
