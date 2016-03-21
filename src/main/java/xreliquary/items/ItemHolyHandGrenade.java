package xreliquary.items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.reference.Names;

public class ItemHolyHandGrenade extends ItemBase {

    public ItemHolyHandGrenade() {
        super(Names.holy_hand_grenade);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
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
        EntityHolyHandGrenade grenade = new EntityHolyHandGrenade(par2World, par3EntityPlayer);
        grenade.func_184538_a(par3EntityPlayer, par3EntityPlayer.rotationPitch, par3EntityPlayer.rotationYaw, -20.0F, 0.9F, 1.0F);
        par2World.spawnEntityInWorld(grenade);

        return par1ItemStack;
    }

}
