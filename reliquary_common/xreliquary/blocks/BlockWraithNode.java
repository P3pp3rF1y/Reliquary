package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWraithNode extends Block {
	public BlockWraithNode(int par1) {
		super(par1, Material.rock);
		this.setBlockBounds(0.125F, 0.125F, 0.125F, 0.875F, 0.875F, 0.875F);
		blockHardness = 1.5F;
		blockResistance = 5.0F;
		this.setUnlocalizedName(Names.WRAITHNODE_NAME);
		this.setCreativeTab(Reliquary.tabsXR);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.WRAITHNODE_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		return blockIcon;
	}
	// @Override
	// public boolean onBlockActivated(World world, int x, int y, int z,
	// EntityPlayer player, int side, float xOff, float yOff, float zOff)
	// {
	// if(player.getCurrentEquippedItem() == null)
	// return true;
	// if(player.getCurrentEquippedItem().getItem() == XRItems.wraithEye)
	// teleportPlayerToNodeFromSide(world, side, player, x, y, z);
	// return true;
	// }
	//
	// protected boolean teleportTo(EntityPlayer player, double par1, double
	// par3, double par5)
	// {
	// double var7 = player.posX;
	// double var9 = player.posY;
	// double var11 = player.posZ;
	// player.posX = par1;
	// player.posY = par3;
	// player.posZ = par5;
	// boolean var13 = false;
	// int var14 = MathHelper.floor_double(player.posX);
	// int var15 = MathHelper.floor_double(player.posY);
	// int var16 = MathHelper.floor_double(player.posZ);
	// int var18;
	//
	// if (player.worldObj.blockExists(var14, var15, var16))
	// {
	// boolean var17 = false;
	//
	// while (!var17 && var15 > 0)
	// {
	// var18 = player.worldObj.getBlockId(var14, var15 - 1, var16);
	//
	// if (var18 != 0 && Block.blocksList[var18].blockMaterial.blocksMovement())
	// {
	// var17 = true;
	// }
	// else
	// {
	// --player.posY;
	// --var15;
	// }
	// }
	//
	// if (var17)
	// {
	// player.setPosition(player.posX, player.posY, player.posZ);
	//
	// if (player.worldObj.getCollidingBoundingBoxes(player,
	// player.boundingBox).isEmpty() &&
	// !player.worldObj.isAnyLiquid(player.boundingBox))
	// {
	// var13 = true;
	// }
	// }
	// }
	//
	// if (!var13)
	// {
	// player.setPosition(var7, var9, var11);
	// return false;
	// }
	// else
	// {
	// short var30 = 128;
	//
	// for (var18 = 0; var18 < var30; ++var18)
	// {
	// double var19 = (double)var18 / ((double)var30 - 1.0D);
	// float var21 = (player.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
	// float var22 = (player.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
	// float var23 = (player.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
	// double var24 = var7 + (player.posX - var7) * var19 +
	// (player.worldObj.rand.nextDouble() - 0.5D) * (double)player.width * 2.0D;
	// double var26 = var9 + (player.posY - var9) * var19 +
	// player.worldObj.rand.nextDouble() * (double)player.height;
	// double var28 = var11 + (player.posZ - var11) * var19 +
	// (player.worldObj.rand.nextDouble() - 0.5D) * (double)player.width * 2.0D;
	// player.worldObj.spawnParticle("portal", var24, var26, var28,
	// (double)var21, (double)var22, (double)var23);
	// }
	//
	// player.worldObj.playSoundEffect(var7, var9, var11, "mob.endermen.portal",
	// 1.0F, 1.0F);
	// player.playSound("mob.endermen.portal", 1.0F, 1.0F);
	// return true;
	// }
	// }
	//
	// public void teleportPlayerToNodeFromSide(World world, int side,
	// EntityPlayer player, int xO, int yO, int zO) {
	// switch (side) {
	// case 0:
	// for(int yD = 0; yD < 1024; yD++)
	// if(world.getBlockId(xO, yO + yD, zO) == this.blockID)
	// teleportTo(player, xO, yO + yD + 1, zO);
	// break;
	// case 1:
	// for(int yD = 0; yD > -1024; yD--)
	// if(world.getBlockId(xO, yO + yD, zO) == this.blockID)
	// teleportTo(player, xO, yO + yD - 1, zO);
	// break;
	// case 2:
	// for(int zD = 0; zD < 1024; zD++)
	// if(world.getBlockId(xO, yO, zO + zD) == this.blockID)
	// teleportTo(player, xO, yO, zO + zD + 1);
	// break;
	// case 3:
	// for(int zD = 0; zD > -1024; zD--)
	// if(world.getBlockId(xO, yO, zO + zD) == this.blockID)
	// teleportTo(player, xO, yO, zO + zD - 1);
	// break;
	// case 4:
	// for(int xD = 0; xD < 1024; xD++)
	// if(world.getBlockId(xO + xD, yO, zO) == this.blockID)
	// teleportTo(player, xO + xD + 1, yO, zO);
	// break;
	// case 5:
	// for(int xD = 0; xD > -1024; xD--)
	// if(world.getBlockId(xO + xD, yO, zO) == this.blockID)
	// teleportTo(player, xO + xD - 1, yO, zO);
	// break;
	// }
	// }
}
