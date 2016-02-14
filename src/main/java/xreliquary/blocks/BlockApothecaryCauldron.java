package xreliquary.blocks;

import lib.enderwizards.sandstone.blocks.BlockBase;
import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.reference.Names;

import java.util.List;
import java.util.Random;

@ContentInit
public class BlockApothecaryCauldron extends BlockBase {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);

    public BlockApothecaryCauldron() {
        super(Material.iron, Names.apothecary_cauldron);
        this.setHardness(1.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[]{LEVEL});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(LEVEL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collisionEntity) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collisionEntity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collisionEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collisionEntity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collisionEntity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collisionEntity);
        this.setBlockBoundsForItemRender();
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether
     * or not to render the shared face of two adjacent blocks and also whether
     * the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {return false;}

    /**
     * Triggered whenever an entity collides with this block (enters into the
     * block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity collidingEntity) {
        if (!world.isRemote) {
            TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(pos);
            if (cauldron != null)
                cauldron.handleCollidingEntity( world, pos, collidingEntity );
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            ItemStack itemstack = player.inventory.getCurrentItem();
            if (itemstack == null) {
                return true;
            } else {
                TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(pos);

                if (cauldron != null)
                    return cauldron.handleBlockActivation(world, player );
            }
        }
        return true;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
        TileEntityCauldron cauldron = (TileEntityCauldron) worldIn.getTileEntity(pos);
        if (cauldron != null) {
            return cauldron.getColorMultiplier();
        }

        return super.colorMultiplier(worldIn, pos, renderPass);
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
        if (world.rand.nextInt(20) == 1) {
            TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(pos);
            if (cauldron != null) {
                cauldron.fillWithRain(world);
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // this might destroy the universe
        return ItemBlock.getItemFromBlock(Reliquary.CONTENT.getBlock(Names.apothecary_cauldron));
    }

    /**
     * If this returns true, then comparators facing away from this block will
     * use the value from getComparatorInputOverride instead of the actual
     * redstone signal strength.
     */
    public boolean hasComparatorInputOverride() {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is
     * used instead of the redstone signal strength when this block inputs to a
     * comparator.
     */
    public int getComparatorInputOverride(World world ,BlockPos pos) {
        TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
        if (cauldron != null) {
            return cauldron.getLiquidLevel();
        }
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
        return new TileEntityCauldron();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
