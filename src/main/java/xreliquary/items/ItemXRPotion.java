package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.reference.Names;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemXRPotion extends ItemBase {

	public ItemXRPotion() {
		super(Names.Items.POTION);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
	}

	// returns an empty vial when used in crafting recipes, unless it's one of
	// the base potion types.
	@Override
	public boolean hasContainerItem(ItemStack ist) {
		return XRPotionHelper.getPotionEffectsFromStack(ist).size() > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack potion, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		XRPotionHelper.addPotionTooltip(potion, tooltip);
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {
		if(!(entity instanceof EntityPlayer) || world.isRemote)
			return stack;

		EntityPlayer player = (EntityPlayer) entity;

		if(!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		for(PotionEffect effect : XRPotionHelper.getPotionEffectsFromStack(stack)) {
			if(effect == null)
				continue;
			player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), false, false));
		}

		if(!player.capabilities.isCreativeMode) {
			ItemStack emptyVial = new ItemStack(this);
			emptyVial.setTagCompound(new NBTTagCompound()); //doing this as without this vials dropped on ground and picked up wouldn't stack properly - they get empty NBT assigned
			if(stack.getCount() <= 0)
				return emptyVial;
			else
				player.inventory.addItemStackToInventory(emptyVial);
		}
		return stack;
	}

	public boolean isSplash(ItemStack ist) {
		return NBTHelper.getBoolean("splash", ist);
	}

	public boolean isLingering(ItemStack ist) {
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
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (!isInCreativeTab(tab))
			return;

		subItems.add(new ItemStack(this)); //just an empty one

		List<ItemStack> splashPotions = new ArrayList<>();
		List<ItemStack> lingeringPotions = new ArrayList<>();
		for(PotionEssence essence : PotionMap.uniquePotions) {
			ItemStack potion = new ItemStack(this, 1);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
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
	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		if(!isSplash(ist) && XRPotionHelper.getPotionEffectsFromStack(ist).size() > 0)
			return EnumAction.DRINK;
		return EnumAction.NONE;
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		//noinspection ConstantConditions
		if(!stack.hasTagCompound() || stack.getTagCompound().hasNoTags()) {
			return "item.potion_empty";
		} else if(isLingering(stack)) {
			return "item.potion_lingering";
		} else if(isSplash(stack)) {
			return "item.potion_splash";
		}

		return super.getUnlocalizedName(stack);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!isSplash(stack) && !isLingering(stack)) {
			if(XRPotionHelper.getPotionEffectsFromStack(stack).size() > 0) {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			} else {
				return new ActionResult<>(EnumActionResult.PASS, stack);
			}
		} else {
			if(world.isRemote)
				return new ActionResult<>(EnumActionResult.PASS, stack);
			EntityThrownXRPotion e = new EntityThrownXRPotion(world, player, stack);
			e.shoot(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);

			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			world.spawnEntity(e);
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}
}
