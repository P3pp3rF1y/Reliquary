package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xreliquary.util.InventoryHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RandHelper;
import xreliquary.util.RegistryHelper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ToggleableItem extends ItemBase {

	protected static final String QUANTITY_TAG = "Quantity";
	protected static final String ITEM_NAME_TAG = "Name";
	private static final String ENABLED_TAG = "enabled";
	private static final String COOLDOWN_TAG = "coolDown";
	private static final String ITEMS_TAG = "Items";

	protected ToggleableItem(Properties properties, Supplier<Boolean> isDisabled) {
		super(properties, isDisabled);
	}

	protected ToggleableItem(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return isEnabled(stack);
	}

	protected void setCooldown(ItemStack stack, World world, int cooldown) {
		NBTHelper.putLong(COOLDOWN_TAG, stack, world.getGameTime() + cooldown);
	}

	protected boolean isInCooldown(ItemStack stack, World world) {
		return NBTHelper.getLong(COOLDOWN_TAG, stack) > world.getGameTime();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote && player.isSneaking()) {
			toggleEnabled(stack);
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.2F));
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || oldStack.hasEffect() != newStack.hasEffect();
	}

	public boolean isEnabled(ItemStack stack) {
		return NBTHelper.getBoolean(ENABLED_TAG, stack);
	}

	void toggleEnabled(ItemStack stack) {
		NBTHelper.putBoolean(ENABLED_TAG, stack, !NBTHelper.getBoolean(ENABLED_TAG, stack));
	}

	protected void consumeAndCharge(PlayerEntity player, int freeCapacity, int chargePerItem, Item item, int maxCount, IntConsumer addCharge) {
		consumeAndCharge(player, freeCapacity, chargePerItem, ist -> ist.getItem() == item, maxCount, addCharge);
	}

	protected void consumeAndCharge(PlayerEntity player, int freeCapacity, int chargePerItem, Predicate<ItemStack> itemMatches, int maxCount, IntConsumer addCharge) {
		int maximumToConsume = Math.min(freeCapacity / chargePerItem, maxCount);
		if (maximumToConsume == 0) {
			return;
		}
		int chargeToAdd = InventoryHelper.consumeItemStack(itemMatches, player, maximumToConsume).getCount() * chargePerItem;
		if (chargeToAdd > 0) {
			addCharge.accept(chargeToAdd);
		}
	}

	protected void addItemToInternalStorage(ItemStack stack, Item item, int chargeToAdd) {
		addItemToInternalStorage(stack, RegistryHelper.getItemRegistryName(item), chargeToAdd);
	}

	protected void addItemToInternalStorage(ItemStack stack, String itemRegistryName, int chargeToAdd) {
		AtomicBoolean found = new AtomicBoolean(false);
		updateItems(stack, tag -> {
			String itemName = tag.getString(ITEM_NAME_TAG);
			if (itemName.equals(itemRegistryName)) {
				int quantity = tag.getInt(QUANTITY_TAG);
				tag.putInt(QUANTITY_TAG, quantity + chargeToAdd);
				found.set(true);
			}
		}, found::get, list -> {
			if (!found.get()) {
				CompoundNBT newTagData = new CompoundNBT();
				newTagData.putString(ITEM_NAME_TAG, itemRegistryName);
				newTagData.putInt(QUANTITY_TAG, chargeToAdd);
				list.add(newTagData);
			}
		});
	}

	private void updateItems(ItemStack stack, Consumer<CompoundNBT> actOnItemTag, BooleanSupplier shouldExit, Consumer<ListNBT> actOnListAfter) {
		CompoundNBT tag = NBTHelper.getTag(stack);
		iterateItems(tag, actOnItemTag, shouldExit, itemList -> {
			tag.put(ITEMS_TAG, itemList);
			actOnListAfter.accept(itemList);
		});
		stack.setTag(tag);
	}

	protected void iterateItems(ItemStack stack, Consumer<CompoundNBT> actOnItemTag, BooleanSupplier shouldExit) {
		iterateItems(NBTHelper.getTag(stack), actOnItemTag, shouldExit, list -> {});
	}

	private void iterateItems(CompoundNBT tagCompound, Consumer<CompoundNBT> actOnItemTag, BooleanSupplier shouldExit, Consumer<ListNBT> actOnListAfter) {
		ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);
		for (int i = 0; i < tagList.size(); ++i) {
			actOnItemTag.accept(tagList.getCompound(i));
			if (shouldExit.getAsBoolean()) {
				break;
			}
		}
		actOnListAfter.accept(tagList);
	}

	public boolean removeItemFromInternalStorage(ItemStack stack, ForgeRegistryEntry<?> registryEntry, int quantityToRemove, boolean simulate, PlayerEntity player) {
		if (player.isCreative()) {
			return true;
		}
		AtomicBoolean updated = new AtomicBoolean(false);
		updateItems(stack, tag -> {
			String itemName = tag.getString(ITEM_NAME_TAG);
			if (itemName.equals(RegistryHelper.getRegistryName(registryEntry).toString())) {
				int originalQuantity = tag.getInt(QUANTITY_TAG);
				if (!simulate) {
					tag.putInt(QUANTITY_TAG, originalQuantity - quantityToRemove);
				}
				updated.set(true);
			}
		}, updated::get, list -> {});
		return updated.get();
	}

	public int getInternalStorageItemCount(ItemStack stack, Item item) {
		return getInternalStorageItemCount(stack, RegistryHelper.getItemRegistryName(item));
	}
	public int getInternalStorageItemCount(ItemStack stack, String itemRegistryName) {
		AtomicInteger ret = new AtomicInteger(-1);
		iterateItems(stack, tag -> {
			if (tag.getString(ITEM_NAME_TAG).equals(itemRegistryName)) {
				ret.set(tag.getInt(QUANTITY_TAG));
			}
		}, () -> ret.get() >= 0);

		return Math.max(ret.get(), 0);
	}

	protected boolean hasSpaceForItem(ItemStack stack, Item item, int quantityLimit) {
		return getInternalStorageItemCount(stack, item) < quantityLimit;
	}
}
