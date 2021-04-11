package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerDeathHandler;
import xreliquary.init.ModItems;
import xreliquary.network.PacketHandler;
import xreliquary.network.SpawnAngelheartVialParticlesPacket;
import xreliquary.reference.Settings;
import xreliquary.util.EntityHelper;
import xreliquary.util.InventoryHelper;

public class AngelheartVialItem extends ItemBase {
	public AngelheartVialItem() {
		super(new Properties());

		CommonEventHandler.registerPlayerDeathHandler(new IPlayerDeathHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingDeathEvent event) {
				return InventoryHelper.playerHasItem(player, ModItems.ANGELHEART_VIAL.get());
			}

			@SuppressWarnings({"java:S2440", "InstantiationOfUtilityClass"}) //instantiating the packet for its type to be used as identifier for the packet
			@Override
			public boolean apply(PlayerEntity player, LivingDeathEvent event) {
				decreaseAngelHeartByOne(player);

				// player should see a vial "shatter" effect and hear the glass break to
				// let them know they lost a vial.
				PacketHandler.sendToClient((ServerPlayerEntity) player, new SpawnAngelheartVialParticlesPacket());

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
		return new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
	}

	// returns an empty vial when used in crafting recipes.
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	private static void decreaseAngelHeartByOne(PlayerEntity player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}
			if (player.inventory.mainInventory.get(slot).getItem() == ModItems.ANGELHEART_VIAL.get()) {
				player.inventory.decrStackSize(slot, 1);
				return;
			}
		}
	}
}
