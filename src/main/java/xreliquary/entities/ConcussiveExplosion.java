package xreliquary.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ConcussiveExplosion extends Explosion {

	/** whether or not the explosion sets fire to blocks around it */
	public boolean isFlaming = false;
	public boolean field_82755_b = true;
	private int field_77289_h = 16;
	private World worldObj;
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public Entity exploder;
	public float explosionSize;
	private Map field_77288_k = new HashMap();
	private EntityPlayer shootingEntity;
	public boolean hurtsPlayer;

	public ConcussiveExplosion(World par1World, Entity par2Entity, EntityPlayer par3Entity, double par3, double par5, double par7, float par9) {
		super(par1World, par2Entity, par3, par5, par7, par9);
		worldObj = par1World;
		exploder = par2Entity;
		shootingEntity = par3Entity;
		explosionSize = par9;
		explosionX = par3;
		explosionY = par5;
		explosionZ = par7;
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
		double var15;
		double var17;
		double var19;

		for (var3 = 0; var3 < field_77289_h; ++var3) {
			for (var4 = 0; var4 < field_77289_h; ++var4) {
				for (var5 = 0; var5 < field_77289_h; ++var5) {
					if (var3 == 0 || var3 == field_77289_h - 1 || var4 == 0 || var4 == field_77289_h - 1 || var5 == 0 || var5 == field_77289_h - 1) {
						double var6 = var3 / (field_77289_h - 1.0F) * 2.0F - 1.0F;
						double var8 = var4 / (field_77289_h - 1.0F) * 2.0F - 1.0F;
						double var10 = var5 / (field_77289_h - 1.0F) * 2.0F - 1.0F;
						double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
						var6 /= var12;
						var8 /= var12;
						var10 /= var12;
						var15 = explosionX;
						var17 = explosionY;
						var19 = explosionZ;
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
		List var9 = worldObj.getEntitiesWithinAABBExcludingEntity(exploder, AxisAlignedBB.getAABBPool().getAABB(var3, var5, var7, var4, var28, var29));

		Vec3 var30 = worldObj.getWorldVec3Pool().getVecFromPool(explosionX, explosionY, explosionZ);

		for (int var11 = 0; var11 < var9.size(); ++var11) {
			Entity var31 = (Entity) var9.get(var11);
			if (!(var31 instanceof EntityLiving)) {
				continue;
			}
			if (var31 == shootingEntity && !hurtsPlayer) {
				continue;
			}
			double var13 = var31.getDistance(explosionX, explosionY, explosionZ) / explosionSize;
			if (var13 <= 1.0D) {
				var15 = var31.posX - explosionX;
				var17 = var31.posY + var31.getEyeHeight() - explosionY;
				var19 = var31.posZ - explosionZ;
				double var33 = MathHelper.sqrt_double(var15 * var15 + var17 * var17 + var19 * var19);

				if (var33 != 0.0D) {
					var15 /= var33;
					var17 /= var33;
					var19 /= var33;
					double var32 = worldObj.getBlockDensity(var30, var31.boundingBox);
					double var34 = (1.0D - var13) * var32;
					var31.attackEntityFrom(DamageSource.causePlayerDamage(shootingEntity), (int) ((var34 * var34 + var34) * 6.0D * (explosionSize * 2) + 3.0D));
					var31.motionX += var15 * var34;
					var31.motionY += var17 * var34;
					var31.motionZ += var19 * var34;

					if (var31 instanceof EntityPlayer) {
						field_77288_k.put(var31, worldObj.getWorldVec3Pool().getVecFromPool(var15 * var34, var17 * var34, var19 * var34));
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
		worldObj.playSoundEffect(explosionX, explosionY, explosionZ, "random.explode", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if (explosionSize >= 2.0F && field_82755_b) {
			worldObj.spawnParticle("hugeexplosion", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		} else {
			worldObj.spawnParticle("largeexplode", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
		}

	}

	@Override
	public Map func_77277_b() {
		return field_77288_k;
	}

	public static void customBusterExplosion(Entity par1Entity, EntityPlayer player, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
		if (par1Entity.worldObj.isRemote)
			return;
		par1Entity.worldObj.newExplosion(par1Entity, par2, par4, par6, par8, par9, par10);
	}

	public static ConcussiveExplosion customConcussiveExplosion(Entity par1Entity, EntityPlayer player, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
		ConcussiveExplosion var11 = new ConcussiveExplosion(par1Entity.worldObj, par1Entity, player, par2, par4, par6, par8);
		var11.isFlaming = par9;
		var11.isSmoking = par10;
		var11.doExplosionA();
		var11.doExplosionB(false);

		return var11;
	}

}
