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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.render.RenderApothecaryCauldron;
import xreliquary.client.render.RenderApothecaryMortar;
import xreliquary.init.ContentHandler;
import xreliquary.init.XRInit;
import xreliquary.items.block.ItemBlockBase;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;
import java.util.Random;

@XRInit(itemBlock = ItemBlockBase.class)
public class BlockApothecaryMortar extends BlockContainer {

	public BlockApothecaryMortar() {
		super(Material.rock);

        this.setHardness(1.5F);
        this.setResistance(2.0F);

        this.setBlockName(Names.apothecary_mortar);

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
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? this.topTexture : (side == 0 ? this.bottomTexture : this.blockIcon);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.innerTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_mortar + "_" + "inner");
        this.insideTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_mortar + "_" + "inside");
        this.topTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_mortar + "_top");
        this.bottomTexture = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_mortar + "_" + "bottom");
        this.blockIcon = iconRegister.registerIcon(Reference.MOD_ID + ":" + Names.apothecary_mortar + "_side");
    }

    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List cbList, Entity collisionEntity)
    {
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.3125F, 0.75F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        this.setBlockBoundsForItemRender();
    }


    //called by the renderer to get the texture in a static method.
    @SideOnly(Side.CLIENT)
    public static IIcon getMortarIcon(String textureName)
    {
        BlockApothecaryMortar mortarStatic = (BlockApothecaryMortar)ContentHandler.getBlock(Names.apothecary_mortar);
        return textureName.equals("inner") ? mortarStatic.innerTexture : (textureName.equals("bottom") ? mortarStatic.bottomTexture : textureName.equals("inside") ? mortarStatic.insideTexture : null);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.25F, 0.75F);
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
        return -1;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metaMaybe, float playerX, float playerY, float playerZ)
    {
        //do things
        return true;
    }

    @Override
    public Item getItemDropped(int someInt, Random unusedRandom, int fortuneEnchantLevelIThink)
    {
        //this might destroy the universe
        return ItemBlock.getItemFromBlock(ContentHandler.getBlock(Names.apothecary_mortar));
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return ItemBlock.getItemFromBlock(ContentHandler.getBlock(Names.apothecary_mortar));
    }

	@Override
	public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
		return new TileEntityMortar();
	}

}
