package xreliquary.items.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Settings;

public class FertileLilyPadItem extends BlockItemBase {
	public FertileLilyPadItem() {
		super(ModBlocks.FERTILE_LILYPAD, new Properties().rarity(Rarity.EPIC));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);

		ItemStack stack = player.getHeldItem(hand);
		if (tryPlacingLilyPad(stack, world, player, raytraceresult)) {
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		return new ActionResult<>(ActionResultType.FAIL, stack);
	}

	private boolean tryPlacingLilyPad(ItemStack itemStack, World world, PlayerEntity playerIn, RayTraceResult result) {
		if (result.getType() == RayTraceResult.Type.MISS) {
			return true;
		} else {
			if (result.getType() == RayTraceResult.Type.BLOCK) {
				BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) result;
				BlockPos blockpos = blockraytraceresult.getPos();
				Direction direction = blockraytraceresult.getFace();
				if (!world.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(direction), direction, itemStack)) {
					return false;
				}

				BlockPos posUp = blockpos.up();
				BlockState blockstate = world.getBlockState(blockpos);
				Material material = blockstate.getMaterial();
				IFluidState ifluidstate = world.getFluidState(blockpos);
				if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && world.isAirBlock(posUp)) {
					return placeAndShrinkStack(itemStack, world, playerIn, blockpos, posUp);
				}
			}
		}
		return false;
	}

	private boolean placeAndShrinkStack(ItemStack itemStack, World worldIn, PlayerEntity playerIn, BlockPos blockpos, BlockPos posUp) {
		// special case for handling block placement with water lilies
		net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, posUp);
		worldIn.setBlockState(posUp, ModBlocks.FERTILE_LILYPAD.getDefaultState(), 11);
		if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot, Direction.UP)) {
			blocksnapshot.restore(true, false);
			return false;
		}

		int secondsBetweenGrowthTicks = Settings.COMMON.blocks.fertileLilypad.secondsBetweenGrowthTicks.get();
		worldIn.getPendingBlockTicks().scheduleTick(posUp, ModBlocks.FERTILE_LILYPAD, secondsBetweenGrowthTicks * 20);

		if (playerIn instanceof ServerPlayerEntity) {
			CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerIn, posUp, itemStack);
		}

		if (!playerIn.abilities.isCreativeMode) {
			itemStack.shrink(1);
		}

		playerIn.addStat(Stats.ITEM_USED.get(this));
		worldIn.playSound(playerIn, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		return true;
	}
}
