package reliquary.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerDeathHandler;
import reliquary.init.ModItems;
import reliquary.network.SpawnAngelheartVialParticlesPayload;
import reliquary.reference.Config;
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

			@SuppressWarnings({"java:S2440"})
			//instantiating the packet for its type to be used as identifier for the packet
			@Override
			public boolean apply(Player player, LivingDeathEvent event) {
				decreaseAngelHeartByOne(player);

				// player should see a vial "shatter" effect and hear the glass break to
				// let them know they lost a vial.
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SpawnAngelheartVialParticlesPayload(player.position()));

				// play some glass breaking effects at the player location
				player.level().playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.0F, player.level().random.nextFloat() * 0.1F + 0.9F);

				// gives the player a few hearts, sparing them from death.
				float amountHealed = player.getMaxHealth() * (float) Config.COMMON.items.angelHeartVial.healPercentageOfMaxLife.get() / 100F;
				player.setHealth(amountHealed);

				// if the player had any negative status effects [vanilla only for now], remove them:
				if (Boolean.TRUE.equals(Config.COMMON.items.angelHeartVial.removeNegativeStatus.get())) {
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
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
	}

	// returns an empty vial when used in crafting recipes.
	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	private static void decreaseAngelHeartByOne(Player player) {
		ItemStack stack = InventoryHelper.getItemFromAllPlayerHandlers(player, ModItems.ANGELHEART_VIAL.get());
		if (!stack.isEmpty()) {
			stack.shrink(1);
		}
	}
}
