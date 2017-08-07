package xreliquary.blocks.tile;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.client.particle.EntityCauldronBubbleFX;
import xreliquary.client.particle.EntityCauldronSteamFX;
import xreliquary.compat.waila.provider.IWailaDataChangeIndicator;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileEntityCauldron extends TileEntityBase implements IWailaDataChangeIndicator, ITickable {

	public int redstoneCount = 0;
	@Nonnull public List<PotionEffect> effects = Lists.newArrayList();
	public int glowstoneCount = 0;
	public boolean hasGunpowder = false;
	public boolean hasNetherwart = false;
	public boolean hasDragonBreath = false;
	private int cookTime = 0;
	private int liquidLevel = 0;
	private boolean dataChanged;

	public TileEntityCauldron() {
		dataChanged = true;
	}

	@Override
	public void update() {
		//Item addition gets handled by the block's onEntityCollided method.
		if(getHeatSources().contains(world.getBlockState(getPos().add(0, -1, 0)).getBlock()) && getLiquidLevel() > 0) {
			if(!effects.isEmpty() && hasNetherwart) {
				if(cookTime < getTotalCookTime())
					cookTime++;
			}
			if(world.isRemote) {
				for(int particleCount = 0; particleCount <= 2; ++particleCount)
					spawnBoilingParticles();

				if(hasDragonBreath)
					spawnDragonBreathParticles();
				else if(hasGunpowder)
					spawnGunpowderParticles();

				if(glowstoneCount > 0)
					spawnGlowstoneParticles();
				if(hasNetherwart) {
					spawnNetherwartParticles();
					if(finishedCooking()) {
						spawnFinishedParticles();
					}
				}
				if(redstoneCount > 0)
					spawnRedstoneParticles();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnBoilingParticles() {
		if(world.rand.nextInt(getTotalCookTime() * getTotalCookTime()) > cookTime * cookTime)
			return;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.33F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.33F;

		int color = PotionUtils.getPotionColorFromEffectList(effects);

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = ((color & 255) / 256F);

		EntityCauldronBubbleFX bubble = new EntityCauldronBubbleFX(Minecraft.getMinecraft().getTextureManager(), world, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, red, green, blue);
		EntityCauldronSteamFX steam = new EntityCauldronSteamFX(world, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.05D + 0.02F * getRenderLiquidLevel(), red, green, blue);
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(bubble);
		if(world.rand.nextInt(6) == 0)
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(steam);
	}

	private float getRenderLiquidLevel() {
		int j = MathHelper.clamp(getLiquidLevel(), 0, 3);
		return (float) (6 + 3 * j) / 16.0F;
	}

	@SideOnly(Side.CLIENT)
	private void spawnGunpowderParticles() {
		if(world.rand.nextInt(8) > 0)
			return;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	private void spawnDragonBreathParticles() {
		if(world.rand.nextInt(8) > 0)
			return;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	private void spawnGlowstoneParticles() {
		if(world.rand.nextInt(8) > 0)
			return;
		double gauss = 0.5D + world.rand.nextFloat() / 2;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, gauss, gauss, 0.0F);
	}

	@SideOnly(Side.CLIENT)
	private void spawnNetherwartParticles() {
		if(world.rand.nextInt(8) > 0)
			return;
		double gauss = 0.5D + world.rand.nextFloat() / 2;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, gauss, 0.0F, gauss);
	}

	@SideOnly(Side.CLIENT)
	private void spawnRedstoneParticles() {
		if(world.rand.nextInt(10) / this.redstoneCount > 0)
			return;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.spawnParticle(EnumParticleTypes.REDSTONE, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 1D, 0D, 0D);
	}

	@SideOnly(Side.CLIENT)
	private void spawnFinishedParticles() {
		if(world.rand.nextInt(8) > 0)
			return;
		float xOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (world.rand.nextFloat() - 0.5F) / 1.66F;
		world.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.setLiquidLevel(tag.getShort("liquidLevel"));
		this.glowstoneCount = tag.getInteger("glowstoneCount");
		this.hasNetherwart = tag.getBoolean("hasNetherwart");
		this.hasGunpowder = tag.getBoolean("hasGunpowder");
		this.hasDragonBreath = tag.getBoolean("hasDragonBreath");
		this.redstoneCount = tag.getInteger("redstoneCount");
		this.cookTime = tag.getInteger("cookTime");
		this.effects = XRPotionHelper.getPotionEffectsFromCompoundTag(tag);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("liquidLevel", getLiquidLevel());
		compound.setInteger("cookTime", cookTime);
		compound.setInteger("redstoneCount", redstoneCount);
		compound.setInteger("glowstoneCount", glowstoneCount);
		compound.setBoolean("hasGunpowder", hasGunpowder);
		compound.setBoolean("hasDragonBreath", hasDragonBreath);
		compound.setBoolean("hasNetherwart", hasNetherwart);
		XRPotionHelper.addPotionEffectsToCompoundTag(compound, effects);

		return compound;
	}

	private boolean finishedCooking() {
		return hasNetherwart && !effects.isEmpty() && this.cookTime >= getTotalCookTime() && (!hasDragonBreath || hasGunpowder);
	}

	private NBTTagCompound removeContainedPotion() {
		if(!hasNetherwart || effects.isEmpty() || getLiquidLevel() <= 0)
			return null;

		setLiquidLevel(getLiquidLevel() - 1);
		NBTTagCompound tag = getFinishedPotion();

		if(getLiquidLevel() <= 0) {
			clearAllFields();
		}
		return tag;
	}

	private NBTTagCompound getFinishedPotion() {
		NBTTagCompound tag = new NBTTagCompound();
		XRPotionHelper.addPotionEffectsToCompoundTag(tag, XRPotionHelper.augmentPotionEffects(effects, redstoneCount, glowstoneCount));
		tag.setBoolean("hasPotion", true);
		if(hasDragonBreath) {
			tag.setBoolean("lingering", true);
		} else if(hasGunpowder) {
			tag.setBoolean("splash", true);
		}
		return tag;
	}

	private void clearAllFields() {
		this.cookTime = 0;
		this.glowstoneCount = 0;
		this.hasGunpowder = false;
		this.hasNetherwart = false;
		this.redstoneCount = 0;
		this.effects.clear();
		this.dataChanged = true;
		this.hasDragonBreath = false;
		IBlockState blockState = world.getBlockState(this.getPos());
		world.notifyBlockUpdate(this.getPos(), blockState, blockState, 3);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	private boolean isItemValidForInput(ItemStack ist) {
		if(ist.getItem() instanceof ItemPotionEssence && this.effects.isEmpty())
			return true;

		if(effects.isEmpty())
			return false;

		if(ist.getItem() == Items.GUNPOWDER && !this.hasGunpowder)
			return true;
		if(ist.getItem() == Items.GLOWSTONE_DUST && this.glowstoneCount < getGlowstoneAmpLimit())
			return true;
		if(ist.getItem() == Items.REDSTONE && this.redstoneCount < getRedstoneAmpLimit())
			return true;
		if(ist.getItem() == Items.NETHER_WART && !this.hasNetherwart)
			return true;
		return ist.getItem() == Items.DRAGON_BREATH && !this.hasDragonBreath;
	}

	private void addItem(ItemStack ist) {
		if(ist.getItem() instanceof ItemPotionEssence) {
			effects = XRPotionHelper.getPotionEffectsFromStack(ist);
		} else if(ist.getItem() == Items.GUNPOWDER) {
			this.hasGunpowder = true;
		} else if(ist.getItem() == Items.GLOWSTONE_DUST) {
			++this.glowstoneCount;
		} else if(ist.getItem() == Items.REDSTONE) {
			++this.redstoneCount;
		} else if(ist.getItem() == Items.NETHER_WART) {
			this.hasNetherwart = true;
		} else if(ist.getItem() == Items.DRAGON_BREATH) {
			this.hasDragonBreath = true;
		}

		IBlockState blockState = world.getBlockState(this.getPos());
		world.notifyBlockUpdate(this.getPos(), blockState, blockState, 3);
	}

	private int getGlowstoneAmpLimit() {
		return Settings.ApothecaryCauldron.glowstoneLimit;
	}

	private int getRedstoneAmpLimit() {
		return Settings.ApothecaryCauldron.redstoneLimit;
	}

	private List<Block> getHeatSources() {
		List<Block> heatSources = new ArrayList<>();
		List<String> heatSourceBlockNames = Settings.ApothecaryCauldron.heatSources;

		heatSourceBlockNames.stream().filter(blockName -> !heatSources.contains(RegistryHelper.getBlockFromName(blockName))).forEach(blockName -> heatSources.add(RegistryHelper.getBlockFromName(blockName)));
		//defaults that can't be removed.
		heatSources.add(Blocks.LAVA);
		heatSources.add(Blocks.FLOWING_LAVA);
		heatSources.add(Blocks.FIRE);
/*
		if(Loader.isModLoaded(Compatibility.MOD_ID.THAUMCRAFT))
			heatSources.add(BlocksTC.nitor); //TODO add back when Thaumcraft is back in
*/
		return heatSources;
	}

	private int getTotalCookTime() {
		return Settings.ApothecaryCauldron.cookTime;
	}

	public void handleCollidingEntity(World world, BlockPos pos, Entity collidingEntity) {
		int l = getLiquidLevel();
		float f = (float) pos.getY() + (6.0F + (float) (3 * l)) / 16.0F;
		if(collidingEntity.getEntityBoundingBox().minY <= (double) f) {

			if(collidingEntity.isBurning() && l > 0) {
				collidingEntity.extinguish();
			}
			if(collidingEntity instanceof EntityLivingBase) {
				if(this.effects.isEmpty())
					return;
				//apply potion effects when done cooking potion (potion essence and netherwart in and fire below at the minimum)
				if(finishedCooking()) {
					for(PotionEffect effect : this.effects) {
						Potion potion = effect.getPotion();
						if(potion.isInstant() && world.getWorldTime() % 20 != 0)
							continue;
						PotionEffect reducedEffect = new PotionEffect(effect.getPotion(), potion.isInstant() ? 1 : effect.getDuration() / 20, Math.max(0, effect.getAmplifier() - 1));
						((EntityLivingBase) collidingEntity).addPotionEffect(reducedEffect);
					}
				}

				if(this.cookTime > 0 && world.getWorldTime() % 10 == 0) {
					collidingEntity.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
				}
			}

			if(collidingEntity instanceof EntityItem) {
				ItemStack item = ((EntityItem) collidingEntity).getItem();
				while(this.isItemValidForInput(item)) {
					this.addItem(item);
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

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void fillWithRain() {
		if(getLiquidLevel() < 3 && !finishedCooking()) {
			setLiquidLevel(getLiquidLevel() + 1);
		}
	}

	public boolean handleBlockActivation(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);

		if(itemStack.isEmpty())
			return false;

		if(getLiquidLevel() < 3 && !finishedCooking()) {
			if(itemStack.getItem() == Items.WATER_BUCKET) {
				if(!player.capabilities.isCreativeMode)
					player.setHeldItem(hand, new ItemStack(Items.BUCKET));
			} else if(itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				FluidStack waterStack = new FluidStack(FluidRegistry.WATER, 1000);
				IFluidHandler fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				//noinspection ConstantConditions
				if(!waterStack.equals(fluidHandler.drain(waterStack, false)))
					return false;

				if(!player.capabilities.isCreativeMode)
					fluidHandler.drain(waterStack, true);
			} else {
				return false;
			}
			setLiquidLevel(3);
			cookTime = 0;

			return true;
		} else if(itemStack.getItem() == ModItems.potion && (itemStack.getTagCompound() == null || !itemStack.getTagCompound().getBoolean("hasPotion")) && finishedCooking() && getLiquidLevel() > 0) {
			if(finishedCooking()) {
				ItemStack potion = new ItemStack(ModItems.potion, 1, 0);
				potion.setTagCompound(removeContainedPotion());

				itemStack.shrink(1);

				if(itemStack.getCount() <= 0) {
					player.setHeldItem(hand, potion);
				} else if(!player.inventory.addItemStackToInventory(potion)) {
					world.spawnEntity(new EntityItem(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.5D, (double) pos.getZ() + 0.5D, potion));
				}

				return true;
			}
		} else if(getLiquidLevel() == 3) {
			if(isItemValidForInput(itemStack)) {
				addItem(itemStack);

				if(itemStack.getItem() == Items.DRAGON_BREATH) {
					if(InventoryHelper.tryToAddToInventory(new ItemStack(Items.GLASS_BOTTLE), player.inventory, 1) != 1) {
						InventoryHelper.spawnItemStack(world, pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, new ItemStack(Items.GLASS_BOTTLE));
					}
				}

				itemStack.shrink(1);

				return true;
			}
		}
		return false;
	}

	private void setLiquidLevel(int liquidLevel) {
		this.liquidLevel = liquidLevel;
		if(this.world != null) {
			IBlockState blockState = this.world.getBlockState(this.getPos());
			blockState = blockState.withProperty(BlockApothecaryCauldron.LEVEL, liquidLevel);
			this.world.setBlockState(this.getPos(), blockState);
			this.world.updateComparatorOutputLevel(pos, ModBlocks.apothecaryCauldron);
		}
	}

	@Override
	public boolean getDataChanged() {
		boolean ret = this.dataChanged;
		this.dataChanged = false;
		return ret;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		this.dataChanged = true;
	}
}
