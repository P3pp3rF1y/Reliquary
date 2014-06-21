package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityKrakenSlime extends EntityThrowable {
	
	public static DamageSource SOURCE = new DamageSource("slimed").setDamageIsAbsolute().setProjectile().setMagicDamage();

	public EntityKrakenSlime(World world) {
		super(world);
	}

	public EntityKrakenSlime(World world, EntityPlayer player) {
		super(world, player);
	}

	@Override
	protected void onImpact(MovingObjectPosition object) {
		if(this.worldObj.isRemote)
			return;
		if(object.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && object.entityHit instanceof EntityLiving) {
			EntityLiving living = (EntityLiving) object.entityHit;
			living.attackEntityFrom(SOURCE, 7.5f);
		}
		this.worldObj.playSoundAtEntity(this, "mob.slime.big", 0.5F, 0.4F / (worldObj.rand.nextFloat() * 0.4F + 0.8F));
		this.setDead();
	}
	
    @Override
    protected float getGravityVelocity() {
        return 0F;
    }
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(this.worldObj.isRemote)
			return;
		
		for (int count = 0; count < 4; ++count) {
			float amplifier = 0.25F;
			worldObj.spawnParticle("bubble", posX - motionX * amplifier, posY - motionY * amplifier, posZ - motionZ * amplifier, motionX, motionY, motionZ);
		}
		
		if(this.motionX == 0 && this.motionY == 0 && this.motionZ == 0) {
			this.setDead();
			return;
		}
				
		if(this.posY > this.worldObj.getHeight() || this.posY <= 0) {
			this.setDead();
		}
	
	}

}
