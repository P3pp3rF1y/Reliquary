package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.reference.Colors;
import xreliquary.reference.Names;

@ContentInit
public class ItemFertilePotion extends ItemBase {

    public ItemFertilePotion() {
        super(Names.fertile_potion);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        canRepair = false;
    }

    @Override
    public boolean hasContainerItem(ItemStack ist) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack ist) {
        return new ItemStack(Reliquary.CONTENT.getItem(Names.potion), 1, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        if (renderPass == 1)
            return Integer.parseInt(Colors.FERTILIZER_COLOR, 16);
        else
            return Integer.parseInt(Colors.PURE, 16);
    }

   @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (world.isRemote)
            return ist;
        if (!player.capabilities.isCreativeMode) {
            --ist.stackSize;
        }
        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        world.spawnEntityInWorld(new EntityFertilePotion(world, player));
        return ist;
    }

}
