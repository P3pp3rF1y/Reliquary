package xreliquary.items;

import java.util.List;

import mods.themike.core.item.ItemBase;
import mods.themike.core.util.ObjectUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSojournerStaff extends ItemBase {

	public ItemSojournerStaff() {
		super(Reference.MOD_ID, Names.TORCH_NAME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(513);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		if (world.isRemote)
			return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if (player == null)
			return;
		if (ist.getItemDamage() == 0 || ist.getItemDamage() > 1) {
			if (findAndRemoveTorch(player)) {
				ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 1 : ist.getItemDamage() - 1);
			}
		}
		if (this.isOnCooldown(ist)) {
			decrementCooldown(ist);
		}
	}

	private boolean findAndRemoveTorch(EntityPlayer player) {
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if (player.inventory.mainInventory[slot].getItem() == Item.getItemFromBlock(Blocks.torch)) {
				player.inventory.decrStackSize(slot, 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
		if (this.isOnCooldown(ist))
			return false;
		if (!player.canPlayerEdit(x, y, z, side, ist))
			return false;
		Block blockTargetted = world.getBlock(x, y, z);

		if (ObjectUtils.areBlocksEqual(blockTargetted, Blocks.snow)) {
			side = 1;
		} else if (!ObjectUtils.areBlocksEqual(blockTargetted, Blocks.vine) && !ObjectUtils.areBlocksEqual(blockTargetted, Blocks.tallgrass) && !ObjectUtils.areBlocksEqual(blockTargetted, Blocks.deadbush) && (blockTargetted == null || !blockTargetted.isReplaceable(world, x, y, z))) {
			x += side == 4 ? -1 : side == 5 ? 1 : 0;
			y += side == 0 ? -1 : side == 1 ? 1 : 0;
			z += side == 2 ? -1 : side == 3 ? 1 : 0;
		}
		if (Blocks.torch.canPlaceBlockAt(world, x, y, z)) {
			if (world.canPlaceEntityOnSide(Blocks.torch, x, y, z, false, side, player, ist)) {
				Block var12 = Blocks.torch;

				if (!player.capabilities.isCreativeMode) {
					if (ist.getItemDamage() == 0)
						return false;
					int cost = 1;
					int distance = (int) player.getDistance(x, y, z);
					for (; distance > 6; distance -= 6) {
						cost++;
					}
					if (ist.getItemDamage() >= ist.getMaxDamage() - cost)
						return false;
					ist.setItemDamage(ist.getItemDamage() == 1535 ? 0 : ist.getItemDamage() + cost);
					if (placeBlockAt(ist, player, world, x, y, z, side, xOff, yOff, zOff, attemptSide(world, x, y, z, side))) {
						Blocks.torch.onBlockAdded(world, x, y, z);
						double gauss = 0.5D + world.rand.nextFloat() / 2;
						player.swingItem();
						world.spawnParticle("mobSpell", x + 0.5D, y + 0.5D, z + 0.5D, gauss, gauss, 0.0F);
						world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var12.stepSound.getStepResourcePath(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
						this.setCooldown(ist);
					}
				} else {
					if (placeBlockAt(ist, player, world, x, y, z, side, xOff, yOff, zOff, attemptSide(world, x, y, z, side))) {
                        Blocks.torch.onBlockAdded(world, x, y, z);
						double gauss = 0.5D + world.rand.nextFloat() / 2;
						player.swingItem();
						world.spawnParticle("mobSpell", x + 0.5D, y + 0.5D, z + 0.5D, gauss, gauss, 0.0F);
						world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var12.stepSound.getStepResourcePath(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
						this.setCooldown(ist);
					}
				}
			}
		}
		return true;
	}

	private int attemptSide(World world, int x, int y, int z, int side) {
		return Blocks.torch.onBlockPlaced(world, x, y, z, side, x, y, z, 0);
	}

	private void decrementCooldown(ItemStack ist) {
		NBTHelper.setShort("cooldown", ist, NBTHelper.getShort("cooldown", ist) - 1);
	}

	private boolean isOnCooldown(ItemStack ist) {
		return NBTHelper.getShort("cooldown", ist) > 0;
	}

	private void setCooldown(ItemStack ist) {
		NBTHelper.setShort("cooldown", ist, 10);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		if (this.isOnCooldown(ist))
			return ist;
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
		if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			float xOff = (float) (mop.blockX - player.posX);
			float yOff = (float) (mop.blockY - player.posY);
			float zOff = (float) (mop.blockZ - player.posZ);
			this.onItemUse(ist, player, world, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, xOff, yOff, zOff);
		}
		return ist;
	}

	@Override
	protected MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3) {
		float var4 = 1.0F;
		float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
		float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
		double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * var4;
		double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * var4 + 1.62D - par2EntityPlayer.yOffset;
		double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * var4;
		Vec3 var13 = par1World.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 32.0D;
		Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20 * var21);
		return par1World.rayTraceBlocks_do_do(var13, var23, par3, !par3);
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!world.setBlock(x, y, z, Blocks.torch, metadata, 3))
			return false;

		if (ObjectUtils.areBlocksEqual(world.getBlock(x, y, z), Blocks.torch)) {
			Blocks.torch.onNeighborBlockChange(world, x, y, z, world.getBlock(x, y, z));
			Blocks.torch.onBlockPlacedBy(world, x, y, z, player, stack);
		}

		return true;
	}
}
