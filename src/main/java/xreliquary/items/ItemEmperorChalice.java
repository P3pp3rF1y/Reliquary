package xreliquary.items;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;

public class ItemEmperorChalice extends ItemToggleable implements IFluidContainerItem {

	public ItemEmperorChalice() {
		super(Names.emperor_chalice);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;

	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 16;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.DRINK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		if(world.isRemote)
			return stack;

		if(!(entityLiving instanceof EntityPlayer))
			return stack;

		EntityPlayer player = (EntityPlayer) entityLiving;

		int multiplier = Settings.EmperorChalice.hungerSatiationMultiplier;
		player.getFoodStats().addStats(1, (float) (multiplier / 2));
		player.attackEntityFrom(DamageSource.drown, multiplier);
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking())
			return super.onItemRightClick(ist, world, player, hand);
		float coeff = 1.0F;
		double xOff = player.prevPosX + (player.posX - player.prevPosX) * coeff;
		double yOff = player.prevPosY + (player.posY - player.prevPosY) * coeff + player.getEyeHeight();
		double zOff = player.prevPosZ + (player.posZ - player.prevPosZ) * coeff;
		boolean isInDrainMode = this.isEnabled(ist);
		RayTraceResult result = this.getMovingObjectPositionFromPlayer(world, player, isInDrainMode);

		if(result == null) {
			if(!this.isEnabled(ist)) {
				player.setActiveHand(hand);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, ist);
		} else {

			if(result.typeOfHit == RayTraceResult.Type.BLOCK) {

				if(!world.isBlockModifiable(player, result.getBlockPos()))
					return new ActionResult<>(EnumActionResult.FAIL, ist);

				if(!player.canPlayerEdit(result.getBlockPos(), result.sideHit, ist))
					return new ActionResult<>(EnumActionResult.FAIL, ist);
				;

				if(this.isEnabled(ist)) {
					TileEntity tile = world.getTileEntity(result.getBlockPos());
					if(tile instanceof IFluidHandler) {
						//it's got infinite water.. it just drains water, nothing more.
						FluidStack fluid = new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
						((IFluidHandler) tile).drain(result.sideHit, fluid, true);

						return new ActionResult<>(EnumActionResult.SUCCESS, ist);
					}
				} else {
					TileEntity tile = world.getTileEntity(result.getBlockPos());
					if(tile instanceof IFluidHandler) {
						FluidStack fluid = new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
						int amount = ((IFluidHandler) tile).fill(result.sideHit, fluid, false);

						if(amount > 0) {
							((IFluidHandler) tile).fill(result.sideHit, fluid, true);
						}

						return new ActionResult<>(EnumActionResult.SUCCESS, ist);
					}
				}

				if(!this.isEnabled(ist)) {
					BlockPos waterPlacementPos = result.getBlockPos().offset(result.sideHit);

					if(!player.canPlayerEdit(waterPlacementPos, result.sideHit, ist))
						return new ActionResult<>(EnumActionResult.FAIL, ist);
					;

					if(this.tryPlaceContainedLiquid(world, ist, xOff, yOff, zOff, waterPlacementPos))
						return new ActionResult<>(EnumActionResult.SUCCESS, ist);

				} else {
					String ident = RegistryHelper.getBlockRegistryName(world.getBlockState(result.getBlockPos()).getBlock());
					if((ident.equals(RegistryHelper.getBlockRegistryName(Blocks.flowing_water)) || ident.equals(RegistryHelper.getBlockRegistryName(Blocks.water))) && world.getBlockState(result.getBlockPos()).getValue(Blocks.water.LEVEL) == 0) {
						world.setBlockState(result.getBlockPos(), Blocks.air.getDefaultState());

						return new ActionResult<>(EnumActionResult.SUCCESS, ist);
					}
				}
			}

			return new ActionResult<>(EnumActionResult.PASS, ist);
		}
	}

	public boolean tryPlaceContainedLiquid(World world, ItemStack stack, double posX, double posY, double posZ, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Material material = blockState.getBlock().getMaterial(blockState);

		if(this.isEnabled(stack))
			return false;
		if(!world.isAirBlock(pos) && material.isSolid())
			return false;
		else {
			if(world.provider.doesWaterVaporize()) {
				world.playSound(null, pos, SoundEvents.block_lava_extinguish, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

				for(int var11 = 0; var11 < 8; ++var11) {
					world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			} else {
				world.setBlockState(pos, Blocks.flowing_water.getDefaultState(), 3);
			}

			return true;
		}
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		if(this.isEnabled(container))
			return null;

		return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
	}

	@Override
	public int getCapacity(ItemStack container) {
		return FluidContainerRegistry.BUCKET_VOLUME;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if(!this.isEnabled(container) || resource == null) {
			return 0;
		}

		if(!resource.isFluidEqual(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME)))
			return 0;

		return FluidContainerRegistry.BUCKET_VOLUME;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		if(this.isEnabled(container))
			return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);

		return null;
	}
}
