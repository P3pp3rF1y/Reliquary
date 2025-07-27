package reliquary.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import reliquary.util.RandHelper;

import java.util.List;

public class SalamanderEyeItem extends ItemBase {
	public SalamanderEyeItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 2 != 0) {
			return;
		}

		if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == this || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == this) {
			doFireballEffect(player);
			doExtinguishEffect(player);
		}
	}

	private void doExtinguishEffect(Player player) {
		if (player.isOnFire()) {
			player.clearFire();
		}
		BlockPos.betweenClosed(player.blockPosition().offset(-3, -3, -3), player.blockPosition().offset(3, 3, 3))
				.forEach(pos -> {
					if (player.level().getBlockState(pos).getBlock() instanceof BaseFireBlock) {
						player.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						player.level().playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level().random) * 0.8F);
					}
				});
	}

	private void doFireballEffect(Player player) {
		List<Projectile> projectiles = player.level().getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(5), projectile -> projectile.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE));
		for (Projectile projectile : projectiles) {
			if (player.distanceTo(projectile) < 4) {
				projectile.discard();
			}
			projectile.deflect(ProjectileDeflection.AIM_DEFLECT, player, player, true);
			player.level().playLocalSound(projectile.getX(), projectile.getY(), projectile.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level().random) * 0.8F, false);
		}
		List<SmallFireball> blazeFireballs = player.level().getEntitiesOfClass(SmallFireball.class, player.getBoundingBox().inflate(3));
		for (SmallFireball fireball : blazeFireballs) {
			for (int particles = 0; particles < 4; particles++) {
				player.level().addParticle(DustParticleOptions.REDSTONE, fireball.getX(), fireball.getY(), fireball.getZ(), 0.0D, 1.0D, 1.0D);
			}
			player.level().playLocalSound(fireball.getX(), fireball.getY(), fireball.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level().random) * 0.8F, false);
			fireball.discard();
		}
	}
}
