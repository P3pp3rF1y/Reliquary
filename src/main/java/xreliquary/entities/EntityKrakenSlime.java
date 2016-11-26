package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityKrakenSlime extends EntityThrowable {

	@SuppressWarnings("unused")
	public EntityKrakenSlime(World world) {
		super(world);
	}

	public EntityKrakenSlime(World world, EntityPlayer player) {
		super(world, player);
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if(this.world.isRemote)
			return;
		if(result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityLiving) {
			EntityLiving living = (EntityLiving) result.entityHit;
			living.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 7.5f);
			//living.addPotionEffect(new PotionEffect(PotionSerpentStaff.mobPacificationDebuff.id, 300, 0));
		}

		for(int count = 0; count < 6; ++count) {
			float amplifier = 0.25F;
			world.spawnParticle(EnumParticleTypes.SLIME, posX - motionX * amplifier + world.rand.nextDouble(), posY - motionY * amplifier + world.rand.nextDouble(), posZ - motionZ * amplifier + world.rand.nextDouble(), motionX, motionY, motionZ);
		}
		this.world.playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
		this.setDead();
	}

	@Override
	protected float getGravityVelocity() {
		return 0.001F;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		for(int count = 0; count < 2; ++count) {
			float amplifier = 0.25F;
			world.spawnParticle(EnumParticleTypes.SLIME, posX - motionX * amplifier, posY - motionY * amplifier, posZ - motionZ * amplifier, motionX, motionY, motionZ);
		}

		if(this.world.isRemote)
			return;

		if(this.motionX < 0.5 && this.motionY == 0 && this.motionZ == 0) {
			this.setDead();
			return;
		}

		if(this.posY > this.world.getHeight() || this.posY <= 0) {
			this.setDead();
		}

	}

}
