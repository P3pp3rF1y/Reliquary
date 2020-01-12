package xreliquary.entities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class FakeNetHandlerPlayServer extends ServerPlayNetHandler {
	public FakeNetHandlerPlayServer(ServerPlayerEntity player) {
		super(ServerLifecycleHooks.getCurrentServer(), new NetworkManager(PacketDirection.CLIENTBOUND), player);
	}
	@Override
	public NetworkManager getNetworkManager() {
		return null;
	}

	@Override
	public void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket packet) {
		//noop
	}

	@Override
	public void processTryUseItem(CPlayerTryUseItemPacket packet) {
		//noop
	}

	@Override
	public void processInput(CInputPacket packet) {
		//noop
	}

	@Override
	public void processPlayer(CPlayerPacket packet) {
		//noop
	}

	@Override
	public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
		//noop
	}

	@Override
	public void processPlayerDigging(CPlayerDiggingPacket packet) {
		//noop
	}

	@Override
	public void onDisconnect(ITextComponent reason) {
		//noop
	}

	@Override
	public void sendPacket(IPacket<?> packet) {
		//noop
	}

	@Override
	public void processHeldItemChange(CHeldItemChangePacket packet) {
		//noop
	}

	@Override
	public void processChatMessage(CChatMessagePacket packet) {
		//noop
	}

	@Override
	public void handleAnimation(CAnimateHandPacket packet) {
		//noop
	}

	@Override
	public void processEntityAction(CEntityActionPacket packet) {
		//noop
	}

	@Override
	public void processUseEntity(CUseEntityPacket packet) {
		//noop
	}

	@Override
	public void processClientStatus(CClientStatusPacket packet) {
		//noop
	}

	@Override
	public void processCloseWindow(CCloseWindowPacket packet) {
		//noop
	}

	@Override
	public void processClickWindow(CClickWindowPacket packet) {
		//noop
	}

	@Override
	public void processEnchantItem(CEnchantItemPacket packet) {
		//noop
	}

	@Override
	public void processCreativeInventoryAction(CCreativeInventoryActionPacket packet) {
		//noop
	}

	@Override
	public void processConfirmTransaction(CConfirmTransactionPacket packet) {
		//noop
	}

	@Override
	public void processUpdateSign(CUpdateSignPacket packet) {
		//noop
	}

	@Override
	public void processKeepAlive(CKeepAlivePacket packet) {
		//noop
	}

	@Override
	public void processPlayerAbilities(CPlayerAbilitiesPacket packet) {
		//noop
	}

	@Override
	public void processTabComplete(CTabCompletePacket packet) {
		//noop
	}

	@Override
	public void processClientSettings(CClientSettingsPacket packet) {
		//noop
	}

	@Override
	public void handleSpectate(CSpectatePacket packet) {
		//noop
	}

	@Override
	public void handleResourcePackStatus(CResourcePackStatusPacket packet) {
		//noop
	}
}
