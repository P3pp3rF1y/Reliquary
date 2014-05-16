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

	@SideOnly(Side.CLIENT)
//	private IIcon iconOverlay;

	public ItemHandgun() {
		super(Reference.MOD_ID, Names.handgun);
		this.setMaxDamage((8 << 5) + 11);
		this.setMaxStackSize(1);
		canRepair = false;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
//		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.handgun_overlay);
	}

	@Override
	public IIcon getIcon(ItemStack itemStack, int renderPass) {
//		if (itemStack.getItemDamage() == 0 || renderPass != 1)
			return this.itemIcon;
//		else
//			return iconOverlay;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {

		if (renderPass == 1)
			return getColor(itemStack);
		else
			return Integer.parseInt(Colors.PURE, 16);
	}

	public int getColor(ItemStack itemStack) {

		switch (itemStack.getItemDamage()) {
		case 1:
			return Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16);
		case 2:
			return Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16);
		case 3:
			return Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16);
		case 4:
			return Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16);
		case 5:
			return Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16);
		case 6:
			return Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16);
		case 7:
			return Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16);
		case 8:
			return Integer.parseInt(Colors.SAND_SHOT_COLOR, 16);
		case 9:
			return Integer.parseInt(Colors.STORM_SHOT_COLOR, 16);
		}
		return Integer.parseInt(Colors.PURE, 16);
	}

	@Override
	public void onUpdate(ItemStack ist, World worldObj, Entity e, int i, boolean flag) {
		if (NBTHelper.getShort("cooldownTime", ist) > 0) {
			NBTHelper.setShort("cooldownTime", ist, NBTHelper.getShort("cooldownTime", ist) - 1);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World worldObj, EntityPlayer player) {
		if (NBTHelper.getShort("cooldownTime", ist) <= 0) {
			if (!(NBTHelper.getShort("bulletCount", ist) > 0) && !(NBTHelper.getShort("bulletType", ist) > 0)) {
				player.setItemInUse(ist, this.getMaxItemUseDuration(ist));

			} else {
				fireBullet(ist, worldObj, player);
			}
		}
		return ist;
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
		//System.out.println("Tick count: " + count);
		if (!hasFilledMagazine(player)) {
			NBTHelper.setShort("cooldownTime", ist, 12);
			// play click!
			resetReloadDuration(ist);
			player.stopUsingItem();
			return;
		}
		if (reloadTicks(count) >= calculatePlayerSkillTimer(player) - 1) {
			NBTHelper.setShort("cooldownTime", ist, 24);
			NBTHelper.setShort("bulletType", ist, getMagazineTypeAndRemoveOne(player));
			if (NBTHelper.getShort("bulletType", ist) != 0) {
				player.swingItem();
				this.spawnEmptyMagazine(player);
				NBTHelper.setShort("bulletCount", ist, 8);
				player.worldObj.playSoundAtEntity(player, Reference.LOAD_SOUND, 0.25F, 1.0F);
			}
			if (NBTHelper.getShort("bulletCount", ist) == 0) {
				NBTHelper.setShort("bulletType", ist, 0);
			}
			setGunDamageByContents(ist);
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
			NBTHelper.setShort("cooldownTime", ist, 12);
			switch (NBTHelper.getShort("bulletType", ist)) {
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
			NBTHelper.setShort("bulletCount", ist, NBTHelper.getShort("bulletCount", ist) - 1);
			if (NBTHelper.getShort("bulletCount", ist) == 0) {
				NBTHelper.setShort("bulletType", ist, 0);
			}
			spawnCasing(player);
		}
		setGunDamageByContents(ist);
	}

	private void resetReloadDuration(ItemStack ist) {
		NBTHelper.setShort("reloadDuration", ist, 0);
	}

	private void setGunDamageByContents(ItemStack ist) {
		ist.setItemDamage((8 - NBTHelper.getShort("bulletCount", ist) << 5) + NBTHelper.getShort("bulletType", ist));
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
