package xreliquary.handler;

import com.google.common.collect.Sets;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import xreliquary.blocks.PassivePedestalBlock;
import xreliquary.init.ModItems;
import xreliquary.items.RendingGaleItem;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.util.XRFakePlayerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonEventHandler {
	private CommonEventHandler() {}

	private static final Set<IPlayerHurtHandler> playerHurtHandlers = Sets.newTreeSet(new HandlerPriorityComparator());
	private static final Set<IPlayerDeathHandler> playerDeathHandlers = Sets.newTreeSet(new HandlerPriorityComparator());

	private static final Map<UUID, Boolean> playersFlightStatus = new HashMap<>();

	public static void registerPlayerHurtHandler(IPlayerHurtHandler handler) {
		playerHurtHandlers.add(handler);
	}

	public static void registerPlayerDeathHandler(IPlayerDeathHandler handler) {
		playerDeathHandlers.add(handler);
	}

	public static void registerEventBusListeners(IEventBus eventBus) {
		eventBus.addListener(PassivePedestalBlock::onRightClicked);
		eventBus.addListener(CommonEventHandler::preventMendingAndUnbreaking);
		eventBus.addListener(CommonEventHandler::blameDrullkus);
		eventBus.addListener(CommonEventHandler::beforePlayerHurt);
		eventBus.addListener(CommonEventHandler::beforePlayerDeath);
		eventBus.addListener(CommonEventHandler::onDimensionUnload);
		eventBus.addListener(CommonEventHandler::onPlayerTick);
		eventBus.addListener(PedestalRegistry::serverStopping);
	}

	public static void preventMendingAndUnbreaking(AnvilUpdateEvent event) {
		if (event.getLeft().isEmpty() || event.getRight().isEmpty()) {
			return;
		}

		if (event.getLeft().getItem() != ModItems.MOB_CHARM.get() && event.getLeft().getItem() != ModItems.ALKAHESTRY_TOME.get()) {
			return;
		}

		if (EnchantmentHelper.getEnchantments(event.getRight()).keySet().stream().anyMatch(e -> e == Enchantments.UNBREAKING)) {
			event.setCanceled(true);
		}
	}

	public static void blameDrullkus(PlayerEvent.PlayerLoggedInEvent event) {
		// Thanks for the Witch's Hat texture! Also, blame Drullkus for making me add this. :P
		if (event.getPlayer().getGameProfile().getName().equals("Drullkus")
				&& !event.getPlayer().getPersistentData().contains("gift")
				&& event.getPlayer().getInventory().add(new ItemStack(ModItems.WITCH_HAT.get()))) {
			event.getPlayer().getPersistentData().putBoolean("gift", true);
		}
	}

	public static void beforePlayerHurt(LivingAttackEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player player)) {
			return;
		}

		boolean cancel = false;
		for (IPlayerHurtHandler handler : playerHurtHandlers) {
			if (handler.canApply(player, event) && handler.apply(player, event)) {
				cancel = true;
				break;
			}
		}

		if (cancel) {
			event.setCanceled(true);
			event.setResult(null);
		}
	}

	public static void beforePlayerDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player player)) {
			return;
		}

		boolean cancel = false;
		for (IPlayerDeathHandler handler : playerDeathHandlers) {
			if (handler.canApply(player, event) && handler.apply(player, event)) {
				cancel = true;
				break;
			}
		}

		if (cancel) {
			event.setCanceled(true);
			event.setResult(null);
		}
	}

	public static void onDimensionUnload(WorldEvent.Unload event) {
		if (event.getWorld() instanceof ServerLevel serverLevel) {
			XRFakePlayerFactory.unloadWorld(serverLevel);
		}
	}

	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.side == LogicalSide.CLIENT) {
			return;
		}

		Player player = event.player;

		if (player.isUsingItem() && player.getUseItem().getItem() == ModItems.RENDING_GALE.get() && ModItems.RENDING_GALE.get().getMode(player.getUseItem()) == RendingGaleItem.Mode.FLIGHT && ModItems.RENDING_GALE.get().hasFlightCharge(player.getUseItem())) {
			playersFlightStatus.put(player.getGameProfile().getId(), true);
			player.getAbilities().mayfly = true;
			((ServerPlayer) player).connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
		} else {
			if (!playersFlightStatus.containsKey(player.getGameProfile().getId())) {
				playersFlightStatus.put(player.getGameProfile().getId(), false);
				return;
			}
			boolean isFlying = playersFlightStatus.get(player.getGameProfile().getId());
			if (isFlying) {
				playersFlightStatus.put(player.getGameProfile().getId(), false);
				if (!player.isCreative()) {
					player.getAbilities().mayfly = false;
					player.getAbilities().flying = false;
					((ServerPlayer) player).connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
				}
			}
		}
	}
}
