package xreliquary.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
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

        this.setUnlocalizedName(isActive ? Names.altar : Names.altar_idle);
        this.setLightLevel(isActive ? getAltarActiveLightLevel() : 0.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    private float getAltarActiveLightLevel() {
        return (float)Reliquary.CONFIG.getInt(Names.altar, "output_light_level_while_active") / 16F;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemBlock.getItemFromBlock(Reliquary.CONTENT.getBlock(Names.altar_idle));
    }

    @Override
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!isActive)
            return;
        int worldTime = (int) (world.getWorldTime() % 24000);
        if (worldTime >= 12000)
            return;
        if (!world.canBlockSeeSky(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())))
            return;
        if (rand.nextInt(3) != 0)
            return;
        //TODO: verify particle
        world.spawnParticle( EnumParticleTypes.SPELL_MOB, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY() + 1.1D, pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float xOff, float yOff, float zOff) {
        if (isActive)
            return true;
        TileEntityAltar altar = (TileEntityAltar) world.getTileEntity(pos);
        if (altar == null)
            return true;
        if (player.getCurrentEquippedItem() == null)
            return true;
        if (player.getCurrentEquippedItem().getItem() == Items.redstone) {
            int slot = getSlotWithRedstoneDust(player);
            if (slot == -1)
                return true;
            world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, "random.fizz", 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (world.rand.nextGaussian() / 8));
            for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
                //TODO: verify particle
                world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5D + world.rand.nextGaussian() / 5, pos.getY() + 1.2D, pos.getZ() + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
            }
            if (world.isRemote)
                return true;
            player.inventory.decrStackSize(slot, 1);
            altar.addRedstone();
        } else if (player.getCurrentEquippedItem().getItem() instanceof ItemAlkahestryTome && NBTHelper.getInteger("redstone", player.getCurrentEquippedItem()) >  0) {
            world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, "random.fizz", 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (world.rand.nextGaussian() / 8));
            for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
                //TODO: verify particle
                world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5D + world.rand.nextGaussian() / 5, pos.getY() + 1.2D, pos.getZ() + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
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

    public static void updateAltarBlockState(boolean active, World world, BlockPos pos) {
        //TODO: replace sandstone logic with proper BlockState handling
        IBlockState blockState = world.getBlockState(pos);
        TileEntity te = world.getTileEntity(pos);
        if (active) {
            world.setBlockState(pos, Reliquary.CONTENT.getBlock(Names.altar).getDefaultState());
        } else {
            world.setBlockState(pos, Reliquary.CONTENT.getBlock(Names.altar_idle).getDefaultState());
        }

        //TODO: delete once tested that this is not needed anymore - setBlockState above takes care of this
        //world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        if (te != null) {
            te.validate();
            world.setTileEntity(pos, te);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
        return new TileEntityAltar();
    }

}
