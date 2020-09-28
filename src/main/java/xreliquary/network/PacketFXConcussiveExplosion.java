package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.entities.ConcussiveExplosion;

import java.util.function.Supplier;

public class PacketFXConcussiveExplosion {
	private final float size;
	private final Vector3d pos;

	public PacketFXConcussiveExplosion(float size, Vector3d pos) {
		this.size = size;
		this.pos = pos;
	}

	static void encode(PacketFXConcussiveExplosion msg, PacketBuffer packetBuffer) {
		packetBuffer.writeFloat(msg.size);
		packetBuffer.writeDouble(msg.pos.getX());
		packetBuffer.writeDouble(msg.pos.getY());
		packetBuffer.writeDouble(msg.pos.getZ());
	}

	static PacketFXConcussiveExplosion decode(PacketBuffer packetBuffer) {
		return new PacketFXConcussiveExplosion(packetBuffer.readFloat(), new Vector3d(packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readDouble()));
	}

	static void onMessage(PacketFXConcussiveExplosion msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketFXConcussiveExplosion message) {
		ConcussiveExplosion explosion = new ConcussiveExplosion(Minecraft.getInstance().world, null, null, message.pos, message.size, false);
		explosion.doExplosionB(false);
	}
}
