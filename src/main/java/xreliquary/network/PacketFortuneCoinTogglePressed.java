package xreliquary.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import xreliquary.compat.curios.CuriosCompat;
import xreliquary.init.ModItems;
import xreliquary.items.FortuneCoinItem;
import xreliquary.items.util.ICuriosItem;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketFortuneCoinTogglePressed {

	private final InventoryType inventoryType;
	private final int slot;

	public PacketFortuneCoinTogglePressed(InventoryType inventoryType, int slot) {
		this.inventoryType = inventoryType;
		this.slot = slot;
	}

	static void encode(PacketFortuneCoinTogglePressed msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeByte(msg.inventoryType.ordinal());
		packetBuffer.writeInt(msg.slot);

	}

	static PacketFortuneCoinTogglePressed decode(FriendlyByteBuf packetBuffer) {
		return new PacketFortuneCoinTogglePressed(InventoryType.values()[packetBuffer.readByte()], packetBuffer.readInt());
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
			case CURIOS -> run(() -> () -> CuriosCompat.getStackInSlot(player, ICuriosItem.Type.NECKLACE.getIdentifier(), message.slot)
					.ifPresent(stack -> {
						if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
							ModItems.FORTUNE_COIN.get().toggle(stack);
							showMessage(player, stack);
							CuriosCompat.setStackInSlot(player, ICuriosItem.Type.NECKLACE.getIdentifier(), message.slot, stack);
						}
					}));
		}
	}

	private static void showMessage(ServerPlayer player, ItemStack fortuneCoin) {
		player.displayClientMessage(new TranslatableComponent("chat.xreliquary.fortune_coin.toggle",
						FortuneCoinItem.isEnabled(fortuneCoin) ?
								new TranslatableComponent("chat.xreliquary.fortune_coin.on").withStyle(ChatFormatting.GREEN)
								: new TranslatableComponent("chat.xreliquary.fortune_coin.off").withStyle(ChatFormatting.RED))
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
