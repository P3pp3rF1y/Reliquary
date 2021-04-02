package xreliquary.blocks.tile;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.blocks.ApothecaryCauldronBlock;
import xreliquary.client.particle.BubbleColorParticleData;
import xreliquary.client.particle.SteamColorParticleData;
import xreliquary.compat.waila.provider.IWailaDataChangeIndicator;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.PotionEssenceItem;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.potions.XRPotionHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApothecaryCauldronTileEntity extends TileEntityBase implements IWailaDataChangeIndicator, ITickableTileEntity {
	private int redstoneCount = 0;
	private List<EffectInstance> effects = Lists.newArrayList();
	private int glowstoneCount = 0;
	private boolean hasGunpowder = false;
	private boolean hasNetherwart = false;
	private boolean hasDragonBreath = false;
	private int cookTime = 0;
	private int liquidLevel = 0;
	private boolean dataChanged;

	public ApothecaryCauldronTileEntity() {
		super(ModBlocks.APOTHECARY_CAULDRON_TILE_TYPE.get());
		dataChanged = true;
	}

	@Override
	public void tick() {
		//Item addition gets handled by the block's onEntityCollided method.
		if (getHeatSources().contains(world.getBlockState(getPos().add(0, -1, 0)).getBlock()) && getLiquidLevel() > 0) {
			if (!effects.isEmpty() && hasNetherwart && cookTime < getTotalCookTime()) {
				cookTime++;
			}
			if (world.isRemote) {
				for (int particleCount = 0; particleCount <= 2; ++particleCount) {
					spawnBoilingParticles();
				}

				if (hasDragonBreath) {
					spawnDragonBreathParticles();
				} else if (hasGunpowder) {
					spawnGunpowderParticles();
				}

				if (glowstoneCount > 0) {
					spawnGlowstoneParticles();
				}
				if (hasNetherwart) {
					spawnNetherwartParticles();
					if (finishedCooking()) {
						spawnFinishedParticles();
					}
				}
				if (redstoneCount > 0) {
					spawnRedstoneParticles();
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnBoilingParticles() {
		if (world.rand.nextInt(getTotalCookTime() * getTotalCookTime()) > cookTime * cookTime) {
			return;
		}
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.33F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.33F;

		int color = PotionUtils.getPotionColorFromEffectList(effects);

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = ((color & 255) / 256F);

		world.addParticle(new BubbleColorParticleData(red, green, blue), getPos().getX() + 0.5D + xOffset, getPos().getY() + 0.01D + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D);

		if (world.rand.nextInt(6) == 0) {
			world.addParticle(new SteamColorParticleData(red, green, blue), getPos().getX() + 0.5D + xOffset, getPos().getY() + 0.01D + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, 0D, 0.05D + 0.02F * getRenderLiquidLevel(), 0D);
		}
	}

	private float getRenderLiquidLevel() {
		int j = MathHelper.clamp(getLiquidLevel(), 0, 3);
		return (float) (6 + 3 * j) / 16.0F;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnGunpowderParticles() {
		if (world.rand.nextInt(8) > 0) {
			return;
		}
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.addParticle(ParticleTypes.SMOKE, getPos().getX() + 0.5D + xOffset, getPos().getY() + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnDragonBreathParticles() {
		if (world.rand.nextInt(8) > 0) {
			return;
		}
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.addParticle(ParticleTypes.DRAGON_BREATH, getPos().getX() + 0.5D + xOffset, getPos().getY() + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnGlowstoneParticles() {
		if (world.rand.nextInt(8) > 0) {
			return;
		}
		double gauss = 0.5D + world.rand.nextFloat() / 2;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.addParticle(ParticleTypes.ENTITY_EFFECT, getPos().getX() + 0.5D + xOffset, getPos().getY() + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, gauss, gauss, 0.0F);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnNetherwartParticles() {
		if (world.rand.nextInt(8) > 0) {
			return;
		}
		double gauss = 0.5D + world.rand.nextFloat() / 2;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.addParticle(ParticleTypes.ENTITY_EFFECT, getPos().getX() + 0.5D + xOffset, getPos().getY() + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, gauss, 0.0F, gauss);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnRedstoneParticles() {
		if (world.rand.nextInt(10) / redstoneCount > 0) {
			return;
		}
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.addParticle(RedstoneParticleData.REDSTONE_DUST, getPos().getX() + 0.5D + xOffset, getPos().getY() + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, 1D, 0D, 0D);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnFinishedParticles() {
		if (world.rand.nextInt(8) > 0) {
			return;
		}
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.addParticle(ParticleTypes.WITCH, getPos().getX() + 0.5D + xOffset, getPos().getY() + getRenderLiquidLevel(), getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D);
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		setLiquidLevel(tag.getShort("liquidLevel"));
		glowstoneCount = tag.getInt("glowstoneCount");
		hasNetherwart = tag.getBoolean("hasNetherwart");
		hasGunpowder = tag.getBoolean("hasGunpowder");
		hasDragonBreath = tag.getBoolean("hasDragonBreath");
		redstoneCount = tag.getInt("redstoneCount");
		cookTime = tag.getInt("cookTime");
		effects = XRPotionHelper.getPotionEffectsFromCompoundTag(tag);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("liquidLevel", getLiquidLevel());
		compound.putInt("cookTime", cookTime);
		compound.putInt("redstoneCount", redstoneCount);
		compound.putInt("glowstoneCount", glowstoneCount);
		compound.putBoolean("hasGunpowder", hasGunpowder);
		compound.putBoolean("hasDragonBreath", hasDragonBreath);
		compound.putBoolean("hasNetherwart", hasNetherwart);
		XRPotionHelper.addPotionEffectsToCompoundTag(compound, effects);

		return compound;
	}

	private boolean finishedCooking() {
		return hasNetherwart && !effects.isEmpty() && cookTime >= getTotalCookTime() && (!hasDragonBreath || hasGunpowder);
	}

	private ItemStack removeContainedPotion() {
		ItemStack potion;
		if (hasDragonBreath) {
			potion = new ItemStack(ModItems.LINGERING_POTION.get());
		} else if (hasGunpowder) {
			potion = new ItemStack(ModItems.SPLASH_POTION.get());
		} else {
			potion = new ItemStack(ModItems.POTION.get());
		}
		XRPotionHelper.addPotionEffectsToStack(potion, XRPotionHelper.augmentPotionEffects(effects, redstoneCount, glowstoneCount));

		setLiquidLevel(getLiquidLevel() - 1);
		if (getLiquidLevel() <= 0) {
			clearAllFields();
		}
		return potion;
	}

	private void clearAllFields() {
		cookTime = 0;
		glowstoneCount = 0;
		hasGunpowder = false;
		hasNetherwart = false;
		redstoneCount = 0;
		effects.clear();
		dataChanged = true;
		hasDragonBreath = false;
		BlockState blockState = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	private boolean isItemValidForInput(ItemStack stack) {
		if (stack.getItem() instanceof PotionEssenceItem && effects.isEmpty()) {
			return true;
		}

		if (effects.isEmpty()) {
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

	private void addItem(ItemStack stack) {
		if (stack.getItem() instanceof PotionEssenceItem) {
			effects = XRPotionHelper.getPotionEffectsFromStack(stack);
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

		BlockState blockState = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	private int getGlowstoneAmpLimit() {
		return Settings.COMMON.blocks.apothecaryCauldron.glowstoneLimit.get();
	}

	private int getRedstoneAmpLimit() {
		return Settings.COMMON.blocks.apothecaryCauldron.redstoneLimit.get();
	}

	private Set<Block> getHeatSources() {
		Set<Block> heatSources = new HashSet<>();
		List<String> heatSourceBlockNames = Settings.COMMON.blocks.apothecaryCauldron.heatSources.get();

		heatSourceBlockNames.forEach(blockName -> heatSources.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName))));
		//defaults that can't be removed.
		heatSources.add(Blocks.LAVA);
		heatSources.add(Blocks.FIRE);
/*
		if(ModList.get().isLoaded(Compatibility.MOD_ID.THAUMCRAFT))
			heatSources.add(BlocksTC.nitor); //TODO add back when Thaumcraft is back in
*/
		return heatSources;
	}

	private int getTotalCookTime() {
		return Settings.COMMON.blocks.apothecaryCauldron.cookTime.get();
	}

	public void handleCollidingEntity(World world, BlockPos pos, Entity collidingEntity) {
		int l = getLiquidLevel();
		float f = (float) pos.getY() + (6.0F + (float) (3 * l)) / 16.0F;
		if (collidingEntity.getBoundingBox().minY <= (double) f) {

			if (collidingEntity.isBurning() && l > 0) {
				collidingEntity.extinguish();
			}
			if (collidingEntity instanceof LivingEntity) {
				if (effects.isEmpty()) {
					return;
				}
				//apply potion effects when done cooking potion (potion essence and netherwart in and fire below at the minimum)
				if (finishedCooking()) {
					for (EffectInstance effect : effects) {
						Effect potion = effect.getPotion();
						if (potion.isInstant() && world.getGameTime() % 20 != 0) {
							continue;
						}
						EffectInstance reducedEffect = new EffectInstance(effect.getPotion(), potion.isInstant() ? 1 : effect.getDuration() / 20, Math.max(0, effect.getAmplifier() - 1));
						((LivingEntity) collidingEntity).addPotionEffect(reducedEffect);
					}
				}

				if (cookTime > 0 && world.getGameTime() % 10 == 0) {
					collidingEntity.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
				}
			}

			if (collidingEntity instanceof ItemEntity) {
				ItemStack item = ((ItemEntity) collidingEntity).getItem();
				while (isItemValidForInput(item)) {
					addItem(item);
					item.shrink(1);
				}
			}

		}
	}

	public int getColorMultiplier() {
		return PotionUtils.getPotionColorFromEffectList(effects);
	}

	public int getLiquidLevel() {
		return liquidLevel;
	}

	public void fillWithRain() {
		if (getLiquidLevel() < 3 && !finishedCooking()) {
			setLiquidLevel(getLiquidLevel() + 1);
		}
	}

	public ActionResultType handleBlockActivation(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getHeldItem(hand);

		if (itemStack.isEmpty()) {
			return ActionResultType.CONSUME;
		}

		if (getLiquidLevel() < 3 && !finishedCooking()) {
			return fillWithWater(player, hand, itemStack);
		} else if (itemStack.getItem() == ModItems.EMPTY_POTION_VIAL.get() && finishedCooking() && getLiquidLevel() > 0) {
			if (fillVial(world, player, hand, itemStack)) {
				return ActionResultType.SUCCESS;
			}
		} else if (getLiquidLevel() == 3 && isItemValidForInput(itemStack)) {
			return addIngredient(world, player, itemStack);
		}
		return ActionResultType.CONSUME;
	}

	private ActionResultType addIngredient(World world, PlayerEntity player, ItemStack itemStack) {
		addItem(itemStack);

		if (itemStack.getItem() == Items.DRAGON_BREATH
				&& InventoryHelper.getItemHandlerFrom(player).map(handler -> InventoryHelper.tryToAddToInventory(new ItemStack(Items.GLASS_BOTTLE), handler, 1)).orElse(0) != 1) {
			net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, new ItemStack(Items.GLASS_BOTTLE));
		}

		itemStack.shrink(1);

		return ActionResultType.SUCCESS;
	}

	private boolean fillVial(World world, PlayerEntity player, Hand hand, ItemStack itemStack) {
		if (finishedCooking() && hasNetherwart && !effects.isEmpty() && getLiquidLevel() > 0) {
			ItemStack potion = removeContainedPotion();

			itemStack.shrink(1);

			if (itemStack.getCount() <= 0) {
				player.setHeldItem(hand, potion);
			} else if (!player.inventory.addItemStackToInventory(potion)) {
				world.addEntity(new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.5D, (double) pos.getZ() + 0.5D, potion));
			}

			return true;
		}
		return false;
	}

	private ActionResultType fillWithWater(PlayerEntity player, Hand hand, ItemStack itemStack) {
		if (itemStack.getItem() == Items.WATER_BUCKET) {
			if (!player.isCreative()) {
				player.setHeldItem(hand, new ItemStack(Items.BUCKET));
			}
		} else if (Boolean.FALSE.equals(itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).map(fh -> drainWater(player, fh)).orElse(false))) {
			return ActionResultType.CONSUME;
		}

		setLiquidLevel(3);
		cookTime = 0;

		return ActionResultType.SUCCESS;
	}

	private Boolean drainWater(PlayerEntity player, IFluidHandlerItem fh) {
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
		if (world != null) {
			BlockState blockState = world.getBlockState(getPos());
			blockState = blockState.with(ApothecaryCauldronBlock.LEVEL, liquidLevel);
			world.setBlockState(getPos(), blockState);
			world.updateComparatorOutputLevel(pos, ModBlocks.APOTHECARY_CAULDRON.get());
		}
	}

	@Override
	public boolean getDataChanged() {
		boolean ret = dataChanged;
		dataChanged = false;
		return ret;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		super.onDataPacket(net, packet);
		dataChanged = true;
	}
}
