package xreliquary.entities.shot;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class BlazeShotEntity extends ShotEntityBase {
	public BlazeShotEntity(EntityType<BlazeShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public BlazeShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.BLAZE_SHOT, world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0 && ticksInAir < 9) {
			Vec3d motion = getMotion();
			world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), lowGauss(motion.getX()), lowGauss(motion.getY()), lowGauss(motion.getZ()));
		}
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	protected DamageSource getDamageSource() {
		return super.getDamageSource().setFireDamage();
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.getType() == RayTraceResult.Type.ENTITY) {
			Entity entityHit = ((EntityRayTraceResult) result).getEntity();
			if (entityHit == shootingEntity || !(entityHit instanceof LivingEntity)) {
				return;
			}
			onImpact((LivingEntity) entityHit);
		} else if(result.getType() == RayTraceResult.Type.BLOCK) {
			if(shootingEntity == null) {
				return;
			}

			BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
			groundImpact(blockResult.getFace());

			BlockPos fireSpawnPos = blockResult.getPos().offset(blockResult.getFace());

			if (shootingEntity.canPlayerEdit(fireSpawnPos, blockResult.getFace(), new ItemStack(Items.FLINT_AND_STEEL)) && world.isAirBlock(fireSpawnPos)) {
				world.setBlockState(fireSpawnPos, Blocks.FIRE.getDefaultState());
			}
		}
	}

	@Override
	protected void onImpact(LivingEntity entityLiving) {
		entityLiving.setFire(40);
		super.onImpact(entityLiving);
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			spawnMotionBasedParticle(ParticleTypes.FLAME);
		}
	}

	@Override
	protected void groundImpact(Direction sideHit) {
		remove();
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		// they're not COMPLETELY useless against fireImmune mobs, just mostly
		// useless.
		//this probably isn't gonna work now the bullets do purely fire damage.
		return mop.isImmuneToFire() ? 2 : (10 + d12());
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		Vec3d motion = getMotion();
		double motionX = motion.getX();
		double motionY = motion.getY();
		double motionZ = motion.getZ();
		for(int particles = 0; particles < 40; particles++) {
			switch(sideHit) {
				case DOWN:
					world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), smallGauss(0.1D) + motionX / 4, -posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
					break;
				case UP:
					world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), smallGauss(0.1D) + motionX / 4, posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
					break;
				case NORTH:
					world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, -posGauss(0.2D));
					break;
				case SOUTH:
					world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, posGauss(0.2D));
					break;
				case WEST:
					world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), -posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
					break;
				case EAST:
					world.addParticle(ParticleTypes.FLAME, getPosX(), getPosY(), getPosZ(), posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
					break;
			}
		}
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.BLAZE;
	}
}
