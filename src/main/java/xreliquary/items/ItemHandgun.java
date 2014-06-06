package xreliquary.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntityBlazeShot;
import xreliquary.entities.EntityBusterShot;
import xreliquary.entities.EntityConcussiveShot;
import xreliquary.entities.EntityEnderShot;
import xreliquary.entities.EntityExorcismShot;
import xreliquary.entities.EntityNeutralShot;
import xreliquary.entities.EntitySandShot;
import xreliquary.entities.EntitySeekerShot;
import xreliquary.entities.EntityStormShot;
import xreliquary.init.ContentHandler;
import xreliquary.init.XRInit;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@XRInit
public class ItemHandgun extends ItemBase {

	public ItemHandgun() {
		super(Reference.MOD_ID, Names.handgun);
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

    public int getCooldown(ItemStack ist) {
        return NBTHelper.getShort("cooldownTime", ist);
    }

    public void setCooldown(ItemStack ist, int i) {
        NBTHelper.setShort("cooldownTime", ist, i);
    }

	@Override
	public void onUpdate(ItemStack ist, World worldObj, Entity e, int i, boolean flag) {
		if (getCooldown(ist) > 0) {
			setCooldown(ist, getCooldown(ist) - 1);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World worldObj, EntityPlayer player) {
		if (getCooldown(ist) <= 0) {
			if (!(getBulletCount(ist) > 0) && !(getBulletType(ist) > 0)) {
				player.setItemInUse(ist, this.getMaxItemUseDuration(ist));

			} else {
				fireBullet(ist, worldObj, player);
			}
		}
		return ist;
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
		if (!hasFilledMagazine(player)) {
			setCooldown(ist, 12);
			// play click!
			resetReloadDuration(ist);
			player.stopUsingItem();
			return;
		}
		if (reloadTicks(count) >= calculatePlayerSkillTimer(player) - 1) {
            setCooldown(ist, 24);
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
		return EnumAction.block;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return this.getItemUseDuration();
	}

	private int reloadTicks(int i) {
		return this.getItemUseDuration() - i;
	}

	private int getItemUseDuration() {
		return 256;
	}

	private void fireBullet(ItemStack ist, World worldObj, EntityPlayer player) {
		if (!worldObj.isRemote) {
			setCooldown(ist, 12);
			switch (getBulletType(ist)) {
			case 0:
				return;
			case 1:
				EntityNeutralShot ns = new EntityNeutralShot(worldObj, player);
				worldObj.spawnEntityInWorld(ns);
				break;
			case 2:
				EntityExorcismShot exs = new EntityExorcismShot(worldObj, player);
				worldObj.spawnEntityInWorld(exs);
				break;
			case 3:
				EntityBlazeShot bls = new EntityBlazeShot(worldObj, player);
				worldObj.spawnEntityInWorld(bls);
				break;
			case 4:
				EntityEnderShot es = new EntityEnderShot(worldObj, player);
				worldObj.spawnEntityInWorld(es);
				break;
			case 5:
				EntityConcussiveShot cs = new EntityConcussiveShot(worldObj, player);
				worldObj.spawnEntityInWorld(cs);
				break;
			case 6:
				EntityBusterShot bs = new EntityBusterShot(worldObj, player);
				worldObj.spawnEntityInWorld(bs);
				break;
			case 7:
				EntitySeekerShot ss = new EntitySeekerShot(worldObj, player);
				worldObj.spawnEntityInWorld(ss);
				break;
			case 8:
				EntitySandShot sas = new EntitySandShot(worldObj, player);
				worldObj.spawnEntityInWorld(sas);
				break;
			case 9:
				EntityStormShot sts = new EntityStormShot(worldObj, player);
				worldObj.spawnEntityInWorld(sts);
				break;
			}
			resetReloadDuration(ist);
			worldObj.playSoundAtEntity(player, Reference.SHOT_SOUND, 0.2F, 1.2F);
			setBulletCount(ist, getBulletCount(ist) - 1);
			if (getBulletCount(ist) == 0) {
				setBulletType(ist, 0);
			}
			spawnCasing(player);
		}
	}

	private void resetReloadDuration(ItemStack ist) {
		NBTHelper.setShort("reloadDuration", ist, 0);
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

	private int calculatePlayerSkillTimer(EntityPlayer player) {
		if (player.experienceLevel >= 20)
			return 12;
		return 20 - player.experienceLevel + 12;
	}
}
