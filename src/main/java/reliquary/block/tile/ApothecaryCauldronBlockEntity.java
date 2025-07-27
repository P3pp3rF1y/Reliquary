package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import reliquary.block.ApothecaryCauldronBlock;
import reliquary.client.init.ModParticles;
import reliquary.compat.jade.provider.IJadeDataChangeIndicator;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.item.PotionEssenceItem;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.potions.PotionHelper;

import java.util.HashSet;
import java.util.Set;

public class ApothecaryCauldronBlockEntity extends BlockEntityBase implements IJadeDataChangeIndicator {
	private int redstoneCount = 0;
	private PotionContents potionContents = PotionContents.EMPTY;
	private int glowstoneCount = 0;
	private boolean hasGunpowder = false;
	private boolean hasNetherwart = false;
	private boolean hasDragonBreath = false;
	private int cookTime = 0;
	private int liquidLevel = 0;
	private boolean dataChanged;

	public ApothecaryCauldronBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.APOTHECARY_CAULDRON_TILE_TYPE.get(), pos, state);
		dataChanged = true;
	}

	public void serverTick(Level level, BlockPos pos) {
		//Item addition gets handled by the block's onEntityCollided method.
		if (getHeatSources().contains(level.getBlockState(pos.offset(0, -1, 0)).getBlock()) && getLiquidLevel() > 0) {
			if (potionContents.hasEffects() && hasNetherwart && cookTime < getTotalCookTime()) {
				cookTime++;
			}
			if (level.isClientSide) {
				spawnParticles(level, pos);
			}
		}
	}

	private void spawnParticles(Level level, BlockPos pos) {
		for (int particleCount = 0; particleCount <= 2; ++particleCount) {
			spawnBoilingParticles(level, pos);
		}

		if (hasDragonBreath) {
			spawnDragonBreathParticles(level, pos);
		} else if (hasGunpowder) {
			spawnGunpowderParticles(level, pos);
		}

		if (glowstoneCount > 0) {
			spawnGlowstoneParticles(level, pos);
		}
		if (hasNetherwart) {
			spawnNetherwartParticles(level, pos);
			if (finishedCooking()) {
				spawnFinishedParticles(level, pos);
			}
		}
		if (redstoneCount > 0) {
			spawnRedstoneParticles(level, pos);
		}
	}

	private void spawnBoilingParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(getTotalCookTime() * getTotalCookTime()) > cookTime * cookTime) {
			return;
		}
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.33F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.33F;

		int color = potionContents.getColor();

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = ((color & 255) / 256F);

		level.addParticle(ColorParticleOption.create(ModParticles.CAULDRON_BUBBLE.get(), red, green, blue), pos.getX() + 0.5D + xOffset, pos.getY() + 0.01D + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0D, 0D, 0D);

		if (level.random.nextInt(6) == 0) {
			level.addParticle(ColorParticleOption.create(ModParticles.CAULDRON_STEAM.get(), red, green, blue), pos.getX() + 0.5D + xOffset, pos.getY() + 0.01D + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0D, 0.05D + 0.02F * getRenderLiquidLevel(), 0D);
		}
	}

	private float getRenderLiquidLevel() {
		int j = Mth.clamp(getLiquidLevel(), 0, 3);
		return (6 + 3 * j) / 16.0F;
	}

	private void spawnGunpowderParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(8) > 0) {
			return;
		}
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5D + xOffset, pos.getY() + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	private void spawnDragonBreathParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(8) > 0) {
			return;
		}
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		level.addParticle(ParticleTypes.DRAGON_BREATH, pos.getX() + 0.5D + xOffset, pos.getY() + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	private void spawnGlowstoneParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(8) > 0) {
			return;
		}
		float gauss = 0.5F + level.random.nextFloat() / 2;
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, gauss, gauss, 0.0F), pos.getX() + 0.5D + xOffset, pos.getY() + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0, 0, 0);
	}

	private void spawnNetherwartParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(8) > 0) {
			return;
		}
		float gauss = 0.5F + level.random.nextFloat() / 2;
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, gauss, 0.0F, gauss), pos.getX() + 0.5D + xOffset, pos.getY() + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0, 0, 0);
	}

	private void spawnRedstoneParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(10) / redstoneCount > 0) {
			return;
		}
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		level.addParticle(DustParticleOptions.REDSTONE, pos.getX() + 0.5D + xOffset, pos.getY() + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 1D, 0D, 0D);
	}

	private void spawnFinishedParticles(Level level, BlockPos pos) {
		if (level.random.nextInt(8) > 0) {
			return;
		}
		float xOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (level.random.nextFloat() - 0.5F) / 1.66F;
		level.addParticle(ParticleTypes.WITCH, pos.getX() + 0.5D + xOffset, pos.getY() + getRenderLiquidLevel(), pos.getZ() + 0.5D + zOffset, 0D, 0D, 0D);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		setLiquidLevel(tag.getShort("liquidLevel"));
		glowstoneCount = tag.getInt("glowstoneCount");
		hasNetherwart = tag.getBoolean("hasNetherwart");
		hasGunpowder = tag.getBoolean("hasGunpowder");
		hasDragonBreath = tag.getBoolean("hasDragonBreath");
		redstoneCount = tag.getInt("redstoneCount");
		cookTime = tag.getInt("cookTime");
		potionContents = PotionHelper.getPotionContentsFromCompoundTag(tag);
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);
		compound.putInt("liquidLevel", getLiquidLevel());
		compound.putInt("cookTime", cookTime);
		compound.putInt("redstoneCount", redstoneCount);
		compound.putInt("glowstoneCount", glowstoneCount);
		compound.putBoolean("hasGunpowder", hasGunpowder);
		compound.putBoolean("hasDragonBreath", hasDragonBreath);
		compound.putBoolean("hasNetherwart", hasNetherwart);
		PotionHelper.addPotionContentsToCompoundTag(compound, potionContents);
	}

	private boolean finishedCooking() {
		return hasNetherwart && potionContents.hasEffects() && cookTime >= getTotalCookTime() && (!hasDragonBreath || hasGunpowder);
	}

	private ItemStack removeContainedPotion(Level level, BlockPos pos) {
		ItemStack potion;
		if (hasDragonBreath) {
			potion = new ItemStack(ModItems.LINGERING_POTION.get());
		} else if (hasGunpowder) {
			potion = new ItemStack(ModItems.SPLASH_POTION.get());
		} else {
			potion = new ItemStack(ModItems.POTION.get());
		}
		potion.set(DataComponents.POTION_CONTENTS, PotionHelper.augmentPotionContents(potionContents, redstoneCount, glowstoneCount));

		setLiquidLevel(getLiquidLevel() - 1);
		if (getLiquidLevel() <= 0) {
			clearAllFields(level, pos);
		}
		return potion;
	}

	private void clearAllFields(Level level, BlockPos pos) {
		cookTime = 0;
		glowstoneCount = 0;
		hasGunpowder = false;
		hasNetherwart = false;
		redstoneCount = 0;
		potionContents = PotionContents.EMPTY;
		dataChanged = true;
		hasDragonBreath = false;
		level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	private boolean isItemValidForInput(ItemStack stack) {
		if (stack.getItem() instanceof PotionEssenceItem && !potionContents.hasEffects()) {
			return true;
		}

		if (!potionContents.hasEffects()) {
			return false;
		}

		if (stack.getItem() == Items.GUNPOWDER && !hasGunpowder) {
			return true;
		}
		if (stack.getItem() == Items.GLOWSTONE_DUST && glowstoneCount < getGlowstoneAmpLimit()) {
			return true;
		}
		if (stack.getItem() == Items.REDSTONE && redstoneCount < getRedstoneAmpLimit()) {
			return true;
		}
		if (stack.getItem() == Items.NETHER_WART && !hasNetherwart) {
			return true;
		}
		return stack.getItem() == Items.DRAGON_BREATH && !hasDragonBreath;
	}

	private void addItem(ItemStack stack, Level level, BlockPos pos) {
		if (stack.getItem() instanceof PotionEssenceItem) {
			potionContents = stack.get(DataComponents.POTION_CONTENTS);
		} else if (stack.getItem() == Items.GUNPOWDER) {
			hasGunpowder = true;
		} else if (stack.getItem() == Items.GLOWSTONE_DUST) {
			++glowstoneCount;
		} else if (stack.getItem() == Items.REDSTONE) {
			++redstoneCount;
		} else if (stack.getItem() == Items.NETHER_WART) {
			hasNetherwart = true;
		} else if (stack.getItem() == Items.DRAGON_BREATH) {
			hasDragonBreath = true;
		}

		level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
	}

	private int getGlowstoneAmpLimit() {
		return Config.COMMON.blocks.apothecaryCauldron.glowstoneLimit.get();
	}

	private int getRedstoneAmpLimit() {
		return Config.COMMON.blocks.apothecaryCauldron.redstoneLimit.get();
	}

	private Set<Block> getHeatSources() {
		Set<Block> heatSources = new HashSet<>();

        Config.COMMON.blocks.apothecaryCauldron.heatSources.get()
				.forEach(blockName -> heatSources.add(BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockName))));
		//defaults that can't be removed.
		heatSources.add(Blocks.LAVA);
		heatSources.add(Blocks.FIRE);
		heatSources.add(Blocks.SOUL_FIRE);
		heatSources.add(Blocks.CAMPFIRE);
		heatSources.add(Blocks.SOUL_CAMPFIRE);

		return heatSources;
	}

	private int getTotalCookTime() {
		return Config.COMMON.blocks.apothecaryCauldron.cookTime.get();
	}

	public PotionContents getPotionContents() {
		return potionContents;
	}

	public boolean hasNetherwart() {
		return hasNetherwart;
	}

	public boolean hasGunpowder() {
		return hasGunpowder;
	}

	public boolean hasDragonBreath() {
		return hasDragonBreath;
	}

	public int getRedstoneCount() {
		return redstoneCount;
	}

	public int getGlowstoneCount() {
		return glowstoneCount;
	}

	public void handleCollidingEntity(Level level, BlockPos pos, Entity collidingEntity) {
		int l = getLiquidLevel();
		float f = pos.getY() + (6.0F + (3 * l)) / 16.0F;
		if (collidingEntity.getBoundingBox().minY <= f) {

			if (collidingEntity.isOnFire() && l > 0) {
				collidingEntity.clearFire();
			}
			if (collidingEntity instanceof LivingEntity livingEntity && !addEffectsToEntity(level, collidingEntity, livingEntity)) {
				return;
			}

			if (collidingEntity instanceof ItemEntity itemEntity) {
				pickupItem(level, pos, itemEntity);
			}

		}
	}

	private void pickupItem(Level level, BlockPos pos, ItemEntity itemEntity) {
		ItemStack item = itemEntity.getItem();
		while (isItemValidForInput(item)) {
			addItem(item, level, pos);
			item.shrink(1);
		}
	}

	private boolean addEffectsToEntity(Level level, Entity collidingEntity, LivingEntity livingEntity) {
		if (!potionContents.hasEffects()) {
			return false;
		}
		//apply potion effects when done cooking potion (potion essence and netherwart in and fire below at the minimum)
		if (finishedCooking()) {
			for (MobEffectInstance effect : potionContents.getAllEffects()) {
				MobEffect potion = effect.getEffect().value();
				if (potion.isInstantenous() && level.getGameTime() % 20 != 0) {
					continue;
				}
				MobEffectInstance reducedEffect = new MobEffectInstance(effect.getEffect(), potion.isInstantenous() ? 1 : effect.getDuration() / 20, Math.max(0, effect.getAmplifier() - 1));
				livingEntity.addEffect(reducedEffect);
			}
		}

		if (cookTime > 0 && level.getGameTime() % 10 == 0) {
			collidingEntity.hurt(level.damageSources().inFire(), 1.0F);
		}
		return true;
	}

	public int getColorMultiplier() {
		return potionContents.getColor();
	}

	public int getLiquidLevel() {
		return liquidLevel;
	}

	public void fillWithRain() {
		if (getLiquidLevel() < 3 && !finishedCooking()) {
			setLiquidLevel(getLiquidLevel() + 1);
		}
	}

	public ItemInteractionResult handleBlockActivation(Level level, Player player, InteractionHand hand, BlockPos pos) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (itemStack.isEmpty()) {
			return ItemInteractionResult.CONSUME;
		}

		if (getLiquidLevel() < 3 && !finishedCooking()) {
			return fillWithWater(player, hand, itemStack);
		} else if (itemStack.getItem() == ModItems.EMPTY_POTION_VIAL.get() && finishedCooking() && getLiquidLevel() > 0) {
			if (fillVial(level, player, hand, itemStack, pos)) {
				return ItemInteractionResult.SUCCESS;
			}
		} else if (getLiquidLevel() == 3 && isItemValidForInput(itemStack)) {
			return addIngredient(level, player, itemStack, pos);
		}
		return ItemInteractionResult.CONSUME;
	}

	private ItemInteractionResult addIngredient(Level level, Player player, ItemStack itemStack, BlockPos pos) {
		addItem(itemStack, level, pos);

		if (itemStack.getItem() == Items.DRAGON_BREATH
				&& InventoryHelper.tryToAddToInventory(new ItemStack(Items.GLASS_BOTTLE), InventoryHelper.getMainInventoryItemHandlerFrom(player), 1) != 1) {
			Containers.dropItemStack(level, worldPosition.getX() + 0.5f, worldPosition.getY() + 1.5f, worldPosition.getZ() + 0.5f, new ItemStack(Items.GLASS_BOTTLE));
		}

		itemStack.shrink(1);

		return ItemInteractionResult.SUCCESS;
	}

	private boolean fillVial(Level level, Player player, InteractionHand hand, ItemStack itemStack, BlockPos pos) {
		if (finishedCooking() && hasNetherwart && potionContents.hasEffects() && getLiquidLevel() > 0) {
			ItemStack potion = removeContainedPotion(level, pos);

			itemStack.shrink(1);

			if (itemStack.getCount() <= 0) {
				player.setItemInHand(hand, potion);
			} else if (!player.getInventory().add(potion)) {
				level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.5D, worldPosition.getZ() + 0.5D, potion));
			}

			return true;
		}
		return false;
	}

	private ItemInteractionResult fillWithWater(Player player, InteractionHand hand, ItemStack itemStack) {
		if (itemStack.getItem() == Items.WATER_BUCKET) {
			if (!player.isCreative()) {
				player.setItemInHand(hand, new ItemStack(Items.BUCKET));
			}
		} else {
			IFluidHandlerItem fluidHandlerCapability = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
			if (fluidHandlerCapability == null || !drainWater(player, fluidHandlerCapability)) {
				return ItemInteractionResult.CONSUME;
			}
		}

		setLiquidLevel(3);
		player.level().playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
		cookTime = 0;

		return ItemInteractionResult.SUCCESS;
	}

	private Boolean drainWater(Player player, IFluidHandlerItem fh) {
		FluidStack waterStack = new FluidStack(Fluids.WATER, 1000);
		if (!waterStack.equals(fh.drain(waterStack, IFluidHandler.FluidAction.SIMULATE))) {
			return false;
		}

		if (!player.isCreative()) {
			fh.drain(waterStack, IFluidHandler.FluidAction.EXECUTE);
		}
		return true;
	}

	private void setLiquidLevel(int liquidLevel) {
		this.liquidLevel = liquidLevel;
		if (level != null) {
			BlockState blockState = getBlockState();
			blockState = blockState.setValue(ApothecaryCauldronBlock.LEVEL, liquidLevel);
			level.setBlockAndUpdate(getBlockPos(), blockState);
			level.updateNeighbourForOutputSignal(worldPosition, ModBlocks.APOTHECARY_CAULDRON.get());
		}
	}

	@Override
	public boolean getDataChanged() {
		boolean ret = dataChanged;
		dataChanged = false;
		return ret;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		super.onDataPacket(net, packet, registries);
		dataChanged = true;
	}
}
