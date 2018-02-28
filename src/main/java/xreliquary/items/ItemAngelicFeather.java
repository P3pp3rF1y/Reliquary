package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;

public class ItemAngelicFeather extends ItemBase {

	public ItemAngelicFeather() {
		super(Names.Items.ANGELIC_FEATHER);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(EntityPlayer player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.FALL
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.angelicFeather)
						&& player.fallDistance > 0.0F;
			}

			@Override
			public boolean apply(EntityPlayer player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.Items.AngelicFeather.hungerCostPercent / 100F);
				player.addExhaustion(hungerDamage);
				return true;
			}

			@Override
			public Priority getPriority() {
				return Priority.HIGH;
			}
		});
	}

	// so it can be extended by phoenix down
	ItemAngelicFeather(String name) {
		super(name);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	// event driven item, does nothing here.

	// minor jump buff
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		int potency = this instanceof ItemPhoenixDown ? Settings.Items.PhoenixDown.leapingPotency : Settings.Items.AngelicFeather.leapingPotency;
		if(potency == 0)
			return;
		potency -= 1;
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 2, potency, true, false));
		}
	}
}
