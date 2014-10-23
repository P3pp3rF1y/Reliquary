package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xreliquary.lib.Names;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemGlacialStaff extends ItemIceRod {
    public ItemGlacialStaff() {
        super(Names.glacial_staff);
    }

    @Override
    public boolean isFull3D(){ return true; }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 11;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.block;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (!player.isSneaking()) {
            player.setItemInUse(ist, getMaxItemUseDuration(ist));
        }

        return super.onItemRightClick(ist, world, player);
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
        String charge = Integer.toString(NBTHelper.getInteger("snowballs", ist));
        this.formatTooltip(ImmutableMap.of("charge", charge), ist, list);
    }


    //a longer ranged version of "getMovingObjectPositionFromPlayer" basically
    public MovingObjectPosition getBlockTarget(World world, EntityPlayer player) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + (double)(world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 12.0D;
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.func_147447_a(vec3, vec31, true, false, false);
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
        MovingObjectPosition mop = this.getBlockTarget(player.worldObj, player);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            float xOff = (float) (mop.blockX - player.posX);
            float yOff = (float) (mop.blockY - player.posY);
            float zOff = (float) (mop.blockZ - player.posZ);
            this.onItemUse(ist, player, player.worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, xOff, yOff, zOff);
        }
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int sideHit, float xOff, float yOff, float zOff) {
        if (NBTHelper.getInteger("snowballs", ist) >= getSnowballCost()) {
            double areaCoefficient = 5D;
            spawnBlizzardParticles(player, x, y, z, areaCoefficient);
            if (player.getItemInUseDuration() != 0 && player.getItemInUseDuration() % 10 == 0) {
                NBTHelper.setInteger("snowballs", ist, NBTHelper.getInteger("snowballs", ist) - getSnowballCost());
                doBlizzardEffect(player, x, y, z, areaCoefficient);
            }
        }
        return false;
    }

    public void doBlizzardEffect(EntityPlayer player, int x, int y, int z, double areaCoefficient) {
        if (player.worldObj.isRemote)
            return;
        double lowerX = x - areaCoefficient;
        double lowerY = y;
        double lowerZ = z - areaCoefficient;
        double upperX = x + areaCoefficient;
        double upperY = y + 15D;
        double upperZ = z + areaCoefficient;
        List eList = player.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
        Iterator iterator = eList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();
            if (e instanceof EntityLivingBase && !e.isEntityEqual(player)) {
                EntityLivingBase livingBase = (EntityLivingBase)e;
                PotionEffect slow = new PotionEffect(Potion.moveSlowdown.id, 30, 0);

                //if the creature is slowed already, refresh the duration and increase the amplifier by 1.
                //5 hits is all it takes to max out the amplitude.
                if (livingBase.getActivePotionEffect(Potion.moveSlowdown) != null)
                    slow = new PotionEffect(Potion.moveSlowdown.id, Math.min(livingBase.getActivePotionEffect(Potion.moveSlowdown).getDuration() + 30, 300), Math.min(livingBase.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1, 4));

                ((EntityLivingBase) e).addPotionEffect(slow);
                e.attackEntityFrom(DamageSource.causePlayerDamage(player), slow.getAmplifier());
            }
        }
    }

    public void spawnBlizzardParticles(EntityPlayer player, int x, int y, int z, double areaCoefficient) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 16; ++i) {
            double randX = (x + 0.5D) + areaCoefficient * (player.worldObj.rand.nextFloat() - 0.5F);
            double randMotX = player.worldObj.rand.nextGaussian() * 0.1D;
            double randY = y + 10D;
            double randMotY = (0.2F + (player.worldObj.rand.nextFloat() * 0.2F)) * -1F;
            double randZ = (z + 0.5D) + areaCoefficient * (player.worldObj.rand.nextFloat() - 0.5F);
            double randMotZ = player.worldObj.rand.nextGaussian() * 0.1D;
            if (Math.abs(randX - (x + 0.5D)) >= 4.0D && Math.abs(randZ - (z + 0.5D)) >= 4.0D)
                continue;
            player.worldObj.spawnParticle("blockdust_" + Block.getIdFromBlock(Blocks.snow_layer) + "_" + 0, randX, randY, randZ, randMotX, randMotY, randMotZ);
        }
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        super.onUpdate(ist, world, e, i, b);
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
        int z = MathHelper.floor_double(player.posZ);

        if (this.isEnabled(ist)) {
            for (int xOff = -2; xOff <= 2; xOff++) {
                for (int zOff = -2; zOff <= 2; zOff++) {
                    if (Math.abs(xOff) == 2 && Math.abs(zOff) == 2)
                        continue;
                    doFreezeCheck(ist, x, y, z, world, xOff, zOff);
                }
            }
        }

        for (int xOff = -5; xOff <= 5; xOff++) {
            for (int yOff = -5; yOff <= 5; yOff++) {
                for (int zOff = -5; zOff <= 5; zOff++) {
                    if (Math.abs(yOff) < 3 && Math.abs(xOff) < 3 && Math.abs(zOff) < 3 && !(Math.abs(xOff) == 2 && Math.abs(zOff) == 2))
                        continue;
                    doThawCheck(ist, x, y, z, world, xOff, yOff, zOff);
                }
            }
        }
    }

    public void doFreezeCheck(ItemStack ist, int x, int y, int z, World world, int xOff, int zOff) {
        x += xOff;
        z += zOff;
        Block block = world.getBlock(x, y, z);
        if (block.getMaterial() == Material.water  && world.getBlockMetadata(x, y, z) == 0) {
            addFrozenBlockToList(ist, x, y, z);
            world.setBlock(x, y, z, Blocks.packed_ice);

            float red = 0.75F;
            float green = 0.75F;
            float blue = 1.0F;
            String nameOfParticle = "reddust";

            for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
                if (world.isRemote) {
                    float xVel = world.rand.nextFloat();
                    float yVel = world.rand.nextFloat() + 0.5F;
                    float zVel = world.rand.nextFloat();
                    EntityFX effect = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(nameOfParticle, x + xVel, y + yVel, z + zVel, red, green, blue);
                }
            }
        } else if (block.getMaterial() == Material.lava && world.getBlockMetadata(x, y, z) == 0) {
            addFrozenBlockToList(ist, x, y, z);
            world.setBlock(x, y, z, Blocks.obsidian);
            for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
                float xVel = world.rand.nextFloat();
                float yVel = world.rand.nextFloat() + 0.5F;
                float zVel = world.rand.nextFloat();
                world.spawnParticle(world.rand.nextInt(3) == 0 ? "largesmoke" : "smoke", x + xVel, y + yVel, z + zVel, 0.0D, 0.2D, 0.0D);

            }

        }
    }

    public void doThawCheck(ItemStack ist, int x, int y, int z, World world, int xOff, int yOff, int zOff) {
        x += xOff;
        y += yOff;
        z += zOff;
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.packed_ice) {
            if (removeFrozenBlockFromList(ist, x, y, z)) {
                world.setBlock(x, y, z, Blocks.water);
                for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
                    float xVel = world.rand.nextFloat();
                    float yVel = world.rand.nextFloat() + 0.5F;
                    float zVel = world.rand.nextFloat();
                    world.spawnParticle(world.rand.nextInt(3) == 0 ? "largesmoke" : "smoke", x + xVel, y + yVel, z + zVel, 0.0D, 0.2D, 0.0D);

                }
            }
        } else if (block == Blocks.obsidian) {
            if (removeFrozenBlockFromList(ist, x, y, z)) {
                world.setBlock(x, y, z, Blocks.lava);

                float red = 1.0F;
                float green = 0.0F;
                float blue = 0.0F;
                String nameOfParticle = "reddust";

                for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
                    if (world.isRemote) {
                        float xVel = world.rand.nextFloat();
                        float yVel = world.rand.nextFloat() + 0.5F;
                        float zVel = world.rand.nextFloat();
                        EntityFX effect = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(nameOfParticle, x + xVel, y + yVel, z + zVel, red, green, blue);
                    }
                }
            }
        }
    }

    private void addFrozenBlockToList(ItemStack ist, int x, int y, int z) {
        NBTTagCompound tagCompound = ist.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        if (tagCompound.getTag("BlockLocations") == null)
            tagCompound.setTag("BlockLocations", new NBTTagList());
        NBTTagList tagList = tagCompound.getTagList("BlockLocations", 10);

        NBTTagCompound newTagData = new NBTTagCompound();
        newTagData.setInteger("x", x);
        newTagData.setInteger("y", y);
        newTagData.setInteger("z", z);

        tagList.appendTag(newTagData);

        tagCompound.setTag("BlockLocations", tagList);

        ist.setTagCompound(tagCompound);
    }

    private boolean removeFrozenBlockFromList(ItemStack ist, int x, int y, int z) {
        NBTTagCompound tagCompound = ist.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        if (tagCompound.getTag("BlockLocations") == null)
            tagCompound.setTag("BlockLocations", new NBTTagList());
        NBTTagList tagList = tagCompound.getTagList("BlockLocations", 10);

        boolean removedBlock = false;

        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
            if (tagItemData.getInteger("x") == x && tagItemData.getInteger("y") == y && tagItemData.getInteger("z") == z) {
                tagItemData.setBoolean("remove", true);
                removedBlock = true;
            }
        }

        NBTTagList newTagList = new NBTTagList();
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
            if (!tagItemData.getBoolean("remove")) {
                NBTTagCompound newTagData = new NBTTagCompound();
                newTagData.setInteger("x", tagItemData.getInteger("x"));
                newTagData.setInteger("y", tagItemData.getInteger("y"));
                newTagData.setInteger("z", tagItemData.getInteger("z"));
                newTagList.appendTag(newTagData);
            }
        }

        tagCompound.setTag("BlockLocations", newTagList);
        ist.setTagCompound(tagCompound);
        return removedBlock;
    }
}
