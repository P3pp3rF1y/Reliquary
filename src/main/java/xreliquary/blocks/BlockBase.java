package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block {

	public BlockBase(Material material, String langName) {
		super(material);
		this.setUnlocalizedName(langName);
		this.setHardness(1.0F);
		this.setResistance(1.0F);
	}
}