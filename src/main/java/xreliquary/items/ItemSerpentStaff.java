package xreliquary.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
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
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        //drain effect
        int drain = player.worldObj.rand.nextInt(4);
        if (entity.attackEntityFrom(DamageSource.causePlayerDamage(player), drain)) {
            player.heal(drain);
            stack.damageItem(1, player);
        }
        return false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 11;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

}
