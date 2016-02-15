package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemInfernalChalice extends ItemToggleable {
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

    protected int fluidLimit() { return Settings.InfernalChalice.fluidLimit; }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            return super.onItemRightClick(stack, world, player);
        }

        float movementThresholdCoefficient = 1.0F;
        double xOffset = player.prevPosX + (player.posX - player.prevPosX) * movementThresholdCoefficient;
        double yOffset = player.prevPosY + (player.posY - player.prevPosY) * movementThresholdCoefficient + player.getEyeHeight();
        double zOffset = player.prevPosZ + (player.posZ - player.prevPosZ) * movementThresholdCoefficient;
        boolean isInDrainMode = this.isEnabled(stack);
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, isInDrainMode);

        if (mop == null) {
            return stack;
        } else {
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

                if (!world.isBlockModifiable( player, mop.getBlockPos() ))
                    return stack;

                if (!player.canPlayerEdit(mop.getBlockPos(), mop.sideHit, stack))
                    return stack;

                //fluid handler support!
                if (this.isEnabled(stack) && NBTHelper.getInteger("fluidStacks", stack) + 1000 <= fluidLimit()) {
                    TileEntity tile = world.getTileEntity(mop.getBlockPos());
                    if (tile instanceof IFluidHandler) {
                        FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                        FluidStack simulatedDrainedFluid = ((IFluidHandler) tile).drain(mop.sideHit, fluid, false);
                        if (simulatedDrainedFluid.amount == 1000) {
                            NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) - 1000);
                        }

                        return stack;
                    }
                } else {
                    TileEntity tile = world.getTileEntity(mop.getBlockPos());
                    if (tile instanceof IFluidHandler && NBTHelper.getInteger("fluidStacks", stack) >= 1000) {
                        FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                        int amount = ((IFluidHandler) tile).fill(mop.sideHit, fluid, false);

                        if (amount == 1000) {
                            ((IFluidHandler) tile).fill(mop.sideHit, fluid, true);
                            NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) - 1000);
                        }

                        return stack;
                    }
                }

                String ident =  RegistryHelper.getBlockRegistryName(world.getBlockState(mop.getBlockPos()).getBlock());
                if (this.isEnabled(stack) && (ident.equals( RegistryHelper.getBlockRegistryName(Blocks.flowing_lava)) || ident.equals( RegistryHelper.getBlockRegistryName(Blocks.lava))) && world.getBlockState(mop.getBlockPos()).getValue(Blocks.lava.LEVEL) == 0) {
                    world.setBlockState( mop.getBlockPos(), Blocks.air.getDefaultState() );
                    NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) + 1000);
                    return stack;
                }

                if (!this.isEnabled(stack)  && NBTHelper.getInteger("fluidStacks", stack) >= 1000) {
                    BlockPos adjustedPos = mop.getBlockPos().offset(mop.sideHit);

                    if (!player.canPlayerEdit(adjustedPos, mop.sideHit, stack))
                        return stack;

                    if (this.tryPlaceContainedLiquid(world, stack, xOffset, yOffset, zOffset, adjustedPos) && !player.capabilities.isCreativeMode) {
                        NBTHelper.setInteger("fluidStacks", stack, NBTHelper.getInteger("fluidStacks", stack) - 1000);
                        return stack;
                    }

                }
            }

            return stack;
        }
    }

    public boolean tryPlaceContainedLiquid(World world, ItemStack ist, double par2, double par4, double par6, BlockPos pos) {
        Material material = world.getBlockState(pos).getBlock().getMaterial();
        if (!world.isAirBlock(pos) && material.isSolid())
            return false;
        else {

            world.setBlockState(pos, Blocks.flowing_lava.getDefaultState(), 3);
            return true;
        }
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;
    }
}
