package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.shot.ShotEntityBase;
import xreliquary.init.ModItems;
import xreliquary.init.ModSounds;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HandgunItem extends ItemBase {
	private static final int PLAYER_HANDGUN_SKILL_MAXIMUM = 20;
	private static final int HANDGUN_RELOAD_SKILL_OFFSET = 10;
	private static final int HANDGUN_COOLDOWN_SKILL_OFFSET = 5;

	public interface IShotEntityFactory {
		ShotEntityBase createShot(World world, PlayerEntity player, Hand hand);
	}

	private Map<String, IShotEntityFactory> magazineShotFactories = new HashMap<>();
	private Map<String, Supplier<BulletItem>> magazineBulletItems = new HashMap<>();

	public void registerMagazine(String magazineRegistryName, IShotEntityFactory factory, Supplier<BulletItem> getBulletItem) {
		magazineShotFactories.put(magazineRegistryName, factory);
		magazineBulletItems.put(magazineRegistryName, getBulletItem);
	}

	public HandgunItem() {
		super(Names.Items.HANDGUN, new Properties().maxStackSize(1));
	}

	private short getBulletCount(ItemStack handgun) {
		return NBTHelper.getShort("bulletCount", handgun);
	}

	public ItemStack getBulletStack(ItemStack handgun) {
		String magazineType = getMagazineType(handgun);
		if (!magazineBulletItems.containsKey(magazineType)) {
			return new ItemStack(ModItems.EMPTY_BULLET, 1);
		}

		BulletItem bulletItem = magazineBulletItems.get(magazineType).get();
		ItemStack bulletStack = new ItemStack(bulletItem, getBulletCount(handgun));
		XRPotionHelper.addPotionEffectsToStack(bulletStack, getPotionEffects(handgun));

		return bulletStack;
	}

	private void setBulletCount(ItemStack handgun, short bulletCount) {
		NBTHelper.putShort("bulletCount", handgun, bulletCount);
	}

	private String getMagazineType(ItemStack handgun) {
		return NBTHelper.getString("magazineType", handgun);
	}

	private void setMagazineType(ItemStack handgun, ItemStack magazine) {
		//noinspection ConstantConditions
		NBTHelper.putString("magazineType", handgun, magazine.getItem().getRegistryName().toString());
	}

	private boolean hasAmmo(ItemStack handgun) {
		return getBulletCount(handgun) > 0;
	}

	private boolean isInCooldown(ItemStack handgun) {
		return NBTHelper.getBoolean("inCoolDown", handgun);
	}

	private void setInCooldown(ItemStack handgun, boolean inCooldown) {
		NBTHelper.putBoolean("inCoolDown", handgun, inCooldown);
	}

	public long getCooldown(ItemStack handgun) {
		return NBTHelper.getLong("coolDownTime", handgun);
	}

	private void setCooldown(ItemStack handgun, long coolDownTime) {
		NBTHelper.putLong("coolDownTime", handgun, coolDownTime);
	}

	private List<EffectInstance> getPotionEffects(ItemStack handgun) {
		return XRPotionHelper.getPotionEffectsFromStack(handgun);
	}

	private void setPotionEffects(ItemStack handgun, List<EffectInstance> potionEffects) {
		XRPotionHelper.cleanPotionEffects(handgun);
		XRPotionHelper.addPotionEffectsToStack(handgun, potionEffects);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack handgun, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2",
				ImmutableMap.of("count", String.valueOf(getBulletCount(handgun)), "type",
						LanguageHelper.getLocalization("magazine." + Reference.MOD_ID + "." + getMagazineTranslationKey(handgun))), tooltip);

		XRPotionHelper.addPotionTooltip(handgun, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private String getMagazineTranslationKey(ItemStack handgun) {
		return getMagazineType(handgun).replace(Reference.MOD_ID + ":", "");
	}

	@Override
	public UseAction getUseAction(ItemStack handgun) {
		if(getBulletCount(handgun) > 0) {
			return UseAction.NONE;
		} else {
			return UseAction.BLOCK;
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation( ItemStack oldStack,  ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();

	}

	@Override
	public void inventoryTick(ItemStack handgun, World world, Entity entity, int slotNumber, boolean isSelected) {
		if(world.isRemote) {
			return;
		}

		if(isInCooldown(handgun) && (isCooldownOver(world, handgun) || !isValidCooldownTime(world, handgun))) {
			setInCooldown(handgun, false);
		}
	}

	private boolean isCooldownOver(World world, ItemStack handgun) {
		return getCooldown(handgun) < world.getGameTime() && world.getGameTime() - getCooldown(handgun) < 12000;
	}

	private boolean isValidCooldownTime(World world, ItemStack handgun) {
		return Math.min(Math.abs(world.getGameTime() - getCooldown(handgun)), Math.abs(world.getGameTime() - 23999 - getCooldown(handgun))) <= getUseDuration(handgun);
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player,  Hand hand) {
		ItemStack handgun = player.getHeldItem(hand);

		if((hasFilledMagazine(player) && getBulletCount(handgun) == 0) || (getBulletCount(handgun) > 0 && (hasHandgunInHand(player, hand) || cooledMoreThanSecondHandgun(handgun, player, hand)))) {
			player.setActiveHand(hand);
			return new ActionResult<>(ActionResultType.SUCCESS, handgun);
		}
		return new ActionResult<>(ActionResultType.PASS, handgun);
	}

	private boolean cooledMoreThanSecondHandgun(ItemStack handgun, PlayerEntity player, Hand hand) {
		if(!isInCooldown(handgun)) {
			return true;
		}

		if(hand == Hand.MAIN_HAND) {
			return !isInCooldown(player.getHeldItemOffhand()) && getCooldown(handgun) < getCooldown(player.getHeldItemOffhand());
		} else {
			return !isInCooldown(player.getHeldItemMainhand()) && getCooldown(handgun) < getCooldown(player.getHeldItemMainhand());
		}
	}

	private boolean secondHandgunCooledEnough(World world, PlayerEntity player, Hand hand) {
		ItemStack secondHandgun;

		if(hand == Hand.MAIN_HAND) {
			secondHandgun = player.getHeldItemOffhand();
		} else {
			secondHandgun = player.getHeldItemMainhand();
		}
		return !isInCooldown(secondHandgun) || (getCooldown(secondHandgun) - world.getGameTime()) < (getPlayerReloadDelay(player) / 2);

	}

	private boolean hasHandgunInHand(PlayerEntity player, Hand hand) {
		if(hand == Hand.MAIN_HAND) {
			return player.getHeldItemMainhand().getItem() == this;
		}

		return player.getHeldItemOffhand().getItem() == this;
	}

	@Override
	public void onUsingTick(ItemStack handgun, LivingEntity entity, int unadjustedCount) {
		if(entity.world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
		int actualCount = unadjustedCount - maxUseOffset;
		actualCount -= 1;

		//you can't reload if you don't have any full mags left, so the rest of the method doesn't fire at all.
		if(!hasFilledMagazine(player) || actualCount == 0) {
			player.stopActiveHand();
			return;
		}

		//loaded and ready to fire
		if(!isInCooldown(handgun) && getBulletCount(handgun) > 0 && (hasHandgunInHand(player, player.getActiveHand()) || secondHandgunCooledEnough(player.world, player, player.getActiveHand()))) {
			player.stopActiveHand();
		}
	}

	@Override
	public int getUseDuration(ItemStack handgun) {
		return getItemUseDuration();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack handgun, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if(!(entityLiving instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entityLiving;

		// fire bullet
		if(hasAmmo(handgun)) {
			if(!isInCooldown(handgun)) {
				setCooldown(handgun, worldIn.getGameTime() + PLAYER_HANDGUN_SKILL_MAXIMUM + HANDGUN_COOLDOWN_SKILL_OFFSET - Math.min(player.experienceLevel, PLAYER_HANDGUN_SKILL_MAXIMUM));
				setInCooldown(handgun, true);

				fireBullet(handgun, worldIn, player, handgun == player.getHeldItemMainhand() ? Hand.MAIN_HAND : Hand.OFF_HAND);
			}
			return;
		}

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(handgun, player.world.getGameTime() + 12);
		setInCooldown(handgun, true);

		getMagazineSlot(player).ifPresent(slot -> {
			ItemStack magazine = player.inventory.mainInventory.get(slot);
			setMagazineType(handgun, magazine);
			setPotionEffects(handgun, XRPotionHelper.getPotionEffectsFromStack(magazine));
			magazine.shrink(1);
			if (magazine.isEmpty()) {
				player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
			}
		});

		if(hasAmmo(handgun)) {
			player.swingArm(player.getActiveHand());
			spawnEmptyMagazine(player);
			setBulletCount(handgun, (short) 8);
			player.world.playSound(null, player.getPosition(), ModSounds.xload, SoundCategory.PLAYERS, 0.25F, 1.0F);
		}
		if(getBulletCount(handgun) == 0) {
			setPotionEffects(handgun, Collections.emptyList());
		}
	}

	private int getItemUseDuration() {
		return HANDGUN_RELOAD_SKILL_OFFSET + PLAYER_HANDGUN_SKILL_MAXIMUM;
	}

	private void fireBullet(ItemStack handgun, World world, PlayerEntity player, Hand hand) {
		if(!world.isRemote) {
			String magazineType = getMagazineType(handgun);
			if (!magazineShotFactories.containsKey(magazineType)) {
				return;
			}
			spawnShotEntity(handgun, world, player, hand, magazineType);
			world.playSound(null, player.getPosition(), ModSounds.xshot, SoundCategory.PLAYERS, 0.5F, 1.2F);

			setBulletCount(handgun, (short) (getBulletCount(handgun) - 1));
			if(getBulletCount(handgun) == 0) {
				setPotionEffects(handgun, Collections.emptyList());
			}
			spawnCasing(player);
		}
	}

	private void spawnShotEntity(ItemStack handgun, World world, PlayerEntity player, Hand hand, String magazineType) {
		ShotEntityBase shot = magazineShotFactories.get(magazineType).createShot(world, player, hand).addPotionEffects(getPotionEffects(handgun));
		double motionX = -MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
		double motionZ = MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
		double motionY = -MathHelper.sin(player.rotationPitch / 180.0F * (float) Math.PI);
		shot.shoot(motionX, motionY, motionZ, 1.2F, 1.0F);
		world.addEntity(shot);
	}

	private void spawnEmptyMagazine(PlayerEntity player) {
		ItemStack emptyMagazine = new ItemStack(ModItems.EMPTY_MAGAZINE);
		if(!player.inventory.addItemStackToInventory(emptyMagazine)) {
			player.entityDropItem(emptyMagazine, 0.1F);
		}
	}

	private void spawnCasing(PlayerEntity player) {
		ItemStack emptyCasing = new ItemStack(ModItems.EMPTY_BULLET);
		if(!player.inventory.addItemStackToInventory(emptyCasing)) {
			player.entityDropItem(emptyCasing, 0.1F);
		}
	}

	private boolean hasFilledMagazine(PlayerEntity player) {
		for(ItemStack stack : player.inventory.mainInventory) {
			if(stack == null) {
				continue;
			}
			if(stack.getItem() instanceof MagazineItem && stack.getItem() != ModItems.EMPTY_MAGAZINE) {
				return true;
			}
		}
		return false;
	}

	private Optional<Integer> getMagazineSlot(PlayerEntity player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			Item item = player.inventory.mainInventory.get(slot).getItem();
			if(item instanceof MagazineItem && item != ModItems.EMPTY_MAGAZINE) {
				return Optional.of(slot);
			}
		}
		return Optional.empty();
	}

	private int getPlayerReloadDelay(PlayerEntity player) {
		return PLAYER_HANDGUN_SKILL_MAXIMUM + HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, PLAYER_HANDGUN_SKILL_MAXIMUM);
	}
}
