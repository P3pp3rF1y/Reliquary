package xreliquary.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import xreliquary.items.util.ILeftClickableItem;

import java.util.function.Supplier;

public class LeftClickedItemPacket {
	@SuppressWarnings("InstantiationOfUtilityClass")
	public static final LeftClickedItemPacket INSTANCE = new LeftClickedItemPacket();

	private LeftClickedItemPacket() {
		//noop
	}

	static void encode() {
		//noop
	}

	static LeftClickedItemPacket decode() {
		return LeftClickedItemPacket.INSTANCE;
	}

	static void onMessage(Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		//noinspection ConstantConditions - always runs on server where sender is available
		context.enqueueWork(() -> handleMessage(contextSupplier.get().getSender()));
		context.setPacketHandled(true);
	}

	private static void handleMessage(ServerPlayer sender) {
		ItemStack stack = sender.getMainHandItem();

		if (stack.getItem() instanceof ILeftClickableItem leftClickableItem) {
			leftClickableItem.onLeftClickItem(stack, sender);
		}
	}
}
