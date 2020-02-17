package xreliquary.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerDeathHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.EntityHelper;
import xreliquary.util.InventoryHelper;

import java.util.Random;

public class AngelheartVialItem extends ItemBase {
	public AngelheartVialItem() {
		super("angelheart_vial", new Properties());

		CommonEventHandler.registerPlayerDeathHandler(new IPlayerDeathHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingDeathEvent event) {
				return InventoryHelper.playerHasItem(player, ModItems.ANGELHEART_VIAL);
			}

			@Override
			public boolean apply(PlayerEntity player, LivingDeathEvent event) {
				decreaseAngelHeartByOne(player);

				// player should see a vial "shatter" effect and hear the glass break to
				// let them know they lost a vial.
				spawnAngelheartVialParticles(player);

				// play some glass breaking effects at the player location
				player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);

				// gives the player a few hearts, sparing them from death.
				float amountHealed = player.getMaxHealth() * (float) Settings.COMMON.items.angelHeartVial.healPercentageOfMaxLife.get() / 100F;
				player.setHealth(amountHealed);

				// if the player had any negative status effects [vanilla only for now], remove them:
				if (Boolean.TRUE.equals(Settings.COMMON.items.angelHeartVial.removeNegativeStatus.get())) {
					EntityHelper.removeNegativeStatusEffects(player);
				}

				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.LOW;
			}
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(ModItems.EMPTY_POTION_VIAL);
	}

	// returns an empty vial when used in crafting recipes.
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	private static void spawnAngelheartVialParticles(PlayerEntity player) {
		double var8 = player.posX;
		double var10 = player.posY;
		double var12 = player.posZ;
		Random var7 = player.world.rand;
		ItemParticleData itemParticleData = new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.POTION));
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

		player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);
	}

	private static void decreaseAngelHeartByOne(PlayerEntity player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}
			if (player.inventory.mainInventory.get(slot).getItem() == ModItems.ANGELHEART_VIAL) {
				player.inventory.decrStackSize(slot, 1);
				return;
			}
		}
	}
}
