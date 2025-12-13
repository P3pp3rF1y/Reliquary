package reliquary.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import reliquary.init.ModItems;
import reliquary.reference.Compatibility;
import reliquary.util.PlayerInventoryProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

public class CuriosCompat {

	private static final EmptyCuriosHandler EMPTY_HANDLER = new EmptyCuriosHandler();

	private void addPlayerInventoryHandlers() {
		PlayerInventoryProvider.get().addPlayerInventoryHandler(Compatibility.ModIds.CURIOS, player -> CuriosApi.getSlots(false).keySet(),
				(player, identifier) -> getFromCuriosSlotStackHandler(player, identifier, ICurioStacksHandler::getSlots, 0),
				(player, identifier, slot) -> getFromCuriosSlotStackHandler(player, identifier, sh -> sh.getStacks().getStackInSlot(slot), ItemStack.EMPTY),
				(player, identifier, slot, stack) -> CuriosApi.getCuriosInventory(player).flatMap(h -> h.getStacksHandler(identifier)).ifPresent(sh -> sh.getStacks().setStackInSlot(slot, stack)),
						true);
	}

	public static <T> T getFromCuriosSlotStackHandler(LivingEntity livingEntity, String identifier, Function<ICurioStacksHandler, T> getFromHandler, T defaultValue) {
		return CuriosApi.getCuriosInventory(livingEntity)
				.map(h -> h.getStacksHandler(identifier).map(getFromHandler).orElse(defaultValue)).orElse(defaultValue);
	}

	public CuriosCompat(IEventBus modBus) {
		modBus.addListener(this::onRegisterCapabilities);

		if (FMLEnvironment.dist.isClient()) {
			CuriosCompatClient.registerLayerDefinitions(modBus);
		}

		addPlayerInventoryHandlers();
	}

	public void onRegisterCapabilities(RegisterCapabilitiesEvent evt) {
		evt.registerItem(
				CuriosCapability.ITEM,
				(itemStack, unused) -> new CuriosBaubleItemWrapper(itemStack),
				ModItems.FORTUNE_COIN.get(), ModItems.MOB_CHARM_BELT.get(), ModItems.TWILIGHT_CLOAK.get());
	}

	public static Optional<ItemStack> getStackInSlot(LivingEntity entity, String slotName, int slot) {
		return CuriosApi.getCuriosInventory(entity).flatMap(handler -> handler.getStacksHandler(slotName)
				.map(sh -> sh.getStacks().getStackInSlot(slot)));
	}

	public static void setStackInSlot(LivingEntity entity, String slotName, int slot, ItemStack stack) {
		CuriosApi.getCuriosInventory(entity).flatMap(handler -> handler.getStacksHandler(slotName)).ifPresent(sh -> sh.getStacks().setStackInSlot(slot, stack));
	}

	private static class EmptyCuriosHandler extends ItemStackHandler implements IDynamicStackHandler {
		@Override
		public void setPreviousStackInSlot(int i, @Nonnull ItemStack itemStack) {
			//noop
		}

		@Override
		public ItemStack getPreviousStackInSlot(int i) {
			return ItemStack.EMPTY;
		}

		@Override
		public void grow(int i) {
			//noop
		}

		@Override
		public void shrink(int i) {
			//noop
		}
	}
}
