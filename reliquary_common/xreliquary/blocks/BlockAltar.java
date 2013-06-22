package xreliquary.blocks;

import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAltar extends BlockContainer {
	private final boolean isActive;

	protected BlockAltar(int par1, boolean f) {
		super(par1, Material.rock);
		isActive = f;
		blockHardness = 1.5F;
		blockResistance = 5.0F;
		this.setUnlocalizedName(f ? Names.ALTAR_ACTIVE_NAME : Names.ALTAR_IDLE_NAME);
		this.setCreativeTab(Reliquary.tabsXR);
		this.setLightValue(f ? 1.0F : 0.0F);
	}

	@SideOnly(Side.CLIENT)
	private static Icon icons[];

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		return icons[isActive ? 1 : 0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		icons = new Icon[2];
		icons[0] = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.ALTAR_IDLE_NAME);
		icons[1] = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.ALTAR_ACTIVE_NAME);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return XRBlocks.altarIdle.blockID;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (!isActive) return;
		int worldTime = (int)(world.getWorldTime() % 24000);
		if (worldTime >= 12000) return;
		if (!world.canBlockSeeTheSky(x, y + 1, z)) return;
		if (rand.nextInt(3) != 0) return;
		world.spawnParticle("mobSpell", x + 0.5D + rand.nextGaussian() / 8, y + 1.1D, z + 0.5D + rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOff, float yOff, float zOff) {
		if (isActive) return true;
		TEAltar altar = (TEAltar)world.getBlockTileEntity(x, y, z);
		if (altar == null) return true;
		if (player.getCurrentEquippedItem() == null) return true;
		if (player.getCurrentEquippedItem().getItem() == Item.redstone) {
			int slot = getSlotWithRedstoneDust(player);
			if (slot == -1) return true;
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float)(world.rand.nextGaussian() / 8));
			for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
				world.spawnParticle("reddust", x + 0.5D + world.rand.nextGaussian() / 5, y + 1.2D, z + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
			}
			if (world.isRemote) return true;
			player.inventory.decrStackSize(slot, 1);
			altar.addRedstone();
		}
		return true;
	}

	private int getSlotWithRedstoneDust(EntityPlayer player) {
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if (player.inventory.mainInventory[slot].getItem() == Item.redstone) return slot;
		}
		return -1;
	}

	public static void updateAltarBlockState(boolean active, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (active) {
			world.setBlock(x, y, z, XRBlocks.altarActive.blockID);
		} else {
			world.setBlock(x, y, z, XRBlocks.altarIdle.blockID);
		}
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
		if (te != null) {
			te.validate();
			world.setBlockTileEntity(x, y, z, te);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TEAltar();
	}
}
