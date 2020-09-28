package xreliquary.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CauldronSteamParticle extends SpriteTexturedParticle {
	private final IAnimatedSprite spriteSet;

	private CauldronSteamParticle(ClientWorld world, ColorParticleData particleData, double x, double y, double z, double ySpeed, IAnimatedSprite spriteSet) {
		super(world, x, y, z, 0, 0, 0);
		particleRed = particleData.getRed();
		particleGreen = particleData.getGreen();
		particleBlue = particleData.getBlue();
		motionX *= 0.1F;
		motionY *= 0.1F;
		motionZ *= 0.1F;
		motionY += ySpeed;
		this.spriteSet = spriteSet;
		maxAge = 8 + world.rand.nextInt(32);
	}

	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if (age++ >= maxAge) {
			setExpired();
		} else {
			particleAlpha = (float) (maxAge - age) / maxAge;
			selectSpriteWithAge(spriteSet);
			move(motionX, motionY, motionZ);
			if (posY == prevPosY) {
				motionX *= 1.1D;
				motionZ *= 1.1D;
			}

			motionX *= 0.96D;
			motionY *= 0.96D;
			motionZ *= 0.96D;
			if (onGround) {
				motionX *= 0.7D;
				motionZ *= 0.7D;
			}
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<SteamColorParticleData> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Nullable
		@Override
		public Particle makeParticle(SteamColorParticleData particleData, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CauldronSteamParticle particle = new CauldronSteamParticle(world, particleData, x, y, z, ySpeed, spriteSet);
			particle.setSprite(spriteSet.get(particle.age, particle.maxAge));
			return particle;
		}
	}
}
