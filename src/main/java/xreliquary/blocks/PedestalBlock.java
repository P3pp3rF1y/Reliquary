package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Settings;
import xreliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class PedestalBlock extends PassivePedestalBlock {
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

	public PedestalBlock() {
		super();
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ENABLED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(ENABLED);
	}

	@Override
	protected boolean isDisabled() {
		return Boolean.TRUE.equals(Settings.COMMON.disable.disablePedestal.get());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(world.getDimensionKey().getRegistryName(), pos, 160);

		for (BlockPos pedestalPosition : pedestalPositions) {
			WorldHelper.getTile(world, pedestalPosition, PedestalTileEntity.class).ifPresent(PedestalTileEntity::updateRedstone);
		}
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PedestalTileEntity();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		//noinspection ConstantConditions
		((PedestalTileEntity) worldIn.getTileEntity(pos)).neighborUpdate();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (Boolean.TRUE.equals(state.get(ENABLED)) && rand.nextInt(3) == 1) {
			Direction enumfacing = state.get(FACING);
			double xMiddle = (double) pos.getX() + 0.5D;
			double y = (double) pos.getY() + 4.0D / 16.0D + rand.nextDouble() * 4.0D / 16.0D;
			double zMiddle = (double) pos.getZ() + 0.5D;
			double sideOffset = 0.27D;
			double randomOffset = rand.nextDouble() * 0.3D - 0.15D;

			switch (enumfacing) {
				case WEST:
					world.addParticle(RedstoneParticleData.REDSTONE_DUST, xMiddle + sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D);
					break;
				case EAST:
					world.addParticle(RedstoneParticleData.REDSTONE_DUST, xMiddle - sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D);
					break;
				case NORTH:
					world.addParticle(RedstoneParticleData.REDSTONE_DUST, xMiddle + randomOffset, y, zMiddle + sideOffset, 0.0D, 0.0D, 0.0D);
					break;
				default:
				case SOUTH:
					world.addParticle(RedstoneParticleData.REDSTONE_DUST, xMiddle + randomOffset, y, zMiddle - sideOffset, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (world.isRemote) {
			return !heldItem.isEmpty() || player.isCrouching() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
		}

		return WorldHelper.getTile(world, pos, PedestalTileEntity.class).map(pedestal -> {
					if (heldItem.isEmpty() && !player.isCrouching() && hand == Hand.MAIN_HAND && hit.getFace() == state.get(FACING).getOpposite()
							&& switchClicked(hit.getFace(), hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
						pedestal.toggleSwitch();
						return ActionResultType.SUCCESS;
					}
					return super.onBlockActivated(state, world, pos, player, hand, hit);
				}
		).orElse(ActionResultType.FAIL);
	}

	private boolean switchClicked(Direction side, Vector3d hitVec) {
		double xOff = hitVec.getX();
		double yOff = hitVec.getY();
		double zOff = hitVec.getZ();

		if (yOff < 0.3 || yOff > 0.65) {
			return false;
		}
		return (side.getAxis() == Direction.Axis.Z && xOff >= 0.35 && xOff <= 0.65)
				|| (side.getAxis() == Direction.Axis.X && zOff >= 0.35 && zOff <= 0.65);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) {
			return;
		}
		PedestalRegistry.unregisterPosition(world.getDimensionKey().getRegistryName(), pos);
		WorldHelper.getTile(world, pos, PedestalTileEntity.class).ifPresent(PedestalTileEntity::removeAndSpawnItem);
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
