package xreliquary.entities;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EntityExplosionContext;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import xreliquary.network.PacketFXConcussiveExplosion;
import xreliquary.network.PacketHandler;
import xreliquary.util.RandHelper;

import java.util.List;
import java.util.Map;

public class ConcussiveExplosion extends Explosion {
	private final World world;
	private final Vector3d pos;
	protected final Entity exploder;
	private float explosionSize;
	private final Map<PlayerEntity, Vector3d> playerKnockbackMap;
	private final PlayerEntity shootingEntity;

	public ConcussiveExplosion(World world, Entity entity, PlayerEntity par3Entity, Vector3d pos, float size, boolean isFlaming) {
		super(world, entity, null, null, pos.getX(), pos.getY(), pos.getZ(), size, isFlaming, Mode.BREAK);
		this.world = world;
		exploder = entity;
		shootingEntity = par3Entity;
		this.pos = pos;
		explosionSize = size;
		playerKnockbackMap = Maps.newHashMap();
	}

	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	@Override
	public void doExplosionA() {
		float var1 = explosionSize;

		explosionSize *= 2.0F;
		List<Entity> var9 = world.getEntitiesWithinAABBExcludingEntity(exploder,
				new AxisAlignedBB(pos.add(-explosionSize - 1.0D, -explosionSize - 1.0D, -explosionSize - 1.0D),
						pos.add(explosionSize + 1.0D, explosionSize + 1.0D, explosionSize + 1.0D)));

		for (Entity entity : var9) {
			if (affectEntity(entity)) {
				attackEntityWithExplosion(pos, entity);
			}
		}

		explosionSize = var1;
	}

	private void attackEntityWithExplosion(Vector3d var30, Entity entity) {
		double d5;
		double d7;
		double d9;
		double var13 = Math.sqrt(entity.getDistanceSq(pos)) / explosionSize;
		if (var13 <= 1.0D) {
			d5 = entity.getPosX() - pos.getX();
			d7 = entity.getPosY() + entity.getEyeHeight() - pos.getY();
			d9 = entity.getPosZ() - pos.getZ();
			double var33 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

			if (var33 != 0.0D) {
				d5 /= var33;
				d7 /= var33;
				d9 /= var33;
				double var32 = getBlockDensity(var30, entity);
				double d10 = (1.0D - var13) * var32;
				entity.attackEntityFrom(DamageSource.causeThrownDamage(exploder, shootingEntity), (int) ((d10 * d10 + d10) * 6.0D * (explosionSize * 2) + 3.0D));
				entity.setMotion(entity.getMotion().add(d5 * d10, d7 * d10, d9 * d10));
			}
		}
	}

	protected boolean affectEntity(Entity entity) {
		return entity instanceof MobEntity;
	}

	/**
	 * Does the second part of the explosion (sounds, particles, drop spawn)
	 */
	@Override
	public void doExplosionB(boolean spawnParticles) {
		world.playSound(null, new BlockPos(pos), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + RandHelper.getRandomMinusOneToOne(world.rand) * 0.2F) * 0.7F);

		if (explosionSize >= 2.0F) {
			world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY(), pos.getZ(), 1.0D, 0.0D, 0.0D);
		} else {
			world.addParticle(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 1.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public Map<PlayerEntity, Vector3d> getPlayerKnockbackMap() {
		return playerKnockbackMap;
	}

	public static class GrenadeConcussiveExplosion extends ConcussiveExplosion {

		GrenadeConcussiveExplosion(World world, Entity entity, PlayerEntity par3Entity, Vector3d pos) {
			super(world, entity, par3Entity, pos, (float) 4.0, false);
		}

		@Override
		protected boolean affectEntity(Entity entity) {
			return (super.affectEntity(entity) && !(entity instanceof PlayerEntity))
					|| (entity instanceof PlayerEntity && exploder.getCustomName() != null && exploder.getCustomName().getString().contains(((PlayerEntity) entity).getGameProfile().getName()));
		}
	}

	public static void customBusterExplosion(Entity par1Entity, double x, double y, double z, float par8) {
		if (par1Entity.world.isRemote) {
			return;
		}
		par1Entity.world.createExplosion(par1Entity, x, y, z, par8, false, Mode.BREAK);
	}

	public static void customConcussiveExplosion(Entity entity, PlayerEntity player, Vector3d pos, float size, boolean isFlaming) {
		ConcussiveExplosion var11 = new ConcussiveExplosion(entity.world, entity, player, pos, size, isFlaming);
		var11.doExplosionA();
		var11.doExplosionB(false);

		PacketHandler.sendToAllAround(new PacketFXConcussiveExplosion(size, pos), new PacketDistributor.TargetPoint(entity.getPosX(), entity.getPosY(), entity.getPosZ(), 96.0D, entity.getEntityWorld().getDimensionKey()));

	}

	static void grenadeConcussiveExplosion(Entity entity, PlayerEntity player, Vector3d pos) {
		GrenadeConcussiveExplosion var11 = new GrenadeConcussiveExplosion(entity.world, entity, player, pos);
		var11.doExplosionA();
		var11.doExplosionB(false);

		PacketHandler.sendToAllAround(new PacketFXConcussiveExplosion((float) 4.0, pos), new PacketDistributor.TargetPoint(entity.getPosX(), entity.getPosY(), entity.getPosZ(), 96.0D, entity.getEntityWorld().getDimensionKey()));

	}

}
