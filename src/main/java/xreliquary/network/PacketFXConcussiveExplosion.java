package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import xreliquary.entities.ConcussiveExplosion;

import java.util.function.Supplier;

public class PacketFXConcussiveExplosion {
	private final float size;
	private final Vec3 pos;

	public PacketFXConcussiveExplosion(float size, Vec3 pos) {
		this.size = size;
		this.pos = pos;
	}

	static void encode(PacketFXConcussiveExplosion msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeFloat(msg.size);
		packetBuffer.writeDouble(msg.pos.x());
		packetBuffer.writeDouble(msg.pos.y());
		packetBuffer.writeDouble(msg.pos.z());
	}

	static PacketFXConcussiveExplosion decode(FriendlyByteBuf packetBuffer) {
		return new PacketFXConcussiveExplosion(packetBuffer.readFloat(), new Vec3(packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readDouble()));
	}

	static void onMessage(PacketFXConcussiveExplosion msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketFXConcussiveExplosion message) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		ConcussiveExplosion explosion = new ConcussiveExplosion(level, null, null, message.pos, message.size, false);
		explosion.finalizeExplosion(false);
	}
}
