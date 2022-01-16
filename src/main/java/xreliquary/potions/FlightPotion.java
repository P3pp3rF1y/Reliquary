package xreliquary.potions;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import xreliquary.init.ModPotions;

public class FlightPotion extends MobEffect {

	public FlightPotion() {
		super(MobEffectCategory.BENEFICIAL, 0);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBase, int amplifier) {
		if (entityLivingBase.level.isClientSide || !(entityLivingBase instanceof Player player)) {
			return;
		}

		if (!player.getAbilities().mayfly) {
			player.getAbilities().mayfly = true;
			((ServerPlayer) player).connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
		}
		player.fallDistance = 0;
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entityLivingBase, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entityLivingBase, attributeMap, amplifier);

		if (!(entityLivingBase instanceof Player player)) {
			return;
		}

		if (player.hasEffect(ModPotions.FLIGHT_POTION.get())) {
			return;
		}

		if (!player.isCreative()) {
			player.getAbilities().mayfly = false;
			player.getAbilities().flying = false;
			((ServerPlayer) player).connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
		}
	}
}
