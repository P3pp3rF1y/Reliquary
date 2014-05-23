package xreliquary.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.items.block.ItemBlockBase;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;
import java.util.Random;

@XRInit(itemBlock = ItemBlockBase.class)
public class BlockInterdictionTorch extends BlockTorch {

    public BlockInterdictionTorch() {
        super();
        this.setBlockName(Names.interdiction_torch);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setHardness(0.0F);
        this.setLightLevel(1.0F);
        this.setTickRandomly(true);
        this.setStepSound(BlockTorch.soundTypeWood);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.interdiction_torch);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        world.scheduleBlockUpdate(x, y, z, this, tickRate());
        return super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        world.scheduleBlockUpdate(x, y, z, this, tickRate());
        if (world.isRemote)
            return;

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5));
        for(EntityLivingBase entity : entities) {
            // TODO: Add a blacklist via config option.
            if(entity instanceof IBossDisplayData || entity instanceof EntityPlayer)
                continue;
//
//            double distance = 5.0d - Math.sqrt(Math.pow(monster.posX - (x + 0.5), 2) + Math.pow(monster.posZ - (z + 0.5), 2));
//
//            if(distance <= 0)
//                continue;
//
//            double knockbackMultiplier = 1.0 + (0.32 * distance);
//
//            // TODO: Sometimes zombies break through. I think this is because of how I handle the 'monster.posZ + monster.motionZ <= z' part.
//
//            if(monster.posX + monster.motionX > monster.posX)
//                monster.motionX *= monster.posX + monster.motionX <= x ? -knockbackMultiplier : knockbackMultiplier;
//            else
//                monster.motionX *= monster.posX + monster.motionX <= x ? knockbackMultiplier : -knockbackMultiplier;
//
//            if(monster.posZ + monster.motionZ > monster.posZ)
//                monster.motionZ *= monster.posZ + monster.motionZ <= z ? -knockbackMultiplier : knockbackMultiplier;
//            else
//                monster.motionZ *= monster.posZ + monster.motionZ <= z ? knockbackMultiplier : -knockbackMultiplier;
//
//            if(monster.posX + monster.motionX == monster.posX)
//                monster.motionX = monster.posX <= x ? -knockbackMultiplier : knockbackMultiplier;
//
//            if(monster.posZ + monster.motionZ == monster.posZ)
//                monster.motionZ = monster.posZ <= z ? -knockbackMultiplier : knockbackMultiplier;
//
//            monster.moveEntity(monster.motionX, monster.motionY, monster.motionZ);

            //start x3n0's attempt to rebuild his old algorithm
            //slight offset to account for the block technically not being precisely at a whole number, positionally
            //we're intentionally ignoring y because it does wonky stuff and makes mobs float/etc.
            double sourceX = (double)x + 0.5D;
            double sourceZ = (double)z + 0.5D;

            //first we get the difference, but we also check the absolute value to minimize the effect of the
            //formula in the event of an infinitesimally small difference, which produces "Infinity" in the calculation (not even kidding)
            double xDiff = (entity.posX - sourceX);
            if (Math.abs(xDiff) < 0.25D) xDiff = xDiff < 0 ? -0.25D : 0.25D;
            double zDiff = (entity.posZ - sourceZ);
            if (Math.abs(zDiff) < 0.25D) zDiff = zDiff < 0 ? -0.25D : 0.25D;

            double xMotion = 5D / xDiff * 0.03D;
            double zMotion = 5D / zDiff * 0.03D;
            entity.motionX += xMotion;
            entity.motionZ += zMotion;
        }
    }

    public int tickRate() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int orientation = world.getBlockMetadata(x, y, z);
        double xOffset = (double)((float)x + 0.5F);
        double yOffset = (double)((float)y + 0.7F);
        double zOffset = (double)((float)z + 0.5F);
        double verticalModifier = 0.2199999988079071D;
        double horizontalModifier = 0.27000001072883606D;

        if (orientation == 1) {
            world.spawnParticle("mobSpell", xOffset - horizontalModifier, yOffset + verticalModifier, zOffset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", xOffset - horizontalModifier, yOffset + verticalModifier, zOffset, 0.0D, 0.0D, 0.0D);
        } else if (orientation == 2) {
            world.spawnParticle("mobSpell", xOffset + horizontalModifier, yOffset + verticalModifier, zOffset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", xOffset + horizontalModifier, yOffset + verticalModifier, zOffset, 0.0D, 0.0D, 0.0D);
        } else if (orientation == 3) {
            world.spawnParticle("mobSpell", xOffset, yOffset + verticalModifier, zOffset - horizontalModifier, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", xOffset, yOffset + verticalModifier, zOffset - horizontalModifier, 0.0D, 0.0D, 0.0D);
        } else if (orientation == 4) {
            world.spawnParticle("mobSpell", xOffset, yOffset + verticalModifier, zOffset + horizontalModifier, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", xOffset, yOffset + verticalModifier, zOffset + horizontalModifier, 0.0D, 0.0D, 0.0D);
        } else {
            world.spawnParticle("mobSpell", xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
        }
    }

}
