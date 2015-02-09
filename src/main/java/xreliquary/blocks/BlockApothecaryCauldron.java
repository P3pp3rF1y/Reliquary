package xreliquary.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.blocks.BlockBase;
import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.client.render.RenderApothecaryCauldron;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;
import java.util.Random;

@ContentInit
public class BlockApothecaryCauldron extends BlockBase {

    public BlockApothecaryCauldron() {
        super(Material.iron, Names.apothecary_cauldron);
        this.setHardness(1.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    private IIcon innerTexture;

    @SideOnly(Side.CLIENT)
    private IIcon insideTexture;

    @SideOnly(Side.CLIENT)
    private IIcon topTexture;

    @SideOnly(Side.CLIENT)
    private IIcon bottomTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon waterTexture;

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.topTexture : (side == 0 ? this.bottomTexture : this.blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.innerTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_cauldron + "_" + "inner");
        this.insideTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_cauldron + "_" + "inside");
        this.topTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_cauldron + "_top");
        this.bottomTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_cauldron + "_" + "bottom");
        this.blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_cauldron + "_side");
        this.waterTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.cauldron_water);
    }

    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List cbList, Entity collisionEntity) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        this.setBlockBoundsForItemRender();
    }

    // called by the renderer to get the texture in a static method.
    @SideOnly(Side.CLIENT)
    public static IIcon getCauldronIcon(String textureName) {
        BlockApothecaryCauldron cauldronStatic = (BlockApothecaryCauldron) Reliquary.CONTENT.getBlock(Names.apothecary_cauldron);
        return textureName.equals("inner") ? cauldronStatic.innerTexture : (textureName.equals("bottom") ? cauldronStatic.bottomTexture : textureName.equals("inside") ? cauldronStatic.insideTexture : null);
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

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType() {
        return RenderApothecaryCauldron.renderID;
    }

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
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collidingEntity) {
        int l = world.getBlockMetadata(x, y, z);
        float f = (float) y + (6.0F + (float) (3 * l)) / 16.0F;

        TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(x, y, z);
        if (!world.isRemote && collidingEntity.boundingBox.minY <= (double) f) {
            if (collidingEntity.isBurning() && l > 0) {
                collidingEntity.extinguish();
                //this.setMetaData(world, x, y, z, l - 1);
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metaMaybe, float playerX, float playerY, float playerZ) {
        if (world.isRemote) {
            return true;
        } else {
            ItemStack itemstack = player.inventory.getCurrentItem();
            TileEntityCauldron cauldron = (TileEntityCauldron)world.getTileEntity(x, y, z);

            if (itemstack == null) {
                return true;
            } else {
                int j1 = world.getBlockMetadata(x, y, z);

                if (itemstack.getItem() == Items.water_bucket) {
                    if (j1 < 3) {
                        if (!player.capabilities.isCreativeMode) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                        }

                        this.setMetaData(world, x, y, z, 3);
                        cauldron.cookTime = 0;
                    }

                    return true;
                } else {
                    if (itemstack.getItem() == Reliquary.CONTENT.getItem(Names.potion) && (itemstack.getTagCompound() == null || !itemstack.getTagCompound().getBoolean("hasPotion"))) {
                        if (j1 > 0) {

                            if (cauldron.finishedCooking()) {
                                ItemStack potion = new ItemStack(Reliquary.CONTENT.getItem(Names.potion), 1, 0);
                                potion.setTagCompound(cauldron.removeContainedPotion());

                                --itemstack.stackSize;

                                if (itemstack.stackSize <= 0) {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, potion);
                                } else if (!player.inventory.addItemStackToInventory(potion)) {
                                    world.spawnEntityInWorld(new EntityItem(world, (double) x + 0.5D, (double) y + 1.5D, (double) z + 0.5D, potion));
                                }

                                this.setMetaData(world, x, y, z, j1 - 1);

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
    public void setMetaData(World world, int x, int y, int z, int meta) {
        world.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(meta, 0, 3), 2);
        // no clue what this is
        world.func_147453_f(x, y, z, this);
    }

    @Override
    public void fillWithRain(World world, int x, int y, int z) {
        if (world.rand.nextInt(20) == 1) {
            int l = world.getBlockMetadata(x, y, z);

            if (l < 3) {
                world.setBlockMetadataWithNotify(x, y, z, l + 1, 2);
            }
        }
    }

    @Override
    public Item getItemDropped(int someInt, Random unusedRandom, int fortuneEnchantLevelIThink) {
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
    public int getComparatorInputOverride(World world, int x, int y, int z, int noClueWhatThisIntIs) {
        int meta = world.getBlockMetadata(x, y, z);
        // lol at this wasted cycle.
        return meta;
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
    public boolean hasTileEntity(int metadata) {
        return true;
    }
}
