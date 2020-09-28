package xreliquary.handler;

import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.XRFakePlayerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
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

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void preventMendingAndUnbreaking(AnvilUpdateEvent event) {
		if (event.getLeft().isEmpty() || event.getRight().isEmpty()) {
			return;
		}

		if (event.getLeft().getItem() != ModItems.MOB_CHARM && event.getLeft().getItem() != ModItems.ALKAHESTRY_TOME) {
			return;
		}

		if (EnchantmentHelper.getEnchantments(event.getRight()).keySet().stream().anyMatch(e -> e == Enchantments.UNBREAKING)) {
			event.setCanceled(true);
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void blameDrullkus(PlayerEvent.PlayerLoggedInEvent event) {
		// Thanks for the Witch's Hat texture! Also, blame Drullkus for making me add this. :P
		if (event.getPlayer().getGameProfile().getName().equals("Drullkus")
				&& !event.getPlayer().getPersistentData().contains("gift")
				&& event.getPlayer().inventory.addItemStackToInventory(new ItemStack(ModItems.WITCH_HAT))) {
			event.getPlayer().getPersistentData().putBoolean("gift", true);
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void beforePlayerHurt(LivingAttackEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;

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

	@SuppressWarnings("unused")
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void beforePlayerDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;

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

	@SuppressWarnings("unused")
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onDimensionUnload(WorldEvent.Unload event) {
		if (event.getWorld() instanceof ServerWorld) {
			XRFakePlayerFactory.unloadWorld((ServerWorld) event.getWorld());
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.side == LogicalSide.CLIENT) {
			return;
		}

		PlayerEntity player = event.player;

		if (player.isHandActive() && player.getActiveItemStack().getItem() == ModItems.RENDING_GALE && ModItems.RENDING_GALE.isFlightMode(player.getActiveItemStack()) && ModItems.RENDING_GALE.hasFlightCharge(player.getActiveItemStack())) {
			playersFlightStatus.put(player.getGameProfile().getId(), true);
			player.abilities.allowFlying = true;
			((ServerPlayerEntity) player).connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
		} else {
			if (!playersFlightStatus.containsKey(player.getGameProfile().getId())) {
				playersFlightStatus.put(player.getGameProfile().getId(), false);
				return;
			}
			boolean isFlying = playersFlightStatus.get(player.getGameProfile().getId());
			if (isFlying) {
				playersFlightStatus.put(player.getGameProfile().getId(), false);
				if (!player.isCreative()) {
					player.abilities.allowFlying = false;
					player.abilities.isFlying = false;
					((ServerPlayerEntity) player).connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
				}
			}
		}
	}
}
