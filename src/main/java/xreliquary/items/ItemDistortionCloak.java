package xreliquary.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.common.TimeKeeperHandler;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDistortionCloak extends ItemXR {

    protected ItemDistortionCloak(int par1) {
        super(par1);
        this.setMaxDamage(2401);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setUnlocalizedName(Names.DISTORTION_CLOAK_NAME);
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer,
            List par3List, boolean par4) {
        par3List.add("Right click: invisibility, sneak to teleport.");
        par3List.add("Consumes ender pearls for its charge.");
        par3List.add("Currently " + getChargeTime(ist) / 1200 + "m"
                + getChargeTime(ist) / 20 % 60 + "s remaining.");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return isActive(stack);
    }

    public boolean isActive(ItemStack ist) {
        return getBoolean("isActive", ist);
    }

    public void toggleActive(ItemStack ist) {
        setActive(ist, !isActive(ist));
    }

    public void setActive(ItemStack ist, boolean b) {
        setBoolean("isActive", ist, b);
    }

    @SideOnly(Side.CLIENT)
    private Icon iconOverlay;

    @SideOnly(Side.CLIENT)
    private Icon iconBase;

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        iconBase = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.DISTORTION_CLOAK_NAME);
        iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.DISTORTION_CLOAK_OVERLAY_NAME);
    }

    @Override
    public Icon getIcon(ItemStack itemStack, int renderPass) {
        if (renderPass != 1)
            return iconBase;
        else
            return iconOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        if (renderPass == 1) {
            int i = TimeKeeperHandler.getTime();
            i %= 87;
            if (i > 43) {
                i = 87 - i;
            }
            i = (int) (i * 255F / 43F);
            String red = Integer.toHexString(i);
            return Integer.parseInt(String.format("%s%s%s", red, "00", "00"),
                    16);
        } else
            return Integer.parseInt(Colors.DARKEST, 16);

        // else
        // if (renderPass == 1)
        // return Integer.parseInt(Constants.BLOOD_RED_COLOR,16);
        // else
        // return Integer.parseInt(Constants.DARKEST,16);

    }

    private boolean findAndRemoveEnderPearl(EntityPlayer player) {
        if (player.capabilities.isCreativeMode)
            return true;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() == Item.enderPearl) {
                player.inventory.decrStackSize(slot, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (!(e instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) e;
        if (isActive(ist)) {
            if (getChargeTime(ist) <= 0) {
                if (findAndRemoveEnderPearl(player)) {
                    resetChargeTime(ist);
                } else {
                    toggleActive(ist);
                }
            }
            if (getChargeTime(ist) > 0) {
                player.addPotionEffect(new PotionEffect(Potion.invisibility.id,
                        5, 0));
                decreaseChargeTime(ist);
            }
        }
        if (isOnCooldown(ist)) {
            decrementCooldown(ist);
        }
    }

    private void decreaseChargeTime(ItemStack ist) {
        this.setShort("chargeTime", ist, getChargeTime(ist) - 1);
    }

    private void resetChargeTime(ItemStack ist) {
        this.setShort("chargeTime", ist, 2400);
    }

    private int getChargeTime(ItemStack ist) {
        return this.getShort("chargeTime", ist);
    }

    private void decrementCooldown(ItemStack ist) {
        this.setShort("cooldown", ist, this.getShort("cooldown", ist) - 1);
    }

    private boolean isOnCooldown(ItemStack ist) {
        return this.getShort("cooldown", ist) > 0;
    }

    private void setCooldown(ItemStack ist) {
        this.setShort("cooldown", ist, 10);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world,
            EntityPlayer player) {
        if (player.isSneaking()) {
            if (this.isOnCooldown(ist))
                return ist;
            MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(
                    world, player, true);
            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
                if (!findAndRemoveEnderPearl(player))
                    return ist;
                float xOff = mop.blockX;
                float yOff = mop.blockY + 1F;
                float zOff = mop.blockZ;
                doTeleport(world, player, ist, xOff, yOff, zOff);
            }
        } else {
            toggleActive(ist);
        }
        return ist;
    }

    @Override
    protected MovingObjectPosition getMovingObjectPositionFromPlayer(
            World par1World, EntityPlayer par2EntityPlayer, boolean par3) {
        float var4 = 1.0F;
        float var5 = par2EntityPlayer.prevRotationPitch
                + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch)
                * var4;
        float var6 = par2EntityPlayer.prevRotationYaw
                + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw)
                * var4;
        double var7 = par2EntityPlayer.prevPosX
                + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * var4;
        double var9 = par2EntityPlayer.prevPosY
                + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * var4
                + 1.62D - par2EntityPlayer.yOffset;
        double var11 = par2EntityPlayer.prevPosZ
                + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * var4;
        Vec3 var13 = par1World.getWorldVec3Pool().getVecFromPool(var7, var9,
                var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        double var21 = 128.0D;
        Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20
                * var21);
        return par1World.rayTraceBlocks_do_do(var13, var23, par3, !par3);
    }

    private void doTeleport(World world, EntityPlayer player, ItemStack ist,
            float x, float y, float z) {
        for (int particles = 0; particles < 32; ++particles) {
            world.spawnParticle("portal", x,
                    y + world.rand.nextDouble() * 2.0D, z,
                    world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
        }
        if (!world.isRemote) {
            player.setPositionAndUpdate(x, y, z);
            player.fallDistance = 0.0F;
        }
        this.setCooldown(ist);
    }
}
