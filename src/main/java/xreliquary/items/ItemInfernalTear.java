package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ItemModels;
import xreliquary.reference.Names;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

//TODO: likely extend ItemTear, has logic from before refactoring that removed all logic here

@ContentInit
public class ItemInfernalTear extends ItemToggleable {

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

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int ticksRemaining) {
        if (isEnabled(stack)) {
            return ItemModels.getInstance().getModel(ItemModels.INFERNAL_TEAR);
        }
        return ItemModels.getInstance().getModel(ItemModels.INFERNAL_TEAR_EMPTY);
    }
}