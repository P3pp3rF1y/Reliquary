package xreliquary.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;

public class BlockWraithNode extends BlockBase {
	private static final AxisAlignedBB WRAITH_NODE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.750D, 0.875D);

	public BlockWraithNode() {
		super(Material.ROCK, Names.Blocks.WRAITH_NODE, 1.5F, 5.0F);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return WRAITH_NODE_AABB;
	}

}
