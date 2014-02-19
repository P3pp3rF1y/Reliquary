package xreliquary.items;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.LanguageHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@XRInit
public class ItemMercyCross extends ItemSword {

	public ItemMercyCross() {
		super(ToolMaterial.GOLD);
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		canRepair = true;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setUnlocalizedName(Names.CROSS_NAME);
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
		String value = LanguageHelper.getLocalization("item." + Names.CROSS_NAME + ".tooltip");
		for (String descriptionLine : value.split(";")) {
			if (descriptionLine != null && descriptionLine.length() > 0)
				list.add(descriptionLine);
		}
	}

    @Override
    public float func_150931_i() {
        return 0.0F;
    }

	private boolean isUndead(Entity mop) {
		return mop instanceof EntitySkeleton || mop instanceof EntityGhast || mop instanceof EntityWither || mop instanceof EntityZombie || mop instanceof EntityPigZombie;
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

    // About time it worked. :D
	@Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase monster, EntityLivingBase player) {
        if (isUndead(monster)) {
            monster.worldObj.spawnParticle("largeexplode", monster.posX, monster.posY + monster.height / 2, monster.posZ, 0.0F, 0.0F, 0.0F);
            monster.heal(-(4.0F * 2));
        } else {
            monster.heal(-4.0F);
        }
		return true;
	}
}
