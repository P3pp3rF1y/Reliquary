package reliquary.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import reliquary.Reliquary;
import reliquary.item.util.IScrollableItem;

public record ScrolledItemPayload(double scrollDelta) implements CustomPacketPayload {
	public static final Type<ScrolledItemPayload> TYPE = new Type<>(Reliquary.getRL("scrolled_item"));
	public static final StreamCodec<FriendlyByteBuf, ScrolledItemPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE,
			ScrolledItemPayload::scrollDelta,
			ScrolledItemPayload::new);


	public static void handlePayload(ScrolledItemPayload payload, IPayloadContext context) {
		Player player = context.player();
		ItemStack stack = player.getMainHandItem();

		if (stack.getItem() instanceof IScrollableItem leftClickableItem) {
			leftClickableItem.onMouseScrolled(stack, player, payload.scrollDelta);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
