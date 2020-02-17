package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.items.util.fluid.FluidHandlerInfernalChalice;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class InfernalChaliceItem extends ToggleableItem {
	public InfernalChaliceItem() {
		super("infernal_chalice", new Properties().maxStackSize(1).setNoRepair());

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingAttackEvent event) {
				return (event.getSource() == DamageSource.LAVA || event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.IN_FIRE)
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CHALICE);
			}

			@Override
			public boolean apply(PlayerEntity player, LivingAttackEvent event) {
				player.addExhaustion(event.getAmount() * ((float) Settings.COMMON.items.infernalChalice.hungerCostPercent.get() / 100F));
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
	protected void addMoreInformation(ItemStack chalice, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2",
				ImmutableMap.of("amount", String.valueOf(NBTHelper.getInt("fluidStacks", chalice))), tooltip);

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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			return super.onItemRightClick(world, player, hand);
		}

		RayTraceResult result = rayTrace(world, player, isEnabled(stack) ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

		if (result.getType() != RayTraceResult.Type.BLOCK) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		} else {
			BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
			BlockPos pos = blockResult.getPos();
			if (!world.isBlockModifiable(player, pos)) {
				return new ActionResult<>(ActionResultType.PASS, stack);
			}

			Direction face = blockResult.getFace();
			if (!player.canPlayerEdit(pos, face, stack)) {
				return new ActionResult<>(ActionResultType.PASS, stack);
			}

			return getFluidHandler(stack).map(fluidHandler -> interactWithFluidHandler(world, player, stack, pos, face, fluidHandler)).orElse(new ActionResult<>(ActionResultType.FAIL, stack));
		}
	}

	private ActionResult<ItemStack> interactWithFluidHandler(World world, PlayerEntity player, ItemStack stack, BlockPos pos, Direction face, IFluidHandlerItem fluidHandler) {
		BlockState blockState = world.getBlockState(pos);
		if (isEnabled(stack)) {
			if (blockState.getBlock() == Blocks.LAVA && blockState.get(FlowingFluidBlock.LEVEL) == 0 && fluidHandler.fill(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				fluidHandler.fill(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
				return new ActionResult<>(ActionResultType.SUCCESS, stack);
			}
		} else {
			FluidStack fluidDrained = fluidHandler.drain(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
			if (player.isCreative() || fluidDrained.getAmount() == FluidAttributes.BUCKET_VOLUME) {
				BlockPos adjustedPos = pos.offset(face);
				if (tryPlaceContainedLiquid(world, adjustedPos) && !player.isCreative()) {
					fluidHandler.drain(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
					return new ActionResult<>(ActionResultType.SUCCESS, stack);
				}
			}
		}
		return new ActionResult<>(ActionResultType.PASS, stack);
	}

	private LazyOptional<IFluidHandlerItem> getFluidHandler(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	}

	private boolean tryPlaceContainedLiquid(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Material material = blockState.getMaterial();
		if (!world.isAirBlock(pos) && material.isSolid()) {
			return false;
		} else {
			world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
			return true;
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FluidHandlerInfernalChalice(stack);
	}
}
