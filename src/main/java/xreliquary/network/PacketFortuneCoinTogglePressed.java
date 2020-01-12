package xreliquary.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.init.ModItems;

import java.util.function.Supplier;

public class PacketFortuneCoinTogglePressed {

	private InventoryType inventoryType;
	private int slot;

	public PacketFortuneCoinTogglePressed() {}

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
		contextSupplier.get().enqueueWork(() -> handleMessage(msg, contextSupplier.get().getSender()));
	}

	private static void handleMessage(PacketFortuneCoinTogglePressed message, ServerPlayerEntity player) {
		ItemStack stack = ItemStack.EMPTY;
		switch (message.inventoryType) {
			case MAIN:
				stack = player.inventory.mainInventory.get(message.slot);
				break;
			case OFF_HAND:
				stack = player.inventory.offHandInventory.get(0);
				break;
/*	TODO implement code for Baubles successor
			case BAUBLES:
				if(ModList.get().isLoaded(Compatibility.MOD_ID.BAUBLES)) {
					IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);
					stack = inventoryBaubles.getStackInSlot(message.slot);
				}
				break;*/
		}
		if (stack.getItem() == ModItems.FORTUNE_COIN) {
			ModItems.FORTUNE_COIN.toggle(stack);
		}
	}

	public enum InventoryType {
		MAIN,
		OFF_HAND,
		BAUBLES
	}
}
