package reliquary.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModItems;
import reliquary.items.util.fluid.FluidHandlerInfernalChalice;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.LanguageHelper;
import reliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class InfernalChaliceItem extends ToggleableItem {
	public InfernalChaliceItem() {
		super(new Properties().stacksTo(1).setNoRepair());

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingAttackEvent event) {
				return (event.getSource() == DamageSource.LAVA || event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.IN_FIRE)
						&& player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CHALICE.get());
			}

			@Override
			public boolean apply(Player player, LivingAttackEvent event) {
				player.causeFoodExhaustion(event.getAmount() * ((float) Settings.COMMON.items.infernalChalice.hungerCostPercent.get() / 100F));
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack chalice, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2",
				Map.of("amount", String.valueOf(NBTHelper.getInt("fluidStacks", chalice))), tooltip);

		if (isEnabled(chalice)) {
			LanguageHelper.formatTooltip("tooltip.place", tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.drain", tooltip);
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return super.use(world, player, hand);
		}

		BlockHitResult result = getPlayerPOVHitResult(world, player, isEnabled(stack) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

		if (result.getType() != HitResult.Type.BLOCK) {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		} else {
			BlockPos pos = result.getBlockPos();
			if (!world.mayInteract(player, pos)) {
				return new InteractionResultHolder<>(InteractionResult.PASS, stack);
			}

			Direction face = result.getDirection();
			if (!player.mayUseItemAt(pos, face, stack)) {
				return new InteractionResultHolder<>(InteractionResult.PASS, stack);
			}

			return getFluidHandler(stack).map(fluidHandler -> interactWithFluidHandler(world, player, stack, pos, face, fluidHandler)).orElse(new InteractionResultHolder<>(InteractionResult.FAIL, stack));
		}
	}

	private InteractionResultHolder<ItemStack> interactWithFluidHandler(Level world, Player player, ItemStack stack, BlockPos pos, Direction face, IFluidHandlerItem fluidHandler) {
		BlockState blockState = world.getBlockState(pos);
		if (isEnabled(stack)) {
			if (blockState.getBlock() == Blocks.LAVA && blockState.getValue(LiquidBlock.LEVEL) == 0 && fluidHandler.fill(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				fluidHandler.fill(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			}
		} else {
			FluidStack fluidDrained = fluidHandler.drain(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
			if (player.isCreative() || fluidDrained.getAmount() == FluidType.BUCKET_VOLUME) {
				BlockPos adjustedPos = pos.relative(face);
				if (tryPlaceContainedLiquid(world, adjustedPos) && !player.isCreative()) {
					fluidHandler.drain(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
					return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
				}
			}
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, stack);
	}

	private LazyOptional<IFluidHandlerItem> getFluidHandler(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	}

	private boolean tryPlaceContainedLiquid(Level world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Material material = blockState.getMaterial();
		if (!world.isEmptyBlock(pos) && material.isSolid()) {
			return false;
		} else {
			world.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
			return true;
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidHandlerInfernalChalice(stack);
	}
}
