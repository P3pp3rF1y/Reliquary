package xreliquary.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CauldronBubbleParticle extends SpriteTexturedParticle {
	private static ResourceLocation bubbleTexture = new ResourceLocation(Reference.MOD_ID + ":textures/particles/" + Names.Blocks.CAULDRON_BUBBLE + ".png");

	private final IAnimatedSprite spriteSet;

	public CauldronBubbleParticle(World world, ColorParticleData particleData, double x, double y, double z, IAnimatedSprite spriteSet) {
		super(world, x, y, z, 0D, 0D, 0D);
		this.spriteSet = spriteSet;
		setSize(0.02F, 0.02F);
		this.particleScale = 0.5F + (world.rand.nextFloat() - 0.5F) * 0.4F;
		this.motionX = 0D;
		this.motionY = 0D;
		this.motionZ = 0D;
		this.particleRed = particleData.getRed();
		this.particleGreen = particleData.getGreen();
		this.particleBlue = particleData.getBlue();
		this.maxAge = 20;
		this.age = 0;
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.age++ >= this.maxAge) {
			this.setExpired();
		} else {
			this.selectSpriteWithAge(spriteSet);
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
			return new CauldronBubbleParticle(world, particleData, x, y, z, spriteSet);
		}
	}
}
