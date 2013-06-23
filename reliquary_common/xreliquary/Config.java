package xreliquary;

import java.io.File;
import java.util.logging.Level;
import net.minecraftforge.common.Configuration;
import xreliquary.lib.Indexes;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.FMLLog;

public class Config {
	// items
	public static int handgunID;
	public static int shellID;
	public static int slugID;
	public static int chaliceID;
	public static int glowBreadID;
	public static int glowWaterID;
	public static int condensedPotionID;
	public static int enderStaffID;
	public static int gunPartID;
	public static int sojournerStaffID;
	public static int mercyCrossID;
	public static int fortuneCoinID;
	public static int midasTouchstoneID;
	public static int iceRodID;
	public static int magicbaneID;
	public static int witherlessRoseID;
	public static int holyHandGrenadeID;
	public static int letheTearID;
	public static int destructionCatalystID;
	public static int alkahestID;
	public static int alkahestryTomeID;
	public static int salamanderEyeID;
	public static int wraithEyeID;
	public static int satchelID;
	public static int emptyVoidTearID;
	public static int voidTearID;
	public static int altarActiveID;
	public static int altarIdleID;
	public static int wraithNodeID;
	public static int lilypadID;
	public static int alchemicalGunmetalID;
	public static int apothecaryMortarID;
	public static int gunsmithCrucibleID;
	public static int alembicID;
	public static int potionEssenceID;
	public static int stackedEssenceID;
	// options
	public static boolean disableCoinAudio;
	public static boolean disableGunItems;
	public static boolean disablePotionItems;

	public static void init(File configFile) {
		Configuration config = new Configuration(configFile);
		try {
			config.load();
			// block and item ID configurations.
			handgunID = config.getItem("Handgun", Indexes.HANDGUN_DEFAULT_ID).getInt(Indexes.HANDGUN_DEFAULT_ID);
			slugID = config.getItem("Slug", Indexes.SLUG_DEFAULT_ID).getInt(Indexes.SLUG_DEFAULT_ID);
			shellID = config.getItem("Shell", Indexes.SHELL_DEFAULT_ID).getInt(Indexes.SHELL_DEFAULT_ID);
			chaliceID = config.getItem("Chalice", Indexes.CHALICE_DEFAULT_ID).getInt(Indexes.CHALICE_DEFAULT_ID);
			glowBreadID = config.getItem("Bread", Indexes.BREAD_DEFAULT_ID).getInt(Indexes.BREAD_DEFAULT_ID);
			glowWaterID = config.getItem("Water", Indexes.WATER_DEFAULT_ID).getInt(Indexes.WATER_DEFAULT_ID);
			condensedPotionID = config.getItem("CondensedPotion", Indexes.CONDENSED_POTION_DEFAULT_ID).getInt(Indexes.CONDENSED_POTION_DEFAULT_ID);
			enderStaffID = config.getItem("EnderStaff", Indexes.ENDER_STAFF_DEFAULT_ID).getInt(Indexes.ENDER_STAFF_DEFAULT_ID);
			gunPartID = config.getItem("GunPart", Indexes.GUNPART_DEFAULT_ID).getInt(Indexes.GUNPART_DEFAULT_ID);
			sojournerStaffID = config.getItem("Torch", Indexes.TORCH_DEFAULT_ID).getInt(Indexes.TORCH_DEFAULT_ID);
			mercyCrossID = config.getItem("Cross", Indexes.CROSS_DEFAULT_ID).getInt(Indexes.CROSS_DEFAULT_ID);
			fortuneCoinID = config.getItem("Coin", Indexes.COIN_DEFAULT_ID).getInt(Indexes.COIN_DEFAULT_ID);
			midasTouchstoneID = config.getItem("Touchstone", Indexes.TOUCHSTONE_DEFAULT_ID).getInt(Indexes.TOUCHSTONE_DEFAULT_ID);
			iceRodID = config.getItem("IceRod", Indexes.ICE_ROD_DEFAULT_ID).getInt(Indexes.ICE_ROD_DEFAULT_ID);
			magicbaneID = config.getItem("Magicbane", Indexes.MAGICBANE_DEFAULT_ID).getInt(Indexes.MAGICBANE_DEFAULT_ID);
			witherlessRoseID = config.getItem("Rose", Indexes.WITHERLESS_ROSE_DEFAULT_ID).getInt(Indexes.WITHERLESS_ROSE_DEFAULT_ID);
			holyHandGrenadeID = config.getItem("Grenade", Indexes.GRENADE_DEFAULT_ID).getInt(Indexes.GRENADE_DEFAULT_ID);
			letheTearID = config.getItem("Tear", Indexes.EMPTY_VOID_TEAR_DEFAULT_ID).getInt(Indexes.EMPTY_VOID_TEAR_DEFAULT_ID);
			destructionCatalystID = config.getItem("Catalyst", Indexes.DESTRUCTION_CATALYST_DEFAULT_ID).getInt(Indexes.DESTRUCTION_CATALYST_DEFAULT_ID);
			alkahestID = config.getItem("Alkahest", Indexes.ALKAHEST_DEFAULT_ID).getInt(Indexes.ALKAHEST_DEFAULT_ID);
			alkahestryTomeID = config.getItem("Tome", Indexes.TOME_DEFAULT_ID).getInt(Indexes.TOME_DEFAULT_ID);
			salamanderEyeID = config.getItem("SalamanderEye", Indexes.SALAMANDER_EYE_DEFAULT_ID).getInt(Indexes.SALAMANDER_EYE_DEFAULT_ID);
			wraithEyeID = config.getItem("WraithEye", Indexes.WRAITH_EYE_DEFAULT_ID).getInt(Indexes.WRAITH_EYE_DEFAULT_ID);
			voidTearID = config.getItem("VoidTear", Indexes.VOID_TEAR_DEFAULT_ID).getInt(Indexes.VOID_TEAR_DEFAULT_ID);
			emptyVoidTearID = config.getItem("EmptyVoidTear", Indexes.EMPTY_VOID_TEAR_DEFAULT_ID).getInt(Indexes.EMPTY_VOID_TEAR_DEFAULT_ID);
			satchelID = config.getItem("Satchel", Indexes.SATCHEL_DEFAULT_ID).getInt(Indexes.SATCHEL_DEFAULT_ID);
			alembicID = config.getItem("Alembic", Indexes.ALEMBIC_DEFAULT_ID).getInt(Indexes.ALEMBIC_DEFAULT_ID);
			apothecaryMortarID = config.getItem("Mortar", Indexes.APOTHECARY_MORTAR_DEFAULT_ID).getInt(Indexes.APOTHECARY_MORTAR_DEFAULT_ID);
			gunsmithCrucibleID = config.getItem("Crucible", Indexes.GUNSMITH_CRUCIBLE_DEFAULT_ID).getInt(Indexes.GUNSMITH_CRUCIBLE_DEFAULT_ID);
			potionEssenceID = config.getItem("Essence", Indexes.POTION_ESSENCE_DEFAULT_ID).getInt(Indexes.POTION_ESSENCE_DEFAULT_ID);
			stackedEssenceID = config.getItem("StackedEssence", Indexes.STACKED_ESSENCE_DEFAULT_ID).getInt(Indexes.STACKED_ESSENCE_DEFAULT_ID);
			// blocks
			altarActiveID = config.getBlock("AltarActive", Indexes.ALTAR_ACTIVE_DEFAULT_ID).getInt(Indexes.ALTAR_ACTIVE_DEFAULT_ID);
			altarIdleID = config.getBlock("AltarIdle", Indexes.ALTAR_IDLE_DEFAULT_ID).getInt(Indexes.ALTAR_IDLE_DEFAULT_ID);
			wraithNodeID = config.getBlock("WraithNode", Indexes.WRAITH_NODE_DEFAULT_ID).getInt(Indexes.WRAITH_NODE_DEFAULT_ID);
			lilypadID = config.getBlock("Lilypad", Indexes.LILYPAD_DEFAULT_ID).getInt(Indexes.LILYPAD_DEFAULT_ID);
			// miscellaneous options
			disableCoinAudio = config.get("Misc_Options", "disableCoinAudio", false).getBoolean(Reference.DISABLE_COIN_AUDIO_DEFAULT);
			// item disabling features
			disableGunItems = config.get("Item_Disabling", "disableGunItems", false).getBoolean(Reference.DISABLE_GUN_ITEMS_DEFAULT);
			disablePotionItems = config.get("Item_Disabling", "disablePotionItems", false).getBoolean(Reference.DISABLE_POTION_ITEMS_DEFAULT);
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, Reference.MOD_NAME + " had a problem while loading its configuration.");
		} finally {
			config.save();
		}
	}
	// other configs (ID and Flags)
}
