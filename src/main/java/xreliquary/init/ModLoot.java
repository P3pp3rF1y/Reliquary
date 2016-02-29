package xreliquary.init;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import xreliquary.reference.Settings;

public class ModLoot {
    public static void init() {
        if (Settings.chestLootEnabled) {
            String c = ChestGenHooks.MINESHAFT_CORRIDOR;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 1, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.BAT_WING, 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 1, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SQUID_BEAK, 1, 3, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 1, 5));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 1));

            c = ChestGenHooks.PYRAMID_DESERT_CHEST;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 2, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CHELICERAE, 1, 1, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CREEPER_GLAND, 1, 4, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ZOMBIE_HEART, 1, 5, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 2, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.MOLTEN_CORE, 1, 1, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SQUID_BEAK, 1, 4, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 1));

            c = ChestGenHooks.PYRAMID_JUNGLE_CHEST;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 2, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CREEPER_GLAND, 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.BAT_WING, 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 2, 5));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 1));

            c = ChestGenHooks.STRONGHOLD_CORRIDOR;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 3, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 3, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.FROZEN_CORE, 1, 3, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelheartVial), 1, 2, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.shearsOfWinter), 1, 1, 1));

            c = ChestGenHooks.STRONGHOLD_LIBRARY;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 3, 10));

            c = ChestGenHooks.STRONGHOLD_CROSSING;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.WITHER_RIB, 1, 2, 4));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.FROZEN_CORE, 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelheartVial), 1, 3, 8));

            c = ChestGenHooks.VILLAGE_BLACKSMITH;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ZOMBIE_HEART, 1, 5, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 1, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.glowingWater), 1, 2, 3));

            c = ChestGenHooks.DUNGEON_CHEST;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CHELICERAE, 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ZOMBIE_HEART, 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 1, 2));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.glowingWater), 1, 1, 3));

            c = ChestGenHooks.NETHER_FORTRESS;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.WITHER_RIB, 1, 2, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.MOLTEN_CORE, 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.emptyVoidTear), 1, 1, 1));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.salamanderEye), 1, 1, 1));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModBlocks.interdictionTorch), 1, 1, 1));
        }
    }
}
