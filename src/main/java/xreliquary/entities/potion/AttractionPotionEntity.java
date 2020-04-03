package xreliquary.entities.potion;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;

public class AttractionPotionEntity extends ThrownPotionEntity {
	public AttractionPotionEntity(EntityType<AttractionPotionEntity> entityType, World world) {
		super(entityType, world, new ItemStack(ModItems.ATTRACTION_POTION));
	}

	public AttractionPotionEntity(World world, PlayerEntity player) {
		super(ModEntities.APHRODITE_POTION, world, player, new ItemStack(ModItems.ATTRACTION_POTION));
	}

	public AttractionPotionEntity(World world, double x, double y, double z) {
		super(ModEntities.APHRODITE_POTION, world, x, y, z, new ItemStack(ModItems.ATTRACTION_POTION));
	}

	@Override
	boolean hasLivingEntityEffect() {
		return true;
	}

	@Override
	void doGroundSplashEffect() {
		// none
	}

	@Override
	void doLivingSplashEffect(LivingEntity el) {
		if (!(el instanceof AnimalEntity)) {
			return;
		}
		AnimalEntity e = (AnimalEntity) el;
		if (e.getGrowingAge() != 0) {
			return;
		}
		if (getThrower() instanceof PlayerEntity) {
			e.setInLove((PlayerEntity) getThrower());
		}

		for (int var3 = 0; var3 < 7; ++var3) {
			double var4 = rand.nextGaussian() * 0.02D;
			double var6 = rand.nextGaussian() * 0.02D;
			double var8 = rand.nextGaussian() * 0.02D;
			world.addParticle(ParticleTypes.HEART, e.getPosX() + rand.nextFloat() * e.getWidth() * 2.0F - e.getWidth(), e.getPosY() + 0.5D + rand.nextFloat() * e.getHeight(), e.getPosZ() + rand.nextFloat() * e.getWidth() * 2.0F - e.getWidth(), var4, var6, var8);
		}
	}

	@Override
	int getColor() {
		return Colors.get(Colors.RED);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.ATTRACTION_POTION);
	}
}
