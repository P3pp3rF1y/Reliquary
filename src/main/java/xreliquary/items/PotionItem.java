package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.potion.ThrownXRPotionEntity;
import xreliquary.reference.Names;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PotionItem extends ItemBase implements IPotionItem {

	public PotionItem() {
		super(Names.Items.POTION, new Properties());
	}

	// returns an empty vial when used in crafting recipes, unless it's one of
	// the base potion types.
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack).size() > 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack potion, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		XRPotionHelper.addPotionTooltip(potion, tooltip);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity) || world.isRemote) {
			return stack;
		}

		PlayerEntity player = (PlayerEntity) entity;

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		for (EffectInstance effect : XRPotionHelper.getPotionEffectsFromStack(stack)) {
			player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), false, false));
		}

		if (!player.isCreative()) {
			ItemStack emptyVial = new ItemStack(this);
			emptyVial.setTag(new CompoundNBT()); //doing this as without this vials dropped on ground and picked up wouldn't stack properly - they get empty NBT assigned
			if (stack.getCount() <= 0) {
				return emptyVial;
			} else {
				player.inventory.addItemStackToInventory(emptyVial);
			}
		}
		return stack;
	}

	public boolean isSplash(ItemStack stack) {
		return NBTHelper.getBoolean("splash", stack);
	}

	public boolean isLingering(ItemStack stack) {
		return NBTHelper.getBoolean("lingering", stack);
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 16;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (!isInGroup(group)) {
			return;
		}

		items.add(new ItemStack(this)); //just an empty one

		List<ItemStack> splashPotions = new ArrayList<>();
		List<ItemStack> lingeringPotions = new ArrayList<>();
		for (PotionEssence essence : PotionMap.uniquePotions) {
			ItemStack potion = new ItemStack(this, 1);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
			NBTHelper.putBoolean("hasPotion", potion, true);

			ItemStack splashPotion = potion.copy();
			NBTHelper.putBoolean("splash", splashPotion, true);

			ItemStack lingeringPotion = potion.copy();
			NBTHelper.putBoolean("lingering", lingeringPotion, true);

			items.add(potion);
			splashPotions.add(splashPotion);
			lingeringPotions.add(lingeringPotion);
		}
		items.addAll(splashPotions);
		items.addAll(lingeringPotions);
	}

	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */

	@Override
	public UseAction getUseAction(ItemStack stack) {
		if (!isSplash(stack) && XRPotionHelper.getPotionEffectsFromStack(stack).size() > 0) {
			return UseAction.DRINK;
		}
		return UseAction.NONE;
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		//noinspection ConstantConditions
		if (!stack.hasTag() || stack.getTag().isEmpty()) {
			return new TranslationTextComponent("item.potion_empty");
		} else if (isLingering(stack)) {
			return new TranslationTextComponent("item.potion_lingering");
		} else if (isSplash(stack)) {
			return new TranslationTextComponent("item.potion_splash");
		}

		return super.getDisplayName(stack);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!isSplash(stack) && !isLingering(stack)) {
			if (XRPotionHelper.getPotionEffectsFromStack(stack).size() > 0) {
				player.setActiveHand(hand);
				return new ActionResult<>(ActionResultType.SUCCESS, stack);
			} else {
				return new ActionResult<>(ActionResultType.PASS, stack);
			}
		} else {
			if (world.isRemote) {
				return new ActionResult<>(ActionResultType.PASS, stack);
			}
			ThrownXRPotionEntity e = new ThrownXRPotionEntity(world, player, stack);
			e.shoot(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);

			if (!player.isCreative()) {
				stack.shrink(1);
			}
			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			world.addEntity(e);
		}
		return new ActionResult<>(ActionResultType.PASS, stack);
	}

	@Override
	public List<EffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}
}
