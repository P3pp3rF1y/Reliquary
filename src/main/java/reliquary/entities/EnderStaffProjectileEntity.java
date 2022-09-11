package reliquary.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import reliquary.init.ModEntities;

@SuppressWarnings("squid:S2160")
@OnlyIn(
		value = Dist.CLIENT,
		_interface = ItemSupplier.class
)
public class EnderStaffProjectileEntity extends ThrowableProjectile implements ItemSupplier, IEntityAdditionalSpawnData {
	public EnderStaffProjectileEntity(EntityType<EnderStaffProjectileEntity> entityType, Level world) {
		super(entityType, world);
	}

	private boolean normalGravity = false;

	public EnderStaffProjectileEntity(Level world, Player entityPlayer, boolean shortRange) {
		super(ModEntities.ENDER_STAFF_PROJECTILE.get(), entityPlayer, world);
		normalGravity = shortRange;
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravity() {
		if (normalGravity) {
			return super.getGravity();
		}
		return 0.005F;
	}

	@Override
	protected void defineSynchedData() {
		//noop
	}

	@Override
	public void tick() {
		super.tick();

		if (tickCount % 4 == level.random.nextInt(5)) {
			level.addParticle(ParticleTypes.PORTAL, getX(), getY(), getZ(), 0.0D, 0.0D, 1.0D);
		}

		if (isInWater()) {
			// nobody likes being at the bottom of a lake.
			onThrowableCollision(new BlockHitResult(position(), Direction.UP, blockPosition(), true));
		}
	}

	@Override
	protected void checkInsideBlocks() {
		super.checkInsideBlocks();

		//scaling up delta movement here because this is the last place that can be overriden before ThrowableProjectile's tick logic scales delta movement down using set constant
		Vec3 deltaMovement = getDeltaMovement();
		double tinyScaleUp = 1.008;
		setDeltaMovement(deltaMovement.x * tinyScaleUp, deltaMovement.y, deltaMovement.z * tinyScaleUp);
	}

	@Override
	protected void onHit(HitResult result) {
		onThrowableCollision(result);
	}

	private void onThrowableCollision(HitResult result) {
		Entity thrower = getOwner();
		if (!(thrower instanceof Player) || ((int) getY()) <= 0) {
			discard();
			return;
		}

		for (int i = 0; i < 32; i++) {
			level.addParticle(ParticleTypes.PORTAL, getX(), getY() + random.nextDouble() * 2D, getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
		}

		if (!level.isClientSide) {
			thrower.fallDistance = 0.0F;

			int x = (int) Math.round(getX());
			int y = (int) Math.round(getY());
			int z = (int) Math.round(getZ());

			if (result.getType() != HitResult.Type.MISS) {
				BlockPos pos;
				if (result.getType() == HitResult.Type.ENTITY) {
					Entity entityHit = ((EntityHitResult) result).getEntity();
					entityHit.hurt(DamageSource.thrown(this, thrower), 0);
					pos = entityHit.blockPosition();
				} else {
					BlockHitResult blockResult = (BlockHitResult) result;
					pos = blockResult.getBlockPos().relative(blockResult.getDirection());
				}

				y = pos.getY();
				x = pos.getX();
				z = pos.getZ();
			}
			float targetX = x + 0.5F;
			float targetY = y + 0.5F;
			float targetZ = z + 0.5F;
			if (thrower instanceof ServerPlayer serverPlayer) {
				ForgeEventFactory.onEnderPearlLand(serverPlayer, targetX, targetY, targetZ, new ThrownEnderpearl(level, serverPlayer), 0);
			}

			thrower.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
			thrower.teleportTo(targetX, targetY, targetZ);
		}
		discard();
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(Items.ENDER_PEARL);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeBoolean(normalGravity);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		normalGravity = additionalData.readBoolean();
	}
}
