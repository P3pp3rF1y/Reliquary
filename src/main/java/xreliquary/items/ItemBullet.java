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

public class ItemBullet extends ItemXR {

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
                + ":" + Names.BULLET_NAME);
        iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.BULLET_OVERLAY_NAME);
    }

    @Override
    public Icon getIcon(ItemStack itemStack, int renderPass) {
        if (itemStack.getItemDamage() == 0)
            return itemIcon;
        if (renderPass != 1)
            return itemIcon;
        else
            return iconOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {

        if (renderPass == 1)
            return getColor(itemStack);
        else
            return Integer.parseInt(Colors.PURE, 16);
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
        return Integer.parseInt(Colors.PURE, 16);
    }

    // public boolean hasColor(ItemStack itemStack) {
    //
    // return itemStack.getItemDamage() != 0;
    // }

    protected ItemBullet(int par1) {
        super(par1);
        // 0 = Empty, 1 = Neutral, 2 = Exorcism, 3 = Blaze
        // 4 = Ender, 5 = Concussive, 6 = Buster, 7 = Seeker
        // 8 = Sand, 9 = Storm
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);

    }

    @Override
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        int i = par1ItemStack.getItemDamage();
        switch (i) {
        case 0:
            par3List.add("An empty shell.");
            break;
        case 1:
            par3List.add("An ordinary bullet.");
            break;
        case 2:
            par3List.add("Effective vs. undead.");
            break;
        case 3:
            par3List.add("Deals only fire damage.");
            break;
        case 4:
            par3List.add("Seek targets, passes through anything.");
            break;
        case 5:
            par3List.add("Deal damage to a small area.");
            break;
        case 6:
            par3List.add("Create a sizable explosion.");
            break;
        case 7:
            par3List.add("Seeks its targets.");
            break;
        case 8:
            par3List.add("Blinds target. Wrecks creepers.");
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
            return Names.BULLET_0_LOCAL;
        case 1:
            return Names.BULLET_1_LOCAL;
        case 2:
            return Names.BULLET_2_LOCAL;
        case 3:
            return Names.BULLET_3_LOCAL;
        case 4:
            return Names.BULLET_4_LOCAL;
        case 5:
            return Names.BULLET_5_LOCAL;
        case 6:
            return Names.BULLET_6_LOCAL;
        case 7:
            return Names.BULLET_7_LOCAL;
        case 8:
            return Names.BULLET_8_LOCAL;
        case 9:
            return Names.BULLET_9_LOCAL;
        default:
            return Names.BULLET_0_LOCAL;
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
