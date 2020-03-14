package xreliquary.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;

@SuppressWarnings("squid:S2160")
@OnlyIn(
		value = Dist.CLIENT,
		_interface = IRendersAsItem.class
)
public class HolyHandGrenadeEntity extends ThrowableEntity implements IRendersAsItem {
	private int count = 0;
	private PlayerEntity playerThrower;

	public HolyHandGrenadeEntity(EntityType<HolyHandGrenadeEntity> entityType, World world) {
		super(entityType, world);
	}

	public HolyHandGrenadeEntity(World world, PlayerEntity player, String customName) {
		super(ModEntities.HOLY_HAND_GRENADE, player, world);
		playerThrower = player;
		setCustomName(new StringTextComponent(customName));
	}

	public HolyHandGrenadeEntity(World world, double x, double y, double z) {
		super(ModEntities.HOLY_HAND_GRENADE, x, y, z, world);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.03F;
	}

	@Override
	protected void registerData() {
		//noop
	}

	@Override
	public void tick() {
		super.tick();
		if (count == 2) {
			for (int particles = 0; particles < rand.nextInt(2) + 1; particles++) {
				world.addParticle(ParticleTypes.ENTITY_EFFECT, posX + world.rand.nextDouble(), posY + world.rand.nextDouble(), posZ + world.rand.nextDouble(), 0D, 0D, 0D);
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
	protected void onImpact(RayTraceResult result) {
		if (!world.isRemote) {
			remove();
		}

		//just making sure that player doesn't see the particles on client when the grenade is thrown
		if (!world.isRemote || ticksExisted > 3 || result.getType() != RayTraceResult.Type.ENTITY || !(((EntityRayTraceResult) result).getEntity() instanceof PlayerEntity)) {
			ConcussiveExplosion.grenadeConcussiveExplosion(this, playerThrower, getPositionVec());
		}
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.HOLY_HAND_GRENADE);
	}


	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
