package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModItems;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;

public class InfernalClawsItem extends ItemBase {
	public InfernalClawsItem() {
		super(new Properties().stacksTo(1));

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingAttackEvent event) {
				return (event.getSource() == player.damageSources().inFire() || event.getSource() == player.damageSources().onFire())
						&& player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CLAWS.get());

			}

			@Override
			public boolean apply(Player player, LivingAttackEvent event) {
				player.causeFoodExhaustion(event.getAmount() * ((float) Settings.COMMON.items.infernalClaws.hungerCostPercent.get() / 100F));
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.RED);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	// this item's effects are handled in events
}
