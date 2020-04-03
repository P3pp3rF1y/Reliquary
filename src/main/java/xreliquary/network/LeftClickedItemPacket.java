package xreliquary.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.items.util.ILeftClickableItem;

import java.util.function.Supplier;

public class LeftClickedItemPacket {
	public LeftClickedItemPacket() {
	}

	static void encode(LeftClickedItemPacket msg, PacketBuffer packetBuffer) {
	}

	static LeftClickedItemPacket decode(PacketBuffer packetBuffer) {
		return new LeftClickedItemPacket();
	}

	static void onMessage(LeftClickedItemPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		//noinspection ConstantConditions - always runs on server where sender is available
		context.enqueueWork(() -> handleMessage(msg, contextSupplier.get().getSender()));
		context.setPacketHandled(true);
	}

	private static void handleMessage(LeftClickedItemPacket msg, ServerPlayerEntity sender) {
		ItemStack stack = sender.getHeldItemMainhand();

		if (stack.getItem() instanceof ILeftClickableItem) {
			((ILeftClickableItem) stack.getItem()).onLeftClickItem(stack, sender);
		}
	}
}
