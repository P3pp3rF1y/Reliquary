package xreliquary.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;

public class CauldronBubbleParticleType extends ParticleType<BubbleColorParticleData> {
	public CauldronBubbleParticleType() {
		super(false, BubbleColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<BubbleColorParticleData> func_230522_e_() {
		return BubbleColorParticleData.CODEC;
	}
}
