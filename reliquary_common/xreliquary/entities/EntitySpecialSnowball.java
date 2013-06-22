package xreliquary.entities;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntitySpecialSnowball extends EntitySnowball {
	public int ticksInAir;
	public int ticksInGround;
	public int inTile;
	public int xTile;
	public int yTile;
	public int zTile;

	public EntitySpecialSnowball(World par1World) {
		super(par1World);
	}

	public EntitySpecialSnowball(World par1World, EntityLiving par2EntityLiving) {
		super(par1World, par2EntityLiving);
		this.setSize(0.01F, 0.01F);
	}

	public EntitySpecialSnowball(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	protected float func_70182_d() {
		return 1.2F;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
		if (par1MovingObjectPosition.entityHit != null) {
			byte var2 = 2;
			if (par1MovingObjectPosition.entityHit.isImmuneToFire()) {
				var2 = 10;
			}
			par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), var2);
		}
		for (int var3 = 0; var3 < 8; ++var3) {
			worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		}
		if (!worldObj.isRemote) {
			this.setDead();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		super.onUpdate();
		if (throwableShake > 0) {
			--throwableShake;
		}
		if (ticksInAir % 4 == worldObj.rand.nextInt(5)) {
			worldObj.spawnParticle("reddust", posX, posY, posZ, 5.0D, 5.0D, 1.0D);
		}
		xTile = (int)Math.round(posX);
		yTile = (int)Math.round(posY);
		zTile = (int)Math.round(posZ);
		inTile = worldObj.getBlockId(xTile, yTile, zTile);
		if (inGround) {
			int var1 = worldObj.getBlockId(xTile, yTile, zTile);
			if (var1 == inTile) {
				++ticksInGround;
				if (ticksInGround == 1200) {
					this.setDead();
				}
				return;
			}
			inGround = false;
			motionX *= rand.nextFloat() * 0.2F;
			motionY *= rand.nextFloat() * 0.2F;
			motionZ *= rand.nextFloat() * 0.2F;
			ticksInGround = 0;
			ticksInAir = 0;
		} else {
			++ticksInAir;
		}
		Vec3 var16 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
		Vec3 var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
		MovingObjectPosition var3 = worldObj.rayTraceBlocks(var16, var2);
		var16 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
		var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
		if (var3 != null) {
			var2 = worldObj.getWorldVec3Pool().getVecFromPool(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
		}
		if (!worldObj.isRemote) {
			Entity var4 = null;
			List var5 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double var6 = 0.0D;
			EntityLiving var8 = this.getThrower();
			for (int var9 = 0; var9 < var5.size(); ++var9) {
				Entity var10 = (Entity)var5.get(var9);
				if (var10.canBeCollidedWith() && (var10 != var8 || ticksInAir >= 5)) {
					float var11 = 0.1F;
					AxisAlignedBB var12 = var10.boundingBox.expand(var11, var11, var11);
					MovingObjectPosition var13 = var12.calculateIntercept(var16, var2);
					if (var13 != null) {
						double var14 = var16.distanceTo(var13.hitVec);
						if (var14 < var6 || var6 == 0.0D) {
							var4 = var10;
							var6 = var14;
						}
					}
				}
			}
			if (var4 != null) {
				var3 = new MovingObjectPosition(var4);
			}
		}
		if (var3 != null) {
			if (var3.typeOfHit == EnumMovingObjectType.TILE && worldObj.getBlockId(var3.blockX, var3.blockY, var3.blockZ) == Block.portal.blockID) {
				this.setInPortal();
			} else {
				this.onImpact(var3);
			}
		}
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float var17 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
		for (rotationPitch = (float)(Math.atan2(motionY, var17) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {
			;
		}
		while (rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}
		while (rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}
		while (rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		float var18 = 0.99F;
		float var19 = this.getGravityVelocity();
		if (this.isInWater()) {
			for (int var7 = 0; var7 < 4; ++var7) {
				float var20 = 0.25F;
				worldObj.spawnParticle("bubble", posX - motionX * var20, posY - motionY * var20, posZ - motionZ * var20, motionX, motionY, motionZ);
			}
			var18 = 0.8F;
		}
		motionX *= var18;
		motionY *= var18;
		motionZ *= var18;
		motionY -= var19;
		this.setPosition(posX, posY, posZ);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		// flies slightly farther than a normal projectile;
		return 0.01F;
	}
}
