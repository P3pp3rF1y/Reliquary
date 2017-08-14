package xreliquary.entities;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import xreliquary.network.PacketFXConcussiveExplosion;
import xreliquary.network.PacketHandler;

import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
public class ConcussiveExplosion extends Explosion {

	private World world;
	private double explosionX;
	private double explosionY;
	private double explosionZ;
	Entity exploder;
	private float explosionSize;
	private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
	private EntityPlayer shootingEntity;

	public ConcussiveExplosion(World world, Entity entity, EntityPlayer par3Entity, double explosionX, double explosionY, double explosionZ, float size, boolean isFlaming, boolean isSmoking) {
		super(world, entity, explosionX, explosionY, explosionZ, size, isFlaming, isSmoking);
		this.world = world;
		exploder = entity;
		shootingEntity = par3Entity;
		explosionSize = size;
		this.explosionX = explosionX;
		this.explosionY = explosionY;
		this.explosionZ = explosionZ;
		this.playerKnockbackMap = Maps.newHashMap();
	}

	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	@Override
	public void doExplosionA() {
		float var1 = explosionSize;
		int var3;
		int var4;
		int var5;
		double d5;
		double d7;
		double d9;

		explosionSize *= 2.0F;
		var3 = MathHelper.floor(explosionX - explosionSize - 1.0D);
		var4 = MathHelper.floor(explosionX + explosionSize + 1.0D);
		var5 = MathHelper.floor(explosionY - explosionSize - 1.0D);
		int var28 = MathHelper.floor(explosionY + explosionSize + 1.0D);
		int var7 = MathHelper.floor(explosionZ - explosionSize - 1.0D);
		int var29 = MathHelper.floor(explosionZ + explosionSize + 1.0D);
		List<Entity> var9 = world.getEntitiesWithinAABBExcludingEntity(exploder, new AxisAlignedBB(var3, var5, var7, var4, var28, var29));

		Vec3d var30 = new Vec3d(explosionX, explosionY, explosionZ);

		for(Entity entity : var9) {
			if(!affectEntity(entity)) {
				continue;
			}
			double var13 = entity.getDistance(explosionX, explosionY, explosionZ) / explosionSize;
			if(var13 <= 1.0D) {
				d5 = entity.posX - explosionX;
				d7 = entity.posY + entity.getEyeHeight() - explosionY;
				d9 = entity.posZ - explosionZ;
				double var33 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

				if(var33 != 0.0D) {
					d5 /= var33;
					d7 /= var33;
					d9 /= var33;
					double var32 = world.getBlockDensity(var30, entity.getEntityBoundingBox());
					double d10 = (1.0D - var13) * var32;
					entity.attackEntityFrom(DamageSource.causeThrownDamage(exploder, shootingEntity), (int) ((d10 * d10 + d10) * 6.0D * (explosionSize * 2) + 3.0D));
					entity.motionX += d5 * d10;
					entity.motionY += d7 * d10;
					entity.motionZ += d9 * d10;
				}
			}
		}

		explosionSize = var1;
	}

	protected boolean affectEntity(Entity entity) {
		return entity instanceof EntityLiving;
	}

	/**
	 * Does the second part of the explosion (sounds, particles, drop spawn)
	 */
	@Override
	public void doExplosionB(boolean spawnParticles) {
		world.playSound(null, new BlockPos(explosionX, explosionY, explosionZ), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

		if(explosionSize >= 2.0F) {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		} else {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap() {
		return this.playerKnockbackMap;
	}

	public static class GrenadeConcussiveExplosion extends ConcussiveExplosion {

		GrenadeConcussiveExplosion(World world, Entity entity, EntityPlayer par3Entity, double explosionX, double explosionY, double explosionZ, float size, boolean isFlaming, boolean isSmoking) {
			super(world, entity, par3Entity, explosionX, explosionY, explosionZ, size, isFlaming, isSmoking);
		}

		@Override
		protected boolean affectEntity(Entity entity) {
			return entity instanceof EntityPlayer && ((EntityHolyHandGrenade) exploder).getCustomName() != null && ((EntityHolyHandGrenade) exploder).getCustomName().contains(((EntityPlayer) entity).getGameProfile().getName());
		}
	}

	public static void customBusterExplosion(Entity par1Entity, double x, double y, double z, float par8) {
		if(par1Entity.world.isRemote)
			return;
		par1Entity.world.newExplosion(par1Entity, x, y, z, par8, false, true);
	}

	public static ConcussiveExplosion customConcussiveExplosion(Entity entity, EntityPlayer player, double explosionX, double explosionY, double explosionZ, float size, boolean isFlaming) {
		ConcussiveExplosion var11 = new ConcussiveExplosion(entity.world, entity, player, explosionX, explosionY, explosionZ, size, isFlaming, true);
		var11.doExplosionA();
		var11.doExplosionB(false);

		PacketHandler.networkWrapper.sendToAllAround(new PacketFXConcussiveExplosion(size, explosionX, explosionY, explosionZ), new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 96.0D));

		return var11;
	}

	public static GrenadeConcussiveExplosion grenadeConcussiveExplosion(Entity entity, EntityPlayer player, double explosionX, double explosionY, double explosionZ, float size, boolean isFlaming) {
		GrenadeConcussiveExplosion var11 = new GrenadeConcussiveExplosion(entity.world, entity, player, explosionX, explosionY, explosionZ, size, isFlaming, true);
		var11.doExplosionA();
		var11.doExplosionB(false);

		PacketHandler.networkWrapper.sendToAllAround(new PacketFXConcussiveExplosion(size, explosionX, explosionY, explosionZ), new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 96.0D));

		return var11;
	}

}
