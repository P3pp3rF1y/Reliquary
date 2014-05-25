package xreliquary.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ContentHandler;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;

import java.util.List;
import java.util.Random;

@XRInit
public class BlockApothecaryCauldron extends BlockContainer {
	public BlockApothecaryCauldron(boolean par1) {
		super(Material.iron);

        this.setHardness(1.5F);
        this.setResistance(5.0F);

        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    private IIcon innerTexture;

    @SideOnly(Side.CLIENT)
    private IIcon topTexture;

    @SideOnly(Side.CLIENT)
    private IIcon bottomTexture;

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.topTexture : (side == 0 ? this.bottomTexture : this.blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.innerTexture = iconRegister.registerIcon(this.getTextureName() + "_" + "inner");
        this.topTexture = iconRegister.registerIcon(this.getTextureName() + "_top");
        this.bottomTexture = iconRegister.registerIcon(this.getTextureName() + "_" + "bottom");
        this.blockIcon = iconRegister.registerIcon(this.getTextureName() + "_side");
    }

    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List cbList, Entity collisionEntity)
    {
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


    //called by the renderer to get the texture in a static method.
    @SideOnly(Side.CLIENT)
    public static IIcon getCauldronIcon(String textureName)
    {
        BlockApothecaryCauldron cauldronStatic = (BlockApothecaryCauldron)ContentHandler.getBlock(Names.apothecary_cauldron);
        return textureName.equals("inner") ? cauldronStatic.innerTexture : (textureName.equals("bottom") ? cauldronStatic.bottomTexture : null);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 24;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collidingEntity)
    {
        int l = func_150027_b(world.getBlockMetadata(x, y, z));
        float f = (float)y + (6.0F + (float)(3 * l)) / 16.0F;

        if (!world.isRemote && collidingEntity.isBurning() && l > 0 && collidingEntity.boundingBox.minY <= (double)f)
        {
            collidingEntity.extinguish();
            this.setMetaData(world, x, y, z, l - 1);
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (p_149727_1_.isRemote)
        {
            return true;
        }
        else
        {
            ItemStack itemstack = p_149727_5_.inventory.getCurrentItem();

            if (itemstack == null)
            {
                return true;
            }
            else
            {
                int i1 = p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_);
                int j1 = func_150027_b(i1);

                if (itemstack.getItem() == Items.water_bucket)
                {
                    if (j1 < 3)
                    {
                        if (!p_149727_5_.capabilities.isCreativeMode)
                        {
                            p_149727_5_.inventory.setInventorySlotContents(p_149727_5_.inventory.currentItem, new ItemStack(Items.bucket));
                        }

                        this.setMetaData(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, 3);
                    }

                    return true;
                }
                else
                {
                    if (itemstack.getItem() == Items.glass_bottle)
                    {
                        if (j1 > 0)
                        {
                            if (!p_149727_5_.capabilities.isCreativeMode)
                            {
                                ItemStack itemstack1 = new ItemStack(Items.potionitem, 1, 0);

                                if (!p_149727_5_.inventory.addItemStackToInventory(itemstack1))
                                {
                                    p_149727_1_.spawnEntityInWorld(new EntityItem(p_149727_1_, (double)p_149727_2_ + 0.5D, (double)p_149727_3_ + 1.5D, (double)p_149727_4_ + 0.5D, itemstack1));
                                }
                                else if (p_149727_5_ instanceof EntityPlayerMP)
                                {
                                    ((EntityPlayerMP)p_149727_5_).sendContainerToPlayer(p_149727_5_.inventoryContainer);
                                }

                                --itemstack.stackSize;

                                if (itemstack.stackSize <= 0)
                                {
                                    p_149727_5_.inventory.setInventorySlotContents(p_149727_5_.inventory.currentItem, (ItemStack)null);
                                }
                            }

                            this.setMetaData(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, j1 - 1);
                        }
                    }
                    else if (j1 > 0 && itemstack.getItem() instanceof ItemArmor && ((ItemArmor)itemstack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.CLOTH)
                    {
                        ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                        itemarmor.removeColor(itemstack);
                        this.setMetaData(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, j1 - 1);
                        return true;
                    }

                    return false;
                }
            }
        }
    }

    //does stuff with the metadata
    public void setMetaData(World world, int x, int y, int z, int meta)
    {
        world.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(meta, 0, 3), 2);
        //no clue what this is
        world.func_147453_f(x, y, z, this);
    }

    @Override
    public void fillWithRain(World world, int x, int y, int z)
    {
        if (world.rand.nextInt(20) == 1)
        {
            int l = world.getBlockMetadata(x, y, z);

            if (l < 3)
            {
                world.setBlockMetadataWithNotify(x, y, z, l + 1, 2);
            }
        }
    }

    //public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    //{
        //TODO does this seriously need to be here? It should drop itself by default.
    //}

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Items.cauldron;
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        int i1 = p_149736_1_.getBlockMetadata(p_149736_2_, p_149736_3_, p_149736_4_);
        return func_150027_b(i1);
    }

    public static int func_150027_b(int p_150027_0_)
    {
        return p_150027_0_;
    }

    @SideOnly(Side.CLIENT)
    public static float getRenderLiquidLevel(int p_150025_0_)
    {
        int j = MathHelper.clamp_int(p_150025_0_, 0, 3);
        return (float)(6 + 3 * j) / 16.0F;
    }
	@Override
	public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
		return new TileEntityCauldron();
	}

}
