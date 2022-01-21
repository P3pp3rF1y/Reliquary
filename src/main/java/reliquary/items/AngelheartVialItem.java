package reliquary.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerDeathHandler;
import reliquary.init.ModItems;
import reliquary.network.PacketHandler;
import reliquary.network.SpawnAngelheartVialParticlesPacket;
import reliquary.reference.Settings;
import reliquary.util.EntityHelper;
import reliquary.util.InventoryHelper;

public class AngelheartVialItem extends ItemBase {
	public AngelheartVialItem() {
		super(new Properties());

		CommonEventHandler.registerPlayerDeathHandler(new IPlayerDeathHandler() {
			@Override
			public boolean canApply(Player player, LivingDeathEvent event) {
				return InventoryHelper.playerHasItem(player, ModItems.ANGELHEART_VIAL.get());
			}

			@SuppressWarnings({"java:S2440", "InstantiationOfUtilityClass"}) //instantiating the packet for its type to be used as identifier for the packet
			@Override
			public boolean apply(Player player, LivingDeathEvent event) {
				decreaseAngelHeartByOne(player);

				// player should see a vial "shatter" effect and hear the glass break to
				// let them know they lost a vial.
				PacketHandler.sendToClient((ServerPlayer) player, new SpawnAngelheartVialParticlesPacket());

				// play some glass breaking effects at the player location
				player.level.playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.0F, player.level.random.nextFloat() * 0.1F + 0.9F);

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
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
	}

	// returns an empty vial when used in crafting recipes.
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	private static void decreaseAngelHeartByOne(Player player) {
		for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
			if (player.getInventory().items.get(slot).isEmpty()) {
				continue;
			}
			if (player.getInventory().items.get(slot).getItem() == ModItems.ANGELHEART_VIAL.get()) {
				player.getInventory().removeItem(slot, 1);
				return;
			}
		}
	}
}
