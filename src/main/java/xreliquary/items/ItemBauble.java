package xreliquary.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public abstract class ItemBauble extends ItemBase implements IBauble {

    public ItemBauble(String langName) {
        super(langName);
    }

    @Override
    public abstract BaubleType getBaubleType(ItemStack stack);

    @Override
    public abstract void onWornTick(ItemStack stack, EntityLivingBase player);

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (!player.worldObj.isRemote)
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2.2F));
        onWornTick(stack, player);
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        // Nothing?
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }
}
