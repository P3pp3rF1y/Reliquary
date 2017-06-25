package xreliquary.entities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityEnderStaffProjectile extends EntityThrowable {
	private int ticksInAir;
	private int ticksInGround;

	public EntityEnderStaffProjectile(World world) {
		super(world);
	}

	private boolean normalGravity = false;

	public EntityEnderStaffProjectile(World world, EntityPlayer entityPlayer, boolean shortRange) {
		super(world, entityPlayer);
		this.normalGravity = shortRange;
	}

	public EntityEnderStaffProjectile(World world, double d, double d1, double d2) {
		super(world, d, d1, d2);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		// flies slightly farther than a normal projectile;
		// stolen from the "special" snowball, altered to allow two gravity
		// options (one emulates a normal ender pearl).
		if(this.normalGravity)
			return super.getGravityVelocity();
		return 0.01F;
	}

	@Override
	public void onUpdate() {
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		//super.onUpdate();
		onEntityUpdate();
		if(throwableShake > 0) {
			--throwableShake;
		}

		if(ticksInAir % 4 == world.rand.nextInt(5)) {
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY, posZ, 0.0D, 0.0D, 1.0D);
		}

		int xTile = (int) Math.round(posX);
		int yTile = (int) Math.round(posY);
		int zTile = (int) Math.round(posZ);

		IBlockState inTile = world.getBlockState(new BlockPos(xTile, yTile, zTile));

		if(inGround) {
			IBlockState var1 = world.getBlockState(new BlockPos(xTile, yTile, zTile));

			//TODO: ?? maybe I am blind but why do we check this when these two get initialized to the same block?
			if(var1 == inTile) {
				++ticksInGround;

				if(ticksInGround == 1200) {
					this.setDead();
				}

				return;
			}

			inGround = false;
			motionX = 0F;
			motionY = 0F;
			motionZ = 0F;
			ticksInGround = 0;
			ticksInAir = 0;
		} else {
			++ticksInAir;
		}

		Vec3d var16 = new Vec3d(posX, posY, posZ);
		Vec3d var2 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
		RayTraceResult var3 = world.rayTraceBlocks(var16, var2, false, true, false);
		var16 = new Vec3d(posX, posY, posZ);
		var2 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

		if(var3 != null) {
			var2 = new Vec3d(var3.hitVec.x, var3.hitVec.y, var3.hitVec.z);
		}

		if(!world.isRemote) {
			Entity var4 = null;
			List<Entity> var5 = world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D, 1.0D, 1.0D));
			double var6 = 0.0D;
			EntityLivingBase var8 = this.getThrower();

			for(Entity var10 : var5) {
				if(var10.canBeCollidedWith() && (var10 != var8 || ticksInAir >= 5)) {
					float var11 = 0.5F;
					AxisAlignedBB var12 = var10.getEntityBoundingBox().grow(var11, var11, var11);
					RayTraceResult var13 = var12.calculateIntercept(var16, var2);

					if(var13 != null) {
						double var14 = var16.distanceTo(var13.hitVec);

						if(var14 < var6 || var6 == 0.0D) {
							var4 = var10;
							var6 = var14;
						}
					}
				}
			}

			if(var4 != null) {
				var3 = new RayTraceResult(var4);
			}
		}

		if(var3 != null) {
			if(var3.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(var3.getBlockPos()).getBlock() == Blocks.PORTAL) {
				this.setPortal(var3.getBlockPos());
			} else {
				this.onImpact(var3);
			}
		}

		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float var17 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
		//noinspection SuspiciousNameCombination
		rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

		//noinspection StatementWithEmptyBody
		for(rotationPitch = (float) (Math.atan2(motionY, var17) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {

		}

		while(rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}

		while(rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}

		while(rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}

		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		float var18 = 0.99F;
		float var19 = this.getGravityVelocity();

		if(this.isInWater()) {
			// nobody likes being at the bottom of a lake.
			this.onThrowableCollision(null);
		}

		motionX *= var18;
		motionY *= var18;
		motionZ *= var18;
		motionY -= var19;
		this.setPosition(posX, posY, posZ);
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		onThrowableCollision(result);
	}

	private void onThrowableCollision(RayTraceResult result) {
		if(result != null && result.entityHit != null) {
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
		}
		for(int i = 0; i < 32; i++) {
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY + rand.nextDouble() * 2D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
		}

		if(!world.isRemote) {
			// zombies are too stupid to bend the fabric of space and time.
			if(this.getThrower() != null && getThrower() instanceof EntityPlayer) {
				getThrower().fallDistance = 0.0F;

				int x = (int) Math.round(posX);
				int y = (int) Math.round(posY);
				//apparently in transition, player gets pushed out to the void. That's no good.
				int z = (int) Math.round(posZ);

				if(result != null) {
					EnumFacing side = result.sideHit;

					BlockPos pos;
					if(result.typeOfHit == RayTraceResult.Type.ENTITY) {
						pos = result.entityHit.getPosition();
					} else {
						pos = result.getBlockPos().offset(side);
					}

					y = pos.getY();
					x = pos.getX();
					z = pos.getZ();
				}

				if(y < 0) {
					this.setDead();
					return;
				}

				getThrower().playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
				getThrower().setPositionAndUpdate(x + 0.5F, y + 0.5F, z + 0.5F);
			}
			this.setDead();
		}
	}
}
