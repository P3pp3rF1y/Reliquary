package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;
import mods.themike.core.item.ItemBase;

public class ItemHeroMedallion extends ItemBase {

	public ItemHeroMedallion() {
		super(Reference.MOD_ID, "heroMedallion");
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		this.formatTooltip(ImmutableMap.of("experience", String.valueOf(NBTHelper.getShort("experience", stack))), stack, list);
	}

    // TODO: Make this actually work.
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if(!player.isSneaking()) {
			if(stack.getTagCompound().hasKey("experience")) {
				if(stack.getTagCompound().getShort("experience") + 10 <= 1760) {
					stack.getTagCompound().setShort("experience", (short) (stack.getTagCompound().getShort("experience") + 10));
					player.addExperience(-10);
				}
			} else {
				stack.getTagCompound().setShort("experience", (short) 10);
				player.addExperience(-10);
			}
		} else {
			if(stack.getTagCompound().hasKey("experience")) {
				if(stack.getTagCompound().getShort("experience") - 10 >= 0) {
					stack.getTagCompound().setShort("experience", (short) (stack.getTagCompound().getShort("experience") - 10));
					player.addExperience(10);
				}
			}
		}
		return stack;
	}

}
