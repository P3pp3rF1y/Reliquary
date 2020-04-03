package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerDeathHandler;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.network.PacketHandler;
import xreliquary.network.SpawnPhoenixDownParticlesPacket;
import xreliquary.reference.Settings;
import xreliquary.util.EntityHelper;
import xreliquary.util.InventoryHelper;

public class PhoenixDownItem extends AngelicFeatherItem {

	public PhoenixDownItem() {
		super("phoenix_down");

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.FALL
						&& player.getHealth() > Math.round(event.getAmount())
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.PHOENIX_DOWN);
			}

			@Override
			public boolean apply(PlayerEntity player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.COMMON.items.phoenixDown.hungerCostPercent.get() / 100F);
				player.addExhaustion(hungerDamage);
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});

		CommonEventHandler.registerPlayerDeathHandler(new IPlayerDeathHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingDeathEvent event) {
				return InventoryHelper.playerHasItem(player, ModItems.PHOENIX_DOWN);
			}

			@Override
			public boolean apply(PlayerEntity player, LivingDeathEvent event) {
				// item reverts to a normal feather.
				revertPhoenixDownToAngelicFeather(player);

				// gives the player a few hearts, sparing them from death.
				float amountHealed = player.getMaxHealth() * (float) Settings.COMMON.items.phoenixDown.healPercentageOfMaxLife.get() / 100F;
				player.setHealth(amountHealed);

				// if the player had any negative status effects [vanilla only for now], remove them:
				if (Settings.COMMON.items.phoenixDown.removeNegativeStatus.get()) {
					EntityHelper.removeNegativeStatusEffects(player);
				}

				// added bonus, has some extra effects when drowning or dying to lava
				if (event.getSource() == DamageSource.LAVA && Settings.COMMON.items.phoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou.get()) {
					player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 200, 0));
				}
				if (event.getSource() == DamageSource.DROWN && Settings.COMMON.items.phoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou.get()) {
					player.setAir(10);
					player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 200, 0));
				}

				// give the player temporary resistance to other damages.
				if (Settings.COMMON.items.phoenixDown.giveTemporaryDamageResistance.get()) {
					player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 1));
				}

				// give the player temporary regeneration.
				if (Settings.COMMON.items.phoenixDown.giveTemporaryRegeneration.get()) {
					player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, 1));
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

	private static void revertPhoenixDownToAngelicFeather(PlayerEntity player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}
			if (player.inventory.mainInventory.get(slot).getItem() == ModItems.PHOENIX_DOWN) {
				player.inventory.mainInventory.set(slot, new ItemStack(ModItems.ANGELIC_FEATHER));
				return;
			}
		}
	}

	private static void spawnPhoenixResurrectionParticles(PlayerEntity player) {
		PacketHandler.sendToClient((ServerPlayerEntity) player, new SpawnPhoenixDownParticlesPacket());
	}
}
