package xreliquary.entities.shot;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class StormShotEntity extends ShotEntityBase {
	public StormShotEntity(EntityType<StormShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public StormShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.STORM_SHOT, world, player, hand);
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doFlightEffects() {
		// does nothing
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
			BlockPos pos = blockResult.getPos().offset(blockResult.getFace());
			if (world instanceof ServerWorld && world.isRainingAt(pos) && world.getWorldInfo().isRaining() && world.getWorldInfo().isThundering()) {
				LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
				if (bolt != null) {
					bolt.moveForced(pos.getX(), pos.getY(), pos.getZ());
					world.addEntity(bolt);
				}
			}
		}
		super.onImpact(result);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// does nothing
	}

	@Override
	void spawnHitParticles(int i) {
		Vector3d motion = getMotion();
		for (int particles = 0; particles < i; particles++) {
			world.addParticle(ParticleTypes.BUBBLE, getPosX(), getPosY(), getPosZ(), gaussian(motion.getX()), rand.nextFloat() + motion.getY(), gaussian(motion.getZ()));
		}
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	void doDamage(LivingEntity entity) {
		if (world instanceof ServerWorld && world.isRainingAt(entity.getPosition()) && world.getWorldInfo().isRaining() && world.getWorldInfo().isThundering()) {
			LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
			if (bolt != null) {
				bolt.moveForced(entity.getPosX(), entity.getPosY(), entity.getPosZ());
				world.addEntity(bolt);
				if (entity instanceof CreeperEntity) {
					entity.func_241841_a(((ServerWorld) world), bolt);
				}
			}
		}
		super.doDamage(entity);
	}

	@Override
	int getDamageOfShot(LivingEntity entity) {
		float f = 1F + (world.isRaining() ? 0.5F : 0F) + (world.isThundering() ? 0.5F : 0F);
		return Math.round(9F * f) + d6();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.STORM;
	}
}
