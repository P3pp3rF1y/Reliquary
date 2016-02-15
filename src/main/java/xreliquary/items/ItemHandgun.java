package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.shot.*;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ItemHandgun extends ItemBase {

    public ItemHandgun() {
        super(Names.handgun);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
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

    public void setLastFiredShotType(ItemStack ist, int i) {
        NBTHelper.setShort("lastFiredShot", ist, i);
    }

    public boolean isInCooldown(ItemStack ist) {return NBTHelper.getBoolean("inCooldown", ist);}

    public void setInCooldown(ItemStack ist, boolean inCooldown) {NBTHelper.setBoolean("inCooldown", ist, inCooldown);}

    public long getCooldown(ItemStack ist) { return NBTHelper.getLong("cooldownTime", ist); }

    public void setCooldown(ItemStack ist, long i) {
        NBTHelper.setLong("cooldownTime", ist, i);
    }

    @Override
    public void onUpdate(ItemStack ist, World worldObj, Entity e, int i, boolean flag) {
        if (!worldObj.isRemote) {
            if (isInCooldown(ist) && isCooldownOver(worldObj, ist)
                        || !isValidCooldownTime(worldObj, ist)) {
                setInCooldown(ist, false);
            }
        }
    }

    private boolean isCooldownOver(World worldObj, ItemStack ist) {
        return getCooldown(ist) < worldObj.getWorldTime() && worldObj.getWorldTime() - getCooldown(ist) < 12000;
    }

    private boolean isValidCooldownTime(World worldObj, ItemStack ist) {
        return Math.min(Math.abs(worldObj.getWorldTime() - getCooldown(ist)), Math.abs(worldObj.getWorldTime() - 23999 - getCooldown(ist))) <= getMaxItemUseDuration(ist);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World worldObj, EntityPlayer player) {
        if (!isInCooldown(ist)) {
            if (!(getBulletCount(ist) > 0) && !(getBulletType(ist) > 0)) {
                player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
            } else {
                if (!worldObj.isRemote) {
                    setCooldown(ist, worldObj.getWorldTime() + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_COOLDOWN_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM));
                    setInCooldown(ist, true);

                    fireBullet(ist, worldObj, player);
                }
            }
        }
        return ist;
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int unadjustedCount) {
        if (player.worldObj.isRemote)
            return;

        int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
        int actualCount = unadjustedCount - maxUseOffset;
        actualCount -= 1;
        //you can't reload if you don't have any full mags left, so the rest of the method doesn't fire at all.
        if (!hasFilledMagazine(player)) {
            //arbitrary "feels good" cooldown for after the reload - this one just plays so you can't "fail" at reloading too fast.
            setCooldown(ist, player.worldObj.getWorldTime() + 12);
            setInCooldown(ist, true);
            player.stopUsingItem();
            return;
        }

        if (actualCount == 0) {
            //arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
            setCooldown(ist, player.worldObj.getWorldTime() + 12);
            setInCooldown(ist, true);
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
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return this.getItemUseDuration();
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
        if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.magazine, 1, 0))) {
            player.entityDropItem(new ItemStack(ModItems.magazine, 1, 0), 0.1F);
        }
    }

    private void spawnCasing(EntityPlayer player) {
        if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.bullet, 1, 0))) {
            player.entityDropItem(new ItemStack(ModItems.bullet, 1, 0), 0.1F);
        }
    }

    private boolean hasFilledMagazine(EntityPlayer player) {
        for (ItemStack ist : player.inventory.mainInventory) {
            if (ist == null) {
                continue;
            }
            if (ist.getItem() == ModItems.magazine && ist.getItemDamage() != 0)
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
            if (player.inventory.mainInventory[slot].getItem() == ModItems.magazine && player.inventory.mainInventory[slot].getItemDamage() != 0) {
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


}
