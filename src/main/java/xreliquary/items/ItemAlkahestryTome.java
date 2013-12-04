package xreliquary.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlkahestryTome extends ItemXR {

    protected ItemAlkahestryTome(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setContainerItem(this);
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.TOME_NAME);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist) {
        return false;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add("It says: perform basic, intermediate or");
        par3List.add("advanced Alkahestry, whatever that is.");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

}
