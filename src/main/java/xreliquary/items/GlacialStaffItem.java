package xreliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("java:S110")
public class GlacialStaffItem extends IceMagusRodItem {
	private static final String SNOWBALLS_TAG = "snowballs";
	private static final String BLOCK_LOCATIONS_TAG = "blockLocations";

	public GlacialStaffItem() {
		super();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2", Map.of("charge", Integer.toString(NBTHelper.getInt(SNOWBALLS_TAG, staff))), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.BLUE + Items.SNOWBALL.getName(new ItemStack(Items.SNOWBALL)).toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity e) {
		if (e instanceof LivingEntity livingBase && NBTHelper.getInt(SNOWBALLS_TAG, stack) >= getSnowballCost()) {
			MobEffectInstance slow = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0);

			//if the creature is slowed already, refresh the duration and increase the amplifier by 1.
			//5 hits is all it takes to max out the amplitude.
			MobEffectInstance slownessEffect = livingBase.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
			if (slownessEffect != null) {
				slow = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.min(slownessEffect.getDuration() + 30, 300),
						Math.min(slownessEffect.getAmplifier() + 1, 4));
			}

			((LivingEntity) e).addEffect(slow);
			e.hurt(DamageSource.playerAttack(player), slow.getAmplifier());
			NBTHelper.putInt(SNOWBALLS_TAG, stack, NBTHelper.getInt(SNOWBALLS_TAG, stack) - getSnowballCost());
		}
		return super.onLeftClickEntity(stack, player, e);
	}

	@Override
	public void inventoryTick(ItemStack staff, Level world, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(staff, world, entity, itemSlot, isSelected);

		if (world.getGameTime() % 2 != 0) {
			return;
		}

		if (!(entity instanceof Player player)) {
			return;
		}

		if (isEnabled(staff)) {
			freezeBlocks(staff, world, player);
		}
		meltBlocks(staff, world, player);
	}

	private void freezeBlocks(ItemStack staff, Level world, Player player) {
		BlockPos playerPos = player.blockPosition();
		BlockPos.betweenClosed(playerPos.offset(-2, -1, -2), playerPos.offset(2, -1, 2))
				.forEach(pos -> {
					if (Math.abs(playerPos.getX() - pos.getX()) == 2 && Math.abs(playerPos.getZ() - pos.getZ()) == 2) {
						return;
					}
					doFreezeCheck(staff, pos, world);
				});
	}

	private void meltBlocks(ItemStack staff, Level world, Player player) {
		if (!world.isClientSide) {
			BlockPos playerPos = player.blockPosition();
			for (BlockPos pos : getBlockLocations(staff)) {
				int xOff = Math.abs(playerPos.getX() - pos.getX());
				int yOff = Math.abs(playerPos.getY() - pos.getY());
				int zOff = Math.abs(playerPos.getZ() - pos.getZ());

				if (xOff < 3 && yOff < 3 && zOff < 3 && !(xOff == 2 && zOff == 2)) {
					continue;
				}

				doThawCheck(staff, pos, world);
			}
		}
	}

	private Set<BlockPos> getBlockLocations(ItemStack stack) {
		CompoundTag tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundTag();
		}
		Set<BlockPos> locations = new HashSet<>();

		tagCompound.getList(BLOCK_LOCATIONS_TAG, Tag.TAG_LONG)
				.forEach(nbt -> locations.add(BlockPos.of(((LongTag) nbt).getAsLong())));
		return locations;
	}

	private void doFreezeCheck(ItemStack stack, BlockPos pos, Level world) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == Blocks.WATER && blockState.getValue(LiquidBlock.LEVEL) == 0 && world.isEmptyBlock(pos.above())) {
			addFrozenBlockToList(stack, pos);
			world.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
			for (int particleNum = world.random.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.random.nextFloat();
				float yVel = world.random.nextFloat() + 0.5F;
				float zVel = world.random.nextFloat();
				world.addParticle(ICE_PARTICLE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.75F, 0.75F, 1.0F);
			}
		} else if (blockState.getBlock() == Blocks.LAVA && blockState.getValue(LiquidBlock.LEVEL) == 0) {
			addFrozenBlockToList(stack, pos);
			world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
			for (int particleNum = world.random.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.random.nextFloat();
				float yVel = world.random.nextFloat() + 0.5F;
				float zVel = world.random.nextFloat();
				world.addParticle(world.random.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.0D, 0.2D, 0.0D);
			}
		}
	}

	private void doThawCheck(ItemStack stack, BlockPos pos, Level world) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState == Blocks.PACKED_ICE.defaultBlockState()) {
			if (removeFrozenBlockFromList(stack, pos)) {
				world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
				for (int particleNum = world.random.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = world.random.nextFloat();
					float yVel = world.random.nextFloat() + 0.5F;
					float zVel = world.random.nextFloat();
					world.addParticle(world.random.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.0D, 0.2D, 0.0D);
				}
			}
		} else if (blockState == Blocks.OBSIDIAN.defaultBlockState() && removeFrozenBlockFromList(stack, pos)) {
			world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
			for (int particleNum = world.random.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.random.nextFloat();
				float yVel = world.random.nextFloat() + 0.5F;
				float zVel = world.random.nextFloat();
				world.addParticle(DustParticleOptions.REDSTONE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0F, 0.2F, 0F);
			}
		}
	}

	private void addFrozenBlockToList(ItemStack stack, BlockPos pos) {
		CompoundTag tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundTag();
		}

		ListTag tagList = tagCompound.getList(BLOCK_LOCATIONS_TAG, Tag.TAG_LONG);
		tagList.add(LongTag.valueOf(pos.asLong()));

		tagCompound.put(BLOCK_LOCATIONS_TAG, tagList);

		stack.setTag(tagCompound);
	}

	private boolean removeFrozenBlockFromList(ItemStack stack, BlockPos pos) {
		CompoundTag tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundTag();
		}

		ListTag tagList = tagCompound.getList(BLOCK_LOCATIONS_TAG, Tag.TAG_LONG);

		Iterator<Tag> it = tagList.iterator();

		boolean removedBlock = false;
		while (it.hasNext()) {
			LongTag nbtPos = (LongTag) it.next();
			if (nbtPos.getAsLong() == pos.asLong()) {
				it.remove();
				removedBlock = true;
			}
		}

		if (removedBlock) {
			tagCompound.put(BLOCK_LOCATIONS_TAG, tagList);
			stack.setTag(tagCompound);
		}
		return removedBlock;
	}
}
