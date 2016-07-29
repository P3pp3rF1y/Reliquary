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
public class ItemMobCharm extends ItemBase {
	private static final String TYPE_TAG = "type";

	public ItemMobCharm() {
		super(Names.mob_charm);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(Settings.MobCharm.durability);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.canRepair = false;
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.mob_charm + "_" + getType(ist);
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
		if(stack.getItem() != ModItems.mobCharm || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TYPE_TAG))
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

	public ItemStack getMobCharmForEntity(Entity entity) {
		if(entity instanceof EntityGhast) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.GHAST_META);
		} else if(entity instanceof EntityMagmaCube) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.MAGMA_CUBE_META);
		} else if(entity instanceof EntitySlime) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.SLIME_META);
		} else if(entity instanceof EntityPigZombie) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.ZOMBIE_PIGMAN_META);
		} else if(entity instanceof EntityZombie) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.ZOMBIE_META);
		} else if(entity instanceof EntitySkeleton) {
			if(((EntitySkeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
				return XRRecipes.mobCharm(Reference.MOB_CHARM.WITHER_SKELETON_META);
			} else {
				return XRRecipes.mobCharm(Reference.MOB_CHARM.SKELETON_META);
			}
		} else if(entity instanceof EntityCreeper) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.CREEPER_META);
		} else if(entity instanceof EntityWitch) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.WITCH_META);
		} else if(entity instanceof EntityCaveSpider) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.CAVE_SPIDER_META);
		} else if(entity instanceof EntitySpider) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.SPIDER_META);
		} else if(entity instanceof EntityEnderman) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.ENDERMAN_META);
		} else if(entity instanceof EntityBlaze) {
			return XRRecipes.mobCharm(Reference.MOB_CHARM.BLAZE_META);
		}

		return null;
	}

}
