package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCondensedSplashBlindness extends EntityThrownPotion {
	public EntityCondensedSplashBlindness(World par1World) {
		super(par1World);
	}

	public EntityCondensedSplashBlindness(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@SideOnly(Side.CLIENT)
	public EntityCondensedSplashBlindness(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityCondensedSplashBlindness(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	boolean hasLivingEntityEffect() {
		return true;
	}

	@Override
	void doGroundSplashEffect() {
		// none
	}

	@Override
	void doLivingSplashEffect(EntityLiving e) {
		e.addPotionEffect(new PotionEffect(Potion.blindness.id, 900, 0));
	}

	@Override
	float getRed() {
		return 0.1F;
	}

	@Override
	float getGreen() {
		return 0.1F;
	}

	@Override
	float getBlue() {
		return 0.1F;
	}
}
