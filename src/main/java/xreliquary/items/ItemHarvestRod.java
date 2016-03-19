package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
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
import net.minecraft.nbt.NBTTagLong;
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
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.StackHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemHarvestRod extends ItemToggleable {

	public static final String CROP_LOCATIONS_NBT_TAG = "crop_locations";
	public static final String COOLDOWN_NBT_TAG = "cooldown";
	public static final String AOE_INITIAL_BLOCK_NBT_TAG = "aoe_initial_block";
	public static final String BONE_MEAL_MODE = "bone_meal";
	public static final String PLANTABLE_MODE = "plantable";
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
		return Settings.HarvestRod.harvestBreakRadius;
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
			if(currentStack.getItem() instanceof IPlantable && addItemToInventory(currentStack, harvestRod, player)) {
				break;
			}
		}
	}

	private boolean addItemToInventory(ItemStack stack, ItemStack harvestRod, EntityPlayer player) {
		NBTTagList itemsList = harvestRod.getTagCompound().getTagList("Items", 10);
		boolean addedToExistingStack = false;
		for(byte i = 0; i < itemsList.tagCount(); ++i) {
			NBTTagCompound item = itemsList.getCompoundTagAt(i);
			ItemStack currentStack = ItemStack.loadItemStackFromNBT(item);
			if(StackHelper.isItemAndNbtEqual(stack, currentStack)) {
				int itemQuantity = getItemQuantity(harvestRod, i);
				if(itemQuantity < Settings.HarvestRod.maxCapacityPerPlantable && InventoryHelper.consumeItem(stack, player, 0, 1)) {
					setItemQuantity(harvestRod, i, ++itemQuantity);
					itemsList.set(i, currentStack.serializeNBT());

					addedToExistingStack = true;
				} else {
					return false;
				}
			}
		}
		if(!addedToExistingStack && InventoryHelper.consumeItem(stack, player, 0, 1)) {
			ItemStack newStack = stack.copy();
			setItemQuantity(harvestRod, (byte) itemsList.tagCount(), 1);
			itemsList.appendTag(newStack.serializeNBT());
		} else {
			return false;
		}

		harvestRod.getTagCompound().setTag("Items", itemsList);

		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack ist, BlockPos pos, EntityPlayer player) {
		if(player.worldObj.isRemote)
			return false;

		Block block = player.worldObj.getBlockState(pos).getBlock();
		if(block instanceof IPlantable || block instanceof IGrowable) {
			for(int xOff = -getBreakRadius(); xOff <= getBreakRadius(); xOff++) {
				for(int yOff = -getBreakRadius(); yOff <= getBreakRadius(); yOff++) {
					for(int zOff = -getBreakRadius(); zOff <= getBreakRadius(); zOff++) {
						doHarvestBlockBreak(ist, pos, player, xOff, yOff, zOff);
					}
				}
			}
		}

		return true;
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

	private void boneMealBlock(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side, boolean updateCharge) {
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

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);
		if(mop != null) {
			player.setItemInUse(stack, getMaxItemUseDuration(stack));
		}
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 300;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
		if(player.worldObj.isRemote)
			return;

		if(!player.capabilities.isCreativeMode)
			setBoneMealCount(stack, Math.max(0, getBoneMealCount(stack) - getBonemealCost() * getTimesBoneMealUsed(stack, timeLeft)));

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);

		if(mop != null) {
			BlockPos pos = mop.getBlockPos();
			if(getBoneMealCount(stack) >= getBonemealCost() || player.capabilities.isCreativeMode) {
				boneMealBlock(stack, player, world, pos, EnumFacing.UP);
				return;
			}
		}

		return;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		if(player.worldObj.isRemote)
			return;

		if(isCoolDownOver(stack, count)) {
			if(getBoneMealCount(stack) >= (getBonemealCost() * getTimesBoneMealUsed(stack, count)) || player.capabilities.isCreativeMode) {
				MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);
				if(mop != null) {
					World world = player.worldObj;

					BlockPos blockToBoneMeal = getNextBlockToBoneMeal(world, mop.getBlockPos(), Settings.HarvestRod.harvestBreakRadius);

					if(blockToBoneMeal != null)
						boneMealBlock(stack, player, world, blockToBoneMeal, EnumFacing.UP, false);
				}
			} else {
				player.stopUsingItem();
			}
		}
	}

	public int getTimesBoneMealUsed(ItemStack stack, int count) {
		if(getMaxItemUseDuration(stack) - count < AOE_START_COOLDOWN)
			return 0;
		return (getMaxItemUseDuration(stack) - (count + AOE_START_COOLDOWN)) / Settings.HarvestRod.bonemealAOECooldown;
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
		return getMaxItemUseDuration(stack) - count >= AOE_START_COOLDOWN && (getMaxItemUseDuration(stack) - count) % Settings.HarvestRod.bonemealAOECooldown == 0;
	}

	private BlockPos getNextBlockToBoneMeal(World world, BlockPos pos, int range) {
		for(int x = pos.getX() - range; x <= pos.getX() + range; x++) {
			for(int y = pos.getY() - range; y <= pos.getY() + range; y++) {
				for(int z = pos.getZ() - range; z <= pos.getZ() + range; z++) {
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentPos);
					if(blockState.getBlock() instanceof IGrowable && ((IGrowable) blockState.getBlock()).canGrow(world, currentPos, blockState, world.isRemote)) {
						return currentPos;
					}
				}
			}
		}
		return null;
	}

	private void cycleMode(ItemStack stack) {
		String currentMode = getMode(stack);
		List<ItemStack> items = getInventoryItems(stack);
		switch(currentMode) {
			case BONE_MEAL_MODE:
				if(items.size() > 0) {
					setMode(stack, PLANTABLE_MODE);
					setCurrentPlantableIndex(stack, (byte) 0);
				}
				break;
			case PLANTABLE_MODE:
				if(items.size() > getCurrentPlantableIndex(stack) + 1) {
					setCurrentPlantableIndex(stack, (byte) (getCurrentPlantableIndex(stack) + 1));
				} else {
					setMode(stack, BONE_MEAL_MODE);
				}
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

	public List<ItemStack> getInventoryItems(ItemStack stack) {
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

	public int getItemQuantity(ItemStack stack, byte itemIndex) {
		NBTTagList plantableQuantities = stack.getTagCompound().getTagList(PLANTABLE_QUANTITIES_NBT_TAG, 3);

		if(plantableQuantities.hasNoTags() || plantableQuantities.tagCount() <= itemIndex)
			return 0;

		return ((NBTTagInt) plantableQuantities.get(itemIndex)).getInt();
	}

	private void setItemQuantity(ItemStack stack, byte itemIndex, int quantity) {
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