package reliquary.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import reliquary.init.ModDataComponents;
import reliquary.item.util.IScrollableItem;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.NoPlayerBlockItemUseContext;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;
import java.util.StringJoiner;

public class SojournerStaffItem extends ChargeableItem implements IScrollableItem {
	private static final int COOLDOWN = 10;
	public static final int TORCH_SLOT = 0;

	public SojournerStaffItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % COOLDOWN != 0) {
			return;
		}

		if (isEnabled(stack)) {
			scanForMatchingTorchesToFillInternalStorage(stack, player);
		}
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level().isClientSide) {
			return InteractionResult.PASS;
		}
		cycleTorchMode(stack, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	private void scanForMatchingTorchesToFillInternalStorage(ItemStack staff, Player player) {
		runOnHandler(staff, handler -> {
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack stackInSlot = handler.getStackInSlot(slot);
				if (!stackInSlot.isEmpty() || slot == TORCH_SLOT) {
					consumeAndCharge(staff, slot, player, Config.COMMON.items.sojournerStaff.maxCapacityPerItemType.get() - stackInSlot.getCount(), 1, 16);
				}
			}
			consumeAndCharge(staff, handler.getSlots(), player, Config.COMMON.items.sojournerStaff.maxCapacityPerItemType.get(), 1, 16);
		});
	}

	public ItemStack getCurrentTorch(ItemStack stack) {
		return getFromHandler(stack, handler -> getCurrentTorchIndex(stack) < handler.getSlots() ? handler.getStackInSlot(getCurrentTorchIndex(stack)) : ItemStack.EMPTY);
	}

	public int getTorchCount(ItemStack stack) {
		return getFromHandler(stack, handler -> getCurrentTorchIndex(stack) < handler.getSlots() ? handler.getCountInSlot(getCurrentTorchIndex(stack)) : 0);
	}

	private void cycleTorchMode(ItemStack stack, boolean next) {
		ItemStack currentTorch = getCurrentTorch(stack);
		if (getCurrentTorchIndex(stack) != TORCH_SLOT && currentTorch.isEmpty()) {
			return;
		}
		runOnHandler(stack, handler -> {
			int slots = handler.getSlots();
			if (slots == 1) {
				return;
			}
			int currentIndex = getCurrentTorchIndex(stack);
			stack.set(ModDataComponents.TORCH_INDEX, (byte) Math.floorMod(currentIndex + (next ? 1 : -1), slots));
		});
	}

	private int getCurrentTorchIndex(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.TORCH_INDEX, (byte) 0);
	}

	@Override
	protected void addMoreInformation(ItemStack staff, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		StringJoiner joiner = new StringJoiner(";");
		runOnHandler(staff, handler -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stackInSlot = handler.getStackInSlot(i);
				if (!stackInSlot.isEmpty()) {
					joiner.add(stackInSlot.getHoverName().getString() + ": " + stackInSlot.getCount());
				}
			}
		});

		if (getTorchCount(staff) > 0) {
			tooltipBuilder.data(this, ".tooltip.contents", joiner.toString());
			tooltipBuilder.data(this, ".tooltip.placing", getCurrentTorch(staff).getHoverName().getString());
		}

		if (isEnabled(staff)) {
			tooltipBuilder.absorbActive(new ItemStack(Blocks.TORCH).getHoverName().getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return placeTorch(context);
	}

	private InteractionResult placeTorch(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction face = context.getClickedFace();
		ItemStack stack = context.getItemInHand();

		BlockPos placeBlockAt = context.getLevel().getBlockState(pos).canBeReplaced() ? pos : pos.relative(face);

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		ItemStack torch = getCurrentTorch(stack);
		if (player == null || torch.isEmpty() || !(torch.getItem() instanceof BlockItem)) {
			return InteractionResult.FAIL;
		}
		if (!player.mayUseItemAt(placeBlockAt, face, stack) || player.isCrouching()) {
			return InteractionResult.PASS;
		}
		player.swing(hand);

		Block blockToPlace = ((BlockItem) torch.getItem()).getBlock();
		NoPlayerBlockItemUseContext placeContext = new NoPlayerBlockItemUseContext(level, placeBlockAt, new ItemStack(blockToPlace), face);
		if (!placeContext.canPlace() || !removeTorches(player, stack, placeBlockAt)) {
			return InteractionResult.FAIL;
		}
		((BlockItem) torch.getItem()).place(placeContext);
		float gauss = 0.5F + level.random.nextFloat() / 2;
		level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, gauss, gauss, 0.0F),
				placeBlockAt.getX() + 0.5D, placeBlockAt.getY() + 0.5D, placeBlockAt.getZ() + 0.5D,
				0, 0, 0);
		return InteractionResult.SUCCESS;
	}

	private boolean removeTorches(Player player, ItemStack staff, BlockPos placeBlockAt) {
		if (!player.isCreative()) {
			int distance = (int) player.getEyePosition(1).distanceTo(new Vec3(placeBlockAt.getX(), placeBlockAt.getY(), placeBlockAt.getZ()));
			int cost = 1 + distance / Config.COMMON.items.sojournerStaff.tilePerCostMultiplier.get();

			int torchIndex = getCurrentTorchIndex(staff);
			return useCharge(staff, torchIndex, cost);
		}
		return true;
	}

	public boolean removeTorch(ItemStack stack) {
		return useCharge(stack, TORCH_SLOT, 1);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!player.isShiftKeyDown()) {
			HitResult rayTraceResult = longRayTrace(level, player);
			if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
				placeTorch(new UseOnContext(player, hand, (BlockHitResult) rayTraceResult));
			} else {
				ItemStack staff = player.getItemInHand(hand);
				ItemStack torch = getCurrentTorch(staff);
				if (torch.getCount() > torch.getMaxStackSize()) {
					torch = torch.copyWithCount(torch.getMaxStackSize());
				}

				int inserted = InventoryHelper.insertIntoInventory(torch, InventoryHelper.getMainInventoryItemHandlerFrom(player));
				if (inserted > 0) {
					int currentTorchIndex = getCurrentTorchIndex(staff);
					extractStoredCharge(staff, currentTorchIndex, inserted);
					if (currentTorchIndex != TORCH_SLOT && getTorchCount(staff) == 0) {
						removeSlot(staff, currentTorchIndex);
					}
				}
			}
		}
		return super.use(level, player, hand);
	}

	@Override
	public void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		if (chargeStack != null) {
			getFromHandler(containerStack, handler -> handler.insertItemOrAddIntoNewSlotIfNoStackMatches(chargeStack));
		}
	}

	@Override
	protected void extractStoredCharge(ItemStack containerStack, int slot, int chargeToExtract) {
		getFromHandler(containerStack, handler -> handler.extractItem(slot, chargeToExtract, false));
	}

	@Override
	protected boolean removeSlotWhenEmpty(int slot) {
		return slot != TORCH_SLOT;
	}

	@Override
	protected void removeSlot(ItemStack containerStack, int slot) {
		runOnHandler(containerStack, handler -> {
			handler.removeSlot(slot);
			if (getCurrentTorchIndex(containerStack) >= handler.getSlots()) {
				cycleTorchMode(containerStack, false);
			}
		});

	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		return getTorchCount(containerStack);
	}

	private HitResult longRayTrace(Level level, Player player) {
		float f = player.getXRot();
		float f1 = player.getYRot();
		Vec3 vec3d = player.getEyePosition(1.0F);
		float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = Config.COMMON.items.sojournerStaff.maxRange.get();
		Vec3 vec3d1 = vec3d.add(f6 * d0, f5 * d0, f7 * d0);
		return level.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		if (slot == TORCH_SLOT) {
			return stack.is(Items.TORCH);
		}
		return !stack.is(Items.TORCH) && Config.COMMON.items.sojournerStaff.isTorch(stack) && doesNotExistInAnotherSlot(containerStack, slot, stack);
	}

	private boolean doesNotExistInAnotherSlot(ItemStack containerStack, int slot, ItemStack stack) {
		return getFromHandler(containerStack, handler -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				if (i == slot) {
					continue;
				}

				ItemStack stackInSlot = handler.getStackInSlot(i);
				if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stackInSlot, stack)) {
					return false;
				}
			}
			return true;
		});
	}

	@Override
	protected int getContainerSlotLimit(int slot) {
		return Config.COMMON.items.sojournerStaff.maxCapacityPerItemType.get();
	}
}
