package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.lib.Names;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

@ContentInit
public class ItemInfernalTear extends ItemToggleable {

    public ItemInfernalTear() {
        super(Names.infernal_tear);
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
                player.addExperience((int) (Math.round(((double) (1d / (double) recipe.cost) / (double) recipe.yield) * 150)));
            }
        }
    }
}