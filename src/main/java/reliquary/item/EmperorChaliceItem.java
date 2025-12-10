package reliquary.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import reliquary.reference.Config;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class EmperorChaliceItem extends ToggleableItem {

	public EmperorChaliceItem(Properties properties) {
		super(properties.stacksTo(1).setNoCombineRepair().rarity(Rarity.EPIC));
		NeoForge.EVENT_BUS.addListener(this::onBlockRightClick);
	}

	@Override
	protected void addMoreInformation(ItemStack stack, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.description(this, ".tooltip2");
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 16;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.DRINK;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (level.isClientSide()) {
			return stack;
		}

		if (!(livingEntity instanceof Player player)) {
			return stack;
		}

		int multiplier = Config.COMMON.items.emperorChalice.hungerSatiationMultiplier.get();
		player.getFoodData().eat(1, (float) multiplier / 2);
		if (player.level() instanceof ServerLevel serverLevel) {
			player.hurtServer(serverLevel, player.damageSources().drown(), multiplier);
		}
		return stack;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack emperorChalice = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return super.use(level, player, hand);
		}
		boolean isInDrainMode = isEnabled(emperorChalice);
		BlockHitResult result = getPlayerPOVHitResult(level, player, isInDrainMode ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

		//noinspection ConstantConditions
		if (result == null || result.getType() == HitResult.Type.MISS) {
			if (!isEnabled(emperorChalice)) {
				player.startUsingItem(hand);
			}
			return InteractionResult.SUCCESS;
		} else if (result.getType() == HitResult.Type.BLOCK) {
			if (!level.mayInteract(player, result.getBlockPos()) || !player.mayUseItemAt(result.getBlockPos(), result.getDirection(), emperorChalice)) {
				return InteractionResult.FAIL;
			}

			boolean success;
			if (!isEnabled(emperorChalice)) {
				success = placeWater(level, player, hand, result);
			} else {
				ItemAccess itemAccess = ItemAccess.forStack(emperorChalice);
				ResourceHandler<FluidResource> fluidHandler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
				if (fluidHandler != null) {
					success = !FluidUtil.tryPickupFluid(fluidHandler, player, level, result.getBlockPos(), result.getDirection()).isEmpty();
				} else {
					success = false;
				}
			}
			if (success) {
				return InteractionResult.SUCCESS.heldItemTransformedTo(emperorChalice);
			}
		}

		return InteractionResult.PASS;
	}

	private boolean placeWater(Level level, Player player, InteractionHand hand, BlockHitResult result) {
		FluidResource water = FluidResource.of(Fluids.WATER);
		if (FluidUtil.tryPlaceFluid(water, player, level, hand, result.getBlockPos())) {
			return true;
		}
		return FluidUtil.tryPlaceFluid(water, player, level, hand, result.getBlockPos().relative(result.getDirection()));
	}

	private void onBlockRightClick(PlayerInteractEvent.RightClickBlock evt) {
		if (evt.getItemStack().getItem() == this) {
			Level level = evt.getLevel();
			BlockState state = level.getBlockState(evt.getPos());
			if (!isEnabled(evt.getItemStack()) && state.getBlock() == Blocks.CAULDRON) {
				fillCauldron(evt, level);
			} else if (isEnabled(evt.getItemStack()) && state.getBlock() == Blocks.WATER_CAULDRON && ((LayeredCauldronBlock) state.getBlock()).isFull(state)) {
				emptyCauldron(evt, level);
			}
		}
	}

	private void emptyCauldron(PlayerInteractEvent.RightClickBlock evt, Level level) {
		level.setBlockAndUpdate(evt.getPos(), Blocks.CAULDRON.defaultBlockState());
		cancelEvent(evt);
	}

	private void fillCauldron(PlayerInteractEvent.RightClickBlock evt, Level level) {
		level.setBlockAndUpdate(evt.getPos(), Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
		cancelEvent(evt);
	}

	private void cancelEvent(PlayerInteractEvent.RightClickBlock evt) {
		evt.setUseItem(TriState.FALSE);
		evt.setCanceled(true);
		evt.setCancellationResult(InteractionResult.SUCCESS);
	}
}
