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
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.RecipeSorter;
import xreliquary.Reliquary;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryDrainRecipe;
import xreliquary.items.alkahestry.AlkahestryRedstoneRecipe;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class XRRecipes {

    //convenience itemstack methods, these are just to make it a little easier to read.
    public static Item getItem(String name) { return ContentHandler.getItem(name); }

    //public static ItemStack fertilizer() { return XRRecipes.potion(Reference.FERTILIZER_META); }

    public static ItemStack emptyVoidTear() { return new ItemStack(getItem(Names.void_tear_empty), 1, 0); }

    public static ItemStack witherSkull() { return new ItemStack(Items.skull, 1, 1); }

    public static ItemStack roseBush() { return new ItemStack(Blocks.double_plant, 1, 4); }

    public static ItemStack blackWool() { return new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META); }

    public static ItemStack lapis() { return new ItemStack(Items.dye, 1, 4); }

    public static ItemStack gunPart(int i, int m) { return new ItemStack(getItem(Names.gun_part), i, m); }

    public static ItemStack magazine(int m) { return magazine(1, m); }

    public static ItemStack magazine(int i, int m) { return new ItemStack(getItem(Names.magazine), i, m); }

    public static ItemStack bullet(int m) { return bullet(1, m); }

    public static ItemStack bullet(int i, int m) { return new ItemStack(getItem(Names.bullet), i, m); }

    //public static ItemStack potion(int m) { return potion(1, m); }

    //public static ItemStack potion(int i, int m) { return new ItemStack(getItem(Names.condensed_potion), i, m); }

    public static ItemStack ingredient(int m) { return new ItemStack(getItem(Names.mob_ingredient), 1, m); }

    //more convenience methods here, these shore up the names of the ingredient stuff, because they're really long.

    public static ItemStack enderHeart() { return ingredient(Reference.ENDER_INGREDIENT_META); }
    public static ItemStack creeperGland() { return ingredient(Reference.CREEPER_INGREDIENT_META); }
    public static ItemStack slimePearl() { return ingredient(Reference.SLIME_INGREDIENT_META); }
    public static ItemStack batWing() { return ingredient(Reference.BAT_INGREDIENT_META); }
    public static ItemStack ribBone() { return ingredient(Reference.SKELETON_INGREDIENT_META); }
    public static ItemStack witherRib() { return ingredient(Reference.WITHER_INGREDIENT_META); }
    public static ItemStack stormEye() { return ingredient(Reference.STORM_INGREDIENT_META); }
    public static ItemStack fertileEssence() { return ingredient(Reference.FERTILE_INGREDIENT_META); }
    public static ItemStack frozenCore() { return ingredient(Reference.FROZEN_INGREDIENT_META); }
    public static ItemStack moltenCore() { return ingredient(Reference.MOLTEN_INGREDIENT_META); }
    public static ItemStack zombieHeart() { return ingredient(Reference.ZOMBIE_INGREDIENT_META); }
    public static ItemStack infernalClaw() { return ingredient(Reference.CLAW_INGREDIENT_META); }
    public static ItemStack shellFragment() { return ingredient(Reference.SHELL_INGREDIENT_META); }
    public static ItemStack squidBeak() { return ingredient(Reference.SQUID_INGREDIENT_META); }
    public static ItemStack spiderFangs() { return ingredient(Reference.SPIDER_INGREDIENT_META); }
    
    public static ItemStack heartPearl(int m) { return new ItemStack(getItem(Names.heart_pearl), 1, m); }

    public static ItemStack nianZhu(int m) { return new ItemStack(getItem(Names.heart_zhu), 1, m); }


    //this version of the addRecipe method checks first to see if the recipe is disabled in our automated recipe-disabler config
    //if any component of the item is in the recipe disabler list, it will ALSO block the recipe automatically.
    //override disabler forces the recipe to evaluate anyway. This occurs for items that don't fall into XR scope, and thus shouldn't be evaluated.
    public static void addRecipe(boolean isShapeless, boolean overrideDisabler, ItemStack result, Object... params) {
        if (!overrideDisabler) {
            boolean enabled = Reliquary.CONFIG.getGroup(Names.recipe_enabled).containsKey(ContentHelper.getIdent(result.getItem()).replace(':', '_')) && Reliquary.CONFIG.getBool(Names.recipe_enabled, ContentHelper.getIdent(result.getItem()).replace(':', '_'));
            if (!enabled) return;
            for (Object obj : params) {
                String unlocalizedName = null;
                if (obj instanceof Block) {
                    unlocalizedName = ContentHelper.getIdent((Block) obj);
                } else if (obj instanceof Item) {
                    unlocalizedName = ContentHelper.getIdent((Item) obj);
                } else if (obj instanceof ItemStack) {
                    unlocalizedName = ContentHelper.getIdent(((ItemStack) obj).getItem());
                }
                if (unlocalizedName == null || !Reliquary.CONFIG.getKeys(Names.recipe_enabled).contains(unlocalizedName.replace(Reference.MOD_ID + "_", Reference.MOD_ID + ":")))
                    continue;
                enabled = enabled && Reliquary.CONFIG.getBool(Names.recipe_enabled, unlocalizedName);
            }
            if (!enabled) return;
        }
        if (!isShapeless) GameRegistry.addRecipe(result, params);
            else GameRegistry.addShapelessRecipe(result, params);
    }

    private static boolean isEasyMode(String s) {
        return Reliquary.CONFIG.getBool(Names.easy_mode_recipes, s);
    }

    public static void init() {
        // tome and alkahestry recipes
        GameRegistry.addRecipe(new AlkahestryDrainRecipe());
        GameRegistry.addRecipe(new AlkahestryRedstoneRecipe());
        GameRegistry.addRecipe(new AlkahestryCraftingRecipe());

        RecipeSorter.register(Reference.MOD_ID + ":alkahest_crafting", AlkahestryCraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
        RecipeSorter.register(Reference.MOD_ID + ":alkahest_redstone", AlkahestryRedstoneRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_crafting");
        RecipeSorter.register(Reference.MOD_ID + ":alkahest_drain", AlkahestryDrainRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_redstone");

        //misc recipes
        //frozen cores to make packed ice.
        addRecipe(true, true, new ItemStack(Blocks.packed_ice, 1, 0), Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, frozenCore());

        //apothecary mortar recipe
        addRecipe(false, false, new ItemStack(ContentHandler.getBlock(Names.apothecary_mortar), 1, 0), "gng", "ngn", "nnn", 'n', Blocks.quartz_block, 'g', creeperGland());

        //apothecary cauldron recipe
        addRecipe(false, false, new ItemStack(ContentHandler.getBlock(Names.apothecary_cauldron), 1, 0), "gng", "ici", "nmn", 'g', creeperGland(), 'n', enderHeart(), 'i', infernalClaw(), 'c', Blocks.cauldron, 'm', moltenCore());

        //alkahestry tome
        if (isEasyMode(Names.alkahestry_tome))
            addRecipe(true, false, new ItemStack(getItem(Names.alkahestry_tome), 1, 0), Items.book, getItem(Names.witch_hat), moltenCore(), witherSkull());
        else
            addRecipe(true, false, new ItemStack(getItem(Names.alkahestry_tome), 1, 0), moltenCore(), getItem(Names.witch_hat), stormEye(), creeperGland(), Items.book, slimePearl(), spiderFangs(), witherSkull(), enderHeart());

        //glowstone altar
        if (isEasyMode(Names.altar))
            addRecipe(true, false, new ItemStack(ContentHandler.getBlock(Names.altar_idle), 1), Blocks.obsidian, Blocks.redstone_lamp, enderHeart(), creeperGland());
        else
            addRecipe(false, false, new ItemStack(ContentHandler.getBlock(Names.altar_idle), 1), "dnd", "olo", "dgd", 'd', Items.glowstone_dust, 'n', enderHeart(), 'o', Blocks.obsidian, 'l', Blocks.redstone_lamp, 'g', creeperGland());

        //lilypad
        addRecipe(true, false, new ItemStack(ContentHandler.getBlock(Names.lilypad), 1), fertileEssence(), fertileEssence(), fertileEssence(), Blocks.waterlily);

        //wraith node
        addRecipe(true, false, new ItemStack(ContentHandler.getBlock(Names.wraith_node), 1), enderHeart(), Items.emerald);

        //interdiction torch
        if (isEasyMode(Names.interdiction_torch))
            addRecipe(false, false, new ItemStack(getItem(Names.interdiction_torch), 4, 0), "bm", "nr", 'b', batWing(), 'm', moltenCore(), 'n', enderHeart(), 'r', Items.blaze_rod);
        else
            addRecipe(false, false, new ItemStack(getItem(Names.interdiction_torch), 4, 0), " n ", "mdm", "bwb", 'n', enderHeart(), 'm', moltenCore(), 'd', Items.diamond, 'b', Items.blaze_rod, 'w', batWing());

        // glowy bread
        addRecipe(true, false, new ItemStack(getItem(Names.glowing_bread), 3), Items.bread, Items.bread, Items.bread, getItem(Names.glowing_water));

        //fertile essence
        if (isEasyMode(Names.fertile_essence))
            addRecipe(true, false, fertileEssence(), ribBone(), ribBone(), ribBone(), Items.wheat_seeds);
        else
            addRecipe(true, false, fertileEssence(), ribBone(), ribBone(), ribBone(), ribBone(), ribBone(), ribBone(), ribBone(), ribBone(), Items.wheat_seeds);

        // bullets...
        // empty cases back into nuggets
        addRecipe(true, true, new ItemStack(Items.gold_nugget, 1), bullet(1, 0), bullet(1, 0), bullet(1, 0), bullet(1, 0));
        // neutral
        addRecipe(true, false, bullet(8, 1), Items.flint, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // exorcist
        addRecipe(true, false, bullet(8, 2), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), zombieHeart());
        // blaze
        addRecipe(true, false, bullet(8, 3), Items.blaze_powder, Items.blaze_rod, Items.gold_nugget, Items.gold_nugget);
        // ender
        addRecipe(true, false, bullet(8, 4),  bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), enderHeart());
        // concussive
        addRecipe(true, false, bullet(8, 5), Items.slime_ball, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // buster
        addRecipe(true, false, bullet(8, 6), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), creeperGland());
        // seeker, the only thing with an easy mode recipe
        if (isEasyMode(Names.seeker_shot))
            addRecipe(true, false, bullet(8, 7), lapis(), Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        else
            addRecipe(true, false, bullet(4, 7), lapis(), slimePearl(), Items.gold_nugget, Items.gunpowder);
        // sand
        addRecipe(true, false, bullet(8, 8), Blocks.sandstone, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // storm
        addRecipe(true, false, bullet(8, 9), stormEye(), Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // frozen shot TODO
        // venom shot TODO
        // fertile shot TODO
        // rage shot TODO
        // traitor shot TODO
        // calm shot TODO
        // molten shot TODO

        // magazines...
        addRecipe(false, false, magazine(5, 0), "i i", "igi", "sis", 's', Blocks.stone, 'i', Items.iron_ingot, 'g', Blocks.glass);

        // neutral
        addRecipe(true, false, magazine(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), magazine(1, 0));
        // exorcist
        addRecipe(true, false, magazine(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), magazine(1, 0));
        // blaze
        addRecipe(true, false, magazine(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), magazine(1, 0));
        // ender
        addRecipe(true, false, magazine(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), magazine(1, 0));
        // venom
        addRecipe(true, false, magazine(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), magazine(1, 0));
        // buster
        addRecipe(true, false, magazine(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), magazine(1, 0));
        // seeker
        addRecipe(true, false, magazine(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), magazine(1, 0));
        // sand
        addRecipe(true, false, magazine(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), magazine(1, 0));
        // storm
        addRecipe(true, false, magazine(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), magazine(1, 0));

        // gunpart 0 = grip, 1 = barrel, 2 = mechanism
        addRecipe(false, false, gunPart(1, 0), "iii", "imi", "ici", 'i', Items.iron_ingot, 'c', magazine(1, 0), 'm', Items.magma_cream);
        addRecipe(false, false, gunPart(1, 1), "iii", "eme", "iii", 'i', Items.iron_ingot, 'e', enderHeart(), 'm', Items.magma_cream);
        addRecipe(false, false, gunPart(1, 2), "iib", "rmi", "iii", 'i', Items.iron_ingot, 'b', Blocks.stone_button, 'r', Items.blaze_rod, 'm', moltenCore());

        // handgun
        addRecipe(false, false, new ItemStack(getItem(Names.handgun), 1, 0), "bim", "isi", "igi", 'i', Items.iron_ingot, 'b', gunPart(1, 1), 'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', slimePearl());

		// fortune coin
        if (isEasyMode(Names.fortune_coin))
            addRecipe(true, false, new ItemStack(getItem(Names.fortune_coin), 1), enderHeart(), Items.gold_nugget, slimePearl(), batWing());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.fortune_coin), 1), "ege", "gng", "ege", 'e', Items.ender_eye, 'g', Items.gold_ingot, 'n', enderHeart());

        // cross of mercy
        addRecipe(false, false, new ItemStack(getItem(Names.mercy_cross), 1), "wgr", "glg", "sgz", 'w', witherRib(), 'g', Items.gold_ingot, 'r', ribBone(), 'l', Items.leather, 's', new ItemStack(Items.skull, 1, 1), 'z', zombieHeart());

        // holy hand grenade
        addRecipe(true, false, new ItemStack(getItem(Names.holy_hand_grenade), 4), getItem(Names.glowing_water), Items.gold_nugget, Blocks.tnt, creeperGland());

        // sojourner's staff
        if (isEasyMode(Names.sojourner_staff))
            addRecipe(true, false, new ItemStack(getItem(Names.sojourner_staff), 1), moltenCore(), Items.gold_ingot, Items.blaze_rod, emptyVoidTear());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.sojourner_staff), 1), "gcm", "itc", "big", 'g', Items.gold_nugget, 'c', Items.magma_cream, 'm', moltenCore(), 'i', Items.gold_ingot, 't', getItem(Names.infernal_tear), 'b', Items.blaze_rod);

        // lantern of paranoia
        if (isEasyMode(Names.lantern_of_paranoia))
            addRecipe(false, false, new ItemStack(getItem(Names.lantern_of_paranoia), 1), "isi", "gmg", " i ", 'i', Items.iron_ingot, 's', slimePearl(),  'g', Blocks.glass, 'm', moltenCore());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.lantern_of_paranoia), 1), "imi", "gtg", "ili", 'i', Items.iron_ingot, 'm', moltenCore(), 'g', Blocks.glass, 't', ContentHandler.getBlock(Names.interdiction_torch), 'l', creeperGland());

        // midas touchstone
        addRecipe(true, false, new ItemStack(getItem(Names.midas_touchstone), 1, 0), Blocks.anvil, Blocks.gold_block, Blocks.gold_block, moltenCore(), moltenCore(), moltenCore(), creeperGland(), creeperGland(), emptyVoidTear());

        // emperor's chalice
        if (isEasyMode(Names.emperor_chalice))
            addRecipe(true, false, new ItemStack(getItem(Names.emperor_chalice), 1, 0), Items.emerald, Items.gold_ingot, Items.bucket, emptyVoidTear());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.emperor_chalice), 1, 0), "ses", "ivi", "lbl", 's', slimePearl(), 'e', Items.emerald, 'i', Items.gold_ingot, 'v', emptyVoidTear(),  'l', lapis(), 'b', Items.bucket);

        // infernal chalice
        if (isEasyMode(Names.infernal_chalice))
            addRecipe(true, false, new ItemStack(getItem(Names.infernal_chalice), 1), getItem(Names.infernal_claws), getItem(Names.emperor_chalice), getItem(Names.infernal_tear), moltenCore());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.infernal_chalice), 1), "imi", "wcw", "mtm", 'i', getItem(Names.infernal_claws), 'm', moltenCore(), 'w', witherRib(), 'c', getItem(Names.emperor_chalice), 't', getItem(Names.infernal_tear));

        // salamander's eye
        if (isEasyMode(Names.salamander_eye))
            addRecipe(true, false, new ItemStack(getItem(Names.salamander_eye), 1), Items.ender_eye, moltenCore(), frozenCore(), enderHeart());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.salamander_eye), 1), "fnm", "geg", "mnf", 'f', frozenCore(), 'n', enderHeart(), 'm', moltenCore(), 'g', Items.ghast_tear, 'e', Items.ender_eye);

        // ice rod
        if (isEasyMode(Names.ice_magus_rod))
            addRecipe(false, false, new ItemStack(getItem(Names.ice_magus_rod), 1, 0), " df", " vd", "i  ", 'd', Items.diamond, 'f', frozenCore(), 'i', Items.iron_ingot, 'v', emptyVoidTear());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.ice_magus_rod), 1, 0), "fdf", "ptd", "ipf", 'f', frozenCore(), 'd', Items.diamond, 'p', Blocks.packed_ice, 't', emptyVoidTear(), 'i', Items.iron_ingot);

        //glacial staff
        if (isEasyMode(Names.glacial_staff))
            addRecipe(true, false, new ItemStack(getItem(Names.glacial_staff), 1, 0), getItem(Names.ice_magus_rod), emptyVoidTear(), frozenCore(), getItem(Names.shears_of_winter));
        else
            addRecipe(false, false, new ItemStack(getItem(Names.glacial_staff), 1, 0), "fds", "fvd", "iff", 'f', frozenCore(), 'd', Items.diamond, 's', getItem(Names.shears_of_winter), 'v', emptyVoidTear(), 'i', getItem(Names.ice_magus_rod));

        // ender staff
        if (isEasyMode(Names.ender_staff))
            addRecipe(false, false, new ItemStack(getItem(Names.ender_staff), 1, 0), " be", "nvb", "sn ", 'v', getItem(Names.void_tear), 'e', Items.ender_eye, 's', Items.stick, 'n', enderHeart(), 'b', batWing());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.ender_staff), 1, 0), "nbe", "nvb", "rnn", 'n', enderHeart(), 'b', batWing(), 'e', Items.ender_eye, 'v', emptyVoidTear(), 'r', Items.blaze_rod);

        // rending gale
        if (isEasyMode(Names.rending_gale))
            addRecipe(false, false, new ItemStack(getItem(Names.rending_gale), 1, 0), " be", "gvb", "sg ", 'b', batWing(), 'e', stormEye(), 'g', Items.gold_ingot, 'v', emptyVoidTear(), 's', Items.stick);
        else
            addRecipe(false, false, new ItemStack(getItem(Names.rending_gale), 1, 0), "ebe", "fvb", "rfe", 'e', stormEye(), 'b', batWing(), 'f', getItem(Names.angelic_feather), 'v', emptyVoidTear(), 'r', Items.blaze_rod);

        // harvest rod
        addRecipe(false, false, new ItemStack(getItem(Names.harvest_rod), 1, 0), " rf", "vtr", "sv ", 'r', roseBush(), 'f', fertileEssence(), 'v', Blocks.vine, 't', emptyVoidTear(), 's', Items.stick );

        // pyromancer staff
        if (isEasyMode(Names.pyromancer_staff))
            addRecipe(true, false, new ItemStack(getItem(Names.pyromancer_staff), 1, 0), getItem(Names.infernal_claws), Items.blaze_rod, getItem(Names.infernal_tear), getItem(Names.salamander_eye));
        else
            addRecipe(false, false, new ItemStack(getItem(Names.pyromancer_staff), 1, 0), "mcs", "mic", "rmm", 'm', moltenCore(), 'c', getItem(Names.infernal_claws), 's', getItem(Names.salamander_eye), 'i', getItem(Names.infernal_tear), 'r', Items.blaze_rod);

        // serpent staff
        if (isEasyMode(Names.serpent_staff))
            addRecipe(false, false, new ItemStack(getItem(Names.serpent_staff), 1), " ce", " kc", "s  ", 'c', spiderFangs(), 'e', Items.ender_eye, 'k', shellFragment(), 'b', Items.stick);
        else
            addRecipe(false, false, new ItemStack(getItem(Names.serpent_staff), 1), "coe", "pko", "bpc", 'c', spiderFangs(), 'o', Blocks.obsidian, 'e', Items.ender_eye, 'p', slimePearl(), 'k', shellFragment(), 'b', Items.blaze_rod);

        // rod of lyssa
        if (isEasyMode(Names.rod_of_lyssa))
            addRecipe(true, false, new ItemStack(getItem(Names.rod_of_lyssa), 1, 0), infernalClaw(), batWing(), enderHeart(), Items.fishing_rod);
        else
            addRecipe(false, false, new ItemStack(getItem(Names.rod_of_lyssa), 1, 0), " br", "nms", "r i", 'b', batWing(), 'r', Items.blaze_rod, 'n', enderHeart(), 'm', moltenCore(), 's', Items.string, 'i', infernalClaw());

        // shears of winter
        addRecipe(true, false, new ItemStack(getItem(Names.shears_of_winter), 1, 0), frozenCore(), Items.shears, Items.diamond, Items.diamond);

        // magicbane
        if (isEasyMode(Names.magicbane))
            addRecipe(false, false, new ItemStack(getItem(Names.magicbane), 1, 0), "ng", "in", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'n', enderHeart());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.magicbane), 1, 0), "een", "nge", "ine", 'e', Items.ender_eye, 'n', enderHeart(), 'g', Items.gold_ingot, 'i', Items.iron_ingot);

        // witherless rose
        addRecipe(false, false, new ItemStack(getItem(Names.witherless_rose), 1), "fnf", "nrn", "fnf", 'f', fertileEssence(), 'n', Items.nether_star, 'r', roseBush() );

        // crimson cloth
        addRecipe(true, false, ingredient(Reference.CLOTH_INGREDIENT_META), new ItemStack(Blocks.wool, 1, Reference.RED_WOOL_META), new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META), enderHeart(), enderHeart());

        // cloak
        addRecipe(false, false, new ItemStack(getItem(Names.twilight_cloak), 1), "ici", "bcb", "bcb", 'i', Items.iron_ingot, 'b', blackWool(), 'c', ingredient(Reference.CLOTH_INGREDIENT_META));

        // void tear
        if (isEasyMode(Names.void_tear))
            addRecipe(true, false, emptyVoidTear(), Items.ghast_tear, enderHeart(), slimePearl(), lapis());
        else
            addRecipe(false, false, emptyVoidTear(), "lel", "pgp", "lnl", 'l', lapis(), 'e', Items.ender_pearl, 'p', slimePearl(), 'g', Items.ghast_tear, 'n', enderHeart());

        // infernal tear
        if (isEasyMode(Names.infernal_tear))
            addRecipe(true, false, new ItemStack(getItem(Names.infernal_tear), 1, 0), emptyVoidTear(), getItem(Names.witch_hat), moltenCore(), infernalClaw());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.infernal_tear), 1, 0), "php", "mtm", "pcp", 'p', Items.blaze_powder, 'h', getItem(Names.witch_hat), 'm', moltenCore(), 't', emptyVoidTear(), 'c', infernalClaw());
        
        // angelic feather
        if (isEasyMode(Names.angelic_feather))
            addRecipe(true, false, new ItemStack(getItem(Names.angelic_feather), 1), Items.feather, enderHeart(), batWing(), fertileEssence());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.angelic_feather), 1), "dgd", "bfb", "ene", 'd', Items.glowstone_dust, 'g', Items.gold_ingot, 'b', batWing(), 'e', fertileEssence(), 'n', enderHeart(), 'f', Items.feather);

        // phoenix down
        addRecipe(true, false, new ItemStack(getItem(Names.phoenix_down), 1), getItem(Names.angelheart_vial), getItem(Names.angelheart_vial), getItem(Names.angelheart_vial), getItem(Names.angelic_feather));

        // infernal claw
        addRecipe(true, false, infernalClaw(), Items.leather, moltenCore(), ribBone(), slimePearl());

        // infernal claws
        if (isEasyMode(Names.infernal_claws))
            addRecipe(true, false, new ItemStack(getItem(Names.infernal_claws), 1), infernalClaw(), infernalClaw(), infernalClaw(), slimePearl());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.infernal_claws), 1), "ccc", "cpc", "mlm", 'c', infernalClaw(), 'p', slimePearl(), 'm', moltenCore(), 'l', Items.leather);

        // squid beak, wither rib & rib bone to bonemeal
        addRecipe(true, true, new ItemStack(Items.dye, 2, Reference.WHITE_DYE_META), squidBeak());
        addRecipe(true, true, new ItemStack(Items.dye, 6, Reference.WHITE_DYE_META), ribBone());
        addRecipe(true, true, new ItemStack(Items.dye, 8, Reference.WHITE_DYE_META), witherRib());

        // kraken shell fragment
        addRecipe(true, false, shellFragment(), squidBeak(), squidBeak(), squidBeak(), slimePearl());

        // kraken shell
        if (isEasyMode(Names.kraken_shell))
            addRecipe(true, false, new ItemStack(getItem(Names.kraken_shell), 1, 0), shellFragment(), shellFragment(), shellFragment(), enderHeart());
        else
            addRecipe(false, false, new ItemStack(getItem(Names.kraken_shell), 1, 0), "nfn", "epe", "fnf", 'n', enderHeart(), 'f', shellFragment(), 'e', Items.ender_eye, 'p', slimePearl());

        // hero medallion - any meta fortune coin for people with "grandfathered" fortune coins (pre-Enabled NBT used item damage)
        if (isEasyMode(Names.hero_medallion))
            addRecipe(true, false, new ItemStack(getItem(Names.hero_medallion), 1), enderHeart(), new ItemStack(getItem(Names.fortune_coin), 1, -1), getItem(Names.witch_hat), getItem(Names.infernal_tear));
        else
            addRecipe(false, false, new ItemStack(getItem(Names.hero_medallion), 1), "nie", "iti", "fin", 'n', enderHeart(), 'i', Items.iron_ingot, 'e', Items.ender_eye, 't', getItem(Names.infernal_tear), 'f', new ItemStack(getItem(Names.fortune_coin), 1, -1));

        // destruction catalyst
        if (isEasyMode(Names.destruction_catalyst))
            addRecipe(true, false, new ItemStack(getItem(Names.destruction_catalyst), 1, 0), Items.flint_and_steel, moltenCore(), creeperGland(), getItem(Names.infernal_tear));
        else
            addRecipe(false, false, new ItemStack(getItem(Names.destruction_catalyst), 1, 0), "tmc", "gim", "fgt", 't', Blocks.tnt, 'm', moltenCore(), 'c', creeperGland(), 'g', Items.gold_ingot, 'i', getItem(Names.infernal_tear), 'f', Items.flint_and_steel);

        // nian zhu heart pearls
        addRecipe(false, false, heartPearl(Reference.ZOMBIE_ZHU_META), "ppp", "sts", "ppp", 'p', zombieHeart(), 's', Items.rotten_flesh, 't', Items.bone);
        addRecipe(false, false, heartPearl(Reference.SKELETON_ZHU_META), "ppp", "sts", "ppp", 'p', ribBone(), 's', Items.bone, 't', Items.flint);
        addRecipe(false, false, heartPearl(Reference.WITHER_SKELETON_ZHU_META), "ppp", "sts", "ppp", 'p', witherRib(), 's', Items.bone, 't', witherSkull());
        addRecipe(false, false, heartPearl(Reference.CREEPER_ZHU_META), "ppp", "sts", "ppp", 'p', creeperGland(), 's', Items.gunpowder, 't', Items.bone);

        // nian zhu actual items
        addRecipe(true, false, nianZhu(Reference.ZOMBIE_ZHU_META), Items.string, heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META));
        addRecipe(true, false, nianZhu(Reference.SKELETON_ZHU_META), Items.string, heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META));
        addRecipe(true, false, nianZhu(Reference.WITHER_SKELETON_ZHU_META), Items.string, heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META));
        addRecipe(true, false, nianZhu(Reference.CREEPER_ZHU_META), Items.string, heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META));

		/* potions and splash potions */
//
//        // empty vial
//        addRecipe(false, false, potion(5, Reference.EMPTY_VIAL_META), "g g", "g g", " g ", 'g', Blocks.glass_pane);
//
//        // base solvent
//        addRecipe(true, false, potion(Reference.POTION_META), Items.nether_wart, potion(Reference.WATER_META));
//
//        // base splash solvent
//        addRecipe(true, false, potion(Reference.SPLASH_META), Items.nether_wart, Items.gunpowder, potion(Reference.WATER_META));
//        addRecipe(true, false, potion(Reference.SPLASH_META), potion(Reference.POTION_META), Items.gunpowder);
//
//        // glowing water
//        addRecipe(true, false, new ItemStack(getItem(Names.glowing_water), 1), potion(Reference.SPLASH_META), Items.glowstone_dust, Items.glowstone_dust, Items.glowstone_dust);
//
//        // angelheart vial
//        addRecipe(true, false, new ItemStack(getItem(Names.angelheart_vial), 2), potion(Reference.PANACEA_META), getItem(Names.glowing_water));
//
//        // speed potion
//        addRecipe(true, false, potion(Reference.SPEED_META), potion(Reference.POTION_META), Items.sugar, Items.redstone, Items.glowstone_dust);
//
//        // dig potion
//        addRecipe(true, false, potion(Reference.DIGGING_META), potion(Reference.POTION_META), Items.bone, Items.redstone, Items.glowstone_dust);
//
//        // strength potion
//        addRecipe(true, false, potion(Reference.STRENGTH_META), potion(Reference.POTION_META), Items.blaze_powder, Items.redstone, Items.glowstone_dust);
//
//        // heal potion
//        addRecipe(true, false, potion(Reference.HEALING_META), potion(Reference.POTION_META), Items.speckled_melon, Items.glowstone_dust);
//
//        // jump potion
//        addRecipe(true, false, potion(Reference.BOUNDING_META), potion(Reference.POTION_META), Items.feather, Items.redstone, Items.glowstone_dust);
//
//        // regen potion
//        addRecipe(true, false, potion(Reference.REGENERATION_META), potion(Reference.POTION_META), Items.ghast_tear, Items.redstone, Items.glowstone_dust);
//
//        // resist potion
//        addRecipe(true, false, potion(Reference.RESISTANCE_META), potion(Reference.POTION_META), Items.leather, Items.redstone, Items.glowstone_dust);
//
//        // fire resist potion
//        addRecipe(true, false, potion(Reference.FIRE_WARDING_META), potion(Reference.POTION_META), Items.magma_cream, Items.redstone);
//
//        // breathing potion
//        addRecipe(true, false, potion(Reference.BREATHING_META), potion(Reference.POTION_META), new ItemStack(Items.dye, 1, 0), Items.redstone);
//
//        // invis potion
//        addRecipe(true, false, potion(Reference.INVISIBILITY_META), potion(Reference.INFRAVISION_META), Items.fermented_spider_eye, Items.redstone);
//
//        // vision potion
//        addRecipe(true, false, potion(Reference.INFRAVISION_META), potion(Reference.POTION_META), Items.golden_carrot, Items.redstone);
//
//        // protection potion
//        addRecipe(true, false, potion(8, Reference.PROTECTION_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), potion(Reference.FIRE_WARDING_META), Items.glowstone_dust, potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META), potion(Reference.RESISTANCE_META));
//
//        // potency potion
//        addRecipe(true, false, potion(8, Reference.POTENCE_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), potion(Reference.STRENGTH_META), Items.glowstone_dust, potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META), potion(Reference.BOUNDING_META));
//
//        // celerity potion
//        addRecipe(true, false, potion(8, Reference.CELERITY_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), potion(Reference.DIGGING_META), Items.glowstone_dust, potion(Reference.SPEED_META), potion(Reference.SPEED_META), potion(Reference.SPEED_META), potion(Reference.SPEED_META));
//
//        // stalker potion
//        addRecipe(true, false, potion(8, Reference.STALKER_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), potion(Reference.INFRAVISION_META), Items.glowstone_dust, potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META), potion(Reference.INVISIBILITY_META));
//
//        // panacea potion
//        addRecipe(true, false, potion(8, Reference.PANACEA_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), potion(Reference.HEALING_META), Items.milk_bucket, potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META), potion(Reference.REGENERATION_META));
//
//        // aphrodite
//        addRecipe(true, false, potion(Reference.APHRODITE_META), potion(Reference.SPLASH_META), new ItemStack(Items.dye, 1, 3), new ItemStack(Items.dye, 1, 1), Items.redstone);
//
//        // poison
//        addRecipe(true, false, potion(Reference.POISON_META), potion(Reference.SPLASH_META), Items.spider_eye, Items.fermented_spider_eye, Items.redstone);
//
//        // harm
//        addRecipe(true, false, potion(Reference.ACID_META), potion(Reference.SPLASH_META), Items.speckled_melon, Items.fermented_spider_eye, Items.glowstone_dust);
//
//        // confusion
//        addRecipe(true, false, potion(Reference.CONFUSION_META), potion(Reference.SPLASH_META), Items.golden_carrot, Items.fermented_spider_eye, Items.redstone);
//
//        // slowness
//        addRecipe(true, false, potion(Reference.SLOWING_META), potion(Reference.SPLASH_META), Items.sugar, Items.fermented_spider_eye, Items.glowstone_dust);
//
//        // weakness
//        addRecipe(true, false, potion(Reference.WEAKNESS_META), potion(Reference.SPLASH_META), Items.blaze_powder, Items.fermented_spider_eye, Items.glowstone_dust);
//
//        // wither
//        addRecipe(true, false, potion(Reference.WITHER_META), potion(Reference.SPLASH_META), new ItemStack(Items.skull, 1, 1), Items.glowstone_dust, Items.glowstone_dust);
//
//        // blindness
//        addRecipe(true, false, potion(Reference.BLINDING_META), potion(Reference.SPLASH_META), Items.golden_carrot, Items.fermented_spider_eye, Items.golden_carrot);
//
//        // ruin
//        addRecipe(true, false, potion(3, Reference.RUINATION_META), potion(Reference.SLOWING_META), potion(Reference.WEAKNESS_META), potion(Reference.POISON_META), Items.glowstone_dust);
//
//        // fertility
//        addRecipe(true, false, potion(Reference.FERTILIZER_META), potion(Reference.SPLASH_META), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15));
    }

}
