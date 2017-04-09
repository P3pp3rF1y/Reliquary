package xreliquary.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;

public class PacketFortuneCoinTogglePressed implements IMessage, IMessageHandler<PacketFortuneCoinTogglePressed, IMessage> {

	private InventoryType inventoryType;
	private int slot;

	public PacketFortuneCoinTogglePressed() {}

	public PacketFortuneCoinTogglePressed(InventoryType inventoryType, int slot) {
		this.inventoryType = inventoryType;
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		inventoryType = InventoryType.values()[buf.readByte()];
		slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(inventoryType.ordinal());
		buf.writeInt(slot);
	}

	@Override
	public IMessage onMessage(PacketFortuneCoinTogglePressed message, MessageContext ctx) {
		((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(() -> handleMessage(message, ctx.getServerHandler().player));

		return null;
	}

	private void handleMessage(PacketFortuneCoinTogglePressed message, EntityPlayerMP player) {
		ItemStack stack = ItemStack.EMPTY;
		switch(message.inventoryType) {
			case MAIN:
				stack = player.inventory.mainInventory.get(message.slot);
				break;
			case OFF_HAND:
				stack = player.inventory.offHandInventory.get(0);
				break;
			case BAUBLES:
				if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
					IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);
					stack = inventoryBaubles.getStackInSlot(message.slot);
				}
				break;
		}
		if (stack.getItem() == ModItems.fortuneCoin)
			ModItems.fortuneCoin.toggle(stack);
	}

	public enum InventoryType {
		MAIN,
		OFF_HAND,
		BAUBLES
	}
}
