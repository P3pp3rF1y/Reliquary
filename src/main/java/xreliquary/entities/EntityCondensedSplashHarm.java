package xreliquary.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCondensedSplashHarm extends EntityThrownPotion {
    public EntityCondensedSplashHarm(World par1World) {
        super(par1World);
    }

    public EntityCondensedSplashHarm(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @SideOnly(Side.CLIENT)
    public EntityCondensedSplashHarm(World par1World, double par2, double par4, double par6, int par8) {
        this(par1World, par2, par4, par6);
    }

    public EntityCondensedSplashHarm(World par1World, double par2, double par4, double par6) {
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
    void doLivingSplashEffect(EntityLivingBase e) {
        e.attackEntityFrom(DamageSource.magic, 8);
    }

    @Override
    float getRed() {
        return 1F;
    }

    @Override
    float getGreen() {
        return 0.5F;
    }

    @Override
    float getBlue() {
        return 0;
    }
}
