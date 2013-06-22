package xreliquary.items;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import xreliquary.Config;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFortuneCoin extends ItemXR {
	protected ItemFortuneCoin(int par1) {
		super(par1);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
		this.setUnlocalizedName(Names.FORTUNE_COIN_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Draws in nearby items and XP.");
		par3List.add("Right click to toggle.");
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
		iconBase = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.FORTUNE_COIN_NAME);
		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.FORTUNE_COIN_OVERLAY_NAME);
	}

	@Override
	public Icon getIcon(ItemStack itemStack, int renderPass) {
		if (itemStack.getItemDamage() == 0) return iconBase;
		if (renderPass != 1) return iconBase;
		else return iconOverlay;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if (!Config.disableCoinAudio) if (this.getShort("soundTimer", ist) > 0) {
			if (this.getShort("soundTimer", ist) % 2 == 0) {
				world.playSoundAtEntity(e, "random.orb", 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
			}
			this.setShort("soundTimer", ist, this.getShort("soundTimer", ist) - 1);
		}
		if (ist.getItemDamage() == 0) return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer)e;
		}
		if (player == null) return;
		scanForEntitiesInRange(world, player, 5.0D);
	}

	private void scanForEntitiesInRange(World world, EntityPlayer player, double d) {
		List iList = world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		Iterator iterator = iList.iterator();
		while (iterator.hasNext()) {
			EntityItem item = (EntityItem)iterator.next();
			if (!checkForRoom(item.getEntityItem(), player)) {
				continue;
			}
			if (item.delayBeforeCanPickup > 0) {
				item.delayBeforeCanPickup = 0;
			}
			if (player.getDistanceToEntity(item) < 1.5D) {
				continue;
			}
			teleportEntityToPlayer(item, player);
			break;
		}
		List iList2 = world.getEntitiesWithinAABB(EntityXPOrb.class, AxisAlignedBB.getBoundingBox(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		Iterator iterator2 = iList2.iterator();
		while (iterator2.hasNext()) {
			EntityXPOrb item = (EntityXPOrb)iterator2.next();
			if (player.xpCooldown > 0) {
				player.xpCooldown = 0;
			}
			if (player.getDistanceToEntity(item) < 1.5D) {
				continue;
			}
			teleportEntityToPlayer(item, player);
			break;
		}
	}

	private void teleportEntityToPlayer(Entity item, EntityPlayer player) {
		player.worldObj.spawnParticle("portal", item.posX, item.posY, item.posZ, 0.0D, 0.1D, 0.0D);
		player.getLookVec();
		double x = player.posX + player.getLookVec().xCoord * 0.2D;
		double y = player.posY - player.height / 2F;
		double z = player.posZ + player.getLookVec().zCoord * 0.2D;
		item.setPosition(x, y, z);
		player.worldObj.spawnParticle("portal", item.posX, item.posY, item.posZ, 0.0D, 0.1D, 0.0D);
		if (!Config.disableCoinAudio) {
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
		}
	}

	private boolean checkForRoom(ItemStack item, EntityPlayer player) {
		int remaining = item.stackSize;
		for (ItemStack ist : player.inventory.mainInventory) {
			if (ist == null) {
				continue;
			}
			if (ist.getItem() == item.getItem() && ist.getItemDamage() == item.getItemDamage()) {
				if (ist.stackSize + remaining <= ist.getMaxStackSize()) return true;
				else {
					int count = ist.stackSize;
					while (count < ist.getMaxStackSize()) {
						count++;
						remaining--;
						if (remaining == 0) return true;
					}
				}
			}
		}
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) return true;
		}
		return false;
	}

	@Override
	public void onUsingItemTick(ItemStack ist, EntityPlayer player, int count) {
		if (ist.getItemDamage() == 0) return;
		scanForEntitiesInRange(player.worldObj, player, 15.0D);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 64;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.block;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		if (world.isRemote) return ist;
		if (player.isSneaking()) {
			player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
		} else {
			if (!Config.disableCoinAudio) {
				this.setShort("soundTimer", ist, 6);
			}
			ist.setItemDamage(ist.getItemDamage() == 0 ? 1 : 0);
		}
		return ist;
	}
}
