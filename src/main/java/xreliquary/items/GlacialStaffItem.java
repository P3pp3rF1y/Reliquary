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
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class GlacialStaffItem extends IceMagusRodItem {
	public GlacialStaffItem() {
		super(Names.Items.GLACIAL_STAFF);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInt("snowballs", staff))), tooltip);
		if (isEnabled(staff))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.BLUE + Items.SNOWBALL.getDisplayName(new ItemStack(Items.SNOWBALL)).toString()), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity e) {
		if (e instanceof LivingEntity && NBTHelper.getInt("snowballs", stack) >= getSnowballCost()) {
			LivingEntity livingBase = (LivingEntity) e;
			EffectInstance slow = new EffectInstance(Effects.SLOWNESS, 30, 0);

			//if the creature is slowed already, refresh the duration and increase the amplifier by 1.
			//5 hits is all it takes to max out the amplitude.
			if (livingBase.getActivePotionEffect(Effects.SLOWNESS) != null)
				//noinspection ConstantConditions
				slow = new EffectInstance(Effects.SLOWNESS, Math.min(livingBase.getActivePotionEffect(Effects.SLOWNESS).getDuration() + 30, 300),
						Math.min(livingBase.getActivePotionEffect(Effects.SLOWNESS).getAmplifier() + 1, 4));

			((LivingEntity) e).addPotionEffect(slow);
			e.attackEntityFrom(DamageSource.causePlayerDamage(player), slow.getAmplifier());
			NBTHelper.putInt("snowballs", stack, NBTHelper.getInt("snowballs", stack) - getSnowballCost());
		}
		return super.onLeftClickEntity(stack, player, e);
	}

	@Override
	public void inventoryTick(ItemStack staff, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(staff, world, entity, itemSlot, isSelected);
		PlayerEntity player = null;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
		}
		if (player == null)
			return;

		int x = MathHelper.floor(player.posX);
		int y = MathHelper.floor(player.getBoundingBox().minY) - 1;
		int z = MathHelper.floor(player.posZ);

		if (isEnabled(staff)) {
			for (int xOff = -2; xOff <= 2; xOff++) {
				for (int zOff = -2; zOff <= 2; zOff++) {
					if (Math.abs(xOff) == 2 && Math.abs(zOff) == 2)
						continue;
					doFreezeCheck(staff, x, y, z, world, xOff, zOff);
				}
			}
		}

		if (!world.isRemote) {
			for (BlockPos pos : getBlockLocations(staff)) {
				int xOff = Math.abs(MathHelper.floor(player.posX) - pos.getX());
				int yOff = Math.abs(MathHelper.floor(player.posY) - pos.getY());
				int zOff = Math.abs(MathHelper.floor(player.posZ) - pos.getZ());

				if (xOff < 3 && yOff < 3 && zOff < 3 && !(xOff == 2 && zOff == 2))
					continue;

				doThawCheck(staff, pos.getX(), pos.getY(), pos.getZ(), world);
			}
		}
	}

	private BlockPos[] getBlockLocations(ItemStack stack) {
		CompoundNBT tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}

		if (!tagCompound.contains("BlockLocations"))
			tagCompound.put("BlockLocations", new ListNBT());
		ListNBT tagList = tagCompound.getList("BlockLocations", 10);

		BlockPos[] locations = new BlockPos[tagList.size()];

		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbtLocation = (CompoundNBT) tagList.get(i);
			locations[i] = new BlockPos(nbtLocation.getInt("x"), nbtLocation.getInt("y"), nbtLocation.getInt("z"));
		}

		return locations;
	}

	private void doFreezeCheck(ItemStack stack, int x, int y, int z, World world, int xOff, int zOff) {
		x += xOff;
		z += zOff;
		BlockState blockState = world.getBlockState(new BlockPos(x, y, z));
		if (blockState.getBlock() == Blocks.WATER && blockState.get(FlowingFluidBlock.LEVEL) == 0 && world.isAirBlock(new BlockPos(x, y + 1, z))) {
			addFrozenBlockToList(stack, x, y, z);
			world.setBlockState(new BlockPos(x, y, z), Blocks.PACKED_ICE.getDefaultState());

			for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.addParticle(RedstoneParticleData.REDSTONE_DUST, x + xVel, y + yVel, z + zVel, 0.75F, 0.75F, 1.0F);
			}
		} else if (blockState.getBlock() == Blocks.LAVA && blockState.get(FlowingFluidBlock.LEVEL) == 0) {
			addFrozenBlockToList(stack, x, y, z);
			world.setBlockState(new BlockPos(x, y, z), Blocks.OBSIDIAN.getDefaultState());
			for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.addParticle(world.rand.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, x + xVel, y + yVel, z + zVel, 0.0D, 0.2D, 0.0D);

			}

		}
	}

	private void doThawCheck(ItemStack stack, int x, int y, int z, World world) {
		BlockState blockState = world.getBlockState(new BlockPos(x, y, z));
		if (blockState == Blocks.PACKED_ICE.getDefaultState()) {
			if (removeFrozenBlockFromList(stack, x, y, z)) {
				world.setBlockState(new BlockPos(x, y, z), Blocks.WATER.getDefaultState());
				for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = world.rand.nextFloat();
					float yVel = world.rand.nextFloat() + 0.5F;
					float zVel = world.rand.nextFloat();
					world.addParticle(world.rand.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, x + xVel, y + yVel, z + zVel, 0.0D, 0.2D, 0.0D);

				}
			}
		} else if (blockState == Blocks.OBSIDIAN.getDefaultState()) {
			if (removeFrozenBlockFromList(stack, x, y, z)) {
				world.setBlockState(new BlockPos(x, y, z), Blocks.LAVA.getDefaultState());

				float red = 1.0F;
				float green = 0.0F;
				float blue = 0.0F;

				for (int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = world.rand.nextFloat();
					float yVel = world.rand.nextFloat() + 0.5F;
					float zVel = world.rand.nextFloat();
					world.addParticle(RedstoneParticleData.REDSTONE_DUST, x + xVel, y + yVel, z + zVel, red, green, blue);
				}
			}
		}
	}

	private void addFrozenBlockToList(ItemStack stack, int x, int y, int z) {
		CompoundNBT tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}

		if (!tagCompound.contains("BlockLocations"))
			tagCompound.put("BlockLocations", new ListNBT());
		ListNBT tagList = tagCompound.getList("BlockLocations", 10);

		CompoundNBT newTagData = new CompoundNBT();
		newTagData.putInt("x", x);
		newTagData.putInt("y", y);
		newTagData.putInt("z", z);

		tagList.add(newTagData);

		tagCompound.put("BlockLocations", tagList);

		stack.setTag(tagCompound);
	}

	private boolean removeFrozenBlockFromList(ItemStack stack, int x, int y, int z) {
		CompoundNBT tagCompound = stack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}

		if (!tagCompound.contains("BlockLocations"))
			tagCompound.put("BlockLocations", new ListNBT());
		ListNBT tagList = tagCompound.getList("BlockLocations", 10);

		boolean removedBlock = false;

		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tagItemData = tagList.getCompound(i);
			if (tagItemData.getInt("x") == x && tagItemData.getInt("y") == y && tagItemData.getInt("z") == z) {
				tagItemData.putBoolean("remove", true);
				removedBlock = true;
			}
		}

		ListNBT newTagList = new ListNBT();
		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tagItemData = tagList.getCompound(i);
			if (!tagItemData.getBoolean("remove")) {
				CompoundNBT newTagData = new CompoundNBT();
				newTagData.putInt("x", tagItemData.getInt("x"));
				newTagData.putInt("y", tagItemData.getInt("y"));
				newTagData.putInt("z", tagItemData.getInt("z"));
				newTagList.add(newTagData);
			}
		}

		tagCompound.put("BlockLocations", newTagList);
		stack.setTag(tagCompound);
		return removedBlock;
	}
}
