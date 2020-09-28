package xreliquary.potions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import xreliquary.init.ModPotions;
import xreliquary.reference.Reference;

public class PotionFlight extends Effect {

	public PotionFlight() {
		super(EffectType.BENEFICIAL, 0);
		setRegistryName(Reference.MOD_ID, "flight");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(LivingEntity entityLivingBase, int amplifier) {
		if (entityLivingBase.world.isRemote || !(entityLivingBase instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entityLivingBase;

		if (!player.abilities.allowFlying) {
			player.abilities.allowFlying = true;
			((ServerPlayerEntity) player).connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
		}
		player.fallDistance = 0;
	}

	@Override
	public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBase, AttributeModifierManager attributeModifierManager, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBase, attributeModifierManager, amplifier);

		if (!(entityLivingBase instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entityLivingBase;

		if (player.getActivePotionEffect(ModPotions.potionFlight) != null) {
			return;
		}

		if (!player.isCreative()) {
			player.abilities.allowFlying = false;
			player.abilities.isFlying = false;
			((ServerPlayerEntity) player).connection.sendPacket(new SPlayerAbilitiesPacket(player.abilities));
		}
	}
}
