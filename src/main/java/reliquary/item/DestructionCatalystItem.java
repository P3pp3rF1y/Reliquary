package reliquary.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import reliquary.init.ModDataComponents;
import reliquary.reference.Config;
import reliquary.util.RandHelper;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class DestructionCatalystItem extends ChargeableItem {

	public DestructionCatalystItem() {
		super(new Properties().stacksTo(1).setNoRepair());
	}

	@Override
	protected void addMoreInformation(ItemStack catalyst, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", getGunpowder(catalyst));

		if (isEnabled(catalyst)) {
			tooltipBuilder.absorbActive(Items.GUNPOWDER.getName(new ItemStack(Items.GUNPOWDER)).getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResult useOn(UseOnContext itemUseContext) {
		Player player = itemUseContext.getPlayer();
		if (player != null && player.isCrouching()) {
			return InteractionResult.PASS;
		}

		ItemStack stack = itemUseContext.getItemInHand();
		if (getGunpowder(stack) >= gunpowderCost() || (player != null && player.isCreative())) {
			if (doExplosion(itemUseContext.getLevel(), itemUseContext.getClickedPos(), itemUseContext.getClickedFace()) && player != null && !player.isCreative() && !player.level().isClientSide()) {
				useCharge(stack, gunpowderCost());
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public void inventoryTick(ItemStack catalyst, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 10 != 0) {
			return;
		}

		if (isEnabled(catalyst)) {
			int gunpowderCharge = getGunpowder(catalyst);
			consumeAndCharge(catalyst, 0, player, gunpowderLimit() - gunpowderCharge, 1, 16);
		}
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		return slot == 0 && stack.is(Items.GUNPOWDER);
	}

	@Override
	public void addStoredCharge(ItemStack catalyst, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		catalyst.set(ModDataComponents.GUNPOWDER, getGunpowder(catalyst) + chargeToAdd);
	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		return getGunpowder(containerStack);
	}

	@Override
	protected int getSlotWorth(int slot) {
		return slot == 0 ? gunpowderWorth() : 0;
	}

	public static int getGunpowder(ItemStack catalyst) {
		return catalyst.getOrDefault(ModDataComponents.GUNPOWDER, 0);
	}

	private int getExplosionRadius() {
		return Config.COMMON.items.destructionCatalyst.explosionRadius.get();
	}

	private boolean perfectCube() {
		return Config.COMMON.items.destructionCatalyst.perfectCube.get();
	}

	private boolean doExplosion(Level level, BlockPos pos, Direction direction) {
		boolean destroyedSomething = false;
		boolean playOnce = true;
		BlockPos origin = pos;
		if (Boolean.FALSE.equals(Config.COMMON.items.destructionCatalyst.centeredExplosion.get())) {
			origin = pos.relative(direction.getOpposite(), getExplosionRadius());
		}
		for (BlockPos target : BlockPos.betweenClosed(origin.offset(-getExplosionRadius(), -getExplosionRadius(), -getExplosionRadius()),
				origin.offset(getExplosionRadius(), getExplosionRadius(), getExplosionRadius()))) {
			if (!perfectCube()) {
				double distance = origin.distSqr(target);
				if (distance >= getExplosionRadius()) {
					continue;
				}
			}

			//noinspection ConstantConditions
			if (isBreakable(BuiltInRegistries.BLOCK.getKey(level.getBlockState(target).getBlock()).toString())) {
				level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
				if (level.random.nextInt(2) == 0) {
					level.addParticle(ParticleTypes.EXPLOSION, target.getX() + (level.random.nextFloat() - 0.5F), target.getY() + (level.random.nextFloat() - 0.5F), target.getZ() + (level.random.nextFloat() - 0.5F), 0.0D, 0.0D, 0.0D);
				}
				destroyedSomething = true;
				if (playOnce) {
					level.playSound(null, target, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + RandHelper.getRandomMinusOneToOne(level.random) * 0.2F) * 0.7F);
					playOnce = false;
				}
			}
		}
		return destroyedSomething;
	}

	private boolean isBreakable(String id) {
		return Config.COMMON.items.destructionCatalyst.mundaneBlocks.get().contains(id);
	}

	private int gunpowderCost() {
		return Config.COMMON.items.destructionCatalyst.gunpowderCost.get();
	}

	private int gunpowderWorth() {
		return Config.COMMON.items.destructionCatalyst.gunpowderWorth.get();
	}

	private int gunpowderLimit() {
		return Config.COMMON.items.destructionCatalyst.gunpowderLimit.get();
	}
}
