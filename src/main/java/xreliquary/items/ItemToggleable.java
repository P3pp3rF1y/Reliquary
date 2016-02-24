package xreliquary.items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.util.NBTHelper;


public class ItemToggleable extends ItemBase
{

    public ItemToggleable(String langName) {
        super(langName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
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
