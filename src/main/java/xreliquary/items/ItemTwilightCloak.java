package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.event.ClientEventHandler;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@ContentInit
public class ItemTwilightCloak extends ItemBase {

    public ItemTwilightCloak() {
        super(Names.twilight_cloak);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        // this.setMaxDamage(2401);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (!(e instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) e;
        // always on for now, takes effect only at night, or low light
        // (configurable)
        // if (ist.getItemDamage() == 0) return;

        int playerX = MathHelper.floor_double(player.posX);
        int playerY = MathHelper.floor_double(player.boundingBox.minY);
        int playerZ = MathHelper.floor_double(player.posZ);

        if (player.worldObj.getBlockLightValue(playerX, playerY, playerZ) > Reliquary.CONFIG.getInt(Names.twilight_cloak, "maxLightLevel"))
            return;
        // checks if the effect would do anything.
        PotionEffect quickInvisibility = new PotionEffect(Potion.invisibility.id, 2, 0, true);
        if (!player.isPotionApplicable(quickInvisibility))
            return;
        player.addPotionEffect(quickInvisibility);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        // ist.setItemDamage(ist.getItemDamage() == 0 ? 1 : 0);
        return ist;
    }

}
