package xreliquary.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class CauldronBubbleParticleType extends ParticleType<BubbleColorParticleData> {
	public CauldronBubbleParticleType() {
		super(false, BubbleColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<BubbleColorParticleData> codec() {
		return BubbleColorParticleData.CODEC;
	}
}
