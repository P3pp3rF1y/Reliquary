package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.blocks.tile.TileEntityPedestalPassive;
import xreliquary.reference.Names;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;

public class BlockPedestalPassive extends BlockBase {
	static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
	static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private static final AxisAlignedBB PEDESTAL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.6875D, 0.875D);

	public BlockPedestalPassive() {
		this(Names.Blocks.PEDESTAL_PASSIVE);
	}
	public BlockPedestalPassive(String name) {
		super(Material.ROCK, name);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(int i = 0; i < 16; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EnumDyeColor color = EnumDyeColor.RED;

		if(worldIn.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) worldIn.getTileEntity(pos);

			//noinspection ConstantConditions
			color = pedestal.getClothColor();
		}

		return state.withProperty(COLOR, color);
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
		int meta = 0;

		if(world.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

			//noinspection ConstantConditions
			meta = pedestal.getClothColor().getMetadata();
		}

		//noinspection ConstantConditions
		return new ItemStack(Item.getItemFromBlock(this), 1, meta);
	}

	@Override
	public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		Item item = Item.getItemFromBlock(this);

		int meta = 0;
		if(world.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

			//noinspection ConstantConditions
			meta = pedestal.getClothColor().getMetadata();
		}

		drops.add(new ItemStack(item, 1, meta));
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 3);
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, COLOR);
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(
			@Nonnull World world,
			@Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if(worldIn.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) worldIn.getTileEntity(pos);

			//noinspection ConstantConditions
			pedestal.setColor(state.getValue(COLOR));
		}
	}

	@Override
	public String getHarvestTool(@Nonnull IBlockState state) {
		return "pickaxe";
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		ItemStack heldItem = player.getHeldItem(hand);
		if(world.isRemote)
			return !heldItem.isEmpty() || player.isSneaking();

		if(!(world.getTileEntity(pos) instanceof TileEntityPedestalPassive))
			return false;

		TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

		if(heldItem.isEmpty()) {
			if(player.isSneaking()) {
				//noinspection ConstantConditions
				pedestal.removeLastPedestalStack();
				return true;
			} else {
				return false;
			}
		} else {
			return InventoryHelper.getItemHandlerFrom(pedestal)
					.map(itemHandler -> InventoryHelper.tryAddingPlayerCurrentItem(player, itemHandler, EnumHand.MAIN_HAND)).orElse(false);
		}
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

		if(pedestal != null) {
			pedestal.dropPedestalInventory();
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
		//If it will harvest, delay deletion of the block until after getDrops
		return willHarvest || super.removedByPlayer(state, world, pos, player, false);
	}

	@Override
	public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, TileEntity te, ItemStack tool) {
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityPedestalPassive();
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PEDESTAL_AABB;
	}

	@SubscribeEvent
	public void onRightClicked(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();

		//should only really use the event in case that the player is sneaking with something in offhand and empty mainhand
		if(!player.isSneaking() || !player.getHeldItemMainhand().isEmpty() || player.getHeldItemOffhand().isEmpty())
			return;

		Block block = player.world.getBlockState(event.getPos()).getBlock();
		if(block != this)
			return;

		TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) player.world.getTileEntity(event.getPos());

		if(pedestal != null) {
			pedestal.removeLastPedestalStack();
		}

		event.setCanceled(true);
	}
}
