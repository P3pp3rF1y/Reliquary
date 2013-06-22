package xreliquary.items;

import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import xreliquary.entities.EntityBlazeShot;
import xreliquary.entities.EntityBusterShot;
import xreliquary.entities.EntityConcussiveShot;
import xreliquary.entities.EntityEnderShot;
import xreliquary.entities.EntityExorcismShot;
import xreliquary.entities.EntityNeutralShot;
import xreliquary.entities.EntitySandShot;
import xreliquary.entities.EntitySeekerShot;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHandgun extends ItemXR {
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
		iconBase = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.HANDGUN_NAME);
		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.HANDGUN_OVERLAY_NAME);
	}

	@Override
	public Icon getIcon(ItemStack itemStack, int renderPass) {
		if (itemStack.getItemDamage() == 0) return iconBase;
		if (renderPass != 1) return iconBase;
		else return iconOverlay;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (renderPass == 1) return getColor(itemStack);
		else return Integer.parseInt(Colors.PURE, 16);
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

	protected ItemHandgun(int par1) {
		super(par1);
		this.setMaxDamage((8 << 5) + 11);
		this.setMaxStackSize(1);
		canRepair = false;
		this.setUnlocalizedName(Names.HANDGUN_NAME);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("For great justice.");
		par3List.add("Right click fires, hold to reload.");
	}

	@Override
	public void onUpdate(ItemStack ist, World worldObj, Entity e, int i, boolean flag) {
		if (this.getShort("cooldownTime", ist) > 0) {
			this.setShort("cooldownTime", ist, this.getShort("cooldownTime", ist) - 1);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World worldObj, EntityPlayer player) {
		if (this.getShort("cooldownTime", ist) <= 0) {
			if (!(this.getShort("bulletCount", ist) > 0) && !(this.getShort("bulletType", ist) > 0)) {
				player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
			} else {
				fireBullet(ist, worldObj, player);
			}
		}
		return ist;
	}

	@Override
	public void onUsingItemTick(ItemStack ist, EntityPlayer player, int count) {
		System.out.println("Tick count: " + count);
		if (reloadTicks(count) >= calculatePlayerSkillTimer(player) - 1) {
			this.setShort("cooldownTime", ist, 24);
			if (this.getShort("bulletType", ist) != 0) {
				player.swingItem();
				this.setShort("bulletCount", ist, 8);
				player.worldObj.playSoundAtEntity(player, Reference.LOAD_SOUND, 0.25F, 1.0F);
			}
			if (this.getShort("bulletCount", ist) == 0) {
				this.setShort("bulletType", ist, 0);
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
			this.setShort("cooldownTime", ist, 12);
			switch (this.getShort("bulletType", ist)) {
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
			}
			resetReloadDuration(ist);
			worldObj.playSoundAtEntity(player, Reference.SHOT_SOUND, 0.2F, 1.2F);
			this.setShort("bulletCount", ist, this.getShort("bulletCount", ist) - 1);
			if (this.getShort("bulletCount", ist) == 0) {
				this.setShort("bulletType", ist, 0);
			}
			spawnCasing(player);
		}
		setGunDamageByContents(ist);
	}

	private void resetReloadDuration(ItemStack ist) {
		this.setShort("reloadDuration", ist, 0);
	}

	private void setGunDamageByContents(ItemStack ist) {
		ist.setItemDamage((8 - this.getShort("bulletCount", ist) << 5) + this.getShort("bulletType", ist));
	}

	private void spawnCasing(EntityPlayer player) {
		if (!player.inventory.addItemStackToInventory(new ItemStack(XRItems.shell, 1, 0))) {
			player.dropPlayerItem(new ItemStack(XRItems.shell, 1, 0));
		}
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	private int calculatePlayerSkillTimer(EntityPlayer player) {
		if (player.experienceLevel >= 20) return 12;
		return 20 - player.experienceLevel + 12;
	}
}
