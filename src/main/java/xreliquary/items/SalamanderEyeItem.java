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
		super("salamander_eye", new Properties().maxStackSize(1));
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
		if(world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;

		if(player.getHeldItem(Hand.MAIN_HAND).getItem() == this || player.getHeldItem(Hand.OFF_HAND).getItem() == this) {
			doFireballEffect(player);
			doExtinguishEffect(player);
		}
	}

	private void doExtinguishEffect(PlayerEntity player) {
		if(player.isBurning()) {
			player.extinguish();
		}
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		for(int xOff = -3; xOff <= 3; xOff++) {
			for(int yOff = -3; yOff <= 3; yOff++) {
				for(int zOff = -3; zOff <= 3; zOff++) {
					if(player.world.getBlockState(new BlockPos(x + xOff, y + yOff, z + zOff)).getBlock() == Blocks.FIRE) {
						player.world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), Blocks.AIR.getDefaultState());
						player.world.playSound(x + xOff + 0.5D, y + yOff + 0.5D, z + zOff + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
					}
				}
			}
		}
	}

	private void doFireballEffect(PlayerEntity player) {
		List<FireballEntity> ghastFireballs = player.world.getEntitiesWithinAABB(FireballEntity.class, new AxisAlignedBB(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5));
		for(FireballEntity fireball : ghastFireballs) {
			if(player.getDistance(fireball) < 4) {
				fireball.remove();
			}
			fireball.attackEntityFrom(DamageSource.causePlayerDamage(player), 1);
			player.world.playSound(fireball.posX, fireball.posY, fireball.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
		}
		List<SmallFireballEntity> blazeFireballs = player.world.getEntitiesWithinAABB(SmallFireballEntity.class, new AxisAlignedBB(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));
		for(SmallFireballEntity fireball : blazeFireballs) {
			for(int particles = 0; particles < 4; particles++) {
				player.world.addParticle(RedstoneParticleData.REDSTONE_DUST, fireball.posX, fireball.posY, fireball.posZ, 0.0D, 1.0D, 1.0D);
			}
			player.world.playSound(fireball.posX, fireball.posY, fireball.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
			fireball.remove();
		}
	}
}
