package xreliquary.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMercyCross extends ItemSword {

    protected ItemMercyCross(int par1) {
        super(par1, EnumToolMaterial.GOLD);
        this.setMaxDamage(64);
        this.setMaxStackSize(1);
        canRepair = true;
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.CROSS_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {

        itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":"
                + this.getUnlocalizedName().substring(
                        this.getUnlocalizedName().indexOf(".") + 1));
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
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add("'Damn, that thing's heavy!'");
        par3List.add("That, my friend, is because");
        par3List.add("it's so full of mercy.");
    }

    @Override
    public int getDamageVsEntity(Entity par1Entity) {
        if (isUndead(par1Entity)) {
            par1Entity.worldObj.spawnParticle("largeexplode", par1Entity.posX,
                    par1Entity.posY + par1Entity.height / 2, par1Entity.posZ,
                    0.0F, 0.0F, 0.0F);
        }
        return isUndead(par1Entity) ? super.getDamageVsEntity(par1Entity) * 2
                : super.getDamageVsEntity(par1Entity) * 1;
    }

    private boolean isUndead(Entity mop) {
        return mop instanceof EntitySkeleton || mop instanceof EntityGhast
                || mop instanceof EntityWither || mop instanceof EntityZombie
                || mop instanceof EntityPigZombie;
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base,
     * (Quality+1)*2 if correct blocktype, 1.5F if sword
     */
    @Override
    public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
        return 1.5F;
    }

    /**
     * Current implementations of this method in child classes do not use the
     * entry argument beside ev. They just raise the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack,
            EntityLiving par2EntityLiving, EntityLiving par3EntityLiving) {
        par1ItemStack.damageItem(1, par3EntityLiving);
        return true;
    }
}
