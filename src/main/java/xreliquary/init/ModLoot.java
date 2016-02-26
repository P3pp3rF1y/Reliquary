package xreliquary.init;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import xreliquary.reference.Settings;

public class ModLoot {
    public static void init() {
        if (Settings.chestLootEnabled) {
            String c = ChestGenHooks.MINESHAFT_CORRIDOR;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ribBone(), 1, 1, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.slimePearl(), 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.batWing(), 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.enderHeart(), 1, 1, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.squidBeak(), 1, 3, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 1, 5));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 1));

            c = ChestGenHooks.PYRAMID_DESERT_CHEST;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ribBone(), 1, 2, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.spiderFangs(), 1, 1, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.creeperGland(), 1, 4, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.zombieHeart(), 1, 5, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.enderHeart(), 1, 2, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.moltenCore(), 1, 1, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.squidBeak(), 1, 4, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 1));

            c = ChestGenHooks.PYRAMID_JUNGLE_CHEST;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ribBone(), 1, 2, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.creeperGland(), 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.batWing(), 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.enderHeart(), 1, 2, 5));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 1));

            c = ChestGenHooks.STRONGHOLD_CORRIDOR;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ribBone(), 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.slimePearl(), 1, 3, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.enderHeart(), 1, 3, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.frozenCore(), 1, 3, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelheartVial), 1, 2, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.shearsOfWinter), 1, 1, 1));

            c = ChestGenHooks.STRONGHOLD_LIBRARY;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.slimePearl(), 1, 3, 10));

            c = ChestGenHooks.STRONGHOLD_CROSSING;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ribBone(), 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.witherRib(), 1, 2, 4));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.slimePearl(), 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.enderHeart(), 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.frozenCore(), 1, 2, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelheartVial), 1, 3, 8));

            c = ChestGenHooks.VILLAGE_BLACKSMITH;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.zombieHeart(), 1, 5, 6));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 1, 3));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.glowingWater), 1, 2, 3));

            c = ChestGenHooks.DUNGEON_CHEST;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ribBone(), 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.spiderFangs(), 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.zombieHeart(), 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 1, 2));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.glowingWater), 1, 1, 3));

            c = ChestGenHooks.NETHER_FORTRESS;
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.witherRib(), 1, 2, 10));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.slimePearl(), 1, 1, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.moltenCore(), 1, 2, 8));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.emptyVoidTear), 1, 1, 1));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.salamanderEye), 1, 1, 1));
            ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModBlocks.interdictionTorch), 1, 1, 1));
        }
    }
}
