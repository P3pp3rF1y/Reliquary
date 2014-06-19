package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@ContentInit
public class ItemKrakenShell extends ItemBase {

    public ItemKrakenShell() {
        super(Names.kraken_shell);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    //checks to see if the player is in water. If so, give them some minor buffs.
    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (e instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)e;
            if (player.isInWater()) {
                player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 2, 0, true));
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2, 0, true));
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2, 0, true));
            }
        }
    }
}
