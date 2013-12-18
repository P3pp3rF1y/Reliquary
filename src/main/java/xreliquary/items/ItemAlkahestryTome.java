package xreliquary.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlkahestryTome extends ItemXR {

    protected ItemAlkahestryTome(int par1) {
        super(par1);
        this.setMaxDamage(Reliquary.PROXY.tombRedstoneLimit + 1);
        this.setMaxStackSize(1);
        this.canRepair = false;
        this.hasSubtypes = true;
        this.setContainerItem(this);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setUnlocalizedName(Names.TOME_NAME);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	par3List.add(EnumChatFormatting.RED + "Redstone: " + String.valueOf((Reliquary.PROXY.tombRedstoneLimit - stack.getItemDamage())) + "/" + String.valueOf(Reliquary.PROXY.tombRedstoneLimit));
        par3List.add("It says: perform basic, intermediate or");
        par3List.add("advanced Alkahestry, whatever that is.");
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
