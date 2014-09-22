package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.*;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.network.PacketHandler;
import xreliquary.network.RecoilAnimationPacket;
import xreliquary.util.NBTHelper;

@ContentInit
public class ItemHandgun extends ItemBase {

    public ItemHandgun() {
        super(Names.handgun);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int renderPass) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        return Integer.parseInt(Colors.PURE, 16);
    }

    public int getBulletCount(ItemStack ist) {
        return NBTHelper.getShort("bulletCount", ist);
    }

    public void setBulletCount(ItemStack ist, int i) {
        NBTHelper.setShort("bulletCount", ist, i);
    }

    public int getBulletType(ItemStack ist) {
        return NBTHelper.getShort("bulletType", ist);
    }

    public void setBulletType(ItemStack ist, int i) {
        NBTHelper.setShort("bulletType", ist, i);
    }

    public int getLastFiredShotType(ItemStack ist) { return NBTHelper.getShort("lastFiredShot", ist); }

    public void setLastFiredShotType(ItemStack ist, int i) {
        NBTHelper.setShort("lastFiredShot", ist, i);
    }

    public int getRecoilFrameCounter(ItemStack ist) { return NBTHelper.getShort("compensateRecoil", ist); }

    public void setRecoilFrameCounter(ItemStack ist, int i) { NBTHelper.setShort("compensateRecoil", ist, i); }

    public void decrementRecoilCompensationFrames(ItemStack ist) { setRecoilFrameCounter(ist, getRecoilFrameCounter(ist) - 1); }

    public int getCooldown(ItemStack ist) { return NBTHelper.getShort("cooldownTime", ist); }

    public void setCooldown(ItemStack ist, int i) { NBTHelper.setShort("cooldownTime", ist, i); }

    @Override
    public void onUpdate(ItemStack ist, World worldObj, Entity e, int i, boolean flag) {
        if (getCooldown(ist) > 0) {
            setCooldown(ist, getCooldown(ist) - 1);
        }
        if (getRecoilFrameCounter(ist) > 0) {
            if (getRecoilFrameCounter(ist) <= 3) {
                if (!worldObj.isRemote) {
                    if (!(e instanceof EntityPlayer))
                        return;
                    EntityPlayer player = (EntityPlayer) e;

                    PacketHandler.networkWrapper.sendTo(new RecoilAnimationPacket(Reference.RECOIL_COMPENSATION_PACKET_ID, getRecoilCoefficient(ist)), (EntityPlayerMP) player);
                }
            }
            if (getRecoilFrameCounter(ist) > 3) {

                if (!worldObj.isRemote) {
                    if (!(e instanceof EntityPlayer))
                        return;
                    EntityPlayer player = (EntityPlayer) e;

                    PacketHandler.networkWrapper.sendTo(new RecoilAnimationPacket(Reference.RECOIL_PACKET_ID, getRecoilCoefficient(ist)), (EntityPlayerMP) player);
                }
            }
            decrementRecoilCompensationFrames(ist);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World worldObj, EntityPlayer player) {
        if (getCooldown(ist) <= 0) {
            if (!(getBulletCount(ist) > 0) && !(getBulletType(ist) > 0)) {
                player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
            } else {
                setCooldown(ist, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_COOLDOWN_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM));

                fireBullet(ist, worldObj, player);

                //4 is the frame counter
                //on frame 4, decrease (raise angle) the pitch
                //on frames 1-3, increase (lower angle) the pitch
                //skip if 0
                setRecoilFrameCounter(ist, 4);
            }
        }
        return ist;
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int unadjustedCount) {
        int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
        int actualCount = unadjustedCount - maxUseOffset;
        actualCount -= 1;

        //you can't reload if you don't have any full mags left, so the rest of the method doesn't fire at all.
        if (!hasFilledMagazine(player)) {
            //arbitrary "feels good" cooldown for after the reload - this one just plays so you can't "fail" at reloading too fast.
            setCooldown(ist, 12);

            player.stopUsingItem();
            return;
        }
        if (actualCount == 0) {
            //arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
            setCooldown(ist, 12);
            setBulletType(ist, getMagazineTypeAndRemoveOne(player));
            if (getBulletType(ist) != 0) {
                player.swingItem();
                this.spawnEmptyMagazine(player);
                setBulletCount(ist, 8);
                player.worldObj.playSoundAtEntity(player, Reference.LOAD_SOUND, 0.25F, 1.0F);
            }
            if (getBulletCount(ist) == 0) {
                setBulletType(ist, 0);
            }
            player.stopUsingItem();
        }

        if (!(reloadTicks(actualCount, player) > 0))
            return;
        if (actualCount > getPlayerReloadDelay(player) - Reference.HANDGUN_RELOAD_ANIMATION_TICKS) {
            //only 4 ticks of this animation, 5 of the other - to explain why this one is decreased by 1
            float pitchChange = (float)Reference.HANDGUN_RELOAD_PITCH_OFFSET / ((float)Reference.HANDGUN_RELOAD_ANIMATION_TICKS - 1);
            float rotationPitch = player.prevRotationPitch + pitchChange;

            player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, rotationPitch);
        }
        if (actualCount <= Reference.HANDGUN_RELOAD_ANIMATION_TICKS) {

            float pitchChange = (float)Reference.HANDGUN_RELOAD_PITCH_OFFSET / (float)Reference.HANDGUN_RELOAD_ANIMATION_TICKS;
            float rotationPitch = player.prevRotationPitch - pitchChange;

            player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, rotationPitch);
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return this.getItemUseDuration();
    }

    private int reloadTicks(int i, EntityPlayer player) {
        return getPlayerReloadDelay(player) - i;
    }

    private int getItemUseDuration() {
        return Reference.HANDGUN_RELOAD_SKILL_OFFSET + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM;
    }

    private void fireBullet(ItemStack ist, World worldObj, EntityPlayer player) {
        if (!worldObj.isRemote) {
            switch (getBulletType(ist)) {
                case 0: return;
                case Reference.NEUTRAL_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityNeutralShot(worldObj, player)); break;
                case Reference.EXORCISM_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityExorcismShot(worldObj, player)); break;
                case Reference.BLAZE_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityBlazeShot(worldObj, player)); break;
                case Reference.ENDER_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityEnderShot(worldObj, player)); break;
                case Reference.CONCUSSIVE_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityConcussiveShot(worldObj, player)); break;
                case Reference.BUSTER_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityBusterShot(worldObj, player)); break;
                case Reference.SEEKER_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntitySeekerShot(worldObj, player)); break;
                case Reference.SAND_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntitySandShot(worldObj, player)); break;
                case Reference.STORM_SHOT_INDEX: worldObj.spawnEntityInWorld(new EntityStormShot(worldObj, player));break;
            }

            worldObj.playSoundAtEntity(player, Reference.SHOT_SOUND, 0.2F, 1.2F);

            //prevents the gun from forgetting that it fired a certain type of shot.
            setLastFiredShotType(ist, getBulletType(ist));

            setBulletCount(ist, getBulletCount(ist) - 1);
            if (getBulletCount(ist) == 0) {
                setBulletType(ist, 0);
            }
            spawnCasing(player);
        }
    }

    private void spawnEmptyMagazine(EntityPlayer player) {
        if (!player.inventory.addItemStackToInventory(new ItemStack(ContentHandler.getItem(Names.magazine), 1, 0))) {
            player.entityDropItem(new ItemStack(ContentHandler.getItem(Names.magazine), 1, 0), 0.1F);
        }
    }

    private void spawnCasing(EntityPlayer player) {
        if (!player.inventory.addItemStackToInventory(new ItemStack(ContentHandler.getItem(Names.bullet), 1, 0))) {
            player.entityDropItem(new ItemStack(ContentHandler.getItem(Names.bullet), 1, 0), 0.1F);
        }
    }

    private boolean hasFilledMagazine(EntityPlayer player) {
        for (ItemStack ist : player.inventory.mainInventory) {
            if (ist == null) {
                continue;
            }
            if (ist.getItem() == ContentHandler.getItem(Names.magazine) && ist.getItemDamage() != 0)
                return true;
        }
        return false;
    }

    private int getMagazineTypeAndRemoveOne(EntityPlayer player) {
        int bulletFound = 0;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() == ContentHandler.getItem(Names.magazine) && player.inventory.mainInventory[slot].getItemDamage() != 0) {
                bulletFound = player.inventory.mainInventory[slot].getItemDamage();
                player.inventory.decrStackSize(slot, 1);
                return bulletFound;
            }
        }
        return bulletFound;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    private int getPlayerReloadDelay(EntityPlayer player) {
        return Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM);
    }

    private float getRecoilCoefficient(ItemStack ist) {
        switch (getLastFiredShotType(ist)) {
            case 0: return 1.0F;
            case Reference.NEUTRAL_SHOT_INDEX: return 1.0F;
            case Reference.EXORCISM_SHOT_INDEX: return 1.0F;
            case Reference.BLAZE_SHOT_INDEX: return 1.0F;
            case Reference.ENDER_SHOT_INDEX: return 0.5F;
            case Reference.CONCUSSIVE_SHOT_INDEX: return 1.25F;
            case Reference.BUSTER_SHOT_INDEX: return 1.5F;
            case Reference.SEEKER_SHOT_INDEX: return 0.75F;
            case Reference.SAND_SHOT_INDEX: return 1.0F;
            case Reference.STORM_SHOT_INDEX: return 1.0F;
        }
        return 1.0F;
    }


}
