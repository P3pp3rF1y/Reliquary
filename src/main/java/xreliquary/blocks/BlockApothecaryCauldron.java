package xreliquary.blocks;

import lib.enderwizards.sandstone.blocks.BlockBase;
import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.lib.Names;

import java.util.List;
import java.util.Random;

@ContentInit
public class BlockApothecaryCauldron extends BlockBase {

    //TODO: add icon in xreliquary:items/apothecary_cauldron

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);

    public BlockApothecaryCauldron() {
        super(Material.iron, Names.apothecary_cauldron);
        this.setHardness(1.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
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
    public boolean isOpaqueCube() {
        return false;
    }

    //TODO: fix with rendering pass
/*
    */
/**
     * The type of render function that is called for this block
     *//*

    public int getRenderType() {
        return RenderApothecaryCauldron.renderID;
    }
*/

    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the
     * block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity collidingEntity) {
        int l = state.getValue(LEVEL);
        float f = (float) pos.getY() + (6.0F + (float) (3 * l)) / 16.0F;

        TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(pos);
        //TODO: verify that entityBoundingBox is the correct one to use here
        if (!world.isRemote && collidingEntity.getEntityBoundingBox().minY <= (double) f) {
            if (collidingEntity.isBurning() && l > 0) {
                collidingEntity.extinguish();
                //this.setLiquidLevel(world, x, y, z, l - 1);
            }
            if (collidingEntity instanceof EntityLivingBase) {
                if (cauldron == null || cauldron.potionEssence == null)
                    return;
                for (PotionEffect effect : cauldron.potionEssence.getEffects()) {
                    Potion potion = Potion.potionTypes[effect.getPotionID()];
                    if (potion.isInstant() && world.getWorldTime() % 20 != 0)
                        continue;
                    PotionEffect reducedEffect = new PotionEffect(effect.getPotionID(), potion.isInstant() ? 1 : effect.getDuration() / 20, Math.max(0, effect.getAmplifier() - 1));
                    ((EntityLivingBase) collidingEntity).addPotionEffect(reducedEffect);
                }
                if (cauldron.cookTime > 0 && world.getWorldTime() % 20 != 0) {
                    collidingEntity.attackEntityFrom(DamageSource.inFire, 1.0F);
                }
            }
        

            if (collidingEntity instanceof EntityItem) {
                ItemStack item = ((EntityItem) collidingEntity).getEntityItem();
                while (cauldron.isItemValidForInput(item)) {
                    
                    cauldron.addItem(item);
                    if (--item.stackSize < 1)
                        collidingEntity.setDead();
                }
            }
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
            TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(pos);

            if (itemstack == null) {
                return true;
            } else {
                int liquidLevel = state.getValue(LEVEL);

                if (itemstack.getItem() == Items.water_bucket) {
                    if (liquidLevel < 3) {
                        if (!player.capabilities.isCreativeMode) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                        }

                        this.setLiquidLevel(world, pos, state, 3);
                        cauldron.cookTime = 0;
                    }

                    return true;
                } else {
                    if (itemstack.getItem() == Reliquary.CONTENT.getItem(Names.potion) && (itemstack.getTagCompound() == null || !itemstack.getTagCompound().getBoolean("hasPotion"))) {
                        if (liquidLevel > 0) {

                            if (cauldron.finishedCooking()) {
                                ItemStack potion = new ItemStack(Reliquary.CONTENT.getItem(Names.potion), 1, 0);
                                potion.setTagCompound(cauldron.removeContainedPotion());

                                --itemstack.stackSize;

                                if (itemstack.stackSize <= 0) {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, potion);
                                } else if (!player.inventory.addItemStackToInventory(potion)) {
                                    world.spawnEntityInWorld(new EntityItem(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.5D, (double) pos.getZ() + 0.5D, potion));
                                }

                                this.setLiquidLevel(world, pos, state, liquidLevel - 1);

                            }
                        }
                    } else if (cauldron.isItemValidForInput(itemstack)) {
                        cauldron.addItem(itemstack);

                        --itemstack.stackSize;
                        if (itemstack.stackSize <= 0)
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                    return false;
                }
            }
        }
    }

    // does stuff with the metadata
    public void setLiquidLevel(World world, BlockPos pos, IBlockState state, int fluidLevel) {
        world.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp_int(fluidLevel, 0, 3))), 2);
        // no clue what this is
        world.updateComparatorOutputLevel(pos, this);
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
        //TODO: verify that this is what we want to do when there's potion inside cauldron
        if (world.rand.nextInt(20) == 1) {
            IBlockState blockState = world.getBlockState(pos);

            if (blockState.getValue(LEVEL) < 3) {
                world.setBlockState(pos, blockState.cycleProperty(LEVEL), 2);
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // this might destroy the universe
        return ItemBlock.getItemFromBlock(Reliquary.CONTENT.getBlock(Names.apothecary_cauldron));
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
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
        return world.getBlockState(pos).getValue(LEVEL);
    }

    @SideOnly(Side.CLIENT)
    public static float getRenderLiquidLevel(int liquidMeta) {
        int j = MathHelper.clamp_int(liquidMeta, 0, 3);
        return (float) (6 + 3 * j) / 16.0F;
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
