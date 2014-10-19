package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemInfernalChalice extends ItemToggleable {
    public ItemInfernalChalice() {
        super(Names.infernal_chalice);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(513);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (player.isSneaking()) {
//            if (!this.isEnabled(ist))
//                doHasGlacialStaffEnabledCheck(player);
            return super.onItemRightClick(ist, world, player);
        }

        float movementThresholdCoefficient = 1.0F;
        double xOffset = player.prevPosX + (player.posX - player.prevPosX) * movementThresholdCoefficient;
        double yOffset = player.prevPosY + (player.posY - player.prevPosY) * movementThresholdCoefficient + 1.62D - player.yOffset;
        double zOffset = player.prevPosZ + (player.posZ - player.prevPosZ) * movementThresholdCoefficient;
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

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
                if (this.isEnabled(ist) && (ist.getItemDamage() == 0 || ist.getItemDamage() >= 1)) {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof IFluidHandler) {
                        FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                        FluidStack simulatedDrainedFluid = ((IFluidHandler) tile).drain(ForgeDirection.getOrientation(mop.sideHit), fluid, false);
                        if (simulatedDrainedFluid.amount == 1000) {
                            ist.setItemDamage(ist.getItemDamage() - 1);
                        }

                        return ist;
                    }
                } else {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof IFluidHandler && (ist.getItemDamage() > 0 || ist.getItemDamage() < ist.getMaxDamage() - 1)) {
                        FluidStack fluid = new FluidStack(FluidRegistry.LAVA, 1000);
                        int amount = ((IFluidHandler) tile).fill(ForgeDirection.getOrientation(mop.sideHit), fluid, false);

                        if (amount == 1000) {
                            ((IFluidHandler) tile).fill(ForgeDirection.getOrientation(mop.sideHit), fluid, true);
                            ist.setItemDamage(ist.getItemDamage() + 1);
                        }

                        return ist;
                    }
                }

                String ident = ContentHelper.getIdent(world.getBlock(x, y, z));
                if (this.isEnabled(ist) && (ident.equals(ContentHelper.getIdent(Blocks.flowing_lava)) || ident.equals(ContentHelper.getIdent(Blocks.lava))) && world.getBlockMetadata(x, y, z) == 0) {
                    world.setBlock(x, y, z, Blocks.air);
                    ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 1 : ist.getItemDamage() - 1);
                    return ist;
                }

                if (!this.isEnabled(ist) && ist.getItemDamage() >= 1 && ist.getItemDamage() < ist.getMaxDamage() - 1) {
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

                    if (ist.getItemDamage() != 0 && ist.getItemDamage() <= ist.getMaxDamage() - 1 && this.tryPlaceContainedLiquid(world, ist, xOffset, yOffset, zOffset, x, y, z) && !player.capabilities.isCreativeMode) {
                        ist.setItemDamage(ist.getItemDamage() >= ist.getMaxDamage() - 1 ? 0 : ist.getItemDamage() + 1);
                        return ist;
                    }

                }
            }

            return ist;
        }
    }

//    public void doHasGlacialStaffEnabledCheck(EntityPlayer player) {
//        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
//            if (player.inventory.mainInventory[i] != null && player.inventory.mainInventory[i].getItem() instanceof ItemGlacialStaff) {
//                if (((ItemToggleable)player.inventory.mainInventory[i].getItem()).isEnabled(player.inventory.mainInventory[i])) {
//                    ((ItemToggleable)player.inventory.mainInventory[i].getItem()).toggleEnabled(player.inventory.mainInventory[i]);
//                }
//            }
//        }
//    }

    public boolean tryPlaceContainedLiquid(World world, ItemStack ist, double par2, double par4, double par6, int x, int y, int z) {
        Material material = world.getBlock(x, y, z).getMaterial();
        if ((!world.isAirBlock(x, y, z) && material.isSolid()) || (material.isLiquid() && (material != Material.lava || world.getBlockMetadata(x, y, z) == 0)))
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

//        if (this.isEnabled(ist)) {
//            doHasGlacialStaffEnabledCheck(player);
//
//
//            int x = MathHelper.floor_double(player.posX);
//            int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
//            int z = MathHelper.floor_double(player.posZ);
//
//            for (int xOff = -3; xOff <= 3; xOff++) {
//                for (int yOff = -3; yOff <= 3; yOff++) {
//                    for (int zOff = -3; zOff <= 3; zOff++) {
//                        if (ist.getItemDamage() == 0 || ist.getItemDamage() > 1)
//                            doDrainCheck(ist, x, y, z, world, xOff, yOff, zOff);
//                    }
//                }
//            }
//
//        }
    }
//
//    public void doDrainCheck(ItemStack ist, int x, int y, int z, World world, int xOff, int yOff, int zOff) {
//        x += xOff;
//        y += yOff;
//        z += zOff;
//        Block block = world.getBlock(x, y, z);
//        if (block.getMaterial() == Material.lava && world.getBlockMetadata(x, y, z) == 0) {
//            ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 1 : ist.getItemDamage() - 1);
//            world.setBlock(x, y, z, Blocks.cobblestone);
//
//            float red = 1.0F;
//            float green = 0.0F;
//            float blue = 0.0F;
//            String nameOfParticle = "reddust";
//
//            for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
//                if (world.isRemote) {
//                    float xVel = world.rand.nextFloat();
//                    float yVel = world.rand.nextFloat() + 0.5F;
//                    float zVel = world.rand.nextFloat();
//                    EntityFX effect = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(nameOfParticle, x + xVel, y + yVel, z + zVel, red, green, blue);
//                }
//            }
//        }
//    }
}
