package xreliquary.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import java.util.Locale;

public class ColorParticleData implements IParticleData {
	private ParticleType<ColorParticleData> particleType;
	private final float red;
	private final float green;
	private final float blue;

	public ColorParticleData(ParticleType<ColorParticleData> particleType, float red, float green, float blue) {
		this.particleType = particleType;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public ParticleType<?> getType() {
		return particleType;
	}

	@Override
	public void write(PacketBuffer packetBuffer) {
		packetBuffer.writeFloat(red);
		packetBuffer.writeFloat(green);
		packetBuffer.writeFloat(blue);
	}

	@Override
	public String getParameters() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", getType().getRegistryName(), red, green, blue);
	}

	public static final IDeserializer<ColorParticleData> DESERIALIZER = new IDeserializer<ColorParticleData>() {
		@Override
		public ColorParticleData deserialize(ParticleType<ColorParticleData> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float r = stringReader.readFloat();
			stringReader.expect(' ');
			float g = stringReader.readFloat();
			stringReader.expect(' ');
			float b = stringReader.readFloat();
			stringReader.expect(' ');
			return new ColorParticleData(particleType, r, g, b);
		}

		@Override
		public ColorParticleData read(ParticleType<ColorParticleData> particleType, PacketBuffer packetBuffer) {
			return new ColorParticleData(particleType, packetBuffer.readFloat(), packetBuffer.readFloat(), packetBuffer.readFloat());
		}
	};

	public float getRed() {
		return red;
	}

	public float getGreen() {
		return green;
	}

	public float getBlue() {
		return blue;
	}
}
