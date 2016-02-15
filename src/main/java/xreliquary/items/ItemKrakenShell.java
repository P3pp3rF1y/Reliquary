package xreliquary.items;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

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

    // checks to see if the player is in water. If so, give them some minor
    // buffs.
    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (e instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e;
            if (player.isInWater()) {
                player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 2, 0, true, false));
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2, 0, true, false));
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2, 0, true, false));
            }
        }
    }
}
