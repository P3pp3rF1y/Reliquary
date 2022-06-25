package reliquary.blocks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import reliquary.compat.waila.provider.IWailaDataChangeIndicator;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;
import reliquary.util.potions.PotionIngredient;
import reliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ApothecaryMortarBlockEntity extends BlockEntityBase implements IWailaDataChangeIndicator {
	public static final int PESTLE_USAGE_MAX = 5; // the number of times you have to use the pestle
	// counts the number of times the player has right clicked the block
	// arbitrarily setting the number of times the player needs to grind the
	// materials to five.
	private int pestleUsedCounter;
	private boolean dataChanged;
	private long finishCoolDown;

	private final ItemStackHandler items = new ItemStackHandler(3) {
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
				if (getStackInSlot(i).isEmpty()) {
					continue;
				}
				if (getStackInSlot(i).sameItem(stack)) {
					return false;
				}
			}
			return XRPotionHelper.isIngredient(stack) || XRPotionHelper.isItemEssence(stack);
		}

		@Override
		protected void onContentsChanged(int slot) {
			dataChanged = true;
			WorldHelper.notifyBlockUpdate(ApothecaryMortarBlockEntity.this);
		}
	};

	public ApothecaryMortarBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.APOTHECARY_MORTAR_TILE_TYPE.get(), pos, state);
		pestleUsedCounter = 0;
		dataChanged = true;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		items.deserializeNBT(tag.getCompound("items"));
		pestleUsedCounter = tag.getShort("pestleUsed");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putShort("pestleUsed", (short) pestleUsedCounter);
		compound.put("items", items.serializeNBT());
	}

	// gets the contents of the tile entity as an array of inventory
	public NonNullList<ItemStack> getItemStacks() {
		return InventoryHelper.getItemStacks(items);
	}

	// increases the "pestleUsed" counter, checks to see if it is at its limit
	public boolean usePestle(Level level) {
		int itemCount = 0;
		List<PotionIngredient> potionIngredients = new ArrayList<>();
		for (ItemStack item : getItemStacks()) {
			if (item.isEmpty()) {
				continue;
			}
			++itemCount;
			XRPotionHelper.getIngredient(item).ifPresent(potionIngredients::add);
		}
		if (itemCount > 1) {
			pestleUsedCounter++;
			spawnPestleParticles(level);
		}
		return pestleUsedCounter >= PESTLE_USAGE_MAX && createPotionEssence(potionIngredients, level);
	}

	private boolean createPotionEssence(List<PotionIngredient> potionIngredients, Level level) {
		List<MobEffectInstance> resultEffects = XRPotionHelper.combineIngredients(potionIngredients);
		if (resultEffects.isEmpty()) {
			pestleUsedCounter = 0;
			for (int clearSlot = 0; clearSlot < items.getSlots(); ++clearSlot) {
				if (items.getStackInSlot(clearSlot).isEmpty()) {
					continue;
				}
				if (!level.isClientSide) {
					ItemEntity itemEntity = new ItemEntity(level, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D, items.getStackInSlot(clearSlot).copy());
					level.addFreshEntity(itemEntity);
				}
				items.setStackInSlot(clearSlot, ItemStack.EMPTY);
			}
		} else {
			for (int clearSlot = 0; clearSlot < items.getSlots(); ++clearSlot) {
				items.setStackInSlot(clearSlot, ItemStack.EMPTY);
			}
			pestleUsedCounter = 0;
			finishCoolDown = level.getGameTime() + 20; // 1 second cooldown before essence can be put in to prevent insta insert of it
			if (level.isClientSide) {
				return true;
			}
			ItemStack resultItem = new ItemStack(ModItems.POTION_ESSENCE.get());
			XRPotionHelper.addPotionEffectsToStack(resultItem, resultEffects);

			ItemEntity itemEntity = new ItemEntity(level, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D, resultItem);
			level.addFreshEntity(itemEntity);
		}
		setChanged();
		return false;
	}

	private void spawnPestleParticles(Level level) {
		level.addParticle(ParticleTypes.SMOKE, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.15D, getBlockPos().getZ() + 0.5D, 0.0D, 0.1D, 0.0D);
	}

	public boolean isInCooldown(Level level) {
		return level.getGameTime() < finishCoolDown;
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

	public void dropItems(Level level) {
		InventoryHelper.dropInventoryItems(level, worldPosition, items);
	}

	public int getPestleUsedCounter() {
		return pestleUsedCounter;
	}
}
