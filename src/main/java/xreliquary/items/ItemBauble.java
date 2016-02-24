package xreliquary.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import xreliquary.reference.Compatibility;

@Optional.Interface(iface="baubles.api.IBauble", modid= Compatibility.MOD_ID.Baubles, striprefs=true)
public abstract class ItemBauble extends ItemBase implements IBauble {

    public ItemBauble(String langName) {
        super(langName);
    }

    @Override
    @Optional.Method(modid = Compatibility.MOD_ID.Baubles)
    public abstract BaubleType getBaubleType(ItemStack stack);

    @Override
    @Optional.Method(modid = Compatibility.MOD_ID.Baubles)
    public abstract void onWornTick(ItemStack stack, EntityLivingBase player);

    @Override
    @Optional.Method(modid = Compatibility.MOD_ID.Baubles)
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (!player.worldObj.isRemote)
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2.2F));
        onWornTick(stack, player);
    }

    @Override
    @Optional.Method(modid = Compatibility.MOD_ID.Baubles)
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        // Nothing?
    }

    @Override
    @Optional.Method(modid = Compatibility.MOD_ID.Baubles)
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = Compatibility.MOD_ID.Baubles)
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }
}
