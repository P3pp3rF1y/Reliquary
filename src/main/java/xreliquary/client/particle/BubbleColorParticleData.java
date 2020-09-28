package xreliquary.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleType;
import xreliquary.client.init.ModParticles;

public class BubbleColorParticleData extends ColorParticleData {
	public static final Codec<BubbleColorParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("r").forGetter(ColorParticleData::getRed),
			Codec.FLOAT.fieldOf("g").forGetter(ColorParticleData::getGreen),
			Codec.FLOAT.fieldOf("b").forGetter(ColorParticleData::getBlue)
	).apply(instance, BubbleColorParticleData::new));

	public BubbleColorParticleData(float red, float green, float blue) {
		super(red, green, blue);
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticles.CAULDRON_BUBBLE;
	}

	public static final IDeserializer<BubbleColorParticleData> DESERIALIZER = new IDeserializer<BubbleColorParticleData>() {
		@Override
		public BubbleColorParticleData deserialize(ParticleType<BubbleColorParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return DeserializationHelper.deserialize(BubbleColorParticleData::new, reader);
		}

		@Override
		public BubbleColorParticleData read(ParticleType<BubbleColorParticleData> particleTypeIn, PacketBuffer buffer) {
			return DeserializationHelper.read(BubbleColorParticleData::new, buffer);
		}
	};
}
