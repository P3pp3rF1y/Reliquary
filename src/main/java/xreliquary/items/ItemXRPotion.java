package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xeno on 11/9/2014.
 */
public class ItemXRPotion extends ItemBase {

	public ItemXRPotion() {
		super(Names.potion);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
	}

	// returns an empty vial when used in crafting recipes, unless it's one of
	// the base potion types.
	@Override
	public boolean hasContainerItem(ItemStack ist) {
		PotionEssence essence = new PotionEssence(ist.getTagCompound());
		return essence.getEffects().size() > 0;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean flag) {
		PotionEssence essence = new PotionEssence(ist.getTagCompound());
		XRPotionHelper.addPotionInfo(essence, list);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack ist, World world, EntityPlayer player) {
		if(!player.capabilities.isCreativeMode) {
			--ist.stackSize;
		}
		if(!world.isRemote) {
			for(PotionEffect effect : new PotionEssence(ist.getTagCompound()).getEffects()) {
				if(effect == null)
					continue;
				player.addPotionEffect(new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(), false, false));
			}
		}
		if(!player.capabilities.isCreativeMode) {
			if(ist.stackSize <= 0)
				return new ItemStack(this, 1, 0);
			player.inventory.addItemStackToInventory(new ItemStack(this, 1, 0));
		}
		return ist;
	}

	public boolean getSplash(ItemStack ist) {
		return NBTHelper.getBoolean("splash", ist);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack ist, int renderPass) {
		if(renderPass == 1)
			return getColor(ist);
		else
			return Integer.parseInt(Colors.PURE, 16);
	}

	public int getColor(ItemStack itemStack) {
		//used when rendering as thrown entity
		if(NBTHelper.getInteger("renderColor", itemStack) > 0)
			return NBTHelper.getInteger("renderColor", itemStack);

		PotionEssence essence = new PotionEssence(itemStack.getTagCompound());
		boolean hasEffect = essence.getEffects().size() > 0;
		if(!hasEffect)
			return Integer.parseInt(Colors.PURE, 16);

		return PotionHelper.calcPotionLiquidColor(new PotionEssence(itemStack.getTagCompound()).getEffects());
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 16;
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(ModItems.potion)); //just an empty one

		List<ItemStack> splashPotions = new ArrayList<>();
		for(PotionEssence essence : Settings.Potions.uniquePotions) {
			ItemStack potion = new ItemStack(ModItems.potion, 1);
			potion.setTagCompound(essence.writeToNBT());
			NBTHelper.setBoolean("hasPotion", potion, true);

			ItemStack splashPotion = potion.copy();
			NBTHelper.setBoolean("splash", splashPotion, true);

			subItems.add(potion);
			splashPotions.add(splashPotion);
		}
		subItems.addAll(splashPotions);
	}

	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		if(!getSplash(ist) && new PotionEssence(ist.getTagCompound()).getEffects().size() > 0)
			return EnumAction.DRINK;
		return EnumAction.NONE;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		PotionEssence essence = new PotionEssence(ist.getTagCompound());
		if(!getSplash(ist)) {
			if(essence.getEffects().size() > 0) {
				player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
				return ist;
			} else {
				MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

				if(mop == null)
					return ist;
				else {
					if(mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
						if(world.getBlockState(mop.getBlockPos()).getBlock() instanceof BlockApothecaryCauldron) {
							TileEntityCauldron cauldronTile = (TileEntityCauldron) world.getTileEntity(mop.getBlockPos());
							NBTTagCompound potionTag = cauldronTile.removeContainedPotion(world);
							ItemStack newPotion = new ItemStack(this, 1, 0);
							newPotion.setTagCompound(potionTag);

							if(--ist.stackSize <= 0) {
								return newPotion;
							}

							if(!player.inventory.addItemStackToInventory(newPotion)) {
								player.entityDropItem(newPotion, 0.1F);
							}
						}
					}
				}
			}
		} else {
			if(world.isRemote)
				return ist;
			EntityThrownXRPotion e = new EntityThrownXRPotion(world, player, ist);
			if(e == null)
				return ist;
			e.func_184538_a(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);

			if(!player.capabilities.isCreativeMode) {
				--ist.stackSize;
			}
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			world.spawnEntityInWorld(e);
		}
		return ist;
	}
}
