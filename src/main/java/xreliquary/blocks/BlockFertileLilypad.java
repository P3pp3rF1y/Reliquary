package xreliquary.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFertileLilypad extends BlockFlower {
    protected BlockFertileLilypad(int par1) {
        super(par1, Material.plants);
        float var3 = 0.5F;
        float var4 = 0.015625F;
        this.setTickRandomly(true);
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4,
                0.5F + var3);
        this.setUnlocalizedName(Names.LILYPAD_NAME);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.LILYPAD_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
        return blockIcon;
    }

    // the update tick for this block, rather than growing, forces stuff around
    // it to grow.
    @Override
    public void updateTick(World par1World, int x, int y, int z,
            Random par5Random) {
        this.growCropsNearby(par1World, x, y, z);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        world.spawnParticle("mobSpell", x + 0.5D + rand.nextGaussian() / 8, y,
                z + 0.5D + rand.nextGaussian() / 8, 0.0D, 0.9D, 0.5D);
    }

    public void growCropsNearby(World world, int xO, int yO, int zO) {
        for (int xD = -4; xD <= 4; xD++) {
            for (int yD = -1; yD <= 4; yD++) {
                for (int zD = -4; zD <= 4; zD++) {
                    int x = xO + xD;
                    int y = yO + yD;
                    int z = zO + zD;
                    int id = world.getBlockId(x, y, z);
                    Block block = Block.blocksList[id];

                    if (id == blockID) {
                        continue;
                    }

                    if (block instanceof IPlantable) {
                        block.updateTick(world, x, y, z, world.rand);
                    }
                }
            }
        }
    }

    @Override
    public int getRenderType() {
        return 23;
    }

    @Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3,
            int par4, AxisAlignedBB par5AxisAlignedBB, List par6List,
            Entity par7Entity) {
        if (par7Entity == null || !(par7Entity instanceof EntityBoat)) {
            super.addCollisionBoxesToList(par1World, par2, par3, par4,
                    par5AxisAlignedBB, par6List, par7Entity);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World,
            int par2, int par3, int par4) {
        return AxisAlignedBB.getAABBPool().getAABB(par2 + minX, par3 + minY,
                par4 + minZ, par2 + maxX, par3 + maxY, par4 + maxZ);

    }

    @Override
    protected boolean canThisPlantGrowOnThisBlockID(int par1) {
        return par1 == Block.waterStill.blockID;
    }

    @Override
    public boolean canBlockStay(World par1World, int par2, int par3, int par4) {
        return par3 >= 0 && par3 < 256 ? par1World.getBlockMaterial(par2,
                par3 - 1, par4) == Material.water
                && par1World.getBlockMetadata(par2, par3 - 1, par4) == 0
                : false;
    }
}
