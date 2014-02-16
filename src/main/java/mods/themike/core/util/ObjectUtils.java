package mods.themike.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * ItemBase, a helper class for blocks and items, but not manipulating objects themselves. Right now,
 * all it does it provide a clean way to get a block identifier (as in, minecraft:water).
 *
 * @author TheMike
 */
public class ObjectUtils {

    /**
     * Returns a block identifier. Examples of these are 'minecraft:water', 'xreliquary:lilypad', etc. Will return null
     * if the block itself is null.
     *
     * @param block
     *            The block to get the identifier from.
     */
    public static String getBlockIdentifier(Block block) {
        return block == null ? null : Block.blockRegistry.getNameForObject(block);
    }

    /**
     * Returns a item identifier. Examples of these are 'minecraft:water', 'xreliquary:lilypad', etc. Will return null
     * if the item itself is null.
     *
     * @param item
     *            The item to get the identifier from.
     */
    public static String getItemIdentifier(Item item) {
        return item == null ? null : Item.itemRegistry.getNameForObject(item);
    }

    /**
     * Returns true if the item idenifier of item1 matches that of item2. Returns false otherwise.
     */
    public static boolean areItemsEqual(Item item1, Item item2) {
        return getItemIdentifier(item1).equals(getItemIdentifier(item2));
    }

    /**
     * Returns true if the block idenifier of block1 matches that of block2. Returns false otherwise.
     */
    public static boolean areBlocksEqual(Block block1, Block block2) {
        return getBlockIdentifier(block1).equals(getBlockIdentifier(block2));
    }

}
