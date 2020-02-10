package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import xreliquary.blocks.FertileLilyPadBlock;
import xreliquary.entities.EntityXRFakePlayer;
import xreliquary.init.ModCapabilities;
import xreliquary.items.util.FilteredBigItemStack;
import xreliquary.items.util.HarvestRodItemStackHandler;
import xreliquary.items.util.IHarvestRodCache;
import xreliquary.network.PacketCountSync;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.ItemHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.XRFakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class HarvestRodItem extends ToggleableItem {
	public static final String BONE_MEAL_MODE = "bone_meal";
	private static final String PLANTABLE_MODE = "plantable";
	public static final String HOE_MODE = "hoe";
	private static final String MODE_NBT_TAG = "mode";
	private static final String PLANTABLE_INDEX_NBT_TAG = "plantable_index";
	private static final int AOE_START_COOLDOWN = 10;

	public HarvestRodItem() {
		super(Names.Items.HARVEST_ROD, new Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack rod, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charge", Integer.toString(getBoneMealCount(rod, true))), tooltip);
		for (int slot = 1; slot < getCountPlantable(rod, true); slot++) {
			ItemStack plantable = getPlantableInSlot(rod, slot, true);
			LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip3",
					ImmutableMap.of("plantable", plantable.getItem().getDisplayName(plantable).getString(), "charge", Integer.toString(getPlantableQuantity(rod, slot, true))), tooltip);
		}

		if (isEnabled(rod)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.WHITE + new ItemStack(Items.BONE_MEAL).getDisplayName().toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	private int getBonemealLimit() {
		return Settings.COMMON.items.harvestRod.boneMealLimit.get();
	}

	private int getBonemealWorth() {
		return Settings.COMMON.items.harvestRod.boneMealWorth.get();
	}

	public int getBonemealCost() {
		return Settings.COMMON.items.harvestRod.boneMealCost.get();
	}

	public int getLuckRolls() {
		return Settings.COMMON.items.harvestRod.boneMealLuckRolls.get();
	}

	public int getLuckPercent() {
		return Settings.COMMON.items.harvestRod.boneMealLuckPercentChance.get();
	}

	private int getBreakRadius() {
		return Settings.COMMON.items.harvestRod.aoeRadius.get();
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilitySerializable<CompoundNBT>() {
			HarvestRodItemStackHandler itemHandler = new HarvestRodItemStackHandler();

			@Override
			public CompoundNBT serializeNBT() {
				return itemHandler.serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundNBT tagCompound) {
				itemHandler.deserializeNBT(tagCompound);
			}

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> itemHandler));
			}
		};
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote) {
			return;
		}
		PlayerEntity player = null;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
		}
		if (player == null) {
			return;
		}

		if (isEnabled(stack)) {
			if (getBoneMealCount(stack) + getBonemealWorth() <= getBonemealLimit()) {
				if (InventoryHelper.consumeItem(new ItemStack(Items.BONE_MEAL), player)) {
					setBoneMealCount(stack, getBoneMealCount(stack) + getBonemealWorth(), player);
				}
			}

			consumePlantables(stack, player);
		}
	}

	private void consumePlantables(ItemStack harvestRod, PlayerEntity player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack currentStack = player.inventory.mainInventory.get(slot);
			if (currentStack.getItem() instanceof IPlantable && incrementPlantable(harvestRod, currentStack, player)) {
				InventoryHelper.consumeItem(currentStack, player, 0, 1);
				break;
			}
		}
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (player.world.isRemote) {
			return false;
		}

		boolean brokenBlock = false;

		Block block = player.world.getBlockState(pos).getBlock();
		if (block instanceof IPlantable || block == Blocks.MELON || block == Blocks.PUMPKIN) {
			for (int xOff = -getBreakRadius(); xOff <= getBreakRadius(); xOff++) {
				for (int yOff = -getBreakRadius(); yOff <= getBreakRadius(); yOff++) {
					for (int zOff = -getBreakRadius(); zOff <= getBreakRadius(); zOff++) {
						brokenBlock |= doHarvestBlockBreak(block, stack, pos, player, xOff, yOff, zOff);
					}
				}
			}
		}

		return brokenBlock;
	}

	private boolean doHarvestBlockBreak(Block initialBlock, ItemStack stack, BlockPos pos, PlayerEntity player, int xOff, int yOff, int zOff) {
		pos = pos.add(xOff, yOff, zOff);

		BlockState blockState = player.world.getBlockState(pos);
		Block block = blockState.getBlock();

		if ((initialBlock == Blocks.MELON || initialBlock == Blocks.PUMPKIN) && !(block == Blocks.MELON || block == Blocks.PUMPKIN)) {
			return false;
		}

		if (!(block instanceof IPlantable || block == Blocks.MELON || block == Blocks.PUMPKIN)) {
			return false;
		}
		if (block instanceof FertileLilyPadBlock) {
			return false;
		}

		if (player.world.isRemote) {
			for (int particles = 0; particles <= 8; particles++) {
				player.world.playEvent(player, 2001, pos, Block.getStateId(blockState));
			}
		} else if (player.world instanceof ServerWorld) {
			List<ItemStack> drops = Block.getDrops(blockState, (ServerWorld) player.world, pos, null, player, stack);
			Random rand = new Random();

			for (ItemStack itemStack : drops) {
				float f = 0.7F;
				double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				ItemEntity entityitem = new ItemEntity(player.world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemStack);
				entityitem.setPickupDelay(10);
				player.world.addEntity(entityitem);
			}

			player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
			player.addStat(Stats.BLOCK_MINED.get(blockState.getBlock()));
			player.addExhaustion(0.01F);
		}

		return true;
	}

	private void boneMealBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos, boolean updateNBT) {
		ItemStack fakeItemStack = new ItemStack(Items.BONE_MEAL);

		boolean usedRod = false;
		for (int repeatedUses = 0; repeatedUses <= getLuckRolls(); repeatedUses++) {
			if ((repeatedUses == 0 || world.rand.nextInt(100) <= getLuckPercent()) && BoneMealItem.applyBonemeal(fakeItemStack, world, pos, player)) {
				if (!usedRod) {
					usedRod = true;
				}
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
			}
		}

		if (usedRod && !player.isCreative()) {
			setBoneMealCount(stack, getBoneMealCount(stack) - getBonemealCost(), player, updateNBT);
		}
	}

	public int getBoneMealCount(ItemStack stack) {
		return getBoneMealCount(stack, false);
	}

	public int getBoneMealCount(ItemStack stack, boolean isClient) {
		if (isClient) {
			return NBTHelper.getContainedStackCount(stack, 0);
		}
		return getFromHandler(stack, HarvestRodItemStackHandler::getBoneMealCount).orElse(0);
	}

	public void setBoneMealCount(ItemStack harvestRod, int boneMealCount) {
		setBoneMealCount(harvestRod, boneMealCount, null, true);
	}

	private <T> Optional<T> getFromHandler(ItemStack harvestRod, Function<HarvestRodItemStackHandler, T> get) {
		return InventoryHelper.getFromHandler(harvestRod, get, HarvestRodItemStackHandler.class);
	}

	private void runOnHandler(ItemStack harvestRod, Consumer<HarvestRodItemStackHandler> run) {
		InventoryHelper.runOnItemHandler(harvestRod, run, HarvestRodItemStackHandler.class);
	}

	private void setBoneMealCount(ItemStack harvestRod, int boneMealCount, PlayerEntity player) {
		runOnHandler(harvestRod, h -> {
			h.setBoneMealCount(boneMealCount);
			updateContainedItemNBT(harvestRod, player, (short) 0, ItemStack.EMPTY, boneMealCount);
		});
	}

	private void setBoneMealCount(ItemStack harvestRod, int boneMealCount, @Nullable PlayerEntity player, boolean updateNBT) {
		runOnHandler(harvestRod, h -> {
			h.setBoneMealCount(boneMealCount);
			updateContainedItemNBT(harvestRod, player, (short) 0, ItemStack.EMPTY, boneMealCount, updateNBT);
		});
	}

	private boolean incrementPlantable(ItemStack harvestRod, ItemStack plantable, PlayerEntity player) {
		return getFromHandler(harvestRod, h -> {
			ItemStack plantableCopy = plantable.copy();
			plantableCopy.setCount(1);
			return h.insertPlantable(plantableCopy).map(bigStackSlot -> {
				updateContainedItemNBT(harvestRod, player, bigStackSlot.shortValue(), plantableCopy, getPlantableQuantity(harvestRod, bigStackSlot));
				return true;
			}).orElse(false);
		}).orElse(false);
	}

	private void updateContainedItemNBT(ItemStack harvestRod, PlayerEntity player, short slot, ItemStack stack, int count) {
		updateContainedItemNBT(harvestRod, player, slot, stack, count, true);
	}

	private void updateContainedItemNBT(ItemStack harvestRod, @Nullable PlayerEntity player, short slot, ItemStack stack, int count, boolean udpateNbt) {
		if (udpateNbt && player !=null && player.isHandActive() && (player.getHeldItemMainhand() == harvestRod || player.getHeldItemOffhand() == harvestRod)) {
			Hand hand = player.getHeldItemMainhand() == harvestRod ? Hand.MAIN_HAND : Hand.OFF_HAND;
			PacketHandler.sendToClient((ServerPlayerEntity) player, new PacketCountSync(hand, slot, stack, count));
		} else {
			NBTHelper.updateContainedStack(harvestRod, slot, stack, count);
		}
	}

	private void decrementPlantable(ItemStack harvestRod, int slot, PlayerEntity player, boolean updateNBT) {
		runOnHandler(harvestRod, h -> h.decrementPlantable(slot));
		getFromHandler(harvestRod, h -> h.getBigStack(slot))
				.ifPresent(plantable -> updateContainedItemNBT(harvestRod, player, (short) slot, plantable.getFilterStack(), plantable.getAmount(), updateNBT));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			return super.onItemRightClick(world, player, hand);
		}

		player.setActiveHand(hand);

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 300;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack harvestRod, World world, LivingEntity entity, int timeLeft) {
		if (entity.world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		RayTraceResult result = rayTrace(player.world, player, RayTraceContext.FluidMode.ANY);

		if (result.getType() == RayTraceResult.Type.BLOCK) {
			harvestRod.getCapability(ModCapabilities.HARVEST_ROD_CACHE, null).ifPresent(IHarvestRodCache::reset);
			BlockPos pos = ((BlockRayTraceResult) result).getPos();

			String mode = getMode(harvestRod);
			switch (mode) {
				case BONE_MEAL_MODE:
					if (getBoneMealCount(harvestRod) >= getBonemealCost() || player.isCreative()) {
						boneMealBlock(harvestRod, player, world, pos, true);
					}
					break;
				case PLANTABLE_MODE:
					if (getPlantableQuantity(harvestRod, getCurrentPlantableSlot(harvestRod)) > 0 || player.isCreative()) {
						plantItem(harvestRod, player, pos, player.getActiveHand(), true);
					}
					break;
				case HOE_MODE:
					hoeLand(world, pos);
					break;
				default:
			}

		} else {
			removeStackFromCurrent(harvestRod, player);
		}
	}

	private void hoeLand(World world, BlockPos pos) {
		ItemStack fakeHoe = new ItemStack(Items.WOODEN_HOE);
		EntityXRFakePlayer fakePlayer = XRFakePlayerFactory.get((ServerWorld) world);
		fakePlayer.setHeldItem(Hand.MAIN_HAND, fakeHoe);

		if (Items.WOODEN_HOE.onItemUse(ItemHelper.getItemUseContext(pos, fakePlayer)) == ActionResultType.SUCCESS) {
			world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	private void removeStackFromCurrent(ItemStack stack, PlayerEntity player) {
		if (getMode(stack).equals(BONE_MEAL_MODE)) {
			ItemStack boneMealStack = new ItemStack(Items.BONE_MEAL);
			int numberToAdd = Math.min(boneMealStack.getMaxStackSize(), getBoneMealCount(stack));
			int numberAdded = InventoryHelper.getItemHandlerFrom(player).map(handler -> InventoryHelper.tryToAddToInventory(boneMealStack, handler, numberToAdd)).orElse(0);
			setBoneMealCount(stack, getBoneMealCount(stack) - numberAdded, player, true);
		} else if (getMode(stack).equals(PLANTABLE_MODE)) {
			byte plantableSlot = getCurrentPlantableSlot(stack);
			ItemStack plantableStack = getCurrentPlantable(stack);
			int plantableQuantity = getPlantableQuantity(stack, plantableSlot);

			int numberToAdd = Math.min(plantableStack.getMaxStackSize(), plantableQuantity);
			int numberAdded = InventoryHelper.getItemHandlerFrom(player).map(handler -> InventoryHelper.tryToAddToInventory(plantableStack, handler, numberToAdd)).orElse(0);

			int updatedPlantableQuantity = getPlantableQuantity(stack, plantableSlot) - numberAdded;

			setPlantableQuantity(stack, plantableSlot, updatedPlantableQuantity, player);
			if (updatedPlantableQuantity == 0) {
				shiftModeOnEmptyPlantable(stack, plantableSlot);
			}
		}
	}

	public void shiftModeOnEmptyPlantable(ItemStack harvestRod, byte plantableSlot) {
		if (plantableSlot > 0) {
			setCurrentPlantableSlot(harvestRod, (byte) (plantableSlot - 1));
		}
		cycleMode(harvestRod);
	}

	private void plantItem(ItemStack harvestRod, PlayerEntity player, BlockPos pos, Hand hand, boolean updateNBT) {
		byte plantableSlot = getCurrentPlantableSlot(harvestRod);
		ItemStack fakePlantableStack = getCurrentPlantable(harvestRod).copy();
		fakePlantableStack.setCount(1);

		EntityXRFakePlayer fakePlayer = XRFakePlayerFactory.get((ServerWorld) player.world);
		fakePlayer.setHeldItem(hand, fakePlantableStack);

		if (fakePlantableStack.onItemUse(ItemHelper.getItemUseContext(pos, fakePlayer)) == ActionResultType.SUCCESS) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));

			if (!player.isCreative()) {
				int plantableQuantity = getPlantableQuantity(harvestRod, plantableSlot);
				decrementPlantable(harvestRod, plantableSlot, player, updateNBT);
				if (plantableQuantity <= 1) {
					shiftModeOnEmptyPlantable(harvestRod, plantableSlot);
				}
			}
		}
	}

	private ItemStack getCurrentPlantable(ItemStack harvestRod) {
		return getCurrentPlantable(harvestRod, false);
	}

	public ItemStack getCurrentPlantable(ItemStack harvestRod, boolean isClient) {
		int currentSlot = getCurrentPlantableSlot(harvestRod);
		if (isClient) {
			return NBTHelper.getContainedStack(harvestRod, currentSlot);
		}

		return getPlantableInSlot(harvestRod, currentSlot);
	}

	public ItemStack getPlantableInSlot(ItemStack harvestRod, int slot) {
		return getPlantableInSlot(harvestRod, slot, false);
	}

	private ItemStack getPlantableInSlot(ItemStack harvestRod, int slot, boolean isClient) {
		if (isClient) {
			return NBTHelper.getContainedStack(harvestRod, slot);
		}

		return getFromHandler(harvestRod, h -> h.getBigStack(slot).getFullStack()).orElse(ItemStack.EMPTY);
	}

	@Override
	public void onUsingTick(ItemStack harvestRod, LivingEntity entity, int count) {
		if (entity.world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		if (isCoolDownOver(harvestRod, count)) {
			RayTraceResult result = rayTrace(player.world, player, RayTraceContext.FluidMode.ANY);
			if (result.getType() == RayTraceResult.Type.BLOCK) {
				BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
				World world = player.world;
				harvestRod.getCapability(ModCapabilities.HARVEST_ROD_CACHE, null).ifPresent(cache -> {
					switch (getMode(harvestRod)) {
						case BONE_MEAL_MODE:
							if (getBoneMealCount(harvestRod) >= getBonemealCost() || player.isCreative()) {
								getNextBlockToBoneMeal(world, cache, blockResult.getPos(), Settings.COMMON.items.harvestRod.aoeRadius.get())
										.ifPresent(blockToBoneMeal -> boneMealBlock(harvestRod, player, world, blockToBoneMeal, false));
							}
							break;
						case PLANTABLE_MODE:
							if (getPlantableQuantity(harvestRod, getCurrentPlantableSlot(harvestRod)) >= 1 || player.isCreative()) {
								getNextBlockToPlantOn(world, cache, blockResult.getPos(), Settings.COMMON.items.harvestRod.aoeRadius.get(), (IPlantable) getCurrentPlantable(harvestRod).getItem())
										.ifPresent(blockToPlantOn -> plantItem(harvestRod, player, blockToPlantOn, player.getActiveHand(), false));
							}
							break;
						case HOE_MODE:
							getNextBlockToHoe(world, cache, blockResult.getPos(), Settings.COMMON.items.harvestRod.aoeRadius.get()).ifPresent(blockToHoe -> hoeLand(world, blockToHoe));
							break;
						default:
							break;
					}
				});
			}
		}
	}

	private Optional<BlockPos> getNextBlockToHoe(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		if (cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos())) {
			fillQueueToHoe(world, cache, pos, range);
		}

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToHoe(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();
		for (int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for (int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					BlockState blockState = world.getBlockState(currentPos);
					Block block = blockState.getBlock();

					if (world.isAirBlock(currentPos.up()) && (block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.COARSE_DIRT)) {
						cache.addBlockToQueue(currentPos);
					}
				}
			}
		}

	}

	private Optional<BlockPos> getNextBlockToPlantOn(World world, IHarvestRodCache cache, BlockPos pos, int range, IPlantable plantable) {
		if (cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos())) {
			fillQueueToPlant(world, cache, pos, range, plantable);
		}

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToPlant(World world, IHarvestRodCache cache, BlockPos pos, int range, IPlantable plantable) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();

		boolean checkerboard = false;
		boolean bothOddOrEven = false;

		if (plantable == Items.PUMPKIN_SEEDS || plantable == Items.MELON_SEEDS) {
			checkerboard = true;
			boolean xEven = pos.getX() % 2 == 0;
			boolean zEven = pos.getZ() % 2 == 0;
			bothOddOrEven = xEven == zEven;
		}

		for (int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for (int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					BlockState blockState = world.getBlockState(currentPos);
					if ((!checkerboard || (bothOddOrEven == ((currentPos.getX() % 2 == 0) == (currentPos.getZ() % 2 == 0)))) && blockState.getBlock().canSustainPlant(blockState, world, pos, Direction.UP, plantable) && world.isAirBlock(currentPos.up())) {
						cache.addBlockToQueue(currentPos);
					}
				}
			}
		}
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (entity.world.isRemote) {
			return false;
		}
		if (entity.isSneaking()) {
			cycleMode(stack);
			return true;
		}
		return false;
	}

	private boolean isCoolDownOver(ItemStack stack, int count) {
		return getUseDuration(stack) - count >= AOE_START_COOLDOWN && (getUseDuration(stack) - count) % Settings.COMMON.items.harvestRod.aoeCooldown.get() == 0;
	}

	private Optional<BlockPos> getNextBlockToBoneMeal(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		if (cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos())) {
			fillQueueToBoneMeal(world, cache, pos, range);
		}

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToBoneMeal(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();
		for (int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for (int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					BlockState blockState = world.getBlockState(currentPos);
					if (blockState.getBlock() instanceof IGrowable && ((IGrowable) blockState.getBlock()).canGrow(world, currentPos, blockState, world.isRemote)) {
						cache.addBlockToQueue(currentPos);
					}
				}
			}
		}
	}

	private void cycleMode(ItemStack harvestRod) {
		String currentMode = getMode(harvestRod);
		int plantableCount = getCountPlantable(harvestRod);
		switch (currentMode) {
			case BONE_MEAL_MODE:
				if (plantableCount > 0) {
					setMode(harvestRod, PLANTABLE_MODE);
					setCurrentPlantableSlot(harvestRod, (byte) 1);
				} else {
					setMode(harvestRod, HOE_MODE);
				}
				break;
			case PLANTABLE_MODE:
				if (plantableCount > getCurrentPlantableSlot(harvestRod)) {
					setCurrentPlantableSlot(harvestRod, (byte) (getCurrentPlantableSlot(harvestRod) + 1));
				} else {
					setMode(harvestRod, HOE_MODE);
				}
				break;
			case HOE_MODE:
				setMode(harvestRod, BONE_MEAL_MODE);
				break;
			default:
				break;
		}
	}

	public int getCountPlantable(ItemStack harvestRod) {
		return getCountPlantable(harvestRod, false);
	}

	private int getCountPlantable(ItemStack harvestRod, boolean isClient) {
		if (isClient) {
			return NBTHelper.getCountContainedStacks(harvestRod);
		}

		return getFromHandler(harvestRod, HarvestRodItemStackHandler::getCountPlantable).orElse(0);
	}

	public byte getCurrentPlantableSlot(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTag() ? stack.getTag().getByte(PLANTABLE_INDEX_NBT_TAG) : (byte) -1;
	}

	private void setCurrentPlantableSlot(ItemStack stack, byte index) {
		if (stack.hasTag()) {
			//noinspection ConstantConditions
			stack.getTag().putByte(PLANTABLE_INDEX_NBT_TAG, index);
		}
	}

	private void setMode(ItemStack stack, String mode) {
		NBTHelper.putString(MODE_NBT_TAG, stack, mode);
	}

	public String getMode(ItemStack stack) {
		String mode = NBTHelper.getString(MODE_NBT_TAG, stack);
		return mode.equals("") ? BONE_MEAL_MODE : mode;
	}

	public int getPlantableQuantity(ItemStack harvestRod, int parentSlot) {
		return getPlantableQuantity(harvestRod, parentSlot, false);
	}

	public int getPlantableQuantity(ItemStack harvestRod, int slot, boolean isClient) {
		if (isClient) {
			return NBTHelper.getContainedStackCount(harvestRod, slot);
		}

		return getFromHandler(harvestRod, h->h.getTotalAmount(slot)).orElse(0);
	}

	public Optional<FilteredBigItemStack> setPlantableQuantity(ItemStack harvestRod, byte plantableSlot, int quantity) {
		return getFromHandler(harvestRod, h -> {
					h.setTotalAmount(plantableSlot, quantity);
			return h.getBigStack(plantableSlot);
		});
	}

	private void setPlantableQuantity(ItemStack harvestRod, byte plantableSlot, int quantity, PlayerEntity player) {
		setPlantableQuantity(harvestRod, plantableSlot, quantity)
				.ifPresent(bigStack -> updateContainedItemNBT(harvestRod, player, plantableSlot, bigStack.getFilterStack(), bigStack.getAmount(), true));
	}
}