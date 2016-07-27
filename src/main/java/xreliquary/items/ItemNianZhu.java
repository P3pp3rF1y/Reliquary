package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemNianZhu extends ItemBase {
	private static final String TYPE_TAG = "type";

	public ItemNianZhu() {
		super(Names.nian_zhu);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.canRepair = false;
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item.heart_zhu_" + getType(ist);
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
		if (stack.getItem() != ModItems.nianZhu || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TYPE_TAG))
			return -1;

		return stack.getTagCompound().getByte(TYPE_TAG);
	}

	public void setType(ItemStack stack, byte type) {
		NBTTagCompound compound = stack.getTagCompound();

		if (compound == null)
			compound = new NBTTagCompound();

		compound.setByte(TYPE_TAG, type);

		stack.setTagCompound(compound);
	}
}
