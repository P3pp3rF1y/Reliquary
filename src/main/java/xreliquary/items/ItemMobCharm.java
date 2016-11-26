package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;

public class ItemMobCharm extends ItemBase {
	private static final String TYPE_TAG = "type";

	public ItemMobCharm() {
		super(Names.Items.MOB_CHARM);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(Settings.MobCharm.durability);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.canRepair = false;
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.MOB_CHARM + "_" + getType(ist);
	}

	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs creativeTab, NonNullList<ItemStack> subItems) {
		for(byte i = 0; i < Reference.MOB_CHARM.COUNT_TYPES; i++) {
			ItemStack subItem = new ItemStack(item);
			setType(subItem, i);
			subItems.add(subItem);
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

	public byte getMobCharmTypeForEntity(Entity entity) {
		if(entity instanceof EntityGhast) {
			return Reference.MOB_CHARM.GHAST_META;
		} else if(entity instanceof EntityMagmaCube) {
			return Reference.MOB_CHARM.MAGMA_CUBE_META;
		} else if(entity instanceof EntitySlime) {
			return Reference.MOB_CHARM.SLIME_META;
		} else if(entity instanceof EntityPigZombie) {
			return Reference.MOB_CHARM.ZOMBIE_PIGMAN_META;
		} else if(entity instanceof EntityZombie) {
			return Reference.MOB_CHARM.ZOMBIE_META;
		} else if(entity instanceof EntitySkeleton || entity instanceof EntityStray) {
			return Reference.MOB_CHARM.SKELETON_META;
		} else if(entity instanceof EntityWitherSkeleton) {
			return Reference.MOB_CHARM.WITHER_SKELETON_META;
		} else if(entity instanceof EntityCreeper) {
			return Reference.MOB_CHARM.CREEPER_META;
		} else if(entity instanceof EntityWitch) {
			return Reference.MOB_CHARM.WITCH_META;
		} else if(entity instanceof EntityCaveSpider) {
			return Reference.MOB_CHARM.CAVE_SPIDER_META;
		} else if(entity instanceof EntitySpider) {
			return Reference.MOB_CHARM.SPIDER_META;
		} else if(entity instanceof EntityEnderman) {
			return Reference.MOB_CHARM.ENDERMAN_META;
		} else if(entity instanceof EntityBlaze) {
			return Reference.MOB_CHARM.BLAZE_META;
		} else if(entity instanceof EntityGuardian) {
			return Reference.MOB_CHARM.GUARDIAN_META;
		}

		return -1;
	}

}
