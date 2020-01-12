package xreliquary.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CauldronSteamParticle extends SpriteTexturedParticle {
	private final IAnimatedSprite spriteSet;

	public CauldronSteamParticle(World world, ColorParticleData particleData, double x, double y, double z, double yMot, IAnimatedSprite spriteSet) {
		this(world, particleData, x, y, z, 0D, yMot, 0D, spriteSet);
	}

	private CauldronSteamParticle(World world, ColorParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteSet) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		this.particleRed = particleData.getRed();
		this.particleGreen = particleData.getGreen();
		this.particleBlue = particleData.getBlue();
		this.spriteSet = spriteSet;
	}

	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if (age++ >= maxAge) {
			setExpired();
		} else {
			selectSpriteWithAge(spriteSet);
			move(motionX, motionY, motionZ);
			if (posY == prevPosY) {
				motionX *= 1.1D;
				motionZ *= 1.1D;
			}

			motionX *= 0.9599999785423279D;
			motionY *= 0.9599999785423279D;
			motionZ *= 0.9599999785423279D;
			if (onGround) {
				motionX *= 0.699999988079071D;
				motionZ *= 0.699999988079071D;
			}

		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (age++ >= maxAge) {
			setExpired();
		}

		selectSpriteWithAge(spriteSet);
		setAlphaF((1F - ((float) age / (float) maxAge)) / 2F);
		move(motionX, motionY, motionZ);

		if (posY == prevPosY) {
			motionX *= 1.1D;
			motionZ *= 1.1D;
		}

		motionX *= 0.9599999785423279D;
		motionY *= 0.9599999785423279D;
		motionZ *= 0.9599999785423279D;

		if (onGround) {
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<ColorParticleData> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Nullable
		@Override
		public Particle makeParticle(ColorParticleData particleData, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new CauldronSteamParticle(world, particleData, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
		}
	}
}
