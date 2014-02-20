package xreliquary.items;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@XRInit
public class ItemWitherlessRose extends ItemBase {

	public ItemWitherlessRose() {
		super(Reference.MOD_ID, Names.witherless_rose);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		EntityPlayer player = null;
		if (!(e instanceof EntityPlayer))
			return;
		player = (EntityPlayer) e;
		if (player.isPotionActive(Potion.wither.id)) {
			player.removePotionEffect(Potion.wither.id);
			for (int particles = 0; particles < 10; particles++) {
				double gauss1 = gaussian(world.rand);
				double gauss2 = gaussian(world.rand);
				world.spawnParticle("mobSpell", player.posX + gauss1, player.posY + player.height / 2, player.posZ + gauss2, 0.0, 0.0, 1.0);
			}
		}
	}

	public double gaussian(Random rand) {
		return rand.nextGaussian() / 6;
	}
}
