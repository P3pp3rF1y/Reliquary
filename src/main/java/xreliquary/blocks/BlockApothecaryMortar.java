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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockApothecaryMortar extends BlockBase {
	private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private static final AxisAlignedBB MORTAR_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.4D, 0.75F);

	public BlockApothecaryMortar() {
		super(Material.ROCK, Names.Blocks.APOTHECARY_MORTAR, 1.5F, 2.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state,
			@Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB mask, @Nonnull List list, Entity collidingEntity) {
		//noinspection unchecked
		addCollisionBoxToList(pos, mask, list, MORTAR_AABB);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityMortar();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return MORTAR_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float xOff, float yOff, float zOff) {

		ItemStack heldItem = player.getHeldItem(hand);
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity == null || !(tileEntity instanceof TileEntityMortar))
			return false;
		TileEntityMortar mortar = (TileEntityMortar) tileEntity;

		if(heldItem.isEmpty()) {
			if(player.isSneaking()) {
				xreliquary.util.InventoryHelper.tryRemovingLastStack(mortar, world, mortar.getPos());
				return true;
			}
			boolean done = mortar.usePestle();
			world.playSound(null, pos, this.blockSoundType.getStepSound(), SoundCategory.BLOCKS, (this.blockSoundType.getVolume() + 1.0F) / 2.0F, this.blockSoundType.getPitch() * 0.8F);
			player.swingArm(hand);
			return done;
		}

		//if we're in cooldown prevent player from insta inserting essence that they just got from mortar
		if(mortar.isInCooldown() && heldItem.getItem() == ModItems.potionEssence)
			return false;

		NonNullList<ItemStack> mortarItems = mortar.getItemStacks();
		boolean putItemInSlot = false;

		for(int slot = 0; slot < mortarItems.size(); slot++) {
			ItemStack item = new ItemStack(heldItem.getItem(), 1, heldItem.getItemDamage());
			item.setTagCompound(heldItem.getTagCompound());
			if(mortarItems.get(slot).isEmpty() && mortar.isItemValidForSlot(slot, item)) {
				heldItem.shrink(1);
				mortar.setInventorySlotContents(slot, item);
				putItemInSlot = true;
				break;
			}
		}
		if(!putItemInSlot) {
			mortar.usePestle();
			world.playSound(null, pos, this.blockSoundType.getStepSound(), SoundCategory.BLOCKS, (this.blockSoundType.getVolume() + 1.0F) / 2.0F, this.blockSoundType.getPitch() * 0.8F);
			return false;
		} else {
			mortar.markDirty();
		}
		return true;
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(
			@Nonnull World world,
			@Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		// this might destroy the universe
		return ItemBlock.getItemFromBlock(ModBlocks.apothecaryMortar);
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileentity = world.getTileEntity(pos);

		if(tileentity instanceof TileEntityMortar) {
			InventoryHelper.dropInventoryItems(world, pos, (TileEntityMortar) tileentity);
		}

		super.breakBlock(world, pos, state);
	}
}
