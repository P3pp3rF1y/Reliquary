package xreliquary.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class CauldronSteamParticleType extends ParticleType<SteamColorParticleData> {
	public CauldronSteamParticleType() {
		super(false, SteamColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<SteamColorParticleData> codec() {
		return SteamColorParticleData.CODEC;
	}
}
