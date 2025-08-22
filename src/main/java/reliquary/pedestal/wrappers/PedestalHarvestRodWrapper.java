package reliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItemWrapper;
import reliquary.block.FertileLilyPadBlock;
import reliquary.init.ModItems;
import reliquary.item.HarvestRodItem;
import reliquary.reference.Config;
import reliquary.util.ItemHelper;

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
		BlockPos pos = pedestal.getBlockPosition();
		int cooldown = Config.COMMON.items.harvestRod.pedestalCooldown.get();
		pedestal.getFakePlayer().ifPresent(fakePlayer -> {
			int range = Config.COMMON.items.harvestRod.pedestalRange.get();

			hoeLand(level, fakePlayer, pos, range);

			plantSeeds(level, fakePlayer, pos, stack, range);

			boneMealCrops(level, fakePlayer, pos, stack, range);

			breakCrops(level, fakePlayer, pos, stack, range);

		});
		pedestal.setActionCoolDown(cooldown);
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	private void breakCrops(Level level, Player player, BlockPos pos, ItemStack stack, int range) {
		if (breakCoolDown > 0) {
			breakCoolDown--;
		} else {
			if (!breakNext(level, player, pos, stack, range)) {
				breakCoolDown = NO_JOB_COOL_DOWN_CYCLES;
			}
		}
	}

	private boolean breakNext(Level level, Player player, BlockPos pos, ItemStack stack, int range) {
		return getNextBlockToBreak(level, pos, range).map(nextBlockToBreak -> {
			doHarvestBlockBreak(level, player, stack, nextBlockToBreak);
			return true;
		}).orElse(false);
	}

	private void doHarvestBlockBreak(Level level, Player player, ItemStack stack, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel) level, pos, null, player, stack);
		for (ItemStack drop : drops) {
			float f = 0.7F;
			double d = (level.random.nextFloat() * f) + (1.0F - f) * 0.5D;
			double d1 = (level.random.nextFloat() * f) + (1.0F - f) * 0.5D;
			double d2 = (level.random.nextFloat() * f) + (1.0F - f) * 0.5D;
			ItemEntity entityitem = new ItemEntity(level, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, drop);
			entityitem.setPickUpDelay(10);
			level.addFreshEntity(entityitem);
		}

		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
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

	private boolean boneMealNext(Level level, FakePlayer fakePlayer, BlockPos pos, ItemStack stack, int range) {
		return getNextBlockToBoneMeal(level, pos, range).map(blockToBoneMeal -> {
			boneMealBlock(stack, fakePlayer, level, blockToBoneMeal);
			return true;
		}).orElse(false);
	}

	private void boneMealBlock(ItemStack stack, FakePlayer fakePlayer, Level level, BlockPos pos) {
		ItemStack fakeItemStack = new ItemStack(Items.BONE_MEAL);

		boolean boneMealUsed = false;
		for (int repeatedUses = 0; repeatedUses <= harvestRod.getLuckRolls(); repeatedUses++) {
			if ((repeatedUses == 0 || level.random.nextInt(100) <= harvestRod.getLuckPercent()) && BoneMealItem.applyBonemeal(fakeItemStack, level, pos, fakePlayer)) {
				boneMealUsed = true;
			}
		}
		if (boneMealUsed) {
			level.levelEvent(2005, pos, 0);
			harvestRod.setBoneMealCount(stack, harvestRod.getBoneMealCount(stack) - harvestRod.getBonemealCost());
		}
	}

	private void plantSeeds(Level level, Player player, BlockPos pos, ItemStack stack, int range) {
		if (plantCoolDown > 0) {
			plantCoolDown--;
		} else {
			byte plantableSlot = 1;
			if (harvestRod.getCountOfPlantables(stack) > 0) {
				int quantity = harvestRod.getPlantableQuantity(stack, plantableSlot);

				if (quantity > 0 && plantNext(level, player, pos, stack, range, plantableSlot)) {
					return;
				}
			}
			plantCoolDown = NO_JOB_COOL_DOWN_CYCLES;
		}
	}

	private boolean plantNext(Level level, Player player, BlockPos pos, ItemStack stack, int range, byte plantableSlot) {
		return getNextBlockToPlantOn(level, pos, range, harvestRod.getPlantableInSlot(stack, plantableSlot)).map(blockToPlantOn -> {
			plantItem(player, blockToPlantOn, stack, plantableSlot);
			return true;
		}).orElse(false);
	}

	private void plantItem(Player player, BlockPos pos, ItemStack stack, byte idx) {
		ItemStack fakePlantableStack = harvestRod.getPlantableInSlot(stack, idx).copy();
		fakePlantableStack.setCount(1);
		player.setItemInHand(InteractionHand.MAIN_HAND, fakePlantableStack);

		if (fakePlantableStack.useOn(ItemHelper.getItemUseContext(pos, player)).consumesAction()) {
			harvestRod.useCharge(stack, idx, 1);
		}
	}

	private void hoeLand(Level level, Player player, BlockPos pos, int range) {
		if (hoeCoolDown > 0) {
			hoeCoolDown--;
		} else {
			if (!hoeNext(level, player, pos, range)) {
				hoeCoolDown = NO_JOB_COOL_DOWN_CYCLES;
			}
		}
	}

	private boolean hoeNext(Level level, Player player, BlockPos pos, int range) {
		return getNextBlockToHoe(level, pos, range).map(blockToHoe -> {
			ItemStack fakeHoe = new ItemStack(Items.WOODEN_HOE);
			player.setItemInHand(InteractionHand.MAIN_HAND, fakeHoe);
			Items.WOODEN_HOE.useOn(ItemHelper.getItemUseContext(blockToHoe, player));
			return true;
		}).orElse(false);
	}

	private Optional<BlockPos> getNextBlockToBreak(Level level, BlockPos pos, int range) {
		if (queueToBreak.isEmpty()) {
			fillQueueToBreak(level, pos, range);
		}
		return Optional.ofNullable(queueToBreak.poll());

	}

	private void fillQueueToBreak(Level level, BlockPos pos, int range) {
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState state = level.getBlockState(currentPos);
					Block block = state.getBlock();
					if (state.is(BlockTags.CROPS) || block instanceof BushBlock || block == Blocks.MELON || block == Blocks.PUMPKIN) {
						if (block instanceof FertileLilyPadBlock || block == Blocks.PUMPKIN_STEM || block == Blocks.MELON_STEM
								|| block instanceof CropBlock cropBlock && cropBlock.isValidBonemealTarget(level, currentPos, state)
								|| block instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) < 3
								|| block instanceof SweetBerryBushBlock && state.getValue(SweetBerryBushBlock.AGE) < 3) {
							return;
						}

						queueToBreak.add(currentPos);
					}
				});
	}

	private Optional<BlockPos> getNextBlockToHoe(Level level, BlockPos pos, int range) {
		if (queueToHoe.isEmpty()) {
			fillQueueToHoe(level, pos, range);
		}

		return Optional.ofNullable(queueToHoe.poll());
	}

	private void fillQueueToHoe(Level level, BlockPos pos, int range) {
		queueToHoe.clear();
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState blockState = level.getBlockState(currentPos);
					Block block = blockState.getBlock();

					if (level.isEmptyBlock(currentPos.above()) && (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT)) {
						queueToHoe.add(currentPos);
					}
				}
		);
	}

	private Optional<BlockPos> getNextBlockToPlantOn(Level level, BlockPos pos, int range, ItemStack plantable) {
		if (queueToPlant.isEmpty()) {
			fillQueueToPlant(level, pos, range, plantable);
		}

		return Optional.ofNullable(queueToPlant.poll());
	}

	private void fillQueueToPlant(Level level, BlockPos pos, int range, ItemStack plantable) {
		queueToPlant.clear();

		boolean checkerboard = false;
		boolean bothOddOrEven = false;

		if (plantable.getItem() == Items.PUMPKIN_SEEDS || plantable.getItem() == Items.MELON_SEEDS) {
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
					BlockState blockState = level.getBlockState(currentPos);
					if ((!finalCheckerboard || (finalBothOddOrEven == ((currentPos.getX() % 2 == 0) == (currentPos.getZ() % 2 == 0))))
							&& HarvestRodItem.canPlacePlantableAt(level, currentPos.above(), plantable) && level.isEmptyBlock(currentPos.above())) {
						queueToPlant.add(currentPos);
					}
				});

	}

	private Optional<BlockPos> getNextBlockToBoneMeal(Level level, BlockPos pos, int range) {
		if (queueToBoneMeal.isEmpty()) {
			fillQueueToBoneMeal(level, pos, range);
		}

		return Optional.ofNullable(queueToBoneMeal.poll());
	}

	private void fillQueueToBoneMeal(Level level, BlockPos pos, int range) {
		queueToBoneMeal.clear();
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range)).forEach(
				p -> {
					BlockPos currentPos = p.immutable();
					BlockState blockState = level.getBlockState(currentPos);
					if (blockState.getBlock() != Blocks.GRASS_BLOCK && blockState.getBlock() instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(level, currentPos, blockState)) {
						queueToBoneMeal.add(currentPos);
					}
				});
	}
}
