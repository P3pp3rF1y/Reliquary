package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import reliquary.items.ICreativeTabItemGenerator;

import java.util.function.Consumer;

public class WraithNodeBlock extends Block implements ICreativeTabItemGenerator {
	private static final VoxelShape SHAPE = box(2, 0, 2, 14, 12, 14);

	public WraithNodeBlock() {
		super(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 5.0F).noOcclusion());
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
}
