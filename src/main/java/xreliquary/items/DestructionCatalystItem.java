package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class DestructionCatalystItem extends ToggleableItem {

	private static final String GUNPOWDER_TAG = "gunpowder";

	public DestructionCatalystItem() {
		super("destruction_catalyst", new Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack catalyst, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2",
				ImmutableMap.of("charge", String.valueOf(NBTHelper.getInt(GUNPOWDER_TAG, catalyst))), tooltip);

		if (isEnabled(catalyst)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", Items.GUNPOWDER.getDisplayName(new ItemStack(Items.GUNPOWDER)).getString()), tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext itemUseContext) {
		PlayerEntity player = itemUseContext.getPlayer();
		ItemStack stack = itemUseContext.getItem();
		if (NBTHelper.getInt(GUNPOWDER_TAG, stack) > gunpowderCost() || (player != null && player.isCreative())) {
			if (doExplosion(itemUseContext.getWorld(), itemUseContext.getPos(), itemUseContext.getFace()) && player != null && !player.isCreative()) {
				NBTHelper.putInt(GUNPOWDER_TAG, stack, NBTHelper.getInt(GUNPOWDER_TAG, stack) - gunpowderCost());
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	public void inventoryTick(ItemStack catalyst, World world, Entity e, int itemSlot, boolean isSelected) {
		if (world.isRemote) {
			return;
		}
		PlayerEntity player = null;
		if (e instanceof PlayerEntity) {
			player = (PlayerEntity) e;
		}
		if (player == null) {
			return;
		}

		if (isEnabled(catalyst) && NBTHelper.getInt(GUNPOWDER_TAG, catalyst) + gunpowderWorth() < gunpowderLimit() && InventoryHelper.consumeItem(new ItemStack(Items.GUNPOWDER), player)) {
			NBTHelper.putInt(GUNPOWDER_TAG, catalyst, NBTHelper.getInt(GUNPOWDER_TAG, catalyst) + gunpowderWorth());
		}
	}

	private int getExplosionRadius() {
		return Settings.COMMON.items.destructionCatalyst.explosionRadius.get();
	}

	private boolean perfectCube() {
		return Settings.COMMON.items.destructionCatalyst.perfectCube.get();
	}

	private boolean doExplosion(World world, BlockPos pos, Direction direction) {
		boolean destroyedSomething = false;
		boolean playOnce = true;
		BlockPos origin = pos.offset(direction.getOpposite(), getExplosionRadius());
		for (BlockPos target : BlockPos.getAllInBox(origin.add(-getExplosionRadius(), -getExplosionRadius(), -getExplosionRadius()),
				origin.add(getExplosionRadius(), getExplosionRadius(), getExplosionRadius())).collect(Collectors.toList())) {
			if (!perfectCube()) {
				double distance = origin.distanceSq(target);
				if (distance >= getExplosionRadius()) {
					continue;
				}
			}

			//noinspection ConstantConditions
			if (isBreakable(world.getBlockState(target).getBlock().getRegistryName().toString())) {
				world.setBlockState(target, Blocks.AIR.getDefaultState());
				if (world.rand.nextInt(2) == 0) {
					world.addParticle(ParticleTypes.EXPLOSION, target.getX() + (world.rand.nextFloat() - 0.5F), target.getY() + (world.rand.nextFloat() - 0.5F), target.getZ() + (world.rand.nextFloat() - 0.5F), 0.0D, 0.0D, 0.0D);
				}
				destroyedSomething = true;
				if (playOnce) {
					world.playSound(null, target, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
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
