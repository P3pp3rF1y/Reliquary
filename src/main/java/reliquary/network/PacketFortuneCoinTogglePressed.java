package reliquary.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import reliquary.compat.curios.CuriosCompat;
import reliquary.init.ModItems;
import reliquary.items.FortuneCoinItem;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketFortuneCoinTogglePressed {

	private final InventoryType inventoryType;
	private final int slot;
	private final String identifier;

	public PacketFortuneCoinTogglePressed(InventoryType inventoryType, int slot) {
		this(inventoryType, "", slot);
	}
	public PacketFortuneCoinTogglePressed(InventoryType inventoryType, String identifier, int slot) {
		this.inventoryType = inventoryType;
		this.identifier = identifier;
		this.slot = slot;
	}

	static void encode(PacketFortuneCoinTogglePressed msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeByte(msg.inventoryType.ordinal());
		packetBuffer.writeUtf(msg.identifier);
		packetBuffer.writeInt(msg.slot);
	}

	static PacketFortuneCoinTogglePressed decode(FriendlyByteBuf packetBuffer) {
		return new PacketFortuneCoinTogglePressed(InventoryType.values()[packetBuffer.readByte()], packetBuffer.readUtf(), packetBuffer.readInt());
	}

	static void onMessage(PacketFortuneCoinTogglePressed msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg, context.getSender()));
		context.setPacketHandled(true);
	}

	private static void handleMessage(PacketFortuneCoinTogglePressed message, @Nullable ServerPlayer player) {
		if (player == null) {
			return;
		}
		switch (message.inventoryType) {
			case MAIN -> {
				ItemStack stack2 = player.getInventory().items.get(message.slot);
				if (stack2.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack2);
					showMessage(player, stack2);
				}
			}
			case OFF_HAND -> {
				ItemStack stack1 = player.getInventory().offhand.get(0);
				if (stack1.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack1);
					showMessage(player, stack1);
				}
			}
			case CURIOS -> run(() -> () -> CuriosCompat.getStackInSlot(player, message.identifier, message.slot)
					.ifPresent(stack -> {
						if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
							ModItems.FORTUNE_COIN.get().toggle(stack);
							showMessage(player, stack);
							CuriosCompat.setStackInSlot(player, message.identifier, message.slot, stack);
						}
					}));
		}
	}

	private static void showMessage(ServerPlayer player, ItemStack fortuneCoin) {
		player.displayClientMessage(Component.translatable("chat.reliquary.fortune_coin.toggle",
						FortuneCoinItem.isEnabled(fortuneCoin) ?
								Component.translatable("chat.reliquary.fortune_coin.on").withStyle(ChatFormatting.GREEN)
								: Component.translatable("chat.reliquary.fortune_coin.off").withStyle(ChatFormatting.RED))
				, true);
	}

	private static void run(Supplier<Runnable> toRun) {
		toRun.get().run();
	}

	public enum InventoryType {
		MAIN,
		OFF_HAND,
		CURIOS
	}
}
