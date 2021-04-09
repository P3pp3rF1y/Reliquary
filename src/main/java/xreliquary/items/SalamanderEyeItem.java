package xreliquary.items;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.util.RandHelper;

import java.util.List;

public class SalamanderEyeItem extends ItemBase {
	public SalamanderEyeItem() {
		super(new Properties().maxStackSize(1));
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
		if (world.isRemote || !(entity instanceof PlayerEntity) || world.getGameTime() % 2 != 0) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;

		if (player.getHeldItem(Hand.MAIN_HAND).getItem() == this || player.getHeldItem(Hand.OFF_HAND).getItem() == this) {
			doFireballEffect(player);
			doExtinguishEffect(player);
		}
	}

	private void doExtinguishEffect(PlayerEntity player) {
		if (player.isBurning()) {
			player.extinguish();
		}
		BlockPos.getAllInBoxMutable(player.getPosition().add(-3,-3,-3), player.getPosition().add(3,3,3))
				.forEach(pos -> {
					if (player.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
						player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
						player.world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F);
					}
				});
	}

	private void doFireballEffect(PlayerEntity player) {
		List<FireballEntity> ghastFireballs = player.world.getEntitiesWithinAABB(FireballEntity.class, new AxisAlignedBB(player.getPosX() - 5, player.getPosY() - 5, player.getPosZ() - 5, player.getPosX() + 5, player.getPosY() + 5, player.getPosZ() + 5));
		for (FireballEntity fireball : ghastFireballs) {
			if (player.getDistance(fireball) < 4) {
				fireball.remove();
			}
			fireball.attackEntityFrom(DamageSource.causePlayerDamage(player), 1);
			player.world.playSound(fireball.getPosX(), fireball.getPosY(), fireball.getPosZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
		}
		List<SmallFireballEntity> blazeFireballs = player.world.getEntitiesWithinAABB(SmallFireballEntity.class, new AxisAlignedBB(player.getPosX() - 3, player.getPosY() - 3, player.getPosZ() - 3, player.getPosX() + 3, player.getPosY() + 3, player.getPosZ() + 3));
		for (SmallFireballEntity fireball : blazeFireballs) {
			for (int particles = 0; particles < 4; particles++) {
				player.world.addParticle(RedstoneParticleData.REDSTONE_DUST, fireball.getPosX(), fireball.getPosY(), fireball.getPosZ(), 0.0D, 1.0D, 1.0D);
			}
			player.world.playSound(fireball.getPosX(), fireball.getPosY(), fireball.getPosZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
			fireball.remove();
		}
	}
}
