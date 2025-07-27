package reliquary.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import reliquary.common.gui.MobCharmBeltMenu;
import reliquary.item.util.ICuriosItem;
import reliquary.reference.Config;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobCharmBeltItem extends ItemBase implements ICuriosItem {
	public MobCharmBeltItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public ICuriosItem.Type getCuriosType() {
		return ICuriosItem.Type.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity player) {
		//noop
	}

	@Override
	public void onEquipped(String identifier, LivingEntity player) {
		if (player.level().isClientSide) {
			player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1F, 1F);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isCrouching()) {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}

		if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(new SimpleMenuProvider((w, p, pl) -> new MobCharmBeltMenu(w, p, stack), stack.getHoverName()), buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	public ItemStack getMobCharmInSlot(ItemStack belt, int slotIndex) {
		return getFromHandler(belt, handler -> slotIndex < handler.getSlots() ? handler.getStackInSlot(slotIndex) : ItemStack.EMPTY);
	}

	public void putMobCharmInSlot(ItemStack belt, int slotIndex, ItemStack mobCharm) {
		if (mobCharm.isEmpty()) {
			removeMobCharmInSlot(belt, slotIndex);
			return;
		}

		runOnHandler(belt, handler -> {
			if (slotIndex >= handler.getSlots()) {
				handler.insertIntoNewSlot(mobCharm);
			} else {
				handler.setStackInSlot(slotIndex, mobCharm);
			}
		});
	}

	public ItemStack removeMobCharmInSlot(ItemStack belt, int slotIndex) {
		return getFromHandler(belt, handler -> slotIndex < handler.getSlots() ? handler.removeStackAndSlot(slotIndex) : ItemStack.EMPTY);
	}

	public int getCharmCount(ItemStack belt) {
		return getFromHandler(belt, MobCharmComponentItemHandler::getSlots);
	}

	public boolean hasCharm(ItemStack belt, ResourceLocation entityRegistryName) {
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

	ItemStack damageCharm(Player player, ItemStack belt, ResourceLocation entityRegistryName) {
		return getFromHandler(belt, handler -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack charmStack = handler.getStackInSlot(i);
				if (MobCharmItem.isCharmFor(charmStack, entityRegistryName)) {
					charmStack.hurtAndBreak(Config.COMMON.items.mobCharm.damagePerKill.get(), player, EquipmentSlot.CHEST);
					if (charmStack.isEmpty()) {
						handler.removeStackAndSlot(i);
						return ItemStack.EMPTY;
					} else {
						handler.setStackInSlot(i, charmStack);
						return charmStack;
					}
				}
			}
			return ItemStack.EMPTY;
		});
	}

	public Set<ResourceLocation> getCharmRegistryNames(ItemStack slotStack) {
		return getFromHandler(slotStack, handler -> {
			Set<ResourceLocation> ret = new HashSet<>();
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack charmStack = handler.getStackInSlot(i);
				ret.add(MobCharmItem.getEntityEggRegistryName(charmStack));
			}
			return ret;
		});
	}

	private <T> T getFromHandler(ItemStack stack, Function<MobCharmComponentItemHandler, T> getter) {
		return getter.apply(new MobCharmComponentItemHandler(stack));
	}

	private void runOnHandler(ItemStack stack, Consumer<MobCharmComponentItemHandler> runner) {
		runner.accept(new MobCharmComponentItemHandler(stack));
	}

	private static class MobCharmComponentItemHandler extends ComponentItemHandler {
		public MobCharmComponentItemHandler(MutableDataComponentHolder parent) {
			super(parent, DataComponents.CONTAINER, getSlots(parent));
		}

		private static int getSlots(MutableDataComponentHolder parent) {
			return parent.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).getSlots();
		}

		public void insertIntoNewSlot(ItemStack stack) {
			ItemContainerContents contents = this.getContents();
			NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), this.getSlots() + 1), ItemStack.EMPTY);
			contents.copyInto(list);
			list.set(getSlots(), stack);
			this.parent.set(this.component, ItemContainerContents.fromItems(list));
			this.onContentsChanged(list.size() - 1, ItemStack.EMPTY, stack);
		}

		public ItemStack removeStackAndSlot(int slot) {
			ItemContainerContents contents = this.getContents();
			NonNullList<ItemStack> list = NonNullList.withSize(this.getSlots() - 1, ItemStack.EMPTY);
			ItemStack stack = contents.getStackInSlot(slot);
			for (int i = 0; i < slot; i++) {
				list.set(i, contents.getSlots() > i ? contents.getStackInSlot(i) : ItemStack.EMPTY);
			}
			for (int i = slot; i < list.size(); i++) {
				list.set(i, contents.getSlots() > i + 1 ? contents.getStackInSlot(i + 1) : ItemStack.EMPTY);
			}
			this.parent.set(this.component, ItemContainerContents.fromItems(list));
			this.onContentsChanged(slot, stack, ItemStack.EMPTY);
			return stack;
		}
	}
}
