package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntitySpecialEnderPearl extends EntityEnderPearl {
	public boolean isArced = false;

	public EntitySpecialEnderPearl(World par1World, EntityLiving par2EntityLiving, boolean b) {
		super(par1World, par2EntityLiving);
		this.setSize(0.01F, 0.01F);
		this.isArced = b;
	}

	public EntitySpecialEnderPearl(World par1World) {
		super(par1World);
	}

	public EntitySpecialEnderPearl(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	protected float func_70182_d() {
		return 1.2F;
	}

	@Override
	protected float getGravityVelocity() {
		// flies slightly farther than a normal projectile
		// unless sneaking.
		return this.isArced ? 0.03F : 0.0F;
	}

	@Override
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
		if (par1MovingObjectPosition.entityHit != null) {
			par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0);
		}
		for (int i = 0; i < 32; ++i) {
			this.worldObj.spawnParticle("portal", this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
		}
		if (!this.worldObj.isRemote) {
			if (this.getThrower() != null && this.getThrower() instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP)this.getThrower();
				if (!entityplayermp.playerNetServerHandler.connectionClosed && entityplayermp.worldObj == this.worldObj) {
					EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5);
					if (!MinecraftForge.EVENT_BUS.post(event)) {
						this.getThrower().setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
						this.getThrower().fallDistance = 0.0F;
					}
				}
			}
			this.setDead();
		}
	}
}
