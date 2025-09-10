package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import reliquary.compat.jade.provider.IJadeDataChangeIndicator;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionIngredient;

import java.util.ArrayList;
import java.util.List;

public class ApothecaryMortarBlockEntity extends BlockEntityBase implements IJadeDataChangeIndicator {
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
			//allow potion essence combinations
			if (PotionHelper.isItemEssence(stack)) {
				return true;
			}

			// don't allow essence/items in slots after the third one.
			//only allow valid potion items

			//also now doesn't allow the same item twice.
			for (int i = 0; i < getSlots(); ++i) {
				if (getStackInSlot(i).isEmpty()) {
					continue;
				}
				if (getStackInSlot(i).getItem() == stack.getItem()) {
					return false;
				}
			}
			return PotionHelper.isIngredient(stack);
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
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		items.deserializeNBT(registries, tag.getCompound("items"));
		pestleUsedCounter = tag.getShort("pestleUsed");
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);
		compound.putShort("pestleUsed", (short) pestleUsedCounter);
		compound.put("items", items.serializeNBT(registries));
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
			PotionHelper.getIngredient(item).ifPresent(potionIngredients::add);
		}
		if (itemCount > 1) {
			pestleUsedCounter++;
			spawnPestleParticles(level);
		}
		return pestleUsedCounter >= PESTLE_USAGE_MAX && createPotionEssence(potionIngredients, level);
	}

	private boolean createPotionEssence(List<PotionIngredient> potionIngredients, Level level) {
		PotionContents potionContents = PotionHelper.combineIngredients(potionIngredients);
		if (!potionContents.hasEffects()) {
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
			PotionHelper.addPotionContentsToStack(resultItem, potionContents);

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

	public IItemHandler getItems() {
		return items;
	}

	public void dropItems(Level level) {
		InventoryHelper.dropInventoryItems(level, worldPosition, items);
	}

	public int getPestleUsedCounter() {
		return pestleUsedCounter;
	}
}
