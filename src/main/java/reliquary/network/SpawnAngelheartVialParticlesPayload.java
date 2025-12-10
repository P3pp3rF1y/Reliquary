package reliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import reliquary.Reliquary;
import reliquary.init.ModItems;
import reliquary.util.StreamCodecHelper;

public record SpawnAngelheartVialParticlesPayload(Vec3 position) implements CustomPacketPayload {
	public static final Type<SpawnAngelheartVialParticlesPayload> TYPE = new Type<>(Reliquary.getRL("angelheart_vial_particles"));
	public static final StreamCodec<FriendlyByteBuf, SpawnAngelheartVialParticlesPayload> STREAM_CODEC = StreamCodec.composite(
			StreamCodecHelper.VEC_3_STREAM_CODEC,
			SpawnAngelheartVialParticlesPayload::position,
			SpawnAngelheartVialParticlesPayload::new);

	public static void handlePayload(SpawnAngelheartVialParticlesPayload payload) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		double x = payload.position.x;
		double y = payload.position.y;
		double z = payload.position.z;
		RandomSource random = player.level().random;
		ItemParticleOption itemParticleData = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.ANGELHEART_VIAL.get()));
		for (int i = 0; i < 8; ++i) {
			player.level().addParticle(itemParticleData, x, y, z, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
		}

		float red = 1.0F;
		float green = 0.0F;
		float blue = 1.0F;

		for (int i = 0; i < 100; ++i) {
			double distance = random.nextDouble() * 4.0D;
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double xSpeed = Math.cos(angle) * distance;
			double ySpeed = 0.01D + random.nextDouble() * 0.5D;
			double zSpeed = Math.sin(angle) * distance;
			float colorMultiplier = 0.75F + random.nextFloat() * 0.25F;
			Minecraft.getInstance().particleEngine.createParticle(SpellParticleOption.create(ParticleTypes.EFFECT, red * colorMultiplier, green * colorMultiplier, blue * colorMultiplier, (float) distance), x + xSpeed * 0.1D, y + 0.3D, z + zSpeed * 0.1D, xSpeed, ySpeed, zSpeed);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
