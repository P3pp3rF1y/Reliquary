package xreliquary.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHolyHandGrenade extends EntityThrowable {
	public EntityPlayer playerThrower;

	public EntityHolyHandGrenade(World par1World) {
		super(par1World);
	}

	public EntityHolyHandGrenade(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
		playerThrower = par2EntityPlayer;
	}

	@SideOnly(Side.CLIENT)
	public EntityHolyHandGrenade(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityHolyHandGrenade(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.04F;
	}

	@Override
	protected float func_70182_d() {
		return 0.9F;
	}

	@Override
	protected float func_70183_g() {
		return -20.0F;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(MovingObjectPosition mop) {
		if (playerThrower == null) {
			this.setDead();
		}
		Reliquary.customConcussiveExplosion(this, playerThrower, posX, posY, posZ, 4.0F, false, true);
		this.setDead();
	}
}
