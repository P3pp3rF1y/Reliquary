package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntityKrakenSlime;
import xreliquary.lib.Names;

@ContentInit
public class ItemSerpentStaff extends ItemBase {

    public ItemSerpentStaff() {
        super(Names.serpent_staff);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(200);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.block;
    }

    @Override
    public void onUsingTick(ItemStack item, EntityPlayer player, int count) {
        if (player.worldObj.isRemote || count % 3 != 0)
            return;

        player.worldObj.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        player.worldObj.spawnEntityInWorld(new EntityKrakenSlime(player.worldObj, player));
        item.damageItem(1, player);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, 72000);
        return stack;
    }

}
