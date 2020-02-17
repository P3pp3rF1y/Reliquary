package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

public class InfernalClawsItem extends ItemBase {
	public InfernalClawsItem() {
		super("infernal_claws", new Properties().maxStackSize(1));

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingAttackEvent event) {
				return (event.getSource() == DamageSource.IN_FIRE || event.getSource() == DamageSource.ON_FIRE)
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CLAWS);

			}

			@Override
			public boolean apply(PlayerEntity player, LivingAttackEvent event) {
				player.addExhaustion(event.getAmount() * ((float) Settings.COMMON.items.infernalClaws.hungerCostPercent.get() / 100F));
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

	// this item's effects are handled in events
}
