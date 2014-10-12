package xreliquary.init;

import cpw.mods.fml.common.registry.GameRegistry;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import xreliquary.Reliquary;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryRedstoneRecipe;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class XRRecipes {

    //convenience itemstack methods, these are just to make it a little easier to read.
    public static Item getItem(String name) { return ContentHandler.getItem(name); }

    public static ItemStack fertilizer() { return XRRecipes.potion(Reference.FERTILIZER_META); }

    public static ItemStack emptyVoidTear() { return ((ItemToggleable) getItem(Names.void_tear)).newItemStack(); }

    public static ItemStack witherSkull() { return new ItemStack(Items.skull, 1, 1); }

    public static ItemStack roseBush() { return new ItemStack(Blocks.double_plant, 1, 4); }

    public static ItemStack blackWool() { return new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META); }

    public static ItemStack lapis() { return new ItemStack(Items.dye, 1, 4); }

    public static ItemStack gunPart(int i, int m) { return new ItemStack(getItem(Names.gun_part), i, m); }

    public static ItemStack magazine(int m) { return magazine(1, m); }

    public static ItemStack magazine(int i, int m) { return new ItemStack(getItem(Names.magazine), i, m); }

    public static ItemStack bullet(int m) { return bullet(1, m); }

    public static ItemStack bullet(int i, int m) { return new ItemStack(getItem(Names.bullet), i, m); }

    public static ItemStack potion(int m) { return potion(1, m); }

    public static ItemStack potion(int i, int m) { return new ItemStack(getItem(Names.condensed_potion), i, m); }

    public static ItemStack ingredient(int m) { return new ItemStack(getItem(Names.mob_ingredient), 1, m); }
    
    public static ItemStack heartPearl(int m) { return new ItemStack(getItem(Names.heart_pearl), 1, m); }

    public static ItemStack heartZhu(int m) { return new ItemStack(getItem(Names.heart_zhu), 1, m); }


    //this version of the addRecipe method checks first to see if the recipe is disabled in our automated recipe-disabler config
    //if any component of the item is in the recipe disabler list, it will ALSO block the recipe automatically.
    public static void addRecipe(boolean isShapeless, ItemStack result, Object... params) {
        boolean enabled = Reliquary.CONFIG.getGroup(Names.recipe_enabled).containsKey(ContentHelper.getIdent(result.getItem())) && Reliquary.CONFIG.getBool(Names.recipe_enabled, ContentHelper.getIdent(result.getItem()));
        if (!enabled) return;
        for (Object obj : params) {
            String unlocalizedName = null;
            if (obj instanceof Block) {
                unlocalizedName = ContentHelper.getIdent((Block) obj);
            } else if(obj instanceof Item) {
                unlocalizedName = ContentHelper.getIdent((Item) obj);
            } else if(obj instanceof ItemStack) {
                unlocalizedName = ContentHelper.getIdent(((ItemStack)obj).getItem());
            }
            if (!Reliquary.CONFIG.getKeys(Names.recipe_enabled).contains(unlocalizedName))
                continue;
            enabled = enabled && Reliquary.CONFIG.getBool(Names.recipe_enabled, unlocalizedName);
        }
        if (!enabled) return;
        if (!isShapeless) GameRegistry.addRecipe(result, params);
            else GameRegistry.addShapelessRecipe(result, params);
    }

    public static void init() {
        // tome and alkahestry recipes
        GameRegistry.addRecipe(new AlkahestryRedstoneRecipe());
        GameRegistry.addRecipe(new AlkahestryCraftingRecipe());

        RecipeSorter.register(Reference.MOD_ID + ":alkahest_crafting", AlkahestryCraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
        RecipeSorter.register(Reference.MOD_ID + ":alkahest_redstone", AlkahestryRedstoneRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_crafting");

        addRecipe(true, ((ItemToggleable) getItem(Names.alkahestry_tome)).newItemStack(), Items.book, getItem(Names.witch_hat), ingredient(Reference.MOLTEN_INGREDIENT_META), witherSkull());

        addRecipe(true, new ItemStack(ContentHandler.getBlock(Names.altar_idle), 1), Blocks.obsidian, Blocks.redstone_lamp, ingredient(Reference.ENDER_INGREDIENT_META), ingredient(Reference.CREEPER_INGREDIENT_META));
        addRecipe(true, new ItemStack(ContentHandler.getBlock(Names.lilypad), 1), ingredient(Reference.FERTILE_INGREDIENT_META), ingredient(Reference.FERTILE_INGREDIENT_META), ingredient(Reference.FERTILE_INGREDIENT_META), Blocks.waterlily);
        addRecipe(true, new ItemStack(ContentHandler.getBlock(Names.wraith_node), 1), ingredient(Reference.ENDER_INGREDIENT_META), Items.emerald);
        addRecipe(false, new ItemStack(getItem(Names.interdiction_torch), 1, 0), "bm", "nr", 'm', ingredient(Reference.MOLTEN_INGREDIENT_META), 'b', ingredient(Reference.BAT_INGREDIENT_META), 'r', Items.blaze_rod, 'n', ingredient(Reference.ENDER_INGREDIENT_META));

        // glowy bread
        addRecipe(true, new ItemStack(getItem(Names.glowing_bread), 3), Items.bread, Items.bread, Items.bread, getItem(Names.glowing_water));

        // fertile essence
        addRecipe(true, ingredient(Reference.FERTILE_INGREDIENT_META), fertilizer(), fertilizer(), fertilizer(), fertilizer(), fertilizer(), fertilizer(), fertilizer(), fertilizer(), Items.wheat_seeds);

        // bullets...
        // empty cases back into nuggets
        addRecipe(true, new ItemStack(Items.gold_nugget, 1), bullet(1, 0), bullet(1, 0), bullet(1, 0), bullet(1, 0));
        // neutral
        addRecipe(true, bullet(8, 1), Items.flint, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // exorcist
        addRecipe(true, bullet(8, 2), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), ingredient(Reference.ZOMBIE_INGREDIENT_META));
        // blaze
        addRecipe(true, bullet(8, 3), Items.blaze_powder, Items.blaze_rod, Items.gold_nugget, Items.gold_nugget);
        // ender
        addRecipe(true, bullet(8, 4),  bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), ingredient(Reference.ENDER_INGREDIENT_META));
        // concussive
        addRecipe(true, bullet(8, 5), ingredient(Reference.SLIME_INGREDIENT_META), Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // buster
        addRecipe(true, bullet(8, 6), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), ingredient(Reference.CREEPER_INGREDIENT_META));
        // seeker
        addRecipe(true, bullet(8, 7), Items.string, ingredient(Reference.SLIME_INGREDIENT_META), Items.gold_nugget, Items.gunpowder);
        // sand
        addRecipe(true, bullet(8, 8), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), Blocks.sandstone);
        // storm
        addRecipe(true, bullet(8, 9), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), ingredient(Reference.STORM_INGREDIENT_META));
        // frozen shot TODO
        // venom shot TODO
        // fertile shot TODO
        // rage shot TODO
        // traitor shot TODO
        // calm shot TODO
        // molten shot TODO

        // magazines...
        addRecipe(false, magazine(5, 0), "i i", "igi", "sis", 's', Blocks.stone, 'i', Items.iron_ingot, 'g', Blocks.glass);

        // neutral
        addRecipe(true, magazine(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), magazine(1, 0));
        // exorcist
        addRecipe(true, magazine(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), magazine(1, 0));
        // blaze
        addRecipe(true, magazine(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), magazine(1, 0));
        // ender
        addRecipe(true, magazine(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), magazine(1, 0));
        // venom
        addRecipe(true, magazine(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), magazine(1, 0));
        // buster
        addRecipe(true, magazine(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), magazine(1, 0));
        // seeker
        addRecipe(true, magazine(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), magazine(1, 0));
        // sand
        addRecipe(true, magazine(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), magazine(1, 0));
        // storm
        addRecipe(true, magazine(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), magazine(1, 0));

        // gunpart 0 = grip, 1 = barrel, 2 = mechanism
        addRecipe(false, gunPart(1, 0), "iii", "imi", "ici", 'i', Items.iron_ingot, 'c', magazine(1, 0), 'm', Items.magma_cream);
        addRecipe(false, gunPart(1, 1), "iii", "eme", "iii", 'i', Items.iron_ingot, 'e', ingredient(Reference.ENDER_INGREDIENT_META), 'm', Items.magma_cream);
        addRecipe(false, gunPart(1, 2), "iib", "rmi", "iii", 'i', Items.iron_ingot, 'b', Blocks.stone_button, 'r', Items.blaze_rod, 'm', ingredient(Reference.MOLTEN_INGREDIENT_META));

        // handgun
        addRecipe(false, new ItemStack(getItem(Names.handgun), 1, 0), "bim", "isi", "igi", 'i', Items.iron_ingot, 'b', gunPart(1, 1), 'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', ingredient(Reference.SLIME_INGREDIENT_META));

		/* other items */

        // fortune coin
        addRecipe(true, new ItemStack(getItem(Names.fortune_coin), 1), ingredient(Reference.ENDER_INGREDIENT_META), Items.gold_nugget, ingredient(Reference.SLIME_INGREDIENT_META), ingredient(Reference.BAT_INGREDIENT_META));

        // cross of mercy
        addRecipe(false, new ItemStack(getItem(Names.mercy_cross), 1), "wgr", "glg", "sgz", 'w', ingredient(Reference.WITHER_INGREDIENT_META), 'g', Items.gold_ingot, 'r', ingredient(Reference.SKELETON_INGREDIENT_META), 'l', Items.leather, 's', new ItemStack(Items.skull, 1, 1), 'z', ingredient(Reference.ZOMBIE_INGREDIENT_META));

        // holy hand grenade
        addRecipe(true, new ItemStack(getItem(Names.holy_hand_grenade), 4), getItem(Names.glowing_water), Items.gold_nugget, Blocks.tnt, ingredient(Reference.CREEPER_INGREDIENT_META));

        // sojourner's staff
        addRecipe(false, new ItemStack(getItem(Names.sojourner_staff), 1), "cmc", "gbg", "gvg", 'm', ingredient(Reference.MOLTEN_INGREDIENT_META), 'g', Items.gold_nugget, 'b', Items.blaze_rod, 'c', Items.magma_cream, 'v', emptyVoidTear());

        // lantern of paranoia
        addRecipe(false, new ItemStack(getItem(Names.lantern_of_paranoia), 1), "isi", "gmg", "ili", 'i', Items.iron_ingot, 'm', ingredient(Reference.MOLTEN_INGREDIENT_META), 'g', Blocks.glass, 'n', ingredient(Reference.SLIME_INGREDIENT_META), 'l', ingredient(Reference.CREEPER_INGREDIENT_META));

        // midas touchstone
        addRecipe(true, new ItemStack(getItem(Names.midas_touchstone), 1, 0), Blocks.anvil, Blocks.gold_block, Blocks.gold_block, Blocks.gold_block, ingredient(Reference.MOLTEN_INGREDIENT_META), ingredient(Reference.MOLTEN_INGREDIENT_META), ingredient(Reference.MOLTEN_INGREDIENT_META), ingredient(Reference.CREEPER_INGREDIENT_META), emptyVoidTear());

        // emperor's chalice
        addRecipe(false, new ItemStack(getItem(Names.emperor_chalice), 1, 0), "sgs", "iii", "lbl", 's', ingredient(Reference.SLIME_INGREDIENT_META), 'g', ingredient(Reference.CREEPER_INGREDIENT_META), 'i', Items.gold_ingot, 'l', lapis(), 'b', Items.bucket);

        // infernal chalice
        addRecipe(false, new ItemStack(getItem(Names.infernal_chalice), 1), "mmm", "mcm", "mim", 'i', getItem(Names.infernal_claws), 'm', ingredient(Reference.MOLTEN_INGREDIENT_META), 'c', getItem(Names.emperor_chalice));

        // salamander's eye
        addRecipe(true, new ItemStack(getItem(Names.salamander_eye), 1), Items.ender_eye, ingredient(Reference.MOLTEN_INGREDIENT_META), ingredient(Reference.FROZEN_INGREDIENT_META), ingredient(Reference.ENDER_INGREDIENT_META));

        // ice rod
        addRecipe(false, new ItemStack(getItem(Names.ice_magus_rod), 1, 0), " df", " id", "v  ", 'd', Items.diamond, 'v', emptyVoidTear(), 'i', Items.iron_ingot, 'f', ingredient(Reference.FROZEN_INGREDIENT_META));

        //glacial staff
        addRecipe(true, new ItemStack(getItem(Names.glacial_staff), 1, 0), getItem(Names.ice_magus_rod), emptyVoidTear(), ingredient(Reference.FROZEN_INGREDIENT_META), getItem(Names.shears_of_winter));

        // ender staff
        addRecipe(false, new ItemStack(getItem(Names.ender_staff), 1, 0), " be", "nvb", "sn ", 'v', getItem(Names.void_tear), 'e', Items.ender_eye, 's', Items.stick, 'n', ingredient(Reference.ENDER_INGREDIENT_META), 'b', ingredient(Reference.BAT_INGREDIENT_META));

        // rending gale
        addRecipe(false, new ItemStack(getItem(Names.rending_gale), 1, 0), " be", "gsb", "vg ", 'b', ingredient(Reference.BAT_INGREDIENT_META), 'e', ingredient(Reference.STORM_INGREDIENT_META), 'g', Items.gold_ingot, 's', Items.stick, 'v', emptyVoidTear());

        // harvest rod
        addRecipe(false, new ItemStack(getItem(Names.harvest_rod), 1, 0), " rf", "vtr", "sv ", 'r', roseBush(), 'f', ingredient(Reference.FERTILE_INGREDIENT_META), 'v', Blocks.vine, 't', emptyVoidTear(), 's', Items.stick );

        // pyromancer staff
        addRecipe(false, new ItemStack(getItem(Names.pyromancer_staff), 1, 0), " is", " ci", "v  ", 'i', ingredient(Reference.CLAW_INGREDIENT_META), 's', getItem(Names.salamander_eye), 'c', getItem(Names.infernal_claws), 'v', emptyVoidTear());

        // serpent staff
        addRecipe(false, new ItemStack(getItem(Names.serpent_staff), 1), "oeo", "ckc", " s ", 'o', Blocks.obsidian, 'e', Items.ender_eye, 's', Items.stick, 'k', ingredient(Reference.SLIME_INGREDIENT_META), 'c', ingredient(Reference.SPIDER_INGREDIENT_META));

        // rod of lyssa
        addRecipe(true, new ItemStack(getItem(Names.rod_of_lyssa), 1, 0), ingredient(Reference.CLAW_INGREDIENT_META), ingredient(Reference.BAT_INGREDIENT_META), ingredient(Reference.ENDER_INGREDIENT_META), Items.fishing_rod);

        // shears of winter
        addRecipe(true, new ItemStack(getItem(Names.shears_of_winter), 1, 0), ingredient(Reference.FROZEN_INGREDIENT_META), Items.shears, Items.diamond, Items.diamond);

        // magicbane
        addRecipe(false, new ItemStack(getItem(Names.magicbane), 1, 0), "ng", "in", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'n', ingredient(Reference.ENDER_INGREDIENT_META));

        // witherless rose
        addRecipe(false, new ItemStack(getItem(Names.witherless_rose), 1), "fnf", "nrn", "fnf", 'f', ingredient(Reference.FERTILE_INGREDIENT_META), 'n', Items.nether_star, 'r', roseBush() );

        // crimson cloth
        addRecipe(true, ingredient(Reference.CLOTH_INGREDIENT_META), new ItemStack(Blocks.wool, 1, Reference.RED_WOOL_META), new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META), ingredient(Reference.ENDER_INGREDIENT_META), ingredient(Reference.ENDER_INGREDIENT_META));

        // cloak
        addRecipe(false, new ItemStack(getItem(Names.twilight_cloak), 1), "ici", "bcb", "bcb", 'i', Items.iron_ingot, 'b', blackWool(), 'c', ingredient(Reference.CLOTH_INGREDIENT_META));

        // void tear
        addRecipe(true, emptyVoidTear(), Items.ghast_tear, ingredient(Reference.ENDER_INGREDIENT_META), ingredient(Reference.SLIME_INGREDIENT_META), lapis());

        // angelic feather
        addRecipe(true, new ItemStack(getItem(Names.angelic_feather), 1), Items.feather, ingredient(Reference.FROZEN_INGREDIENT_META), ingredient(Reference.BAT_INGREDIENT_META), ingredient(Reference.FERTILE_INGREDIENT_META));

        // phoenix down
        addRecipe(true, new ItemStack(getItem(Names.phoenix_down), 1), getItem(Names.angelheart_vial), getItem(Names.angelheart_vial), getItem(Names.angelheart_vial), getItem(Names.angelic_feather));

        // infernal claw
        addRecipe(true, ingredient(Reference.CLAW_INGREDIENT_META), Items.leather, ingredient(Reference.MOLTEN_INGREDIENT_META), ingredient(Reference.SKELETON_INGREDIENT_META), ingredient(Reference.SLIME_INGREDIENT_META));

        // infernal claws
        addRecipe(true, new ItemStack(getItem(Names.infernal_claws), 1), ingredient(Reference.CLAW_INGREDIENT_META), ingredient(Reference.CLAW_INGREDIENT_META), ingredient(Reference.CLAW_INGREDIENT_META), ingredient(Reference.SLIME_INGREDIENT_META));

        // squid beak, wither rib & rib bone to bonemeal
        addRecipe(true, new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META), ingredient(Reference.SQUID_INGREDIENT_META));
        addRecipe(true, new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META), ingredient(Reference.SKELETON_INGREDIENT_META));
        addRecipe(true, new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META), ingredient(Reference.WITHER_INGREDIENT_META));

        // kraken shell fragment
        addRecipe(true, ingredient(Reference.SHELL_INGREDIENT_META), ingredient(Reference.SQUID_INGREDIENT_META), ingredient(Reference.SQUID_INGREDIENT_META), ingredient(Reference.SQUID_INGREDIENT_META), ingredient(Reference.SLIME_INGREDIENT_META));

        // kraken shell
        addRecipe(true, new ItemStack(getItem(Names.kraken_shell), 1, 0), ingredient(Reference.SHELL_INGREDIENT_META), ingredient(Reference.SHELL_INGREDIENT_META), ingredient(Reference.SHELL_INGREDIENT_META), ingredient(Reference.ENDER_INGREDIENT_META));

        // hero medallion
        addRecipe(true, new ItemStack(getItem(Names.hero_medallion), 1), ingredient(Reference.ENDER_INGREDIENT_META), getItem(Names.fortune_coin), getItem(Names.witch_hat), emptyVoidTear());

        // destruction catalyst
        addRecipe(true, new ItemStack(getItem(Names.destruction_catalyst), 1, 0), Items.flint_and_steel, ingredient(Reference.MOLTEN_INGREDIENT_META), ingredient(Reference.CREEPER_INGREDIENT_META), emptyVoidTear());

        // nian zhu heart pearls
        addRecipe(false, heartPearl(Reference.ZOMBIE_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.ZOMBIE_INGREDIENT_META), 's', Items.rotten_flesh, 't', Items.bone);
        addRecipe(false, heartPearl(Reference.PIG_ZOMBIE_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.ZOMBIE_INGREDIENT_META), 's', Items.porkchop, 't', Items.gold_nugget);
        addRecipe(false, heartPearl(Reference.SKELETON_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.SKELETON_INGREDIENT_META), 's', Items.bone, 't', Items.flint);
        addRecipe(false, heartPearl(Reference.WITHER_SKELETON_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.WITHER_INGREDIENT_META), 's', Items.bone, 't', witherSkull());
        addRecipe(false, heartPearl(Reference.CREEPER_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.CREEPER_INGREDIENT_META), 's', Items.gunpowder, 't', Items.bone);
        addRecipe(false, heartPearl(Reference.GHAST_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.CREEPER_INGREDIENT_META), 's', Items.ghast_tear, 't', ingredient(Reference.MOLTEN_INGREDIENT_META));
        addRecipe(false, heartPearl(Reference.SPIDER_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.SPIDER_INGREDIENT_META), 's', Items.string, 't', Items.spider_eye);
        addRecipe(false, heartPearl(Reference.CAVE_SPIDER_ZHU_META), "ppp", "sts", "ppp", 'p', ingredient(Reference.SPIDER_INGREDIENT_META), 's', Items.spider_eye, 't', Items.iron_ingot);
        addRecipe(false, heartPearl(Reference.MAGMA_CUBE_ZHU_META), "ppp", "sts", "ppp", 'p', Items.magma_cream, 's', Items.blaze_rod, 't', ingredient(Reference.MOLTEN_INGREDIENT_META));
        addRecipe(false, heartPearl(Reference.BLAZE_ZHU_META), "ppp", "sts", "ppp", 'p', Items.blaze_rod, 's', ingredient(Reference.MOLTEN_INGREDIENT_META), 't', Items.magma_cream);
        addRecipe(false, heartPearl(Reference.ENDERMAN_ZHU_META), "ppp", "sts", "ppp", 'p', Items.ender_eye, 's', ingredient(Reference.ENDER_INGREDIENT_META), 't', Items.ender_pearl);

        // nian zhu actual items
        addRecipe(true, heartZhu(Reference.ZOMBIE_ZHU_META), Items.string, heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META));
        addRecipe(true, heartZhu(Reference.PIG_ZOMBIE_ZHU_META), Items.string, heartPearl(Reference.PIG_ZOMBIE_ZHU_META), heartPearl(Reference.PIG_ZOMBIE_ZHU_META), heartPearl(Reference.PIG_ZOMBIE_ZHU_META), heartPearl(Reference.PIG_ZOMBIE_ZHU_META), heartPearl(Reference.PIG_ZOMBIE_ZHU_META), heartPearl(Reference.PIG_ZOMBIE_ZHU_META));
        addRecipe(true, heartZhu(Reference.SKELETON_ZHU_META), Items.string, heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META));
        addRecipe(true, heartZhu(Reference.WITHER_SKELETON_ZHU_META), Items.string, heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META));
        addRecipe(true, heartZhu(Reference.CREEPER_ZHU_META), Items.string, heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META));
        addRecipe(true, heartZhu(Reference.GHAST_ZHU_META), Items.string, heartPearl(Reference.GHAST_ZHU_META), heartPearl(Reference.GHAST_ZHU_META), heartPearl(Reference.GHAST_ZHU_META), heartPearl(Reference.GHAST_ZHU_META), heartPearl(Reference.GHAST_ZHU_META), heartPearl(Reference.GHAST_ZHU_META));
        addRecipe(true, heartZhu(Reference.SPIDER_ZHU_META), Items.string, heartPearl(Reference.SPIDER_ZHU_META), heartPearl(Reference.SPIDER_ZHU_META), heartPearl(Reference.SPIDER_ZHU_META), heartPearl(Reference.SPIDER_ZHU_META), heartPearl(Reference.SPIDER_ZHU_META), heartPearl(Reference.SPIDER_ZHU_META));
        addRecipe(true, heartZhu(Reference.CAVE_SPIDER_ZHU_META), Items.string, heartPearl(Reference.CAVE_SPIDER_ZHU_META), heartPearl(Reference.CAVE_SPIDER_ZHU_META), heartPearl(Reference.CAVE_SPIDER_ZHU_META), heartPearl(Reference.CAVE_SPIDER_ZHU_META), heartPearl(Reference.CAVE_SPIDER_ZHU_META), heartPearl(Reference.CAVE_SPIDER_ZHU_META));
        addRecipe(true, heartZhu(Reference.MAGMA_CUBE_ZHU_META), Items.string, heartPearl(Reference.MAGMA_CUBE_ZHU_META), heartPearl(Reference.MAGMA_CUBE_ZHU_META), heartPearl(Reference.MAGMA_CUBE_ZHU_META), heartPearl(Reference.MAGMA_CUBE_ZHU_META), heartPearl(Reference.MAGMA_CUBE_ZHU_META), heartPearl(Reference.MAGMA_CUBE_ZHU_META));
        addRecipe(true, heartZhu(Reference.BLAZE_ZHU_META), Items.string, heartPearl(Reference.BLAZE_ZHU_META), heartPearl(Reference.BLAZE_ZHU_META), heartPearl(Reference.BLAZE_ZHU_META), heartPearl(Reference.BLAZE_ZHU_META), heartPearl(Reference.BLAZE_ZHU_META), heartPearl(Reference.BLAZE_ZHU_META));
        addRecipe(true, heartZhu(Reference.ENDERMAN_ZHU_META), Items.string, heartPearl(Reference.ENDERMAN_ZHU_META), heartPearl(Reference.ENDERMAN_ZHU_META), heartPearl(Reference.ENDERMAN_ZHU_META), heartPearl(Reference.ENDERMAN_ZHU_META), heartPearl(Reference.ENDERMAN_ZHU_META), heartPearl(Reference.ENDERMAN_ZHU_META));

		/* potions and splash potions */

        // empty vial
        addRecipe(false, potion(5, Reference.EMPTY_VIAL_META), "g g", "g g", " g ", 'g', Blocks.glass_pane);

        // base solvent
        addRecipe(true, potion(Reference.POTION_META), Items.nether_wart, potion(Reference.WATER_META));

        // base splash solvent
        addRecipe(true, potion(Reference.SPLASH_META), Items.nether_wart, Items.gunpowder, potion(Reference.WATER_META));
        addRecipe(true, potion(Reference.SPLASH_META), potion(Reference.POTION_META), Items.gunpowder);

        // glowing water
        addRecipe(true, new ItemStack(getItem(Names.glowing_water), 1), potion(Reference.SPLASH_META), Items.glowstone_dust, Items.glowstone_dust, Items.glowstone_dust);

        // angelheart vial
        addRecipe(true, new ItemStack(getItem(Names.angelheart_vial), 2), potion(Reference.PANACEA_META), getItem(Names.glowing_water));

        // speed potion
        addRecipe(true, potion(Reference.SPEED_META), potion(Reference.POTION_META), Items.sugar, Items.redstone, Items.glowstone_dust);

        // dig potion
        addRecipe(true, potion(Reference.DIGGING_META), potion(Reference.POTION_META), Items.bone, Items.redstone, Items.glowstone_dust);

        // strength potion
        addRecipe(true, potion(Reference.STRENGTH_META), potion(Reference.POTION_META), Items.blaze_powder, Items.redstone, Items.glowstone_dust);

        // heal potion
        addRecipe(true, potion(Reference.HEALING_META), potion(Reference.POTION_META), Items.speckled_melon, Items.glowstone_dust);

        // jump potion
        addRecipe(true, potion(Reference.BOUNDING_META), potion(Reference.POTION_META), Items.feather, Items.redstone, Items.glowstone_dust);

        // regen potion
        addRecipe(true, potion(Reference.REGENERATION_META), potion(Reference.POTION_META), Items.ghast_tear, Items.redstone, Items.glowstone_dust);

        // resist potion
        addRecipe(true, potion(Reference.RESISTANCE_META), potion(Reference.POTION_META), Items.leather, Items.redstone, Items.glowstone_dust);

        // fire resist potion
        addRecipe(true, potion(Reference.FIRE_WARDING_META), potion(Reference.POTION_META), Items.magma_cream, Items.redstone);

        // breathing potion
        addRecipe(true, potion(Reference.BREATHING_META), potion(Reference.POTION_META), new ItemStack(Items.dye, 1, 0), Items.redstone);

        // invis potion
        addRecipe(true, potion(Reference.INVISIBILITY_META), potion(Reference.INFRAVISION_META), Items.fermented_spider_eye, Items.redstone);

        // vision potion
        addRecipe(true, potion(Reference.INFRAVISION_META), potion(Reference.POTION_META), Items.golden_carrot, Items.redstone);

        // protection potion
        addRecipe(true, potion(8, Reference.PROTECTION_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), Items.glowstone_dust, potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META));

        // potency potion
        addRecipe(true, potion(8, Reference.POTENCE_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), Items.glowstone_dust, potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META));

        // celerity potion
        addRecipe(true, potion(8, Reference.CELERITY_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), Items.glowstone_dust, potion(Reference.SPEED_META), potion(Reference.SPEED_META), potion(Reference.SPEED_META), potion(Reference.SPEED_META));

        // stalker potion
        addRecipe(true, potion(8, Reference.STALKER_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), Items.glowstone_dust, potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META));

        // panacea potion
        addRecipe(true, potion(8, Reference.PANACEA_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), Items.milk_bucket, potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META));

        // aphrodite
        addRecipe(true, potion(Reference.APHRODITE_META), potion(Reference.SPLASH_META), new ItemStack(Items.dye, 1, 3), new ItemStack(Items.dye, 1, 1), Items.redstone);

        // poison
        addRecipe(true, potion(Reference.POISON_META), potion(Reference.SPLASH_META), Items.spider_eye, Items.fermented_spider_eye, Items.redstone);

        // harm
        addRecipe(true, potion(Reference.ACID_META), potion(Reference.SPLASH_META), Items.speckled_melon, Items.fermented_spider_eye, Items.glowstone_dust);

        // confusion
        addRecipe(true, potion(Reference.CONFUSION_META), potion(Reference.SPLASH_META), Items.golden_carrot, Items.fermented_spider_eye, Items.redstone);

        // slowness
        addRecipe(true, potion(Reference.SLOWING_META), potion(Reference.SPLASH_META), Items.sugar, Items.fermented_spider_eye, Items.glowstone_dust);

        // weakness
        addRecipe(true, potion(Reference.WEAKNESS_META), potion(Reference.SPLASH_META), Items.blaze_powder, Items.fermented_spider_eye, Items.glowstone_dust);

        // wither
        addRecipe(true, potion(Reference.WITHER_META), potion(Reference.SPLASH_META), new ItemStack(Items.skull, 1, 1), Items.glowstone_dust, Items.glowstone_dust);

        // blindness
        addRecipe(true, potion(Reference.BLINDING_META), potion(Reference.SPLASH_META), Items.golden_carrot, Items.fermented_spider_eye, Items.golden_carrot);

        // ruin
        addRecipe(true, potion(3, Reference.RUINATION_META), potion(Reference.SLOWING_META), potion(Reference.WEAKNESS_META), potion(Reference.POISON_META), Items.glowstone_dust);

        // fertility
        addRecipe(true, potion(Reference.FERTILIZER_META), potion(Reference.SPLASH_META), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15));

    }

}
