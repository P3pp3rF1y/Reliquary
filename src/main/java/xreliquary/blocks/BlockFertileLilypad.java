package xreliquary.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.blocks.ICustomItemBlock;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import xreliquary.Reliquary;
import xreliquary.items.block.ItemFertileLilypad;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;
import java.util.Random;

@ContentInit
public class BlockFertileLilypad extends BlockFlower implements ICustomItemBlock {

    public BlockFertileLilypad() {
        super(0);
        float var3 = 0.5F;
        float var4 = 0.015625F;
        this.setTickRandomly(false);
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
        this.setBlockName(Names.lilypad);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.lilypad);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random par5Random) {

        this.growCropsNearby(world, x, y, z);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        world.spawnParticle("mobSpell", x + 0.5D + rand.nextGaussian() / 8, y, z + 0.5D + rand.nextGaussian() / 8, 0.0D, 0.9D, 0.5D);
    }

    public void growCropsNearby(World world, int xO, int yO, int zO) {
        int lilyPadsFound = 0;
        //int scheduleDelay = 0;
        for (int xD = -4; xD <= 4; xD++) {
            for (int yD = -1; yD <= 4; yD++) {
                for (int zD = -4; zD <= 4; zD++) {
                    int x = xO + xD;
                    int y = yO + yD;
                    int z = zO + zD;

                    double distance = Math.sqrt(Math.pow(x-xO, 2) + Math.pow(y - yO,2) + Math.pow(z - zO,2));
                    distance += 7;

                    Block block = world.getBlock(x, y, z);

                    if (block instanceof IPlantable || block instanceof IGrowable) {
                        if (!(block instanceof BlockFertileLilypad)) {
                            //68 is a completely arbitrary number to multiply the distance coefficient by
                            //it schedules the next tick. It caps out around 47 seconds, and bottoms out around 27. Both are at least as good or better than average growth.
                            world.scheduleBlockUpdate(x, y, z, block, (int)distance * 68);
                            block.updateTick(world, x, y, z, world.rand);
                        } else {
                            lilyPadsFound++;
                        }
                    }
                }
            }
        }

        //1360 is 68 seconds (roughly the average time of a block's growth tick)
        //420 is the maximum that would have to be subtracted from 1360 to get down to 47 seconds (roughly the mean average of a block's growth tick)
        //20 * the lilyPadsFound is a delay to diminish (lightly) the effect of multiple lilyPads in an area. Not a huge impact.
        world.scheduleBlockUpdate(xO, yO, zO, world.getBlock(xO, yO, zO), 1360 - world.rand.nextInt(420) + (lilyPadsFound * 20));
    }

    @Override
    public int getRenderType() {
        return 23;
    }

    @Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
        if (par7Entity == null || !(par7Entity instanceof EntityBoat)) {
            super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return AxisAlignedBB.getBoundingBox(par2 + minX, par3 + minY, par4 + minZ, par2 + maxX, par3 + maxY, par4 + maxZ);

    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == null ? false : ContentHelper.areBlocksEqual(block, Blocks.water);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return y >= 0 && y < 256 ? world.getBlock(x, y - 1, z).getMaterial() == Material.water && world.getBlockMetadata(x, y - 1, z) == 0 : false;
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
