package xreliquary.entities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderStaffProjectile extends EntityEnderPearl {
	public int ticksInAir;
	public int ticksInGround;
	public Block inTile;
	public int xTile;
	public int yTile;
	public int zTile;

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
		if (this.normalGravity)
			return super.getGravityVelocity();
		return 0.01F;
	}

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
			worldObj.spawnParticle("portal", posX, posY, posZ, 0.0D, 0.0D, 1.0D);
		}
		xTile = (int) Math.round(posX);
		yTile = (int) Math.round(posY);
		zTile = (int) Math.round(posZ);
		inTile = worldObj.getBlock(xTile, yTile, zTile);

		if (inGround) {
			Block var1 = worldObj.getBlock(xTile, yTile, zTile);

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

		Vec3 var16 = Vec3.createVectorHelper(posX, posY, posZ);
		Vec3 var2 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
		MovingObjectPosition var3 = worldObj.func_147447_a(var16, var2, false, true, false);
		var16 = Vec3.createVectorHelper(posX, posY, posZ);
		var2 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

		if (var3 != null) {
			var2 = Vec3.createVectorHelper(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
		}

		if (!worldObj.isRemote) {
			Entity var4 = null;
			List var5 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double var6 = 0.0D;
			EntityLivingBase var8 = this.getThrower();

			for (int var9 = 0; var9 < var5.size(); ++var9) {
				Entity var10 = (Entity) var5.get(var9);

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
			if (var3.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldObj.getBlock(var3.blockX, var3.blockY, var3.blockZ) == Blocks.portal) {
				this.setInPortal();
			} else {
				this.onImpact(var3);
			}
		}

		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float var17 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

		for (rotationPitch = (float) (Math.atan2(motionY, var17) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {

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
			// nobody likes being at the bottom of a lake.
			this.onThrowableCollision(null);
		}

		motionX *= var18;
		motionY *= var18;
		motionZ *= var18;
		motionY -= var19;
		this.setPosition(posX, posY, posZ);
	}

	protected void onThrowableCollision(MovingObjectPosition movingobjectposition) {
		// I broke this because I pass null when it touches water.. only cos I
		// ain't using it.
		// so you have to check for MoP null as well.
		if (movingobjectposition != null && movingobjectposition.entityHit != null) {
			if (!movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0))
				;
		}
		for (int i = 0; i < 32; i++) {
			worldObj.spawnParticle("portal", posX, posY + rand.nextDouble() * 2D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
		}

		if (!worldObj.isRemote) {
			// zombies are too stupid to bend the fabric of space and time.
			if (this.getThrower() != null && getThrower() instanceof EntityPlayer) {
				// lazy teleportation. This should more appropriately do a quick
				// scan for "safe" positions to place a player.
				// for now I'm operating under the [incorrect] assumption that
				// the pearl will always land somewhere safe
				// and the player needs to learn to teleport properly if they
				// wind up crushing themselves.
				getThrower().setPositionAndUpdate(this.posX, this.posY, this.posZ);
				// allows you to defy whatever gravity you were being affected
				// by whilst casting the ender pearl.
				// doing so in midair "cheats" physics and prevents you from
				// taking some (if not all) of the fall damage.
				getThrower().fallDistance = 0.0F;
			}
			this.setDead();
		}
	}
}
