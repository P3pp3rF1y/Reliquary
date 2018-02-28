package xreliquary.handler;

import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.XRFakePlayerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommonEventHandler {

	private static final Set<IPlayerHurtHandler> playerHurtHandlers = Sets.newTreeSet((o1, o2) -> {
		int ret = 10 * (o1.getPriority().ordinal() - o2.getPriority().ordinal());
		return ret == 0 ? 1 : ret; //just make every value unique, same priority sorted on the same level
	});
	private static Map<UUID, Boolean> playersFlightStatus = new HashMap<>();

	public static void registerPlayerHurtHandler(IPlayerHurtHandler handler) {
		playerHurtHandlers.add(handler);
	}

	@SubscribeEvent
	public static void handleMercyCrossDamage(AttackEntityEvent event) {
		if(event.getEntityPlayer().world.isRemote || !(event.getTarget() instanceof EntityLivingBase))
			return;

		if(event.getEntityPlayer().getHeldItemMainhand().getItem() != ModItems.mercyCross)
			return;

		EntityLivingBase target = (EntityLivingBase) event.getTarget();

		ModItems.mercyCross.updateAttackDamageModifier(target, event.getEntityPlayer());
	}

	@SubscribeEvent
	public static void preventMendingAndUnbreaking(AnvilUpdateEvent event) {
		if(event.getLeft().isEmpty() || event.getRight().isEmpty())
			return;

		if (event.getLeft().getItem() != ModItems.mobCharm && event.getLeft().getItem() != ModItems.alkahestryTome)
			return;

		if (EnchantmentHelper.getEnchantments(event.getRight()).keySet().stream().anyMatch(e -> e == Enchantments.UNBREAKING)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void blameDrullkus(PlayerEvent.PlayerLoggedInEvent event) {
		// Thanks for the Witch's Hat texture! Also, blame Drullkus for making me add this. :P
		if(event.player.getGameProfile().getName().equals("Drullkus")) {
			if(!event.player.getEntityData().hasKey("gift")) {
				if(event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.witchHat))) {
					event.player.getEntityData().setBoolean("gift", true);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void beforePlayerHurt(LivingAttackEvent event) {
		Entity entity = event.getEntity();
		if(entity == null || !(entity instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) entity;

		boolean cancel = false;
		for (IPlayerHurtHandler handler : playerHurtHandlers) {
			if (handler.canApply(player, event)) {
				if (handler.apply(player, event)) {
					cancel = true;
					break;
				}
			}
		}

		if(cancel) {
			event.setCanceled(true);
			event.setResult(null);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onDimensionUnload(WorldEvent.Unload event) {
		if(event.getWorld() instanceof WorldServer)
			XRFakePlayerFactory.unloadWorld((WorldServer) event.getWorld());
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.side == Side.CLIENT)
			return;

		EntityPlayer player = event.player;

		if(player.isHandActive() && player.getActiveItemStack().getItem() == ModItems.rendingGale && ModItems.rendingGale.isFlightMode(player.getActiveItemStack()) && ModItems.rendingGale.hasFlightCharge(player, player.getActiveItemStack())) {
			playersFlightStatus.put(player.getGameProfile().getId(), true);
			player.capabilities.allowFlying = true;
			((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		} else {
			if(!playersFlightStatus.containsKey(player.getGameProfile().getId())) {
				playersFlightStatus.put(player.getGameProfile().getId(), false);
			}

			if(playersFlightStatus.get(player.getGameProfile().getId())) {

				playersFlightStatus.put(player.getGameProfile().getId(), false);

				if(!player.capabilities.isCreativeMode) {
					player.capabilities.allowFlying = false;
					player.capabilities.isFlying = false;
					((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
				}
			}
		}
	}
}
