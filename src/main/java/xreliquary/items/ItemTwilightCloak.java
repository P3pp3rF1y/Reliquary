package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
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
public class ItemTwilightCloak extends ItemToggleable {

    public ItemTwilightCloak() {
        super(Names.twilight_cloak);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
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
        if (!this.isEnabled(ist))
            return;
        EntityPlayer player = (EntityPlayer) e;

        //toggled effect, makes player invisible based on light level (configurable)
        int playerX = MathHelper.floor_double(player.posX);
        int playerY = MathHelper.floor_double(player.boundingBox.minY);
        int playerZ = MathHelper.floor_double(player.posZ);

        if (player.worldObj.getBlockLightValue(playerX, playerY, playerZ) > Reliquary.CONFIG.getInt(Names.twilight_cloak, "max_light_level"))
            return;

        if (Reliquary.CONFIG.getBool(Names.twilight_cloak, "only_works_at_night")) {
            long worldTime = player.worldObj.getWorldTime() % 24000;
            if (worldTime > 13187 && worldTime < 22812)
                return;
        }

        //checks if the effect would do anything. Literally all this does is make the player invisible. It doesn't interfere with mob AI.
        //for that, we're attempting to use an event handler.
        PotionEffect quickInvisibility = new PotionEffect(Potion.invisibility.id, 2, 0, false);
        player.addPotionEffect(quickInvisibility);
    }
}
