package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemNianZhu extends ItemBase {
	private static final String TYPE_TAG = "type";

	public ItemNianZhu() {
		super(Names.nian_zhu);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(Settings.NianZhu.durability);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.canRepair = false;
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.nian_zhu + "_" + getType(ist);
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(byte i = 0; i < 13; i++) {
			ItemStack subItem = new ItemStack(par1);
			setType(subItem, i);
			par3List.add(subItem);
		}
	}

	public byte getType(ItemStack stack) {
		if(stack.getItem() != ModItems.nianZhu || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TYPE_TAG))
			return -1;

		return stack.getTagCompound().getByte(TYPE_TAG);
	}

	public void setType(ItemStack stack, byte type) {
		NBTTagCompound compound = stack.getTagCompound();

		if(compound == null)
			compound = new NBTTagCompound();

		compound.setByte(TYPE_TAG, type);

		stack.setTagCompound(compound);
	}

	public ItemStack getNianZhuForEntity(Entity entity) {
		if(entity instanceof EntityGhast) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.GHAST_META);
		} else if(entity instanceof EntityMagmaCube) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.MAGMA_CUBE_META);
		} else if(entity instanceof EntitySlime) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.SLIME_META);
		} else if(entity instanceof EntityPigZombie) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.ZOMBIE_PIGMAN_META);
		} else if(entity instanceof EntityZombie) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.ZOMBIE_META);
		} else if(entity instanceof EntitySkeleton) {
			if(((EntitySkeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
				return XRRecipes.nianZhu(Reference.NIAN_ZHU.WITHER_SKELETON_META);
			} else {
				return XRRecipes.nianZhu(Reference.NIAN_ZHU.SKELETON_META);
			}
		} else if(entity instanceof EntityCreeper) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.CREEPER_META);
		} else if(entity instanceof EntityWitch) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.WITCH_META);
		} else if(entity instanceof EntityCaveSpider) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.CAVE_SPIDER_META);
		} else if(entity instanceof EntitySpider) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.SPIDER_META);
		} else if(entity instanceof EntityEnderman) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.ENDERMAN_META);
		} else if(entity instanceof EntityBlaze) {
			return XRRecipes.nianZhu(Reference.NIAN_ZHU.BLAZE_META);
		}

		return null;
	}

}
