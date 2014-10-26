package xreliquary.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.Random;

public class BlockAlkahestryAltar extends BlockContainer {

    @ContentInit
    static public class BlockActiveAlkahestryAltar extends BlockAlkahestryAltar {
        public BlockActiveAlkahestryAltar() {
            super(true);
        }
    }

    @ContentInit
    static public class BlockIdleAlkahestryAltar extends BlockAlkahestryAltar {
        public BlockIdleAlkahestryAltar() {
            super(false);
        }
    }

    private final boolean isActive;

    public BlockAlkahestryAltar(boolean par1) {
        super(Material.rock);
        isActive = par1;

        this.setHardness(1.5F);
        this.setResistance(5.0F);

        this.setBlockName(isActive ? Names.altar : Names.altar_idle);
        this.setLightLevel(isActive ? 1.0F : 0.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    private static IIcon icons[];

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[isActive ? 1 : 0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[2];
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.altar_idle);
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.altar);
    }

    @Override
    public Item getItemDropped(int par1, Random random, int par3) {
        return ItemBlock.getItemFromBlock(ContentHandler.getBlock(Names.altar_idle));
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (!isActive)
            return;
        int worldTime = (int) (world.getWorldTime() % 24000);
        if (worldTime >= 12000)
            return;
        if (!world.canBlockSeeTheSky(x, y + 1, z))
            return;
        if (rand.nextInt(3) != 0)
            return;
        world.spawnParticle("mobSpell", x + 0.5D + rand.nextGaussian() / 8, y + 1.1D, z + 0.5D + rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOff, float yOff, float zOff) {
        if (isActive)
            return true;
        TileEntityAltar altar = (TileEntityAltar) world.getTileEntity(x, y, z);
        if (altar == null)
            return true;
        if (player.getCurrentEquippedItem() == null)
            return true;
        if (player.getCurrentEquippedItem().getItem() == Items.redstone) {
            int slot = getSlotWithRedstoneDust(player);
            if (slot == -1)
                return true;
            world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (world.rand.nextGaussian() / 8));
            for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
                world.spawnParticle("reddust", x + 0.5D + world.rand.nextGaussian() / 5, y + 1.2D, z + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
            }
            if (world.isRemote)
                return true;
            player.inventory.decrStackSize(slot, 1);
            altar.addRedstone();
        } else if (player.getCurrentEquippedItem().getItem() instanceof ItemAlkahestryTome && NBTHelper.getInteger("redstone", player.getCurrentEquippedItem()) >  0) {
            world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (world.rand.nextGaussian() / 8));
            for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
                world.spawnParticle("reddust", x + 0.5D + world.rand.nextGaussian() / 5, y + 1.2D, z + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
            }
            if (world.isRemote)
                return true;
            NBTHelper.setInteger("redstone", player.getCurrentEquippedItem(), NBTHelper.getInteger("redstone", player.getCurrentEquippedItem()) - 1);
            altar.addRedstone();
        }
        return true;
    }

    private int getSlotWithRedstoneDust(EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() == Items.redstone)
                return slot;
        }
        return -1;
    }

    public static void updateAltarBlockState(boolean active, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        if (active) {
            world.setBlock(x, y, z, ContentHandler.getBlock(Names.altar));
        } else {
            world.setBlock(x, y, z, ContentHandler.getBlock(Names.altar_idle));
        }

        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        if (te != null) {
            te.validate();
            world.setTileEntity(x, y, z, te);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
        return new TileEntityAltar();
    }

}
