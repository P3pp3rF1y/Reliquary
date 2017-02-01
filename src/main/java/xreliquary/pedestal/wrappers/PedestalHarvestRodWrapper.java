package xreliquary.pedestal.wrappers;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.blocks.BlockFertileLilypad;
import xreliquary.init.ModItems;
import xreliquary.items.ItemHarvestRod;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class PedestalHarvestRodWrapper implements IPedestalActionItemWrapper {

	private static final int NO_JOB_COOL_DOWN_CYCLES = 10;
	private static final ItemHarvestRod harvestRod = ModItems.harvestRod;

	private int hoeCoolDown = 0;
	private int plantCoolDown = 0;
	private int boneMealCoolDown = 0;
	private int breakCoolDown = 0;

	private Queue<BlockPos> queueToHoe;
	private Queue<BlockPos> queueToPlant;
	private Queue<BlockPos> queueToBoneMeal;
	private Queue<BlockPos> queueToBreak;

	public PedestalHarvestRodWrapper() {
		queueToHoe = new ArrayDeque<>();
		queueToPlant = new ArrayDeque<>();
		queueToBoneMeal = new ArrayDeque<>();
		queueToBreak = new ArrayDeque<>();
	}

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		EntityPlayer player = pedestal.getFakePlayer();
		int range = Settings.HarvestRod.pedestalRange;
		int cooldown = Settings.HarvestRod.pedestalCooldown;

		hoeLand(world, player, pos, range);

		plantSeeds(world, player, pos, stack, range);

		boneMealCrops(world, player, pos, stack, range);

		breakCrops(world, pos, stack, range);

		pedestal.setActionCoolDown(cooldown);
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
	}

	private void breakCrops(World world, BlockPos pos, ItemStack stack, int range) {
		if(breakCoolDown > 0) {
			breakCoolDown--;
		} else {
			BlockPos nextBlockToBreak = getNextBlockToBreak(world, pos, range);

			if(nextBlockToBreak != null) {
				doHarvestBlockBreak(world, stack, nextBlockToBreak);
				return;
			}
			breakCoolDown = NO_JOB_COOL_DOWN_CYCLES;
		}
	}

	public void doHarvestBlockBreak(World world, ItemStack ist, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		List<ItemStack> drops = block.getDrops(world, pos, blockState, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ist));
		Random rand = new Random();

		for(ItemStack stack : drops) {
			float f = 0.7F;
			double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
			entityitem.setPickupDelay(10);
			world.spawnEntityInWorld(entityitem);
		}

		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	private void boneMealCrops(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {

		if(boneMealCoolDown > 0) {
			boneMealCoolDown--;
		} else {
			if(harvestRod.getBoneMealCount(stack) >= (harvestRod.getBonemealCost())) {
				BlockPos blockToBoneMeal = getNextBlockToBoneMeal(world, pos, range);

				if(blockToBoneMeal != null) {
					boneMealBlock(stack, player, world, blockToBoneMeal, EnumFacing.UP);
					return;
				}
			}
			boneMealCoolDown = NO_JOB_COOL_DOWN_CYCLES;
		}
	}

	private void boneMealBlock(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		ItemStack fakeItemStack = new ItemStack(Items.DYE, 1, Reference.WHITE_DYE_META);
		ItemDye fakeItemDye = (ItemDye) fakeItemStack.getItem();

		boolean boneMealUsed = false;
		for(int repeatedUses = 0; repeatedUses <= harvestRod.getLuckRolls(); repeatedUses++) {
			if(repeatedUses == 0 || world.rand.nextInt(100) <= harvestRod.getLuckPercent()) {
				if(fakeItemDye.onItemUse(fakeItemStack, player, world, pos, EnumHand.MAIN_HAND, side, 0, 0, 0) == EnumActionResult.SUCCESS) {
					boneMealUsed = true;
				}
			}
		}
		if(boneMealUsed)
			harvestRod.setBoneMealCount(ist, harvestRod.getBoneMealCount(ist) - harvestRod.getBonemealCost());
	}

	private void plantSeeds(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {

		if(plantCoolDown > 0) {
			plantCoolDown--;
		} else {
			byte plantableSlot = 1;

			if(harvestRod.getCountPlantable(stack) > 0) {
				int quantity = harvestRod.getPlantableQuantity(stack, plantableSlot);

				if(quantity > 0) {
					BlockPos blockToPlantOn = getNextBlockToPlantOn(world, pos, range, (IPlantable) harvestRod.getPlantableInSlot(stack, plantableSlot).getItem());

					if(blockToPlantOn != null) {
						plantItem(world, player, blockToPlantOn, stack, plantableSlot);
						return;
					}
				}
			}
			plantCoolDown = NO_JOB_COOL_DOWN_CYCLES;
		}
	}

	private void plantItem(World world, EntityPlayer player, BlockPos pos, ItemStack stack, byte idx) {
		ItemStack fakePlantableStack = harvestRod.getPlantableInSlot(stack, idx).copy();
		fakePlantableStack.stackSize = 1;

		if(fakePlantableStack.onItemUse(player, world, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS) {
			harvestRod.setPlantableQuantity(stack, idx, harvestRod.getPlantableQuantity(stack, idx) - 1);
			harvestRod.shiftModeOnEmptyPlantable(stack, idx);
		}
	}

	private void hoeLand(World world, EntityPlayer player, BlockPos pos, int range) {
		if(hoeCoolDown > 0) {
			hoeCoolDown--;
		} else {
			ItemStack fakeHoe = new ItemStack(Items.WOODEN_HOE);
			BlockPos blockToHoe = getNextBlockToHoe(world, pos, range);
			if(blockToHoe != null) {
				Items.WOODEN_HOE.onItemUse(fakeHoe, player, world, blockToHoe, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
			} else {
				hoeCoolDown = NO_JOB_COOL_DOWN_CYCLES;
			}
		}
	}

	//TODO: change these to capabilities in 1.9 and use Harvest Rod methods

	private BlockPos getNextBlockToBreak(World world, BlockPos pos, int range) {
		if(queueToBreak.isEmpty()) {
			fillQueueToBreak(world, pos, range);
		}
		return queueToBreak.poll();

	}

	private void fillQueueToBreak(World world, BlockPos pos, int range) {
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState state = world.getBlockState(currentPos);
					Block block = state.getBlock();
					if(block instanceof IPlantable || block instanceof BlockCrops || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN) {
						if(block instanceof BlockFertileLilypad || block == Blocks.PUMPKIN_STEM || block == Blocks.MELON_STEM)
							continue;
						if(block instanceof BlockCrops && ((BlockCrops) block).canGrow(world, pos, state, false))
							continue;
						if(block instanceof BlockNetherWart && state.getValue(BlockNetherWart.AGE) < 3)
							continue;

						queueToBreak.add(currentPos);
					}
				}
			}
		}
	}

	private BlockPos getNextBlockToHoe(World world, BlockPos pos, int range) {
		if(queueToHoe.isEmpty())
			fillQueueToHoe(world, pos, range);

		return queueToHoe.poll();
	}

	private void fillQueueToHoe(World world, BlockPos pos, int range) {
		queueToHoe.clear();
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);

					IBlockState blockState = world.getBlockState(currentPos);
					Block block = blockState.getBlock();

					if(world.isAirBlock(currentPos.up())) {
						if(block == Blocks.GRASS || (block == Blocks.DIRT && (blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT || blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.COARSE_DIRT))) {
							queueToHoe.add(currentPos);
						}
					}
				}
			}
		}
	}

	private BlockPos getNextBlockToPlantOn(World world, BlockPos pos, int range, IPlantable plantable) {
		if(queueToPlant.isEmpty())
			fillQueueToPlant(world, pos, range, plantable);

		return queueToPlant.poll();
	}

	private void fillQueueToPlant(World world, BlockPos pos, int range, IPlantable plantable) {
		queueToPlant.clear();

		boolean checkerboard = false;
		boolean bothOddOrEven = false;

		if(plantable == Items.PUMPKIN_SEEDS || plantable == Items.MELON_SEEDS) {
			checkerboard = true;
			boolean xEven = pos.getX() % 2 == 0;
			boolean zEven = pos.getZ() % 2 == 0;
			bothOddOrEven = xEven == zEven;
		}

		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					if((!checkerboard || (bothOddOrEven == ((currentPos.getX() % 2 == 0) == (currentPos.getZ() % 2 == 0)))) && blockState.getBlock().canSustainPlant(blockState, world, pos, EnumFacing.UP, plantable) && world.isAirBlock(currentPos.up())) {
						queueToPlant.add(currentPos);
					}
				}
			}
		}
	}

	private BlockPos getNextBlockToBoneMeal(World world, BlockPos pos, int range) {
		if(queueToBoneMeal.isEmpty())
			fillQueueToBoneMeal(world, pos, range);

		return queueToBoneMeal.poll();
	}

	private void fillQueueToBoneMeal(World world, BlockPos pos, int range) {
		queueToBoneMeal.clear();
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					if(blockState.getBlock() != Blocks.GRASS && blockState.getBlock() instanceof IGrowable && ((IGrowable) blockState.getBlock()).canGrow(world, currentPos, blockState, world.isRemote)) {
						queueToBoneMeal.add(currentPos);
					}
				}
			}
		}
	}

}
