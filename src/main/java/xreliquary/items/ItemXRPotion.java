package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.init.ModItems;
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
	public void addInformation(ItemStack ist, EntityPlayer player, List<String> list, boolean flag) {
		PotionEssence essence = new PotionEssence(ist.getTagCompound());
		XRPotionHelper.addPotionInfo(essence, list);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack ist, World world, EntityLivingBase entity) {
		if(!(entity instanceof EntityPlayer))
			return ist;

		EntityPlayer player = (EntityPlayer) entity;

		if(!player.capabilities.isCreativeMode) {
			--ist.stackSize;
		}
		if(!world.isRemote) {
			for(PotionEffect effect : new PotionEssence(ist.getTagCompound()).getEffects()) {
				if(effect == null)
					continue;
				player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), false, false));
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

	public boolean getLingering(ItemStack ist) {
		return NBTHelper.getBoolean("lingering", ist);
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
		List<ItemStack> lingeringPotions = new ArrayList<>();
		for(PotionEssence essence : Settings.Potions.uniquePotions) {
			ItemStack potion = new ItemStack(ModItems.potion, 1);
			potion.setTagCompound(essence.writeToNBT());
			NBTHelper.setBoolean("hasPotion", potion, true);

			ItemStack splashPotion = potion.copy();
			NBTHelper.setBoolean("splash", splashPotion, true);

			ItemStack lingeringPotion = potion.copy();
			NBTHelper.setBoolean("lingering", lingeringPotion, true);

			subItems.add(potion);
			splashPotions.add(splashPotion);
			lingeringPotions.add(lingeringPotion);
		}
		subItems.addAll(splashPotions);
		subItems.addAll(lingeringPotions);
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

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (!stack.hasTagCompound() || stack.getTagCompound().hasNoTags()) {
			return "item.potion_empty";
		} else if (getLingering(stack)) {
			return "item.potion_lingering";
		} else if (getSplash(stack)) {
			return "item.potion_splash";
		}

		return super.getUnlocalizedName(stack);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		PotionEssence essence = new PotionEssence(ist.getTagCompound());
		if(!getSplash(ist) && !getLingering(ist)) {
			if(essence.getEffects().size() > 0) {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.SUCCESS, ist);
			} else {
				RayTraceResult rayTraceResult = this.rayTrace(world, player, true);

				if(rayTraceResult == null)
					return new ActionResult<>(EnumActionResult.PASS, ist);
				else {
					if(rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
						if(world.getBlockState(rayTraceResult.getBlockPos()).getBlock() instanceof BlockApothecaryCauldron) {
							TileEntityCauldron cauldronTile = (TileEntityCauldron) world.getTileEntity(rayTraceResult.getBlockPos());
							NBTTagCompound potionTag = cauldronTile.removeContainedPotion();
							ItemStack newPotion = new ItemStack(this, 1, 0);
							newPotion.setTagCompound(potionTag);

							if(--ist.stackSize <= 0) {
								return new ActionResult<>(EnumActionResult.SUCCESS, newPotion);
							}

							if(!player.inventory.addItemStackToInventory(newPotion)) {
								player.entityDropItem(newPotion, 0.1F);
								return new ActionResult<>(EnumActionResult.SUCCESS, ist);
							}
						}
					}
				}
			}
		} else {
			if(world.isRemote)
				return new ActionResult<>(EnumActionResult.PASS, ist);
			EntityThrownXRPotion e = new EntityThrownXRPotion(world, player, ist);
			e.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);

			if(!player.capabilities.isCreativeMode) {
				--ist.stackSize;
			}
			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			world.spawnEntityInWorld(e);
		}
		return new ActionResult<>(EnumActionResult.PASS, ist);
	}
}
