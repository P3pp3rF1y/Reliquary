package xreliquary.blocks;

import lib.enderwizards.sandstone.blocks.ICustomItemBlock;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.items.block.ItemFertileLilypad;
import xreliquary.lib.Names;

import java.util.List;
import java.util.Random;

//TODO: verify that newly changing this to inherit from BlockBush doesn't break it
@ContentInit
public class BlockFertileLilypad extends BlockBush implements ICustomItemBlock {
//TODO: add json models
    public BlockFertileLilypad() {
        float var3 = 0.5F;
        float var4 = 0.015625F;
        this.setTickRandomly(false);
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
        this.setUnlocalizedName(Names.lilypad);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random par5Random) {

        this.growCropsNearby(world, pos, state);
    }

    @Override
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        world.spawnParticle(EnumParticleTypes.SPELL_MOB, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY(), pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.0D, 0.9D, 0.5D);
    }

    private int secondsBetweenGrowthTicks() {
        return Reliquary.CONFIG.getInt(Names.lilypad, "seconds_between_growth_ticks");
    }

    private int tileRange() {
        return Reliquary.CONFIG.getInt(Names.lilypad, "tile_range");
    }

    private int fullPotencyRange() {
        return Reliquary.CONFIG.getInt(Names.lilypad, "full_potency_range");
    }

    public void growCropsNearby(World world, BlockPos pos, IBlockState state) {
        int xO = pos.getX();
        int yO = pos.getY();
        int zO = pos.getZ();

        for (int xD = -tileRange(); xD <= tileRange(); xD++) {
            for (int yD = -1; yD <= tileRange(); yD++) {
                for (int zD = -tileRange(); zD <= tileRange(); zD++) {
                    int x = xO + xD;
                    int y = yO + yD;
                    int z = zO + zD;

                    double distance = Math.sqrt(Math.pow(x-xO, 2) + Math.pow(y - yO,2) + Math.pow(z - zO,2));
                    distance -= fullPotencyRange();
                    distance = Math.min(1D, distance);
                    double distanceCoefficient = 1D - (distance / tileRange());

                    IBlockState cropState = world.getBlockState(new BlockPos(x, y, z));
                    Block cropBlock = state.getBlock();

                    if (cropBlock instanceof IPlantable || cropBlock instanceof IGrowable) {
                        if (!(cropBlock instanceof BlockFertileLilypad)) {
                            //it schedules the next tick.
                            //TODO:verify that we're setting block update priority correctly
                            world.scheduleBlockUpdate(new BlockPos(x, y, z), cropBlock, (int) (distanceCoefficient * (float) secondsBetweenGrowthTicks() * 20F), 1);
                            cropBlock.updateTick(world, new BlockPos(x, y, z), cropState, world.rand);
                        }
                    }
                }
            }
        }
        world.scheduleBlockUpdate(pos, state.getBlock(), secondsBetweenGrowthTicks() * 20, 1);
    }

    @Override
    public int getRenderType() {
        return 23;
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        if (collidingEntity == null || !(collidingEntity instanceof EntityBoat)) {
            super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        }
    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block != null && ContentHelper.areBlocksEqual(block, Blocks.water);
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState blockBelowState = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
        return pos.getY() >= 0 && pos.getY() < 256
                && blockBelowState.getBlock().getMaterial() == Material.water
                && blockBelowState.getValue(BlockLiquid.LEVEL) == 0;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1));
    }

    @Override
    public Class<? extends ItemBlock> getCustomItemBlock() {
        return ItemFertileLilypad.class;
    }
}
