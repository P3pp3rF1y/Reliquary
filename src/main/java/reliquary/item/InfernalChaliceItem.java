package reliquary.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class InfernalChaliceItem extends ToggleableItem {
	public InfernalChaliceItem() {
		super(new Properties().stacksTo(1).setNoRepair());

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingIncomingDamageEvent event) {
				return (event.getSource().is(DamageTypeTags.IS_FIRE))
						&& player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CHALICE.get());
			}

			@Override
			public boolean apply(Player player, LivingIncomingDamageEvent event) {
				player.causeFoodExhaustion(event.getAmount() * ((float) Config.COMMON.items.infernalChalice.hungerCostPercent.get() / 100F));
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	@Override
	protected void addMoreInformation(ItemStack chalice, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", chalice.getOrDefault(ModDataComponents.FLUID_CONTENTS, SimpleFluidContent.EMPTY).getAmount());
		if (isEnabled(chalice)) {
			tooltipBuilder.description("tooltip.reliquary.place");
		} else {
			tooltipBuilder.description("tooltip.reliquary.drain");
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return super.use(level, player, hand);
		}

		BlockHitResult result = getPlayerPOVHitResult(level, player, isEnabled(stack) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

		if (result.getType() != HitResult.Type.BLOCK) {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		} else {
			BlockPos pos = result.getBlockPos();
			if (!level.mayInteract(player, pos)) {
				return new InteractionResultHolder<>(InteractionResult.PASS, stack);
			}

			Direction face = result.getDirection();
			if (!player.mayUseItemAt(pos, face, stack)) {
				return new InteractionResultHolder<>(InteractionResult.PASS, stack);
			}

			IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
			if (fluidHandler == null) {
				return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
			}

			return interactWithFluidHandler(level, player, stack, pos, face, fluidHandler);
		}
	}

	private InteractionResultHolder<ItemStack> interactWithFluidHandler(Level level, Player player, ItemStack stack, BlockPos pos, Direction face, IFluidHandlerItem fluidHandler) {
		BlockState blockState = level.getBlockState(pos);
		if (isEnabled(stack)) {
			if (blockState.getBlock() == Blocks.LAVA && blockState.getValue(LiquidBlock.LEVEL) == 0 && fluidHandler.fill(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				Fluids.LAVA.getPickupSound().ifPresent(soundEvent -> level.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F));
				fluidHandler.fill(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			}
		} else {
			FluidStack fluidDrained = fluidHandler.drain(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
			if (player.isCreative() || fluidDrained.getAmount() == FluidType.BUCKET_VOLUME) {
				BlockPos adjustedPos = pos.relative(face);
				if (tryPlaceContainedLiquid(player, level, adjustedPos) && !player.isCreative()) {
					fluidHandler.drain(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
					return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
				}
			}
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, stack);
	}

	private boolean tryPlaceContainedLiquid(Player player, Level level, BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		if (!blockState.canBeReplaced(Fluids.LAVA)) {
			return false;
		} else {
			level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
			level.playSound(player, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1, 1);
			return true;
		}
	}

	public static int getFluidBucketAmount(ItemStack stack) {
		IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		return fluidHandler != null ? fluidHandler.getFluidInTank(0).getAmount() / FluidType.BUCKET_VOLUME : 0;
	}
}
