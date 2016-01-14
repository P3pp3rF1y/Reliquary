package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

import java.util.List;

@ContentInit
public class ItemGunPart extends ItemBase {

    public ItemGunPart() {
        super(Names.gun_part);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(4);
        this.setHasSubtypes(true);
        canRepair = false;
    }

    //TODO: include in JSON model
/*
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        icons = new IIcon[3];
        for (int i = 0; i < 3; i++) {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.gun_part + "_" + i);
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        if (meta < 3)
            return icons[meta];
        return icons[0];
    }
*/

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getItemDamage() > 2) {
            return "item." + Names.gun_part + "_" + "0";
        }
        return "item." + Names.gun_part + "_" + String.valueOf(stack.getItemDamage());
    }
}
