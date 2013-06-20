package xreliquary.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import xreliquary.Reliquary;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMagazine extends ItemXR {

    @SideOnly(Side.CLIENT)
    private Icon iconOverlay;

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.MAGAZINE_NAME);
        iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.MAGAZINE_OVERLAY_NAME);
    }

    @Override
    public Icon getIcon(ItemStack itemStack, int renderPass) {
        if (itemStack.getItemDamage() == 0 || renderPass != 1)
            return itemIcon;
        else
            return iconOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        if (itemStack.getItemDamage() == 0 || renderPass != 1)
            return Integer.parseInt(Colors.DARKER, 16);
        else
            return getColor(itemStack);
    }

    public int getColor(ItemStack itemStack) {

        switch (itemStack.getItemDamage()) {
        case 1:
            return Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16);
        case 2:
            return Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16);
        case 3:
            return Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16);
        case 4:
            return Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16);
        case 5:
            return Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16);
        case 6:
            return Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16);
        case 7:
            return Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16);
        case 8:
            return Integer.parseInt(Colors.SAND_SHOT_COLOR, 16);
        case 9:
            return Integer.parseInt(Colors.STORM_SHOT_COLOR, 16);
        }
        return Integer.parseInt(Colors.DARKEST, 16);
    }

    protected ItemMagazine(int par1) {
        super(par1);
        this.setMaxStackSize(64);
        canRepair = false;
        this.setHasSubtypes(true);
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.MAGAZINE_NAME);
    }

    @Override
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        int i = par1ItemStack.getItemDamage();
        switch (i) {
        case 0:
            par3List.add("An empty magazine.");
            break;
        case 1:
            par3List.add("Ordinary bullets.");
            break;
        case 2:
            par3List.add("Effective vs. undead.");
            break;
        case 3:
            par3List.add("Deal bonus fire damage.");
            break;
        case 4:
            par3List.add("Pierce enemies.");
            break;
        case 5:
            par3List.add("Deal damage to a small area.");
            break;
        case 6:
            par3List.add("Create a sizable explosion.");
            break;
        case 7:
            par3List.add("Seek targets.");
            break;
        case 8:
            par3List.add("Blind targets. Wreck creepers.");
            break;
        case 9:
            par3List.add("Causes atmospheric weirdness.");
            break;
        }
    }

    @Override
    public String getItemDisplayName(ItemStack ist) {
        switch (ist.getItemDamage()) {
        case 0:
            return Names.MAGAZINE_0_LOCAL;
        case 1:
            return Names.MAGAZINE_1_LOCAL;
        case 2:
            return Names.MAGAZINE_2_LOCAL;
        case 3:
            return Names.MAGAZINE_3_LOCAL;
        case 4:
            return Names.MAGAZINE_4_LOCAL;
        case 5:
            return Names.MAGAZINE_5_LOCAL;
        case 6:
            return Names.MAGAZINE_6_LOCAL;
        case 7:
            return Names.MAGAZINE_7_LOCAL;
        case 8:
            return Names.MAGAZINE_8_LOCAL;
        case 9:
            return Names.MAGAZINE_9_LOCAL;
        default:
            return Names.MAGAZINE_0_LOCAL;
        }
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
            List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
        par3List.add(new ItemStack(par1, 1, 4));
        par3List.add(new ItemStack(par1, 1, 5));
        par3List.add(new ItemStack(par1, 1, 6));
        par3List.add(new ItemStack(par1, 1, 7));
        par3List.add(new ItemStack(par1, 1, 8));
        par3List.add(new ItemStack(par1, 1, 9));
    }

}
