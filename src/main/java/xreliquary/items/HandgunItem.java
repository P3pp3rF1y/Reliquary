package xreliquary.items;

import com.google.common.collect.ImmutableMap;
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
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HandgunItem extends ItemBase {
	private static final int HANDGUN_RELOAD_SKILL_OFFSET = 10;
	private static final int HANDGUN_COOLDOWN_SKILL_OFFSET = 5;
	private static final String MAGAZINE_TYPE_TAG = "magazineType";

	public interface IShotEntityFactory {
		ShotEntityBase createShot(World world, PlayerEntity player, Hand hand);
	}

	private final Map<String, IShotEntityFactory> magazineShotFactories = new HashMap<>();
	private final Map<String, Supplier<BulletItem>> magazineBulletItems = new HashMap<>();

	public void registerMagazine(String magazineRegistryName, IShotEntityFactory factory, Supplier<BulletItem> getBulletItem) {
		magazineShotFactories.put(magazineRegistryName, factory);
		magazineBulletItems.put(magazineRegistryName, getBulletItem);
	}

	public HandgunItem() {
		super(new Properties().maxStackSize(1), Settings.COMMON.disable.disableHandgun::get);
	}

	private short getBulletCount(ItemStack handgun) {
		return NBTHelper.getShort("bulletCount", handgun);
	}

	public ItemStack getBulletStack(ItemStack handgun) {
		String magazineType = getMagazineType(handgun);
		if (!magazineBulletItems.containsKey(magazineType)) {
			return new ItemStack(ModItems.EMPTY_BULLET.get(), 1);
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
		return NBTHelper.getString(MAGAZINE_TYPE_TAG, handgun);
	}

	private void setMagazineType(ItemStack handgun, ItemStack magazine) {
		NBTHelper.putString(MAGAZINE_TYPE_TAG, handgun, RegistryHelper.getItemRegistryName(magazine.getItem()));
	}

	private boolean hasAmmo(ItemStack handgun) {
		return getBulletCount(handgun) > 0;
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
		if (getBulletCount(handgun) > 0) {
			return UseAction.NONE;
		} else {
			return UseAction.BLOCK;
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || getBulletCount(oldStack) < getBulletCount(newStack);
	}

	private boolean isCooldownOver(World world, ItemStack handgun) {
		return getCooldown(handgun) < world.getGameTime();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack handgun = player.getHeldItem(hand);

		if (getBulletCount(handgun) > 0 && !isCooldownOver(world, handgun) && otherHandgunCooledDownMore(player, hand, handgun)) {
			return new ActionResult<>(ActionResultType.PASS, handgun);
		}

		if (getBulletCount(handgun) > 0 || hasFilledMagazine(player)) {
			player.setActiveHand(hand);
			return new ActionResult<>(ActionResultType.SUCCESS, handgun);
		}
		return new ActionResult<>(ActionResultType.PASS, handgun);
	}

	private boolean otherHandgunCooledDownMore(PlayerEntity player, Hand currentHand, ItemStack currentHandgun) {
		if (currentHand == Hand.MAIN_HAND) {
			ItemStack offHandItem = player.getHeldItemOffhand();
			return offHandItem.getItem() == this && getCooldown(offHandItem) < getCooldown(currentHandgun);
		}

		ItemStack mainHandItem = player.getHeldItemMainhand();
		return mainHandItem.getItem() == this && getCooldown(mainHandItem) < getCooldown(currentHandgun);
	}

	@Override
	public void onUsingTick(ItemStack handgun, LivingEntity entity, int unadjustedCount) {
		if (entity.world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
		int actualCount = unadjustedCount - maxUseOffset;
		actualCount -= 1;

		if (actualCount == 0 || (isCooldownOver(entity.world, handgun) && getBulletCount(handgun) > 0) || !hasFilledMagazine(player)) {
			player.stopActiveHand();
		}
	}

	@Override
	public int getUseDuration(ItemStack handgun) {
		return getItemUseDuration();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack handgun, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entityLiving;

		// fire bullet
		if (hasAmmo(handgun)) {
			if (isCooldownOver(player.world, handgun)) {
				setFiringCooldown(handgun, worldIn, player);
				fireBullet(handgun, worldIn, player, handgun == player.getHeldItemMainhand() ? Hand.MAIN_HAND : Hand.OFF_HAND);
			}
			return;
		}

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(handgun, player.world.getGameTime() + 12);

		getMagazineSlot(player).ifPresent(slot -> {
			ItemStack magazine = player.inventory.mainInventory.get(slot);
			setMagazineType(handgun, magazine);
			setPotionEffects(handgun, XRPotionHelper.getPotionEffectsFromStack(magazine));
			magazine.shrink(1);
			if (magazine.isEmpty()) {
				player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
			}
			player.swingArm(player.getActiveHand());
			spawnEmptyMagazine(player);
			setBulletCount(handgun, (short) 8);
			player.world.playSound(null, player.getPosition(), ModSounds.xload, SoundCategory.PLAYERS, 0.25F, 1.0F);
			setFiringCooldown(handgun, worldIn, player);
		});

		if (getBulletCount(handgun) == 0) {
			setPotionEffects(handgun, Collections.emptyList());
		}
	}

	private void setSecondHandgunFiringCooldown(PlayerEntity player, ItemStack currentHandgun) {
		if (player.getHeldItemMainhand() == currentHandgun) {
			setHalfFiringCooldown(player, player.getHeldItemOffhand());
		} else if(player.getHeldItemOffhand() == currentHandgun) {
			setHalfFiringCooldown(player, player.getHeldItemMainhand());
		}
	}

	private void setHalfFiringCooldown(PlayerEntity player, ItemStack potentialHandgun) {
		if (potentialHandgun.getItem() == this && isCooldownOver(player.world, potentialHandgun)) {
			setCooldown(potentialHandgun, player.world.getGameTime() + (getPlayerFiringCooldown(player) / 2));
		}
	}

	private void setFiringCooldown(ItemStack handgun, World worldIn, PlayerEntity player) {
		setCooldown(handgun, worldIn.getGameTime() + getPlayerFiringCooldown(player));
		setSecondHandgunFiringCooldown(player, handgun);
	}

	private int getPlayerFiringCooldown(PlayerEntity player) {
		return Settings.COMMON.items.handgun.maxSkillLevel.get() + HANDGUN_COOLDOWN_SKILL_OFFSET
				- Math.min(player.experienceLevel, Settings.COMMON.items.handgun.maxSkillLevel.get());
	}

	private int getItemUseDuration() {
		return HANDGUN_RELOAD_SKILL_OFFSET + Settings.COMMON.items.handgun.maxSkillLevel.get();
	}

	private void fireBullet(ItemStack handgun, World world, PlayerEntity player, Hand hand) {
		if (!world.isRemote) {
			String magazineType = getMagazineType(handgun);
			if (!magazineShotFactories.containsKey(magazineType)) {
				return;
			}
			spawnShotEntity(handgun, world, player, hand, magazineType);
			world.playSound(null, player.getPosition(), ModSounds.xshot, SoundCategory.PLAYERS, 0.5F, 1.2F);

			setBulletCount(handgun, (short) (getBulletCount(handgun) - 1));
			if (getBulletCount(handgun) == 0) {
				setPotionEffects(handgun, Collections.emptyList());
			}
			spawnCasing(player);
		}
	}

	private void spawnShotEntity(ItemStack handgun, World world, PlayerEntity player, Hand hand, String magazineType) {
		if (!magazineShotFactories.containsKey(magazineType)) {
			return;
		}
		ShotEntityBase shot = magazineShotFactories.get(magazineType).createShot(world, player, hand).addPotionEffects(getPotionEffects(handgun));
		double motionX = -MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
		double motionZ = MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
		double motionY = -MathHelper.sin(player.rotationPitch / 180.0F * (float) Math.PI);
		shot.shoot(motionX, motionY, motionZ, 1.2F, 1.0F);
		world.addEntity(shot);
	}

	private void spawnEmptyMagazine(PlayerEntity player) {
		ItemStack emptyMagazine = new ItemStack(ModItems.EMPTY_MAGAZINE.get());
		if (!player.inventory.addItemStackToInventory(emptyMagazine)) {
			player.entityDropItem(emptyMagazine, 0.1F);
		}
	}

	private void spawnCasing(PlayerEntity player) {
		ItemStack emptyCasing = new ItemStack(ModItems.EMPTY_BULLET.get());
		if (!player.inventory.addItemStackToInventory(emptyCasing)) {
			player.entityDropItem(emptyCasing, 0.1F);
		}
	}

	private boolean hasFilledMagazine(PlayerEntity player) {
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack == null) {
				continue;
			}
			if (stack.getItem() instanceof MagazineItem && stack.getItem() != ModItems.EMPTY_MAGAZINE.get()) {
				return true;
			}
		}
		return false;
	}

	private Optional<Integer> getMagazineSlot(PlayerEntity player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			Item item = player.inventory.mainInventory.get(slot).getItem();
			if (item instanceof MagazineItem && item != ModItems.EMPTY_MAGAZINE.get()) {
				return Optional.of(slot);
			}
		}
		return Optional.empty();
	}

	private int getPlayerReloadDelay(PlayerEntity player) {
		return Settings.COMMON.items.handgun.maxSkillLevel.get() + HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, Settings.COMMON.items.handgun.maxSkillLevel.get());
	}
}
