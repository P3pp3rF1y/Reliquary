package xreliquary.blocks.tile;

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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
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
import xreliquary.util.potions.PotionEssence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityCauldron extends TileEntityBase implements IWailaDataChangeIndicator, ITickable {

	public int redstoneCount = 0;
	public PotionEssence potionEssence = null;
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
		if(getHeatSources().contains(worldObj.getBlockState(getPos().add(0, -1, 0)).getBlock()) && getLiquidLevel() > 0) {
			if(potionEssence != null && hasNetherwart) {
				if(cookTime < getTotalCookTime())
					cookTime++;
			}
			if(worldObj.isRemote) {
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
		if(worldObj.rand.nextInt(getTotalCookTime() * getTotalCookTime()) > cookTime * cookTime)
			return;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.33F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.33F;

		int color = getColor(potionEssence);

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = ((color & 255) / 256F);

		EntityCauldronBubbleFX bubble = new EntityCauldronBubbleFX(Minecraft.getMinecraft().getTextureManager(), worldObj, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, red, green, blue);
		EntityCauldronSteamFX steam = new EntityCauldronSteamFX(worldObj, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + 0.01D + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.05D + 0.02F * getRenderLiquidLevel(), red, green, blue);
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(bubble);
		if(worldObj.rand.nextInt(6) == 0)
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(steam);
	}

	private float getRenderLiquidLevel() {
		int j = MathHelper.clamp_int(getLiquidLevel(), 0, 3);
		return (float) (6 + 3 * j) / 16.0F;
	}

	private int getColor(PotionEssence essence) {
		return PotionUtils.getPotionColorFromEffectList(essence == null || essence.getEffects() == null ? Collections.emptyList() : essence.getEffects());
	}

	@SideOnly(Side.CLIENT)
	private void spawnGunpowderParticles() {
		if(worldObj.rand.nextInt(8) > 0)
			return;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	private void spawnDragonBreathParticles() {
		if(worldObj.rand.nextInt(8) > 0)
			return;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		worldObj.spawnParticle(EnumParticleTypes.DRAGON_BREATH, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0.0D, 0.1D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	private void spawnGlowstoneParticles() {
		if(worldObj.rand.nextInt(8) > 0)
			return;
		double gauss = 0.5D + worldObj.rand.nextFloat() / 2;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, gauss, gauss, 0.0F);
	}

	@SideOnly(Side.CLIENT)
	private void spawnNetherwartParticles() {
		if(worldObj.rand.nextInt(8) > 0)
			return;
		double gauss = 0.5D + worldObj.rand.nextFloat() / 2;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, gauss, 0.0F, gauss);
	}

	@SideOnly(Side.CLIENT)
	private void spawnRedstoneParticles() {
		if(worldObj.rand.nextInt(10) / this.redstoneCount > 0)
			return;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		worldObj.spawnParticle(EnumParticleTypes.REDSTONE, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 1D, 0D, 0D);
	}

	@SideOnly(Side.CLIENT)
	private void spawnFinishedParticles() {
		if(worldObj.rand.nextInt(8) > 0)
			return;
		float xOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		float zOffset = (worldObj.rand.nextFloat() - 0.5F) / 1.66F;
		worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.getPos().getX() + 0.5D + xOffset, this.getPos().getY() + getRenderLiquidLevel(), this.getPos().getZ() + 0.5D + zOffset, 0D, 0D, 0D);
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
		this.potionEssence = new PotionEssence((NBTTagCompound) tag.getTag("potionEssence"));
		if(potionEssence.getEffects().size() == 0)
			this.potionEssence = null;
	}

	@SuppressWarnings("NullableProblems")
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
		compound.setTag("potionEssence", potionEssence == null ? new NBTTagCompound() : potionEssence.writeToNBT());

		return compound;
	}

	private boolean finishedCooking() {
		return hasNetherwart && potionEssence != null && this.cookTime >= getTotalCookTime() && (!hasDragonBreath || hasGunpowder);
	}

	public NBTTagCompound removeContainedPotion() {
		if(!hasNetherwart || potionEssence == null || getLiquidLevel() <= 0)
			return null;

		setLiquidLevel(getLiquidLevel() - 1);
		NBTTagCompound tag = getFinishedPotion();

		if(getLiquidLevel() <= 0) {
			clearAllFields();
		}
		return tag;
	}

	private NBTTagCompound getFinishedPotion() {
		NBTTagCompound tag = potionEssence.writeToNBT();
		NBTTagList effectsList = tag.getTagList("effects", 10);
		NBTTagCompound newTag = new NBTTagCompound();
		newTag.setTag("effects", effectsList);
		newTag.setBoolean("hasPotion", true);
		if (hasDragonBreath) {
			newTag.setBoolean("lingering", true);
		} else if(hasGunpowder) {
			newTag.setBoolean("splash", true);
		}
		return newTag;
	}

	private void clearAllFields() {
		this.cookTime = 0;
		this.glowstoneCount = 0;
		this.hasGunpowder = false;
		this.hasNetherwart = false;
		this.redstoneCount = 0;
		this.potionEssence = null;
		this.dataChanged = true;
		this.hasDragonBreath = false;
		IBlockState blockState = worldObj.getBlockState(this.getPos());
		worldObj.notifyBlockUpdate(this.getPos(), blockState, blockState, 3);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	private boolean isItemValidForInput(ItemStack ist) {
		if(ist.getItem() instanceof ItemPotionEssence && this.potionEssence == null)
			return true;
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
			potionEssence = new PotionEssence(ist.getTagCompound());
		} else if(ist.getItem() == Items.GUNPOWDER) {
			this.hasGunpowder = true;
		} else if(ist.getItem() == Items.GLOWSTONE_DUST) {
			++this.glowstoneCount;
			potionEssence.addGlowstone(this.glowstoneCount);
		} else if(ist.getItem() == Items.REDSTONE) {
			++this.redstoneCount;
			potionEssence.addRedstone(this.redstoneCount);
		} else if(ist.getItem() == Items.NETHER_WART) {
			this.hasNetherwart = true;
		} else if(ist.getItem() == Items.DRAGON_BREATH) {
			this.hasDragonBreath = true;
		}

		IBlockState blockState = worldObj.getBlockState(this.getPos());
		worldObj.notifyBlockUpdate(this.getPos(), blockState, blockState, 3);
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
				if(this.potionEssence == null)
					return;
				//apply potion effects when done cooking potion (potion essence and netherwart in and fire below at the minimum)
				if (finishedCooking()) {
					for(PotionEffect effect : this.potionEssence.getEffects()) {
						Potion potion = effect.getPotion();
						if(potion.isInstant() && world.getWorldTime() % 20 != 0)
							continue;
						PotionEffect reducedEffect = new PotionEffect(effect.getPotion(), potion.isInstant() ? 1 : effect.getDuration() / 20, Math.max(0, effect.getAmplifier() - 1));
						((EntityLivingBase) collidingEntity).addPotionEffect(reducedEffect);
					}
				}

				if(this.cookTime > 0 && world.getWorldTime() % 10 == 0) {
					collidingEntity.attackEntityFrom(DamageSource.inFire, 1.0F);
				}
			}

			if(collidingEntity instanceof EntityItem) {
				ItemStack item = ((EntityItem) collidingEntity).getEntityItem();
				while(this.isItemValidForInput(item)) {

					this.addItem(item);
					if(--item.stackSize < 1)
						collidingEntity.setDead();
				}
			}

		}
	}

	public int getColorMultiplier() {
		return PotionUtils.getPotionColorFromEffectList(potionEssence == null ? new ArrayList<>() : potionEssence.getEffects());
	}

	public int getLiquidLevel() {
		return liquidLevel;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void fillWithRain() {
		if(getLiquidLevel() < 3 && !finishedCooking()) {
			setLiquidLevel(getLiquidLevel() + 1);
		}
	}

	public boolean handleBlockActivation(World world, EntityPlayer player) {
		ItemStack itemStack = player.inventory.getCurrentItem();

		if(itemStack == null)
			return false;

		if(getLiquidLevel() < 3 && !finishedCooking()) {
			if(itemStack.getItem() == Items.WATER_BUCKET) {
				if(!player.capabilities.isCreativeMode)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.BUCKET));
			} else if(itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				FluidStack waterStack = new FluidStack(FluidRegistry.WATER, 1000);
				IFluidHandler fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				//noinspection ConstantConditions
				if(!fluidHandler.drain(waterStack, false).equals(waterStack))
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

				--itemStack.stackSize;

				if(itemStack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, potion);
				} else if(!player.inventory.addItemStackToInventory(potion)) {
					world.spawnEntityInWorld(new EntityItem(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.5D, (double) pos.getZ() + 0.5D, potion));
				}

				return true;
			}
		} else if(getLiquidLevel() == 3) {
			if(isItemValidForInput(itemStack)) {
				addItem(itemStack);

				if (itemStack.getItem() == Items.DRAGON_BREATH) {
					if (InventoryHelper.tryToAddToInventory(new ItemStack(Items.GLASS_BOTTLE),player.inventory, 1) != 1) {
						InventoryHelper.spawnItemStack(world, pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, new ItemStack(Items.GLASS_BOTTLE));
					}
				}

				--itemStack.stackSize;

				if(itemStack.stackSize <= 0)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);

				return true;
			}
		}
		return false;
	}

	private void setLiquidLevel(int liquidLevel) {
		this.liquidLevel = liquidLevel;
		if(this.worldObj != null) {
			IBlockState blockState = this.worldObj.getBlockState(this.getPos());
			blockState = blockState.withProperty(BlockApothecaryCauldron.LEVEL, liquidLevel);
			this.worldObj.setBlockState(this.getPos(), blockState);
			this.worldObj.updateComparatorOutputLevel(pos, ModBlocks.apothecaryCauldron);
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
