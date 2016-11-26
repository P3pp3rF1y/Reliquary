package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockFertileLilypad extends BlockBush {
	private static final AxisAlignedBB LILY_PAD_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.09375D, 0.9375D);

	@Nonnull
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Water;
	}

	public BlockFertileLilypad() {
		this.setTickRandomly(false);
		BlockBase.init(this, Names.Blocks.FERTILE_LILYPAD);
	}

	@Override
	public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random par5Random) {

		this.growCropsNearby(world, pos, state);
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY(), pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.0D, 0.9D, 0.5D);
	}

	private int secondsBetweenGrowthTicks() {
		return Settings.FertileLilypad.secondsBetweenGrowthTicks;
	}

	private int tileRange() {
		return Settings.FertileLilypad.tileRange;
	}

	private int fullPotencyRange() {
		return Settings.FertileLilypad.fullPotencyRange;
	}

	private void growCropsNearby(World world, BlockPos pos, IBlockState state) {
		int xO = pos.getX();
		int yO = pos.getY();
		int zO = pos.getZ();

		for(int xD = -tileRange(); xD <= tileRange(); xD++) {
			for(int yD = -1; yD <= tileRange(); yD++) {
				for(int zD = -tileRange(); zD <= tileRange(); zD++) {
					int x = xO + xD;
					int y = yO + yD;
					int z = zO + zD;

					double distance = Math.sqrt(Math.pow(x - xO, 2) + Math.pow(y - yO, 2) + Math.pow(z - zO, 2));
					distance -= fullPotencyRange();
					distance = Math.min(1D, distance);
					double distanceCoefficient = 1D - (distance / tileRange());

					IBlockState cropState = world.getBlockState(new BlockPos(x, y, z));
					Block cropBlock = cropState.getBlock();

					if(cropBlock instanceof IPlantable || cropBlock instanceof IGrowable) {
						if(!(cropBlock instanceof BlockFertileLilypad)) {
							//it schedules the next tick.
							world.scheduleBlockUpdate(new BlockPos(x, y, z), cropBlock, (int) (distanceCoefficient * (float) secondsBetweenGrowthTicks() * 20F), 1);
							cropBlock.updateTick(world, new BlockPos(x, y, z), cropState, world.rand);
						}
					}
				}
			}
		}
		world.scheduleBlockUpdate(pos, state.getBlock(), secondsBetweenGrowthTicks() * 20, 1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state,
			@Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB mask, @Nonnull List<AxisAlignedBB> list, Entity collidingEntity) {
		if(!(collidingEntity instanceof EntityBoat)) {
			addCollisionBoxToList(pos, mask, list, LILY_PAD_AABB);
		}
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == Blocks.WATER;
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		IBlockState blockBelowState = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
		return pos.getY() >= 0 && pos.getY() < 256 && blockBelowState.getMaterial() == Material.WATER && blockBelowState.getValue(BlockLiquid.LEVEL) == 0;
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return LILY_PAD_AABB;
	}
}
