package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import mods.themike.core.item.ItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlkahestryTome extends ItemBase {

    protected ItemAlkahestryTome(int par1) {
        super(par1, Reference.MOD_ID, Names.TOME_NAME);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(Reliquary.PROXY.tombRedstoneLimit + 1);
        this.setMaxStackSize(1);
        this.canRepair = false;
        this.hasSubtypes = true;
        this.setContainerItem(this);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    	this.formatTooltip(ImmutableMap.of("redstoneAmount", String.valueOf((Reliquary.PROXY.tombRedstoneLimit - stack.getItemDamage())), "redstoneLimit", String.valueOf(Reliquary.PROXY.tombRedstoneLimit)), stack, list);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }
    
    @Override
    public ItemStack getContainerItemStack(ItemStack stack) {
    	ItemStack copy = stack.copy();
    	copy.stackSize = 1;
    	return copy;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
    
    @Override
    public void getSubItems(int ID, CreativeTabs tabs, List list) {
        ItemStack tombStack = new ItemStack(ID, 1, 0);
        tombStack.setItemDamage(Reliquary.PROXY.tombRedstoneLimit);
        list.add(tombStack);
    }
    
}
