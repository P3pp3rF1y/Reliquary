package xreliquary.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;

import java.util.List;
import java.util.Random;

public class BlockApothecaryMortar extends BlockBase {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockApothecaryMortar() {
        super(Material.rock, Names.apothecary_mortar);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setHardness(1.5F);
        this.setResistance(2.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.3125F, 0.75F);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.3125F, 0.75F);

        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        this.setBlockBoundsForItemRender();
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.3F, 0.75F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 2;
    }

    //TODO move to inventory helper
    private void tryRemovingLastStack(IInventory inventory, World worldObj, BlockPos pos) {
        for (int i = inventory.getSizeInventory() - 1; i >= 0; i--) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                inventory.setInventorySlotContents(i, null);
                if (worldObj.isRemote)
                    return;
                inventory.markDirty();
                EntityItem itemEntity = new EntityItem(worldObj, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                worldObj.spawnEntityInWorld(itemEntity);
                break;
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float xOff, float yOff, float zOff) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null || !(tileEntity instanceof TileEntityMortar))
            return false;
        TileEntityMortar mortar = (TileEntityMortar) tileEntity;
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) {
            if (player.isSneaking()) {
                tryRemovingLastStack(mortar, world, mortar.getPos());
                return true;
            }
            boolean done = mortar.usePestle();
            world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.stepSound.getStepSound(), (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getFrequency() * 0.8F);
            player.swingItem();
            if (done) {
                return true;
            }
            return false;
        }
        ItemStack[] mortarItems = mortar.getItemStacks();
        boolean putItemInSlot = false;
        for (int slot = 0; slot < mortarItems.length; slot++) {
            ItemStack item = new ItemStack(player.getCurrentEquippedItem().getItem(), 1, player.getCurrentEquippedItem().getItemDamage());
            item.setTagCompound(player.getCurrentEquippedItem().getTagCompound());
            if (mortarItems[slot] == null && mortar.isItemValidForSlot(slot, item)) {
                player.getCurrentEquippedItem().stackSize--;
                if (player.getCurrentEquippedItem().stackSize == 0)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                mortar.setInventorySlotContents(slot, item);
                putItemInSlot = true;
                break;
            }
        }
        if (!putItemInSlot) {
            mortar.usePestle();
            world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.stepSound.getStepSound(), (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getFrequency() * 0.8F);
            return false;
        } else {
            //TODO: make sure to optimize markDirty calls
            mortar.markDirty();
        }
        return true;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // this might destroy the universe
        return ItemBlock.getItemFromBlock(ModBlocks.apothecaryMortar);
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return ItemBlock.getItemFromBlock(ModBlocks.apothecaryMortar);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityMortar) {
            InventoryHelper.dropInventoryItems(world, pos, (TileEntityMortar) tileentity);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
        return new TileEntityMortar();
    }

}
