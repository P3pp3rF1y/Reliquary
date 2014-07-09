package xreliquary.items;

import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntitySpecialSnowball;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ContentInit
public class ItemIceRod extends ItemBase {

	public ItemIceRod() {
		super(Names.ice_magus_rod);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(257);
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
	public boolean hasEffect(ItemStack stack, int pass) {
		return true;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		if (world.isRemote)
			return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if (player == null)
			return;
		if (ist.getItemDamage() == 0 || ist.getItemDamage() > 1) {
			if (findAndRemoveSnowball(player)) {
				ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 1 : ist.getItemDamage() - 1);
			}
		}
	}

	private boolean findAndRemoveSnowball(EntityPlayer player) {
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if (player.inventory.mainInventory[slot].getItem() == Items.snowball) {
				player.inventory.decrStackSize(slot, 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		if (world.isRemote)
			return ist;
		if (ist.getItemDamage() == 0)
			return ist;
		if (ist.getItemDamage() < ist.getMaxDamage() - 1) {
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			world.spawnEntityInWorld(new EntitySpecialSnowball(world, player));
			ist.setItemDamage(ist.getItemDamage() == ist.getMaxDamage() - 2 ? 0 : ist.getItemDamage() + 1);
		}
		return ist;
	}
}
