package reliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import reliquary.api.IPedestal;
import reliquary.client.render.PedestalFishHookRenderer;
import reliquary.util.WorldHelper;

import java.util.function.Supplier;

public class PacketPedestalFishHook {

	private final BlockPos pedestalPos;
	private final double hookX;
	private final double hookY;
	private final double hookZ;

	public PacketPedestalFishHook(BlockPos pedestalPos, double hookX, double hookY, double hookZ) {

		this.pedestalPos = pedestalPos;
		this.hookX = hookX;
		this.hookY = hookY;
		this.hookZ = hookZ;
	}

	static void encode(PacketPedestalFishHook msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeInt(msg.pedestalPos.getX());
		packetBuffer.writeInt(msg.pedestalPos.getY());
		packetBuffer.writeInt(msg.pedestalPos.getZ());
		packetBuffer.writeDouble(msg.hookX);
		packetBuffer.writeDouble(msg.hookY);
		packetBuffer.writeDouble(msg.hookZ);
	}

	static PacketPedestalFishHook decode(FriendlyByteBuf packetBuffer) {
		return new PacketPedestalFishHook(new BlockPos(packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readInt()),
				packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readDouble());
	}

	static void onMessage(PacketPedestalFishHook msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketPedestalFishHook message) {
		ClientLevel world = Minecraft.getInstance().level;
		WorldHelper.getBlockEntity(world, message.pedestalPos, IPedestal.class).ifPresent(pedestal -> {
			PedestalFishHookRenderer.HookRenderingData data = null;
			if (message.hookY > 0) {
				data = new PedestalFishHookRenderer.HookRenderingData(message.hookX, message.hookY, message.hookZ);
			}

			pedestal.setItemData(data);
		});
	}
}
