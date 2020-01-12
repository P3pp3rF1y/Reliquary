package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

public class KrakenShellItem extends ItemBase {
	public KrakenShellItem() {
		super(Names.Items.KRAKEN_SHELL, new Properties().maxStackSize(1));

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.DROWN
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.KRAKEN_SHELL);
			}

			@Override
			public boolean apply(PlayerEntity player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.COMMON.items.krakenShell.hungerCostPercent.get() / 100F);
				player.addExhaustion(hungerDamage);
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	// checks to see if the player is in water. If so, give them some minor
	// buffs.
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (player.isInWater()) {
				player.addPotionEffect(new EffectInstance(Effects.HASTE, 5, 0, true, false));
				player.addPotionEffect(new EffectInstance(Effects.SPEED, 5, 0, true, false));
				player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, true, false));
			}
		}
	}
}
