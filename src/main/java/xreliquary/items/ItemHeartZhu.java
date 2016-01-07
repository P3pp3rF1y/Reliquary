package xreliquary.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import xreliquary.Reliquary;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemHeartZhu extends ItemBase {
    public ItemHeartZhu() {
        super(Names.heart_zhu);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(Reference.MOD_ID + ":" + "heart_zhu");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int renderPass) {
        if (renderPass == 1)
            return this.itemIcon;
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        if (renderPass == 1)
            return getColor(itemStack);
        return Integer.parseInt(Colors.PURE,16);
    }

    @Override
    public String getUnlocalizedName(ItemStack ist) {
        return "item.heart_zhu_" + ist.getItemDamage();
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < 4; i++)
            par3List.add(new ItemStack(par1, 1, i));
    }

    public int getColor(ItemStack itemStack) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 0:
                return Integer.parseInt(Colors.ZOMBIE_HEART_ZHU_COLOR, 16);
            case 1:
                return Integer.parseInt(Colors.SKELETON_HEART_ZHU_COLOR, 16);
            case 2:
                return Integer.parseInt(Colors.WITHER_SKELETON_HEART_ZHU_COLOR, 16);
            case 3:
                return Integer.parseInt(Colors.CREEPER_HEART_ZHU_COLOR, 16);
        }
        return Integer.parseInt(Colors.PURE, 16);
    }
}
