package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	protected void addMoreInformation(ItemStack staff, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInt(SNOWBALLS_TAG, staff))), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.BLUE + Items.SNOWBALL.getDisplayName(new ItemStack(Items.SNOWBALL)).toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity e) {
		if (e instanceof LivingEntity && NBTHelper.getInt(SNOWBALLS_TAG, stack) >= getSnowballCost()) {
			LivingEntity livingBase = (LivingEntity) e;
			EffectInstance slow = new EffectInstance(Effects.SLOWNESS, 30, 0);

			//if the creature is slowed already, refresh the duration and increase the amplifier by 1.
			//5 hits is all it takes to max out the amplitude.
			EffectInstance slownessEffect = livingBase.getActivePotionEffect(Effects.SLOWNESS);
			if (slownessEffect != null) {
				slow = new EffectInstance(Effects.SLOWNESS, Math.min(slownessEffect.getDuration() + 30, 300),
						Math.min(slownessEffect.getAmplifier() + 1, 4));
			}

			((LivingEntity) e).addPotionEffect(slow);
			e.attackEntityFrom(DamageSource.causePlayerDamage(player), slow.getAmplifier());
			NBTHelper.putInt(SNOWBALLS_TAG, stack, NBTHelper.getInt(SNOWBALLS_TAG, stack) - getSnowballCost());
		}
		return super.onLeftClickEntity(stack, player, e);
	}

	@Override
	public void inventoryTick(ItemStack staff, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(staff, world, entity, itemSlot, isSelected);

		if (world.getGameTime() % 2 != 0) {
			return;
		}

		PlayerEntity player = null;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
		}
		if (player == null) {
			return;
		}

		if (isEnabled(staff)) {
			freezeBlocks(staff, world, player);
		}
		meltBlocks(staff, world, player);
	}

	private void freezeBlocks(ItemStack staff, World world, PlayerEntity player) {
		BlockPos playerPos = player.getPosition();
		BlockPos.getAllInBoxMutable(playerPos.add(-2, -1, -2), playerPos.add(2, -1, 2))
				.forEach(pos -> {
					if (Math.abs(playerPos.getX() - pos.getX()) == 2 && Math.abs(playerPos.getZ() - pos.getZ()) == 2) {
						return;
					}
					doFreezeCheck(staff, pos, world);
				});
	}

	private void meltBlocks(ItemStack staff, World world, PlayerEntity player) {
		if (!world.isRemote) {
			BlockPos playerPos = player.getPosition();
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
		CompoundNBT tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		Set<BlockPos> locations = new HashSet<>();

		tagCompound.getList(BLOCK_LOCATIONS_TAG, Constants.NBT.TAG_LONG)
				.forEach(nbt -> locations.add(BlockPos.fromLong(((LongNBT) nbt).getLong())));
		return locations;
	}

	private void doFreezeCheck(ItemStack stack, BlockPos pos, World world) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == Blocks.WATER && blockState.get(FlowingFluidBlock.LEVEL) == 0 && world.isAirBlock(pos.up())) {
			addFrozenBlockToList(stack, pos);
			world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
			for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.addParticle(ICE_PARTICLE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.75F, 0.75F, 1.0F);
			}
		} else if (blockState.getBlock() == Blocks.LAVA && blockState.get(FlowingFluidBlock.LEVEL) == 0) {
			addFrozenBlockToList(stack, pos);
			world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
			for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.addParticle(world.rand.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.0D, 0.2D, 0.0D);
			}
		}
	}

	private void doThawCheck(ItemStack stack, BlockPos pos, World world) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState == Blocks.PACKED_ICE.getDefaultState()) {
			if (removeFrozenBlockFromList(stack, pos)) {
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
				for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = world.rand.nextFloat();
					float yVel = world.rand.nextFloat() + 0.5F;
					float zVel = world.rand.nextFloat();
					world.addParticle(world.rand.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.0D, 0.2D, 0.0D);
				}
			}
		} else if (blockState == Blocks.OBSIDIAN.getDefaultState() && removeFrozenBlockFromList(stack, pos)) {
			world.setBlockState(pos, Blocks.LAVA.getDefaultState());
			for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0F, 0.2F, 0F);
			}
		}
	}

	private void addFrozenBlockToList(ItemStack stack, BlockPos pos) {
		CompoundNBT tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}

		ListNBT tagList = tagCompound.getList(BLOCK_LOCATIONS_TAG, Constants.NBT.TAG_LONG);
		tagList.add(LongNBT.valueOf(pos.toLong()));

		tagCompound.put(BLOCK_LOCATIONS_TAG, tagList);

		stack.setTag(tagCompound);
	}

	private boolean removeFrozenBlockFromList(ItemStack stack, BlockPos pos) {
		CompoundNBT tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}

		ListNBT tagList = tagCompound.getList(BLOCK_LOCATIONS_TAG, Constants.NBT.TAG_LONG);

		Iterator<INBT> it = tagList.iterator();

		boolean removedBlock = false;
		while (it.hasNext()) {
			LongNBT nbtPos = (LongNBT) it.next();
			if (nbtPos.getLong() == pos.toLong()) {
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
