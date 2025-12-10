package reliquary.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.MutableStackItemAccess;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class InfernalChaliceItem extends ToggleableItem {
	public InfernalChaliceItem(Properties properties) {
		super(properties.stacksTo(1).setNoCombineRepair());

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
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return super.use(level, player, hand);
		}

		BlockHitResult result = getPlayerPOVHitResult(level, player, isEnabled(stack) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

		if (result.getType() != HitResult.Type.BLOCK) {
			return InteractionResult.PASS;
		} else {
			BlockPos pos = result.getBlockPos();
			if (!level.mayInteract(player, pos)) {
				return InteractionResult.PASS;
			}

			Direction face = result.getDirection();
			if (!player.mayUseItemAt(pos, face, stack)) {
				return InteractionResult.PASS;
			}

			MutableStackItemAccess itemAccess = new MutableStackItemAccess(stack);
			ResourceHandler<FluidResource> fluidHandler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
			if (fluidHandler == null) {
				return InteractionResult.FAIL;
			}

			return interactWithFluidHandler(level, player, stack, pos, face, fluidHandler, itemAccess);
		}
	}

	private InteractionResult interactWithFluidHandler(Level level, Player player, ItemStack stack, BlockPos pos, Direction face, ResourceHandler<FluidResource> fluidHandler, MutableStackItemAccess itemAccess) {
		BlockState blockState = level.getBlockState(pos);
		if (isEnabled(stack)) {
			if (blockState.getBlock() == Blocks.LAVA && blockState.getValue(LiquidBlock.LEVEL) == 0) {
				try (Transaction tx = Transaction.openRoot()) {
					if (fluidHandler.insert(FluidResource.of(Fluids.LAVA), FluidType.BUCKET_VOLUME, tx) == FluidType.BUCKET_VOLUME) {
						level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						Fluids.LAVA.getPickupSound().ifPresent(soundEvent -> level.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F));
						tx.commit();
						return InteractionResult.SUCCESS.heldItemTransformedTo(itemAccess.getStack());
					}
				}
			}
		} else {
			try (Transaction tx = Transaction.openRoot()) {
				if (!player.isCreative() && fluidHandler.extract(FluidResource.of(Fluids.LAVA), FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME) {
					return InteractionResult.PASS;
				}
				BlockPos adjustedPos = pos.relative(face);
				if (tryPlaceContainedLiquid(player, level, adjustedPos)) {
					if (!player.isCreative()) {
						tx.commit();
					}
					return InteractionResult.SUCCESS.heldItemTransformedTo(itemAccess.getStack());
				}

			}
		}
		return InteractionResult.PASS;
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
		ResourceHandler<FluidResource> fluidHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
		return fluidHandler != null ? fluidHandler.getAmountAsInt(0) / FluidType.BUCKET_VOLUME : 0;
	}
}
