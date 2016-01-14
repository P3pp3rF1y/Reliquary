package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

@ContentInit
public class ItemInfernalTear extends ItemToggleable {

    //TODO: add this to JSON model
/*
    @Override
    public IIcon getIcon(ItemStack ist, int renderPass) {
        if (!this.isEnabled(ist) || renderPass != 1)
            return inactiveSprite;
        else
            return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    private IIcon inactiveSprite;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        inactiveSprite = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.infernal_tear_empty);
    }
*/

    public ItemInfernalTear() {
        super(Names.infernal_tear);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean flag) {
        if (!isEnabled(ist))
            return;
        if (!(e instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer)e;
        String ident = ist.getTagCompound().getString("itemID");
        if (Alkahestry.getRegistry().containsKey(ident)) {
            AlkahestRecipe recipe = Alkahestry.getRegistry().get(ident);
            // You need above Cobblestone level to get XP.
            if (recipe.yield != 32 && recipe.cost != 4) {
                player.addExperience((int) (Math.round(((1d / (double) recipe.cost) / (double) recipe.yield) * 150)));
            }
        }
    }
}