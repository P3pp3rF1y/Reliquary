package xreliquary.pedestal.wrappers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.entities.EntityXRFakePlayer;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketPedestalFishHook;
import xreliquary.reference.Settings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedestalFishingRodWrapper implements IPedestalActionItemWrapper {
	private static final int PACKET_RANGE = 50;
	private static final int RANGE = 4;
	private static final int NO_WATER_COOLDOWN = 100;
	private static final int BAD_THROW_TIMEOUT = 60;
	private static final int ABSOLUTE_TIMEOUT = 1200;

	private EntityXRFakePlayer fakePlayer;
	private boolean badThrowChecked;
	private int ticksSinceLastThrow;
	private boolean retractFail = false;

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		ticksSinceLastThrow++;

		if (fakePlayer != null && fakePlayer.fishingBobber != null) {
			handleHookStates(stack, pedestal);
			syncHookData(pedestal);
		} else {
			setupFakePlayer(pedestal.getTheWorld(), pedestal.getBlockPos());
			Optional<BlockPos> p = getBestWaterBlock(pedestal);
			if (p.isPresent()) {
				updateHeldItem(stack);
				setPitchYaw(p.get());
				spawnFishHook(pedestal);
				badThrowChecked = false;
				ticksSinceLastThrow = 0;
			} else {
				pedestal.setActionCoolDown(NO_WATER_COOLDOWN);
			}
		}
	}

	private void handleHookStates(ItemStack stack, IPedestal pedestal) {
		if (retractFail) {
			//take care of failed retract
			if (getTicksCatchable(fakePlayer.fishingBobber) == 0) {
				retractHook(pedestal, stack);
				retractFail = false;
			}
		} else if (!badThrowChecked && ticksSinceLastThrow > BAD_THROW_TIMEOUT) {
			//when hook doesn't land in water retract it after some time
			//noinspection ConstantConditions
			if (!fakePlayer.fishingBobber.isInWater()) {
				retractHook(pedestal, stack);
			} else {
				badThrowChecked = true;
			}
		} else if (ticksSinceLastThrow > ABSOLUTE_TIMEOUT) {
			//sometimes hook can get stuck in a bad state so take care of that
			retractHook(pedestal, stack);
		} else //noinspection ConstantConditions
			if (getTicksCatchable(fakePlayer.fishingBobber) > 0 || fakePlayer.fishingBobber.caughtEntity != null) {
				if (pedestal.getTheWorld().rand.nextInt(100) <= Settings.COMMON.blocks.pedestal.fishingWrapperSuccessRate.get()) {
					retractHook(pedestal, stack);
				} else {
					retractFail = true;
				}
			}
	}

	private void updateHeldItem(ItemStack fishingRod) {
		ItemStack heldItem = fakePlayer.getHeldItemMainhand();
		if (heldItem.isEmpty()) {
			fakePlayer.setHeldItem(Hand.MAIN_HAND, fishingRod);
			return;
		}
		if (!heldItem.isItemEqualIgnoreDurability(fishingRod)) {
			fakePlayer.setHeldItem(Hand.MAIN_HAND, fishingRod);
		}
	}

	private void retractHook(IPedestal pedestal, ItemStack stack) {
		//noinspection ConstantConditions
		int i = fakePlayer.fishingBobber.handleHookRetraction(stack);
		stack.damageItem(i, fakePlayer, p -> {});
		//destroy the item when it gets used up
		if (stack.getCount() == 0) {
			pedestal.destroyItem();
		}

		pedestal.setActionCoolDown(Settings.COMMON.blocks.pedestal.fishingWrapperRetractDelay.get() * 20);
	}

	private Optional<BlockPos> getBestWaterBlock(IPedestal pedestal) {
		List<List<BlockPos>> connectedGroups = new ArrayList<>();
		List<BlockPos> visitedBlocks = new ArrayList<>();

		BlockPos pos = pedestal.getBlockPos();
		int pedestalX = pos.getX();
		int pedestalY = pos.getY();
		int pedestalZ = pos.getZ();

		for (int y = pedestalY - RANGE; y < pedestalY; y++) {
			for (int r = 1; r <= RANGE; r++) {
				int x = pedestalX - r;
				int z = pedestalZ - r;
				while (x <= pedestalX + r) {
					checkForAndAddWaterBlocks(pedestal, visitedBlocks, connectedGroups, pedestalX, pedestalY, pedestalZ, x, y, z);
					x++;
				}
				x--;

				while (z <= pedestalZ + r) {
					checkForAndAddWaterBlocks(pedestal, visitedBlocks, connectedGroups, pedestalX, pedestalY, pedestalZ, x, y, z);
					z++;
				}
				z--;

				while (x >= pedestalX - r) {
					checkForAndAddWaterBlocks(pedestal, visitedBlocks, connectedGroups, pedestalX, pedestalY, pedestalZ, x, y, z);
					x--;
				}
				x++;

				while (z >= pedestalZ - r) {
					checkForAndAddWaterBlocks(pedestal, visitedBlocks, connectedGroups, pedestalX, pedestalY, pedestalZ, x, y, z);
					z--;
				}
			}
		}

		BlockPos closestBlockInLargestGroup = null;
		int closestSqDistance = Integer.MAX_VALUE;
		int mostBlocks = 0;
		for (List<BlockPos> group : connectedGroups) {
			if (group.size() > mostBlocks) {
				mostBlocks = group.size();
				for (BlockPos waterPos : group) {
					int xDiff = waterPos.getX() - pedestalX;
					int yDiff = waterPos.getY() - pedestalY;
					int zDiff = waterPos.getZ() - pedestalZ;

					int sqDistance = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;

					if (sqDistance < closestSqDistance) {
						closestSqDistance = sqDistance;
						closestBlockInLargestGroup = waterPos;
					}
				}
			}
		}

		return Optional.ofNullable(closestBlockInLargestGroup);
	}

	private void checkForAndAddWaterBlocks(IPedestal pedestal, List<BlockPos> visitedBlocks, List<List<BlockPos>> connectedGroups, double pedestalX, double pedestalY, double pedestalZ, int x, int y, int z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		if (!visitedBlocks.contains(blockPos)) {
			List<BlockPos> group = new ArrayList<>();
			checkForWaterAndSearchNeighbors(pedestal, visitedBlocks, pedestalX, pedestalY, pedestalZ, blockPos, group);
			if (!group.isEmpty()) {
				connectedGroups.add(group);
			}
		}
	}

	private void checkForWaterAndSearchNeighbors(IPedestal pedestal, List<BlockPos> visitedBlocks, double pedestalX, double pedestalY, double pedestalZ, BlockPos blockPos, List<BlockPos> group) {
		visitedBlocks.add(blockPos);
		BlockState blockState = pedestal.getTheWorld().getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.WATER) {
			int x = blockPos.getX();
			int y = blockPos.getY();
			int z = blockPos.getZ();

			double startX = fakePlayer.posX;
			double startY = fakePlayer.posY;
			double startZ = fakePlayer.posZ;

			//make sure that the fakePlayer can see the block
			BlockRayTraceResult raytraceresult = pedestal.getTheWorld().rayTraceBlocks(
					new RayTraceContext(new Vec3d(startX, startY, startZ), new Vec3d(((double) x) + 0.5D, ((double) y) + 0.99D, ((double) z) + 0.5D), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, fakePlayer));
			if (raytraceresult.getType() != RayTraceResult.Type.MISS && raytraceresult.getPos().equals(blockPos)) {
				group.add(blockPos);
				for (Direction direction : Direction.Plane.HORIZONTAL) {
					BlockPos neighborPos = blockPos.offset(direction);
					//no search outside of the range
					if (neighborPos.getX() <= pedestalX + RANGE && neighborPos.getX() >= pedestalX - RANGE && neighborPos.getY() <= pedestalY + RANGE && neighborPos.getY() >= pedestalY - RANGE) {
						addNeighboringWater(pedestal, visitedBlocks, group, pedestalX, pedestalY, pedestalZ, neighborPos);
					}
				}
			}
		}
	}

	private void addNeighboringWater(IPedestal pedestal, List<BlockPos> visitedBlocks, List<BlockPos> group, double pedestalX, double pedestalY, double pedestalZ, BlockPos blockPos) {
		if (!visitedBlocks.contains(blockPos)) {
			checkForWaterAndSearchNeighbors(pedestal, visitedBlocks, pedestalX, pedestalY, pedestalZ, blockPos, group);
		}
	}

	private void spawnFishHook(IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		world.playSound(null, pedestal.getBlockPos(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));

		Vec3d pos = fakePlayer.getPositionVec();
		world.addEntity(new FishingBobberEntity(world, fakePlayer, pos.x, pos.y, pos.z));
	}

	private void syncHookData(IPedestal pedestal) {
		FishingBobberEntity hook = fakePlayer.fishingBobber;
		BlockPos pedestalPos = pedestal.getBlockPos();

		if (hook == null) {
			PacketHandler.sendToAllAround(new PacketPedestalFishHook(pedestal.getBlockPos(), -1, -1, -1), new PacketDistributor.TargetPoint(pedestalPos.getX(), pedestalPos.getY(), pedestalPos.getZ(), PACKET_RANGE, pedestal.getTheWorld().getDimension().getType()));
		} else {
			PacketHandler.sendToAllAround(new PacketPedestalFishHook(pedestal.getBlockPos(), hook.posX, hook.posY, hook.posZ), new PacketDistributor.TargetPoint(pedestalPos.getX(), pedestalPos.getY(), pedestalPos.getZ(), PACKET_RANGE, pedestal.getTheWorld().getDimension().getType()));
		}
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		if (fakePlayer != null && fakePlayer.fishingBobber != null) {
			fakePlayer.fishingBobber.remove();
		}
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		if (fakePlayer != null && fakePlayer.fishingBobber != null) {
			fakePlayer.fishingBobber.remove();
			syncHookData(pedestal);
		}
	}

	private void setupFakePlayer(World world, BlockPos pos) {
		if (fakePlayer == null) {
			fakePlayer = new EntityXRFakePlayer((ServerWorld) world);
			fakePlayer.setPosition((double) pos.getX() + 0.5, (double) pos.getY() + 2, (double) pos.getZ() + 0.5);
		}
	}

	private int getTicksCatchable(@Nullable FishingBobberEntity hook) {
		return ObfuscationReflectionHelper.getPrivateValue(FishingBobberEntity.class, hook, "field_146045_ax");
	}

	private void setPitchYaw(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		double degree = 180 / Math.PI;
		double dx = fakePlayer.posX - (x + 0.5);
		double dy = fakePlayer.posY - y;
		double dz = fakePlayer.posZ - (z + 0.5);
		fakePlayer.rotationYaw = (float) -((Math.atan2(dx, dz) * degree) + 180);
		fakePlayer.rotationPitch = (float) (Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * degree);
	}
}
