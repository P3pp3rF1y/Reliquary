package xreliquary.items.alkahestry;

import xreliquary.items.XRItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class XRAlkahestry {
	
    public static void init() {
        addAlkahestry();
    }

    private static void addAlkahestry() {

        addAlkahestry(Block.dirt, 0, 16, 'r', 1);
        addAlkahestry(Block.cobblestone, 0, 16, 'r', 1);
        addAlkahestry(Block.sand, 0, 16, 'r', 1);
        addAlkahestry(Block.gravel, 0, 8, 'r', 1);
        for (int meta = 0; meta < 4; meta++) {
            addAlkahestry(Block.wood, meta, 2, 'r', 1);
        }
        addAlkahestry(Block.sandStone, -1, 4, 'r', 1);
        addAlkahestry(Block.blockClay, 0, 2, 'r', 1);
        addAlkahestry(Block.obsidian, 0, 2, 'r', 1);
        addAlkahestry(Block.netherrack, 0, 16, 'r', 1);
        addAlkahestry(Block.slowSand, 0, 4, 'r', 1);
        addAlkahestry(Block.netherBrick, 0, 16, 'r', 1);
        addAlkahestry(Block.whiteStone, 0, 16, 'r', 1);
        addAlkahestry(Item.coal, 1, 2, 'r', 1);
        addAlkahestry(Item.diamond, 0, 1, 'b', 4);
        addAlkahestry(Item.ingotGold, 0, 1, 'b', 1);
        addAlkahestry(Item.ingotIron, 0, 1, 'l', 4);
        addAlkahestry(Item.gunpowder, 0, 1, 'r', 4);
        addAlkahestry(Item.flint, 0, 8, 'r', 1);
        addAlkahestry(Item.glowstone, 0, 1, 'r', 1);
        addAlkahestry(Item.dyePowder, 4, 1, 'r', 4);
        addAlkahestry(Item.emerald, 0, 1, 'b', 1);
        GameRegistry.addRecipe(new ItemStack(Block.dragonEgg, 1), new Object[] {
                "ddd", "dtd", "ddd", 'd', Block.blockDiamond, 't',
                XRItems.alkahestryTome });
        GameRegistry.addRecipe(new ItemStack(Item.netherStar, 2), new Object[] {
                "tds", "www", "hhh", 'd', Item.diamond, 'w',
                new ItemStack(Item.skull, 1, 1), 't', XRItems.alkahestryTome,
                's', Item.netherStar, 'h', XRItems.glowingWater });
    }

    public static void addAlkahestry(Object baseItem, int meta, int yield, char reagentChar, int cost) {
        /* yield++;
        Object recipeItems[] = new Object[cost + 2];
        recipeItems[0] = new ItemStack(XRItems.alkahestryTome, 1);
        ItemStack original = null;
        if (baseItem instanceof ItemStack) {
            original = (ItemStack) baseItem;
        } else if (baseItem instanceof Item) {
            original = new ItemStack((Item) baseItem, 1, meta);
        } else if (baseItem instanceof Block) {
            original = new ItemStack((Block) baseItem, 1, meta);
        }
        if (original == null)
            return;
        recipeItems[1] = original;
        for (int slot = 2; slot < recipeItems.length; slot++) {
            ItemStack reagent = getReagentByType(reagentChar);
            if (reagent == null)
                return;
            recipeItems[slot] = reagent;
        }
        GameRegistry.addShapelessRecipe(new ItemStack(original.getItem(),
                yield, meta), recipeItems);
    }

    // reagent types as follows:
    // 'r' redstone, 'g' glowstone, 'l' lapis, 'b' lapis block
    private static ItemStack getReagentByType(char reagentChar) {
        switch (reagentChar) {
        case 'r':
            return new ItemStack(Item.redstone, 1);
        case 'l':
            return new ItemStack(Item.dyePowder, 1, 4);
        case 'b':
            return new ItemStack(Block.blockLapis, 1);
        }
        return null;
    */
    }

}
