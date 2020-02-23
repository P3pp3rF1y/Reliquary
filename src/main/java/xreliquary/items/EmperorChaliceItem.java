package xreliquary.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xreliquary.items.util.fluid.FluidHandlerEmperorChalice;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.RandHelper;

import javax.annotation.Nullable;
import java.util.List;

public class EmperorChaliceItem extends ToggleableItem {

	public EmperorChaliceItem() {
		super("emperor_chalice", new Properties().maxStackSize(1).setNoRepair().rarity(Rarity.EPIC));
		MinecraftForge.EVENT_BUS.register(this);
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
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			return super.onItemRightClick(world, player, hand);
		}
		boolean isInDrainMode = isEnabled(stack);
		RayTraceResult result = rayTrace(world, player, isInDrainMode ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

		//noinspection ConstantConditions
		if (result == null) {
			if (!isEnabled(stack)) {
				player.setActiveHand(hand);
			}
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		} else if (result.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) result;

			if (!world.isBlockModifiable(player, blockRayTraceResult.getPos()) || !player.canPlayerEdit(blockRayTraceResult.getPos(), blockRayTraceResult.getFace(), stack)) {
				return new ActionResult<>(ActionResultType.FAIL, stack);
			}

			if (!isEnabled(stack)) {
				BlockPos waterPlacementPos = blockRayTraceResult.getPos().offset(blockRayTraceResult.getFace());

				if (!player.canPlayerEdit(waterPlacementPos, blockRayTraceResult.getFace(), stack)) {
					return new ActionResult<>(ActionResultType.FAIL, stack);
				}

				if (tryPlaceContainedLiquid(world, stack, waterPlacementPos)) {
					return new ActionResult<>(ActionResultType.SUCCESS, stack);
				}

			} else {
				if ((world.getBlockState(blockRayTraceResult.getPos()).getBlock() == Blocks.WATER) && world.getBlockState(blockRayTraceResult.getPos()).get(FlowingFluidBlock.LEVEL) == 0) {
					world.setBlockState(blockRayTraceResult.getPos(), Blocks.AIR.getDefaultState());

					return new ActionResult<>(ActionResultType.SUCCESS, stack);
				}
			}
		}

		return new ActionResult<>(ActionResultType.PASS, stack);
	}

	@SubscribeEvent
	@SuppressWarnings("unused") //used in event
	public void onBlockRightClick(PlayerInteractEvent.RightClickBlock evt) {
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

	private boolean tryPlaceContainedLiquid(World world, ItemStack stack, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Material material = blockState.getMaterial();

		if (isEnabled(stack)) {
			return false;
		}
		if (!world.isAirBlock(pos) && material.isSolid()) {
			return false;
		} else {
			if (world.getDimension().doesWaterVaporize()) {
				world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(world.rand) * 0.8F);

				for (int var11 = 0; var11 < 8; ++var11) {
					world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
				}
			} else {
				world.setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
			}

			return true;
		}
	}
}
