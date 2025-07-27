package reliquary.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import reliquary.entity.shot.ShotBase;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.init.ModSounds;
import reliquary.reference.Config;
import reliquary.util.RegistryHelper;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HandgunItem extends ItemBase {
	private static final int HANDGUN_RELOAD_SKILL_OFFSET = 10;
	private static final int HANDGUN_COOLDOWN_SKILL_OFFSET = 5;

	public interface IShotFactory {
		ShotBase createShot(Level level, Player player, InteractionHand hand);
	}

	private final Map<ResourceLocation, IShotFactory> magazineShotFactories = new HashMap<>();
	private final Map<ResourceLocation, Supplier<BulletItem>> magazineBulletItems = new HashMap<>();

	public void registerMagazine(ResourceLocation magazineRegistryName, IShotFactory factory, Supplier<BulletItem> getBulletItem) {
		magazineShotFactories.put(magazineRegistryName, factory);
		magazineBulletItems.put(magazineRegistryName, getBulletItem);
	}

	public HandgunItem() {
		super(new Properties().stacksTo(1), Config.COMMON.disable.disableHandgun);
	}

	private short getBulletCount(ItemStack handgun) {
		return handgun.getOrDefault(ModDataComponents.BULLET_COUNT, (short) 0);
	}

	public ItemStack getBulletStack(ItemStack handgun) {
		return getMagazineType(handgun).map(magazineType -> {
			if (!magazineBulletItems.containsKey(magazineType)) {
				return new ItemStack(ModItems.EMPTY_BULLET.get(), 1);
			}
			BulletItem bulletItem = magazineBulletItems.get(magazineType).get();
			ItemStack bulletStack = new ItemStack(bulletItem, getBulletCount(handgun));
			bulletStack.set(DataComponents.POTION_CONTENTS, getPotionContents(handgun));
			return bulletStack;
		}).orElseGet(() -> new ItemStack(ModItems.EMPTY_BULLET.get(), 1));
	}

	private void setBulletCount(ItemStack handgun, short bulletCount) {
		handgun.set(ModDataComponents.BULLET_COUNT, bulletCount);
	}

	private Optional<ResourceLocation> getMagazineType(ItemStack handgun) {
		return Optional.ofNullable(handgun.get(ModDataComponents.MAGAZINE_TYPE));
	}

	private void setMagazineType(ItemStack handgun, ItemStack magazine) {
		handgun.set(ModDataComponents.MAGAZINE_TYPE, RegistryHelper.getRegistryName(magazine.getItem()));
	}

	private boolean hasAmmo(ItemStack handgun) {
		return getBulletCount(handgun) > 0;
	}

	public long getCooldown(ItemStack handgun) {
		return handgun.getOrDefault(ModDataComponents.COOLDOWN_TIME, 0L);
	}

	private void setCooldown(ItemStack handgun, long coolDownTime) {
		handgun.set(ModDataComponents.COOLDOWN_TIME, coolDownTime);
	}

	private PotionContents getPotionContents(ItemStack handgun) {
		return handgun.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
	}

	private void setPotionEffects(ItemStack handgun, PotionContents potionEffects) {
		PotionHelper.cleanPotionEffects(handgun);
		PotionHelper.addPotionContentsToStack(handgun, potionEffects);
	}

	@Override
	protected void addMoreInformation(ItemStack handgun, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		if (hasAmmo(handgun)) {
			tooltipBuilder
					.data(this, ".tooltip2", getBulletCount(handgun), getMagazineName(handgun))
					.potionEffects(handgun);
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack handgun) {
		return hasAmmo(handgun);
	}

	private String getMagazineName(ItemStack handgun) {
		return getMagazineType(handgun).map(magazineType -> BuiltInRegistries.ITEM.get(magazineType).getName(new ItemStack(Items.AIR)).getString()).orElse("");
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

	private boolean isCooldownOver(Level level, ItemStack handgun) {
		return getCooldown(handgun) < level.getGameTime();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack handgun = player.getItemInHand(hand);

		if (getBulletCount(handgun) > 0 && !isCooldownOver(level, handgun) && otherHandgunCooledDownMore(player, hand, handgun)) {
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
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack handgun, int remainingUseDuration) {
		if (livingEntity.level().isClientSide || !(livingEntity instanceof Player player)) {
			return;
		}

		int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
		int actualCount = remainingUseDuration - maxUseOffset;
		actualCount -= 1;

		if (actualCount == 0 || (isCooldownOver(livingEntity.level(), handgun) && getBulletCount(handgun) > 0)) {
			player.releaseUsingItem();
		}
	}

	@Override
	public int getUseDuration(ItemStack handgun, LivingEntity livingEntity) {
		return getItemUseDuration();
	}

	@Override
	public void releaseUsing(ItemStack handgun, Level level, LivingEntity livingEntity, int timeLeft) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}

		// fire bullet
		if (hasAmmo(handgun)) {
			if (isCooldownOver(player.level(), handgun)) {
				setFiringCooldown(handgun, level, player);
				fireBullet(handgun, level, player, handgun == player.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
			}
			return;
		}

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(handgun, player.level().getGameTime() + 12);

		getMagazineSlot(player).ifPresent(slot -> {
			ItemStack magazine = player.getInventory().items.get(slot);
			setMagazineType(handgun, magazine);
			setPotionEffects(handgun, magazine.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
			magazine.shrink(1);
			if (magazine.isEmpty()) {
				player.getInventory().items.set(slot, ItemStack.EMPTY);
			}
			player.swing(player.getUsedItemHand());
			spawnEmptyMagazine(player);
			setBulletCount(handgun, (short) 8);
			player.level().playSound(null, player.blockPosition(), ModSounds.HANDGUN_LOAD.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
			setFiringCooldown(handgun, level, player);
		});

		if (getBulletCount(handgun) == 0) {
			setPotionEffects(handgun, PotionContents.EMPTY);
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
		if (potentialHandgun.getItem() == this && isCooldownOver(player.level(), potentialHandgun)) {
			setCooldown(potentialHandgun, player.level().getGameTime() + (getPlayerFiringCooldown(player) / 2));
		}
	}

	private void setFiringCooldown(ItemStack handgun, Level level, Player player) {
		setCooldown(handgun, level.getGameTime() + getPlayerFiringCooldown(player));
		setSecondHandgunFiringCooldown(player, handgun);
	}

	private int getPlayerFiringCooldown(Player player) {
		return Config.COMMON.items.handgun.maxSkillLevel.get() + HANDGUN_COOLDOWN_SKILL_OFFSET
				- Math.min(player.experienceLevel, Config.COMMON.items.handgun.maxSkillLevel.get());
	}

	private int getItemUseDuration() {
		return HANDGUN_RELOAD_SKILL_OFFSET + Config.COMMON.items.handgun.maxSkillLevel.get();
	}

	private void fireBullet(ItemStack handgun, Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide) {
			getMagazineType(handgun).filter(magazineShotFactories::containsKey).ifPresent(magazineType -> {
				spawnShotEntity(handgun, level, player, hand, magazineType);
				level.playSound(null, player.blockPosition(), ModSounds.HANDGUN_SHOT.get(), SoundSource.PLAYERS, 0.5F, 1.2F);

				setBulletCount(handgun, (short) (getBulletCount(handgun) - 1));
				if (getBulletCount(handgun) == 0) {
					setPotionEffects(handgun, PotionContents.EMPTY);
				}
				spawnCasing(player);
			});
		}
	}

	private void spawnShotEntity(ItemStack handgun, Level level, Player player, InteractionHand hand, ResourceLocation magazineType) {
		if (!magazineShotFactories.containsKey(magazineType)) {
			return;
		}
		ShotBase shot = magazineShotFactories.get(magazineType).createShot(level, player, hand).addPotionContents(getPotionContents(handgun));
		
		if (level instanceof ServerLevel serverLevel) {
			int simulationDistance = serverLevel.getServer().getPlayerList().getSimulationDistance();
			HitResult hitResult = player.pick(simulationDistance, 1, false);
			float velocity = 1.2F;
			float inaccuracy = 0.2F;
			if (hitResult.getType() != HitResult.Type.MISS) {
				shot.shoot(hitResult.getLocation().x - shot.getX(), hitResult.getLocation().y - shot.getY(), hitResult.getLocation().z - shot.getZ(), velocity, inaccuracy);
			} else {
				double motionX = -Mth.sin(player.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(player.getXRot() / 180.0F * (float) Math.PI);
				double motionZ = Mth.cos(player.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(player.getXRot() / 180.0F * (float) Math.PI);
				double motionY = -Mth.sin(player.getXRot() / 180.0F * (float) Math.PI);
				shot.shoot(motionX, motionY, motionZ, velocity, inaccuracy);
			}
		}

		level.addFreshEntity(shot);
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
		return Config.COMMON.items.handgun.maxSkillLevel.get() + HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, Config.COMMON.items.handgun.maxSkillLevel.get());
	}
}
