package xreliquary.entities;

import com.google.common.collect.Maps;
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

import java.util.List;
import java.util.Map;

public class ConcussiveExplosion extends Explosion {

	/**
	 * whether or not the explosion sets fire to blocks around it
	 */
	public boolean field_82755_b = true;
	private int field_77289_h = 16;
	private World worldObj;
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public Entity exploder;
	public float explosionSize;
	private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
	private EntityPlayer shootingEntity;
	public boolean hurtsPlayer;

	public ConcussiveExplosion(World world, Entity entity, EntityPlayer par3Entity, double explosionX, double explosionY, double explosionZ, float size, boolean isFlaming, boolean isSmoking) {
		super(world, entity, explosionX, explosionY, explosionZ, size, isFlaming, isSmoking);
		worldObj = world;
		exploder = entity;
		shootingEntity = par3Entity;
		explosionSize = size;
		this.explosionX = explosionX;
		this.explosionY = explosionY;
		this.explosionZ = explosionZ;
		this.playerKnockbackMap = Maps.<EntityPlayer, Vec3d>newHashMap();
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

		for(var3 = 0; var3 < field_77289_h; ++var3) {
			for(var4 = 0; var4 < field_77289_h; ++var4) {
				for(var5 = 0; var5 < field_77289_h; ++var5) {
					if(var3 == 0 || var3 == field_77289_h - 1 || var4 == 0 || var4 == field_77289_h - 1 || var5 == 0 || var5 == field_77289_h - 1) {
						double var6 = var3 / (field_77289_h - 1.0F) * 2.0F - 1.0F;
						double var8 = var4 / (field_77289_h - 1.0F) * 2.0F - 1.0F;
						double var10 = var5 / (field_77289_h - 1.0F) * 2.0F - 1.0F;
						double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
						var6 /= var12;
						var8 /= var12;
						var10 /= var12;
						d5 = explosionX;
						d7 = explosionY;
						d9 = explosionZ;
					}
				}
			}
		}

		explosionSize *= 2.0F;
		var3 = MathHelper.floor_double(explosionX - explosionSize - 1.0D);
		var4 = MathHelper.floor_double(explosionX + explosionSize + 1.0D);
		var5 = MathHelper.floor_double(explosionY - explosionSize - 1.0D);
		int var28 = MathHelper.floor_double(explosionY + explosionSize + 1.0D);
		int var7 = MathHelper.floor_double(explosionZ - explosionSize - 1.0D);
		int var29 = MathHelper.floor_double(explosionZ + explosionSize + 1.0D);
		List var9 = worldObj.getEntitiesWithinAABBExcludingEntity(exploder, new AxisAlignedBB(var3, var5, var7, var4, var28, var29));

		Vec3d var30 = new Vec3d(explosionX, explosionY, explosionZ);

		for(int var11 = 0; var11 < var9.size(); ++var11) {
			Entity entity = (Entity) var9.get(var11);
			if(!(entity instanceof EntityLiving)) {
				continue;
			}
			if(entity == shootingEntity && !hurtsPlayer) {
				continue;
			}
			double var13 = entity.getDistance(explosionX, explosionY, explosionZ) / explosionSize;
			if(var13 <= 1.0D) {
				d5 = entity.posX - explosionX;
				d7 = entity.posY + entity.getEyeHeight() - explosionY;
				d9 = entity.posZ - explosionZ;
				double var33 = MathHelper.sqrt_double(d5 * d5 + d7 * d7 + d9 * d9);

				if(var33 != 0.0D) {
					d5 /= var33;
					d7 /= var33;
					d9 /= var33;
					double var32 = worldObj.getBlockDensity(var30, entity.getEntityBoundingBox());
					double d10 = (1.0D - var13) * var32;
					entity.attackEntityFrom(DamageSource.causePlayerDamage(shootingEntity), (int) ((d10 * d10 + d10) * 6.0D * (explosionSize * 2) + 3.0D));
					entity.motionX += d5 * d10;
					entity.motionY += d7 * d10;
					entity.motionZ += d9 * d10;

					if(entity instanceof EntityPlayer) {
						this.playerKnockbackMap.put((EntityPlayer) entity, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
					}
				}
			}
		}

		explosionSize = var1;
	}

	/**
	 * Does the second part of the explosion (sounds, particles, drop spawn)
	 */
	@Override
	public void doExplosionB(boolean par1) {
		worldObj.playSound(null, new BlockPos(explosionX, explosionY, explosionZ), SoundEvents.entity_generic_explode, SoundCategory.BLOCKS, 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if(explosionSize >= 2.0F && field_82755_b) {
			worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		} else {
			worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		}

	}

	@Override
	public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap() {
		return this.playerKnockbackMap;
	}

	public static void customBusterExplosion(Entity par1Entity, EntityPlayer player, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
		if(par1Entity.worldObj.isRemote)
			return;
		par1Entity.worldObj.newExplosion(par1Entity, par2, par4, par6, par8, par9, par10);
	}

	public static ConcussiveExplosion customConcussiveExplosion(Entity entity, EntityPlayer player, double explosionX, double explosionY, double explosionZ, float size, boolean isFlaming, boolean isSmoking) {
		ConcussiveExplosion var11 = new ConcussiveExplosion(entity.worldObj, entity, player, explosionX, explosionY, explosionZ, size, isFlaming, isSmoking);
		var11.doExplosionA();
		var11.doExplosionB(false);

		return var11;
	}

}
