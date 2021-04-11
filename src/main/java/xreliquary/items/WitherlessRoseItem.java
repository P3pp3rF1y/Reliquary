package xreliquary.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import xreliquary.util.InventoryHelper;

import java.util.Random;

public class WitherlessRoseItem extends ItemBase {
	public WitherlessRoseItem() {
		super(new Properties().maxStackSize(1));
		MinecraftForge.EVENT_BUS.addListener(this::preventWither);
		MinecraftForge.EVENT_BUS.addListener(this::preventWitherAttack);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	private void preventWither(PotionEvent.PotionApplicableEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();
		if (entityLiving instanceof PlayerEntity && event.getPotionEffect().getPotion() == Effects.WITHER && InventoryHelper.playerHasItem((PlayerEntity) entityLiving, this)) {
			event.setResult(Event.Result.DENY);
			addPreventParticles((PlayerEntity) entityLiving);
		}
	}

	private void preventWitherAttack(LivingAttackEvent event) {
		LivingEntity entityLiving = event.getEntityLiving();
		if (entityLiving instanceof PlayerEntity && event.getSource() == DamageSource.WITHER && InventoryHelper.playerHasItem((PlayerEntity) entityLiving, this)) {
			entityLiving.removePotionEffect(Effects.WITHER);
			event.setCanceled(true);
			addPreventParticles((PlayerEntity) entityLiving);
		}
	}

	private void addPreventParticles(PlayerEntity entityLiving) {
		World world = entityLiving.world;
		for (int particles = 0; particles < 10; particles++) {
			double gauss1 = gaussian(world.rand);
			double gauss2 = gaussian(world.rand);
			world.addParticle(ParticleTypes.ENTITY_EFFECT, entityLiving.getPosX() + gauss1, entityLiving.getPosY() + entityLiving.getHeight() / 2, entityLiving.getPosZ() + gauss2, 0.0, 0.0, 1.0);
		}
	}

	private double gaussian(Random rand) {
		return rand.nextGaussian() / 6;
	}
}
