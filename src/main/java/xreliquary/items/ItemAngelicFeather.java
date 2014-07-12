package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;

/**
 * Created by Xeno on 5/15/14.
 */
@ContentInit
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
		return EnumRarity.epic;
	}

	// event driven item, does nothing here.

	// minor jump buff
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if (e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;
			player.addPotionEffect(new PotionEffect(Potion.jump.id, 2, 0, true));
		}
	}
}
