package xreliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WraithNodeBlock extends Block {
	private static final VoxelShape SHAPE = box(2, 0, 2, 14, 12, 14);

	public WraithNodeBlock() {
		super(Properties.of(Material.STONE).strength(1.5F, 5.0F).noOcclusion());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
}
