package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVoidSatchel extends ItemBase implements IVoidUpgradable {

	public ItemVoidSatchel() {
		super(Reference.MOD_ID, Names.VOID_SATCHEL_NAME);
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
	public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		NBTTagCompound tag = ist.getTagCompound();
		String holds = null;
		String capacity = null;
		if (tag == null || new ItemStack(Block.getBlockFromName(tag.getString("itemID")), 1, tag.getShort("itemMeta")).getItem() == null) {
			holds = "nothing";
			capacity = String.valueOf(Reference.CAPACITY_UPGRADE_INCREMENT);
		} else {
			ItemStack contents = new ItemStack(Block.getBlockFromName(tag.getString("itemID")), 1, tag.getShort("itemMeta"));
			String itemName = contents.getDisplayName();
			holds = tag.getShort("itemQuantity") + "x " + itemName;
			capacity = String.valueOf(this.getCapacity(ist));
		}
		this.formatTooltip(ImmutableMap.of("holds", holds, "capacity", capacity), ist, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return getIsTurnedOn(stack);
	}

	private boolean getIsTurnedOn(ItemStack stack) {
		return NBTHelper.getBoolean("isTurnedOn", stack);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if (world.isRemote)
			return;
		if (!getIsTurnedOn(ist))
			return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if (player == null)
			return;
		IInventory inventory = player.inventory;
		compressInventoryForPlayer(ist, inventory);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		NBTHelper.setBoolean("isTurnedOn", ist, !getIsTurnedOn(ist));
		return ist;
	}

	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
		// spew contents onto ground only if aiming at top of a block. Else,
		// toggle satchel.
		if (side == 1) {
			NBTHelper.setBoolean("isTurnedOn", ist, false);
			int quantity = NBTHelper.getShort("itemQuantity", ist) >= 64 ? player.isSneaking() ? NBTHelper.getShort("itemQuantity", ist) : 64 : NBTHelper.getShort("itemQuantity", ist);
			int depletedBy = 0;
			while (quantity > 0) {
                if(ist.getTagCompound() == null)
                    return true;
				ItemStack contents = new ItemStack(Block.getBlockFromName(ist.getTagCompound().getString("itemID")), 1, NBTHelper.getShort("itemMeta", ist));
				if (quantity > contents.getMaxStackSize()) {
					contents.stackSize = contents.getMaxStackSize();
					quantity -= contents.getMaxStackSize();
					depletedBy += contents.getMaxStackSize();
				} else {
					contents.stackSize = quantity;
					depletedBy += quantity;
					quantity = 0;
				}
				if (world.isRemote) {
					continue;
				}
				EntityItem item = new EntityItem(world, x + 0.5F, y + 1.5F, z + 0.5F, contents);
				world.spawnEntityInWorld(item);
			}
			NBTHelper.setShort("itemQuantity", ist, NBTHelper.getShort("itemQuantity", ist) - depletedBy);
		}
		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(x, y, z) instanceof IInventory) {
			IInventory inventory = (IInventory) world.getTileEntity(x, y, z);
			if (player.isSneaking()) {
				compressInventoryForBlock(ist, inventory);
			} else {
				unloadContentsIntoInventory(ist, inventory, player);
			}
			return false;
		}
		return false;
	}

	public void unloadContentsIntoInventory(ItemStack ist, IInventory inventory, EntityPlayer player) {
		NBTTagCompound satchelTag = ist.getTagCompound();
		if (satchelTag == null)
			return;
		ItemStack contents = new ItemStack(Block.getBlockFromName(satchelTag.getString("itemID")), 1, satchelTag.getShort("itemMeta"));
		int quantity = satchelTag.getShort("itemQuantity");
		while (quantity > 0) {
			if (!tryToAddToInventory(contents, inventory)) {
				break;
			}
			quantity--;
		}
		player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
		satchelTag.setShort("itemQuantity", (short) quantity);
		ist.setTagCompound(satchelTag);
	}

	public boolean tryToAddToInventory(ItemStack contents, IInventory inventory) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			if (inventory.getStackInSlot(slot) == null) {
				continue;
			}
			if (inventory.getStackInSlot(slot).isItemEqual(contents)) {
				if (inventory.getStackInSlot(slot).stackSize == inventory.getStackInSlot(slot).getMaxStackSize()) {
					continue;
				}
				inventory.getStackInSlot(slot).stackSize++;
				return true;
			}
		}
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			if (inventory.getStackInSlot(slot) == null) {
				inventory.setInventorySlotContents(slot, new ItemStack(contents.getItem(), contents.stackSize, contents.getItemDamage()));
				return true;
			}
		}
		return false;
	}

	public void compressInventoryForPlayer(ItemStack ist, IInventory inventory) {
		ItemStack target = getTargetItem(ist);
		if (target == null)
			return;
		if (isSatchelFull(ist))
			return;
		if (findAndRemove(inventory, target)) {
			increaseSatchelQuantity(ist);
		}
	}

	public void compressInventoryForBlock(ItemStack ist, IInventory inventory) {
		ItemStack target = getTargetItem(ist);
		if (target == null)
			return;
		while (!isSatchelFull(ist) && findAndRemove(inventory, target)) {
			increaseSatchelQuantity(ist);
		}
	}

	private boolean isSatchelFull(ItemStack ist) {
		return NBTHelper.getShort("itemQuantity", ist) >= getCapacity(ist);
	}

	public void decreaseSatchelQuantity(ItemStack ist) {
		NBTHelper.setShort("itemQuantity", ist, NBTHelper.getShort("itemQuantity", ist) - 1);
	}

	public void increaseSatchelQuantity(ItemStack ist) {
		NBTHelper.setShort("itemQuantity", ist, NBTHelper.getShort("itemQuantity", ist) + 1);
	}

	public boolean findAndRemove(IInventory inventory, ItemStack ist) {
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

	public ItemStack getTargetItem(ItemStack ist) {
        if(ist.getTagCompound() == null)
            return null;
		return new ItemStack(Block.getBlockFromName(ist.getTagCompound().getString("itemID")), 1, NBTHelper.getShort("itemMeta", ist));
	}

	@Override
	public int getCapacity(ItemStack ist) {
		NBTTagCompound tag = ist.getTagCompound();
		if (tag == null)
			return 0;
		else
			return tag.getShort("capacity");
	}

	@Override
	public boolean upgradeCapacity(ItemStack ist) {
		return getCapacity(ist) < 32000;
	}
}
