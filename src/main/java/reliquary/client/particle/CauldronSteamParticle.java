package reliquary.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class CauldronSteamParticle extends SingleQuadParticle {
	private final SpriteSet spriteSet;

	private CauldronSteamParticle(ClientLevel level, ColorParticleOption particleOption, double x, double y, double z, double ySpeed, SpriteSet spriteSet,
			TextureAtlasSprite sprite) {
		super(level, x, y, z, 0, 0, 0, sprite);
		rCol = particleOption.getRed();
		gCol = particleOption.getGreen();
		bCol = particleOption.getBlue();
		xd *= 0.1F;
		yd *= 0.1F;
		zd *= 0.1F;
		yd += ySpeed;
		this.spriteSet = spriteSet;
		lifetime = 8 + level.random.nextInt(32);
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
	protected Layer getLayer() {
		return Layer.OPAQUE;
	}

	public static class Provider implements ParticleProvider<ColorParticleOption> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Nullable
		@Override
		public Particle createParticle(ColorParticleOption particleOption, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
				double zSpeed, RandomSource randomSource) {
			CauldronSteamParticle particle = new CauldronSteamParticle(level, particleOption, x, y, z, ySpeed, spriteSet, spriteSet.get(randomSource));
			particle.setSprite(spriteSet.get(particle.age, particle.lifetime));
			return particle;
		}
	}
}
