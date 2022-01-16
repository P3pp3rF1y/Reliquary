package xreliquary.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;

@SuppressWarnings("squid:S2160")
@OnlyIn(
		value = Dist.CLIENT,
		_interface = ItemSupplier.class
)
public class HolyHandGrenadeEntity extends ThrowableProjectile implements ItemSupplier {
	private int count = 0;
	private Player playerThrower;

	public HolyHandGrenadeEntity(EntityType<HolyHandGrenadeEntity> entityType, Level world) {
		super(entityType, world);
	}

	public HolyHandGrenadeEntity(Level world, Player player, String customName) {
		super(ModEntities.HOLY_HAND_GRENADE.get(), player, world);
		playerThrower = player;
		setCustomName(new TextComponent(customName));
	}

	public HolyHandGrenadeEntity(Level world, double x, double y, double z) {
		super(ModEntities.HOLY_HAND_GRENADE.get(), x, y, z, world);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravity() {
		return 0.03F;
	}

	@Override
	protected void defineSynchedData() {
		//noop
	}

	@Override
	public void tick() {
		super.tick();
		if (count == 2) {
			for (int particles = 0; particles < random.nextInt(2) + 1; particles++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, getX() + level.random.nextDouble(), getY() + level.random.nextDouble(), getZ() + level.random.nextDouble(), 0D, 0D, 0D);
			}
			count = 0;
		} else {
			count++;
		}
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onHit(HitResult result) {
		if (level.isClientSide) {
			return;
		}

		discard();

		//just making sure that player doesn't see the particles on client when the grenade is thrown
		if (tickCount > 3 || result.getType() != HitResult.Type.ENTITY || !(((EntityHitResult) result).getEntity() instanceof Player)) {
			ConcussiveExplosion.grenadeConcussiveExplosion(this, playerThrower, position());
		}
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.HOLY_HAND_GRENADE.get());
	}


	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
