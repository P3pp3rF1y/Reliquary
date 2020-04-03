package xreliquary.entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;

@SuppressWarnings("squid:S2160")
@OnlyIn(
		value = Dist.CLIENT,
		_interface = IRendersAsItem.class
)
public class EnderStaffProjectileEntity extends ThrowableEntity implements IRendersAsItem {
	private int ticksInAir;
	private Entity ignoreEntity;
	private int ignoreTime;

	public EnderStaffProjectileEntity(EntityType<EnderStaffProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	private boolean normalGravity = false;

	public EnderStaffProjectileEntity(World world, PlayerEntity entityPlayer, boolean shortRange) {
		super(ModEntities.ENDER_STAFF_PROJECTILE, entityPlayer, world);
		normalGravity = shortRange;
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		if (normalGravity) {
			return super.getGravityVelocity();
		}
		return 0.005F;
	}

	@Override
	protected void registerData() {
		//noop
	}

	@Override
	public void tick() {
		lastTickPosX = getPosX();
		lastTickPosY = getPosY();
		lastTickPosZ = getPosZ();
		baseTick();
		if (throwableShake > 0) {
			--throwableShake;
		}

		if (ticksInAir % 4 == world.rand.nextInt(5)) {
			world.addParticle(ParticleTypes.PORTAL, getPosX(), getPosY(), getPosZ(), 0.0D, 0.0D, 1.0D);
		}

		if (inGround) {
			inGround = false;
			setMotion(0, 0, 0);
			ticksInAir = 0;
		} else {
			++ticksInAir;
		}

		AxisAlignedBB axisalignedbb = getBoundingBox().expand(getMotion()).grow(1.0D);

		for (Entity entity : world.getEntitiesInAABBexcluding(this, axisalignedbb, e -> !e.isSpectator() && e.canBeCollidedWith())) {
			if (entity == ignoreEntity) {
				++ignoreTime;
				break;
			}

			if (owner != null && ticksExisted < 2 && ignoreEntity == null) {
				ignoreEntity = entity;
				ignoreTime = 3;
				break;
			}
		}

		RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, axisalignedbb,
				e -> !e.isSpectator() && e.canBeCollidedWith() && e != ignoreEntity, RayTraceContext.BlockMode.OUTLINE, true);
		if (ignoreEntity != null && ignoreTime-- <= 0) {
			ignoreEntity = null;
		}

		if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && world.getBlockState(((BlockRayTraceResult) raytraceresult).getPos()).getBlock() == Blocks.NETHER_PORTAL) {
				setPortal(((BlockRayTraceResult) raytraceresult).getPos());
			} else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				onImpact(raytraceresult);
			}
		}

		Vec3d vec3d = getMotion();
		setPosition(getPosX() + vec3d.x, getPosY() + vec3d.y, getPosZ() + vec3d.z);

		float f = MathHelper.sqrt(getDistanceSq(vec3d));

		rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));
		rotationPitch = (float) (MathHelper.atan2(vec3d.y, f) * (double) (180F / (float) Math.PI));

		while (rotationPitch - prevRotationPitch < -180.0F) {
			prevRotationPitch -= 360.0F;
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

		rotationPitch = MathHelper.lerp(0.2F, prevRotationPitch, rotationPitch);
		rotationYaw = MathHelper.lerp(0.2F, prevRotationYaw, rotationYaw);

		float var18 = 0.99F;
		float var19 = getGravityVelocity();

		if (isInWater()) {
			// nobody likes being at the bottom of a lake.
			onThrowableCollision(new BlockRayTraceResult(getPositionVec(), Direction.UP, getPosition(), true));
		}

		setMotion(getMotion().mul(var18, var18, var18).add(0, -var19, 0));
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		onThrowableCollision(result);
	}

	private void onThrowableCollision(RayTraceResult result) {
		if (!(getThrower() instanceof PlayerEntity) || ((int) getPosY()) <= 0) {
			remove();
			return;
		}

		for (int i = 0; i < 32; i++) {
			world.addParticle(ParticleTypes.PORTAL, getPosX(), getPosY() + rand.nextDouble() * 2D, getPosZ(), rand.nextGaussian(), 0.0D, rand.nextGaussian());
		}

		if (!world.isRemote) {
			getThrower().fallDistance = 0.0F;

			int x = (int) Math.round(getPosX());
			int y = (int) Math.round(getPosY());
			int z = (int) Math.round(getPosZ());

			if (result.getType() != RayTraceResult.Type.MISS) {
				BlockPos pos;
				if (result.getType() == RayTraceResult.Type.ENTITY) {
					Entity entityHit = ((EntityRayTraceResult) result).getEntity();
					entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
					pos = entityHit.getPosition();
				} else {
					BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
					pos = blockResult.getPos().offset(blockResult.getFace());
				}

				y = pos.getY();
				x = pos.getX();
				z = pos.getZ();
			}

			getThrower().playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			getThrower().setPositionAndUpdate(x + 0.5F, y + 0.5F, z + 0.5F);
		}
		remove();
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(Items.ENDER_PEARL);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
