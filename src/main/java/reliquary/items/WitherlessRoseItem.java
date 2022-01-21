package reliquary.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import reliquary.util.InventoryHelper;

import java.util.Random;

public class WitherlessRoseItem extends ItemBase {
	public WitherlessRoseItem() {
		super(new Properties().stacksTo(1));
		MinecraftForge.EVENT_BUS.addListener(this::preventWither);
		MinecraftForge.EVENT_BUS.addListener(this::preventWitherAttack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	private void preventWither(PotionEvent.PotionApplicableEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();
		if (entityLiving instanceof Player player && event.getPotionEffect().getEffect() == MobEffects.WITHER && InventoryHelper.playerHasItem(player, this)) {
			event.setResult(Event.Result.DENY);
			addPreventParticles((Player) entityLiving);
		}
	}

	private void preventWitherAttack(LivingAttackEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();
		if (entityLiving instanceof Player player && event.getSource() == DamageSource.WITHER && InventoryHelper.playerHasItem(player, this)) {
			entityLiving.removeEffect(MobEffects.WITHER);
			event.setCanceled(true);
			addPreventParticles((Player) entityLiving);
		}
	}

	private void addPreventParticles(Player entityLiving) {
		Level world = entityLiving.level;
		for (int particles = 0; particles < 10; particles++) {
			double gauss1 = gaussian(world.random);
			double gauss2 = gaussian(world.random);
			world.addParticle(ParticleTypes.ENTITY_EFFECT, entityLiving.getX() + gauss1, entityLiving.getY() + entityLiving.getBbHeight() / 2, entityLiving.getZ() + gauss2, 0.0, 0.0, 1.0);
		}
	}

	private double gaussian(Random rand) {
		return rand.nextGaussian() / 6;
	}
}
