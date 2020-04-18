package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnPhoenixDownParticlesPacket {
	static void encode(SpawnPhoenixDownParticlesPacket msg, PacketBuffer packetBuffer) {
		//noop
	}

	static SpawnPhoenixDownParticlesPacket decode(PacketBuffer packetBuffer) {
		return new SpawnPhoenixDownParticlesPacket();
	}

	static void onMessage(SpawnPhoenixDownParticlesPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(SpawnPhoenixDownParticlesPacket msg) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		for (int particles = 0; particles <= 400; particles++) {
			player.world.addParticle(ParticleTypes.FLAME, player.posX, player.posY, player.posZ, player.world.rand.nextGaussian() * 8, player.world.rand.nextGaussian() * 8, player.world.rand.nextGaussian() * 8);
		}
	}
}