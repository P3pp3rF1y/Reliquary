package xreliquary.items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;


public class ItemGlowingWater extends ItemBase {

    public ItemGlowingWater() {
        super(Names.glowing_water);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        canRepair = false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack ist) {
        return new ItemStack(ModItems.potion, 1, 0);
    }

    // returns an empty vial when used in crafting recipes.
    @Override
    public boolean hasContainerItem(ItemStack ist) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
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
