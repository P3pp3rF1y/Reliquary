package xreliquary.entities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityGlowingWater extends EntityThrowable {
	public EntityGlowingWater(World par1World) {
		super(par1World);
	}

	public EntityGlowingWater(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@SideOnly(Side.CLIENT)
	public EntityGlowingWater(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityGlowingWater(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.03F;
	}

	@Override
	protected float func_70182_d() {
		return 0.7F;
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
		if (!worldObj.isRemote) {
			this.spawnParticles();
			AxisAlignedBB bb = boundingBox.expand(4.0D, 2.0D, 4.0D);
			List eList = worldObj.getEntitiesWithinAABB(EntityLiving.class, bb);
			Iterator i = eList.iterator();
			while (i.hasNext()) {
				EntityLiving e = (EntityLiving) i.next();
				if (isUndead(e) && this.getThrower() != null && this.getThrower() instanceof EntityPlayer) {
					e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), 18 + rand.nextInt(17));
				}
			}

			worldObj.playAuxSFX(2002, (int) Math.round(posX), (int) Math.round(posY), (int) Math.round(posZ), 0);
			this.setDead();
		}
	}

	private void spawnParticles() {
		for (int particles = 0; particles < 40; particles++) {
			worldObj.spawnParticle("mobSpellAmbient", posX + rand.nextGaussian() * 4, posY + rand.nextGaussian() * 4, posZ + rand.nextGaussian() * 4, 1.0F, 1.0F, 0.0F);
		}
	}

	private boolean isUndead(Entity mop) {
		return mop instanceof EntitySkeleton || mop instanceof EntityGhast || mop instanceof EntityWither || mop instanceof EntityZombie || mop instanceof EntityPigZombie;
	}
}
