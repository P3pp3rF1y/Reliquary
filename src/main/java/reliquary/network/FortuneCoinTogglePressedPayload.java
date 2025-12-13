package reliquary.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import reliquary.Reliquary;
import reliquary.init.ModItems;
import reliquary.item.FortuneCoinItem;
import reliquary.util.PlayerInventoryProvider;

public record FortuneCoinTogglePressedPayload(String handlerName, String identifier, int slot) implements CustomPacketPayload {
	public static final Type<FortuneCoinTogglePressedPayload> TYPE = new Type<>(Reliquary.getRL("fortune_coin_toggle_pressed"));
	public static final StreamCodec<FriendlyByteBuf, FortuneCoinTogglePressedPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			FortuneCoinTogglePressedPayload::handlerName,
			ByteBufCodecs.STRING_UTF8,
			FortuneCoinTogglePressedPayload::identifier,
			ByteBufCodecs.INT,
			FortuneCoinTogglePressedPayload::slot,
			FortuneCoinTogglePressedPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handlePayload(FortuneCoinTogglePressedPayload payload, IPayloadContext context) {
		Player player = context.player();


		ItemStack stack = PlayerInventoryProvider.get().getStack(player, payload.handlerName, payload.identifier, payload.slot);
		if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
			ModItems.FORTUNE_COIN.get().toggle(stack);
			showMessage(player, stack);
			PlayerInventoryProvider.get().setStack(player, payload.handlerName, payload.identifier, payload.slot, stack);
		}
	}

	private static void showMessage(Player player, ItemStack fortuneCoin) {
		player.displayClientMessage(Component.translatable("chat.reliquary.fortune_coin.toggle",
						FortuneCoinItem.isEnabled(fortuneCoin) ?
								Component.translatable("chat.reliquary.fortune_coin.on").withStyle(ChatFormatting.GREEN)
								: Component.translatable("chat.reliquary.fortune_coin.off").withStyle(ChatFormatting.RED))
				, true);
	}
}
