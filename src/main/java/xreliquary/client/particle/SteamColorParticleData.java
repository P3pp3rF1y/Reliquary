package xreliquary.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleType;
import xreliquary.client.init.ModParticles;

public class SteamColorParticleData extends ColorParticleData {
	public static final Codec<SteamColorParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("r").forGetter(ColorParticleData::getRed),
			Codec.FLOAT.fieldOf("g").forGetter(ColorParticleData::getGreen),
			Codec.FLOAT.fieldOf("b").forGetter(ColorParticleData::getBlue)
	).apply(instance, SteamColorParticleData::new));

	public SteamColorParticleData(float red, float green, float blue) {
		super(red, green, blue);
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticles.CAULDRON_STEAM;
	}

	public static final IDeserializer<SteamColorParticleData> DESERIALIZER = new IDeserializer<SteamColorParticleData>() {
		@Override
		public SteamColorParticleData deserialize(ParticleType<SteamColorParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return DeserializationHelper.deserialize(SteamColorParticleData::new, reader);
		}

		@Override
		public SteamColorParticleData read(ParticleType<SteamColorParticleData> particleTypeIn, PacketBuffer buffer) {
			return DeserializationHelper.read(SteamColorParticleData::new, buffer);
		}
	};
}
