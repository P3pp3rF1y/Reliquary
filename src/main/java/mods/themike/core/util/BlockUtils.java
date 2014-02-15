package mods.themike.core.util;

import net.minecraft.block.Block;

/**
 * ItemBase, a helper class for blocks, but not manipulating block objects themselves. Right now,
 * all it does it provide a clean way to get a block identifier (as in, minecraft:water).
 *
 * @author TheMike
 */
public class BlockUtils {

    /**
     * Returns a block identifier. Examples of these are 'minecraft:water', 'xreliquary:lilypad', etc. Will return null
     * if the block itself is null.
     *
     * @param block
     *            The block to get the block identifier from.
     */
    public static String getBlockIdentifier(Block block) {
        return block == null ? null : Block.blockRegistry.getNameForObject(block);
    }

}
