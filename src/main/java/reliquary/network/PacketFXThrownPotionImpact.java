package reliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class PacketFXThrownPotionImpact {
	private final int color;
	private final double posX;
	private final double posY;
	private final double posZ;

	public PacketFXThrownPotionImpact(int color, double x, double y, double z) {
		this.color = color;
		posX = x;
		posY = y;
		posZ = z;
	}

	static void encode(PacketFXThrownPotionImpact msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeInt(msg.color);
		packetBuffer.writeDouble(msg.posX);
		packetBuffer.writeDouble(msg.posY);
		packetBuffer.writeDouble(msg.posZ);
	}

	static PacketFXThrownPotionImpact decode(FriendlyByteBuf packetBuffer) {
		return new PacketFXThrownPotionImpact(packetBuffer.readInt(), packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readDouble());
	}

	static void onMessage(PacketFXThrownPotionImpact msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketFXThrownPotionImpact message) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		int color = message.color;
		Random rand = mc.level.random;

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = ((color & 255) / 256F);

		for (int i = 0; i < 100; ++i) {
			double var39 = rand.nextDouble() * 4.0D;
			double angle = rand.nextDouble() * Math.PI * 2.0D;
			double xSpeed = Math.cos(angle) * var39;
			double ySpeed = 0.01D + rand.nextDouble() * 0.5D;
			double zSpeed = Math.sin(angle) * var39;

			Particle particle = mc.particleEngine.createParticle(ParticleTypes.EFFECT, message.posX + xSpeed * 0.1D, message.posY + 0.3D, message.posZ + zSpeed * 0.1D, xSpeed, ySpeed, zSpeed);
			if (particle != null) {
				float var32 = 0.75F + rand.nextFloat() * 0.25F;
				particle.setColor(red * var32, green * var32, blue * var32);
				particle.setPower((float) var39);
			}
		}
	}
}
