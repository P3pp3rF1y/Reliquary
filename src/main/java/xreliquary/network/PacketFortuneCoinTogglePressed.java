package xreliquary.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.compat.curios.CuriosCompat;
import xreliquary.init.ModItems;
import xreliquary.items.util.IBaubleItem;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketFortuneCoinTogglePressed {

	private final InventoryType inventoryType;
	private final int slot;

	public PacketFortuneCoinTogglePressed(InventoryType inventoryType, int slot) {
		this.inventoryType = inventoryType;
		this.slot = slot;
	}

	static void encode(PacketFortuneCoinTogglePressed msg, PacketBuffer packetBuffer) {
		packetBuffer.writeByte(msg.inventoryType.ordinal());
		packetBuffer.writeInt(msg.slot);

	}

	static PacketFortuneCoinTogglePressed decode(PacketBuffer packetBuffer) {
		return new PacketFortuneCoinTogglePressed(InventoryType.values()[packetBuffer.readByte()], packetBuffer.readInt());
	}

	static void onMessage(PacketFortuneCoinTogglePressed msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg, context.getSender()));
		context.setPacketHandled(true);
	}

	private static void handleMessage(PacketFortuneCoinTogglePressed message, @Nullable ServerPlayerEntity player) {
		if (player == null) {
			return;
		}
		switch (message.inventoryType) {
			case MAIN:
				ItemStack stack2 = player.inventory.mainInventory.get(message.slot);
				if (stack2.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack2);
					showMessage(player, stack2);
				}
				break;
			case OFF_HAND:
				ItemStack stack1 = player.inventory.offHandInventory.get(0);
				if (stack1.getItem() == ModItems.FORTUNE_COIN.get()) {
					ModItems.FORTUNE_COIN.get().toggle(stack1);
					showMessage(player, stack1);
				}
				break;
			case CURIOS:
				run(() -> () -> CuriosCompat.getStackInSlot(player, IBaubleItem.Type.NECKLACE.getIdentifier(), message.slot)
						.ifPresent(stack -> {
							if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
								ModItems.FORTUNE_COIN.get().toggle(stack);
								showMessage(player, stack);
								CuriosCompat.setStackInSlot(player, IBaubleItem.Type.NECKLACE.getIdentifier(), message.slot, stack);
							}
						}));
				break;
		}
	}

	private static void showMessage(ServerPlayerEntity player, ItemStack fortuneCoin) {
		player.sendStatusMessage(new TranslationTextComponent("chat.xreliquary.fortune_coin.toggle",
						ModItems.FORTUNE_COIN.get().isEnabled(fortuneCoin) ?
								new TranslationTextComponent("chat.xreliquary.fortune_coin.on").mergeStyle(TextFormatting.GREEN)
								: new TranslationTextComponent("chat.xreliquary.fortune_coin.off").mergeStyle(TextFormatting.RED))
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
