package lib.enderwizards.sandstone.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.List;

public class ItemMultiple extends ItemBase {

    protected final SubItem[] items;

    public ItemMultiple(String name, Object[] items) {
        this(name, buildSubItems(items));
    }

    public ItemMultiple(String name, String[] names) {
        this(name, buildSubItems(names));
    }

    public ItemMultiple(String name, SubItem[] items) {
        super(name);
        this.items = items;
        this.hasSubtypes = true;
        for (SubItem item : items) {
            item.parent = this;
        }
    }

    private static SubItem[] buildSubItems(String[] names) {
        List<SubItem> items = new ArrayList<SubItem>();
        for (String name : names) {
            items.add(new SubItem(name));
        }
        return items.toArray(new SubItem[items.size()]);
    }

    private static SubItem[] buildSubItems(Object[] items) {
        List<SubItem> newItems = new ArrayList<SubItem>();
        for (Object item : items) {
            if (item instanceof String)
                newItems.add(new SubItem((String) item));
            if (item instanceof SubItem)
                newItems.add((SubItem) item);
        }
        return newItems.toArray(new SubItem[newItems.size()]);
    }

    public int getLength() {
        return items.length;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatDoesThisEvenDo) {
        if (stack.getItemDamage() < items.length) {
            items[stack.getItemDamage()].addInformation(stack, player, list);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getItemDamage() < items.length) {
            return items[stack.getItemDamage()].getItemStackDisplayName(stack);
        }
        return "null";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getItemDamage() < items.length) {
            return items[stack.getItemDamage()].getUnlocalizedName();
        }
        return "null";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (meta < items.length) {
            return items[meta].getIcon();
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        for (SubItem item : items) {
            item.registerIcons(iconRegister);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (int index = 0; index < getLength(); index++) {
            list.add(new ItemStack(item, 1, index));
        }
    }

}
