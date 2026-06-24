package reliquary.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import reliquary.Reliquary;
import reliquary.compat.curios.CuriosCompat;
import reliquary.init.ModItems;
import reliquary.item.FortuneCoinItem;

import java.util.function.Supplier;

public record FortuneCoinTogglePressedPayload(InventoryType inventoryType, int slot, String identifier) implements CustomPacketPayload {
	public static final Type<FortuneCoinTogglePressedPayload> TYPE = new Type<>(Reliquary.getRL("fortune_coin_toggle_pressed"));
	public static final StreamCodec<FriendlyByteBuf, FortuneCoinTogglePressedPayload> STREAM_CODEC = StreamCodec.composite(InventoryType.STREAM_CODEC,
			FortuneCoinTogglePressedPayload::inventoryType, ByteBufCodecs.INT, FortuneCoinTogglePressedPayload::slot, ByteBufCodecs.STRING_UTF8,
			FortuneCoinTogglePressedPayload::identifier, FortuneCoinTogglePressedPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public FortuneCoinTogglePressedPayload(InventoryType inventoryType, int slot) {
		this(inventoryType, slot, "");
	}

	public static void handlePayload(FortuneCoinTogglePressedPayload payload, IPayloadContext context) {
		Player player = context.player();
		switch (payload.inventoryType) {
			case MAIN -> {
				ItemStack stack = player.getInventory().getNonEquipmentItems().get(payload.slot);
				if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack);
					showMessage(player, stack);
				}
			}
			case OFF_HAND -> {
				ItemStack stack = player.getOffhandItem();
				if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack);
					showMessage(player, stack);
				}
			}
			case CURIOS -> run(() -> () -> CuriosCompat.getStackInSlot(player, payload.identifier, payload.slot).ifPresent(stack -> {
				if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack);
					showMessage(player, stack);
					CuriosCompat.setStackInSlot(player, payload.identifier, payload.slot, stack);
				}
			}));
		}
	}

	private static void showMessage(Player player, ItemStack fortuneCoin) {
		player.displayClientMessage(Component.translatable("chat.reliquary.fortune_coin.toggle",
				FortuneCoinItem.isEnabled(fortuneCoin)
						? Component.translatable("chat.reliquary.fortune_coin.on").withStyle(ChatFormatting.GREEN)
						: Component.translatable("chat.reliquary.fortune_coin.off").withStyle(ChatFormatting.RED)),
				true);
	}

	private static void run(Supplier<Runnable> toRun) {
		toRun.get().run();
	}

	public enum InventoryType {
		MAIN, OFF_HAND, CURIOS;
		public static final StreamCodec<FriendlyByteBuf, InventoryType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(InventoryType.class);
	}
}
