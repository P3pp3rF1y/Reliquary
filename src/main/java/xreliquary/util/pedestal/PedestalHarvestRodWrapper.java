package xreliquary.util.pedestal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

import java.util.Queue;

public class PedestalHarvestRodWrapper implements IPedestalActionItemWrapper {

	private Queue<BlockPos> queueToHoe;
	private Queue<BlockPos> queueToPlant;
	private Queue<BlockPos> queueToBoneMeal;
	private Queue<BlockPos> queueToBreak;

	@Override

	public void update(ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		int range = Settings.HarvestRod.pedestalRange;
		int cooldown = Settings.HarvestRod.pedestalCooldown;

		hoeLand(pos);

/*		plantSeeds();

		boneMealCrops();

		breakCrops();*/

	}

	private void hoeLand(BlockPos pos) {

	}


}
