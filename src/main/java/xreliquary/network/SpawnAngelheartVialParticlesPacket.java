package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.init.ModItems;

import java.util.Random;
import java.util.function.Supplier;

public class SpawnAngelheartVialParticlesPacket {
	static void encode(SpawnAngelheartVialParticlesPacket msg, PacketBuffer packetBuffer) {
		//noop
	}

	static SpawnAngelheartVialParticlesPacket decode(PacketBuffer packetBuffer) {
		return new SpawnAngelheartVialParticlesPacket();
	}

	static void onMessage(SpawnAngelheartVialParticlesPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(SpawnAngelheartVialParticlesPacket msg) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		double var8 = player.posX;
		double var10 = player.posY;
		double var12 = player.posZ;
		Random var7 = player.world.rand;
		ItemParticleData itemParticleData = new ItemParticleData(ParticleTypes.ITEM, new ItemStack(ModItems.ANGELHEART_VIAL));
		for (int var15 = 0; var15 < 8; ++var15) {
			player.world.addParticle(itemParticleData, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
		}

		// purple, for reals.
		float red = 1.0F;
		float green = 0.0F;
		float blue = 1.0F;

		for (int var20 = 0; var20 < 100; ++var20) {
			double var39 = var7.nextDouble() * 4.0D;
			double var23 = var7.nextDouble() * Math.PI * 2.0D;
			double var25 = Math.cos(var23) * var39;
			double var27 = 0.01D + var7.nextDouble() * 0.5D;
			double var29 = Math.sin(var23) * var39;
			if (player.world.isRemote) {
				Particle var31 = Minecraft.getInstance().particles.addParticle(ParticleTypes.EFFECT, var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
				if (var31 != null) {
					float var32 = 0.75F + var7.nextFloat() * 0.25F;
					var31.setColor(red * var32, green * var32, blue * var32);
					var31.multiplyVelocity((float) var39);
				}
			}
		}
	}
}