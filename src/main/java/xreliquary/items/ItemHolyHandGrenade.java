package xreliquary.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHolyHandGrenade extends ItemXR {

    protected ItemHolyHandGrenade(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setUnlocalizedName(Names.GRENADE_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add("Kills mobs, not blocks");
        par3List.add("or whoever threw it.");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
            EntityPlayer par3EntityPlayer) {
        if (par2World.isRemote)
            return par1ItemStack;
        if (!par3EntityPlayer.capabilities.isCreativeMode) {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        par2World.spawnEntityInWorld(new EntityHolyHandGrenade(par2World,
                par3EntityPlayer));

        return par1ItemStack;
    }

}
