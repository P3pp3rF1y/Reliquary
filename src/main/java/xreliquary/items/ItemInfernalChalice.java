package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

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

    protected int fluidLimit() { return Reliquary.CONFIG.getInt(Names.infernal_chalice, "fluid_limit"); }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            return super.onItemRightClick(ist, world, player);
        }

        float movementThresholdCoefficient = 1.0F;
        double xOffset = player.prevPosX + (player.posX - player.prevPosX) * movementThresholdCoefficient;
        double yOffset = player.prevPosY + (player.posY - player.prevPosY) * movementThresholdCoefficient + 1.62D - player.yOffset;
        double zOffset = player.prevPosZ + (player.posZ - player.prevPosZ) * movementThresholdCoefficient;
        boolean isInDrainMode = this.isEnabled(ist);
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, isInDrainMode);

        if (mop == null) {
            return ist;
        } else {
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int x = mop.blockX;
                int y = mop.blockY;
                int z = mop.blockZ;

                if (!world.canMineBlock(player, x, y, z))
                    return ist;

                if (!player.canPlayerEdit(x, y, z, mop.sideHit, ist))
                    return ist;

                //fluid handler support!
                if (this.isEnabled(ist) && NBTHelper.getInteger("fluidStacks", ist) + 1000 <= fluidLimit()) {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof IFluidHandler) {
                        FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                        FluidStack simulatedDrainedFluid = ((IFluidHandler) tile).drain(ForgeDirection.getOrientation(mop.sideHit), fluid, false);
                        if (simulatedDrainedFluid.amount == 1000) {
                            NBTHelper.setInteger("fluidStacks", ist, NBTHelper.getInteger("fluidStacks", ist) - 1000);
                        }

                        return ist;
                    }
                } else {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof IFluidHandler && NBTHelper.getInteger("fluidStacks", ist) >= 1000) {
                        FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                        int amount = ((IFluidHandler) tile).fill(ForgeDirection.getOrientation(mop.sideHit), fluid, false);

                        if (amount == 1000) {
                            ((IFluidHandler) tile).fill(ForgeDirection.getOrientation(mop.sideHit), fluid, true);
                            NBTHelper.setInteger("fluidStacks", ist, NBTHelper.getInteger("fluidStacks", ist) - 1000);
                        }

                        return ist;
                    }
                }

                String ident = ContentHelper.getIdent(world.getBlock(x, y, z));
                if (this.isEnabled(ist) && (ident.equals(ContentHelper.getIdent(Blocks.flowing_lava)) || ident.equals(ContentHelper.getIdent(Blocks.lava))) && world.getBlockMetadata(x, y, z) == 0) {
                    world.setBlock(x, y, z, Blocks.air);
                    NBTHelper.setInteger("fluidStacks", ist, NBTHelper.getInteger("fluidStacks", ist) + 1000);
                    return ist;
                }

                if (!this.isEnabled(ist)  && NBTHelper.getInteger("fluidStacks", ist) >= 1000) {
                    if (mop.sideHit == 0) {
                        --y;
                    }

                    if (mop.sideHit == 1) {
                        ++y;
                    }

                    if (mop.sideHit == 2) {
                        --z;
                    }

                    if (mop.sideHit == 3) {
                        ++z;
                    }

                    if (mop.sideHit == 4) {
                        --x;
                    }

                    if (mop.sideHit == 5) {
                        ++x;
                    }

                    if (!player.canPlayerEdit(x, y, z, mop.sideHit, ist))
                        return ist;

                    if (this.tryPlaceContainedLiquid(world, ist, xOffset, yOffset, zOffset, x, y, z) && !player.capabilities.isCreativeMode) {
                        NBTHelper.setInteger("fluidStacks", ist, NBTHelper.getInteger("fluidStacks", ist) - 1000);
                        return ist;
                    }

                }
            }

            return ist;
        }
    }

    public boolean tryPlaceContainedLiquid(World world, ItemStack ist, double par2, double par4, double par6, int x, int y, int z) {
        Material material = world.getBlock(x, y, z).getMaterial();
        boolean isNotSolid = !material.isSolid();
        if (!world.isAirBlock(x, y, z) && !isNotSolid)
            return false;
        else {

            world.setBlock(x, y, z, Blocks.flowing_lava, 0, 3);
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
