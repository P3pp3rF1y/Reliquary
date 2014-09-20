package xreliquary.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

@ContentInit
public class ItemMercyCross extends ItemSword {

    public ItemMercyCross() {
        super(ToolMaterial.GOLD);
        this.setMaxDamage(64);
        this.setMaxStackSize(1);
        canRepair = true;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setUnlocalizedName(Names.mercy_cross);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {

        itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        String value = LanguageHelper.getLocalization("item." + Names.mercy_cross + ".tooltip");
        for (String descriptionLine : value.split(";")) {
            if (descriptionLine != null && descriptionLine.length() > 0)
                list.add(descriptionLine);
        }
    }

    @Override
    public float func_150931_i() {
        return 0.0F;
    }

    private boolean isUndead(EntityLivingBase e) {
        return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base,
     * (Quality+1)*2 if correct blocktype, 1.5F if sword
     */
    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return block == Blocks.web ? 15.0F : 1.5F;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = HashMultimap.create();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double) 0, 0));
        return multimap;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase monster, EntityLivingBase player) {
        if (player instanceof EntityPlayer) {
            monster.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 6);
        }
        if (isUndead(monster)) {
            monster.worldObj.spawnParticle("largeexplode", monster.posX, monster.posY + monster.height / 2, monster.posZ, 0.0F, 0.0F, 0.0F);
            monster.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), 6);
        }
        return true;
    }
}
