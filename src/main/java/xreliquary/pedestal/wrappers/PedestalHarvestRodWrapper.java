package xreliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.blocks.FertileLilyPadBlock;
import xreliquary.init.ModItems;
import xreliquary.items.HarvestRodItem;
import xreliquary.reference.Settings;
import xreliquary.util.ItemHelper;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class PedestalHarvestRodWrapper implements IPedestalActionItemWrapper {

	private static final int NO_JOB_COOL_DOWN_CYCLES = 10;
	private static final HarvestRodItem harvestRod = ModItems.HARVEST_ROD.get();

	private int hoeCoolDown = 0;
	private int plantCoolDown = 0;
	private int boneMealCoolDown = 0;
	private int breakCoolDown = 0;

	private final Queue<BlockPos> queueToHoe;
	private final Queue<BlockPos> queueToPlant;
	private final Queue<BlockPos> queueToBoneMeal;
	private final Queue<BlockPos> queueToBreak;

	public PedestalHarvestRodWrapper() {
		queueToHoe = new ArrayDeque<>();
		queueToPlant = new ArrayDeque<>();
		queueToBoneMeal = new ArrayDeque<>();
		queueToBreak = new ArrayDeque<>();
	}

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPos();
		int cooldown = Settings.COMMON.items.harvestRod.pedestalCooldown.get();
		pedestal.getFakePlayer().ifPresent(fakePlayer -> {
			int range = Settings.COMMON.items.harvestRod.pedestalRange.get();

			hoeLand(level, fakePlayer, pos, range);

			plantSeeds(level, fakePlayer, pos, stack, range);

			boneMealCrops(level, fakePlayer, pos, stack, range);

			breakCrops(level, fakePlayer, pos, stack, range);

		});
		pedestal.setActionCoolDown(cooldown);
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		if (!level.isClientSide) {
			harvestRod.updateContainedStacks(stack);
		}
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	private void breakCrops(Level world, Player player, BlockPos pos, ItemStack stack, int range) {
		if (breakCoolDown > 0) {
			breakCoolDown--;
		} else {
			if (!breakNext(world, player, pos, stack, range)) {
				breakCoolDown = NO_JOB_COOL_DOWN_CYCLES;
			}
		}
	}

	private boolean breakNext(Level world, Player player, BlockPos pos, ItemStack stack, int range) {
		return getNextBlockToBreak(world, pos, range).map(nextBlockToBreak -> {
			doHarvestBlockBreak(world, player, stack, nextBlockToBreak);
			return true;
		}).orElse(false);
	}

	private void doHarvestBlockBreak(Level world, Player player, ItemStack stack, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel) world, pos, null, player, stack);
		for (ItemStack drop : drops) {
			float f = 0.7F;
			double d = (world.random.nextFloat() * f) + (1.0F - f) * 0.5D;
			double d1 = (world.random.nextFloat() * f) + (1.0F - f) * 0.5D;
			double d2 = (world.random.nextFloat() * f) + (1.0F - f) * 0.5D;
			ItemEntity entityitem = new ItemEntity(world, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, drop);
			entityitem.setPickUpDelay(10);
			world.addFreshEntity(entityitem);
		}

		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	private void boneMealCrops(Level level, FakePlayer fakePlayer, BlockPos pos, ItemStack stack, int range) {
		if (boneMealCoolDown > 0) {
			boneMealCoolDown--;
		} else {
			if (harvestRod.getBoneMealCount(stack) >= (harvestRod.getBonemealCost()) && boneMealNext(level, fakePlayer, pos, stack, range)) {
				return;
			}
			boneMealCoolDown = NO_JOB_COOL_DOWN_CYCLES;
		}
	}

	private boolean boneMealNext(Level world, FakePlayer fakePlayer, BlockPos pos, ItemStack stack, int range) {
		return getNextBlockToBoneMeal(world, pos, range).map(blockToBoneMeal -> {
			boneMealBlock(stack, fakePlayer, world, blockToBoneMeal);
			return true;
		}).orElse(false);
	}

	private void boneMealBlock(ItemStack stack, FakePlayer fakePlayer, Level world, BlockPos pos) {
		ItemStack fakeItemStack = new ItemStack(Items.BONE_MEAL);

		boolean boneMealUsed = false;
		for (int repeatedUses = 0; repeatedUses <= harvestRod.getLuckRolls(); repeatedUses++) {
			if ((repeatedUses == 0 || world.random.nextInt(100) <= harvestRod.getLuckPercent()) && BoneMealItem.applyBonemeal(fakeItemStack, world, pos, fakePlayer)) {
				boneMealUsed = true;
			}
		}
		if (boneMealUsed) {
			world.levelEvent(2005, pos, 0);
			harvestRod.setBoneMealCount(stack, harvestRod.getBoneMealCount(stack) - harvestRod.getBonemealCost());
		}
	}

	private void plantSeeds(Level world, Player player, BlockPos pos, ItemStack stack, int range) {
		if (plantCoolDown > 0) {
			plantCoolDown--;
		} else {
			byte plantableSlot = 1;
			if (harvestRod.getCountPlantable(stack) > 0) {
				harvestRod.clearPlantableIfNoLongerValid(stack, plantableSlot);
				int quantity = harvestRod.getPlantableQuantity(stack, plantableSlot);

				if (quantity > 0 && plantNext(world, player, pos, stack, range, plantableSlot)) {
					return;
				}
			}
			plantCoolDown = NO_JOB_COOL_DOWN_CYCLES;
		}
	}

	private boolean plantNext(Level world, Player player, BlockPos pos, ItemStack stack, int range, byte plantableSlot) {
		return getNextBlockToPlantOn(world, pos, range, (IPlantable) ((BlockItem) harvestRod.getPlantableInSlot(stack, plantableSlot).getItem()).getBlock()).map(blockToPlantOn -> {
			plantItem(player, blockToPlantOn, stack, plantableSlot);
			return true;
		}).orElse(false);
	}

	private void plantItem(Player player, BlockPos pos, ItemStack stack, byte idx) {
		ItemStack fakePlantableStack = harvestRod.getPlantableInSlot(stack, idx).copy();
		fakePlantableStack.setCount(1);
		player.setItemInHand(InteractionHand.MAIN_HAND, fakePlantableStack);

		if (fakePlantableStack.useOn(ItemHelper.getItemUseContext(pos, player)) == InteractionResult.SUCCESS) {
			harvestRod.setPlantableQuantity(stack, idx, harvestRod.getPlantableQuantity(stack, idx) - 1);
		}
	}

	private void hoeLand(Level world, Player player, BlockPos pos, int range) {
		if (hoeCoolDown > 0) {
			hoeCoolDown--;
		} else {
			if (!hoeNext(world, player, pos, range)) {
				hoeCoolDown = NO_JOB_COOL_DOWN_CYCLES;
			}
		}
	}

	private boolean hoeNext(Level world, Player player, BlockPos pos, int range) {
		return getNextBlockToHoe(world, pos, range).map(blockToHoe -> {
			ItemStack fakeHoe = new ItemStack(Items.WOODEN_HOE);
			player.setItemInHand(InteractionHand.MAIN_HAND, fakeHoe);
			Items.WOODEN_HOE.useOn(ItemHelper.getItemUseContext(blockToHoe, player));
			return true;
		}).orElse(false);
	}

	private Optional<BlockPos> getNextBlockToBreak(Level world, BlockPos pos, int range) {
		if (queueToBreak.isEmpty()) {
			fillQueueToBreak(world, pos, range);
		}
		return Optional.ofNullable(queueToBreak.poll());

	}

	private void fillQueueToBreak(Level world, BlockPos pos, int range) {
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState state = world.getBlockState(currentPos);
					Block block = state.getBlock();
					if (block instanceof IPlantable || block == Blocks.MELON || block == Blocks.PUMPKIN) {
						if (block instanceof FertileLilyPadBlock || block == Blocks.PUMPKIN_STEM || block == Blocks.MELON_STEM
								|| block instanceof CropBlock cropBlock && cropBlock.isValidBonemealTarget(world, currentPos, state, false)
								|| block instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) < 3
								|| block instanceof SweetBerryBushBlock && state.getValue(SweetBerryBushBlock.AGE) < 3) {
							return;
						}

						queueToBreak.add(currentPos);
					}
				});
	}

	private Optional<BlockPos> getNextBlockToHoe(Level world, BlockPos pos, int range) {
		if (queueToHoe.isEmpty()) {
			fillQueueToHoe(world, pos, range);
		}

		return Optional.ofNullable(queueToHoe.poll());
	}

	private void fillQueueToHoe(Level world, BlockPos pos, int range) {
		queueToHoe.clear();
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState blockState = world.getBlockState(currentPos);
					Block block = blockState.getBlock();

					if (world.isEmptyBlock(currentPos.above()) && (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT)) {
						queueToHoe.add(currentPos);
					}
				}
		);
	}

	private Optional<BlockPos> getNextBlockToPlantOn(Level world, BlockPos pos, int range, IPlantable plantable) {
		if (queueToPlant.isEmpty()) {
			fillQueueToPlant(world, pos, range, plantable);
		}

		return Optional.ofNullable(queueToPlant.poll());
	}

	private void fillQueueToPlant(Level world, BlockPos pos, int range, IPlantable plantable) {
		queueToPlant.clear();

		boolean checkerboard = false;
		boolean bothOddOrEven = false;

		if (plantable == Items.PUMPKIN_SEEDS || plantable == Items.MELON_SEEDS) {
			checkerboard = true;
			boolean xEven = pos.getX() % 2 == 0;
			boolean zEven = pos.getZ() % 2 == 0;
			bothOddOrEven = xEven == zEven;
		}

		boolean finalCheckerboard = checkerboard;
		boolean finalBothOddOrEven = bothOddOrEven;
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState blockState = world.getBlockState(currentPos);
					if ((!finalCheckerboard || (finalBothOddOrEven == ((currentPos.getX() % 2 == 0) == (currentPos.getZ() % 2 == 0)))) && blockState.getBlock().canSustainPlant(blockState, world, pos, Direction.UP, plantable) && world.isEmptyBlock(currentPos.above())) {
						queueToPlant.add(currentPos);
					}
				});

	}

	private Optional<BlockPos> getNextBlockToBoneMeal(Level world, BlockPos pos, int range) {
		if (queueToBoneMeal.isEmpty()) {
			fillQueueToBoneMeal(world, pos, range);
		}

		return Optional.ofNullable(queueToBoneMeal.poll());
	}

	private void fillQueueToBoneMeal(Level world, BlockPos pos, int range) {
		queueToBoneMeal.clear();
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState blockState = world.getBlockState(currentPos);
					if (blockState.getBlock() != Blocks.GRASS_BLOCK && blockState.getBlock() instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(world, currentPos, blockState, world.isClientSide)) {
						queueToBoneMeal.add(currentPos);
					}
				});
	}
}
