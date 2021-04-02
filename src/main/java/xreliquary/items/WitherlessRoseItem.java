package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.util.InventoryHelper;

import java.util.Random;

public class WitherlessRoseItem extends ItemBase {
	public WitherlessRoseItem() {
		super(new Properties().maxStackSize(1));

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.WITHER
						&& InventoryHelper.playerHasItem(player, ModItems.WITHERLESS_ROSE.get());
			}

			@Override
			public boolean apply(PlayerEntity player, LivingAttackEvent event) {
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGHEST;
			}
		});
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

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if (player.isPotionActive(Effects.WITHER)) {
			player.removePotionEffect(Effects.WITHER);
			for (int particles = 0; particles < 10; particles++) {
				double gauss1 = gaussian(world.rand);
				double gauss2 = gaussian(world.rand);
				world.addParticle(ParticleTypes.ENTITY_EFFECT, player.getPosX() + gauss1, player.getPosY() + player.getHeight() / 2, player.getPosZ() + gauss2, 0.0, 0.0, 1.0);
			}
		}
	}

	private double gaussian(Random rand) {
		return rand.nextGaussian() / 6;
	}
}
