package xreliquary.items;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.entities.EntitySpecialEnderPearl;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnderStaff extends ItemWithCapacity {
	protected ItemEnderStaff(int par1) {
		super(par1);
		this.setUnlocalizedName(Names.ENDER_STAFF_NAME);
		this.DEFAULT_TARGET_ITEM = new ItemStack(Item.enderPearl, 1, 0);
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List infoList, boolean par4) {
		infoList.add("Right click to cast");
		infoList.add("an Ender Pearl.");
		infoList.add("Sneak to arc it.");
		super.addInformation(ist, player, infoList, par4);
	}

	@Override
	protected boolean isActive(ItemStack ist) {
		return true;
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

	private boolean consumeEnderPearl(ItemStack ist) {
		return this.decreaseQuantity(ist);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		if (this.isOnCooldown(ist)) return ist;
		if (!hasItem(ist)) return ist;
		if (player.isSneaking()) {
			castArcEnderPearl(ist, world, player);
		} else {
			castStraightEnderPearl(ist, world, player);
		}
		return ist;
	}

	private void castStraightEnderPearl(ItemStack ist, World world, EntityPlayer player) {
		if (consumeEnderPearl(ist)) {
			setCooldown(ist, 10);
			world.spawnEntityInWorld(new EntitySpecialEnderPearl(world, player, false));
		}
	}

	private void castArcEnderPearl(ItemStack ist, World world, EntityPlayer player) {
		if (consumeEnderPearl(ist)) {
			setCooldown(ist, 10);
			world.spawnEntityInWorld(new EntitySpecialEnderPearl(world, player, true));
		}
	}
}
