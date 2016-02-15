package xreliquary.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.List;

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
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        String value = LanguageHelper.getLocalization("item." + Names.mercy_cross + ".tooltip");
        for (String descriptionLine : value.split(";")) {
            if (descriptionLine != null && descriptionLine.length() > 0)
                list.add(descriptionLine);
        }
    }

    @Override
    public float getDamageVsEntity() {
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
    public float getStrVsBlock(ItemStack stack, Block block) {
        return block == Blocks.web ? 15.0F : 1.5F;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = HashMultimap.create();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double) 0, 0));
        return multimap;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity monster) {
        if (monster instanceof EntityLiving) {
            if (isUndead((EntityLiving)monster)) {
                monster.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, monster.posX + (itemRand.nextFloat() - 0.5F), monster.posY + (itemRand.nextFloat() - 0.5F) + (monster.height / 2), monster.posZ + (itemRand.nextFloat() - 0.5F), 0.0F, 0.0F, 0.0F);
            }
        }
        return super.onLeftClickEntity(stack, player, monster);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase monster, EntityLivingBase player) {
        int dmg = isUndead(monster) ? 12 : 6;
        if (player instanceof EntityPlayer) {
            monster.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), dmg);
        }
        return true;
    }
}
