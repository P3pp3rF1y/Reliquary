package xreliquary.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class EntityHolyHandGrenade extends EntityThrowable {

	private int count = 0;
	private EntityPlayer playerThrower;
	private String customName;

	@SuppressWarnings("unused")
	public EntityHolyHandGrenade(World world) {
		super(world);
	}

	public EntityHolyHandGrenade(World world, EntityPlayer player, String customName) {
		super(world, player);
		//offSetGrenade(player);
		playerThrower = player;
		this.customName = customName;
	}

	private void offSetGrenade(EntityPlayer player) {
		float pitch = MathHelper.sin(player.rotationPitch * 0.017453292F);
		float yOffset = - 2F * pitch;
		float xOffset = (- 2F * MathHelper.sin(player.rotationYawHead * 0.017453292F)) * MathHelper.sqrt((1F - MathHelper.abs(pitch)) / 1F) * 2;
		float zOffset = 2F * MathHelper.cos(player.rotationYawHead * 0.017453292F) * MathHelper.sqrt((1F - MathHelper.abs(pitch)) / 1F) * 2;

		this.setPosition(posX + xOffset, posY + yOffset, posZ + zOffset);
	}

	public EntityHolyHandGrenade(World world, double x, double y, double z, String customName) {
		super(world, x, y, z);
		this.customName = customName;
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.03F;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(count == 2) {
			for(int particles = 0; particles < rand.nextInt(2) + 1; particles++) {
				world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX + world.rand.nextDouble(), posY + world.rand.nextDouble(), posZ + world.rand.nextDouble(), 0D, 0D, 0D);
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
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (!world.isRemote)
			this.setDead();

		//just making sure that player doesn't see the particles on client when the grenade is thrown
		if (!world.isRemote || ticksExisted > 3 || result.typeOfHit != RayTraceResult.Type.ENTITY || !(result.entityHit instanceof EntityPlayer))
			ConcussiveExplosion.customConcussiveExplosion(this, playerThrower, posX, posY, posZ, 4.0F, false);
	}

	String getCustomName() {
		return customName;
	}
}
