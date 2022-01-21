package reliquary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CauldronSteamParticle extends TextureSheetParticle {
	private final SpriteSet spriteSet;

	private CauldronSteamParticle(ClientLevel world, ColorParticleData particleData, double x, double y, double z, double ySpeed, SpriteSet spriteSet) {
		super(world, x, y, z, 0, 0, 0);
		rCol = particleData.getRed();
		gCol = particleData.getGreen();
		bCol = particleData.getBlue();
		xd *= 0.1F;
		yd *= 0.1F;
		zd *= 0.1F;
		yd += ySpeed;
		this.spriteSet = spriteSet;
		lifetime = 8 + world.random.nextInt(32);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		if (age++ >= lifetime) {
			remove();
		} else {
			alpha = (float) (lifetime - age) / lifetime;
			setSpriteFromAge(spriteSet);
			move(xd, yd, zd);
			if (y == yo) {
				xd *= 1.1D;
				zd *= 1.1D;
			}

			xd *= 0.96D;
			yd *= 0.96D;
			zd *= 0.96D;
			if (onGround) {
				xd *= 0.7D;
				zd *= 0.7D;
			}
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<SteamColorParticleData> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Nullable
		@Override
		public Particle createParticle(SteamColorParticleData particleData, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CauldronSteamParticle particle = new CauldronSteamParticle(level, particleData, x, y, z, ySpeed, spriteSet);
			particle.setSprite(spriteSet.get(particle.age, particle.lifetime));
			return particle;
		}
	}
}
