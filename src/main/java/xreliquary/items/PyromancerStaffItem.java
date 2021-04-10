package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.items.util.ILeftClickableItem;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RandHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PyromancerStaffItem extends ToggleableItem implements ILeftClickableItem {
	private static final String BLAZE_MODE = "blaze";
	private static final String BLAZE_CHARGES_TAG = BLAZE_MODE;
	private static final String CHARGE_MODE = "charge";
	private static final String ERUPTION_MODE = "eruption";
	private static final int EFFECT_COOLDOWN = 2;
	private static final int INVENTORY_SEARCH_COOLDOWN = EFFECT_COOLDOWN * 5;

	public PyromancerStaffItem() {
		super(new Properties().maxStackSize(1));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity e, int i, boolean f) {
		if (!(e instanceof PlayerEntity) || world.getGameTime() % EFFECT_COOLDOWN != 0) {
			return;
		}

		PlayerEntity player = (PlayerEntity) e;
		doFireballAbsorbEffect(stack, player);

		if (!isEnabled(stack)) {
			doExtinguishEffect(player);
		} else {
			scanForFireChargeAndBlazePowder(stack, player);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable World world, List<ITextComponent> tooltip) {
		AtomicInteger charges = new AtomicInteger(0);
		AtomicInteger blaze = new AtomicInteger(0);
		iterateItems(staff, tag -> {
			String itemName = tag.getString(ITEM_NAME_TAG);
			int quantity = tag.getInt(QUANTITY_TAG);

			if (itemName.equals(RegistryHelper.getItemRegistryName(Items.BLAZE_POWDER))) {
				blaze.set(quantity);
			} else if (itemName.equals(RegistryHelper.getItemRegistryName(Items.FIRE_CHARGE))) {
				charges.set(quantity);
			}
		}, () -> false);
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charges",
				Integer.toString(charges.get()), BLAZE_CHARGES_TAG, Integer.toString(blaze.get())), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active",
					ImmutableMap.of("item", TextFormatting.RED + Items.BLAZE_POWDER.getDisplayName(new ItemStack(Items.BLAZE_POWDER)).getString()
							+ TextFormatting.WHITE + " & " + TextFormatting.RED + Items.FIRE_CHARGE.getDisplayName(new ItemStack(Items.FIRE_CHARGE)).getString()), tooltip);
		}

		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 11;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	public String getMode(ItemStack stack) {
		if (NBTHelper.getString("mode", stack).equals("")) {
			setMode(stack, BLAZE_MODE);
		}
		return NBTHelper.getString("mode", stack);
	}

	private void setMode(ItemStack stack, String s) {
		NBTHelper.putString("mode", stack, s);
	}

	private void cycleMode(ItemStack stack) {
		if (getMode(stack).equals(BLAZE_MODE)) {
			setMode(stack, CHARGE_MODE);
		} else if (getMode(stack).equals(CHARGE_MODE)) {
			setMode(stack, ERUPTION_MODE);
		} else if (getMode(stack).equals(ERUPTION_MODE)) {
			setMode(stack, "flint_and_steel");
		} else {
			setMode(stack, BLAZE_MODE);
		}
	}

	@Override
	public ActionResultType onLeftClickItem(ItemStack stack, LivingEntity entityLiving) {
		if (!entityLiving.isSneaking()) {
			return ActionResultType.CONSUME;
		}
		if (entityLiving.world.isRemote) {
			return ActionResultType.PASS;
		}
		cycleMode(stack);
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			super.onItemRightClick(world, player, hand);
		} else {
			if (getMode(stack).equals(BLAZE_MODE)) {
				if (player.isSwingInProgress) {
					return new ActionResult<>(ActionResultType.PASS, stack);
				}
				player.swingArm(hand);
				shootBlazeFireball(player, stack);
			} else if (getMode(stack).equals(CHARGE_MODE)) {
				if (player.isSwingInProgress) {
					return new ActionResult<>(ActionResultType.PASS, stack);
				}
				player.swingArm(hand);
				Vector3d lookVec = player.getLookVec();
				shootGhastFireball(player, stack, lookVec);
			} else {
				player.setActiveHand(hand);
			}
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	private void shootGhastFireball(PlayerEntity player, ItemStack stack, Vector3d lookVec) {
		if (removeItemFromInternalStorage(stack, Items.FIRE_CHARGE, getFireChargeCost(), player.world.isRemote, player)) {
			player.world.playEvent(player, 1016, player.getPosition(), 0);
			FireballEntity fireball = new FireballEntity(player.world, player, lookVec.x, lookVec.y, lookVec.z);
			fireball.accelerationX = lookVec.x / 3;
			fireball.accelerationY = lookVec.y / 3;
			fireball.accelerationZ = lookVec.z / 3;
			fireball.setPosition(fireball.getPosX() + lookVec.x, player.getPosY() + player.getEyeHeight(), fireball.getPosZ() + lookVec.z);
			player.world.addEntity(fireball);

		}
	}

	private void shootBlazeFireball(PlayerEntity player, ItemStack stack) {
		Vector3d lookVec = player.getLookVec();
		//blaze fireball!
		if (removeItemFromInternalStorage(stack, Items.BLAZE_POWDER, getBlazePowderCost(), player.world.isRemote, player)) {
			player.world.playEvent(player, 1018, player.getPosition(), 0);
			SmallFireballEntity fireball = new SmallFireballEntity(player.world, player, lookVec.x, lookVec.y, lookVec.z);
			fireball.accelerationX = lookVec.x / 3;
			fireball.accelerationY = lookVec.y / 3;
			fireball.accelerationZ = lookVec.z / 3;
			fireball.setPosition(fireball.getPosX() + lookVec.x, player.getPosY() + player.getEyeHeight(), fireball.getPosZ() + lookVec.z);
			player.world.addEntity(fireball);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		if (!(entity instanceof PlayerEntity)) {
			return;
		}
		if (getMode(stack).equals(ERUPTION_MODE)) {
			PlayerEntity player = (PlayerEntity) entity;
			RayTraceResult rayTraceResult = player.pick(12, 1, true);

			if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
				count -= 1;
				count = getUseDuration(stack) - count;

				BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
				doEruptionAuxEffects(player, blockRayTraceResult.getPos().getX(), blockRayTraceResult.getPos().getY(), blockRayTraceResult.getPos().getZ());
				if (count % 10 == 0 && removeItemFromInternalStorage(stack, Items.BLAZE_POWDER, getBlazePowderCost(), player.world.isRemote, player)) {
					doEruptionEffect(player, blockRayTraceResult.getPos().getX(), blockRayTraceResult.getPos().getY(), blockRayTraceResult.getPos().getZ());
				}
			}
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		Direction face = context.getFace();
		World world = context.getWorld();

		if (player == null) {
			return ActionResultType.PASS;
		}

		ItemStack stack = player.getHeldItem(context.getHand());
		if (getMode(stack).equals("flint_and_steel")) {
			BlockPos placeFireAt = context.getPos().offset(face);
			if (!player.canPlayerEdit(placeFireAt, face, stack)) {
				return ActionResultType.PASS;
			} else {
				if (world.isAirBlock(placeFireAt)) {
					world.playSound((double) placeFireAt.getX() + 0.5D, (double) placeFireAt.getY() + 0.5D, (double) placeFireAt.getZ() + 0.5D, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F, false);
					world.setBlockState(placeFireAt, Blocks.FIRE.getDefaultState());
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	private void doEruptionAuxEffects(PlayerEntity player, int soundX, int soundY, int soundZ) {
		player.world.playSound((double) soundX + 0.5D, (double) soundY + 0.5D, (double) soundZ + 0.5D, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.NEUTRAL, 0.2F, 0.03F + (0.07F * random.nextFloat()), false);

		for (int particleCount = 0; particleCount < 2; ++particleCount) {
			double randX = (soundX + 0.5D) + (player.world.rand.nextFloat() - 0.5F) * 5D;
			double randZ = (soundZ + 0.5D) + (player.world.rand.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 4; ++particleCount) {
			double randX = soundX + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 6; ++particleCount) {
			double randX = soundX + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D;
			double randZ = soundZ + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D);
			}
		}
		for (int particleCount = 0; particleCount < 8; ++particleCount) {
			double randX = soundX + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D);
			}
		}
	}

	private void doEruptionEffect(PlayerEntity player, int x, int y, int z) {
		double lowerX = x - 5D + 0.5D;
		double lowerZ = z - 5D + 0.5D;
		double upperX = x + 5D + 0.5D;
		double upperY = y + 5D;
		double upperZ = z + 5D + 0.5D;
		List<MobEntity> entities = player.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(lowerX, y, lowerZ, upperX, upperY, upperZ));

		entities.stream().filter(e -> !e.isEntityEqual(player)).forEach(e -> {
			e.setFire(40);
			if (!e.isImmuneToFire()) {
				e.attackEntityFrom(DamageSource.causePlayerDamage(player), 4F);
			}
		});
	}

	private void scanForFireChargeAndBlazePowder(ItemStack staff, PlayerEntity player) {
		if (player.world.getGameTime() % INVENTORY_SEARCH_COOLDOWN != 0) {
			return;
		}

		int currentFireChargeCount = getInternalStorageItemCount(staff, Items.FIRE_CHARGE);
		consumeAndCharge(player, getFireChargeLimit() - currentFireChargeCount, getFireChargeWorth(), Items.FIRE_CHARGE, 16,
				chargeToAdd -> addItemToInternalStorage(staff, Items.FIRE_CHARGE, chargeToAdd));

		int currentBlazePowderCount = getInternalStorageItemCount(staff, Items.BLAZE_POWDER);
		consumeAndCharge(player, getBlazePowderLimit() - currentBlazePowderCount, getBlazePowderWorth(), Items.BLAZE_POWDER, 16,
				chargeToAdd -> addItemToInternalStorage(staff, Items.BLAZE_POWDER, chargeToAdd));
	}

	private int getFireChargeWorth() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeWorth.get();
	}

	private int getFireChargeCost() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeCost.get();
	}

	private int getFireChargeLimit() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeLimit.get();
	}

	private int getBlazePowderWorth() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderWorth.get();
	}

	private int getBlazePowderCost() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderCost.get();
	}

	private int getBlazePowderLimit() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderLimit.get();
	}

	private int getBlazeAbsorbWorth() {
		return Settings.COMMON.items.pyromancerStaff.blazeAbsorbWorth.get();
	}

	private int getGhastAbsorbWorth() {
		return Settings.COMMON.items.pyromancerStaff.ghastAbsorbWorth.get();
	}

	private void doExtinguishEffect(PlayerEntity player) {
		if (player.isBurning()) {
			player.extinguish();
		}
		BlockPos.getAllInBoxMutable(player.getPosition().add(-3, -3, -3), player.getPosition().add(3, 3, 3)).forEach(pos -> {
			Block block = player.world.getBlockState(pos).getBlock();
			if (block == Blocks.FIRE) {
				player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
				player.world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F);
			}
		});
	}

	private void doFireballAbsorbEffect(ItemStack stack, PlayerEntity player) {
		if (player.world.isRemote) {
			return;
		}
		absorbGhastFireballs(stack, player);
		absorbBlazeFireballs(stack, player);
	}

	private void absorbBlazeFireballs(ItemStack stack, PlayerEntity player) {
		List<SmallFireballEntity> blazeFireballs = player.world.getEntitiesWithinAABB(SmallFireballEntity.class, player.getBoundingBox().grow(3));
		for (SmallFireballEntity fireball : blazeFireballs) {
			if (fireball.func_234616_v_() == player) {
				continue;
			}
			if (hasSpaceForItem(stack, Items.BLAZE_POWDER, getBlazePowderLimit())) {
				for (int particles = 0; particles < 4; particles++) {
					player.world.addParticle(RedstoneParticleData.REDSTONE_DUST, fireball.getPosX(), fireball.getPosY(), fireball.getPosZ(), 0.0D, 1.0D, 1.0D);
				}
				player.world.playSound(fireball.getPosX(), fireball.getPosY(), fireball.getPosZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);

				addItemToInternalStorage(stack, Items.BLAZE_POWDER, getBlazeAbsorbWorth());
			}
			fireball.remove();
		}
	}

	private void absorbGhastFireballs(ItemStack stack, PlayerEntity player) {
		List<FireballEntity> ghastFireballs = player.world.getEntitiesWithinAABB(FireballEntity.class, player.getBoundingBox().grow(4));
		for (FireballEntity fireball : ghastFireballs) {
			if (fireball.func_234616_v_() != player) {
				if (hasSpaceForItem(stack, Items.FIRE_CHARGE, getFireChargeLimit())) {
					addItemToInternalStorage(stack, Items.FIRE_CHARGE, getGhastAbsorbWorth());
					player.world.playSound(fireball.getPosX(), fireball.getPosY(), fireball.getPosZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
				}
				fireball.remove();
			}
		}
	}
}
