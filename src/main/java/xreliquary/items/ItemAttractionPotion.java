package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.potion.*;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@ContentInit
public class ItemAttractionPotion extends ItemBase {

    public ItemAttractionPotion() {
        super(Names.attraction_potion);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        canRepair = false;
    }

    @Override
    public boolean hasContainerItem(ItemStack ist) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack ist) {
        return new ItemStack(ContentHandler.getItem(Names.potion), 1, 0);
    }

    @SideOnly(Side.CLIENT)
    private IIcon iconSplashOverlay;

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        iconSplashOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.potion_splash_overlay);
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int renderPass) {
        if (renderPass == 1)
            return iconSplashOverlay;
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        if (renderPass == 1)
            return Integer.parseInt(Colors.APHRODITE_COLOR, 16);
        else
            return Integer.parseInt(Colors.PURE, 16);
    }

   @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (world.isRemote)
            return ist;
        if (!player.capabilities.isCreativeMode) {
            --ist.stackSize;
        }
        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        world.spawnEntityInWorld(new EntityAttractionPotion(world, player));
        return ist;
    }

}
