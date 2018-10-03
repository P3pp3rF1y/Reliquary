package xreliquary.blocks.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xreliquary.compat.waila.provider.IWailaDataChangeIndicator;
import xreliquary.init.ModItems;
import xreliquary.util.InventoryHelper;
import xreliquary.util.WorldHelper;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileEntityMortar extends TileEntityBase implements IWailaDataChangeIndicator {

	private static final int PESTLE_USAGE_MAX = 5; // the number of times you have to use the pestle
	// counts the number of times the player has right clicked the block
	// arbitrarily setting the number of times the player needs to grind the
	// materials to five.
	private int pestleUsedCounter;
	private boolean dataChanged;
	private long finishCoolDown;

	private ItemStackHandler items = new ItemStackHandler(3) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return isItemValid(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}

		private boolean isItemValid(@Nonnull ItemStack ist) {
			// don't allow essence/items in slots after the third one.
			//only allow valid potion items

			//also now doesn't allow the same item twice.
			for(int i = 0; i < getSlots(); ++i) {
				if(this.getStackInSlot(i).isEmpty())
					continue;
				if(this.getStackInSlot(i).isItemEqual(ist))
					return false;
			}
			return XRPotionHelper.isItemIngredient(ist) || XRPotionHelper.isItemEssence(ist);
		}

		@Override
		protected void onContentsChanged(int slot) {
			dataChanged = true;
			WorldHelper.notifyBlockUpdate(TileEntityMortar.this);
		}
	};

	public TileEntityMortar() {
		super();
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
		items.deserializeNBT(tag.getCompoundTag("items"));
		pestleUsedCounter = tag.getShort("pestleUsed");
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("pestleUsed", (short) this.pestleUsedCounter);
		compound.setTag("items", items.serializeNBT());

		return compound;
	}

	// gets the contents of the tile entity as an array of inventory
	public NonNullList<ItemStack> getItemStacks() {
		return InventoryHelper.getItemStacks(items);
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
				for(int clearSlot = 0; clearSlot < items.getSlots(); ++clearSlot) {
					if(items.getStackInSlot(clearSlot).isEmpty())
						continue;
					if(!this.world.isRemote) {
						EntityItem itemEntity = new EntityItem(world, this.getPos().getX() + 0.5D, this.getPos().getY() + 0.5D, this.getPos().getZ() + 0.5D, items.getStackInSlot(clearSlot).copy());
						world.spawnEntity(itemEntity);
					}
					items.setStackInSlot(clearSlot, ItemStack.EMPTY);
				}
			} else {
				for(int clearSlot = 0; clearSlot < items.getSlots(); ++clearSlot) {
					items.setStackInSlot(clearSlot, ItemStack.EMPTY);
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
	public boolean getDataChanged() {
		boolean ret = this.dataChanged;
		this.dataChanged = false;
		return ret;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			//noinspection unchecked
			return (T) items;
		}

		return super.getCapability(capability, facing);
	}
}
