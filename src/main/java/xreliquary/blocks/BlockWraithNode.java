package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

public class BlockWraithNode extends Block {
	private static final AxisAlignedBB WRAITH_NODE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.750D, 0.875D);

	public BlockWraithNode() {
		super(Material.rock);
		blockHardness = 1.5F;
		blockResistance = 5.0F;
		this.setUnlocalizedName(Names.wraith_node);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return WRAITH_NODE_AABB;
	}

}