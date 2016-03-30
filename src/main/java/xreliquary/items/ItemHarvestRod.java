package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockFertileLilypad;
import xreliquary.items.util.HarvestRodPlayerProps;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.StackHelper;

import java.util.ArrayList;
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
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.WHITE + Items.dye.getItemStackDisplayName(new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META))), ist, list);
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
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
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
					setBoneMealCount(ist, getBoneMealCount(ist) + getBonemealWorth());
				}
			}

			consumePlantables(ist, player);
		}
	}

	private void consumePlantables(ItemStack harvestRod, EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			ItemStack currentStack = player.inventory.mainInventory[slot];
			if(currentStack == null) {
				continue;
			}
			if(currentStack.getItem() instanceof IPlantable && addPlantableToInventory(currentStack, harvestRod, player)) {
				break;
			}
		}
	}

	private boolean addPlantableToInventory(ItemStack stack, ItemStack harvestRod, EntityPlayer player) {
		NBTTagList itemsList = harvestRod.getTagCompound().getTagList("Items", 10);
		boolean addedToExistingStack = false;
		for(byte i = 0; i < itemsList.tagCount(); ++i) {
			NBTTagCompound item = itemsList.getCompoundTagAt(i);
			ItemStack currentStack = ItemStack.loadItemStackFromNBT(item);
			if(StackHelper.isItemAndNbtEqual(stack, currentStack)) {
				int itemQuantity = getPlantableQuantity(harvestRod, i);
				if(itemQuantity < Settings.HarvestRod.maxCapacityPerPlantable && InventoryHelper.consumeItem(stack, player, 0, 1)) {
					setPlantableQuantity(harvestRod, i, ++itemQuantity);
					itemsList.set(i, currentStack.serializeNBT());

					addedToExistingStack = true;
				} else {
					return false;
				}
			}
		}
		if(!addedToExistingStack && InventoryHelper.consumeItem(stack, player, 0, 1)) {
			ItemStack newStack = stack.copy();
			setPlantableQuantity(harvestRod, (byte) itemsList.tagCount(), 1);
			itemsList.appendTag(newStack.serializeNBT());
		} else {
			return false;
		}

		harvestRod.getTagCompound().setTag("Items", itemsList);

		return true;
	}

	private void removePlantableFromInventory(ItemStack harvestRod, byte idx) {
		NBTTagCompound tagCompound = harvestRod.getTagCompound();

		NBTTagList itemsList = tagCompound.getTagList("Items", 10);
		itemsList.removeTag(idx);
		harvestRod.getTagCompound().setTag("Items", itemsList);

		NBTTagList quantities = tagCompound.getTagList(PLANTABLE_QUANTITIES_NBT_TAG, 3);
		quantities.removeTag(idx);
		harvestRod.getTagCompound().setTag(PLANTABLE_QUANTITIES_NBT_TAG, quantities);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack ist, BlockPos pos, EntityPlayer player) {
		if(player.worldObj.isRemote)
			return false;

		boolean brokenBlock = false;

		Block block = player.worldObj.getBlockState(pos).getBlock();
		if(block instanceof IPlantable || block instanceof IGrowable) {
			for(int xOff = -getBreakRadius(); xOff <= getBreakRadius(); xOff++) {
				for(int yOff = -getBreakRadius(); yOff <= getBreakRadius(); yOff++) {
					for(int zOff = -getBreakRadius(); zOff <= getBreakRadius(); zOff++) {
						doHarvestBlockBreak(ist, pos, player, xOff, yOff, zOff);
						brokenBlock = true;
					}
				}
			}
		}

		return brokenBlock;
	}

	public void doHarvestBlockBreak(ItemStack ist, BlockPos pos, EntityPlayer player, int xOff, int yOff, int zOff) {
		pos = pos.add(xOff, yOff, zOff);

		IBlockState blockState = player.worldObj.getBlockState(pos);

		if(!(blockState.getBlock() instanceof IPlantable) && !(blockState.getBlock() instanceof BlockCrops))
			return;
		if(blockState.getBlock() instanceof BlockFertileLilypad)
			return;

		List<ItemStack> drops = blockState.getBlock().getDrops(player.worldObj, pos, blockState, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, ist));
		Random rand = new Random();

		if(player.worldObj.isRemote) {
			for(int particles = 0; particles <= 8; particles++)
				player.worldObj.playAuxSFXAtEntity(player, 2001, pos, Block.getStateId(blockState));
		} else {
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
			player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(blockState.getBlock())], 1);
			player.addExhaustion(0.01F);
		}
	}

	private void boneMealBlock(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		boneMealBlock(ist, player, world, pos, side, true);
	}

	private boolean boneMealBlock(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side, boolean updateCharge) {
		ItemStack fakeItemStack = new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META);
		ItemDye fakeItemDye = (ItemDye) fakeItemStack.getItem();

		boolean usedRod = false;
		for(int repeatedUses = 0; repeatedUses <= getLuckRolls(); repeatedUses++) {
			if(repeatedUses == 0 || world.rand.nextInt(100) <= getLuckPercent()) {
				if(fakeItemDye.onItemUse(fakeItemStack, player, world, pos, side, 0, 0, 0)) {
					if(!usedRod)
						usedRod = true;
					player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
				}
			}
		}

		if(updateCharge && usedRod && !player.capabilities.isCreativeMode)
			setBoneMealCount(ist, getBoneMealCount(ist) - getBonemealCost());

		return usedRod;
	}

	public int getBoneMealCount(ItemStack ist) {
		return NBTHelper.getInteger("bonemeal", ist);
	}

	private void setBoneMealCount(ItemStack ist, int boneMealCount) {
		NBTHelper.setInteger("bonemeal", ist, boneMealCount);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(player.isSneaking())
			return super.onItemRightClick(stack, world, player);

		setupHarvestRodPlayerProps(player);

		player.setItemInUse(stack, getMaxItemUseDuration(stack));

		return stack;
	}

	private void setupHarvestRodPlayerProps(EntityPlayer player) {
		if(HarvestRodPlayerProps.get(player) == null)
			HarvestRodPlayerProps.register(player);

	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 300;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
		if(player.worldObj.isRemote)
			return;

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);

		if(mop != null) {
			HarvestRodPlayerProps props = HarvestRodPlayerProps.get(player);

			if(!player.capabilities.isCreativeMode && props.getTimesUsed() > 0) {
				if(getMode(stack).equals(BONE_MEAL_MODE))
					setBoneMealCount(stack, getBoneMealCount(stack) - props.getTimesUsed());
				else if(getMode(stack).equals(PLANTABLE_MODE)) {
					setPlantableQuantity(stack, getCurrentPlantableIndex(stack), getPlantableQuantity(stack, getCurrentPlantableIndex(stack)) - props.getTimesUsed());
				}
			}

			BlockPos pos = mop.getBlockPos();

			if(getMode(stack).equals(BONE_MEAL_MODE)) {
				if(getBoneMealCount(stack) >= getBonemealCost() || player.capabilities.isCreativeMode) {
					boneMealBlock(stack, player, world, pos, EnumFacing.UP);
				}
			} else if(getMode(stack).equals(PLANTABLE_MODE)) {
				if(getPlantableQuantity(stack, getCurrentPlantableIndex(stack)) > 0 || player.capabilities.isCreativeMode) {
					plantItem(stack, player, pos);
				}
			}
			//clear the cached queue values
			props.reset();
		} else {
			removeStackFromCurrent(stack, player);
		}
	}

	private void removeStackFromCurrent(ItemStack stack, EntityPlayer player) {
		if(getMode(stack).equals(BONE_MEAL_MODE)) {
			ItemStack boneMealStack = new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META);
			int numberToAdd = Math.min(boneMealStack.getMaxStackSize(), getBoneMealCount(stack));
			int numberAdded = InventoryHelper.tryToAddToInventory(boneMealStack, player.inventory, 0, numberToAdd);
			setBoneMealCount(stack, getBoneMealCount(stack) - numberAdded);
		} else if(getMode(stack).equals(PLANTABLE_MODE)) {
			byte idx = getCurrentPlantableIndex(stack);
			ItemStack plantableStack = getPlantableItems(stack).get(idx);
			int numberToAdd = Math.min(plantableStack.getMaxStackSize(), getPlantableQuantity(stack, idx));
			int numberAdded = InventoryHelper.tryToAddToInventory(plantableStack, player.inventory, 0, numberToAdd);
			setPlantableQuantity(stack, idx, getPlantableQuantity(stack, idx) - numberAdded);
			if(getPlantableQuantity(stack, idx) <= 0) {
				removePlantableFromInventory(stack, idx);
				if(getPlantableItems(stack).size() > idx)
					setCurrentPlantableIndex(stack, (byte) (idx - 1));
				cycleMode(stack);
			}

		}
	}

	private boolean plantItem(ItemStack stack, EntityPlayer player, BlockPos pos) {
		return plantItem(stack, player, pos, true);
	}

	private boolean plantItem(ItemStack stack, EntityPlayer player, BlockPos pos, boolean updateCharge) {
		byte idx = getCurrentPlantableIndex(stack);
		ItemStack fakePlantableStack = getPlantableItems(stack).get(idx).copy();
		fakePlantableStack.stackSize = 1;

		if(fakePlantableStack.onItemUse(player, player.worldObj, pos, EnumFacing.UP, 0, 0, 0)) {
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));

			if(updateCharge && !player.capabilities.isCreativeMode) {
				setPlantableQuantity(stack, idx, getPlantableQuantity(stack, idx) - 1);
			}

			return true;
		}

		return false;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		if(player.worldObj.isRemote)
			return;

		if(isCoolDownOver(stack, count)) {
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);
			if(mop != null) {
				World world = player.worldObj;
				HarvestRodPlayerProps props = HarvestRodPlayerProps.get(player);

				switch(getMode(stack)) {
					case BONE_MEAL_MODE:
						if(getBoneMealCount(stack) >= (getBonemealCost() * props.getTimesUsed() + 1) || player.capabilities.isCreativeMode) {
							BlockPos blockToBoneMeal = getNextBlockToBoneMeal(world, props, mop.getBlockPos(), Settings.HarvestRod.AOERadius);

							if(blockToBoneMeal != null) {
								if(boneMealBlock(stack, player, world, blockToBoneMeal, EnumFacing.UP, false)) {
									props.incrementTimesUsed();
								}
								return;
							}
						}
						break;
					case PLANTABLE_MODE:
						if(getPlantableQuantity(stack, getCurrentPlantableIndex(stack)) >= props.getTimesUsed() + 1 || player.capabilities.isCreativeMode) {
							BlockPos blockToPlantOn = getNextBlockToPlantOn(world, props, mop.getBlockPos(), Settings.HarvestRod.AOERadius, (IPlantable) getPlantableItems(stack).get(getCurrentPlantableIndex(stack)).getItem());

							if(blockToPlantOn != null) {
								if(plantItem(stack, player, blockToPlantOn, false)) {
									props.incrementTimesUsed();
								}
								return;
							}
						}
						break;
					case HOE_MODE:
						ItemStack fakeHoe = new ItemStack(Items.wooden_hoe);
						BlockPos blockToHoe = getNextBlockToHoe(world, props, mop.getBlockPos(), Settings.HarvestRod.AOERadius);
						if(blockToHoe != null) {
							Items.wooden_hoe.onItemUse(fakeHoe, player, world, blockToHoe, EnumFacing.UP, 0, 0, 0);
							return;
						}
					default:
						break;
				}
			}
		}
	}

	private BlockPos getNextBlockToHoe(World world, HarvestRodPlayerProps props, BlockPos pos, int range) {
		if(props.isQueueEmpty() || !pos.equals(props.getStartBlockPos()))
			fillQueueToHoe(world, props, pos, range);

		return props.getNextBlockInQueue();
	}

	private void fillQueueToHoe(World world, HarvestRodPlayerProps props, BlockPos pos, int range) {
		props.setStartBlockPos(pos);
		props.clearBlockQueue();
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);

					IBlockState blockState = world.getBlockState(currentPos);
					Block block = blockState.getBlock();

					if(world.isAirBlock(currentPos.up())) {
						if(block == Blocks.grass || (block == Blocks.dirt && (blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT || blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.COARSE_DIRT))) {
							props.addBlockToQueue(currentPos);
						}
					}
				}
			}
		}

	}

	private BlockPos getNextBlockToPlantOn(World world, HarvestRodPlayerProps props, BlockPos pos, int range, IPlantable plantable) {
		if(props.isQueueEmpty() || !pos.equals(props.getStartBlockPos()))
			fillQueueToPlant(world, props, pos, range, plantable);

		return props.getNextBlockInQueue();
	}

	private void fillQueueToPlant(World world, HarvestRodPlayerProps props, BlockPos pos, int range, IPlantable plantable) {
		props.setStartBlockPos(pos);
		props.clearBlockQueue();

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
							blockState.getBlock().canSustainPlant(world, pos, EnumFacing.UP, plantable) && world.isAirBlock(currentPos.up())) {
						props.addBlockToQueue(currentPos);
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

	private BlockPos getNextBlockToBoneMeal(World world, HarvestRodPlayerProps props, BlockPos pos, int range) {
		if(props.isQueueEmpty() || !pos.equals(props.getStartBlockPos()))
			fillQueueToBoneMeal(world, props, pos, range);

		return props.getNextBlockInQueue();
	}

	private void fillQueueToBoneMeal(World world, HarvestRodPlayerProps props, BlockPos pos, int range) {
		props.setStartBlockPos(pos);
		props.clearBlockQueue();
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					if(blockState.getBlock() instanceof IGrowable && ((IGrowable) blockState.getBlock()).canGrow(world, currentPos, blockState, world.isRemote)) {
						props.addBlockToQueue(currentPos);
					}
				}
			}
		}
	}

	private void cycleMode(ItemStack stack) {
		String currentMode = getMode(stack);
		List<ItemStack> items = getPlantableItems(stack);
		switch(currentMode) {
			case BONE_MEAL_MODE:
				if(items.size() > 0) {
					setMode(stack, PLANTABLE_MODE);
					setCurrentPlantableIndex(stack, (byte) 0);
				} else {
					setMode(stack, HOE_MODE);
				}
				break;
			case PLANTABLE_MODE:
				if(items.size() > getCurrentPlantableIndex(stack) + 1) {
					setCurrentPlantableIndex(stack, (byte) (getCurrentPlantableIndex(stack) + 1));
				} else {
					setMode(stack, HOE_MODE);
				}
				break;
			case HOE_MODE:
				setMode(stack, BONE_MEAL_MODE);
				break;
			default:
				break;
		}
	}

	public byte getCurrentPlantableIndex(ItemStack stack) {
		return stack.getTagCompound().getByte(PLANTABLE_INDEX_NBT_TAG);
	}

	private void setCurrentPlantableIndex(ItemStack stack, byte index) {
		stack.getTagCompound().setByte(PLANTABLE_INDEX_NBT_TAG, index);
	}

	private void setMode(ItemStack stack, String mode) {
		NBTHelper.setString(MODE_NBT_TAG, stack, mode);
	}

	public String getMode(ItemStack stack) {
		String mode = NBTHelper.getString(MODE_NBT_TAG, stack);

		return mode.equals("") ? BONE_MEAL_MODE : mode;
	}

	public List<ItemStack> getPlantableItems(ItemStack stack) {
		NBTTagList itemsList = stack.getTagCompound().getTagList("Items", 10);
		ArrayList<ItemStack> items = new ArrayList<>();

		for(int i = 0; i < itemsList.tagCount(); ++i) {
			NBTTagCompound item = itemsList.getCompoundTagAt(i);
			byte slotIndex = item.getByte("Slot");
			if(slotIndex >= 0) {
				items.add(ItemStack.loadItemStackFromNBT(item));
			}
		}
		return items;
	}

	public int getPlantableQuantity(ItemStack stack, byte itemIndex) {
		NBTTagList plantableQuantities = stack.getTagCompound().getTagList(PLANTABLE_QUANTITIES_NBT_TAG, 3);

		if(plantableQuantities.hasNoTags() || plantableQuantities.tagCount() <= itemIndex)
			return 0;

		return ((NBTTagInt) plantableQuantities.get(itemIndex)).getInt();
	}

	private void setPlantableQuantity(ItemStack stack, byte itemIndex, int quantity) {
		NBTTagList plantableQuantities = stack.getTagCompound().getTagList(PLANTABLE_QUANTITIES_NBT_TAG, 3);

		byte currentSize = (byte) plantableQuantities.tagCount();

		while(currentSize <= itemIndex) {
			plantableQuantities.appendTag(new NBTTagInt(0));

			currentSize++;
		}

		plantableQuantities.set(itemIndex, new NBTTagInt(quantity));

		stack.getTagCompound().setTag(PLANTABLE_QUANTITIES_NBT_TAG, plantableQuantities);
	}

}