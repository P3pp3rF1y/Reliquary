package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import xreliquary.Reliquary;
import xreliquary.reference.Reference;

public class BlockBase extends Block {

	BlockBase(Material material, String langName) {
		this(material, langName, 1.0F, 1.0F);
	}

	BlockBase(Material material, String name, float hardness, float resistance) {
		super(material);
		this.setHardness(hardness);
		this.setResistance(resistance);
		init(this, name);
	}

	public static void init(Block block, String name) {
		block.setRegistryName(Reference.MOD_ID, name);
		block.setUnlocalizedName(name);
		block.setCreativeTab(Reliquary.CREATIVE_TAB);
	}
}
