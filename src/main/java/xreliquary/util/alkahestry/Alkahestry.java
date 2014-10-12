package xreliquary.util.alkahestry;

import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class Alkahestry {

    public static int LOW_TIER = 4;
    public static int MIDDLE_TIER = 8;
    public static int HIGH_TIER = 32;
    public static int UBER_TIER = 64;

    private static Map<String, AlkahestRecipe> REGISTRY = new HashMap<String, AlkahestRecipe>();

    public static void addKey(AlkahestRecipe recipe) {
        if (recipe.dictionaryName == null)
            REGISTRY.put(ContentHelper.getIdent(recipe.item.getItem()), recipe);
        else
            REGISTRY.put("OreDictionary:" + String.valueOf(OreDictionary.getOreID(recipe.dictionaryName)), recipe);
    }

    public static AlkahestRecipe getDictionaryKey(ItemStack stack) {
        for (AlkahestRecipe recipe : getRegistry().values()) {
            if (recipe.dictionaryName == null)
                return null;
            for (ItemStack dict : OreDictionary.getOres(recipe.dictionaryName)) {
                if (OreDictionary.itemMatches(dict, stack, false))
                    return recipe;
            }
        }
        return null;
    }

    public static void init() {

        addKey(new AlkahestRecipe(new ItemStack(Blocks.dirt), 32, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.cobblestone), 32, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.sand), 32, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.gravel), 16, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.sandstone, 1, -1), 8, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.clay), 2, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.netherrack), 8, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.coal, 1, 1), 4, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.glowstone_dust), 4, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.glowstone, 1, 0), 1, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.dye, 1, 4), 1, LOW_TIER));

        addKey(new AlkahestRecipe(new ItemStack(Blocks.obsidian), 4, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.soul_sand), 8, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.nether_brick), 4, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Blocks.end_stone), 16, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.coal, 1, 0), 4, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.gunpowder), 2, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.flint), 8, MIDDLE_TIER));


        //high tier
        addKey(new AlkahestRecipe(new ItemStack(Items.gold_ingot), 1, HIGH_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.iron_ingot), 1, HIGH_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Items.emerald), 1, HIGH_TIER));

        // I guess mods should start following the new naming convention.
        // *shrugs*
        addKey(new AlkahestRecipe("tin_ingot", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("silver_ingot", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("copper_ingot", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("steel_ingot", 1, HIGH_TIER));

        addKey(new AlkahestRecipe("ingotTin", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotSilver", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotCopper", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotSteel", 1, HIGH_TIER));

        addKey(new AlkahestRecipe(new ItemStack(Items.diamond), 1, UBER_TIER));

        //above uber
        addKey(new AlkahestRecipe(new ItemStack(Items.nether_star), 1, UBER_TIER * 2));
    }

    public static Map<String, AlkahestRecipe> getRegistry() {
        return REGISTRY;
    }

}
