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
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class DestructionCatalystItem extends ToggleableItem {

	public DestructionCatalystItem() {
		super(Names.Items.DESTRUCTION_CATALYST, new Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack catalyst, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getRegistryName() + ".tooltip2",
				ImmutableMap.of("charge", String.valueOf(NBTHelper.getInt("gunpowder", catalyst))), tooltip);

		if (isEnabled(catalyst)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", Items.GUNPOWDER.getDisplayName(new ItemStack(Items.GUNPOWDER)).getString()), tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext itemUseContext) {
		PlayerEntity player = itemUseContext.getPlayer();
		ItemStack stack = itemUseContext.getItem();
		if (NBTHelper.getInt("gunpowder", stack) > gunpowderCost() || (player != null && player.isCreative())) {
			if (doExplosion(itemUseContext.getWorld(), itemUseContext.getPos(), itemUseContext.getFace()) && player != null && !player.isCreative()) {
				NBTHelper.putInt("gunpowder", stack, NBTHelper.getInt("gunpowder", stack) - gunpowderCost());
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	public void inventoryTick(ItemStack catalyst, World world, Entity e, int itemSlot, boolean isSelected) {
		if (world.isRemote)
			return;
		PlayerEntity player = null;
		if (e instanceof PlayerEntity) {
			player = (PlayerEntity) e;
		}
		if (player == null)
			return;

		if (isEnabled(catalyst)) {
			if (NBTHelper.getInt("gunpowder", catalyst) + gunpowderWorth() < gunpowderLimit()) {
				if (InventoryHelper.consumeItem(new ItemStack(Items.GUNPOWDER), player)) {
					NBTHelper.putInt("gunpowder", catalyst, NBTHelper.getInt("gunpowder", catalyst) + gunpowderWorth());
				}
			}
		}
	}

	private int getExplosionRadius() {
		return Settings.COMMON.items.destructionCatalyst.explosionRadius.get();
	}

	private boolean centeredExplosion() {
		return Settings.COMMON.items.destructionCatalyst.centeredExplosion.get();
	}

	private boolean perfectCube() {
		return Settings.COMMON.items.destructionCatalyst.perfectCube.get();
	}

	private boolean doExplosion(World world, BlockPos pos, Direction direction) {
		boolean destroyedSomething = false;
		boolean playOnce = true;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if (!centeredExplosion()) {
			y = pos.getY() + (direction == Direction.DOWN ? getExplosionRadius() : direction == Direction.UP ? -getExplosionRadius() : 0);
			z = pos.getZ() + (direction == Direction.NORTH ? getExplosionRadius() : direction == Direction.SOUTH ? -getExplosionRadius() : 0);
			x = pos.getX() + (direction == Direction.WEST ? getExplosionRadius() : direction == Direction.EAST ? -getExplosionRadius() : 0);
		}
		for (int xD = -getExplosionRadius(); xD <= getExplosionRadius(); xD++) {
			for (int yD = -getExplosionRadius(); yD <= getExplosionRadius(); yD++) {
				for (int zD = -getExplosionRadius(); zD <= getExplosionRadius(); zD++) {
					if (!perfectCube()) {
						BlockPos origin = new BlockPos(x, y, z);
						BlockPos target = new BlockPos(x + xD, y + yD, z + zD);
						double distance = origin.distanceSq(target);
						if (distance >= getExplosionRadius())
							continue;
					}

					//noinspection ConstantConditions
					if (isBreakable(world.getBlockState(new BlockPos(x + xD, y + yD, z + zD)).getBlock().getRegistryName().toString())) {
						world.setBlockState(new BlockPos(x + xD, y + yD, z + zD), Blocks.AIR.getDefaultState());
						if (world.rand.nextInt(2) == 0) {
							world.addParticle(ParticleTypes.EXPLOSION, x + xD + (world.rand.nextFloat() - 0.5F), y + yD + (world.rand.nextFloat() - 0.5F), z + zD + (world.rand.nextFloat() - 0.5F), 0.0D, 0.0D, 0.0D);
						}
						destroyedSomething = true;
						if (playOnce) {
							world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
							playOnce = false;
						}
					}
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
