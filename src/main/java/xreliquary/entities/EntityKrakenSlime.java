package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityKrakenSlime extends EntityThrowable {

    public EntityKrakenSlime(World world) {
        super(world);
    }

    public EntityKrakenSlime(World world, EntityPlayer player) {
        super(world, player);
    }

    @Override
    protected void onImpact(MovingObjectPosition object) {
        if (this.worldObj.isRemote)
            return;
        if (object.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && object.entityHit instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) object.entityHit;
            living.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 7.5f);
            //living.addPotionEffect(new PotionEffect(PotionSerpentStaff.mobPacificationDebuff.id, 300, 0));
        }

        for (int count = 0; count < 6; ++count) {
            float amplifier = 0.25F;
            worldObj.spawnParticle( EnumParticleTypes.SLIME, posX - motionX * amplifier + worldObj.rand.nextDouble(), posY - motionY * amplifier + worldObj.rand.nextDouble(), posZ - motionZ * amplifier + worldObj.rand.nextDouble(), motionX, motionY, motionZ);
        }
        this.worldObj.playSoundAtEntity(this, "mob.slime.big", 0.5F, 0.4F / (worldObj.rand.nextFloat() * 0.4F + 0.8F));
        this.setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.001F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        for (int count = 0; count < 2; ++count) {
            float amplifier = 0.25F;
            worldObj.spawnParticle(EnumParticleTypes.SLIME, posX - motionX * amplifier, posY - motionY * amplifier, posZ - motionZ * amplifier, motionX, motionY, motionZ);
        }

        if (this.worldObj.isRemote)
            return;


        if (this.motionX < 0.5 && this.motionY == 0 && this.motionZ == 0) {
            this.setDead();
            return;
        }

        if (this.posY > this.worldObj.getHeight() || this.posY <= 0) {
            this.setDead();
        }

    }

}
