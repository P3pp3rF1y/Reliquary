package lib.enderwizards.sandstone.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemToggleable extends ItemBase {

    public ItemToggleable(String langName) {
        super(langName);
        this.hasSubtypes = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return this.isEnabled(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player.isSneaking()) {
            toggleEnabled(stack);
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
            return stack;
        }
        return stack;
    }

    public boolean isEnabled(ItemStack ist) {
        return NBTHelper.getBoolean("enabled", ist);
    }

    public void toggleEnabled(ItemStack ist) {
        NBTHelper.setBoolean("enabled", ist, !NBTHelper.getBoolean("enabled", ist));
    }

}
