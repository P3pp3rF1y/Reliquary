package xreliquary.init;

import cpw.mods.fml.common.registry.GameRegistry;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.items.ItemToggleable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import xreliquary.Reliquary;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryRedstoneRecipe;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class XRRecipes {

    public static ItemStack gunPart(int m) {
        return gunPart(1, m);
    }

    public static ItemStack gunPart(int i, int m) {
        return new ItemStack(ContentHandler.getItem(Names.gun_part), i, m);
    }

    public static ItemStack magazine(int m) {
        return magazine(1, m);
    }

    public static ItemStack magazine(int i, int m) {
        return new ItemStack(ContentHandler.getItem(Names.magazine), i, m);
    }

    public static ItemStack bullet(int m) {
        return bullet(1, m);
    }

    public static ItemStack bullet(int i, int m) {
        return new ItemStack(ContentHandler.getItem(Names.bullet), i, m);
    }

    public static ItemStack potion(int m) {
        return potion(1, m);
    }

    public static ItemStack potion(int i, int m) {
        return new ItemStack(ContentHandler.getItem(Names.condensed_potion), i, m);
    }

    public static void init() {

        GameRegistry.addRecipe(new ItemStack(ContentHandler.getBlock(Names.altar_idle), 1), "olo", "lel", "olo", 'o', Blocks.obsidian, 'l', Blocks.redstone_lamp, 'e', Items.emerald);
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getBlock(Names.lilypad), 1), "www", "wlw", "www", 'w', XRRecipes.potion(Reference.FERTILIZER_META), 'l', Blocks.waterlily);
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getBlock(Names.wraith_node), 1), "vv", "vv", 'v', ContentHandler.getItem(Names.void_tear_empty));
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.interdiction_torch), 1, 0), "mdm", "gbg", "gbg", 'm', Items.magma_cream, 'd', Items.diamond, 'g', ContentHandler.getItem(Names.glowing_water), 'b', Items.blaze_rod);

        GameRegistry.addShapelessRecipe(new ItemStack(ContentHandler.getItem(Names.glowing_bread), 3), new Object[]{Items.bread, Items.bread, Items.bread, ContentHandler.getItem(Names.glowing_water)});
        // bullets...
        // empty cases back into nuggets
        GameRegistry.addShapelessRecipe(new ItemStack(Items.gold_nugget, 1), bullet(1, 0));
        // neutral
        GameRegistry.addRecipe(bullet(4, 1), new Object[]{"sis", "ngn", "ngn", 's', Blocks.stone, 'i', Items.iron_ingot, 'n', Items.gold_nugget, 'g', Items.gunpowder});
        // exorcist
        GameRegistry.addRecipe(bullet(8, 2), new Object[]{"bbb", "bhb", "bbb", 'b', bullet(1, 1), 'h', ContentHandler.getItem(Names.glowing_water)});
        // blaze
        GameRegistry.addRecipe(bullet(4, 3), new Object[]{"prp", "npn", "ngn", 'p', Items.blaze_powder, 'r', Items.blaze_rod, 'n', Items.gold_nugget, 'g', Items.gunpowder});
        // ender
        GameRegistry.addRecipe(bullet(8, 4), new Object[]{"bbb", "beb", "bbb", 'b', bullet(1, 7), 'e', Items.ender_eye});
        // venom
        GameRegistry.addRecipe(bullet(4, 5), new Object[]{"gfg", "ngn", "ngn", 'f', Items.slime_ball, 'n', Items.gold_nugget, 'g', Items.gunpowder});
        // buster
        GameRegistry.addShapelessRecipe(bullet(4, 6), new Object[]{bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), Items.gunpowder, Items.gunpowder, Items.gunpowder, Items.gunpowder, Blocks.tnt});
        // seeker
        GameRegistry.addRecipe(bullet(4, 7), new Object[]{"sls", "nbn", "ngn", 's', Items.string, 'l', new ItemStack(Items.dye, 1, 4), 'b', Items.slime_ball, 'n', Items.gold_nugget, 'g', Items.gunpowder});

        // sand
        GameRegistry.addShapelessRecipe(bullet(4, 8), new Object[]{bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sandstone});

        // storm
        GameRegistry.addRecipe(bullet(8, 9), new Object[]{"bbb", "bhb", "bbb", 'b', bullet(1, 1), 'h', new ItemStack(Items.dye, 1, 4)});

        // magazines...
        GameRegistry.addRecipe(magazine(5, 0), new Object[]{"i i", "igi", "sis", 's', Blocks.stone, 'i', Items.iron_ingot, 'g', Blocks.glass});

        // neutral
        GameRegistry.addShapelessRecipe(magazine(1, 1), new Object[]{bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), magazine(1, 0)});
        // exorcist
        GameRegistry.addShapelessRecipe(magazine(1, 2), new Object[]{bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), magazine(1, 0)});
        // blaze
        GameRegistry.addShapelessRecipe(magazine(1, 3), new Object[]{bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), magazine(1, 0)});
        // ender
        GameRegistry.addShapelessRecipe(magazine(1, 4), new Object[]{bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), magazine(1, 0)});
        // venom
        GameRegistry.addShapelessRecipe(magazine(1, 5), new Object[]{bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), magazine(1, 0)});
        // buster
        GameRegistry.addShapelessRecipe(magazine(1, 6), new Object[]{bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), magazine(1, 0)});
        // seeker
        GameRegistry.addShapelessRecipe(magazine(1, 7), new Object[]{bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), magazine(1, 0)});

        // sand
        GameRegistry.addShapelessRecipe(magazine(1, 8), new Object[]{bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), magazine(1, 0)});

        // storm
        GameRegistry.addShapelessRecipe(magazine(1, 9), new Object[]{bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), magazine(1, 0)});

        // gunpart 0 = grip, 1 = barrel, 2 = mechanism
        GameRegistry.addRecipe(gunPart(1, 0), new Object[]{"iii", "imi", "ici", 'i', Items.iron_ingot, 'c', magazine(1, 0), 'm', Items.magma_cream});
        GameRegistry.addRecipe(gunPart(1, 1), new Object[]{"iii", "eme", "iii", 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'm', Items.magma_cream});
        GameRegistry.addRecipe(gunPart(1, 2), new Object[]{"iib", "rmi", "iii", 'i', Items.iron_ingot, 'b', Blocks.stone_button, 'r', Items.blaze_rod, 'm', Items.magma_cream});

        // handgun
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.handgun), 1, 0), new Object[]{"bim", "isi", "igi", 'i', Items.iron_ingot, 'b', gunPart(1, 1), 'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', Items.slime_ball});

        // tome and alkahestry recipes
        GameRegistry.addShapelessRecipe(((ItemToggleable) ContentHandler.getItem(Names.alkahestry_tome)).newItemStack(), new Object[]{Items.book, ContentHandler.getItem(Names.witch_hat), Items.magma_cream, Items.gold_ingot, Blocks.glowstone, Items.nether_wart, new ItemStack(Items.skull, 1, 1), Items.ghast_tear, Items.lava_bucket});

        GameRegistry.addRecipe(new AlkahestryRedstoneRecipe());
        GameRegistry.addRecipe(new AlkahestryCraftingRecipe());

        RecipeSorter.register(Reference.MOD_ID + ":alkahest_crafting", AlkahestryCraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
        RecipeSorter.register(Reference.MOD_ID + ":alkahest_redstone", AlkahestryRedstoneRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_crafting");

		/* other items */

        // fortune coin
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.fortune_coin), 1), new Object[]{"ege", "ghg", "ege", 'e', Items.ender_eye, 'g', Items.gold_ingot, 'h', ContentHandler.getItem(Names.glowing_water)});

        // cross of mercy
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.mercy_cross), 1), new Object[]{"wgw", "glg", "wgw", 'w', ContentHandler.getItem(Names.glowing_water), 'g', Items.gold_ingot, 'l', Items.leather});

        // grenade
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.holy_hand_grenade), 4), new Object[]{"wgw", "gtg", "wgw", 'w', ContentHandler.getItem(Names.glowing_water), 'g', Items.gold_nugget, 't', Blocks.tnt});

        // sojourner's staff
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.sojourner_staff), 1), new Object[]{"cgc", "gbg", "wgw", 'w', ContentHandler.getItem(Names.glowing_water), 'g', Items.gold_ingot, 'b', Items.blaze_rod, 'c', Items.magma_cream});

        // touchstone
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.midas_touchstone), 1, 0), new Object[]{"bbb", "rtr", "ggg", 'b', Blocks.glowstone, 'r', Items.blaze_rod, 'g', Items.gold_ingot, 't', Items.ghast_tear});

        // chalice
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.emperor_chalice), 1, 0), new Object[]{"gtg", "ege", "tgt", 't', Items.ghast_tear, 'e', Items.emerald, 'g', Items.gold_ingot});

        // salamander's eye
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.salamander_eye), 1), new Object[]{"bcb", "tet", "bcb", 'b', Items.blaze_rod, 'c', Items.magma_cream, 't', Items.ghast_tear, 'e', Items.ender_eye});

        // wraith's eye upgrade
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.wraith_eye), 1), new Object[]{"eee", "bsb", "eee", 'e', ContentHandler.getItem(Names.void_tear_empty), 's', ContentHandler.getItem(Names.salamander_eye), 'b', Blocks.emerald_block});

        // ice rod
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.ice_magus_rod), 1, 0), new Object[]{"dtd", "tpt", "tpt", 'd', Items.diamond, 't', Items.ghast_tear, 'p', Blocks.packed_ice});

        // magicbane
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.magicbane), 1, 0), new Object[]{"eee", "ege", "iee", 'e', Items.ender_eye, 'g', Items.gold_ingot, 'i', Items.iron_ingot});

        // rose
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.witherless_rose), 1), new Object[]{"hnh", "nrn", "hnh", 'h', ContentHandler.getItem(Names.glowing_water), 'n', Items.nether_star, 'r', new ItemStack(Blocks.double_plant, 1, 4)});

        // crimson cloth
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.crimson_cloth), 1), new Object[]{"prp", "bdb", "prp", 'p', potion(Reference.INVISIBILITY_META), 'r', new ItemStack(Blocks.wool, 1, Reference.RED_WOOL_META), 'b', new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META), 'd', potion(Reference.BLINDING_META)});

        // cloak
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.twilight_cloak), 1), new Object[]{"bcb", "cvc", "cbc", 'b', new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META), 'c', ContentHandler.getItem(Names.crimson_cloth), 'v', ContentHandler.getItem(Names.void_tear_empty)});

        // void tear
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.void_tear_empty), 1, 0), new Object[]{"et", "te", 'e', Items.ender_eye, 't', Items.ghast_tear});

        // angelic feather
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.angelic_feather), 1), new Object[]{"aja", "vfv", "aja", 'a', ContentHandler.getItem(Names.angelheart_vial), 'v', ContentHandler.getItem(Names.void_tear_empty), 'f', Items.feather, 'j', potion(Reference.BOUNDING_META)});

        // phoenix down
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.phoenix_down), 1), new Object[]{"ama", "mfm", "ama", 'm', Items.magma_cream, 'a', ContentHandler.getItem(Names.angelheart_vial), 'f', ContentHandler.getItem(Names.angelic_feather)});

        // dragon talon
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.dragon_talon), 1), new Object[]{"flf", "lvl", "flf", 'f', potion(Reference.FIRE_WARDING_META), 'l', Items.leather, 'v', ContentHandler.getItem(Names.void_tear_empty)});

        // dragon claws
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.dragon_claws), 1), new Object[]{"cmc", "cvc", "mcm", 'c', ContentHandler.getItem(Names.dragon_talon), 'm', Items.magma_cream, 'v', ContentHandler.getItem(Names.void_tear_empty)});

        // claws of the firedrinker
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.claws_of_the_firedrinker), 1), new Object[]{"cmc", "mem", "cmc", 'c', ContentHandler.getItem(Names.dragon_claws), 'm', Items.magma_cream, 'e', ContentHandler.getItem(Names.salamander_eye)});

        // squid beak to bonemeal
        GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META), new Object[]{ContentHandler.getItem(Names.squid_beak)});

        // kraken shell fragment
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.kraken_shell_fragment), 1, 0), new Object[]{"awa", "wbw", "awa", 'a', ContentHandler.getItem(Names.angelheart_vial), 'w', potion(Reference.BREATHING_META), 'b', ContentHandler.getItem(Names.squid_beak)});

        // kraken shell
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.kraken_shell), 1, 0), new Object[]{"sss", "svs", "sss", 's', ContentHandler.getItem(Names.kraken_shell_fragment), 'v', ContentHandler.getItem(Names.void_tear_empty)});

        // hero medallion
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.hero_medallion), 1), new Object[]{"eie", "ivi", "eie", 'e', Items.ender_eye, 'i', Items.iron_ingot, 'v', ContentHandler.getItem(Names.void_tear_empty)});

        // destruction catalyst
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.destruction_catalyst), 1, 0), new Object[]{"rrr", "rtr", "rrf", 'f', Items.flint, 't', new ItemStack(ContentHandler.getItem(Names.midas_touchstone), 1, -1), 'r', Items.blaze_rod});

        // serpent staff
        GameRegistry.addRecipe(new ItemStack(ContentHandler.getItem(Names.serpent_staff), 1), "oeo", "sks", " s ", 'o', Blocks.obsidian, 'e', Items.ender_eye, 's', Items.stick, 'k', ContentHandler.getItem(Names.kraken_shell));

		/* potions and splash potions */

        // empty vial
        GameRegistry.addRecipe(potion(5, Reference.EMPTY_VIAL_META), new Object[]{"g g", "g g", " g ", 'g', Blocks.glass_pane});

        // base solvent
        GameRegistry.addShapelessRecipe(potion(Reference.POTION_META), new Object[]{Items.nether_wart, potion(Reference.WATER_META)});

        // base splash solvent
        GameRegistry.addShapelessRecipe(potion(Reference.SPLASH_META), new Object[]{Items.nether_wart, Items.gunpowder, potion(Reference.WATER_META)});
        GameRegistry.addShapelessRecipe(potion(Reference.SPLASH_META), new Object[]{potion(Reference.POTION_META), Items.gunpowder});

        // glowing water
        GameRegistry.addShapelessRecipe(new ItemStack(ContentHandler.getItem(Names.glowing_water), 1), new Object[]{potion(Reference.SPLASH_META), Items.glowstone_dust, Items.glowstone_dust, Items.glowstone_dust});

        // angelheart vial
        GameRegistry.addShapelessRecipe(new ItemStack(ContentHandler.getItem(Names.angelheart_vial), 2), new Object[]{potion(Reference.PANACEA_META), ContentHandler.getItem(Names.glowing_water)});

        // speed potion
        GameRegistry.addShapelessRecipe(potion(Reference.SPEED_META), new Object[]{potion(Reference.POTION_META), Items.sugar, Items.redstone, Items.glowstone_dust});

        // dig potion
        GameRegistry.addShapelessRecipe(potion(Reference.DIGGING_META), new Object[]{potion(Reference.POTION_META), Items.bone, Items.redstone, Items.glowstone_dust});

        // strength potion
        GameRegistry.addShapelessRecipe(potion(Reference.STRENGTH_META), new Object[]{potion(Reference.POTION_META), Items.blaze_powder, Items.redstone, Items.glowstone_dust});

        // heal potion
        GameRegistry.addShapelessRecipe(potion(Reference.HEALING_META), new Object[]{potion(Reference.POTION_META), Items.speckled_melon, Items.glowstone_dust});

        // jump potion
        GameRegistry.addShapelessRecipe(potion(Reference.BOUNDING_META), new Object[]{potion(Reference.POTION_META), Items.feather, Items.redstone, Items.glowstone_dust});

        // regen potion
        GameRegistry.addShapelessRecipe(potion(Reference.REGENERATION_META), new Object[]{potion(Reference.POTION_META), Items.ghast_tear, Items.redstone, Items.glowstone_dust});

        // resist potion
        GameRegistry.addShapelessRecipe(potion(Reference.RESISTANCE_META), new Object[]{potion(Reference.POTION_META), Items.leather, Items.redstone, Items.glowstone_dust});

        // fire resist potion
        GameRegistry.addShapelessRecipe(potion(Reference.FIRE_WARDING_META), new Object[]{potion(Reference.POTION_META), Items.magma_cream, Items.redstone});

        // breathing potion
        GameRegistry.addShapelessRecipe(potion(Reference.BREATHING_META), new Object[]{potion(Reference.POTION_META), new ItemStack(Items.dye, 1, 0), Items.redstone});

        // invis potion
        GameRegistry.addShapelessRecipe(potion(Reference.INVISIBILITY_META), new Object[]{potion(Reference.INFRAVISION_META), Items.fermented_spider_eye, Items.redstone});

        // vision potion
        GameRegistry.addShapelessRecipe(potion(Reference.INFRAVISION_META), new Object[]{potion(Reference.POTION_META), Items.golden_carrot, Items.redstone});

        // protection potion
        GameRegistry.addShapelessRecipe(potion(4, Reference.PROTECTION_META), new Object[]{potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), Items.glowstone_dust, potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META)});

        // potency potion
        GameRegistry.addShapelessRecipe(potion(4, Reference.POTENCE_META), new Object[]{potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), Items.glowstone_dust, potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META)});

        // celerity potion
        GameRegistry.addShapelessRecipe(potion(4, Reference.CELERITY_META), new Object[]{potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), Items.glowstone_dust, potion(Reference.SPEED_META), potion(Reference.SPEED_META), potion(Reference.SPEED_META), potion(Reference.SPEED_META)});

        // stalker potion
        GameRegistry.addShapelessRecipe(potion(Reference.STALKER_META), new Object[]{potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), Items.glowstone_dust, potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META)});

        // panacea potion
        GameRegistry.addShapelessRecipe(potion(4, Reference.PANACEA_META), new Object[]{potion(Reference.HEALING_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), Items.milk_bucket, potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META)});

        // aphrodite
        GameRegistry.addShapelessRecipe(potion(Reference.APHRODITE_META), new Object[]{potion(Reference.SPLASH_META), new ItemStack(Items.dye, 1, 3), new ItemStack(Items.dye, 1, 1), Items.redstone});

        // poison
        GameRegistry.addShapelessRecipe(potion(Reference.POISON_META), new Object[]{potion(Reference.SPLASH_META), Items.spider_eye, Items.fermented_spider_eye, Items.redstone});

        // harm
        GameRegistry.addShapelessRecipe(potion(Reference.ACID_META), new Object[]{potion(Reference.SPLASH_META), Items.speckled_melon, Items.fermented_spider_eye, Items.glowstone_dust});

        // confusion
        GameRegistry.addShapelessRecipe(potion(Reference.CONFUSION_META), new Object[]{potion(Reference.SPLASH_META), Items.golden_carrot, Items.fermented_spider_eye, Items.redstone});

        // slowness
        GameRegistry.addShapelessRecipe(potion(Reference.SLOWING_META), new Object[]{potion(Reference.SPLASH_META), Items.sugar, Items.fermented_spider_eye, Items.glowstone_dust});

        // weakness
        GameRegistry.addShapelessRecipe(potion(Reference.WEAKNESS_META), new Object[]{potion(Reference.SPLASH_META), Items.blaze_powder, Items.fermented_spider_eye, Items.glowstone_dust});

        // wither
        GameRegistry.addShapelessRecipe(potion(Reference.WITHER_META), new Object[]{potion(Reference.SPLASH_META), new ItemStack(Items.skull, 1, 1), Items.glowstone_dust, Items.glowstone_dust});

        // blindness
        GameRegistry.addShapelessRecipe(potion(Reference.BLINDING_META), new Object[]{potion(Reference.SPLASH_META), Items.golden_carrot, Items.fermented_spider_eye, Items.golden_carrot});

        // ruin
        GameRegistry.addShapelessRecipe(potion(3, Reference.RUINATION_META), new Object[]{potion(Reference.SLOWING_META), potion(Reference.WEAKNESS_META), potion(Reference.POISON_META), Items.glowstone_dust});

        // fertility
        GameRegistry.addShapelessRecipe(potion(Reference.FERTILIZER_META), new Object[]{potion(Reference.SPLASH_META), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15)});

    }

}
