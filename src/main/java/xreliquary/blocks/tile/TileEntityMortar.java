package xreliquary.blocks.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import xreliquary.compat.waila.provider.IWailaDataChangeIndicator;
import xreliquary.init.ModItems;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileEntityMortar extends TileEntityInventory implements IWailaDataChangeIndicator {

	private static final int PESTLE_USAGE_MAX = 5; // the number of times you have to use the pestle
	// counts the number of times the player has right clicked the block
	// arbitrarily setting the number of times the player needs to grind the
	// materials to five.
	private int pestleUsedCounter;
	private String customInventoryName;
	private boolean dataChanged;
	private long finishCoolDown;

	public TileEntityMortar() {
		//inventory size
		super(3);
		pestleUsedCounter = 0;
		dataChanged = true;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10);
		this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte b0 = item.getByte("Slot");

			if(b0 >= 0 && b0 < this.getSizeInventory()) {
				this.inventory.set(b0, new ItemStack(item));
			}
		}

		this.pestleUsedCounter = tag.getShort("pestleUsed");

		if(tag.hasKey("CustomName", 8)) {
			this.customInventoryName = tag.getString("CustomName");
		}
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("pestleUsed", (short) this.pestleUsedCounter);
		NBTTagList items = new NBTTagList();

		for(int slot = 0; slot < this.inventory.size(); ++slot) {
			if(!this.inventory.get(slot).isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();
				this.inventory.get(slot).writeToNBT(item);
				item.setByte("Slot", (byte) slot);
				items.appendTag(item);
			}
		}

		compound.setTag("Items", items);

		if(this.hasCustomName()) {
			compound.setString("CustomName", this.getName());
		}

		return compound;
	}

	// gets the contents of the tile entity as an array of inventory
	public NonNullList<ItemStack> getItemStacks() {
		return inventory;
	}

	// increases the "pestleUsed" counter, checks to see if it is at its limit
	public boolean usePestle() {
		int itemCount = 0;
		List<PotionIngredient> potionIngredients = new ArrayList<>();
		for(ItemStack item : this.getItemStacks()) {
			if(item.isEmpty())
				continue;
			++itemCount;
			potionIngredients.add(XRPotionHelper.getIngredient(item));
		}
		if(itemCount > 1) {
			pestleUsedCounter++;
			spawnPestleParticles();
		}
		if(pestleUsedCounter >= PESTLE_USAGE_MAX) {
			//we've "maxed" the pestle counter and we need to see if the essence would contain potion effects.
			//if it doesn't, just return the ingredients to the player, we are nice like that.
			List<PotionEffect> resultEffects = XRPotionHelper.combineIngredients(potionIngredients);
			if(resultEffects.isEmpty()) {
				pestleUsedCounter = 0;
				for(int clearSlot = 0; clearSlot < this.getSizeInventory(); ++clearSlot) {
					if(this.getStackInSlot(clearSlot).isEmpty())
						continue;
					if(!this.world.isRemote) {
						EntityItem itemEntity = new EntityItem(world, this.getPos().getX() + 0.5D, this.getPos().getY() + 0.5D, this.getPos().getZ() + 0.5D, this.getStackInSlot(clearSlot).copy());
						world.spawnEntity(itemEntity);
					}
					this.setInventorySlotContents(clearSlot, ItemStack.EMPTY);
				}
			} else {
				for(int clearSlot = 0; clearSlot < this.getSizeInventory(); ++clearSlot) {
					this.setInventorySlotContents(clearSlot, ItemStack.EMPTY);
				}
				pestleUsedCounter = 0;
				this.finishCoolDown = this.world.getTotalWorldTime() + 20; // 1 second cooldown before essence can be put in to prevent insta insert of it
				if(world.isRemote)
					return true;
				ItemStack resultItem = new ItemStack(ModItems.potionEssence, 1, 0);
				XRPotionHelper.addPotionEffectsToStack(resultItem, resultEffects);

				EntityItem itemEntity = new EntityItem(world, this.getPos().getX() + 0.5D, this.getPos().getY() + 0.5D, this.getPos().getZ() + 0.5D, resultItem);
				world.spawnEntity(itemEntity);
			}
			markDirty();
			return true;
		}
		return false;
	}

	private void spawnPestleParticles() {
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.getPos().getX() + 0.5D, this.getPos().getY() + 0.15D, this.getPos().getZ() + 0.5D, 0.0D, 0.1D, 0.0D);
	}

	public boolean isInCooldown() {
		return this.world.getTotalWorldTime() < finishCoolDown;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		this.dataChanged = true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return this.world.getTileEntity(this.getPos()) == this && player.getDistanceSq((double) this.getPos().getX() + 0.5D, (double) this.getPos().getY() + 0.5D, (double) this.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {
	}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack ist) {
		// don't allow essence/items in slots after the third one.
		//only allow valid potion items

		//also now doesn't allow the same item twice.
		for(int i = 0; i < this.getSizeInventory(); ++i) {
			if(this.getStackInSlot(i).isEmpty())
				continue;
			if(this.getStackInSlot(i).isItemEqual(ist))
				return false;
		}
		return slot <= 3 && (XRPotionHelper.isItemIngredient(ist) || XRPotionHelper.isItemEssence(ist));
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Nonnull
	@Override
	public String getName() {
		return this.hasCustomName() ? this.customInventoryName : "container.tile_entity_mortar";
	}

	@Override
	public boolean hasCustomName() {
		return customInventoryName != null;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
	}

	@Override
	public boolean getDataChanged() {
		boolean ret = this.dataChanged;
		this.dataChanged = false;
		return ret;
	}
}
