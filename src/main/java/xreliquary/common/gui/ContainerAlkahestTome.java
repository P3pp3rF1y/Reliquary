package xreliquary.common.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ContainerAlkahestTome extends Container {
	@ObjectHolder(Reference.MOD_ID + ":" + Names.Items.ALKAHESTRY_TOME)
	public static ContainerType<ContainerAlkahestTome> TYPE;

	public ContainerAlkahestTome(int windowId) {
		super(TYPE, windowId);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public static ContainerAlkahestTome fromBuffer(int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
		return new ContainerAlkahestTome(windowId);
	}
}
