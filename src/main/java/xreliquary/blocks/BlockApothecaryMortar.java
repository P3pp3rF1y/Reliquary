package xreliquary.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
	private static final AxisAlignedBB MORTAR_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.3D, 0.75F);

	public BlockApothecaryMortar() {
		super(Material.rock, Names.apothecary_mortar);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setHardness(1.5F);
		this.setResistance(2.0F);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List list, Entity collidingEntity) {
		addCollisionBoxToList(pos, mask, list, MORTAR_AABB);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
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

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityMortar();
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return MORTAR_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float xOff, float yOff, float zOff) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity == null || !(tileEntity instanceof TileEntityMortar))
			return false;
		TileEntityMortar mortar = (TileEntityMortar) tileEntity;
		if(heldItem == null) {
			if(player.isSneaking()) {
				xreliquary.util.InventoryHelper.tryRemovingLastStack(mortar, world, mortar.getPos());
				return true;
			}
			boolean done = mortar.usePestle();
			world.playSound(null, pos, this.stepSound.getStepSound(), SoundCategory.BLOCKS, (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
			player.swingArm(hand);
			if(done) {
				return true;
			}
			return false;
		}
		ItemStack[] mortarItems = mortar.getItemStacks();
		boolean putItemInSlot = false;
		for(int slot = 0; slot < mortarItems.length; slot++) {
			ItemStack item = new ItemStack(heldItem.getItem(), 1, heldItem.getItemDamage());
			item.setTagCompound(heldItem.getTagCompound());
			if(mortarItems[slot] == null && mortar.isItemValidForSlot(slot, item)) {
				heldItem.stackSize--;
				if(heldItem.stackSize == 0)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				mortar.setInventorySlotContents(slot, item);
				putItemInSlot = true;
				break;
			}
		}
		if(!putItemInSlot) {
			mortar.usePestle();
			world.playSound(null, pos, this.stepSound.getStepSound(), SoundCategory.BLOCKS, (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
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

		if(tileentity instanceof TileEntityMortar) {
			InventoryHelper.dropInventoryItems(world, pos, (TileEntityMortar) tileentity);
		}

		super.breakBlock(world, pos, state);
	}
}
