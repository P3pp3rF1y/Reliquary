package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCondensedSplashWeakness extends EntityThrownPotion {
	public EntityCondensedSplashWeakness(World par1World) {
		super(par1World);
	}

	public EntityCondensedSplashWeakness(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@SideOnly(Side.CLIENT)
	public EntityCondensedSplashWeakness(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityCondensedSplashWeakness(World par1World, double par2, double par4, double par6) {
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
		e.addPotionEffect(new PotionEffect(Potion.weakness.id, 900, 1));
	}

	@Override
	float getRed() {
		return 0.3F;
	}

	@Override
	float getGreen() {
		return 1.0F;
	}

	@Override
	float getBlue() {
		return 0.3F;
	}
}
