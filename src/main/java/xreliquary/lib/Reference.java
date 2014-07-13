package xreliquary.lib;

public class Reference {
	// class for all the mod related constants
	public static final String VERSION = "1.2.DEV";

	public static final String MOD_ID = "xreliquary";
	public static final String MOD_NAME = "Reliquary";

	public static final String CLIENT_PROXY = "xreliquary.client.ClientProxy";
	public static final String COMMON_PROXY = "xreliquary.common.CommonProxy";

	public static final int WATER_SPRITE = 0;
	public static final int SPLASH_POTION_SPRITE = 1;
	public static final int GRENADE_SPRITE = 12;

	// miscellaneous configurable things
	public static final int DESTRUCTION_CATALYST_COST = 3; // gunpowder cost
	public static final int CAPACITY_UPGRADE_INCREMENT = 64;
	public static final int PESTLE_USAGE_MAX = 5; // the number of times you
													// have to use the pestle.

	public static final String ART_PATH_ENTITIES = "textures/entities/";
	public static final String THROWN_ITEM_SPRITES = "thrownItemsSheet.png";

	public static final String MODEL_TEXTURE_PATH = "textures/models/";

	public static final int SPLASH_META = 0;
	public static final int APHRODITE_META = 1;
	public static final int POISON_META = 2;
	public static final int ACID_META = 3;
	public static final int CONFUSION_META = 4;
	public static final int SLOWING_META = 5;
	public static final int WEAKNESS_META = 6;
	public static final int WITHER_META = 7;
	public static final int BLINDING_META = 8;
	public static final int RUINATION_META = 9;
	public static final int FERTILIZER_META = 10;

	public static final int EMPTY_VIAL_META = 11;
	public static final int POTION_META = 12;
	public static final int SPEED_META = 13;
	public static final int DIGGING_META = 14;
	public static final int STRENGTH_META = 15;
	public static final int HEALING_META = 16;
	public static final int BOUNDING_META = 17;
	public static final int REGENERATION_META = 18;
	public static final int RESISTANCE_META = 19;
	public static final int FIRE_WARDING_META = 20;
	public static final int BREATHING_META = 21;
	public static final int INVISIBILITY_META = 22;
	public static final int INFRAVISION_META = 23;
	public static final int PROTECTION_META = 24;
	public static final int POTENCE_META = 25;
	public static final int CELERITY_META = 26;
	public static final int PANACEA_META = 27;
	public static final int STALKER_META = 28;
	public static final int WATER_META = 29;

	// these are the names that point the Elsewhere Flask to the proper
	// localization names in the lang files.
	// they don't perfectly line up with what I've called them throughout the
	// mod (in code/in localizations)
	// I may line them all up later. Most of them are obvious.
	public static final String HASTE_POTION_SHORTHAND = "haste";
	public static final String JUMP_POTION_SHORTHAND = "leaping";
	public static final String STRENGTH_POTION_SHORTHAND = "strength";
	public static final String MOVESPEED_POTION_SHORTHAND = "movespeed";
	public static final String NIGHTVISION_POTION_SHORTHAND = "nightvision";
	public static final String INVISIBILITY_POTION_SHORTHAND = "invisibility";
	public static final String HEAL_POTION_SHORTHAND = "heal";
	public static final String REGENERATION_POTION_SHORTHAND = "regeneration";
	public static final String RESISTANCE_POTION_SHORTHAND = "resistance";
	public static final String FIRE_RESISTANCE_POTION_SHORTHAND = "fireresistance";
	public static final String MILK_POTION_SHORTHAND = "milk";

	public static final String LOAD_SOUND = Reference.MOD_ID + ":xload";
	public static final String SHOT_SOUND = Reference.MOD_ID + ":xshot";
	public static final String BOOK_SOUND = Reference.MOD_ID + ":book";

	// Misc options for configuration
	public static final boolean DISABLE_COIN_AUDIO_DEFAULT = false;

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
}
