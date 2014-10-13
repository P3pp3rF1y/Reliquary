package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@ContentInit
public class ItemEmperorChalice extends ItemBase {

    public ItemEmperorChalice() {
        super(Names.emperor_chalice);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @SideOnly(Side.CLIENT)
    private IIcon iconOverlay;

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.emperor_chalice_overlay);
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int renderPass) {
        if (itemStack.getItemDamage() == 0 || renderPass != 1)
            return this.itemIcon;
        else
            return iconOverlay;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 16;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.drink;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public ItemStack onEaten(ItemStack ist, World world, EntityPlayer player) {
        if (world.isRemote)
            return ist;

        int multiplier = (Integer) Reliquary.CONFIG.get(Names.emperor_chalice, "hunger_satiation_multiplier");
        player.getFoodStats().addStats(1, (float) (multiplier / 2));
        player.attackEntityFrom(DamageSource.drown, multiplier);
        return new ItemStack(this, 1, 0);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        float var4 = 1.0F;
        double var5 = player.prevPosX + (player.posX - player.prevPosX) * var4;
        double var7 = player.prevPosY + (player.posY - player.prevPosY) * var4 + 1.62D - player.yOffset;
        double var9 = player.prevPosZ + (player.posZ - player.prevPosZ) * var4;
        boolean var11 = ist.getItemDamage() == 0;
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, var11);

        if (mop == null) {
            if (ist.getItemDamage() > 0) {
                player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
            }
            return ist;
        } else {

            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int var13 = mop.blockX;
                int var14 = mop.blockY;
                int var15 = mop.blockZ;

                if (!world.canMineBlock(player, var13, var14, var15))
                    return ist;

                if (!player.canPlayerEdit(var13, var14, var15, mop.sideHit, ist))
                    return ist;

                String ident = ContentHelper.getIdent(world.getBlock(var13, var14, var15));
                if ((ident.equals(ContentHelper.getIdent(Blocks.flowing_water)) || ident.equals(ContentHelper.getIdent(Blocks.water))) && world.getBlockMetadata(var13, var14, var15) == 0) {
                    world.setBlock(var13, var14, var15, Blocks.air);

                    return new ItemStack(ist.getItem(), 1, 1);
                }

                if (ist.getItemDamage() == 1) {
                    if (mop.sideHit == 0) {
                        --var14;
                    }

                    if (mop.sideHit == 1) {
                        ++var14;
                    }

                    if (mop.sideHit == 2) {
                        --var15;
                    }

                    if (mop.sideHit == 3) {
                        ++var15;
                    }

                    if (mop.sideHit == 4) {
                        --var13;
                    }

                    if (mop.sideHit == 5) {
                        ++var13;
                    }

                    if (!player.canPlayerEdit(var13, var14, var15, mop.sideHit, ist))
                        return ist;

                    if (this.tryPlaceContainedLiquid(world, ist, var5, var7, var9, var13, var14, var15) && !player.capabilities.isCreativeMode)
                        return new ItemStack(this, 1, player.isSneaking() ? 0 : 1);

                }
            }

            return ist;
        }
    }

    public boolean tryPlaceContainedLiquid(World par1World, ItemStack ist, double par2, double par4, double par6, int par8, int par9, int par10) {
        if (ist.getItemDamage() != 1)
            return false;
        else if (!par1World.isAirBlock(par8, par9, par10) && par1World.getBlock(par8, par9, par10).getMaterial().isSolid())
            return false;
        else {
            if (par1World.provider.isHellWorld) {
                par1World.playSoundEffect(par2 + 0.5D, par4 + 0.5D, par6 + 0.5D, "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                for (int var11 = 0; var11 < 8; ++var11) {
                    par1World.spawnParticle("largesmoke", par8 + Math.random(), par9 + Math.random(), par10 + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            } else {
                par1World.setBlock(par8, par9, par10, Blocks.flowing_water, 0, 3);
            }

            return true;
        }
    }


}
