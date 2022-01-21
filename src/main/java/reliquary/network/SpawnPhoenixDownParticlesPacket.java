package reliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnPhoenixDownParticlesPacket {
	public static final SpawnPhoenixDownParticlesPacket INSTANCE = new SpawnPhoenixDownParticlesPacket();

	private SpawnPhoenixDownParticlesPacket() {}

	static void encode(SpawnPhoenixDownParticlesPacket msg, FriendlyByteBuf packetBuffer) {
		//noop
	}

	static SpawnPhoenixDownParticlesPacket decode() {
		return SpawnPhoenixDownParticlesPacket.INSTANCE;
	}

	static void onMessage(SpawnPhoenixDownParticlesPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@SuppressWarnings("ConstantConditions") // the player isn't null when particles are spawned
	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(SpawnPhoenixDownParticlesPacket msg) {
		LocalPlayer player = Minecraft.getInstance().player;
		for (int particles = 0; particles <= 400; particles++) {
			player.level.addParticle(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), player.level.random.nextGaussian() * 8, player.level.random.nextGaussian() * 8, player.level.random.nextGaussian() * 8);
		}
	}
}
