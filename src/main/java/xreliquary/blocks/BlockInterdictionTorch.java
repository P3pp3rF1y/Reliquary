package xreliquary.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.blocks.ICustomItemBlock;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ContentInit
public class BlockInterdictionTorch extends BlockTorch implements ICustomItemBlock {

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
        int radius = Reliquary.CONFIG.getInt(Names.interdiction_torch, "push_radius");

        List<String> entitiesThatCanBePushed = (List<String>) Reliquary.CONFIG.get(Names.interdiction_torch, "entities_that_can_be_pushed");
        List<String> projectilesThatCanBePushed = (List<String>) Reliquary.CONFIG.get(Names.interdiction_torch, "projectiles_that_can_be_pushed");


        List<Entity> entities = (List<Entity>) world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius));
        for (Entity entity : entities) {
            // TODO: Add a blacklist via config option.
            if (entity instanceof EntityPlayer)
                continue;
            Class entityClass = entity.getClass();
            String entityName = (String)EntityList.classToStringMapping.get(entityClass);
            if (entitiesThatCanBePushed.contains(entityName) || (projectilesThatCanBePushed.contains(entityName) && Reliquary.CONFIG.getBool(Names.interdiction_torch, "can_push_projectiles"))) {
                double distance = entity.getDistance((double) x, (double) y, (double) z);
                if (distance >= radius || distance == 0)
                    continue;

                // the multiplier is based on a set rate added to an inverse
                // proportion to the distance.
                // we raise the distance to 1 if it's less than one, or it becomes a
                // crazy multiplier we don't want/need.
                if (distance < 1D)
                    distance = 1D;
                double knockbackMultiplier = 1D + (1D / distance);

                // we also need a reduction coefficient because the above force is
                // WAY TOO MUCH to apply every tick.
                double reductionCoefficient = 0.04D;

                // the resultant vector between the two 3d coordinates is the
                // difference of each coordinate pair
                // note that we do not add 0.5 to the y coord, if we wanted to be
                // SUPER accurate, we would be using
                // the entity height offset to find its "center of mass"
                Vec3 angleOfAttack = Vec3.createVectorHelper(entity.posX - (x + 0.5D), entity.posY - y, entity.posZ - (z + 0.5D));

                // we use the resultant vector to determine the force to apply.
                double xForce = angleOfAttack.xCoord * knockbackMultiplier * reductionCoefficient;
                double yForce = angleOfAttack.yCoord * knockbackMultiplier * reductionCoefficient;
                double zForce = angleOfAttack.zCoord * knockbackMultiplier * reductionCoefficient;
                entity.motionX += xForce;
                entity.motionY += yForce;
                entity.motionZ += zForce;
            }
        }
    }

    public int tickRate() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int orientation = world.getBlockMetadata(x, y, z);
        double xOffset = (double) ((float) x + 0.5F);
        double yOffset = (double) ((float) y + 0.7F);
        double zOffset = (double) ((float) z + 0.5F);
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

    @Override
    public Class<? extends ItemBlock> getCustomItemBlock() {
        return ItemBlockBase.class;
    }

}
