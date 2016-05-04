package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockFertileLilypad;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModItems;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.items.util.HarvestRodItemStackHandler;
import xreliquary.items.util.IHarvestRodCache;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketItemHandlerSync;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemHarvestRod extends ItemToggleable {

	public static final String BONE_MEAL_MODE = "bone_meal";
	public static final String PLANTABLE_MODE = "plantable";
	public static final String HOE_MODE = "hoe";
	public static final String MODE_NBT_TAG = "mode";
	public static final String PLANTABLE_INDEX_NBT_TAG = "plantable_index";
	public static final String PLANTABLE_QUANTITIES_NBT_TAG = "plantable_quantities";
	private static final int AOE_START_COOLDOWN = 10;

	public ItemHarvestRod() {
		super(Names.harvest_rod);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("charge", Integer.toString(getBoneMealCount(ist))), ist, list);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.WHITE + Items.dye.getItemStackDisplayName(new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META))), ist, list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		return EnumAction.BLOCK;
	}

	public int getBonemealLimit() {
		return Settings.HarvestRod.boneMealLimit;
	}

	public int getBonemealWorth() {
		return Settings.HarvestRod.boneMealWorth;
	}

	public int getBonemealCost() {
		return Settings.HarvestRod.boneMealCost;
	}

	public int getLuckRolls() {
		return Settings.HarvestRod.boneMealLuckRolls;
	}

	public int getLuckPercent() {
		return Settings.HarvestRod.boneMealLuckPercentChance;
	}

	public int getBreakRadius() {
		return Settings.HarvestRod.AOERadius;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new ICapabilitySerializable<NBTTagCompound>() {
			HarvestRodItemStackHandler itemHandler = new HarvestRodItemStackHandler();

			@Override
			public NBTTagCompound serializeNBT() {
				return itemHandler.serializeNBT();
			}

			@Override
			public void deserializeNBT(NBTTagCompound tagCompound) {
				itemHandler.deserializeNBT(tagCompound);
			}

			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					return true;
				return false;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					return (T) itemHandler;

				return null;
			}
		};
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int slotNumber, boolean isSelected) {
		//TODO remove backwards compatibility in the future
		handleBackwardsCompatibility(ist);

		if(world.isRemote)
			return;
		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;

		if(this.isEnabled(ist)) {
			if(getBoneMealCount(ist) + getBonemealWorth() <= getBonemealLimit()) {
				if(InventoryHelper.consumeItem(new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META), player)) {
					setBoneMealCount(ist, getBoneMealCount(ist) + getBonemealWorth(), slotNumber, player);
				}
			}

			consumePlantables(ist, player, slotNumber);
		}

		if (player.inventory.getStackInSlot(slotNumber)!= null && player.inventory.getStackInSlot(slotNumber).getItem() == ModItems.harvestRod && isSelected) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
		} else if (player.inventory.offHandInventory[0] != null && player.inventory.offHandInventory[0].getItem() == ModItems.harvestRod) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(EnumHand.OFF_HAND, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
		}
	}

	private NBTTagCompound getItemHandlerNBT(ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return null;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.serializeNBT();
	}

	@Deprecated
	private void handleBackwardsCompatibility(ItemStack ist) {
		if(ist.getTagCompound() != null && ist.getTagCompound().hasKey("bonemeal")) {
			IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

			if(itemHandler instanceof FilteredItemStackHandler) {
				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				if(ist.getTagCompound().hasKey("bonemeal")) {
					filteredHandler.setTotalAmount(0, NBTHelper.getInteger("bonemeal", ist));
					ist.getTagCompound().removeTag("bonemeal");
				}

				if(ist.getTagCompound().hasKey("Items")) {
					NBTTagList itemsList = ist.getTagCompound().getTagList("Items", 10);
					NBTTagList quantities = ist.getTagCompound().getTagList(PLANTABLE_QUANTITIES_NBT_TAG, 3);

					for(int i = 0; i < itemsList.tagCount(); ++i) {
						NBTTagCompound item = itemsList.getCompoundTagAt(i);
						byte slotIndex = item.getByte("Slot");
						if(slotIndex >= 0) {
							int slot = itemHandler.getSlots() - 1;
							filteredHandler.insertItem(slot, ItemStack.loadItemStackFromNBT(item), false);
							filteredHandler.setTotalAmount(filteredHandler.getParentSlot(slot), ((NBTTagInt) quantities.get(i)).getInt());
						}
					}
					ist.getTagCompound().removeTag("Items");
					ist.getTagCompound().removeTag(PLANTABLE_QUANTITIES_NBT_TAG);
				}
			}

			setMode(ist, BONE_MEAL_MODE);
		}
	}

	private void consumePlantables(ItemStack harvestRod, EntityPlayer player, int slotNumber) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			ItemStack currentStack = player.inventory.mainInventory[slot];
			if(currentStack == null) {
				continue;
			}
			if(currentStack.getItem() instanceof IPlantable && incrementPlantable(harvestRod, currentStack, slotNumber, player)) {
				InventoryHelper.consumeItem(currentStack, player, 0, 1);
				break;
			}
		}
	}

	@Override
	public boolean onBlockStartBreak(ItemStack ist, BlockPos pos, EntityPlayer player) {
		if(player.worldObj.isRemote)
			return false;

		boolean brokenBlock = false;

		Block block = player.worldObj.getBlockState(pos).getBlock();
		if(block instanceof IPlantable || block instanceof BlockCrops || block == Blocks.melon_block || block == Blocks.pumpkin) {
			for(int xOff = -getBreakRadius(); xOff <= getBreakRadius(); xOff++) {
				for(int yOff = -getBreakRadius(); yOff <= getBreakRadius(); yOff++) {
					for(int zOff = -getBreakRadius(); zOff <= getBreakRadius(); zOff++) {
						doHarvestBlockBreak(block, ist, pos, player, xOff, yOff, zOff);
						brokenBlock = true;
					}
				}
			}
		}

		return brokenBlock;
	}

	public boolean doHarvestBlockBreak(Block initialBlock, ItemStack ist, BlockPos pos, EntityPlayer player, int xOff, int yOff, int zOff) {
		pos = pos.add(xOff, yOff, zOff);

		IBlockState blockState = player.worldObj.getBlockState(pos);
		Block block = blockState.getBlock();

		if((initialBlock == Blocks.melon_block || initialBlock == Blocks.pumpkin) && !(block == Blocks.melon_block || block == Blocks.pumpkin))
			return false;

		if(!(block instanceof IPlantable || block instanceof BlockCrops || block == Blocks.melon_block || block == Blocks.pumpkin))
			return false;
		if(block instanceof BlockFertileLilypad)
			return false;

		if(player.worldObj.isRemote) {
			for(int particles = 0; particles <= 8; particles++)
				player.worldObj.playAuxSFXAtEntity(player, 2001, pos, Block.getStateId(blockState));
		} else {
			List<ItemStack> drops = blockState.getBlock().getDrops(player.worldObj, pos, blockState, EnchantmentHelper.getEnchantmentLevel(Enchantments.fortune, ist));
			Random rand = new Random();

			for(ItemStack stack : drops) {
				float f = 0.7F;
				double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
				EntityItem entityitem = new EntityItem(player.worldObj, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
				entityitem.setPickupDelay(10);
				player.worldObj.spawnEntityInWorld(entityitem);
			}

			player.worldObj.setBlockState(pos, Blocks.air.getDefaultState());
			player.addStat(StatList.func_188055_a(blockState.getBlock()));
			player.addExhaustion(0.01F);
		}

		return true;
	}

	private void boneMealBlock(ItemStack ist, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side) {
		ItemStack fakeItemStack = new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META);
		ItemDye fakeItemDye = (ItemDye) fakeItemStack.getItem();

		boolean usedRod = false;
		for(int repeatedUses = 0; repeatedUses <= getLuckRolls(); repeatedUses++) {
			if(repeatedUses == 0 || world.rand.nextInt(100) <= getLuckPercent()) {
				if(fakeItemDye.onItemUse(fakeItemStack, player, world, pos, EnumHand.MAIN_HAND, side, 0, 0, 0) == EnumActionResult.SUCCESS) {
					if(!usedRod)
						usedRod = true;
					player.worldObj.playSound(null, player.getPosition(), SoundEvents.entity_experience_orb_touch, SoundCategory.NEUTRAL, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
				}
			}
		}

		if(usedRod && !player.capabilities.isCreativeMode)
			setBoneMealCount(ist, getBoneMealCount(ist) - getBonemealCost(), hand, player);
	}

	public int getBoneMealCount(ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return 0;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.getTotalAmount(HarvestRodItemStackHandler.BONEMEAL_SLOT);
	}

	public void setBoneMealCount(ItemStack ist, int boneMealCount) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		filteredHandler.setTotalAmount(0, boneMealCount);

	}

	private boolean incrementPlantable(ItemStack harvestRod, ItemStack plantable, int slotNumber, EntityPlayer player) {
		IItemHandler itemHandler = harvestRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return false;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		ItemStack plantableCopy = plantable.copy();
		plantableCopy.stackSize = 1;

		for(int slot = 2; slot < filteredHandler.getSlots(); slot++) {
			ItemStack remainingStack = filteredHandler.insertItem(slot, plantableCopy, false);
			if(remainingStack == null || remainingStack.stackSize == 0) {
				PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(harvestRod)), (EntityPlayerMP) player);
				return true;
			}
		}
		return false;
	}

	private void setBoneMealCount(ItemStack ist, int boneMealCount, int slotNumber, EntityPlayer player) {
		setBoneMealCount(ist, boneMealCount);

		PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
	}

	private void setBoneMealCount(ItemStack ist, int boneMealCount, EnumHand hand, EntityPlayer player) {
		setBoneMealCount(ist, boneMealCount);

		PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(hand, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
	}

	private void decrementPlantable(ItemStack harvestRod, int parentSlot, EnumHand hand, EntityPlayer player) {
		IItemHandler itemHandler = harvestRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		filteredHandler.setTotalAmount(parentSlot, filteredHandler.getTotalAmount(parentSlot) - 1);

		PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(hand, getItemHandlerNBT(harvestRod)), (EntityPlayerMP) player);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking())
			return super.onItemRightClick(stack, world, player, hand);

		player.setActiveHand(hand);

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 300;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack harvestRod, World world, EntityLivingBase entity, int timeLeft) {
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		RayTraceResult result = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);

		if(result != null) {
			IHarvestRodCache cache = harvestRod.getCapability(ModCapabilities.HARVEST_ROD_CACHE, null);
			if(cache != null) {
				cache.reset();
			}
			BlockPos pos = result.getBlockPos();

			if(getMode(harvestRod) == BONE_MEAL_MODE) {
				if(getBoneMealCount(harvestRod) >= getBonemealCost() || player.capabilities.isCreativeMode) {
					boneMealBlock(harvestRod, player, player.getActiveHand(), world, pos, EnumFacing.UP);
				}
			} else if(getMode(harvestRod) == PLANTABLE_MODE) {
				if(getPlantableQuantity(harvestRod, getCurrentPlantableSlot(harvestRod)) > 0 || player.capabilities.isCreativeMode) {
					plantItem(harvestRod, player, pos, player.getActiveHand());
				}
			}

		} else {
			removeStackFromCurrent(harvestRod, player, player.getActiveHand());
		}
	}

	private void removeStackFromCurrent(ItemStack stack, EntityPlayer player, EnumHand hand) {
		if(getMode(stack).equals(BONE_MEAL_MODE)) {
			ItemStack boneMealStack = new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META);
			int numberToAdd = Math.min(boneMealStack.getMaxStackSize(), getBoneMealCount(stack));
			int numberAdded = InventoryHelper.tryToAddToInventory(boneMealStack, player.inventory, 0, numberToAdd);
			setBoneMealCount(stack, getBoneMealCount(stack) - numberAdded, hand, player);
		} else if(getMode(stack).equals(PLANTABLE_MODE)) {
			byte plantableSlot = getCurrentPlantableSlot(stack);
			ItemStack plantableStack = getCurrentPlantable(stack);
			int plantableQuantity = getPlantableQuantity(stack, plantableSlot);

			int numberToAdd = Math.min(plantableStack.getMaxStackSize(), plantableQuantity);
			int numberAdded = InventoryHelper.tryToAddToInventory(plantableStack, player.inventory, 0, numberToAdd);

			int updatedPlantableQuantity = getPlantableQuantity(stack, plantableSlot) - numberAdded;

			setPlantableQuantity(stack, plantableSlot, updatedPlantableQuantity, hand, player);
			if(updatedPlantableQuantity == 0)
				shiftModeOnEmptyPlantable(stack, plantableSlot);
		}
	}

	public void shiftModeOnEmptyPlantable(ItemStack harvestRod, byte plantableSlot) {
		if(plantableSlot > 0)
			setCurrentPlantableSlot(harvestRod, (byte) (plantableSlot - 1));
		cycleMode(harvestRod);
	}

	private void plantItem(ItemStack harvestRod, EntityPlayer player, BlockPos pos, EnumHand hand) {
		byte plantableSlot = getCurrentPlantableSlot(harvestRod);
		ItemStack fakePlantableStack = getCurrentPlantable(harvestRod).copy();
		fakePlantableStack.stackSize = 1;

		if(fakePlantableStack.onItemUse(player, player.worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS) {
			player.worldObj.playSound(null, player.getPosition(), SoundEvents.entity_experience_orb_pickup, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));

			if(!player.capabilities.isCreativeMode) {
				int plantableQuantity = getPlantableQuantity(harvestRod, plantableSlot);
				decrementPlantable(harvestRod, plantableSlot, hand, player);
				if(plantableQuantity <= 1)
					shiftModeOnEmptyPlantable(harvestRod, plantableSlot);
			}
		}
	}

	public ItemStack getCurrentPlantable(ItemStack harvestRod) {
		int currentSlot = getCurrentPlantableSlot(harvestRod);

		return getPlantableInSlot(harvestRod, currentSlot);
	}

	public ItemStack getPlantableInSlot(ItemStack harvestRod, int currentSlot) {
		IItemHandler itemHandler = harvestRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return null;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.getStackInParentSlot(currentSlot);
	}

	@Override
	public void onUsingTick(ItemStack harvestRod, EntityLivingBase entity, int count) {
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		if(isCoolDownOver(harvestRod, count)) {
			RayTraceResult result = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);
			if(result != null) {
				World world = player.worldObj;
				IHarvestRodCache cache = harvestRod.getCapability(ModCapabilities.HARVEST_ROD_CACHE, null);

				if(cache != null) {
					switch(getMode(harvestRod)) {
						case BONE_MEAL_MODE:
							if(getBoneMealCount(harvestRod) >= getBonemealCost() || player.capabilities.isCreativeMode) {
								BlockPos blockToBoneMeal = getNextBlockToBoneMeal(world, cache, result.getBlockPos(), Settings.HarvestRod.AOERadius);

								if(blockToBoneMeal != null) {
									boneMealBlock(harvestRod, player, player.getActiveHand(), world, blockToBoneMeal, EnumFacing.UP);
									return;
								}
							}
							break;
						case PLANTABLE_MODE:
							if(getPlantableQuantity(harvestRod, getCurrentPlantableSlot(harvestRod)) >= 1 || player.capabilities.isCreativeMode) {
								BlockPos blockToPlantOn = getNextBlockToPlantOn(world, cache, result.getBlockPos(), Settings.HarvestRod.AOERadius, (IPlantable) getCurrentPlantable(harvestRod).getItem());

								if(blockToPlantOn != null) {
									plantItem(harvestRod, player, blockToPlantOn, player.getActiveHand());
									return;
								}
							}
							break;
						case HOE_MODE:
							ItemStack fakeHoe = new ItemStack(Items.wooden_hoe);
							BlockPos blockToHoe = getNextBlockToHoe(world, cache, result.getBlockPos(), Settings.HarvestRod.AOERadius);
							if(blockToHoe != null) {
								if(Items.wooden_hoe.onItemUse(fakeHoe, player, world, blockToHoe, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS)
									((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new SPacketSoundEffect(SoundEvents.item_hoe_till, SoundCategory.BLOCKS, blockToHoe.getX(), blockToHoe.getY(), blockToHoe.getZ(), 1.0F, 1.0F));
								return;
							}
						default:
							break;
					}
				}
			}
		}
	}

	private BlockPos getNextBlockToHoe(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		if(cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos()))
			fillQueueToHoe(world, cache, pos, range);

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToHoe(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					Block block = blockState.getBlock();

					if(world.isAirBlock(currentPos.up())) {
						if(block == Blocks.grass || (block == Blocks.dirt && (blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT || blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.COARSE_DIRT))) {
							cache.addBlockToQueue(currentPos);
						}
					}
				}
			}
		}

	}

	private BlockPos getNextBlockToPlantOn(World world, IHarvestRodCache cache, BlockPos pos, int range, IPlantable plantable) {
		if(cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos()))
			fillQueueToPlant(world, cache, pos, range, plantable);

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToPlant(World world, IHarvestRodCache cache, BlockPos pos, int range, IPlantable plantable) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();

		boolean checkerboard = false;
		boolean bothOddOrEven = false;

		if(plantable == Items.pumpkin_seeds || plantable == Items.melon_seeds) {
			checkerboard = true;
			boolean xEven = pos.getX() % 2 == 0;
			boolean zEven = pos.getZ() % 2 == 0;
			bothOddOrEven = xEven == zEven;
		}

		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					if((!checkerboard || (bothOddOrEven == ((currentPos.getX() % 2 == 0) == (currentPos.getZ() % 2 == 0)))) &&
							blockState.getBlock().canSustainPlant(blockState, world, pos, EnumFacing.UP, plantable) && world.isAirBlock(currentPos.up())) {
						cache.addBlockToQueue(currentPos);
					}
				}
			}
		}
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if(entityLiving.worldObj.isRemote)
			return false;
		if(entityLiving.isSneaking()) {
			cycleMode(stack);
			return true;
		}
		return false;
	}

	private boolean isCoolDownOver(ItemStack stack, int count) {
		return getMaxItemUseDuration(stack) - count >= AOE_START_COOLDOWN && (getMaxItemUseDuration(stack) - count) % Settings.HarvestRod.AOECooldown == 0;
	}

	private BlockPos getNextBlockToBoneMeal(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		if(cache.isQueueEmpty() || !pos.equals(cache.getStartBlockPos()))
			fillQueueToBoneMeal(world, cache, pos, range);

		return cache.getNextBlockInQueue();
	}

	private void fillQueueToBoneMeal(World world, IHarvestRodCache cache, BlockPos pos, int range) {
		cache.setStartBlockPos(pos);
		cache.clearBlockQueue();
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					if(blockState.getBlock() instanceof IGrowable && ((IGrowable) blockState.getBlock()).canGrow(world, currentPos, blockState, world.isRemote)) {
						cache.addBlockToQueue(currentPos);
					}
				}
			}
		}
	}

	private void cycleMode(ItemStack harvestRod) {
		String currentMode = getMode(harvestRod);
		int plantableCount = getCountPlantable(harvestRod);
		switch(currentMode) {
			case BONE_MEAL_MODE:
				if(plantableCount > 0) {
					setMode(harvestRod, PLANTABLE_MODE);
					setCurrentPlantableSlot(harvestRod, (byte) 1);
				} else {
					setMode(harvestRod, HOE_MODE);
				}
				break;
			case PLANTABLE_MODE:
				if(plantableCount > getCurrentPlantableSlot(harvestRod)) {
					setCurrentPlantableSlot(harvestRod, (byte) (getCurrentPlantableSlot(harvestRod) + 1));
				} else {
					setMode(harvestRod, HOE_MODE);
				}
				break;
			case HOE_MODE:
				setMode(harvestRod, BONE_MEAL_MODE);
				break;
			default:
				break;
		}
	}

	public int getCountPlantable(ItemStack harvestRod) {
		IItemHandler itemHandler = harvestRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return 0;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return ((filteredHandler.getSlots() / FilteredItemStackHandler.SLOTS_PER_TYPE) - 2); //one stack is bonemeal and the other null stack for inserts
	}

	public byte getCurrentPlantableSlot(ItemStack stack) {
		return stack.getTagCompound().getByte(PLANTABLE_INDEX_NBT_TAG);
	}

	private void setCurrentPlantableSlot(ItemStack stack, byte index) {
		stack.getTagCompound().setByte(PLANTABLE_INDEX_NBT_TAG, index);
	}

	private void setMode(ItemStack stack, String mode) {
		NBTHelper.setString(MODE_NBT_TAG, stack, mode);
	}

	public String getMode(ItemStack stack) {
		String mode = NBTHelper.getString(MODE_NBT_TAG, stack);

		return mode.equals("") ? BONE_MEAL_MODE : mode;
	}

	public int getPlantableQuantity(ItemStack harvestRod, int parentSlot) {
		IItemHandler itemHandler = harvestRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return 0;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.getTotalAmount(parentSlot);
	}

	public void setPlantableQuantity(ItemStack harvestRod, byte plantableSlot, int quantity) {
		setPlantableQuantity(harvestRod, plantableSlot, quantity, null, null);
	}

	public void setPlantableQuantity(ItemStack harvestRod, byte plantableSlot, int quantity, EnumHand hand, EntityPlayer player) {
		IItemHandler itemHandler = harvestRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		filteredHandler.setTotalAmount(plantableSlot, quantity);

		if(quantity != 0 && player != null)
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(hand, getItemHandlerNBT(harvestRod)), (EntityPlayerMP) player);
	}
}