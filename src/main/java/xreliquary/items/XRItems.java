package xreliquary.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Config;
import xreliquary.items.alkahestry.AlkahestryRecipe;
import xreliquary.items.alkahestry.AlkahestryRedstoneRecipe;
import xreliquary.items.alkahestry.AlkahestryCraftingHandler;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class XRItems {
    public static Item handgun;
    public static Item magazine;
    public static Item bullet;
    public static Item emperorChalice;
    public static Item glowingBread;
    public static Item glowingWater;
    public static Item condensedPotion;
    public static Item distortionCloak;
    public static Item gunPart;
    public static Item sojournerStaff;
    public static Item mercyCross;
    public static Item fortuneCoin;
    public static Item midasTouchstone;
    public static Item iceRod;
    public static Item magicbane;
    public static Item witherlessRose;
    public static Item holyHandGrenade;
    public static Item destructionCatalyst;
    public static Item alkahestryTome;
    public static Item salamanderEye;
    public static Item wraithEye;
    public static Item emptyVoidTear;
    public static Item voidTear;
    public static Item voidSatchel;

    public static ItemStack gunPart(int m) {
        return gunPart(1, m);
    }

    public static ItemStack gunPart(int i, int m) {
        return new ItemStack(gunPart, i, m);
    }

    public static ItemStack magazine(int m) {
        return magazine(1, m);
    }

    public static ItemStack magazine(int i, int m) {
        return new ItemStack(magazine, i, m);
    }

    public static ItemStack bullet(int m) {
        return bullet(1, m);
    }

    public static ItemStack bullet(int i, int m) {
        return new ItemStack(bullet, i, m);
    }

    public static ItemStack potion(int m) {
        return potion(1, m);
    }

    public static ItemStack potion(int i, int m) {
        return new ItemStack(condensedPotion, i, m);
    }

    public static void init() {
        handgun = new ItemHandgun(Config.handgunID);
        bullet = new ItemBullet(Config.bulletID);
        mercyCross = new ItemMercyCross(Config.mercyCrossID);
        magazine = new ItemMagazine(Config.magazineID);
        holyHandGrenade = new ItemHolyHandGrenade(Config.holyHandGrenadeID);
        fortuneCoin = new ItemFortuneCoin(Config.fortuneCoinID);
        glowingWater = new ItemGlowingWater(Config.glowWaterID);
        gunPart = new ItemGunPart(Config.gunPartID);
        glowingBread = new ItemGlowingBread(Config.glowBreadID, 20, 1.0F, false);
        sojournerStaff = new ItemSojournerStaff(Config.sojournerStaffID);
        alkahestryTome = new ItemAlkahestryTome(Config.alkahestryTomeID);
        midasTouchstone = new ItemMidasTouchstone(Config.midasTouchstoneID);
        emperorChalice = new ItemEmperorChalice(Config.chaliceID);
        salamanderEye = new ItemSalamanderEye(Config.salamanderEyeID);
        iceRod = new ItemIceRod(Config.iceRodID);
        condensedPotion = new ItemCondensedPotion(Config.condensedPotionID);
        magicbane = new ItemMagicbane(Config.magicbaneID);
        witherlessRose = new ItemWitherlessRose(Config.witherlessRoseID);
        distortionCloak = new ItemDistortionCloak(Config.distortionCloakID);
        emptyVoidTear = new ItemEmptyVoidTear(Config.emptyVoidTearID);
        voidTear = new ItemVoidTear(Config.voidTearID);
        wraithEye = new ItemWraithEye(Config.wraithEyeID);
        voidSatchel = new ItemVoidSatchel(Config.satchelID);
        destructionCatalyst = new ItemDestructionCatalyst(
                Config.destructionCatalystID);

        LanguageRegistry.addName(handgun, Names.HANDGUN_LOCAL);

        LanguageRegistry.addName(potion(Reference.SPLASH_META),
                Names.SPLASH_NAME);
        LanguageRegistry.addName(potion(Reference.POISON_META),
                Names.POISON_NAME);
        LanguageRegistry.addName(potion(Reference.ACID_META), Names.ACID_NAME);
        LanguageRegistry.addName(potion(Reference.SLOWING_META),
                Names.SLOWING_NAME);
        LanguageRegistry.addName(potion(Reference.WEAKNESS_META),
                Names.WEAKNESS_NAME);
        LanguageRegistry.addName(potion(Reference.BLINDING_META),
                Names.BLINDING_NAME);
        LanguageRegistry.addName(potion(Reference.WITHER_META),
                Names.WITHER_NAME);
        LanguageRegistry.addName(potion(Reference.APHRODITE_META),
                Names.APHRODITE_NAME);
        LanguageRegistry.addName(potion(Reference.CONFUSION_META),
                Names.CONFUSION_NAME);
        LanguageRegistry.addName(potion(Reference.RUINATION_META),
                Names.RUINATION_NAME);
        LanguageRegistry.addName(potion(Reference.FERTILIZER_META),
                Names.FERTILIZER_NAME);
        LanguageRegistry.addName(potion(Reference.EMPTY_VIAL_META),
                Names.EMPTY_VIAL_NAME);
        LanguageRegistry.addName(potion(Reference.POTION_META),
                Names.POTION_NAME);
        LanguageRegistry
                .addName(potion(Reference.SPEED_META), Names.SPEED_NAME);
        LanguageRegistry.addName(potion(Reference.DIGGING_META),
                Names.DIGGING_NAME);
        LanguageRegistry.addName(potion(Reference.STRENGTH_META),
                Names.STRENGTH_NAME);
        LanguageRegistry.addName(potion(Reference.HEALING_META),
                Names.HEALING_NAME);
        LanguageRegistry.addName(potion(Reference.BOUNDING_META),
                Names.BOUNDING_NAME);
        LanguageRegistry.addName(potion(Reference.REGENERATION_META),
                Names.REGENERATION_NAME);
        LanguageRegistry.addName(potion(Reference.RESISTANCE_META),
                Names.RESISTANCE_NAME);
        LanguageRegistry.addName(potion(Reference.FIRE_WARDING_META),
                Names.FIRE_WARDING_NAME);
        LanguageRegistry.addName(potion(Reference.BREATHING_META),
                Names.BREATHING_NAME);
        LanguageRegistry.addName(potion(Reference.INVISIBILITY_META),
                Names.INVISIBILITY_NAME);
        LanguageRegistry.addName(potion(Reference.INFRAVISION_META),
                Names.INFRAVISION_NAME);
        LanguageRegistry.addName(potion(Reference.PROTECTION_META),
                Names.PROTECTION_NAME);
        LanguageRegistry.addName(potion(Reference.POTENCE_META),
                Names.POTENCE_NAME);
        LanguageRegistry.addName(potion(Reference.CELERITY_META),
                Names.CELERITY_NAME);
        LanguageRegistry.addName(potion(Reference.PANACEA_META),
                Names.PANACEA_NAME);
        LanguageRegistry
                .addName(potion(Reference.WATER_META), Names.WATER_NAME);

        LanguageRegistry.addName(mercyCross, Names.CROSS_LOCAL);
        LanguageRegistry.addName(holyHandGrenade, Names.GRENADE_LOCAL);
        LanguageRegistry.addName(fortuneCoin, Names.FORTUNE_COIN_LOCAL);
        LanguageRegistry.addName(glowingWater, Names.GLOWING_WATER_LOCAL);
        LanguageRegistry.addName(gunPart(0), Names.GUNPART_0_LOCAL);
        LanguageRegistry.addName(gunPart(1), Names.GUNPART_1_LOCAL);
        LanguageRegistry.addName(gunPart(2), Names.GUNPART_2_LOCAL);
        LanguageRegistry.addName(glowingBread, Names.BREAD_LOCAL);
        LanguageRegistry.addName(sojournerStaff, Names.TORCH_LOCAL);
        LanguageRegistry.addName(alkahestryTome, Names.TOME_LOCAL);
        LanguageRegistry.addName(midasTouchstone, Names.TOUCHSTONE_LOCAL);
        LanguageRegistry.addName(emperorChalice, Names.CHALICE_LOCAL);
        LanguageRegistry.addName(salamanderEye, Names.SALAMANDER_EYE_LOCAL);
        LanguageRegistry.addName(iceRod, Names.ICE_ROD_LOCAL);
        LanguageRegistry.addName(magicbane, Names.MAGICBANE_LOCAL);
        LanguageRegistry.addName(witherlessRose, Names.WITHERLESS_ROSE_LOCAL);
        LanguageRegistry.addName(distortionCloak, Names.DISTORTION_CLOAK_LOCAL);
        LanguageRegistry.addName(emptyVoidTear, Names.EMPTY_VOID_TEAR_LOCAL);
        LanguageRegistry.addName(voidTear, Names.VOID_TEAR_LOCAL);
        LanguageRegistry.addName(wraithEye, Names.WRAITH_EYE_LOCAL);
        LanguageRegistry.addName(voidSatchel, Names.VOID_SATCHEL_LOCAL);
        LanguageRegistry.addName(destructionCatalyst,
                Names.DESTRUCTION_CATALYST_LOCAL);

        addRecipes();
    }

    public static void addRecipes() {
        // bread
        GameRegistry
                .addShapelessRecipe(new ItemStack(glowingBread, 3),
                        new Object[] { Item.bread, Item.bread, Item.bread,
                                glowingWater });
        // bullets...
        // empty cases back into nuggets
        GameRegistry.addShapelessRecipe(new ItemStack(Item.goldNugget, 1),
                new Object[] { bullet(1, 0) });
        // neutral
        GameRegistry.addRecipe(bullet(4, 1), new Object[] { "sis", "ngn",
                "ngn", 's', Block.stone, 'i', Item.ingotIron, 'n',
                Item.goldNugget, 'g', Item.gunpowder });
        // exorcist
        GameRegistry.addRecipe(bullet(8, 2), new Object[] { "bbb", "bhb",
                "bbb", 'b', bullet(1, 1), 'h', glowingWater });
        // blaze
        GameRegistry.addRecipe(bullet(4, 3), new Object[] { "prp", "npn",
                "ngn", 'p', Item.blazePowder, 'r', Item.blazeRod, 'n',
                Item.goldNugget, 'g', Item.gunpowder });
        // ender
        GameRegistry.addRecipe(bullet(8, 4), new Object[] { "bbb", "beb",
                "bbb", 'b', bullet(1, 7), 'e', Item.eyeOfEnder });
        // venom
        GameRegistry.addRecipe(bullet(4, 5), new Object[] { "gfg", "ngn",
                "ngn", 'f', Item.slimeBall, 'n', Item.goldNugget, 'g',
                Item.gunpowder });
        // buster
        GameRegistry.addShapelessRecipe(bullet(4, 6),
                new Object[] { bullet(1, 5), bullet(1, 5), bullet(1, 5),
                        bullet(1, 5), Item.gunpowder, Item.gunpowder,
                        Item.gunpowder, Item.gunpowder, Block.tnt });
        // seeker
        GameRegistry.addRecipe(bullet(4, 7), new Object[] { "sls", "nbn",
                "ngn", 's', Item.silk, 'l',
                new ItemStack(Item.dyePowder, 1, 4), 'b', Item.slimeBall, 'n',
                Item.goldNugget, 'g', Item.gunpowder });

        // sand
        GameRegistry.addShapelessRecipe(bullet(4, 8),
                new Object[] { bullet(1, 1), bullet(1, 1), bullet(1, 1),
                        bullet(1, 1), Block.sand, Block.sand, Block.sand,
                        Block.sand, Block.sandStone });

        // storm
        GameRegistry.addRecipe(bullet(8, 9), new Object[] { "bbb", "bhb",
                "bbb", 'b', bullet(1, 1), 'h',
                new ItemStack(Item.dyePowder, 1, 4) });

        // magazines...
        GameRegistry.addRecipe(magazine(5, 0),
                new Object[] { "i i", "igi", "sis", 's', Block.stone, 'i',
                        Item.ingotIron, 'g', Block.glass });

        // neutral
        GameRegistry.addShapelessRecipe(magazine(1, 1),
                new Object[] { bullet(1, 1), bullet(1, 1), bullet(1, 1),
                        bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1),
                        bullet(1, 1), magazine(1, 0) });
        // exorcist
        GameRegistry.addShapelessRecipe(magazine(1, 2),
                new Object[] { bullet(1, 2), bullet(1, 2), bullet(1, 2),
                        bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2),
                        bullet(1, 2), magazine(1, 0) });
        // blaze
        GameRegistry.addShapelessRecipe(magazine(1, 3),
                new Object[] { bullet(1, 3), bullet(1, 3), bullet(1, 3),
                        bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3),
                        bullet(1, 3), magazine(1, 0) });
        // ender
        GameRegistry.addShapelessRecipe(magazine(1, 4),
                new Object[] { bullet(1, 4), bullet(1, 4), bullet(1, 4),
                        bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4),
                        bullet(1, 4), magazine(1, 0) });
        // venom
        GameRegistry.addShapelessRecipe(magazine(1, 5),
                new Object[] { bullet(1, 5), bullet(1, 5), bullet(1, 5),
                        bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5),
                        bullet(1, 5), magazine(1, 0) });
        // buster
        GameRegistry.addShapelessRecipe(magazine(1, 6),
                new Object[] { bullet(1, 6), bullet(1, 6), bullet(1, 6),
                        bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6),
                        bullet(1, 6), magazine(1, 0) });
        // seeker
        GameRegistry.addShapelessRecipe(magazine(1, 7),
                new Object[] { bullet(1, 7), bullet(1, 7), bullet(1, 7),
                        bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7),
                        bullet(1, 7), magazine(1, 0) });

        // sand
        GameRegistry.addShapelessRecipe(magazine(1, 8),
                new Object[] { bullet(1, 8), bullet(1, 8), bullet(1, 8),
                        bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8),
                        bullet(1, 8), magazine(1, 0) });

        // storm
        GameRegistry.addShapelessRecipe(magazine(1, 9),
                new Object[] { bullet(1, 9), bullet(1, 9), bullet(1, 9),
                        bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9),
                        bullet(1, 9), magazine(1, 0) });

        // coin
        GameRegistry.addRecipe(new ItemStack(fortuneCoin, 1), new Object[] {
                "ege", "ghg", "ege", 'e', Item.eyeOfEnder, 'g', Item.ingotGold,
                'h', glowingWater });

        // cross
        GameRegistry.addRecipe(new ItemStack(mercyCross, 1), new Object[] {
                "wgw", "glg", "wgw", 'w', glowingWater, 'g', Item.ingotGold,
                'l', Item.leather });

        // grenade
        GameRegistry.addRecipe(new ItemStack(holyHandGrenade, 4), new Object[] {
                "wgw", "gtg", "wgw", 'w', glowingWater, 'g', Item.goldNugget,
                't', Block.tnt });
        // torch
        GameRegistry.addRecipe(new ItemStack(sojournerStaff, 1), new Object[] {
                "cgc", "gbg", "wgw", 'w', glowingWater, 'g', Item.ingotGold,
                'b', Item.blazeRod, 'c', Item.magmaCream });

        // gunpart 0 = grip, 1 = barrel, 2 = mechanism
        GameRegistry.addRecipe(gunPart(1, 0), new Object[] { "iii", "imi",
                "ici", 'i', Item.ingotIron, 'c', magazine(1, 0), 'm',
                Item.magmaCream });
        GameRegistry.addRecipe(gunPart(1, 1), new Object[] { "iii", "eme",
                "iii", 'i', Item.ingotIron, 'e', Item.enderPearl, 'm',
                Item.magmaCream });
        GameRegistry.addRecipe(gunPart(1, 2), new Object[] { "iib", "rmi",
                "iii", 'i', Item.ingotIron, 'b', Block.stoneButton, 'r',
                Item.blazeRod, 'm', Item.magmaCream });

        // handgun
        GameRegistry.addRecipe(new ItemStack(handgun, 1, 0), new Object[] {
                "bim", "isi", "igi", 'i', Item.ingotIron, 'b', gunPart(1, 1),
                'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', Item.slimeBall });

        // tome
        ItemStack tombStack = new ItemStack(alkahestryTome, 1, 0);
        tombStack.setItemDamage(Config.tombRedstoneLimit);
        GameRegistry.addShapelessRecipe(tombStack,
                new Object[] { Item.book, Item.blazeRod, Item.magmaCream,
                        Item.ingotGold, Block.glowStone, Item.netherStalkSeeds,
                        new ItemStack(Item.skull, 1, 1), Item.ghastTear,
                        Item.bucketLava });
        
        GameRegistry.registerCraftingHandler(new AlkahestryCraftingHandler());
        GameRegistry.addRecipe(new AlkahestryRedstoneRecipe());
        GameRegistry.addRecipe(new AlkahestryRecipe());
        
        // touchstone
        GameRegistry
                .addRecipe(new ItemStack(midasTouchstone, 1, 0),
                        new Object[] { "bbb", "rtr", "ggg", 'b',
                                Block.blockGold, 'r', Item.blazeRod, 'g',
                                Item.ingotGold, 't', Item.ghastTear });

        // chalice
        GameRegistry.addRecipe(new ItemStack(emperorChalice, 1, 0),
                new Object[] { "gtg", "ege", "tgt", 't', Item.ghastTear, 'e',
                        Item.emerald, 'g', Item.ingotGold });

        // salamander's eye
        GameRegistry.addRecipe(new ItemStack(salamanderEye, 1, 0),
                new Object[] { "bcb", "tet", "bcb", 'b', Item.blazeRod, 'c',
                        Item.magmaCream, 't', Item.ghastTear, 'e',
                        Item.eyeOfEnder }); // wraith's eye upgrade
        GameRegistry.addRecipe(new ItemStack(wraithEye, 1, 0), new Object[] {
                "eee", "bsb", "eee", 'e', emptyVoidTear, 's', salamanderEye,
                'b', Block.blockEmerald });

        // ice rod
        GameRegistry.addRecipe(new ItemStack(iceRod, 1, 0), new Object[] {
                "dtd", "tit", "tit", 'd', Item.diamond, 't', Item.ghastTear,
                'i', Item.ingotIron });

        // magicbane
        GameRegistry.addRecipe(new ItemStack(magicbane, 1, 0), new Object[] {
                "eee", "ege", "iee", 'e', Item.eyeOfEnder, 'g', Item.ingotGold,
                'i', Item.ingotIron });

        // rose
        GameRegistry.addRecipe(new ItemStack(witherlessRose, 1, 0),
                new Object[] { "hnh", "nrn", "hnh", 'h', glowingWater, 'n',
                        Item.netherStar, 'r', Block.plantRed });

        // cloak
        GameRegistry.addRecipe(new ItemStack(distortionCloak, 1, 0),
                new Object[] { "eee", "ewe", "eee", 'e', Item.eyeOfEnder, 'w',
                        new ItemStack(Block.cloth, 1, 11) });

        // void tear
        GameRegistry.addRecipe(new ItemStack(emptyVoidTear, 1, 0),
                new Object[] { "et", "te", 'e', Item.eyeOfEnder, 't',
                        Item.ghastTear });

        // TODO possibly give satchels a damage bar, so these recipes will have
        // to change.

        // void satchel
        GameRegistry.addRecipe(new ItemStack(voidSatchel, 1, 0), new Object[] {
                "lsl", "ltl", "lll", 'l', Item.leather, 's', Item.silk, 't',
                voidTear }); // upgrade it!
        GameRegistry.addShapelessRecipe(new ItemStack(voidSatchel, 1, 0),
                new Object[] { emptyVoidTear, emptyVoidTear, emptyVoidTear,
                        voidSatchel });

        // destruction catalyst
        GameRegistry.addRecipe(new ItemStack(destructionCatalyst, 1, 0),
                new Object[] { "rrr", "rtr", "rrf", 'f', Item.flint, 't',
                        new ItemStack(midasTouchstone, 1, -1), 'r',
                        Item.blazeRod });

        // potions!
        // empty vial
        GameRegistry.addRecipe(potion(5, Reference.EMPTY_VIAL_META),
                new Object[] { "g g", "g g", " g ", 'g', Block.thinGlass });

        // base solvent
        GameRegistry.addShapelessRecipe(potion(Reference.POTION_META),
                new Object[] { Item.netherStalkSeeds, Item.redstone,
                        Item.glowstone, potion(Reference.WATER_META) });

        // base splash solvent
        GameRegistry.addShapelessRecipe(potion(Reference.SPLASH_META),
                new Object[] { Item.netherStalkSeeds, Item.gunpowder,
                        Item.glowstone, potion(Reference.WATER_META) });

        // glowing water
        GameRegistry.addShapelessRecipe(new ItemStack(glowingWater, 1),
                new Object[] { potion(Reference.SPLASH_META),
                        Item.glowstone, Item.glowstone,
                        Item.glowstone });

        // speed potion
        GameRegistry.addShapelessRecipe(potion(Reference.SPEED_META),
                new Object[] { potion(Reference.POTION_META), Item.sugar,
                        Item.redstone, Item.glowstone });

        // dig potion
        GameRegistry.addShapelessRecipe(potion(Reference.DIGGING_META),
                new Object[] { potion(Reference.POTION_META), Item.bone,
                        Item.redstone, Item.glowstone });

        // strength potion
        GameRegistry.addShapelessRecipe(potion(Reference.STRENGTH_META),
                new Object[] { potion(Reference.POTION_META), Item.blazePowder,
                        Item.redstone, Item.glowstone });

        // heal potion
        GameRegistry.addShapelessRecipe(potion(Reference.HEALING_META),
                new Object[] { potion(Reference.POTION_META),
                        Item.speckledMelon, Item.glowstone,
                        Item.glowstone });

        // jump potion
        GameRegistry.addShapelessRecipe(potion(Reference.BOUNDING_META),
                new Object[] { potion(Reference.POTION_META), Item.feather,
                        Item.redstone, Item.glowstone });

        // regen potion
        GameRegistry.addShapelessRecipe(potion(Reference.REGENERATION_META),
                new Object[] { potion(Reference.POTION_META), Item.ghastTear,
                        Item.redstone, Item.redstone });

        // resist potion
        GameRegistry.addShapelessRecipe(potion(Reference.RESISTANCE_META),
                new Object[] { potion(Reference.POTION_META), Item.leather,
                        Item.redstone, Item.redstone });

        // fire resist potion
        GameRegistry.addShapelessRecipe(potion(Reference.FIRE_WARDING_META),
                new Object[] { potion(Reference.POTION_META), Item.magmaCream,
                        Item.redstone, Item.redstone });

        // breathing potion
        GameRegistry.addShapelessRecipe(potion(Reference.BREATHING_META),
                new Object[] { potion(Reference.POTION_META),
                        new ItemStack(Item.dyePowder, 1, 0), Item.redstone,
                        Item.redstone });

        // invis potion
        GameRegistry
                .addShapelessRecipe(potion(Reference.INVISIBILITY_META),
                        new Object[] { potion(Reference.INFRAVISION_META),
                                Item.fermentedSpiderEye, Item.redstone,
                                Item.redstone });

        // vision potion
        GameRegistry.addShapelessRecipe(potion(Reference.INFRAVISION_META),
                new Object[] { potion(Reference.POTION_META),
                        Item.goldenCarrot, Item.redstone, Item.redstone });

        // protection potion
        GameRegistry.addShapelessRecipe(potion(8, Reference.PROTECTION_META),
                new Object[] { potion(Reference.FIRE_WARDING_META),
                        potion(Reference.FIRE_WARDING_META),
                        potion(Reference.FIRE_WARDING_META),
                        potion(Reference.FIRE_WARDING_META),
                        Item.glowstone, potion(Reference.RESISTANCE_META),
                        potion(Reference.RESISTANCE_META),
                        potion(Reference.RESISTANCE_META),
                        potion(Reference.RESISTANCE_META) });

        // potence potion
        GameRegistry.addShapelessRecipe(potion(8, Reference.POTENCE_META),
                new Object[] { potion(Reference.STRENGTH_META),
                        potion(Reference.STRENGTH_META),
                        potion(Reference.STRENGTH_META),
                        potion(Reference.STRENGTH_META), Item.glowstone,
                        potion(Reference.BOUNDING_META),
                        potion(Reference.BOUNDING_META),
                        potion(Reference.BOUNDING_META),
                        potion(Reference.BOUNDING_META) });

        // celerity potion
        GameRegistry.addShapelessRecipe(potion(8, Reference.CELERITY_META),
                new Object[] { potion(Reference.DIGGING_META),
                        potion(Reference.DIGGING_META),
                        potion(Reference.DIGGING_META),
                        potion(Reference.DIGGING_META), Item.glowstone,
                        potion(Reference.SPEED_META),
                        potion(Reference.SPEED_META),
                        potion(Reference.SPEED_META),
                        potion(Reference.SPEED_META) });

        // panacea potion
        GameRegistry.addShapelessRecipe(potion(8, Reference.PANACEA_META),
                new Object[] { potion(Reference.HEALING_META),
                        potion(Reference.HEALING_META),
                        potion(Reference.HEALING_META),
                        potion(Reference.HEALING_META), Item.bucketMilk,
                        potion(Reference.REGENERATION_META),
                        potion(Reference.REGENERATION_META),
                        potion(Reference.REGENERATION_META),
                        potion(Reference.REGENERATION_META) });

        // aphrodite
        GameRegistry.addShapelessRecipe(potion(Reference.APHRODITE_META),
                new Object[] { potion(Reference.SPLASH_META),
                        new ItemStack(Item.dyePowder, 1, 3),
                        new ItemStack(Item.dyePowder, 1, 1), Item.redstone });

        // poison
        GameRegistry.addShapelessRecipe(potion(Reference.POISON_META),
                new Object[] { potion(Reference.SPLASH_META), Item.spiderEye,
                        Item.fermentedSpiderEye, Item.redstone });

        // harm
        GameRegistry.addShapelessRecipe(potion(Reference.ACID_META),
                new Object[] { potion(Reference.SPLASH_META),
                        Item.speckledMelon, Item.fermentedSpiderEye,
                        Item.glowstone });

        // confusion
        GameRegistry.addShapelessRecipe(potion(Reference.CONFUSION_META),
                new Object[] { potion(Reference.SPLASH_META),
                        Item.goldenCarrot, Item.fermentedSpiderEye,
                        Item.redstone });

        // slowness
        GameRegistry.addShapelessRecipe(potion(Reference.SLOWING_META),
                new Object[] { potion(Reference.SPLASH_META), Item.sugar,
                        Item.fermentedSpiderEye, Item.glowstone });

        // weakness
        GameRegistry.addShapelessRecipe(potion(Reference.WEAKNESS_META),
                new Object[] { potion(Reference.SPLASH_META), Item.blazePowder,
                        Item.fermentedSpiderEye, Item.glowstone });

        // wither
        GameRegistry.addShapelessRecipe(potion(Reference.WITHER_META),
                new Object[] { potion(Reference.SPLASH_META),
                        new ItemStack(Item.skull, 1, 1), Item.glowstone,
                        Item.glowstone });

        // blindness
        GameRegistry.addShapelessRecipe(potion(Reference.BLINDING_META),
                new Object[] { potion(Reference.SPLASH_META),
                        Item.goldenCarrot, Item.fermentedSpiderEye,
                        Item.goldenCarrot });

        // ruin
        GameRegistry.addShapelessRecipe(potion(3, Reference.RUINATION_META),
                new Object[] { potion(Reference.SLOWING_META),
                        potion(Reference.WEAKNESS_META),
                        potion(Reference.POISON_META), Item.glowstone });

        // fertility
        GameRegistry.addShapelessRecipe(potion(Reference.FERTILIZER_META),
                new Object[] { potion(Reference.SPLASH_META),
                        new ItemStack(Item.dyePowder, 1, 15),
                        new ItemStack(Item.dyePowder, 1, 15),
                        new ItemStack(Item.dyePowder, 1, 15) });

        GameRegistry.registerCraftingHandler(new WaterHandler());
        GameRegistry.registerCraftingHandler(new VoidUpgradableHandler());

        // URGENT TODO DEBUG RECIPES NEED TO BE REMOVED EVERY RELEASE!
        // IF THERE'S NOTHING BELOW THIS POINT, GREAT.

    }
}
