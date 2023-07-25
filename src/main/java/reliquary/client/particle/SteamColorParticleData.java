package reliquary.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import reliquary.client.init.ModParticles;

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
		return ModParticles.CAULDRON_STEAM.get();
	}

	@SuppressWarnings("deprecation")
	public static final Deserializer<SteamColorParticleData> DESERIALIZER = new Deserializer<>() {
		@Override
		public SteamColorParticleData fromCommand(ParticleType<SteamColorParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return DeserializationHelper.deserialize(SteamColorParticleData::new, reader);
		}

		@Override
		public SteamColorParticleData fromNetwork(ParticleType<SteamColorParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return DeserializationHelper.read(SteamColorParticleData::new, buffer);
		}
	};
}
