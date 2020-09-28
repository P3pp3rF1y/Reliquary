package xreliquary.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;

public class CauldronSteamParticleType extends ParticleType<SteamColorParticleData> {
	public CauldronSteamParticleType() {
		super(false, SteamColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<SteamColorParticleData> func_230522_e_() {
		return SteamColorParticleData.CODEC;
	}
}
