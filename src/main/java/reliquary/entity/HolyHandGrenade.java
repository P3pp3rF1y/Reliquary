package reliquary.entity;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;

@SuppressWarnings("squid:S2160")
public class HolyHandGrenade extends ThrowableProjectile implements ItemSupplier {
	private int count = 0;
	private Player playerThrower;

	public HolyHandGrenade(EntityType<HolyHandGrenade> entityType, Level level) {
		super(entityType, level);
	}

	public HolyHandGrenade(Level level, Player player, String customName) {
		super(ModEntities.HOLY_HAND_GRENADE.get(), player, level);
		playerThrower = player;
		setCustomName(Component.literal(customName));
	}

	public HolyHandGrenade(Level level, Position position) {
		super(ModEntities.HOLY_HAND_GRENADE.get(), position.x(), position.y(), position.z(), level);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected double getDefaultGravity() {
		return 0.03F;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		//noop
	}

	@Override
	public void tick() {
		super.tick();
		if (count == 2) {
			for (int particles = 0; particles < random.nextInt(2) + 1; particles++) {
				level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, FastColor.ARGB32.opaque( 0)), getX() + level().random.nextDouble(), getY() + level().random.nextDouble(), getZ() + level().random.nextDouble(), 0D, 0D, 0D);
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
		if (level().isClientSide) {
			return;
		}

		if (tickCount > 3 || result.getType() != HitResult.Type.ENTITY || !(((EntityHitResult) result).getEntity() instanceof Player)) {
			ConcussiveExplosion.grenadeConcussiveExplosion(this, playerThrower, position());
		}

		discard();
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.HOLY_HAND_GRENADE.get());
	}
}
