package xreliquary.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHolyHandGrenade extends EntityThrowable {

	int count = 0;
	public EntityPlayer playerThrower;
	private String customName;

	public EntityHolyHandGrenade(World par1World) {
		super(par1World);
	}

	public EntityHolyHandGrenade(World par1World, EntityPlayer par2EntityPlayer, String customName) {
		super(par1World, par2EntityPlayer);
		playerThrower = par2EntityPlayer;
		this.customName = customName;
	}

	@SideOnly(Side.CLIENT)
	public EntityHolyHandGrenade(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6, "");
	}

	public EntityHolyHandGrenade(World par1World, double par2, double par4, double par6, String customName) {
		super(par1World, par2, par4, par6);
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
				worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, posX + worldObj.rand.nextDouble(), posY + worldObj.rand.nextDouble(), posZ + worldObj.rand.nextDouble(), 0D, 0D, 0D);
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
		this.setDead();

		ConcussiveExplosion.customConcussiveExplosion(this, playerThrower, posX, posY, posZ, 4.0F, false, true);
	}

	public String getCustomName() {
		return customName;
	}
}
