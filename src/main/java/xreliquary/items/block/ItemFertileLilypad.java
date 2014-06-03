package xreliquary.items.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.ContentHandler;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFertileLilypad extends ItemBlock {

	public ItemFertileLilypad(Block block) {
		super(block);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.lilypad);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack i, int renderpass) {
		return itemIcon;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		MovingObjectPosition var4 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

		if (var4 == null)
			return par1ItemStack;
		else {
			if (var4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int var5 = var4.blockX;
				int var6 = var4.blockY;
				int var7 = var4.blockZ;

				if (!par2World.canMineBlock(par3EntityPlayer, var5, var6, var7))
					return par1ItemStack;

				if (!par3EntityPlayer.canPlayerEdit(var5, var6, var7, var4.sideHit, par1ItemStack))
					return par1ItemStack;

				if (par2World.getBlock(var5, var6, var7).getMaterial() == Material.water && par2World.getBlockMetadata(var5, var6, var7) == 0 && par2World.isAirBlock(var5, var6 + 1, var7)) {
					par2World.setBlock(var5, var6 + 1, var7, ContentHandler.getBlock(Names.lilypad));

					if (!par3EntityPlayer.capabilities.isCreativeMode) {
						--par1ItemStack.stackSize;
					}
				}
			}

			return par1ItemStack;
		}
	}

}
