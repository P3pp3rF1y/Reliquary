package reliquary.entities.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import reliquary.init.ModEntities;
import reliquary.reference.Settings;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("squid:S2160")
public abstract class ShotEntityBase extends Projectile {
	private static final EntityDataAccessor<Byte> CRITICAL = SynchedEntityData.defineId(ShotEntityBase.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ShotEntityBase.class, EntityDataSerializers.INT);
	private List<MobEffectInstance> potionEffects = Collections.emptyList();

	/**
	 * The owner of this arrow.
	 */
	protected int ticksInAir = 0;

	private int ricochetCounter = 0;
	private boolean scheduledForDeath = false;

	protected <T extends ShotEntityBase> ShotEntityBase(EntityType<T> entityType, Level world) {
		super(entityType, world);
	}

	protected <T extends ShotEntityBase> ShotEntityBase(EntityType<T> entityType, Level world, Player player, InteractionHand hand) {
		this(entityType, world);
		setOwner(player);
		moveTo(player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), player.getYRot(), player.getXRot());
		setPos(
				getX() - Mth.cos(getYRot() / 180.0F * (float) Math.PI) * (hand == InteractionHand.MAIN_HAND ? 1 : -1) * 0.16F,
				getY() - 0.2D,
				getZ() - Mth.sin(getYRot() / 180.0F * (float) Math.PI) * (hand == InteractionHand.MAIN_HAND ? 1 : -1) * 0.16F
		);
	}

	protected Optional<Player> getShooterPlayer() {
		return Optional.ofNullable((Player) getOwner());
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(CRITICAL, (byte) 0);
		entityData.define(COLOR, 0);
	}

	public ShotEntityBase addPotionEffects(List<MobEffectInstance> effects) {
		if (!effects.isEmpty()) {
			potionEffects = effects;
			entityData.set(COLOR, PotionUtils.getColor(effects));
		}

		return this;
	}

	@Override
	public void shoot(double motionX, double motionY, double motionZ, float velocity, float inaccuracy) {
		float var9 = (float) Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
		motionX /= var9;
		motionY /= var9;
		motionZ /= var9;
		motionX += random.nextGaussian() * 0.0075D * inaccuracy;
		motionY += random.nextGaussian() * 0.0075D * inaccuracy;
		motionZ += random.nextGaussian() * 0.0075D * inaccuracy;
		motionX *= velocity;
		motionY *= velocity;
		motionZ *= velocity;
		setDeltaMovement(motionX, motionY, motionZ);
		float var10 = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
		setYRot((float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI));
		setXRot((float) (Math.atan2(motionY, var10) * 180.0D / Math.PI));
		yRotO = getYRot();
		xRotO = getXRot();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		setPos(x, y, z);
		setRot(yaw, pitch);
	}

	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void lerpMotion(double motionX, double motionY, double motionZ) {
		setDeltaMovement(motionX, motionY, motionZ);

		if (xRotO == 0.0F && yRotO == 0.0F) {
			float var7 = (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
			setYRot((float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI));
			setXRot((float) (Math.atan2(motionY, var7) * 180.0D / Math.PI));
			yRotO = getYRot();
			xRotO = getXRot();
			moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
		}
	}

	/**
	 * Called to update the entity's position/logic. Special snippets of the
	 * usual projectile code have been removed so they can be handled manually
	 * in the onImpact methods of the base shot.
	 */
	@Override
	public void tick() {
		super.tick();
		if (ticksInAir > 200) {
			discard();
		}

		if (level().isClientSide) {
			spawnPotionParticles();
		}
		Vec3 motionVec = getDeltaMovement();
		if (xRotO == 0.0F && yRotO == 0.0F) {
			float pythingy = (float) Math.sqrt(motionVec.x() * motionVec.x() + motionVec.z() * motionVec.z());
			setYRot((float) (Math.atan2(motionVec.x(), motionVec.z()) * 180.0D / Math.PI));
			setXRot((float) (Math.atan2(motionVec.y(), pythingy) * 180.0D / Math.PI));
			yRotO = getYRot();
			xRotO = getXRot();
		}

		++ticksInAir;
		if (ticksInAir == 2) {
			level().addParticle(ParticleTypes.FLAME, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0D, 0D, 0D);
			for (int particles = 0; particles < 3; particles++) {
				doFiringEffects();
			}

		} else {
			doFlightEffects();
		}

		Vec3 posVector = new Vec3(getX(), getY(), getZ());
		Vec3 approachVector = new Vec3(getX() + motionVec.x(), getY() + motionVec.y(), getZ() + motionVec.z());

		HitResult objectStruckByVector = level().clip(new ClipContext(posVector, approachVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

		Entity hitEntity = getHitEntity(motionVec, posVector, approachVector);

		if (hitEntity != null) {
			objectStruckByVector = new EntityHitResult(hitEntity);
		}

		//noinspection ConstantConditions - world.rayTraceBlocks can still produce null under certain conditions
		if (objectStruckByVector != null) {
			applyPotionEffects(objectStruckByVector);
			onHit(objectStruckByVector);
		}

		if (scheduledForDeath) {
			discard();
		}

		Vec3 newPos = position().add(getDeltaMovement());
		setPos(newPos.x, newPos.y, newPos.z);
	}

	@Nullable
	private Entity getHitEntity(Vec3 motionVec, Vec3 posVector, Vec3 approachVector) {
		Entity hitEntity = null;
		List<Entity> struckEntitiesInAABB = level().getEntities(this, getBoundingBox().expandTowards(motionVec).inflate(1.0D, 1.0D, 1.0D));
		double var7 = 0.0D;
		Iterator<Entity> struckEntityIterator = struckEntitiesInAABB.iterator();
		float var11;

		while (struckEntityIterator.hasNext()) {
			Entity struckEntity = struckEntityIterator.next();
			if (struckEntity.isPickable() && (struckEntity != getOwner() || ticksInAir >= 5)) {
				var11 = 0.5F;
				AABB var12 = struckEntity.getBoundingBox().inflate(var11, var11, var11);
				Optional<Vec3> hitResult = var12.clip(posVector, approachVector);

				if (hitResult.isPresent()) {
					double var14 = posVector.distanceTo(hitResult.get());

					if (var14 < var7 || var7 == 0.0D) {
						hitEntity = struckEntity;
						var7 = var14;
					}
				}
			}
		}
		return hitEntity;
	}

	private void spawnPotionParticles() {
		int color = getColor();

		if (color != 0) {
			double d0 = (color >> 16 & 255) / 255.0D;
			double d1 = (color >> 8 & 255) / 255.0D;
			double d2 = (color & 255) / 255.0D;

			for (int j = 0; j < 2; ++j) {
				level().addParticle(ParticleTypes.ENTITY_EFFECT, getX() + (random.nextDouble() - 0.5D) * getBbWidth(), getY() + random.nextDouble() * getBbHeight(), getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), d0, d1, d2);
			}
		}
	}

	public int getColor() {
		return entityData.get(COLOR);
	}

	private void applyPotionEffects(HitResult objectStruckByVector) {
		if (objectStruckByVector.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityStruckResult = ((EntityHitResult) objectStruckByVector);
			if (entityStruckResult.getEntity() instanceof LivingEntity living && potionEffects != null && !potionEffects.isEmpty()) {
				XRPotionHelper.applyEffectsToEntity(potionEffects, this, getOwner(), living);
			}
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		potionEffects = XRPotionHelper.getPotionEffectsFromCompoundTag(compound);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		XRPotionHelper.addPotionEffectsToCompoundTag(compound, potionEffects);
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = 64.0D;
		return distance < d0 * d0;
	}

	/**
	 * If returns false, the item will not inflict any damage against entities.
	 */
	@Override
	public boolean isAttackable() {
		return false;
	}

	// these are just simulated dice rolls, they make it slightly easier to
	// adjust damage
	int d3() {
		return random.nextInt(3) + 1;
	}

	int d6() {
		return random.nextInt(6) + 1;
	}

	int d12() {
		return random.nextInt(12) + 1;
	}

	void doDamage(LivingEntity e) {
		// minor modification here, the shots are quite strong
		// so I've made it so they only do half damage against player entities.
		e.hurt(getDamageSource(e), (e instanceof Player ? 0.5F : 1F) * adjustDamageForPotionShots(getDamageOfShot(e)));
	}

	protected DamageSource getDamageSource(LivingEntity livingEntity) {
		return livingEntity.damageSources().source(ModEntities.BULLET_DAMAGE_TYPE, getOwner(), this);
	}

	private float adjustDamageForPotionShots(int damageOfShot) {
		return potionEffects != null && !potionEffects.isEmpty() ? 4 : damageOfShot; //setting the cap to damage 4 for potion shots
	}

	protected void spawnMotionBasedParticle(ParticleOptions particleData) {
		spawnMotionBasedParticle(particleData, getY());
	}

	protected void spawnMotionBasedParticle(ParticleOptions particleData, double y) {
		Vec3 motion = getDeltaMovement();
		level().addParticle(particleData, getX(), y, getZ(), gaussian(motion.x()), gaussian(motion.y()), gaussian(motion.z()));
	}

	protected void groundImpact(Direction sideHit) {
		ricochet(sideHit);
	}

	/**
	 * @param d is the factor of the double
	 * @return a negative or positive value with limits of 50% of d
	 */
	protected double smallGauss(double d) {
		return (level().random.nextFloat() - 0.5D) * d;
	}

	/**
	 * @param d is the factor of the double
	 * @return a positive value between 0% and 50% of d
	 */
	double posGauss(double d) {
		return random.nextFloat() * 0.5D * d;
	}

	/**
	 * @param d haphazardly determines the upper bounds of the [always
	 *          positive] gaussian
	 * @return a [comparatively] normal gaussian ranging from 75% to 125% of the
	 * parameter d
	 */
	double gaussian(double d) {
		return d + d * ((random.nextFloat() - 0.5D) / 4);
	}

	/**
	 * @param d haphazardly determines the upper bounds of the [always
	 *          positive] gaussian
	 * @return a [comparatively] low gaussian ranging from 25% to 75% of the
	 * parameter d
	 */
	double lowGauss(double d) {
		return d - d * (random.nextFloat() / 4 + 0.5);
	}

	/**
	 * Handles the ricochet "event", more or less determines when the entity
	 * does or doesn't ricochet. If the ricochet limit is set to 0, it will
	 * still "ricochet" once, but will immediately self destruct by calling its
	 * burstEffect and setting itself to dead.
	 */
	private void ricochet(Direction sideHit) {
		switch (sideHit) {
			case DOWN, UP -> setDeltaMovement(getDeltaMovement().multiply(1, -1, 1));
			case WEST, EAST -> setDeltaMovement(getDeltaMovement().multiply(-1, 1, 1));
			case SOUTH, NORTH -> setDeltaMovement(getDeltaMovement().multiply(1, 1, -1));
			default -> {/*noop*/}
		}
		ricochetCounter++;
		if (ricochetCounter > getRicochetMax()) {
			doBurstEffect(sideHit);
			scheduledForDeath = true;
			for (int particles = 0; particles < 4; particles++) {
				switch (sideHit) {
					case DOWN -> level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), gaussian(0.1D), -gaussian(0.1D), gaussian(0.1D));
					case UP, SOUTH, EAST -> level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), gaussian(0.1D), gaussian(0.1D), gaussian(0.1D));
					case NORTH -> level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), gaussian(0.1D), gaussian(0.1D), -gaussian(0.1D));
					case WEST -> level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), -gaussian(0.1D), gaussian(0.1D), gaussian(0.1D));
					default -> {/*noop*/}
				}
			}
		}
	}

	/*
	 * custom seek method, currently only being used in seeker and ender shots.
	 * it could be better, but it works. As of writing this, both shots share
	 * the same formulas.
	 */
	void seekTarget() {
		Entity closestTarget = null;
		List<String> huntableEntitiesBlacklist = Settings.COMMON.items.seekerShot.huntableEntitiesBlacklist.get();
		List<Entity> targetsList = level().getEntities(this,
				new AABB(getX() - 5, getY() - 5, getZ() - 5, getX() + 5, getY() + 5, getZ() + 5),
				Mob.class::isInstance);
		Iterator<Entity> iTarget = targetsList.iterator();
		double closestDistance = Double.MAX_VALUE;
		while (iTarget.hasNext()) {
			Entity currentTarget = iTarget.next();

			String entityName = RegistryHelper.getRegistryName(currentTarget).toString();
			if (huntableEntitiesBlacklist.contains(entityName) || (currentTarget == getOwner()) || (!currentTarget.isAlive()
					|| (currentTarget instanceof LivingEntity living && living.getHealth() <= 0))) {
				continue;
			}
			// goes for the closest thing it can
			if (distanceTo(currentTarget) < closestDistance) {
				closestDistance = distanceTo(currentTarget);
				closestTarget = currentTarget;
			}
		}
		// these are extremely touchy, tune them lightly.
		if (closestTarget != null && getOwner() != null) {
			double x = closestTarget.getBoundingBox().minX + closestTarget.getBoundingBox().maxX;
			x /= 2D;
			double y = closestTarget.getBoundingBox().minY + closestTarget.getBoundingBox().maxY;
			y /= 2D;
			double z = closestTarget.getBoundingBox().minZ + closestTarget.getBoundingBox().maxZ;
			z /= 2D;
			double trueX = getBoundingBox().minX + getBoundingBox().maxX;
			trueX /= 2D;
			double trueY = getBoundingBox().minY + getBoundingBox().maxY;
			trueY /= 2D;
			double trueZ = getBoundingBox().minZ + getBoundingBox().maxZ;
			trueZ /= 2D;
			Vec3 seekVector = new Vec3(x - trueX, y - trueY, z - trueZ);
			seekVector = seekVector.normalize();
			setDeltaMovement(seekVector.multiply(0.4D, 0.4D, 0.4D));

			if (level().isClientSide) {
				lerpMotion(getDeltaMovement().x(), getDeltaMovement().y(), getDeltaMovement().z());
			}
		}
	}

	/**
	 * Determines the maximum number of bounces a bullet gets before burstEffect
	 * + dying. 0 means it is destroyed the moment it makes impact. This only
	 * matters if you actually call "groundImpact" from your onImpact(MOP)
	 * method.
	 */
	abstract int getRicochetMax();

	/**
	 * @param e an optional parameter, some shots need to know what entity
	 *          they're hitting for damage counts
	 * @return the int of damage the shot should deal. Most of these use my "dX"
	 * methods of this class to randomize damage, to a degree.
	 */
	abstract int getDamageOfShot(LivingEntity e);

	/**
	 * Additional entity impact effects should go here
	 *
	 * @param entityLiving the entity being struck
	 */
	protected void onImpact(LivingEntity entityLiving) {
		if (!level().isClientSide) {
			if (entityLiving != getOwner() || ticksInAir > 3) {
				doDamage(entityLiving);
			}
			spawnHitParticles(8);
			scheduledForDeath = true;
		}
	}

	@Override
	protected void onHit(HitResult result) {
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity entity = ((EntityHitResult) result).getEntity();
			if (entity == getOwner() || !(entity instanceof LivingEntity)) {
				return;
			}
			onImpact((LivingEntity) entity);
		} else if (result.getType() == HitResult.Type.BLOCK) {
			groundImpact(((BlockHitResult) result).getDirection());
		}
	}

	/**
	 * This is the effect called when the shot reaches the ground and has no
	 * ricochets remaining. It can also be called at any time by hooking into
	 * flight/firing effects.
	 *
	 * @param sideHit is sometimes used when you need particles to fly in a certain
	 *                direction.
	 */
	abstract void doBurstEffect(Direction sideHit);

	/**
	 * The particle/effect window when the gun is initially fired, between
	 * "ticksAlive" 1 and 3.
	 */
	abstract void doFiringEffects();

	/**
	 * The particle/effect window any time after the initial firing window of
	 * doFiringEffects. (ticksAlive >= 3)
	 */
	abstract void doFlightEffects();

	/**
	 * The particles which spawn when the bullet impacts an entity.
	 *
	 * @param i the number of times you want the inner for loop to produce a
	 *          single particle at random velocity
	 */
	abstract void spawnHitParticles(int i);

	// used by the renderer to pull the shot texture directly from the entity.
	// This might not work.
	public abstract ResourceLocation getShotTexture();

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
