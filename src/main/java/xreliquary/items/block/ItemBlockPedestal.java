package xreliquary.items.block;

import net.minecraft.block.Block;

public class ItemBlockPedestal extends ItemBlockBase {
	public ItemBlockPedestal(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}
