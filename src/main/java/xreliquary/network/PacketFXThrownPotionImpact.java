package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class PacketFXThrownPotionImpact {
	private int color;
	private double posX;
	private double posY;
	private double posZ;

	public PacketFXThrownPotionImpact(int color, double x, double y, double z) {
		this.color = color;
		posX = x;
		posY = y;
		posZ = z;
	}

	static void encode(PacketFXThrownPotionImpact msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.color);
		packetBuffer.writeDouble(msg.posX);
		packetBuffer.writeDouble(msg.posY);
		packetBuffer.writeDouble(msg.posZ);
	}

	static PacketFXThrownPotionImpact decode(PacketBuffer packetBuffer) {
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
		int color = message.color;
		Random rand = mc.world.rand;

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = ((color & 255) / 256F);

		for (int i = 0; i < 100; ++i) {
			double var39 = rand.nextDouble() * 4.0D;
			double angle = rand.nextDouble() * Math.PI * 2.0D;
			double xSpeed = Math.cos(angle) * var39;
			double ySpeed = 0.01D + rand.nextDouble() * 0.5D;
			double zSpeed = Math.sin(angle) * var39;

			Particle particle = mc.particles.addParticle(ParticleTypes.EFFECT, message.posX + xSpeed * 0.1D, message.posY + 0.3D, message.posZ + zSpeed * 0.1D, xSpeed, ySpeed, zSpeed);
			if (particle != null) {
				float var32 = 0.75F + rand.nextFloat() * 0.25F;
				particle.setColor(red * var32, green * var32, blue * var32);
				particle.multiplyVelocity((float) var39);
			}
		}
	}
}
