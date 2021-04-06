package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;

@SuppressWarnings("squid:S2160")
@OnlyIn(
		value = Dist.CLIENT,
		_interface = IRendersAsItem.class
)
public class EnderStaffProjectileEntity extends ThrowableEntity implements IRendersAsItem, IEntityAdditionalSpawnData {
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
		super.tick();

		if (ticksExisted % 4 == world.rand.nextInt(5)) {
			world.addParticle(ParticleTypes.PORTAL, getPosX(), getPosY(), getPosZ(), 0.0D, 0.0D, 1.0D);
		}

		if (isInWater()) {
			// nobody likes being at the bottom of a lake.
			onThrowableCollision(new BlockRayTraceResult(getPositionVec(), Direction.UP, getPosition(), true));
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		onThrowableCollision(result);
	}

	private void onThrowableCollision(RayTraceResult result) {
		Entity thrower = getShooter();
		if (!(thrower instanceof PlayerEntity) || ((int) getPosY()) <= 0) {
			remove();
			return;
		}

		for (int i = 0; i < 32; i++) {
			world.addParticle(ParticleTypes.PORTAL, getPosX(), getPosY() + rand.nextDouble() * 2D, getPosZ(), rand.nextGaussian(), 0.0D, rand.nextGaussian());
		}

		if (!world.isRemote) {
			thrower.fallDistance = 0.0F;

			int x = (int) Math.round(getPosX());
			int y = (int) Math.round(getPosY());
			int z = (int) Math.round(getPosZ());

			if (result.getType() != RayTraceResult.Type.MISS) {
				BlockPos pos;
				if (result.getType() == RayTraceResult.Type.ENTITY) {
					Entity entityHit = ((EntityRayTraceResult) result).getEntity();
					entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 0);
					pos = entityHit.getPosition();
				} else {
					BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
					pos = blockResult.getPos().offset(blockResult.getFace());
				}

				y = pos.getY();
				x = pos.getX();
				z = pos.getZ();
			}
			float targetX = x + 0.5F;
			float targetY = y + 0.5F;
			float targetZ = z + 0.5F;
			if (thrower instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) thrower;
				ForgeEventFactory.onEnderPearlLand(player, targetX, targetY, targetZ, new EnderPearlEntity(world, player), 0);
			}

			thrower.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			thrower.setPositionAndUpdate(targetX, targetY, targetZ);
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

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeBoolean(normalGravity);
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		normalGravity = additionalData.readBoolean();
	}
}
