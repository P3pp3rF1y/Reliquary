package reliquary.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import reliquary.items.util.IScrollableItem;

import java.util.function.Supplier;

public class ScrolledItemPacket {
	private final double scrollDelta;

	public ScrolledItemPacket(double scrollDelta) {
		this.scrollDelta = scrollDelta;
	}

	static void encode(ScrolledItemPacket msg, FriendlyByteBuf buffer) {
		buffer.writeDouble(msg.scrollDelta);
	}

	static ScrolledItemPacket decode(FriendlyByteBuf packetBuffer) {
		return new ScrolledItemPacket(packetBuffer.readDouble());
	}

	static void onMessage(ScrolledItemPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		//noinspection ConstantConditions - always runs on server where sender is available
		context.enqueueWork(() -> handleMessage(msg, contextSupplier.get().getSender()));
		context.setPacketHandled(true);
	}

	private static void handleMessage(ScrolledItemPacket msg, ServerPlayer sender) {
		ItemStack stack = sender.getMainHandItem();

		if (stack.getItem() instanceof IScrollableItem leftClickableItem) {
			leftClickableItem.onMouseScrolled(stack, sender, msg.scrollDelta);
		}
	}
}
