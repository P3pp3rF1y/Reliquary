package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

/**
 * Created by Xeno on 5/15/14.
 */
public class ItemAngelicFeather extends ItemBase {

	public ItemAngelicFeather() {
		super(Names.angelic_feather);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	// so it can be extended by phoenix down
	protected ItemAngelicFeather(String name) {
		super(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	// event driven item, does nothing here.

	// minor jump buff
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		int potency = this instanceof ItemPhoenixDown ? Settings.PhoenixDown.leapingPotency : Settings.AngelicFeather.leapingPotency;
		if(potency == 0)
			return;
		potency -= 1;
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 2, potency, true, false));
		}
	}
}
