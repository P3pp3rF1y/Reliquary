package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemInfernalChalice extends ItemToggleable implements IFluidContainerItem {
	public ItemInfernalChalice() {
		super(Names.infernal_chalice);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
		//String fluid = "lava.";
		String amount = Integer.toString(NBTHelper.getInteger("fluidStacks", ist));
		this.formatTooltip(ImmutableMap.of("amount", amount), ist, list);
	}

	protected int fluidLimit() {
		return Settings.InfernalChalice.fluidLimit;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking()) {
			return super.onItemRightClick(stack, world, player, hand);
		}

		float movementThresholdCoefficient = 1.0F;
		double xOffset = player.prevPosX + (player.posX - player.prevPosX) * movementThresholdCoefficient;
		double yOffset = player.prevPosY + (player.posY - player.prevPosY) * movementThresholdCoefficient + player.getEyeHeight();
		double zOffset = player.prevPosZ + (player.posZ - player.prevPosZ) * movementThresholdCoefficient;
		boolean isInDrainMode = this.isEnabled(stack);
		RayTraceResult result = this.getMovingObjectPositionFromPlayer(world, player, isInDrainMode);

		if(result == null) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		} else {
			if(result.typeOfHit == RayTraceResult.Type.BLOCK) {

				if(!world.isBlockModifiable(player, result.getBlockPos()))
					return new ActionResult<>(EnumActionResult.PASS, stack);

				if(!player.canPlayerEdit(result.getBlockPos(), result.sideHit, stack))
					return new ActionResult<>(EnumActionResult.PASS, stack);

				//fluid handler support!
				if(this.isEnabled(stack) && NBTHelper.getInteger("fluidStacks", stack) + 1000 <= fluidLimit()) {
					TileEntity tile = world.getTileEntity(result.getBlockPos());
					if(tile instanceof IFluidHandler) {
						FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
						FluidStack simulatedDrainedFluid = ((IFluidHandler) tile).drain(result.sideHit, fluid, false);
						if(simulatedDrainedFluid.amount == 1000) {
							NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) - 1000);
							return new ActionResult<>(EnumActionResult.SUCCESS, stack);
						}

						return new ActionResult<>(EnumActionResult.FAIL, stack);
					}
				} else {
					TileEntity tile = world.getTileEntity(result.getBlockPos());
					if(tile instanceof IFluidHandler && NBTHelper.getInteger("fluidStacks", stack) >= 1000) {
						FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
						int amount = ((IFluidHandler) tile).fill(result.sideHit, fluid, false);

						if(amount == 1000) {
							((IFluidHandler) tile).fill(result.sideHit, fluid, true);
							NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) - 1000);
							return new ActionResult<>(EnumActionResult.SUCCESS, stack);
						}

						return new ActionResult<>(EnumActionResult.FAIL, stack);
					}
				}

				String ident = RegistryHelper.getBlockRegistryName(world.getBlockState(result.getBlockPos()).getBlock());
				if(this.isEnabled(stack) && (ident.equals(RegistryHelper.getBlockRegistryName(Blocks.flowing_lava)) || ident.equals(RegistryHelper.getBlockRegistryName(Blocks.lava))) && world.getBlockState(result.getBlockPos()).getValue(Blocks.lava.LEVEL) == 0) {
					world.setBlockState(result.getBlockPos(), Blocks.air.getDefaultState());
					NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) + 1000);
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}

				if(!this.isEnabled(stack) && (NBTHelper.getInteger("fluidStacks", stack) >= 1000 || player.capabilities.isCreativeMode)) {
					BlockPos adjustedPos = result.getBlockPos().offset(result.sideHit);

					if(!player.canPlayerEdit(adjustedPos, result.sideHit, stack))
						return new ActionResult<>(EnumActionResult.PASS, stack);

					if(this.tryPlaceContainedLiquid(world, stack, xOffset, yOffset, zOffset, adjustedPos) && !player.capabilities.isCreativeMode) {
						NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) - 1000);
						return new ActionResult<>(EnumActionResult.SUCCESS, stack);
					}

				}
			}

			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
	}

	public boolean tryPlaceContainedLiquid(World world, ItemStack ist, double par2, double par4, double par6, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Material material = blockState.getBlock().getMaterial(blockState);
		if(!world.isAirBlock(pos) && material.isSolid())
			return false;
		else {

			world.setBlockState(pos, Blocks.flowing_lava.getDefaultState(), 3);
			return true;
		}
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		if(this.isEnabled(container))
			return null;
		return new FluidStack(FluidRegistry.LAVA, NBTHelper.getInteger("fluidStacks", container));
	}

	@Override
	public int getCapacity(ItemStack container) {
		if(this.isEnabled(container))
			return fluidLimit() - NBTHelper.getInteger("fluidStacks", container);

		return fluidLimit();
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if(!this.isEnabled(container) || resource == null) {
			return 0;
		}

		if(!resource.isFluidEqual(new FluidStack(FluidRegistry.LAVA, 1000)))
			return 0;

		int toFill = Math.min(fluidLimit() - NBTHelper.getInteger("fluidStacks", container), resource.amount);

		if(doFill) {
			int fluidLevel = NBTHelper.getInteger("fluidStacks", container);
			fluidLevel += toFill;
			NBTHelper.setInteger("fluidStacks", container, fluidLevel);
		}

		return toFill;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		if(this.isEnabled(container)) {
			return null;
		}

		FluidStack stack = new FluidStack(FluidRegistry.LAVA, Math.min(NBTHelper.getInteger("fluidStacks", container), maxDrain));

		if(doDrain) {
			NBTHelper.setInteger("fluidStacks", container, NBTHelper.getInteger("fluidStacks", container) - stack.amount);
		}

		return stack;
	}
}
