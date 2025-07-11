package reliquary.potions;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import reliquary.init.ModEffects;

import javax.annotation.Nullable;

public class FlightEffect extends MobEffect {

	public FlightEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
		NeoForge.EVENT_BUS.addListener(this::onEffectExpired);
		NeoForge.EVENT_BUS.addListener(this::onEffectRemoved);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int potency) {
		if (livingEntity.level().isClientSide) {
			return true;
		}

		if (!(livingEntity instanceof Player player)) {
			return false;
		}

		AttributeInstance creativeFlightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
		if (creativeFlightAttribute != null && creativeFlightAttribute.getValue() == 0) {
			creativeFlightAttribute.setBaseValue(1);
			((ServerPlayer) player).connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
		}
		player.fallDistance = 0;
		return true;
	}

	@Override
	public void removeAttributeModifiers(AttributeMap attributeMap) {
		super.removeAttributeModifiers(attributeMap);
	}

	private void onEffectExpired(MobEffectEvent.Expired event) {
		removeFlight(event.getEntity(), event.getEffectInstance());
	}

	private void onEffectRemoved(MobEffectEvent.Remove event) {
		removeFlight(event.getEntity(), event.getEffectInstance());
	}

	private static void removeFlight(LivingEntity entity, @Nullable MobEffectInstance effectInstance) {
		if (effectInstance == null || !effectInstance.getEffect().value().equals(ModEffects.FLIGHT.value())) {
			return;
		}

		if (!(entity instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (!serverPlayer.isCreative()) {
			AttributeInstance creativeFlightAttribute = serverPlayer.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
			if (creativeFlightAttribute != null) {
				creativeFlightAttribute.setBaseValue(0);
			}
			serverPlayer.getAbilities().flying = false;
			serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
		}
	}
}
