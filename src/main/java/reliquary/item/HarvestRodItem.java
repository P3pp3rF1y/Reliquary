package reliquary.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import reliquary.block.FertileLilyPadBlock;
import reliquary.entity.ReliquaryFakePlayer;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.item.util.HarvestRodCache;
import reliquary.item.util.IScrollableItem;
import reliquary.reference.Config;
import reliquary.util.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HarvestRodItem extends ChargeableItem implements IScrollableItem {
	private static final int AOE_START_COOLDOWN = 10;
	public static final int BONEMEAL_SLOT = 0;

	public HarvestRodItem() {
		super(new Properties().stacksTo(1).setNoRepair());
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.DARK_GREEN);
	}

	@Override
	protected void addMoreInformation(ItemStack rod, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", getBoneMealCount(rod));
		for (int slot = 1; slot < getCountOfPlantables(rod); slot++) {
			ItemStack plantable = getPlantableInSlot(rod, slot);
			tooltipBuilder.charge(this, ".tooltip3", plantable.getItem().getName(plantable).getString(), getPlantableQuantity(rod, slot));
		}

		if (isEnabled(rod)) {
			tooltipBuilder.absorbActive(new ItemStack(Items.BONE_MEAL).getHoverName().getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	private int getBonemealLimit() {
		return Config.COMMON.items.harvestRod.boneMealLimit.get();
	}

	private int getBonemealWorth() {
		return Config.COMMON.items.harvestRod.boneMealWorth.get();
	}

	public int getBonemealCost() {
		return Config.COMMON.items.harvestRod.boneMealCost.get();
	}

	public int getLuckRolls() {
		return Config.COMMON.items.harvestRod.boneMealLuckRolls.get();
	}

	public int getLuckPercent() {
		return Config.COMMON.items.harvestRod.boneMealLuckPercentChance.get();
	}

	private int getBreakRadius() {
		return Config.COMMON.items.harvestRod.aoeRadius.get();
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		if (slot == BONEMEAL_SLOT) {
			return stack.is(Items.BONE_MEAL);
		}
		return isPlantable(stack);
	}

	@Override
	protected int getContainerSlotLimit(int slot) {
		if (slot == BONEMEAL_SLOT) {
			return getBonemealLimit();
		}
		return Config.COMMON.items.harvestRod.maxCapacityPerPlantable.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 10 != 0) {
			return;
		}

		if (isEnabled(stack)) {
			int currentCharge = getBoneMealCount(stack);
			consumeAndCharge(stack, BONEMEAL_SLOT, player, getBonemealLimit() - currentCharge, 1, 16);
			consumePlantables(stack, player);
		}
	}

	@Override
	public void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		if (slot == BONEMEAL_SLOT) {
			int currentCharge = getBoneMealCount(containerStack);
			setBoneMealCount(containerStack, Math.min(getBonemealLimit(), currentCharge + chargeToAdd));
		} else {
			incrementPlantable(containerStack, new ItemStack(Items.BONE_MEAL), chargeToAdd);
		}
	}

	@Override
	protected void extractStoredCharge(ItemStack harvestRod, int slot, int chargeToExtract) {
		if (slot == BONEMEAL_SLOT) {
			super.extractStoredCharge(harvestRod, slot, chargeToExtract);
		} else {
			runOnHandler(harvestRod, h -> h.extractItem(slot, chargeToExtract, false));
		}
	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		if (slot == BONEMEAL_SLOT) {
			return getBoneMealCount(containerStack);
		} else {
			return getPlantableQuantity(containerStack, (byte) slot);
		}
	}

	@Override
	protected int getSlotWorth(int slot) {
		return slot == BONEMEAL_SLOT ? getBonemealWorth() : 1;
	}

	@Override
	protected boolean removeSlotWhenEmpty(int slot) {
		return slot != BONEMEAL_SLOT;
	}

	@Override
	protected void removeSlot(ItemStack harvestRod, int slot) {
		runOnHandler(harvestRod, h -> h.removeSlot(slot));
		shiftModeOnEmptyPlantable(harvestRod, (byte) slot);
	}

	private void consumePlantables(ItemStack harvestRod, Player player) {
		int leftToInsert = 16;

		for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
			ItemStack currentStack = player.getInventory().items.get(slot);
			if (isPlantable(currentStack)) {
				int countInserted = incrementPlantable(harvestRod, currentStack, leftToInsert);
				leftToInsert -= countInserted;
				currentStack.shrink(countInserted);
				player.getInventory().items.set(slot, currentStack.isEmpty() ? ItemStack.EMPTY : currentStack);
				if (leftToInsert == 0) {
					break;
				}
			}
		}
	}

	public static boolean isPlantable(ItemStack currentStack) {
		return currentStack.is(Tags.Items.SEEDS) || currentStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) || currentStack.getItem() instanceof SpecialPlantable;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		if (player.level().isClientSide) {
			return true;
		}

		boolean brokenBlock = false;

		BlockState blockState = player.level().getBlockState(pos);
		if (canBreakBlock(blockState)) {
			for (int xOff = -getBreakRadius(); xOff <= getBreakRadius(); xOff++) {
				for (int yOff = -getBreakRadius(); yOff <= getBreakRadius(); yOff++) {
					for (int zOff = -getBreakRadius(); zOff <= getBreakRadius(); zOff++) {
						brokenBlock |= doHarvestBlockBreak(blockState.getBlock(), player.getMainHandItem(), pos, player, xOff, yOff, zOff);
					}
				}
			}
		}

		return !brokenBlock;
	}

	private boolean doHarvestBlockBreak(Block initialBlock, ItemStack stack, BlockPos pos, Player player, int xOff, int yOff, int zOff) {
		pos = pos.offset(xOff, yOff, zOff);

		BlockState blockState = player.level().getBlockState(pos);
		Block block = blockState.getBlock();

		if ((initialBlock == Blocks.MELON || initialBlock == Blocks.PUMPKIN) && !(block == Blocks.MELON || block == Blocks.PUMPKIN)) {
			return false;
		}

		if (!canBreakBlock(blockState)) {
			return false;
		}
		if (block instanceof FertileLilyPadBlock) {
			return false;
		}

		if (player.level().isClientSide) {
			for (int particles = 0; particles <= 8; particles++) {
				player.level().levelEvent(player, 2001, pos, Block.getId(blockState));
			}
		} else if (player.level() instanceof ServerLevel serverLevel) {
			List<ItemStack> drops = Block.getDrops(blockState, serverLevel, pos, null, player, stack);
			for (ItemStack itemStack : drops) {
				float f = 0.7F;
				double d = (serverLevel.random.nextFloat() * f) + (1.0F - f) * 0.5D;
				double d1 = (serverLevel.random.nextFloat() * f) + (1.0F - f) * 0.5D;
				double d2 = (serverLevel.random.nextFloat() * f) + (1.0F - f) * 0.5D;
				ItemEntity entityitem = new ItemEntity(player.level(), pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, itemStack);
				entityitem.setPickUpDelay(10);
				player.level().addFreshEntity(entityitem);
			}

			player.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			player.awardStat(Stats.BLOCK_MINED.get(blockState.getBlock()));
			player.causeFoodExhaustion(0.01F);
		}

		return true;
	}

	private static boolean canBreakBlock(BlockState blockState) {
		Block block = blockState.getBlock();
		return blockState.is(BlockTags.CROPS) || block instanceof BushBlock || block == Blocks.MELON || block == Blocks.PUMPKIN;
	}

	private void boneMealBlock(ItemStack stack, Player player, Level level, BlockPos pos) {
		ItemStack fakeItemStack = new ItemStack(Items.BONE_MEAL);

		boolean usedRod = false;
		for (int repeatedUses = 0; repeatedUses <= getLuckRolls(); repeatedUses++) {
			if ((repeatedUses == 0 || level.random.nextInt(100) <= getLuckPercent()) && BoneMealItem.applyBonemeal(fakeItemStack, level, pos, player)) {
				if (!usedRod) {
					usedRod = true;
				}
				player.level().levelEvent(1505, pos, 15);
				player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));
			}
		}

		if (usedRod && !player.isCreative()) {
			useCharge(stack, BONEMEAL_SLOT, getBonemealCost());
		}
	}

	public int getBoneMealCount(ItemStack stack) {
		return getFromHandler(stack, handler -> handler.getSlots() > 0 ? handler.getCountInSlot(BONEMEAL_SLOT) : 0);
	}

	public void setBoneMealCount(ItemStack harvestRod, int boneMealCount) {
		runOnHandler(harvestRod, h -> h.setStackInSlot(BONEMEAL_SLOT, boneMealCount == 0 ? ItemStack.EMPTY : new ItemStack(Items.BONE_MEAL, boneMealCount)));
	}

	private int incrementPlantable(ItemStack harvestRod, ItemStack plantable, int maxCount) {
		return getFromHandler(harvestRod, h -> {
			ItemStack plantableCopy = plantable.copy();
			plantableCopy.setCount(Math.min(maxCount, plantableCopy.getCount()));
			ItemStack remaining = h.insertItemOrAddIntoNewSlotIfNoStackMatches(plantableCopy);
			return plantableCopy.getCount() - remaining.getCount();
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return super.use(level, player, hand);
		}

		player.startUsingItem(hand);

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 300;
	}

	@Override
	public void releaseUsing(ItemStack harvestRod, Level level, LivingEntity entity, int timeLeft) {
		if (entity.level().isClientSide || !(entity instanceof Player player)) {
			return;
		}

		BlockHitResult result = getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.ANY);

		if (result.getType() == HitResult.Type.BLOCK) {
			HarvestRodCache harvestRodCache = harvestRod.getCapability(ModItems.HARVEST_ROD_CACHE_CAPABILITY);
			if (harvestRodCache == null) {
				return;
			}
			harvestRodCache.reset();
			BlockPos pos = result.getBlockPos();

			switch (getMode(harvestRod)) {
				case BONE_MEAL:
					if (getBoneMealCount(harvestRod) >= getBonemealCost() || player.isCreative()) {
						boneMealBlock(harvestRod, player, level, pos);
					}
					break;
				case PLANTABLE:
					if (getPlantableQuantity(harvestRod, getCurrentPlantableSlot(harvestRod)) > 0 || player.isCreative()) {
						plantItem(harvestRod, player, pos, player.getUsedItemHand());
					}
					break;
				case HOE:
					hoeLand(level, pos);
					break;
				default:
			}

		} else {
			removeStackFromCurrent(harvestRod, player);
		}
	}

	private void hoeLand(Level level, BlockPos pos) {
		ItemStack fakeHoe = new ItemStack(Items.WOODEN_HOE);
		ReliquaryFakePlayer fakePlayer = FakePlayerFactory.get((ServerLevel) level);
		fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, fakeHoe);

		if (Items.WOODEN_HOE.useOn(ItemHelper.getItemUseContext(pos, fakePlayer)) == InteractionResult.SUCCESS) {
			level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	private void removeStackFromCurrent(ItemStack stack, Player player) {
		if (getMode(stack) == Mode.BONE_MEAL && getBoneMealCount(stack) > 0) {
			ItemStack boneMealStack = new ItemStack(Items.BONE_MEAL);
			int numberToAdd = Math.min(boneMealStack.getMaxStackSize(), getBoneMealCount(stack));
			IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
			int numberAdded = InventoryHelper.tryToAddToInventory(boneMealStack, playerInventory, numberToAdd);
			setBoneMealCount(stack, getBoneMealCount(stack) - numberAdded);
		} else if (getMode(stack) == Mode.PLANTABLE) {
			byte plantableSlot = getCurrentPlantableSlot(stack);
			ItemStack plantableStack = getCurrentPlantable(stack);
			int plantableQuantity = getPlantableQuantity(stack, plantableSlot);
			int numberToAdd = Math.min(plantableStack.getMaxStackSize(), plantableQuantity);
			IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
			int numberAdded = InventoryHelper.tryToAddToInventory(plantableStack, playerInventory, numberToAdd);

			extractStoredCharge(stack, plantableSlot, numberAdded);
			if (getPlantableQuantity(stack, plantableSlot) == 0) {
				removeSlot(stack, plantableSlot);
				shiftModeOnEmptyPlantable(stack, plantableSlot);
			}
		}
	}

	private void shiftModeOnEmptyPlantable(ItemStack harvestRod, byte plantableSlot) {
		if (plantableSlot > 0) {
			setCurrentPlantableSlot(harvestRod, (byte) (plantableSlot - 1));
		}
		cycleMode(harvestRod, true);
	}

	private void plantItem(ItemStack harvestRod, Player player, BlockPos pos, InteractionHand hand) {
		byte plantableSlot = getCurrentPlantableSlot(harvestRod);
		ItemStack fakePlantableStack = getCurrentPlantable(harvestRod).copy();
		fakePlantableStack.setCount(1);

		ReliquaryFakePlayer fakePlayer = FakePlayerFactory.get((ServerLevel) player.level());
		fakePlayer.setItemInHand(hand, fakePlantableStack);

		if (fakePlantableStack.useOn(ItemHelper.getItemUseContext(pos, fakePlayer)).consumesAction()) {
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));

			if (!player.isCreative()) {
				useCharge(harvestRod, plantableSlot, 1);
			}
		}
	}

	public ItemStack getCurrentPlantable(ItemStack harvestRod) {
		int currentSlot = getCurrentPlantableSlot(harvestRod);
		return getPlantableInSlot(harvestRod, currentSlot);
	}

	public ItemStack getPlantableInSlot(ItemStack harvestRod, int slot) {
		if (slot <= BONEMEAL_SLOT) {
			return ItemStack.EMPTY;
		}
		return getFromHandler(harvestRod, h -> h.getSlots() > slot ? h.getStackInSlot(slot) : ItemStack.EMPTY);
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack harvestRod, int remainingUseDuration) {
		if (livingEntity.level().isClientSide || !(livingEntity instanceof Player player)) {
			return;
		}

		if (isCoolDownOver(harvestRod, remainingUseDuration, livingEntity)) {
			BlockHitResult result = getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.ANY);
			if (result.getType() == HitResult.Type.BLOCK) {
				HarvestRodCache harvestRodCache = harvestRod.getCapability(ModItems.HARVEST_ROD_CACHE_CAPABILITY);
				if (harvestRodCache != null) {
					doAction(harvestRod, player, level, harvestRodCache, result.getBlockPos());
				}
			}
		}
	}

	private void doAction(ItemStack harvestRod, Player player, Level level, HarvestRodCache cache, BlockPos pos) {
		switch (getMode(harvestRod)) {
			case BONE_MEAL:
				if (getBoneMealCount(harvestRod) >= getBonemealCost() || player.isCreative()) {
					getNextBlockToBoneMeal(level, cache, pos, Config.COMMON.items.harvestRod.aoeRadius.get())
							.ifPresent(blockToBoneMeal -> boneMealBlock(harvestRod, player, level, blockToBoneMeal));
				}
				break;
			case PLANTABLE:
				if (getPlantableQuantity(harvestRod, getCurrentPlantableSlot(harvestRod)) >= 1 || player.isCreative()) {
					getNextBlockToPlantOn(level, cache, pos, Config.COMMON.items.harvestRod.aoeRadius.get(), getCurrentPlantable(harvestRod))
							.ifPresent(blockToPlantOn -> plantItem(harvestRod, player, blockToPlantOn, player.getUsedItemHand()));
				}
				break;
			case HOE:
				getNextBlockToHoe(level, cache, pos, Config.COMMON.items.harvestRod.aoeRadius.get()).ifPresent(blockToHoe -> hoeLand(level, blockToHoe));
				break;
			default:
				break;
		}
	}

	private Optional<BlockPos> getNextBlockToHoe(Level level, HarvestRodCache cache, BlockPos pos, int range) {
		if (cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos())) {
			fillQueue(cache, pos, range, currentPos -> {
				BlockState blockState = level.getBlockState(currentPos);
				Block block = blockState.getBlock();

				return level.isEmptyBlock(currentPos.above()) && (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT);
			});
		}

		return cache.getNextBlockInQueue();
	}

	private void fillQueue(HarvestRodCache cache, BlockPos pos, int range, Predicate<BlockPos> isValidBlock) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();
		BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range))
				.forEach(currentPos -> {
					if (isValidBlock.test(currentPos)) {
						cache.addBlockToQueue(currentPos.immutable());
					}
				});
	}

	private Optional<BlockPos> getNextBlockToPlantOn(Level level, HarvestRodCache cache, BlockPos pos, int range, ItemStack plantable) {
		if (cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos())) {
			fillQueueToPlant(level, cache, pos, range, plantable);
		}

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToPlant(Level level, HarvestRodCache cache, BlockPos pos, int range, ItemStack plantable) {
		boolean checkerboard = false;
		boolean bothOddOrEven = false;

		if (plantable.getItem() == Items.PUMPKIN_SEEDS || plantable.getItem() == Items.MELON_SEEDS) {
			checkerboard = true;
			boolean xEven = pos.getX() % 2 == 0;
			boolean zEven = pos.getZ() % 2 == 0;
			bothOddOrEven = xEven == zEven;
		}

		boolean finalCheckerboard = checkerboard;
		boolean finalBothOddOrEven = bothOddOrEven;
		fillQueue(cache, pos, range, currentPos ->
				(!finalCheckerboard || (finalBothOddOrEven == ((currentPos.getX() % 2 == 0) == (currentPos.getZ() % 2 == 0))))
						&& level.isEmptyBlock(currentPos.above())
						&& canPlacePlantableAt(level, currentPos.above(), plantable));
	}

	public static boolean canPlacePlantableAt(Level level, BlockPos pos, ItemStack plantable) {
		if (plantable.getItem() instanceof SpecialPlantable specialPlantable && specialPlantable.canPlacePlantAtPosition(plantable, level, pos, null)) {
			return true;
		}
		return (plantable.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().canSurvive(level, pos));
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level().isClientSide) {
			return InteractionResult.PASS;
		}
		cycleMode(stack, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	private boolean isCoolDownOver(ItemStack stack, int count, LivingEntity livingEntity) {
		return getUseDuration(stack, livingEntity) - count >= AOE_START_COOLDOWN && (getUseDuration(stack, livingEntity) - count) % Config.COMMON.items.harvestRod.aoeCooldown.get() == 0;
	}

	private Optional<BlockPos> getNextBlockToBoneMeal(Level level, HarvestRodCache cache, BlockPos pos, int range) {
		if (cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos())) {
			fillQueue(cache, pos, range, currentPos -> {
				BlockState blockState = level.getBlockState(currentPos);
				return blockState.getBlock() instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(level, currentPos, blockState);
			});
		}

		return cache.getNextBlockInQueue();
	}

	private void cycleMode(ItemStack harvestRod, boolean next) {
		Mode currentMode = getMode(harvestRod);
		int plantableCount = getCountOfPlantables(harvestRod);
		if (next) {
			setNextMode(harvestRod, currentMode, plantableCount);
		} else {
			setPreviousMode(harvestRod, currentMode, plantableCount);
		}
	}

	private void setPreviousMode(ItemStack harvestRod, Mode currentMode, int plantableCount) {
		if (currentMode == Mode.PLANTABLE && getCurrentPlantableSlot(harvestRod) > 1) {
			setCurrentPlantableSlot(harvestRod, (byte) (getCurrentPlantableSlot(harvestRod) - 1));
			return;
		}
		Mode previousMode = currentMode.previous();
		if (previousMode == Mode.PLANTABLE) {
			if (plantableCount == 0) {
				previousMode = previousMode.previous();
			} else {
				setCurrentPlantableSlot(harvestRod, (byte) plantableCount);
			}
		}
		setMode(harvestRod, previousMode);
	}

	private void setNextMode(ItemStack harvestRod, Mode currentMode, int plantableCount) {
		if (currentMode == Mode.PLANTABLE && plantableCount > getCurrentPlantableSlot(harvestRod)) {
			setCurrentPlantableSlot(harvestRod, (byte) (getCurrentPlantableSlot(harvestRod) + 1));
			return;
		}
		Mode nextMode = currentMode.next();
		if (nextMode == Mode.PLANTABLE) {
			if (plantableCount == 0) {
				nextMode = nextMode.next();
			} else {
				setCurrentPlantableSlot(harvestRod, (byte) 1);
			}
		}
		setMode(harvestRod, nextMode);
	}

	public int getCountOfPlantables(ItemStack harvestRod) {
		return getFromHandler(harvestRod, h -> Math.max(h.getSlots() - 1, 0));
	}

	public byte getCurrentPlantableSlot(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.PLANTABLE_INDEX, (byte) -1);
	}

	private void setCurrentPlantableSlot(ItemStack stack, byte index) {
		stack.set(ModDataComponents.PLANTABLE_INDEX, index);
	}

	private void setMode(ItemStack stack, Mode mode) {
		stack.set(ModDataComponents.HARVEST_ROD_MODE, mode);
	}

	public Mode getMode(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.HARVEST_ROD_MODE, Mode.BONE_MEAL);
	}

	public int getPlantableQuantity(ItemStack harvestRod, int slot) {
		if (slot <= BONEMEAL_SLOT) {
			return 0;
		}
		return getFromHandler(harvestRod, h -> h.getSlots() > slot ? h.getCountInSlot(slot) : 0);
	}

	public enum Mode implements StringRepresentable {
		BONE_MEAL, PLANTABLE, HOE;

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
		public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Mode.class);

		@Override
		public String getSerializedName() {
			return name();
		}

		public Mode next() {
			return VALUES[(ordinal() + 1) % VALUES.length];
		}

		public Mode previous() {
			return VALUES[Math.floorMod(ordinal() - 1, VALUES.length)];
		}

		private static final Mode[] VALUES;

		static {
			ImmutableMap.Builder<String, Mode> builder = new ImmutableMap.Builder<>();
			for (Mode value : Mode.values()) {
				builder.put(value.getSerializedName(), value);
			}
			VALUES = values();
		}
	}
}