package xreliquary;

import java.util.logging.Level;
import xreliquary.lib.Indexes;
import xreliquary.lib.Reference;
import xreliquary.util.LogHelper;

public class Config {
	
    // items
    public static int handgunID;
    public static int magazineID;
    public static int bulletID;
    public static int chaliceID;
    public static int glowBreadID;
    public static int glowWaterID;
    public static int condensedPotionID;
    public static int distortionCloakID;
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

    
    public static void init() {

        try {

            // block and item ID configurations.
            handgunID = Reliquary.CONFIG.getItem("Handgun", Indexes.HANDGUN_DEFAULT_ID).getInt(Indexes.HANDGUN_DEFAULT_ID);
            magazineID = Reliquary.CONFIG.getItem("Magazine", Indexes.MAGAZINE_DEFAULT_ID).getInt(Indexes.MAGAZINE_DEFAULT_ID);
            bulletID = Reliquary.CONFIG.getItem("Bullet", Indexes.BULLET_DEFAULT_ID).getInt(Indexes.BULLET_DEFAULT_ID);
            chaliceID = Reliquary.CONFIG.getItem("Chalice", Indexes.CHALICE_DEFAULT_ID).getInt(Indexes.CHALICE_DEFAULT_ID);
            glowBreadID = Reliquary.CONFIG.getItem("Bread", Indexes.BREAD_DEFAULT_ID).getInt(Indexes.BREAD_DEFAULT_ID);
            glowWaterID = Reliquary.CONFIG.getItem("Water", Indexes.WATER_DEFAULT_ID).getInt(Indexes.WATER_DEFAULT_ID);
            condensedPotionID = Reliquary.CONFIG.getItem("CondensedPotion", Indexes.CONDENSED_POTION_DEFAULT_ID).getInt(Indexes.CONDENSED_POTION_DEFAULT_ID);
            distortionCloakID = Reliquary.CONFIG.getItem("Cloak", Indexes.DISTORTION_CLOAK_DEFAULT_ID).getInt(Indexes.DISTORTION_CLOAK_DEFAULT_ID);
            gunPartID = Reliquary.CONFIG.getItem("GunPart", Indexes.GUNPART_DEFAULT_ID).getInt(Indexes.GUNPART_DEFAULT_ID);
            sojournerStaffID = Reliquary.CONFIG.getItem("Torch", Indexes.TORCH_DEFAULT_ID).getInt(Indexes.TORCH_DEFAULT_ID);
            mercyCrossID = Reliquary.CONFIG.getItem("Cross", Indexes.CROSS_DEFAULT_ID).getInt(Indexes.CROSS_DEFAULT_ID);
            fortuneCoinID = Reliquary.CONFIG.getItem("Coin", Indexes.COIN_DEFAULT_ID).getInt(Indexes.COIN_DEFAULT_ID);
            midasTouchstoneID = Reliquary.CONFIG.getItem("Touchstone", Indexes.TOUCHSTONE_DEFAULT_ID).getInt(Indexes.TOUCHSTONE_DEFAULT_ID);
            iceRodID = Reliquary.CONFIG.getItem("IceRod", Indexes.ICE_ROD_DEFAULT_ID).getInt(Indexes.ICE_ROD_DEFAULT_ID);
            magicbaneID = Reliquary.CONFIG.getItem("Magicbane", Indexes.MAGICBANE_DEFAULT_ID).getInt(Indexes.MAGICBANE_DEFAULT_ID);
            witherlessRoseID = Reliquary.CONFIG.getItem("Rose", Indexes.WITHERLESS_ROSE_DEFAULT_ID).getInt(Indexes.WITHERLESS_ROSE_DEFAULT_ID);
            holyHandGrenadeID = Reliquary.CONFIG.getItem("Grenade", Indexes.GRENADE_DEFAULT_ID).getInt(Indexes.GRENADE_DEFAULT_ID);
            letheTearID = Reliquary.CONFIG.getItem("Tear", Indexes.EMPTY_VOID_TEAR_DEFAULT_ID).getInt(Indexes.EMPTY_VOID_TEAR_DEFAULT_ID);
            destructionCatalystID = Reliquary.CONFIG.getItem("Catalyst", Indexes.DESTRUCTION_CATALYST_DEFAULT_ID).getInt(Indexes.DESTRUCTION_CATALYST_DEFAULT_ID);
            alkahestID = Reliquary.CONFIG.getItem("Alkahest", Indexes.ALKAHEST_DEFAULT_ID).getInt(Indexes.ALKAHEST_DEFAULT_ID);
            alkahestryTomeID = Reliquary.CONFIG.getItem("Tome", Indexes.TOME_DEFAULT_ID).getInt(Indexes.TOME_DEFAULT_ID);
            salamanderEyeID = Reliquary.CONFIG.getItem("SalamanderEye", Indexes.SALAMANDER_EYE_DEFAULT_ID).getInt(Indexes.SALAMANDER_EYE_DEFAULT_ID);
            wraithEyeID = Reliquary.CONFIG.getItem("WraithEye", Indexes.WRAITH_EYE_DEFAULT_ID).getInt(Indexes.WRAITH_EYE_DEFAULT_ID);
            voidTearID = Reliquary.CONFIG.getItem("VoidTear", Indexes.VOID_TEAR_DEFAULT_ID).getInt(Indexes.VOID_TEAR_DEFAULT_ID);
            emptyVoidTearID = Reliquary.CONFIG.getItem("EmptyVoidTear", Indexes.EMPTY_VOID_TEAR_DEFAULT_ID).getInt(Indexes.EMPTY_VOID_TEAR_DEFAULT_ID);
            satchelID = Reliquary.CONFIG.getItem("Satchel", Indexes.SATCHEL_DEFAULT_ID).getInt(Indexes.SATCHEL_DEFAULT_ID);
            altarActiveID = Reliquary.CONFIG.getBlock("AltarActive", Indexes.ALTAR_ACTIVE_DEFAULT_ID).getInt(Indexes.ALTAR_ACTIVE_DEFAULT_ID);
            altarIdleID = Reliquary.CONFIG.getBlock("AltarIdle", Indexes.ALTAR_IDLE_DEFAULT_ID).getInt(Indexes.ALTAR_IDLE_DEFAULT_ID);
            wraithNodeID = Reliquary.CONFIG.getBlock("WraithNode", Indexes.WRAITH_NODE_DEFAULT_ID).getInt(Indexes.WRAITH_NODE_DEFAULT_ID);
            lilypadID = Reliquary.CONFIG.getBlock("Lilypad", Indexes.LILYPAD_DEFAULT_ID).getInt(Indexes.LILYPAD_DEFAULT_ID);

            // miscellaneous options

        } catch (Exception e) {
            LogHelper.log(Level.SEVERE, Reference.MOD_NAME + " had a problem while loading its configuration.");
        }

    }

}
