package xreliquary.items;

import java.util.List;

import mods.themike.core.item.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class ItemGlowingWater extends ItemBase {

    protected ItemGlowingWater(int par1) {
        super(par1, Reference.MOD_ID, Names.GLOWING_WATER_NAME);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        canRepair = false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (par2World.isRemote)
            return par1ItemStack;
        if (!par3EntityPlayer.capabilities.isCreativeMode) {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        par2World.spawnEntityInWorld(new EntityGlowingWater(par2World, par3EntityPlayer));

        return par1ItemStack;
    }
}
