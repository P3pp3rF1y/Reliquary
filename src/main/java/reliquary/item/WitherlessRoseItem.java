package reliquary.item;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import reliquary.util.InventoryHelper;

public class WitherlessRoseItem extends ItemBase {
	public WitherlessRoseItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
		NeoForge.EVENT_BUS.addListener(this::preventWither);
		NeoForge.EVENT_BUS.addListener(this::preventWitherAttack);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	private void preventWither(MobEffectEvent.Applicable event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity instanceof Player player && event.getEffectInstance() != null && event.getEffectInstance().getEffect() == MobEffects.WITHER && InventoryHelper.playerHasItem(player, this)) {
			event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
			addPreventParticles((Player) livingEntity);
		}
	}

	private void preventWitherAttack(LivingDamageEvent.Pre event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity instanceof Player player && event.getSource() == player.damageSources().wither() && InventoryHelper.playerHasItem(player, this)) {
			livingEntity.removeEffect(MobEffects.WITHER);
			event.setNewDamage(0);
			addPreventParticles((Player) livingEntity);
		}
	}

	private void addPreventParticles(Player entityLiving) {
		Level level = entityLiving.level();
		for (int particles = 0; particles < 10; particles++) {
			double gauss1 = gaussian(level.random);
			double gauss2 = gaussian(level.random);
			level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 0.0F, 1.0F), entityLiving.getX() + gauss1, entityLiving.getY() + entityLiving.getBbHeight() / 2, entityLiving.getZ() + gauss2, 0.0, 0.0, 0.0);
		}
	}

	private double gaussian(RandomSource rand) {
		return rand.nextGaussian() / 6;
	}
}
