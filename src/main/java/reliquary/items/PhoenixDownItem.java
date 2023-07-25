package reliquary.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerDeathHandler;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModItems;
import reliquary.network.PacketHandler;
import reliquary.network.SpawnPhoenixDownParticlesPacket;
import reliquary.reference.Settings;
import reliquary.util.EntityHelper;
import reliquary.util.InventoryHelper;

public class PhoenixDownItem extends AngelicFeatherItem {

	public PhoenixDownItem() {
		super();

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingAttackEvent event) {
				return event.getSource() == player.damageSources().fall()
						&& player.getHealth() > Math.round(event.getAmount())
						&& player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.PHOENIX_DOWN.get());
			}

			@Override
			public boolean apply(Player player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.COMMON.items.phoenixDown.hungerCostPercent.get() / 100F);
				player.causeFoodExhaustion(hungerDamage);
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});

		CommonEventHandler.registerPlayerDeathHandler(new IPlayerDeathHandler() {
			@Override
			public boolean canApply(Player player, LivingDeathEvent event) {
				return InventoryHelper.playerHasItem(player, ModItems.PHOENIX_DOWN.get());
			}

			@Override
			public boolean apply(Player player, LivingDeathEvent event) {
				// item reverts to a normal feather.
				revertPhoenixDownToAngelicFeather(player);

				// gives the player a few hearts, sparing them from death.
				float amountHealed = player.getMaxHealth() * (float) Settings.COMMON.items.phoenixDown.healPercentageOfMaxLife.get() / 100F;
				player.setHealth(amountHealed);

				// if the player had any negative status effects [vanilla only for now], remove them:
				if (Boolean.TRUE.equals(Settings.COMMON.items.phoenixDown.removeNegativeStatus.get())) {
					EntityHelper.removeNegativeStatusEffects(player);
				}

				// added bonus, has some extra effects when drowning or dying to lava
				if (event.getSource() == player.damageSources().lava() && Boolean.TRUE.equals(Settings.COMMON.items.phoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou.get())) {
					player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
				}
				if (event.getSource() == player.damageSources().drown() && Boolean.TRUE.equals(Settings.COMMON.items.phoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou.get())) {
					player.setAirSupply(10);
					player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0));
				}

				// give the player temporary resistance to other damages.
				if (Boolean.TRUE.equals(Settings.COMMON.items.phoenixDown.giveTemporaryDamageResistance.get())) {
					player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
				}

				// give the player temporary regeneration.
				if (Boolean.TRUE.equals(Settings.COMMON.items.phoenixDown.giveTemporaryRegeneration.get())) {
					player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
				}

				// particles, lots of them
				spawnPhoenixResurrectionParticles(player);

				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.LOW;
			}
		});
	}

	private static void revertPhoenixDownToAngelicFeather(Player player) {
		for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
			if (player.getInventory().items.get(slot).isEmpty()) {
				continue;
			}
			if (player.getInventory().items.get(slot).getItem() == ModItems.PHOENIX_DOWN.get()) {
				player.getInventory().items.set(slot, new ItemStack(ModItems.ANGELIC_FEATHER.get()));
				return;
			}
		}
	}

	private static void spawnPhoenixResurrectionParticles(Player player) {
		PacketHandler.sendToClient((ServerPlayer) player, SpawnPhoenixDownParticlesPacket.INSTANCE);
	}
}
