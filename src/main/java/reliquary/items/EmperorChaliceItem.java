package reliquary.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import reliquary.items.util.fluid.FluidHandlerEmperorChalice;
import reliquary.reference.Settings;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class EmperorChaliceItem extends ToggleableItem {

	public EmperorChaliceItem() {
		super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC));
		MinecraftForge.EVENT_BUS.addListener(this::onBlockRightClick);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable Level world, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.description(this, ".tooltip2");
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidHandlerEmperorChalice(stack);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
		if (world.isClientSide) {
			return stack;
		}

		if (!(entityLiving instanceof Player player)) {
			return stack;
		}

		int multiplier = Settings.COMMON.items.emperorChalice.hungerSatiationMultiplier.get();
		player.getFoodData().eat(1, (float) multiplier / 2);
		player.hurt(DamageSource.DROWN, multiplier);
		return stack;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack emperorChalice = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return super.use(world, player, hand);
		}
		boolean isInDrainMode = isEnabled(emperorChalice);
		BlockHitResult result = getPlayerPOVHitResult(world, player, isInDrainMode ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

		//noinspection ConstantConditions
		if (result == null) {
			if (!isEnabled(emperorChalice)) {
				player.startUsingItem(hand);
			}
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, emperorChalice);
		} else if (result.getType() == HitResult.Type.BLOCK) {
			if (!world.mayInteract(player, result.getBlockPos()) || !player.mayUseItemAt(result.getBlockPos(), result.getDirection(), emperorChalice)) {
				return new InteractionResultHolder<>(InteractionResult.FAIL, emperorChalice);
			}

			if (emperorChalice.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(fluidHandler -> {
				if (!isEnabled(emperorChalice)) {
					return placeWater(world, player, hand, fluidHandler, result);
				} else {
					return FluidUtil.tryPickUpFluid(emperorChalice, player, world, result.getBlockPos(), result.getDirection()).isSuccess();
				}
			}).orElse(false)) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, emperorChalice);
			}
		}

		return new InteractionResultHolder<>(InteractionResult.PASS, emperorChalice);
	}

	private boolean placeWater(Level world, Player player, InteractionHand hand, IFluidHandlerItem fluidHandler, BlockHitResult result) {
		if (FluidUtil.tryPlaceFluid(player, world, hand, result.getBlockPos(), fluidHandler, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME))) {
			return true;
		}
		return FluidUtil.tryPlaceFluid(player, world, hand, result.getBlockPos().relative(result.getDirection()), fluidHandler, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));
	}

	private void onBlockRightClick(PlayerInteractEvent.RightClickBlock evt) {
		if (evt.getItemStack().getItem() == this) {
			Level world = evt.getLevel();
			BlockState state = world.getBlockState(evt.getPos());
			if (!isEnabled(evt.getItemStack()) && state.getBlock() == Blocks.CAULDRON) {
				fillCauldron(evt, world);
			} else if (isEnabled(evt.getItemStack()) && state.getBlock() == Blocks.WATER_CAULDRON && ((LayeredCauldronBlock) state.getBlock()).isFull(state)) {
				emptyCauldron(evt, world);
			}
		}
	}

	private void emptyCauldron(PlayerInteractEvent.RightClickBlock evt, Level world) {
		world.setBlockAndUpdate(evt.getPos(), Blocks.CAULDRON.defaultBlockState());
		cancelEvent(evt);
	}

	private void fillCauldron(PlayerInteractEvent.RightClickBlock evt, Level world) {
		world.setBlockAndUpdate(evt.getPos(), Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
		cancelEvent(evt);
	}

	private void cancelEvent(PlayerInteractEvent.RightClickBlock evt) {
		evt.setUseItem(Event.Result.DENY);
		evt.setCanceled(true);
		evt.setCancellationResult(InteractionResult.SUCCESS);
	}
}
