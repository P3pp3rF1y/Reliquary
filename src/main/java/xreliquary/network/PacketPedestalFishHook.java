package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.api.IPedestal;
import xreliquary.client.render.PedestalFishHookRenderer;
import xreliquary.util.WorldHelper;

import java.util.function.Supplier;

public class PacketPedestalFishHook {

	private BlockPos pedestalPos;
	private double hookX;
	private double hookY;
	private double hookZ;

	public PacketPedestalFishHook(BlockPos pedestalPos, double hookX, double hookY, double hookZ) {

		this.pedestalPos = pedestalPos;
		this.hookX = hookX;
		this.hookY = hookY;
		this.hookZ = hookZ;
	}

	static void encode(PacketPedestalFishHook msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.pedestalPos.getX());
		packetBuffer.writeInt(msg.pedestalPos.getY());
		packetBuffer.writeInt(msg.pedestalPos.getZ());
		packetBuffer.writeDouble(msg.hookX);
		packetBuffer.writeDouble(msg.hookY);
		packetBuffer.writeDouble(msg.hookZ);
	}

	static PacketPedestalFishHook decode(PacketBuffer packetBuffer) {
		return new PacketPedestalFishHook(new BlockPos(packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readInt()),
				packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readDouble());
	}

	static void onMessage(PacketPedestalFishHook msg, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> handleMessage(msg));
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketPedestalFishHook message) {
		ClientWorld world = Minecraft.getInstance().world;
		WorldHelper.getTile(world, message.pedestalPos, IPedestal.class).ifPresent(pedestal -> {
			PedestalFishHookRenderer.HookRenderingData data = null;
			if (message.hookY > 0) {
				data = new PedestalFishHookRenderer.HookRenderingData(message.hookX, message.hookY, message.hookZ);
			}

			pedestal.setItemData(data);
		});
	}
}
