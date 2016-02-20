package xreliquary.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import xreliquary.items.alkahestry.AlkahestryChargingRecipe;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryDrainRecipe;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;


public class XRRecipes {

    public static ItemStack emptyVoidTear() { return new ItemStack(ModItems.emptyVoidTear, 1, 0); }

    public static ItemStack witherSkull() { return new ItemStack(Items.skull, 1, 1); }

    public static ItemStack roseBush() { return new ItemStack(Blocks.double_plant, 1, 4); }

    public static ItemStack blackWool() { return new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META); }

    public static ItemStack lapis() { return new ItemStack(Items.dye, 1, 4); }

    public static ItemStack gunPart(int i, int m) { return new ItemStack(ModItems.gunPart, i, m); }

    public static ItemStack magazine(int m) { return magazine(1, m); }

    public static ItemStack magazine(int i, int m) { return new ItemStack(ModItems.magazine, i, m); }

    public static ItemStack bullet(int m) { return bullet(1, m); }

    public static ItemStack bullet(int i, int m) { return new ItemStack(ModItems.bullet, i, m); }

    //public static ItemStack potion(int m) { return potion(1, m); }

    //public static ItemStack potion(int i, int m) { return new ItemStack(getItem(Names.condensed_potion), i, m); }

    public static ItemStack ingredient(int m) { return new ItemStack(ModItems.mobIngredient, 1, m); }

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
    
    public static ItemStack heartPearl(int m) { return new ItemStack(ModItems.heartPearl, 1, m); }

    public static ItemStack nianZhu(int m) { return new ItemStack(ModItems.heartZhu, 1, m); }


    //this version of the addRecipe method checks first to see if the recipe is disabled in our automated recipe-disabler config
    //if any component of the item is in the recipe disabler list, it will ALSO block the recipe automatically.
    //override disabler forces the recipe to evaluate anyway. This occurs for items that don't fall into XR scope, and thus shouldn't be evaluated.
    public static void addRecipe(boolean isShapeless, boolean overrideDisabler, ItemStack result, Object... params) {
        //TODO: figure out if we need disabling recipes vs this done by modpack devs with minetweaker or such
/*        if (!overrideDisabler) {
            boolean enabled = Reliquary.CONFIG.getGroup(Names.recipe_enabled).containsKey( RegistryHelper.getBlockRegistryName(result.getItem()).replace(':', '_')) && Reliquary.CONFIG.getBool(Names.recipe_enabled,  RegistryHelper.getItemRegistryName(result.getItem()).replace(':', '_'));
            if (!enabled) return;
            for (Object obj : params) {
                String unlocalizedName = null;
                if (obj instanceof Block) {
                    unlocalizedName =  RegistryHelper.getBlockRegistryName((Block) obj);
                } else if (obj instanceof Item) {
                    unlocalizedName =  RegistryHelper.getItemRegistryName((Item) obj);
                } else if (obj instanceof ItemStack) {
                    unlocalizedName =  RegistryHelper.getItemRegistryName(((IktemStack) obj).getItem());
                }
                if (unlocalizedName == null || !Reliquary.CONFIG.getKeys(Names.recipe_enabled).contains(unlocalizedName.replace(Reference.MOD_ID + "_", Reference.MOD_ID + ":")))
                    continue;
                enabled = enabled && Reliquary.CONFIG.getBool(Names.recipe_enabled, unlocalizedName);
            }
            if (!enabled) return;
        }*/
        if (!isShapeless) GameRegistry.addRecipe(result, params);
            else GameRegistry.addShapelessRecipe(result, params);
    }

    public static void init() {
        // tome and alkahestry recipes
        GameRegistry.addRecipe(new AlkahestryDrainRecipe());
        GameRegistry.addRecipe(new AlkahestryChargingRecipe());
        GameRegistry.addRecipe(new AlkahestryCraftingRecipe());

        RecipeSorter.register(Reference.MOD_ID + ":alkahest_crafting", AlkahestryCraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
        RecipeSorter.register(Reference.MOD_ID + ":alkahest_charge", AlkahestryChargingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_crafting");
        RecipeSorter.register(Reference.MOD_ID + ":alkahest_drain", AlkahestryDrainRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_charge");

        //misc recipes
        //frozen cores to make packed ice.
        addRecipe(true, true, new ItemStack(Blocks.packed_ice, 1, 0), Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, frozenCore());

        //apothecary mortar recipe
        addRecipe(false, false, new ItemStack(ModBlocks.apothecaryMortar, 1, 0), "gng", "ngn", "nnn", 'n', Blocks.quartz_block, 'g', creeperGland());

        //apothecary cauldron recipe
        addRecipe(false, false, new ItemStack(ModBlocks.apothecaryCauldron, 1, 0), "gng", "ici", "nmn", 'g', creeperGland(), 'n', enderHeart(), 'i', infernalClaw(), 'c', Items.cauldron, 'm', moltenCore());

        //alkahestry tome
        if (Settings.EasyModeRecipes.alkahestryTome)
            addRecipe(true, false, new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage()), Items.book, ModItems.witchHat, moltenCore(), witherSkull());
        else
            addRecipe(true, false, new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage()), moltenCore(), ModItems.witchHat, stormEye(), creeperGland(), Items.book, slimePearl(), spiderFangs(), witherSkull(), enderHeart());

        //glowstone altar
        if (Settings.EasyModeRecipes.altar)
            addRecipe(true, false, new ItemStack(ModBlocks.alkahestryAltar, 1), Blocks.obsidian, Blocks.redstone_lamp, enderHeart(), creeperGland());
        else
            addRecipe(false, false, new ItemStack(ModBlocks.alkahestryAltar, 1), "dnd", "olo", "dgd", 'd', Items.glowstone_dust, 'n', enderHeart(), 'o', Blocks.obsidian, 'l', Blocks.redstone_lamp, 'g', creeperGland());

        //fertile_lilypad
        addRecipe(true, false, new ItemStack(ModBlocks.fertileLilypad, 1), fertileEssence(), fertileEssence(), fertileEssence(), Blocks.waterlily);

        //wraith node
        addRecipe(true, false, new ItemStack(ModBlocks.wraithNode, 1), enderHeart(), Items.emerald);

        //interdiction torch
        if (Settings.EasyModeRecipes.interdictionTorch)
            addRecipe(false, false, new ItemStack(ModBlocks.interdictionTorch, 4, 0), "bm", "nr", 'b', batWing(), 'm', moltenCore(), 'n', enderHeart(), 'r', Items.blaze_rod);
        else
            addRecipe(false, false, new ItemStack(ModBlocks.interdictionTorch, 4, 0), " n ", "mdm", "bwb", 'n', enderHeart(), 'm', moltenCore(), 'd', Items.diamond, 'b', Items.blaze_rod, 'w', batWing());

        // glowy bread
        addRecipe(true, false, new ItemStack(ModItems.glowingBread, 3), Items.bread, Items.bread, Items.bread, ModItems.glowingWater);

        //fertile essence
        if (Settings.EasyModeRecipes.fertileEssence)
            addRecipe(true, false, fertileEssence(), ribBone(), creeperGland(), new ItemStack(Items.dye, 1, Reference.GREEN_DYE_META), slimePearl());
        else
            addRecipe(false, false, fertileEssence(), "gbg", "scs", "gbg", 'g', creeperGland(), 'b', ribBone(), 's', slimePearl(), 'c', new ItemStack(Items.dye, 1, Reference.GREEN_DYE_META));

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
        if (Settings.EasyModeRecipes.seekerShot)
            addRecipe(true, false, bullet(8, 7), lapis(), Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        else
            addRecipe(true, false, bullet(4, 7), lapis(), slimePearl(), Items.gold_nugget, Items.gunpowder);
        // sand
        addRecipe(true, false, bullet(8, 8), Blocks.sandstone, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
        // storm
        addRecipe(true, false, bullet(8, 9), creeperGland(), creeperGland(), Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
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
        addRecipe(false, false, new ItemStack(ModItems.handgun, 1, 0), "bim", "isi", "igi", 'i', Items.iron_ingot, 'b', gunPart(1, 1), 'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', slimePearl());

		// fortune coin
        if (Settings.EasyModeRecipes.fortuneCoin)
            addRecipe(true, false, new ItemStack(ModItems.fortuneCoin, 1), enderHeart(), Items.gold_nugget, slimePearl(), batWing());
        else
            addRecipe(false, false, new ItemStack(ModItems.fortuneCoin, 1), "ege", "gng", "ege", 'e', Items.ender_eye, 'g', Items.gold_ingot, 'n', enderHeart());

        // cross of mercy
        addRecipe(false, false, new ItemStack(ModItems.mercyCross, 1), "wgr", "glg", "sgz", 'w', witherRib(), 'g', Items.gold_ingot, 'r', ribBone(), 'l', Items.leather, 's', new ItemStack(Items.skull, 1, 1), 'z', zombieHeart());

        // holy hand grenade
        addRecipe(true, false, new ItemStack(ModItems.holyHandGrenade, 4), ModItems.glowingWater, Items.gold_nugget, Blocks.tnt, creeperGland());

        // sojourner's staff
        if (Settings.EasyModeRecipes.sojournerStaff)
            addRecipe(true, false, new ItemStack(ModItems.sojournerStaff, 1), moltenCore(), Items.gold_ingot, Items.blaze_rod, emptyVoidTear());
        else
            addRecipe(false, false, new ItemStack(ModItems.sojournerStaff, 1), "gcm", "itc", "big", 'g', Items.gold_nugget, 'c', Items.magma_cream, 'm', moltenCore(), 'i', Items.gold_ingot, 't', ModItems.infernalTear, 'b', Items.blaze_rod);

        // lantern of paranoia
        if (Settings.EasyModeRecipes.lanternOfParanoia)
            addRecipe(false, false, new ItemStack(ModItems.lanternOfParanoia, 1), "isi", "gmg", " i ", 'i', Items.iron_ingot, 's', slimePearl(),  'g', Blocks.glass, 'm', moltenCore());
        else
            addRecipe(false, false, new ItemStack(ModItems.lanternOfParanoia, 1), "imi", "gtg", "ili", 'i', Items.iron_ingot, 'm', moltenCore(), 'g', Blocks.glass, 't', ModBlocks.interdictionTorch, 'l', creeperGland());

        // midas touchstone
        addRecipe(true, false, new ItemStack(ModItems.midasTouchstone, 1, 0), Blocks.anvil, Blocks.gold_block, Blocks.gold_block, moltenCore(), moltenCore(), moltenCore(), creeperGland(), creeperGland(), emptyVoidTear());

        // emperor's chalice
        if (Settings.EasyModeRecipes.emperorChalice)
            addRecipe(true, false, new ItemStack(ModItems.emperorChalice, 1, 0), Items.emerald, Items.gold_ingot, Items.bucket, emptyVoidTear());
        else
            addRecipe(false, false, new ItemStack(ModItems.emperorChalice, 1, 0), "ses", "ivi", "lbl", 's', slimePearl(), 'e', Items.emerald, 'i', Items.gold_ingot, 'v', emptyVoidTear(),  'l', lapis(), 'b', Items.bucket);

        // infernal chalice
        if (Settings.EasyModeRecipes.infernalChalice)
            addRecipe(true, false, new ItemStack(ModItems.infernalChalice, 1), ModItems.infernalClaws, ModItems.emperorChalice, ModItems.infernalTear, moltenCore());
        else
            addRecipe(false, false, new ItemStack(ModItems.infernalChalice, 1), "imi", "wcw", "mtm", 'i', ModItems.infernalClaws, 'm', moltenCore(), 'w', witherRib(), 'c', ModItems.emperorChalice, 't', ModItems.infernalTear);

        // salamander's eye
        if (Settings.EasyModeRecipes.salamanderEye)
            addRecipe(true, false, new ItemStack(ModItems.salamanderEye, 1), Items.ender_eye, moltenCore(), frozenCore(), enderHeart());
        else
            addRecipe(false, false, new ItemStack(ModItems.salamanderEye, 1), "fnm", "geg", "mnf", 'f', frozenCore(), 'n', enderHeart(), 'm', moltenCore(), 'g', Items.ghast_tear, 'e', Items.ender_eye);

        // ice rod
        if (Settings.EasyModeRecipes.iceMagusRod)
            addRecipe(false, false, new ItemStack(ModItems.iceMagusRod, 1, 0), " df", " vd", "i  ", 'd', Items.diamond, 'f', frozenCore(), 'i', Items.iron_ingot, 'v', emptyVoidTear());
        else
            addRecipe(false, false, new ItemStack(ModItems.iceMagusRod, 1, 0), "fdf", "ptd", "ipf", 'f', frozenCore(), 'd', Items.diamond, 'p', Blocks.packed_ice, 't', emptyVoidTear(), 'i', Items.iron_ingot);

        //glacial staff
        if (Settings.EasyModeRecipes.glacialStaff)
            addRecipe(true, false, new ItemStack(ModItems.glacialStaff, 1, 0), ModItems.iceMagusRod, emptyVoidTear(), frozenCore(), ModItems.shearsOfWinter);
        else
            addRecipe(false, false, new ItemStack(ModItems.glacialStaff, 1, 0), "fds", "fvd", "iff", 'f', frozenCore(), 'd', Items.diamond, 's', ModItems.shearsOfWinter, 'v', emptyVoidTear(), 'i', ModItems.iceMagusRod);

        // ender staff
        if (Settings.EasyModeRecipes.enderStaff)
            addRecipe(false, false, new ItemStack(ModItems.enderStaff, 1, 0), " be", "nvb", "sn ", 'v', ModItems.emptyVoidTear, 'e', Items.ender_eye, 's', Items.stick, 'n', enderHeart(), 'b', batWing());
        else
            addRecipe(false, false, new ItemStack(ModItems.enderStaff, 1, 0), "nbe", "nvb", "rnn", 'n', enderHeart(), 'b', batWing(), 'e', Items.ender_eye, 'v', emptyVoidTear(), 'r', Items.blaze_rod);

        // rending gale
        if (Settings.EasyModeRecipes.rendingGale)
            addRecipe(false, false, new ItemStack(ModItems.rendingGale, 1, 0), " be", "gvb", "sg ", 'b', batWing(), 'e', stormEye(), 'g', Items.gold_ingot, 'v', emptyVoidTear(), 's', Items.stick);
        else
            addRecipe(false, false, new ItemStack(ModItems.rendingGale, 1, 0), "ebe", "fvb", "rfe", 'e', stormEye(), 'b', batWing(), 'f', ModItems.angelicFeather, 'v', emptyVoidTear(), 'r', Items.blaze_rod);

        // harvest rod
        addRecipe(false, false, new ItemStack(ModItems.harvestRod, 1, 0), " rf", "vtr", "sv ", 'r', roseBush(), 'f', fertileEssence(), 'v', Blocks.vine, 't', emptyVoidTear(), 's', Items.stick );

        // pyromancer staff
        if (Settings.EasyModeRecipes.pyromancerStaff)
            addRecipe(true, false, new ItemStack(ModItems.pyromancerStaff, 1, 0), ModItems.infernalClaws, Items.blaze_rod, ModItems.infernalTear, ModItems.salamanderEye);
        else
            addRecipe(false, false, new ItemStack(ModItems.pyromancerStaff, 1, 0), "mcs", "mic", "rmm", 'm', moltenCore(), 'c', ModItems.infernalClaws, 's', ModItems.salamanderEye, 'i', ModItems.infernalTear, 'r', Items.blaze_rod);

        // serpent staff
        if (Settings.EasyModeRecipes.serpentStaff)
            addRecipe(false, false, new ItemStack(ModItems.serpentStaff, 1), " ce", " kc", "s  ", 'c', spiderFangs(), 'e', Items.ender_eye, 'k', shellFragment(), 'b', Items.stick);
        else
            addRecipe(false, false, new ItemStack(ModItems.serpentStaff, 1), "coe", "pko", "bpc", 'c', spiderFangs(), 'o', Blocks.obsidian, 'e', Items.ender_eye, 'p', slimePearl(), 'k', shellFragment(), 'b', Items.blaze_rod);

        // rod of lyssa
        if (Settings.EasyModeRecipes.rodOfLyssa)
            addRecipe(true, false, new ItemStack(ModItems.rodOfLyssa, 1, 0), infernalClaw(), batWing(), enderHeart(), Items.fishing_rod);
        else
            addRecipe(false, false, new ItemStack(ModItems.rodOfLyssa, 1, 0), " br", "nms", "r i", 'b', batWing(), 'r', Items.blaze_rod, 'n', enderHeart(), 'm', moltenCore(), 's', Items.string, 'i', infernalClaw());

        // shears of winter
        addRecipe(true, false, new ItemStack(ModItems.shearsOfWinter, 1, 0), frozenCore(), Items.shears, Items.diamond, Items.diamond);

        // magicbane
        if (Settings.EasyModeRecipes.magicBane)
            addRecipe(false, false, new ItemStack(ModItems.magicbane, 1, 0), "ng", "in", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'n', enderHeart());
        else
            addRecipe(false, false, new ItemStack(ModItems.magicbane, 1, 0), "een", "nge", "ine", 'e', Items.ender_eye, 'n', enderHeart(), 'g', Items.gold_ingot, 'i', Items.iron_ingot);

        // witherless rose
        addRecipe(false, false, new ItemStack(ModItems.witherlessRose, 1), "fnf", "nrn", "fnf", 'f', fertileEssence(), 'n', Items.nether_star, 'r', roseBush() );

        // crimson cloth
        addRecipe(true, false, ingredient(Reference.CLOTH_INGREDIENT_META), new ItemStack(Blocks.wool, 1, Reference.RED_WOOL_META), new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META), enderHeart(), enderHeart());

        // cloak
        addRecipe(false, false, new ItemStack(ModItems.twilightCloak, 1), "ici", "bcb", "bcb", 'i', Items.iron_ingot, 'b', blackWool(), 'c', ingredient(Reference.CLOTH_INGREDIENT_META));

        // void tear
        if (Settings.EasyModeRecipes.voidTear)
            addRecipe(true, false, emptyVoidTear(), Items.ghast_tear, enderHeart(), slimePearl(), lapis());
        else
            addRecipe(false, false, emptyVoidTear(), "lel", "pgp", "lnl", 'l', lapis(), 'e', Items.ender_pearl, 'p', slimePearl(), 'g', Items.ghast_tear, 'n', enderHeart());

        // infernal tear
        if (Settings.EasyModeRecipes.infernalTear)
            addRecipe(true, false, new ItemStack(ModItems.infernalTear, 1, 0), emptyVoidTear(), ModItems.witchHat, moltenCore(), infernalClaw());
        else
            addRecipe(false, false, new ItemStack(ModItems.infernalTear, 1, 0), "php", "mtm", "pcp", 'p', Items.blaze_powder, 'h', ModItems.witchHat, 'm', moltenCore(), 't', emptyVoidTear(), 'c', infernalClaw());

        // angelic feather
        if (Settings.EasyModeRecipes.angelicFeather)
            addRecipe(true, false, new ItemStack(ModItems.angelicFeather, 1), Items.feather, enderHeart(), batWing(), fertileEssence());
        else
            addRecipe(false, false, new ItemStack(ModItems.angelicFeather, 1), "dgd", "bfb", "ene", 'd', Items.glowstone_dust, 'g', Items.gold_ingot, 'b', batWing(), 'e', fertileEssence(), 'n', enderHeart(), 'f', Items.feather);

        // phoenix down
        addRecipe(true, false, new ItemStack(ModItems.phoenixDown, 1), ModItems.angelheartVial, ModItems.angelheartVial, ModItems.angelheartVial, ModItems.angelicFeather);

        // infernal claw
        addRecipe(true, false, infernalClaw(), Items.leather, moltenCore(), ribBone(), slimePearl());

        // infernal claws
        if (Settings.EasyModeRecipes.infernalClaws)
            addRecipe(true, false, new ItemStack(ModItems.infernalClaws, 1), infernalClaw(), infernalClaw(), infernalClaw(), slimePearl());
        else
            addRecipe(false, false, new ItemStack(ModItems.infernalClaws, 1), "ccc", "cpc", "mlm", 'c', infernalClaw(), 'p', slimePearl(), 'm', moltenCore(), 'l', Items.leather);

        // squid beak, wither rib & rib bone to bonemeal
        addRecipe(true, true, new ItemStack(Items.dye, 2, Reference.WHITE_DYE_META), squidBeak());
        addRecipe(true, true, new ItemStack(Items.dye, 6, Reference.WHITE_DYE_META), ribBone());
        addRecipe(true, true, new ItemStack(Items.dye, 8, Reference.WHITE_DYE_META), witherRib());

        // kraken shell fragment
        addRecipe(true, false, shellFragment(), squidBeak(), squidBeak(), squidBeak(), slimePearl());

        // kraken shell
        if (Settings.EasyModeRecipes.krakenShell)
            addRecipe(true, false, new ItemStack(ModItems.krakenShell, 1, 0), shellFragment(), shellFragment(), shellFragment(), enderHeart());
        else
            addRecipe(false, false, new ItemStack(ModItems.krakenShell, 1, 0), "nfn", "epe", "fnf", 'n', enderHeart(), 'f', shellFragment(), 'e', Items.ender_eye, 'p', slimePearl());

        // hero medallion - any meta fortune coin for people with "grandfathered" fortune coins (pre-Enabled NBT used item damage)
        if (Settings.EasyModeRecipes.heroMedallion)
            addRecipe(true, false, new ItemStack(ModItems.heroMedallion, 1), enderHeart(), new ItemStack(ModItems.fortuneCoin, 1, -1), ModItems.witchHat, ModItems.infernalTear);
        else
            addRecipe(false, false, new ItemStack(ModItems.heroMedallion, 1), "nie", "iti", "fin", 'n', enderHeart(), 'i', Items.iron_ingot, 'e', Items.ender_eye, 't', ModItems.infernalTear, 'f', new ItemStack(ModItems.fortuneCoin, 1, -1));

        // destruction catalyst
        if (Settings.EasyModeRecipes.destructionCatalyst)
            addRecipe(true, false, new ItemStack(ModItems.destructionCatalyst, 1, 0), Items.flint_and_steel, moltenCore(), creeperGland(), ModItems.infernalTear);
        else
            addRecipe(false, false, new ItemStack(ModItems.destructionCatalyst, 1, 0), "tmc", "gim", "fgt", 't', Blocks.tnt, 'm', moltenCore(), 'c', creeperGland(), 'g', Items.gold_ingot, 'i', ModItems.infernalTear, 'f', Items.flint_and_steel);

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

        // empty vial
        addRecipe(false, false, new ItemStack(ModItems.potion, 1, 0), "g g", "g g", " g ", 'g', Blocks.glass_pane);

        //non-standard potion list.

        // glowing water
        addRecipe(false, false, new ItemStack(ModItems.glowingWater, 5), "gbg", "gdg", "ngp", 'g', Blocks.glass_pane, 'b', Items.water_bucket, 'd', Items.glowstone_dust, 'p', Items.gunpowder, 'n', Items.nether_wart);

        // angelheart vial
        addRecipe(false, false, new ItemStack(ModItems.angelheartVial, 5), "gbg", "gcg", "fgf", 'g', Blocks.glass_pane, 'b', Items.milk_bucket, 'c', infernalClaw(), 'f', fertileEssence());

        // attraction
        addRecipe(false, false, new ItemStack(ModItems.attractionPotion, 5), "gbg", "gfg", "rgc", 'g', Blocks.glass_pane, 'b', Items.water_bucket, 'f', fertileEssence(), 'r', new ItemStack(Items.dye, 1, Reference.RED_DYE_META), 'c', new ItemStack(Items.dye, 1, Reference.BROWN_DYE_META));

        // fertility
        addRecipe(false, false, new ItemStack(ModItems.fertilePotion, 5), "gbg", "gfg", "cgy", 'g', Blocks.glass_pane, 'b', Items.water_bucket, 'f', fertileEssence(), 'c', new ItemStack(Items.dye, 1, Reference.GREEN_DYE_META), 'y', new ItemStack(Items.dye, 1, Reference.YELLOW_DYE_META));
    }

}
