package xreliquary.pedestal.wrappers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PedestalShearsWrapper implements IPedestalActionItemWrapper {
	private boolean isShearingBlock = false;
	private BlockPos blockPosBeingSheared;
	private Queue<BlockPos> blockQueue = new ArrayDeque<>();

	@Override
	public void update(@Nonnull ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		FakePlayer fakePlayer = pedestal.getFakePlayer();
		int shearsRange = Settings.Blocks.Pedestal.shearsWrapperRange;

		if(!shearAnimals(stack, world, fakePlayer, pos, shearsRange)) {
			if(!shearBlocks(stack, world, pedestal, fakePlayer, pos, shearsRange)) {
				pedestal.setActionCoolDown(100);
				return;
			}
		}

		if(!isShearingBlock) {
			pedestal.setActionCoolDown(Settings.Blocks.Pedestal.shearsWrapperCooldown);
		}

		if(stack.getCount() == 0)
			pedestal.destroyCurrentItem();
	}

	@Override
	public void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	private boolean shearBlocks(ItemStack stack, World world, IPedestal pedestal, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		if(!isShearingBlock) {
			if(blockQueue.isEmpty()) {
				updateQueue(stack, world, pos, shearsRange);
				if(blockQueue.isEmpty()) {
					return false;
				}
			}

			blockPosBeingSheared = blockQueue.remove();
			IBlockState blockState = world.getBlockState(blockPosBeingSheared);
			if(blockState.getBlock() instanceof IShearable && ((IShearable) blockState.getBlock()).isShearable(stack, world, blockPosBeingSheared)) {
				float hardness = blockState.getBlockHardness(world, blockPosBeingSheared);
				float digSpeed = stack.getItem().getStrVsBlock(stack, blockState);

				pedestal.setActionCoolDown((int) ((hardness * 1.5f * 20f) / digSpeed));
				isShearingBlock = true;
			}
		} else {
			stack.getItem().onBlockStartBreak(stack, blockPosBeingSheared, fakePlayer);
			IBlockState blockState = world.getBlockState(blockPosBeingSheared);
			world.setBlockToAir(blockPosBeingSheared);
			world.playEvent(2001, blockPosBeingSheared, Block.getStateId(blockState));

			isShearingBlock = false;
		}
		return true;
	}

	private void updateQueue(ItemStack stack, World world, BlockPos pos, int shearsRange) {
		for(int y = pos.getY() - shearsRange; y <= pos.getY() + shearsRange; y++) {
			for(int x = pos.getX() - shearsRange; x <= pos.getX() + shearsRange; x++) {
				for(int z = pos.getZ() - shearsRange; z <= pos.getZ() + shearsRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentBlockPos);
					if(blockState.getBlock() instanceof IShearable && ((IShearable) blockState.getBlock()).isShearable(stack, world, currentBlockPos)) {
						blockQueue.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean shearAnimals(ItemStack stack, World world, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		List<EntityAnimal> entities = world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(pos.getX() - shearsRange, pos.getY() - shearsRange, pos.getZ() - shearsRange, pos.getX() + shearsRange, pos.getY() + shearsRange, pos.getZ() + shearsRange));

		for(EntityAnimal animal : entities) {
			if(animal instanceof IShearable && ((IShearable) animal).isShearable(stack, world, animal.getPosition())) {
				fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);
				fakePlayer.interactOn(animal, EnumHand.MAIN_HAND);
				return true;
			}
		}
		return false;
	}
}
