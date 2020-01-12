package xreliquary.blocks.tile;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.compat.waila.provider.IWailaDataChangeIndicator;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.util.InjectionHelper;
import xreliquary.util.InventoryHelper;
import xreliquary.util.WorldHelper;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ApothecaryMortarTileEntity extends TileEntityBase implements IWailaDataChangeIndicator {
	@SuppressWarnings("WeakerAccess")
	@ObjectHolder(Reference.MOD_ID + ":" + Names.Blocks.APOTHECARY_MORTAR)
	public static final TileEntityType<ApothecaryMortarTileEntity> TYPE = InjectionHelper.nullValue();
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

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return isItemValid(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}

		private boolean isItemValid(ItemStack stack) {
			// don't allow essence/items in slots after the third one.
			//only allow valid potion items

			//also now doesn't allow the same item twice.
			for (int i = 0; i < getSlots(); ++i) {
				if (getStackInSlot(i).isEmpty())
					continue;
				if (getStackInSlot(i).isItemEqual(stack))
					return false;
			}
			return XRPotionHelper.isIngredient(stack) || XRPotionHelper.isItemEssence(stack);
		}

		@Override
		protected void onContentsChanged(int slot) {
			dataChanged = true;
			WorldHelper.notifyBlockUpdate(ApothecaryMortarTileEntity.this);
		}
	};

	public ApothecaryMortarTileEntity() {
		super(TYPE);
		pestleUsedCounter = 0;
		dataChanged = true;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		items.deserializeNBT(tag.getCompound("items"));
		pestleUsedCounter = tag.getShort("pestleUsed");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putShort("pestleUsed", (short) pestleUsedCounter);
		compound.put("items", items.serializeNBT());

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
		for (ItemStack item : getItemStacks()) {
			if (item.isEmpty())
				continue;
			++itemCount;
			potionIngredients.add(XRPotionHelper.getIngredient(item));
		}
		if (itemCount > 1) {
			pestleUsedCounter++;
			spawnPestleParticles();
		}
		return pestleUsedCounter >= PESTLE_USAGE_MAX && createPotionEssence(potionIngredients);
	}

	private boolean createPotionEssence(List<PotionIngredient> potionIngredients) {
		List<EffectInstance> resultEffects = XRPotionHelper.combineIngredients(potionIngredients);
		if (resultEffects.isEmpty()) {
			pestleUsedCounter = 0;
			for (int clearSlot = 0; clearSlot < items.getSlots(); ++clearSlot) {
				if (items.getStackInSlot(clearSlot).isEmpty())
					continue;
				if (!world.isRemote) {
					ItemEntity itemEntity = new ItemEntity(world, getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + 0.5D, items.getStackInSlot(clearSlot).copy());
					world.addEntity(itemEntity);
				}
				items.setStackInSlot(clearSlot, ItemStack.EMPTY);
			}
		} else {
			for (int clearSlot = 0; clearSlot < items.getSlots(); ++clearSlot) {
				items.setStackInSlot(clearSlot, ItemStack.EMPTY);
			}
			pestleUsedCounter = 0;
			finishCoolDown = world.getGameTime() + 20; // 1 second cooldown before essence can be put in to prevent insta insert of it
			if (world.isRemote)
				return true;
			ItemStack resultItem = new ItemStack(ModItems.POTION_ESSENCE);
			XRPotionHelper.addPotionEffectsToStack(resultItem, resultEffects);

			ItemEntity itemEntity = new ItemEntity(world, getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + 0.5D, resultItem);
			world.addEntity(itemEntity);
		}
		markDirty();
		return false;
	}

	private void spawnPestleParticles() {
		world.addParticle(ParticleTypes.SMOKE, getPos().getX() + 0.5D, getPos().getY() + 0.15D, getPos().getZ() + 0.5D, 0.0D, 0.1D, 0.0D);
	}

	public boolean isInCooldown() {
		return world.getGameTime() < finishCoolDown;
	}

	@Override
	public boolean getDataChanged() {
		boolean ret = dataChanged;
		dataChanged = false;
		return ret;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> items));
		}

		return super.getCapability(cap, side);
	}
}
