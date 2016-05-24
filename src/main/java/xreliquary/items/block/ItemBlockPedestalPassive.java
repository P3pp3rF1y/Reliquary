package xreliquary.items.block;

import net.minecraft.block.Block;

public class ItemBlockPedestalPassive extends ItemBlockBase {
	public ItemBlockPedestalPassive(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}
