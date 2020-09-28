package xreliquary.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;

import java.util.Locale;

public abstract class ColorParticleData implements IParticleData {
	private final float red;
	private final float green;
	private final float blue;

	public ColorParticleData(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
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

	public interface IColorParticleDataInitializer<T extends ColorParticleData> {
		T initialize(float red, float green, float blue);
	}

	public static class DeserializationHelper {
		private DeserializationHelper() {}

		public static <T extends ColorParticleData> T deserialize(IColorParticleDataInitializer<T> initializer, StringReader stringReader)
				throws CommandSyntaxException {
			stringReader.expect(' ');
			float r = stringReader.readFloat();
			stringReader.expect(' ');
			float g = stringReader.readFloat();
			stringReader.expect(' ');
			float b = stringReader.readFloat();
			stringReader.expect(' ');
			return initializer.initialize(r, g, b);
		}

		public static <T extends ColorParticleData> T read(IColorParticleDataInitializer<T> initializer, PacketBuffer packetBuffer) {
			return initializer.initialize(packetBuffer.readFloat(), packetBuffer.readFloat(), packetBuffer.readFloat());
		}
	}

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
