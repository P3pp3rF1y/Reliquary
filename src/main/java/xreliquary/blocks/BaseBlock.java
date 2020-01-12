package xreliquary.blocks;

import net.minecraft.block.Block;
import xreliquary.reference.Reference;

public class BaseBlock extends Block {
	BaseBlock(String name, Properties properties) {
		super(properties);
		setRegistryName(Reference.MOD_ID, name);
	}
}
