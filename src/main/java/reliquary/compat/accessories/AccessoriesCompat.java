package reliquary.compat.accessories;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import reliquary.init.ModItems;
import reliquary.reference.Compatibility;
import reliquary.util.PlayerInventoryProvider;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AccessoriesCompat {
	private final Set<String> containerNames = new CopyOnWriteArraySet<>();
	private long lastTagsRefresh = -1;
	private static final int TAGS_REFRESH_COOLDOWN = 100;
	private static final Accessory NO_QUICK_EQUIP_ACCESSORY = new Accessory() {
		@Override
		public boolean canEquipFromUse(ItemStack stack) {
			return false;
		}
	};

	private void addPlayerInventoryHandlers() {
		PlayerInventoryProvider.get().addPlayerInventoryHandler(
				Compatibility.ModIds.ACCESSORIES,
				this::getAccessoriesSlotTags,
				AccessoriesCompat::getSize,
				AccessoriesCompat::getStackInSlot,
				AccessoriesCompat::setStackInSlot,
				true
		);
	}

	public AccessoriesCompat(IEventBus modBus) {
		if (FMLEnvironment.dist.isClient()) {
			AccessoriesCompatClient.registerRenderers(modBus);
		}
		modBus.addListener(this::onSetup);
		addPlayerInventoryHandlers();
	}

	private void onSetup(FMLCommonSetupEvent event) {
		AccessoriesAPI.registerAccessory(ModItems.MOB_CHARM_BELT.get(), NO_QUICK_EQUIP_ACCESSORY);
		AccessoriesAPI.registerAccessory(ModItems.FORTUNE_COIN.get(), NO_QUICK_EQUIP_ACCESSORY);
		AccessoriesAPI.registerAccessory(ModItems.HERO_MEDALLION.get(), NO_QUICK_EQUIP_ACCESSORY);
	}

	public static ItemStack getStackInSlot(LivingEntity entity, String slotName, int slot) {
		return AccessoriesCapability.getOptionally(entity).map(cap -> {
			AccessoriesContainer container = cap.getContainers().get(slotName);
			if (container == null) {
				return ItemStack.EMPTY;
			}
			return container.getAccessories().getItem(slot);
		}).orElse(ItemStack.EMPTY);
	}

	public static void setStackInSlot(LivingEntity entity, String slotName, int slot, ItemStack stack) {
		AccessoriesCapability.getOptionally(entity).ifPresent(cap -> SlotReference.of(entity, slotName, slot).setStack(stack));
	}

	private static int getSize(LivingEntity entity, String slotName) {
		return AccessoriesCapability.getOptionally(entity)
				.map(cap -> {
					AccessoriesContainer container = cap.getContainers().get(slotName);
					if (container == null) {
						return 0;
					}
					return container.getSize();
				}).orElse(0);
	}

	private Set<String> getAccessoriesSlotTags(Player player) {
		long gameTime = player.level().getGameTime();
		if (lastTagsRefresh + TAGS_REFRESH_COOLDOWN < gameTime) {
			lastTagsRefresh = gameTime;
			containerNames.clear();

			containerNames.addAll(AccessoriesCapability.getOptionally(player)
					.map(capability -> capability.getContainers().keySet()
					).orElse(Collections.emptySet()));
		}
		return containerNames;
	}
}
