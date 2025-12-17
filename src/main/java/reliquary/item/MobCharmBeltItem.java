package reliquary.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import reliquary.common.gui.MobCharmBeltMenu;
import reliquary.item.util.ICuriosItem;
import reliquary.reference.Config;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobCharmBeltItem extends ItemBase implements ICuriosItem {
	public MobCharmBeltItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public ICuriosItem.Type getCuriosType() {
		return ICuriosItem.Type.BELT;
	}

	@Override
	public void onEquipped(String identifier, LivingEntity player) {
		if (player.level().isClientSide()) {
			player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1F, 1F);
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isCrouching()) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(new SimpleMenuProvider((w, p, pl) -> new MobCharmBeltMenu(w, p, stack), stack.getHoverName()), buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
		}

		return InteractionResult.SUCCESS;
	}

	public ItemStack getMobCharmInSlot(ItemStack belt, int slotIndex) {
		return getFromHandler(belt, contents -> slotIndex < contents.getSlots() ? contents.getStackInSlot(slotIndex) : ItemStack.EMPTY);
	}

	public void putMobCharmInSlot(ItemStack belt, int slotIndex, ItemStack mobCharm) {
		if (mobCharm.isEmpty()) {
			removeMobCharmInSlot(belt, slotIndex);
			return;
		}

		runOnHandler(belt, contents -> {
			if (slotIndex >= contents.getSlots()) {
				insertIntoNewSlot(belt, contents, mobCharm);
			} else {
				setStackInSlot(belt, contents, slotIndex, mobCharm);
			}
		});
	}

	public ItemStack removeMobCharmInSlot(ItemStack belt, int slotIndex) {
		return getFromHandler(belt, contents -> {
			ItemStack result;
			if (slotIndex < contents.getSlots()) {
				result = removeStackAndSlot(belt, contents, slotIndex);
			} else {
				result = ItemStack.EMPTY;
			}
			return result;
		});
	}

	public int getCharmCount(ItemStack belt) {
		return getFromHandler(belt, ItemContainerContents::getSlots);
	}

	public boolean hasCharm(ItemStack belt, Identifier entityRegistryName) {
		return getFromHandler(belt, handler -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack charmStack = handler.getStackInSlot(i);
				if (MobCharmItem.isCharmFor(charmStack, entityRegistryName)) {
					return true;
				}
			}
			return false;
		});
	}

	ItemStack damageCharm(Player player, ItemStack belt, Identifier entityRegistryName) {
		return getFromHandler(belt, handler -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack charmStack = handler.getStackInSlot(i);
				if (MobCharmItem.isCharmFor(charmStack, entityRegistryName)) {
					charmStack.hurtAndBreak(Config.COMMON.items.mobCharm.damagePerKill.get(), player, EquipmentSlot.CHEST);
					if (charmStack.isEmpty()) {
						removeStackAndSlot(belt, handler, i);
						return ItemStack.EMPTY;
					} else {
						setStackInSlot(belt, handler, i, charmStack);
						return charmStack;
					}
				}
			}
			return ItemStack.EMPTY;
		});
	}

	public Set<Identifier> getCharmRegistryNames(ItemStack slotStack) {
		return getFromHandler(slotStack, handler -> {
			Set<Identifier> ret = new HashSet<>();
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack charmStack = handler.getStackInSlot(i);
				ret.add(MobCharmItem.getEntityEggRegistryName(charmStack));
			}
			return ret;
		});
	}

	private <T> T getFromHandler(ItemStack stack, Function<ItemContainerContents, T> getter) {
		return getter.apply(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
	}

	private void runOnHandler(ItemStack stack, Consumer<ItemContainerContents> runner) {
		runner.accept(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
	}

	private static void insertIntoNewSlot(ItemStack belt, ItemContainerContents contents, ItemStack stack) {
		NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), contents.getSlots() + 1), ItemStack.EMPTY);
		contents.copyInto(list);
		list.set(contents.getSlots(), stack);
		belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
	}

	private static ItemStack removeStackAndSlot(ItemStack belt, ItemContainerContents contents, int slot) {
		NonNullList<ItemStack> list = NonNullList.withSize(contents.getSlots() - 1, ItemStack.EMPTY);
		ItemStack stack = contents.getStackInSlot(slot);
		for (int i = 0; i < slot; i++) {
			list.set(i, contents.getSlots() > i ? contents.getStackInSlot(i) : ItemStack.EMPTY);
		}
		for (int i = slot; i < list.size(); i++) {
			list.set(i, contents.getSlots() > i + 1 ? contents.getStackInSlot(i + 1) : ItemStack.EMPTY);
		}
		belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
		return stack;
	}

	private static void setStackInSlot(ItemStack belt, ItemContainerContents contents, int slotIndex, ItemStack mobCharm) {
		NonNullList<ItemStack> updatedList = NonNullList.withSize(contents.getSlots(), ItemStack.EMPTY);
		contents.copyInto(updatedList);
		updatedList.set(slotIndex, mobCharm);
		belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(updatedList));
	}
}
