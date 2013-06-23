package xreliquary.items;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemWithCapacity extends ItemXR {
	protected ItemStack DEFAULT_TARGET_ITEM = null;

	protected ItemWithCapacity(int par1) {
		super(par1);
		this.setMaxDamage(1002);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	protected boolean isVoidItemFull(ItemStack ist) {
		return getShort("itemQuantity", ist) >= VoidUpgradeHandler.getCapacity(ist.getTagCompound());
	}

	// just a failsafe, returns true/false based on success.
	protected boolean decreaseQuantity(ItemStack ist) {
		if (hasItem(ist)) {
			setQuantity(ist, getQuantity(ist) - 1);
		} else return false;
		return true;
	}

	protected void increaseQuantity(ItemStack ist) {
		setQuantity(ist, getQuantity(ist) + 1);
	}

	protected void updateItemDamage(ItemStack ist) {
		float capacity = getCapacity(ist);
		float itemCount = getQuantity(ist);
		float damageValue = 1000F - itemCount * 1000F / capacity + 1;
		ist.setItemDamage((int)damageValue);
	}

	protected void setQuantity(ItemStack ist, int i) {
		setShort("itemQuantity", ist, (short)i);
		updateItemDamage(ist);
	}

	protected int getQuantity(ItemStack ist) {
		return getShort("itemQuantity", ist);
	}

	protected boolean hasItem(ItemStack ist) {
		return getQuantity(ist) > 0;
	}

	protected void setCapacity(ItemStack ist, int i) {
		setShort("capacity", ist, (short)i);
		updateItemDamage(ist);
	}

	protected int getCapacity(ItemStack ist) {
		return getShort("capacity", ist);
	}

	protected ItemStack getTargetItem(ItemStack ist) {
		if (hasTarget(ist)) return new ItemStack(getShort("itemID", ist), 1, getShort("itemMeta", ist));
		else return null;
	}

	// returns true if the itemID of the target is there.
	protected boolean hasTarget(ItemStack ist) {
		return getShort("itemID", ist) != 0;
	}

	// returns false if item has NBT data, as those are invalid.
	// also returns false if the target item is null.
	protected boolean setTargetItem(ItemStack ist, ItemStack target) {
		if (target == null) return false;
		if (target.getTagCompound() != null) return false;
		setShort("itemID", ist, target.itemID);
		setShort("itemMeta", ist, target.getItemDamage());
		return true;
	}

	protected boolean findAndRemove(IInventory inventory, ItemStack ist) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack == null) {
				continue;
			}
			if (inventory.getStackInSlot(slot).isItemEqual(ist)) {
				inventory.decrStackSize(slot, 1);
				return true;
			}
		}
		return false;
	}

	protected void absorbTargetItem(ItemStack ist, IInventory inventory) {
		ItemStack target = getTargetItem(ist);
		if (target == null) return;
		if (isVoidItemFull(ist)) return;
		if (findAndRemove(inventory, target)) {
			increaseQuantity(ist);
		}
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List infoList, boolean par4) {
		NBTTagCompound tag = ist.getTagCompound();
		if (tag == null) return;
		String capacity = "Capacity: ";
		capacity += getCapacity(ist);
		String holding = "Holding: ";
		holding += getQuantity(ist);
		infoList.add(capacity);
		infoList.add(holding);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		super.onUpdate(ist, world, e, i, f);
		if (!isInitialized(ist) && getDefaultTargetItem() != null) {
			initialize(ist, getDefaultTargetItem());
		}
		if (world.isRemote) return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer)e;
		}
		if (player == null) return;
		if (!isActive(ist)) return;
		if (isVoidItemFull(ist)) return;
		absorbTargetItem(ist, player.inventory);
	}

	protected boolean isActive(ItemStack ist) {
		return getBoolean("isActive", ist);
	}

	protected void setIsActive(ItemStack ist, boolean b) {
		setBoolean("isActive", ist, b);
	}

	protected ItemStack getDefaultTargetItem() {
		return DEFAULT_TARGET_ITEM;
	}

	protected void initialize(ItemStack ist, ItemStack target) {
		setTargetItem(ist, target);
		setCapacity(ist, 64);
		setBoolean("initialized", ist, true);
	}

	protected boolean isInitialized(ItemStack ist) {
		return getBoolean("initialized", ist);
	}
}
