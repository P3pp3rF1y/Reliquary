package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

@ContentInit
public class ItemEmperorChalice extends ItemToggleable {

    public ItemEmperorChalice()
    {
        super(Names.emperor_chalice);
        this.setCreativeTab( Reliquary.CREATIVE_TAB );
        this.setMaxDamage( 0 );
        this.setMaxStackSize( 1 );
        canRepair = false;

    }
    //TODO: put this in JSON
/*    @SideOnly(Side.CLIENT)
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
        //same as infernal, enabled == drink mode.
        if (this.isEnabled(itemStack) || renderPass != 1)
            return this.itemIcon;
        else
            return iconOverlay;
    }*/

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 16;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.DRINK;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
            return stack;

        int multiplier = (Integer) Reliquary.CONFIG.get(Names.emperor_chalice, "hunger_satiation_multiplier");
        player.getFoodStats().addStats(1, (float) (multiplier / 2));
        player.attackEntityFrom(DamageSource.drown, multiplier);
        return stack;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (player.isSneaking())
            return super.onItemRightClick(ist, world, player);
        float coeff = 1.0F;
        double xOff = player.prevPosX + (player.posX - player.prevPosX) * coeff;
        double yOff = player.prevPosY + (player.posY - player.prevPosY) * coeff + player.getEyeHeight();
        double zOff = player.prevPosZ + (player.posZ - player.prevPosZ) * coeff;
        boolean isInDrainMode = this.isEnabled(ist);
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, isInDrainMode);

        if (mop == null) {
            if (!this.isEnabled(ist)) {
                player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
            }
            return ist;
        } else {

            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

                if (!world.isBlockModifiable( player, mop.getBlockPos() ))
                    return ist;

                if (!player.canPlayerEdit(mop.getBlockPos(), mop.sideHit, ist))
                    return ist;

                if (this.isEnabled(ist)) {
                    TileEntity tile = world.getTileEntity(mop.getBlockPos());
                    if (tile instanceof IFluidHandler) {
                        //it's got infinite water.. it just drains water, nothing more.
                        FluidStack fluid = new FluidStack(FluidRegistry.WATER, 1000);
                        ((IFluidHandler) tile).drain(mop.sideHit, fluid, true);

                        return ist;
                    }
                } else {
                    TileEntity tile = world.getTileEntity(mop.getBlockPos());
                    if (tile instanceof IFluidHandler) {
                        FluidStack fluid = new FluidStack(FluidRegistry.WATER, 1000);
                        int amount = ((IFluidHandler) tile).fill(mop.sideHit, fluid, false);

                        if (amount > 0) {
                            ((IFluidHandler) tile).fill(mop.sideHit, fluid, true);
                        }

                        return ist;
                    }
                }

                if (!this.isEnabled(ist)) {
                    //TODO:verify that this vector addition works correctly instead of the old multiple if statement
                    mop.getBlockPos().add( mop.sideHit.getDirectionVec());

                    if (!player.canPlayerEdit(mop.getBlockPos(), mop.sideHit, ist))
                        return ist;

                    if (this.tryPlaceContainedLiquid(world, ist, xOff, yOff, zOff, mop.getBlockPos()))
                        return ist;

                } else {
                    String ident = ContentHelper.getIdent(world.getBlockState(mop.getBlockPos()).getBlock());
                    if ((ident.equals(ContentHelper.getIdent(Blocks.flowing_water)) || ident.equals(ContentHelper.getIdent(Blocks.water))) && world.getBlockState(mop.getBlockPos()).getValue(Blocks.water.LEVEL) == 0) {
                        world.setBlockState( mop.getBlockPos(), Blocks.air.getDefaultState() );

                        return ist;
                    }
                }
            }

            return ist;
        }
    }

    public boolean tryPlaceContainedLiquid(World world, ItemStack stack, double posX, double posY, double posZ, BlockPos pos) {
        Material material = world.getBlockState(pos).getBlock().getMaterial();
        if (this.isEnabled(stack))
            return false;
        if (!world.isAirBlock(pos) && material.isSolid())
            return false;
        else {
            if (world.provider.doesWaterVaporize()) {
                world.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                for (int var11 = 0; var11 < 8; ++var11) {
                    world.spawnParticle( EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
                }
            } else {
                world.setBlockState(pos, Blocks.flowing_water.getDefaultState(), 3);
            }

            return true;
        }
    }
}
