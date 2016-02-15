package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.List;

public class ItemGunPart extends ItemBase {

    public ItemGunPart() {
        super(Names.gun_part);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(4);
        this.setHasSubtypes(true);
        canRepair = false;
    }

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
