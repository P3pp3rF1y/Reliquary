package reliquary.items;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.entities.shot.ShotEntityBase;
import reliquary.init.ModItems;
import reliquary.init.ModSounds;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;
import reliquary.util.NBTHelper;
import reliquary.util.RegistryHelper;
import reliquary.util.potions.XRPotionHelper;

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
		ShotEntityBase createShot(Level world, Player player, InteractionHand hand);
	}

	private final Map<String, IShotEntityFactory> magazineShotFactories = new HashMap<>();
	private final Map<String, Supplier<BulletItem>> magazineBulletItems = new HashMap<>();

	public void registerMagazine(String magazineRegistryName, IShotEntityFactory factory, Supplier<BulletItem> getBulletItem) {
		magazineShotFactories.put(magazineRegistryName, factory);
		magazineBulletItems.put(magazineRegistryName, getBulletItem);
	}

	public HandgunItem() {
		super(new Properties().stacksTo(1), Settings.COMMON.disable.disableHandgun::get);
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

	private List<MobEffectInstance> getPotionEffects(ItemStack handgun) {
		return XRPotionHelper.getPotionEffectsFromStack(handgun);
	}

	private void setPotionEffects(ItemStack handgun, List<MobEffectInstance> potionEffects) {
		XRPotionHelper.cleanPotionEffects(handgun);
		XRPotionHelper.addPotionEffectsToStack(handgun, potionEffects);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack handgun, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2",
				Map.of("count", String.valueOf(getBulletCount(handgun)), "type",
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
	public UseAnim getUseAnimation(ItemStack handgun) {
		if (getBulletCount(handgun) > 0) {
			return UseAnim.NONE;
		} else {
			return UseAnim.BLOCK;
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || getBulletCount(oldStack) < getBulletCount(newStack);
	}

	private boolean isCooldownOver(Level world, ItemStack handgun) {
		return getCooldown(handgun) < world.getGameTime();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack handgun = player.getItemInHand(hand);

		if (getBulletCount(handgun) > 0 && !isCooldownOver(world, handgun) && otherHandgunCooledDownMore(player, hand, handgun)) {
			return new InteractionResultHolder<>(InteractionResult.PASS, handgun);
		}

		if (getBulletCount(handgun) > 0 || hasFilledMagazine(player)) {
			player.startUsingItem(hand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, handgun);
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, handgun);
	}

	private boolean otherHandgunCooledDownMore(Player player, InteractionHand currentHand, ItemStack currentHandgun) {
		if (currentHand == InteractionHand.MAIN_HAND) {
			ItemStack offHandItem = player.getOffhandItem();
			return offHandItem.getItem() == this && getCooldown(offHandItem) < getCooldown(currentHandgun);
		}

		ItemStack mainHandItem = player.getMainHandItem();
		return mainHandItem.getItem() == this && getCooldown(mainHandItem) < getCooldown(currentHandgun);
	}

	@Override
	public void onUsingTick(ItemStack handgun, LivingEntity entity, int unadjustedCount) {
		if (entity.level.isClientSide || !(entity instanceof Player player)) {
			return;
		}

		int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
		int actualCount = unadjustedCount - maxUseOffset;
		actualCount -= 1;

		if (actualCount == 0 || (isCooldownOver(entity.level, handgun) && getBulletCount(handgun) > 0) || !hasFilledMagazine(player)) {
			player.releaseUsingItem();
		}
	}

	@Override
	public int getUseDuration(ItemStack handgun) {
		return getItemUseDuration();
	}

	@Override
	public void releaseUsing(ItemStack handgun, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof Player player)) {
			return;
		}

		// fire bullet
		if (hasAmmo(handgun)) {
			if (isCooldownOver(player.level, handgun)) {
				setFiringCooldown(handgun, worldIn, player);
				fireBullet(handgun, worldIn, player, handgun == player.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
			}
			return;
		}

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(handgun, player.level.getGameTime() + 12);

		getMagazineSlot(player).ifPresent(slot -> {
			ItemStack magazine = player.getInventory().items.get(slot);
			setMagazineType(handgun, magazine);
			setPotionEffects(handgun, XRPotionHelper.getPotionEffectsFromStack(magazine));
			magazine.shrink(1);
			if (magazine.isEmpty()) {
				player.getInventory().items.set(slot, ItemStack.EMPTY);
			}
			player.swing(player.getUsedItemHand());
			spawnEmptyMagazine(player);
			setBulletCount(handgun, (short) 8);
			player.level.playSound(null, player.blockPosition(), ModSounds.HANDGUN_LOAD.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
			setFiringCooldown(handgun, worldIn, player);
		});

		if (getBulletCount(handgun) == 0) {
			setPotionEffects(handgun, Collections.emptyList());
		}
	}

	private void setSecondHandgunFiringCooldown(Player player, ItemStack currentHandgun) {
		if (player.getMainHandItem() == currentHandgun) {
			setHalfFiringCooldown(player, player.getOffhandItem());
		} else if (player.getOffhandItem() == currentHandgun) {
			setHalfFiringCooldown(player, player.getMainHandItem());
		}
	}

	private void setHalfFiringCooldown(Player player, ItemStack potentialHandgun) {
		if (potentialHandgun.getItem() == this && isCooldownOver(player.level, potentialHandgun)) {
			setCooldown(potentialHandgun, player.level.getGameTime() + (getPlayerFiringCooldown(player) / 2));
		}
	}

	private void setFiringCooldown(ItemStack handgun, Level worldIn, Player player) {
		setCooldown(handgun, worldIn.getGameTime() + getPlayerFiringCooldown(player));
		setSecondHandgunFiringCooldown(player, handgun);
	}

	private int getPlayerFiringCooldown(Player player) {
		return Settings.COMMON.items.handgun.maxSkillLevel.get() + HANDGUN_COOLDOWN_SKILL_OFFSET
				- Math.min(player.experienceLevel, Settings.COMMON.items.handgun.maxSkillLevel.get());
	}

	private int getItemUseDuration() {
		return HANDGUN_RELOAD_SKILL_OFFSET + Settings.COMMON.items.handgun.maxSkillLevel.get();
	}

	private void fireBullet(ItemStack handgun, Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide) {
			String magazineType = getMagazineType(handgun);
			if (!magazineShotFactories.containsKey(magazineType)) {
				return;
			}
			spawnShotEntity(handgun, world, player, hand, magazineType);
			world.playSound(null, player.blockPosition(), ModSounds.HANDGUN_SHOT.get(), SoundSource.PLAYERS, 0.5F, 1.2F);

			setBulletCount(handgun, (short) (getBulletCount(handgun) - 1));
			if (getBulletCount(handgun) == 0) {
				setPotionEffects(handgun, Collections.emptyList());
			}
			spawnCasing(player);
		}
	}

	private void spawnShotEntity(ItemStack handgun, Level world, Player player, InteractionHand hand, String magazineType) {
		if (!magazineShotFactories.containsKey(magazineType)) {
			return;
		}
		ShotEntityBase shot = magazineShotFactories.get(magazineType).createShot(world, player, hand).addPotionEffects(getPotionEffects(handgun));
		double motionX = -Mth.sin(player.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(player.getXRot() / 180.0F * (float) Math.PI);
		double motionZ = Mth.cos(player.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(player.getXRot() / 180.0F * (float) Math.PI);
		double motionY = -Mth.sin(player.getXRot() / 180.0F * (float) Math.PI);
		shot.shoot(motionX, motionY, motionZ, 1.2F, 1.0F);
		world.addFreshEntity(shot);
	}

	private void spawnEmptyMagazine(Player player) {
		ItemStack emptyMagazine = new ItemStack(ModItems.EMPTY_MAGAZINE.get());
		if (!player.getInventory().add(emptyMagazine)) {
			player.spawnAtLocation(emptyMagazine, 0.1F);
		}
	}

	private void spawnCasing(Player player) {
		ItemStack emptyCasing = new ItemStack(ModItems.EMPTY_BULLET.get());
		if (!player.getInventory().add(emptyCasing)) {
			player.spawnAtLocation(emptyCasing, 0.1F);
		}
	}

	private boolean hasFilledMagazine(Player player) {
		for (ItemStack stack : player.getInventory().items) {
			if (stack == null) {
				continue;
			}
			if (stack.getItem() instanceof MagazineItem && stack.getItem() != ModItems.EMPTY_MAGAZINE.get()) {
				return true;
			}
		}
		return false;
	}

	private Optional<Integer> getMagazineSlot(Player player) {
		for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
			Item item = player.getInventory().items.get(slot).getItem();
			if (item instanceof MagazineItem && item != ModItems.EMPTY_MAGAZINE.get()) {
				return Optional.of(slot);
			}
		}
		return Optional.empty();
	}

	private int getPlayerReloadDelay(Player player) {
		return Settings.COMMON.items.handgun.maxSkillLevel.get() + HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, Settings.COMMON.items.handgun.maxSkillLevel.get());
	}
}
