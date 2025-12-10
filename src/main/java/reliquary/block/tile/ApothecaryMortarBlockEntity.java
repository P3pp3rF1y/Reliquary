package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
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

	private final ItemStacksResourceHandler items = new ItemStacksResourceHandler(3) {

		@Override
		protected int getCapacity(int index, ItemResource resource) {
			return 1;
		}

		@Override
		public boolean isValid(int index, ItemResource resource) {
			//allow potion essence combinations
			if (PotionHelper.isItemEssence(resource.getItem())) {
				return true;
			}

			// don't allow essence/items in slots after the third one.
			//only allow valid potion items

			//also now doesn't allow the same item twice.
			for (int i = 0; i < size(); ++i) {
				if (getResource(i).isEmpty()) {
					continue;
				}
				if (getResource(i).getItem() == resource.getItem()) {
					return false;
				}
			}
			return PotionHelper.isIngredient(resource.getItem());
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
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
	protected void loadAdditional(ValueInput in) {
		super.loadAdditional(in);
		in.child("items").ifPresent(items::deserialize);
		pestleUsedCounter = in.getShortOr("pestleUsed", (short) 0);
	}

	@Override
	public void saveAdditional(ValueOutput out) {
		super.saveAdditional(out);
		out.putShort("pestleUsed", (short) pestleUsedCounter);
		out.putChild("items", items);
	}

	// gets the contents of the tile entity as an array of inventory
	public NonNullList<ItemStack> getItemStacks() {
		return items.copyToList();
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
			for (int clearSlot = 0; clearSlot < items.size(); ++clearSlot) {
				if (items.getResource(clearSlot).isEmpty()) {
					continue;
				}
				if (!level.isClientSide()) {
					ItemEntity itemEntity = new ItemEntity(level, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D, items.getResource(clearSlot).toStack());
					level.addFreshEntity(itemEntity);
				}
				items.set(clearSlot, ItemResource.EMPTY, 0);
			}
		} else {
			for (int clearSlot = 0; clearSlot < items.size(); ++clearSlot) {
				items.set(clearSlot, ItemResource.EMPTY, 0);
			}
			pestleUsedCounter = 0;
			finishCoolDown = level.getGameTime() + 20; // 1 second cooldown before essence can be put in to prevent insta insert of it
			if (level.isClientSide()) {
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

	public ResourceHandler<ItemResource> getItems() {
		return items;
	}

	public void dropItems(Level level) {
		InventoryHelper.dropInventoryItems(level, worldPosition, items);
	}

	public int getPestleUsedCounter() {
		return pestleUsedCounter;
	}
}
