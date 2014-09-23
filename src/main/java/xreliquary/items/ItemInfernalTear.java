package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

@ContentInit
public class ItemInfernalTear extends ItemTear {

    public ItemInfernalTear() {
        super(Names.infernal_tear);
    }

    @Override
    protected void onAbsorb(ItemStack stack, EntityPlayer player) {
        String ident = stack.getTagCompound().getString("itemID");
        if (Alkahestry.getRegistry().containsKey(ident)) {
            AlkahestRecipe recipe = Alkahestry.getRegistry().get(ident);
            // You need above Cobblestone level to get XP.
            if (recipe.yield != 32 && recipe.cost != 4) {
                player.addExperience((int) (Math.round(((double) (1d / (double) recipe.cost) / (double) recipe.yield) * 150)));
            }
        }
    }
}