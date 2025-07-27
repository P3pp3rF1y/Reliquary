package reliquary.entity;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.network.SpawnConcussiveExplosionParticlesPayload;
import reliquary.util.RandHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ConcussiveExplosion extends Explosion {
	private final Level level;
	private final Vec3 pos;
	protected final Entity exploder;
	private float explosionSize;
	private final Map<Player, Vec3> playerKnockbackMap;
	private final Player shootingEntity;

	public ConcussiveExplosion(Level level, @Nullable Entity entity, @Nullable Player player, Vec3 pos, float size, boolean isFlaming) {
		super(level, entity, null, null, pos.x(), pos.y(), pos.z(), size, isFlaming,
				BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
		this.level = level;
		exploder = entity;
		shootingEntity = player;
		this.pos = pos;
		explosionSize = size;
		playerKnockbackMap = Maps.newHashMap();
	}

	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	@Override
	public void explode() {
		float var1 = explosionSize;

		explosionSize *= 2.0F;
		List<Entity> var9 = level.getEntities(exploder,
				new AABB(pos.add(-explosionSize - 1.0D, -explosionSize - 1.0D, -explosionSize - 1.0D),
						pos.add(explosionSize + 1.0D, explosionSize + 1.0D, explosionSize + 1.0D)));

		for (Entity entity : var9) {
			if (affectEntity(entity)) {
				attackEntityWithExplosion(pos, entity);
			}
		}

		explosionSize = var1;
	}

	private void attackEntityWithExplosion(Vec3 var30, Entity entity) {
		double d5;
		double d7;
		double d9;
		double var13 = Math.sqrt(entity.distanceToSqr(pos)) / explosionSize;
		if (var13 <= 1.0D) {
			d5 = entity.getX() - pos.x();
			d7 = entity.getY() + entity.getEyeHeight() - pos.y();
			d9 = entity.getZ() - pos.z();
			double var33 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

			if (var33 != 0.0D) {
				d5 /= var33;
				d7 /= var33;
				d9 /= var33;
				double var32 = getSeenPercent(var30, entity);
				double d10 = (1.0D - var13) * var32;
				entity.hurt(entity.damageSources().thrown(exploder, shootingEntity), (int) ((d10 * d10 + d10) * 6.0D * (explosionSize * 2) + 3.0D));
				entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d10, d7 * d10, d9 * d10));
			}
		}
	}

	protected boolean affectEntity(Entity entity) {
		return entity instanceof Mob;
	}

	/**
	 * Does the second part of the explosion (sounds, particles, drop spawn)
	 */
	@Override
	public void finalizeExplosion(boolean spawnParticles) {
		level.playSound(null, BlockPos.containing(pos.x(), pos.y(), pos.z()), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + RandHelper.getRandomMinusOneToOne(level.random) * 0.2F) * 0.7F);

		if (explosionSize >= 2.0F) {
			level.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.x(), pos.y(), pos.z(), 1.0D, 0.0D, 0.0D);
		} else {
			level.addParticle(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 1.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public Map<Player, Vec3> getHitPlayers() {
		return playerKnockbackMap;
	}

	public static class GrenadeConcussiveExplosion extends ConcussiveExplosion {

		GrenadeConcussiveExplosion(Level level, Entity entity, Player par3Entity, Vec3 pos) {
			super(level, entity, par3Entity, pos, (float) 4.0, false);
		}

		@Override
		protected boolean affectEntity(Entity entity) {
			return (super.affectEntity(entity) && !(entity instanceof Player))
					|| (entity instanceof Player player && exploder != null && exploder.getCustomName() != null && exploder.getCustomName().getString().contains((player).getGameProfile().getName()));
		}
	}

	public static void customBusterExplosion(Entity par1Entity, double x, double y, double z, float par8) {
		if (par1Entity.level().isClientSide) {
			return;
		}
		par1Entity.level().explode(par1Entity, x, y, z, par8, false, Level.ExplosionInteraction.BLOCK);
	}

	public static void customConcussiveExplosion(Entity entity, Player player, Vec3 pos, float size, boolean isFlaming) {
		ConcussiveExplosion var11 = new ConcussiveExplosion(entity.level(), entity, player, pos, size, isFlaming);
		var11.explode();
		var11.finalizeExplosion(false);

		PacketDistributor.sendToPlayersTrackingEntity(entity, new SpawnConcussiveExplosionParticlesPayload(size, pos));
	}

	static void grenadeConcussiveExplosion(Entity entity, Player player, Vec3 pos) {
		GrenadeConcussiveExplosion var11 = new GrenadeConcussiveExplosion(entity.level(), entity, player, pos);
		var11.explode();
		var11.finalizeExplosion(false);

		PacketDistributor.sendToPlayersTrackingEntity(entity, new SpawnConcussiveExplosionParticlesPayload((float) 4.0, pos));
	}
}
