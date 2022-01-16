package xreliquary.entities;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.server.ServerLifecycleHooks;

public class FakeNetHandlerPlayServer extends ServerGamePacketListenerImpl {
	public FakeNetHandlerPlayServer(ServerPlayer player) {
		super(ServerLifecycleHooks.getCurrentServer(), new Connection(PacketFlow.CLIENTBOUND), player);
	}

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public void handleUseItemOn(ServerboundUseItemOnPacket packet) {
		//noop
	}

	@Override
	public void handleUseItem(ServerboundUseItemPacket packet) {
		//noop
	}

	@Override
	public void handlePlayerInput(ServerboundPlayerInputPacket packet) {
		//noop
	}

	@Override
	public void handleMovePlayer(ServerboundMovePlayerPacket packet) {
		//noop
	}

	@Override
	public void teleport(double x, double y, double z, float yaw, float pitch) {
		//noop
	}

	@Override
	public void handlePlayerAction(ServerboundPlayerActionPacket packet) {
		//noop
	}

	@Override
	public void onDisconnect(Component reason) {
		//noop
	}

	@Override
	public void send(Packet<?> packet) {
		//noop
	}

	@Override
	public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {
		//noop
	}

	@Override
	public void handleChat(ServerboundChatPacket packet) {
		//noop
	}

	@Override
	public void handleAnimate(ServerboundSwingPacket packet) {
		//noop
	}

	@Override
	public void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {
		//noop
	}

	@Override
	public void handleInteract(ServerboundInteractPacket packet) {
		//noop
	}

	@Override
	public void handleClientCommand(ServerboundClientCommandPacket packet) {
		//noop
	}

	@Override
	public void handleContainerClose(ServerboundContainerClosePacket packet) {
		//noop
	}

	@Override
	public void handleContainerClick(ServerboundContainerClickPacket packet) {
		//noop
	}

	@Override
	public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {
		//noop
	}

	@Override
	public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
		//noop
	}

	@Override
	public void handleSignUpdate(ServerboundSignUpdatePacket packet) {
		//noop
	}

	@Override
	public void handleKeepAlive(ServerboundKeepAlivePacket packet) {
		//noop
	}

	@Override
	public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {
		//noop
	}

	@Override
	public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {
		//noop
	}

	@Override
	public void handleClientInformation(ServerboundClientInformationPacket packet) {
		//noop
	}

	@Override
	public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {
		//noop
	}

	@Override
	public void handleResourcePackResponse(ServerboundResourcePackPacket packet) {
		//noop
	}
}
