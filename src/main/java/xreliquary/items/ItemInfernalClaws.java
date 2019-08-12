package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

public class ItemInfernalClaws extends ItemBase {

	public ItemInfernalClaws() {
		super(Names.Items.INFERNAL_CLAWS);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(EntityPlayer player, LivingAttackEvent event) {
				return (event.getSource() == DamageSource.IN_FIRE || event.getSource() == DamageSource.ON_FIRE)
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.infernalClaws);

			}

			@Override
			public boolean apply(EntityPlayer player, LivingAttackEvent event) {
				player.addExhaustion(event.getAmount() * ((float) Settings.Items.InfernalClaws.hungerCostPercent / 100F));
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	// this item's effects are handled in events
}
