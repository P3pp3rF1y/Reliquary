package xreliquary.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RandHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class DestructionCatalystItem extends ToggleableItem {

	private static final String GUNPOWDER_TAG = "gunpowder";

	public DestructionCatalystItem() {
		super(new Properties().stacksTo(1).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack catalyst, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2",
				Map.of("charge", String.valueOf(NBTHelper.getInt(GUNPOWDER_TAG, catalyst))), tooltip);

		if (isEnabled(catalyst)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", Items.GUNPOWDER.getName(new ItemStack(Items.GUNPOWDER)).getString()), tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
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
		if (NBTHelper.getInt(GUNPOWDER_TAG, stack) > gunpowderCost() || (player != null && player.isCreative())) {
			if (doExplosion(itemUseContext.getLevel(), itemUseContext.getClickedPos(), itemUseContext.getClickedFace()) && player != null && !player.isCreative()) {
				NBTHelper.putInt(GUNPOWDER_TAG, stack, NBTHelper.getInt(GUNPOWDER_TAG, stack) - gunpowderCost());
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public void inventoryTick(ItemStack catalyst, Level world, Entity e, int itemSlot, boolean isSelected) {
		if (world.isClientSide || world.getGameTime() % 10 != 0) {
			return;
		}
		if (!(e instanceof Player player)) {
			return;
		}

		if (isEnabled(catalyst)) {
			int gunpowderCharge = NBTHelper.getInt(GUNPOWDER_TAG, catalyst);
			consumeAndCharge(player, gunpowderLimit() - gunpowderCharge, gunpowderWorth(), Items.GUNPOWDER, 16,
					chargeToAdd -> NBTHelper.putInt(GUNPOWDER_TAG, catalyst, gunpowderCharge + chargeToAdd));
		}
	}

	private int getExplosionRadius() {
		return Settings.COMMON.items.destructionCatalyst.explosionRadius.get();
	}

	private boolean perfectCube() {
		return Settings.COMMON.items.destructionCatalyst.perfectCube.get();
	}

	private boolean doExplosion(Level world, BlockPos pos, Direction direction) {
		boolean destroyedSomething = false;
		boolean playOnce = true;
		BlockPos origin = pos;
		if (Boolean.FALSE.equals(Settings.COMMON.items.destructionCatalyst.centeredExplosion.get())) {
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
			if (isBreakable(world.getBlockState(target).getBlock().getRegistryName().toString())) {
				world.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
				if (world.random.nextInt(2) == 0) {
					world.addParticle(ParticleTypes.EXPLOSION, target.getX() + (world.random.nextFloat() - 0.5F), target.getY() + (world.random.nextFloat() - 0.5F), target.getZ() + (world.random.nextFloat() - 0.5F), 0.0D, 0.0D, 0.0D);
				}
				destroyedSomething = true;
				if (playOnce) {
					world.playSound(null, target, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + RandHelper.getRandomMinusOneToOne(world.random) * 0.2F) * 0.7F);
					playOnce = false;
				}
			}
		}
		return destroyedSomething;
	}

	private boolean isBreakable(String id) {
		return Settings.COMMON.items.destructionCatalyst.mundaneBlocks.get().contains(id);
	}

	private int gunpowderCost() {
		return Settings.COMMON.items.destructionCatalyst.gunpowderCost.get();
	}

	private int gunpowderWorth() {
		return Settings.COMMON.items.destructionCatalyst.gunpowderWorth.get();
	}

	private int gunpowderLimit() {
		return Settings.COMMON.items.destructionCatalyst.gunpowderLimit.get();
	}
}
