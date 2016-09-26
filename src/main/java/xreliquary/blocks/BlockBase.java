package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import slimeknights.mantle.client.CreativeTab;
import xreliquary.Reliquary;

public class BlockBase extends Block {

	BlockBase(Material material, String langName) {
		this(material, langName, 1.0F, 1.0F);
	}

	BlockBase(Material material, String langName, float hardness, float resistance) {
		super(material);
		this.setHardness(hardness);
		this.setResistance(resistance);
		init(this, langName);
	}

	static void init(Block block, String langName) {
		block.setUnlocalizedName(langName);
		block.setCreativeTab(Reliquary.CREATIVE_TAB);
	}
}