package xreliquary.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import xreliquary.items.util.fluid.FluidHandlerEmperorChalice;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class EmperorChaliceItem extends ToggleableItem {

	public EmperorChaliceItem() {
		super(new Properties().maxStackSize(1).setNoRepair().rarity(Rarity.EPIC));
		MinecraftForge.EVENT_BUS.addListener(this::onBlockRightClick);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", tooltip);
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
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FluidHandlerEmperorChalice(stack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving) {
		if (world.isRemote) {
			return stack;
		}

		if (!(entityLiving instanceof PlayerEntity)) {
			return stack;
		}

		PlayerEntity player = (PlayerEntity) entityLiving;

		int multiplier = Settings.COMMON.items.emperorChalice.hungerSatiationMultiplier.get();
		player.getFoodStats().addStats(1, (float) multiplier / 2);
		player.attackEntityFrom(DamageSource.DROWN, multiplier);
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack emperorChalice = player.getHeldItem(hand);
		if (player.isSneaking()) {
			return super.onItemRightClick(world, player, hand);
		}
		boolean isInDrainMode = isEnabled(emperorChalice);
		BlockRayTraceResult result = rayTrace(world, player, isInDrainMode ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

		//noinspection ConstantConditions
		if (result == null) {
			if (!isEnabled(emperorChalice)) {
				player.setActiveHand(hand);
			}
			return new ActionResult<>(ActionResultType.SUCCESS, emperorChalice);
		} else if (result.getType() == RayTraceResult.Type.BLOCK) {
			if (!world.isBlockModifiable(player, result.getPos()) || !player.canPlayerEdit(result.getPos(), result.getFace(), emperorChalice)) {
				return new ActionResult<>(ActionResultType.FAIL, emperorChalice);
			}

			if (emperorChalice.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(fluidHandler -> {
				if (!isEnabled(emperorChalice)) {
					return placeWater(world, player, hand, fluidHandler, result);
				} else {
					return FluidUtil.tryPickUpFluid(emperorChalice, player, world, result.getPos(), result.getFace()).isSuccess();
				}
			}).orElse(false)) {
				return new ActionResult<>(ActionResultType.SUCCESS, emperorChalice);
			}
		}

		return new ActionResult<>(ActionResultType.PASS, emperorChalice);
	}

	private boolean placeWater(World world, PlayerEntity player, Hand hand, IFluidHandlerItem fluidHandler, BlockRayTraceResult result) {
		if (FluidUtil.tryPlaceFluid(player, world, hand, result.getPos(), fluidHandler, new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME))) {
			return true;
		}
		return FluidUtil.tryPlaceFluid(player, world, hand, result.getPos().offset(result.getFace()), fluidHandler, new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME));
	}

	private void onBlockRightClick(PlayerInteractEvent.RightClickBlock evt) {
		if (evt.getItemStack().getItem() == this) {
			World world = evt.getWorld();
			BlockState state = world.getBlockState(evt.getPos());
			if (state.getBlock() == Blocks.CAULDRON) {
				if (!isEnabled(evt.getItemStack()) && state.get(CauldronBlock.LEVEL) == 0) {
					fillCauldron(evt, world, state);
				} else if (isEnabled(evt.getItemStack()) && state.get(CauldronBlock.LEVEL) == 3) {
					emptyCauldron(evt, world, state);
				}
			}
		}
	}

	private void emptyCauldron(PlayerInteractEvent.RightClickBlock evt, World world, BlockState state) {
		int level = 0;
		setCauldronLevel(evt, world, state, level);
	}

	private void fillCauldron(PlayerInteractEvent.RightClickBlock evt, World world, BlockState state) {
		setCauldronLevel(evt, world, state, 3);
	}

	private void setCauldronLevel(PlayerInteractEvent.RightClickBlock evt, World world, BlockState state, int level) {
		((CauldronBlock) Blocks.CAULDRON).setWaterLevel(world, evt.getPos(), state, level);
		evt.setUseItem(Event.Result.DENY);
		evt.setCanceled(true);
		evt.setCancellationResult(ActionResultType.SUCCESS);
	}
}
