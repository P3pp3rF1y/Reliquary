package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
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

public class ItemKrakenShell extends ItemBase {

	public ItemKrakenShell() {
		super(Names.Items.KRAKEN_SHELL);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(EntityPlayer player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.DROWN
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.krakenShell);
			}

			@Override
			public boolean apply(EntityPlayer player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.Items.KrakenShell.hungerCostPercent / 100F);
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
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	// checks to see if the player is in water. If so, give them some minor
	// buffs.
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;
			if(player.isInWater()) {
				player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 5, 0, true, false));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 5, 0, true, false));
				player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false));
			}
		}
	}
}
