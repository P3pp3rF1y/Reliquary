package xreliquary.items;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@XRInit
public class ItemFortuneCoin extends ItemBase {

	public ItemFortuneCoin() {
		super(Reference.MOD_ID, Names.FORTUNE_COIN_NAME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
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

	@SideOnly(Side.CLIENT)
	private IIcon iconOverlay;

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.FORTUNE_COIN_OVERLAY_NAME);
	}

	@Override
	public IIcon getIcon(ItemStack itemStack, int renderPass) {
		if (itemStack.getItemDamage() == 0 || renderPass != 1)
			return this.itemIcon;
		else
			return iconOverlay;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if (!Reliquary.PROXY.disableCoinAudio)
			if (NBTHelper.getShort("soundTimer", ist) > 0) {
				if (NBTHelper.getShort("soundTimer", ist) % 2 == 0) {
					world.playSoundAtEntity(e, "random.orb", 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
				}
				NBTHelper.setShort("soundTimer", ist, NBTHelper.getShort("soundTimer", ist) - 1);
			}
		if (ist.getItemDamage() == 0)
			return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if (player == null)
			return;
		scanForEntitiesInRange(world, player, 5.0D);
	}

	private void scanForEntitiesInRange(World world, EntityPlayer player, double d) {
		List iList = world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		Iterator iterator = iList.iterator();
		while (iterator.hasNext()) {
			EntityItem item = (EntityItem) iterator.next();
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
			EntityXPOrb item = (EntityXPOrb) iterator2.next();
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
		if (!Reliquary.PROXY.disableCoinAudio) {
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
				if (ist.stackSize + remaining <= ist.getMaxStackSize())
					return true;
				else {
					int count = ist.stackSize;
					while (count < ist.getMaxStackSize()) {
						count++;
						remaining--;
						if (remaining == 0)
							return true;
					}
				}
			}
		}
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null)
				return true;
		}
		return false;
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
		if (ist.getItemDamage() == 0)
			return;
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
		if (world.isRemote)
			return ist;
		if (player.isSneaking()) {
			player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
		} else {
			if (!Reliquary.PROXY.disableCoinAudio) {
				NBTHelper.setShort("soundTimer", ist, 6);
			}
			ist.setItemDamage(ist.getItemDamage() == 0 ? 1 : 0);
		}
		return ist;
	}
}
