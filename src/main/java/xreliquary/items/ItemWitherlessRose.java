package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.Random;

public class ItemWitherlessRose extends ItemBase {

	public ItemWitherlessRose() {
		super(Names.witherless_rose);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		EntityPlayer player = null;
		if(!(e instanceof EntityPlayer))
			return;
		player = (EntityPlayer) e;
		if(player.isPotionActive(MobEffects.wither)) {
			player.removePotionEffect(MobEffects.wither);
			for(int particles = 0; particles < 10; particles++) {
				double gauss1 = gaussian(world.rand);
				double gauss2 = gaussian(world.rand);
				world.spawnParticle(EnumParticleTypes.SPELL_MOB, player.posX + gauss1, player.posY + player.height / 2, player.posZ + gauss2, 0.0, 0.0, 1.0);
			}
		}
	}

	public double gaussian(Random rand) {
		return rand.nextGaussian() / 6;
	}
}
