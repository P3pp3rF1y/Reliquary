package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSojournerStaff extends ItemToggleable {

	public ItemSojournerStaff() {
		super(Names.Items.SOJOURNER_STAFF);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
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
			scanForMatchingTorchesToFillInternalStorage(ist, player);
		}
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
		if(entityLiving.world.isRemote)
			return false;
		if(entityLiving.isSneaking()) {
			cycleTorchMode(ist);
			return true;
		}
		return false;
	}

	private void scanForMatchingTorchesToFillInternalStorage(ItemStack ist, EntityPlayer player) {
		for(String torch : Settings.Items.SojournerStaff.torches) {
			if(!isInternalStorageFullOfItem(ist, torch) && InventoryHelper.consumeItem(is -> is.getItem().getRegistryName() != null && is.getItem().getRegistryName().toString().equals(torch), player)) {
				addItemToInternalStorage(ist, torch);
			}
		}
	}

	private void addItemToInternalStorage(ItemStack ist, String itemRegistryName) {
		NBTTagCompound tagCompound = NBTHelper.getTag(ist);
		if(tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}

		NBTTagList tagList = tagCompound.getTagList("Items", 10);

		boolean added = false;
		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			String itemName = tagItemData.getString("Name");
			if(itemName.equals(itemRegistryName)) {
				int quantity = tagItemData.getInteger("Quantity");
				tagItemData.setInteger("Quantity", quantity + 1);
				added = true;
			}
		}
		if(!added) {
			NBTTagCompound newTagData = new NBTTagCompound();
			newTagData.setString("Name", itemRegistryName);
			newTagData.setInteger("Quantity", 1);
			tagList.appendTag(newTagData);
		}

		tagCompound.setTag("Items", tagList);

		NBTHelper.setTag(ist, tagCompound);
	}

	private static boolean hasItemInInternalStorage(ItemStack ist, String itemRegistryName, int cost) {
		NBTTagCompound tagCompound = NBTHelper.getTag(ist);
		if(tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}
		if(tagCompound.hasNoTags()) {
			tagCompound.setTag("Items", new NBTTagList());
			return false;
		}

		NBTTagList tagList = tagCompound.getTagList("Items", 10);
		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			String itemName = tagItemData.getString("Name");
			if(itemName.equals(itemRegistryName)) {
				int quantity = tagItemData.getInteger("Quantity");
				return quantity >= cost;
			}
		}

		return false;
	}

	private boolean isInternalStorageFullOfItem(ItemStack ist, String itemRegistryName) {
		if(hasItemInInternalStorage(ist, itemRegistryName, 1)) {
			NBTTagCompound tagCompound = NBTHelper.getTag(ist);
			NBTTagList tagList = tagCompound.getTagList("Items", 10);

			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
				String itemName = tagItemData.getString("Name");
				if(itemName.equals(itemRegistryName)) {
					int quantity = tagItemData.getInteger("Quantity");
					return quantity >= getTorchItemMaxCapacity();
				}
			}
		}
		return false;
	}

	//TODO refactor these as they seem needlessly complicated
	public String getTorchPlacementMode(ItemStack ist) {
		if(NBTHelper.getTag(ist) == null) {
			return null;
		}

		NBTTagCompound tagCompound = NBTHelper.getTag(ist);
		String torchToPlace = tagCompound.getString("Torch");

		NBTTagList tagList = tagCompound.getTagList("Items", 10);

		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			String itemName = tagItemData.getString("Name");
			if(itemName.equals(torchToPlace)) {
				int quantity = tagItemData.getInteger("Quantity");
				if(quantity <= 0)
					torchToPlace = null;
			}
		}

		if(torchToPlace == null || torchToPlace.isEmpty()) {
			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
				String itemName = tagItemData.getString("Name");

				int quantity = tagItemData.getInteger("Quantity");
				if(quantity > 0) {
					tagCompound.setString("Torch", itemName);
					return itemName;
				}
			}
		}
		return (torchToPlace == null || torchToPlace.isEmpty()) && Settings.Items.SojournerStaff.torches.length > 0 ? Settings.Items.SojournerStaff.torches[0] : torchToPlace;
	}

	public int getTorchCount(ItemStack ist) {
		if(NBTHelper.getTag(ist) == null) {
			return 0;
		}

		NBTTagCompound tagCompound = NBTHelper.getTag(ist);
		String torchToPlace = tagCompound.getString("Torch");

		NBTTagList tagList = tagCompound.getTagList("Items", 10);

		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			String itemName = tagItemData.getString("Name");
			if(itemName.equals(torchToPlace)) {
				int quantity = tagItemData.getInteger("Quantity");
				if(quantity <= 0)
					torchToPlace = null;
				else
					return quantity;
			}
		}
		if(torchToPlace == null || torchToPlace.isEmpty()) {
			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
				String itemName = tagItemData.getString("Name");

				int quantity = tagItemData.getInteger("Quantity");
				if(quantity > 0) {
					tagCompound.setString("Torch", itemName);
					return quantity;
				}
			}
		}
		return 0;
	}

	private void cycleTorchMode(ItemStack ist) {
		String mode = getTorchPlacementMode(ist);
		if(mode == null || mode.isEmpty())
			return;

		NBTTagCompound tagCompound = NBTHelper.getTag(ist);

		NBTTagList tagList = tagCompound.getTagList("Items", 10);

		boolean itemFound = false;
		String firstItem = null;
		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			String itemName = tagItemData.getString("Name");
			int quantity = tagItemData.getInteger("Quantity");
			if(quantity <= 0)
				continue;
			if(firstItem == null) {
				firstItem = itemName;
			}
			if(itemFound) {
				tagCompound.setString("Torch", itemName);
				return;
			}
			if(itemName.equals(mode))
				itemFound = true;
			if(i == tagList.tagCount() - 1) {
				tagCompound.setString("Torch", firstItem);
			}
		}
	}

	private int getTorchItemMaxCapacity() {
		return Settings.Items.SojournerStaff.maxCapacityPerItemType;
	}

	static boolean removeItemFromInternalStorage(ItemStack ist, String itemRegistryName, int cost, EntityPlayer player) {
		if(player.capabilities.isCreativeMode)
			return true;
		if(hasItemInInternalStorage(ist, itemRegistryName, cost)) {
			NBTTagCompound tagCompound = NBTHelper.getTag(ist);

			NBTTagList tagList = tagCompound.getTagList("Items", 10);

			NBTTagList replacementTagList = new NBTTagList();

			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
				String itemName = tagItemData.getString("Name");
				if(itemName.equals(itemRegistryName)) {
					int quantity = tagItemData.getInteger("Quantity");
					tagItemData.setInteger("Quantity", quantity - cost);
				}
				replacementTagList.appendTag(tagItemData);
			}
			tagCompound.setTag("Items", replacementTagList);
			NBTHelper.setTag(ist, tagCompound);
			return true;
		}
		return false;

	}

	@Override
	public void addInformation(ItemStack staff, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		//maps the contents of the Sojourner's staff to a tooltip, so the player can review the torches stored within.
		String phrase = "Nothing.";
		String placing = "Nothing.";
		NBTTagCompound tagCompound = NBTHelper.getTag(staff);
		if(tagCompound != null) {
			NBTTagList tagList = tagCompound.getTagList("Items", 10);
			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
				String itemName = tagItemData.getString("Name");
				Item containedItem = RegistryHelper.getItemFromName(itemName);
				int quantity = tagItemData.getInteger("Quantity");
				phrase = String.format("%s%s", phrase.equals("Nothing.") ? "" : String.format("%s;", phrase), new ItemStack(containedItem, 1, 0).getDisplayName() + ": " + quantity);
			}

			//add "currently placing: blah blah blah" to the tooltip.
			Item placingItem = null;
			if(getTorchPlacementMode(staff) != null)
				placingItem = RegistryHelper.getItemFromName(getTorchPlacementMode(staff));

			if(placingItem != null) {
				placing = new ItemStack(placingItem, 1, 0).getDisplayName();
			}
		}
		this.formatTooltip(ImmutableMap.of("phrase", phrase, "placing", placing), staff, tooltip);
		if(this.isEnabled(staff))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + getItemStackDisplayName(new ItemStack(Blocks.TORCH))), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOff, float yOff, float zOff) {
		ItemStack stack = player.getHeldItem(hand);
		return placeTorch(player, world, pos, hand, side, stack);
	}

	private EnumActionResult placeTorch(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, ItemStack stack) {
		if(player.isSwingInProgress)
			return EnumActionResult.PASS;
		player.swingArm(hand);
		if(world.isRemote)
			return EnumActionResult.SUCCESS;
		if(!player.canPlayerEdit(pos, side, stack))
			return EnumActionResult.PASS;
		if(player.isSneaking())
			return EnumActionResult.PASS;
		if(getTorchPlacementMode(stack) == null)
			return EnumActionResult.FAIL;
		Block blockAttemptingPlacement = Block.getBlockFromName(getTorchPlacementMode(stack));
		if(blockAttemptingPlacement == null)
			return EnumActionResult.FAIL;

		Block blockTargetted = world.getBlockState(pos).getBlock();
		BlockPos placeBlockAt = pos;

		if(blockTargetted == Blocks.SNOW) {
			side = EnumFacing.UP;
		} else if(blockTargetted != Blocks.VINE && blockTargetted != Blocks.TALLGRASS && blockTargetted != Blocks.DEADBUSH && !blockTargetted.isReplaceable(world, pos)) {
			placeBlockAt = pos.offset(side);
		}

		if(blockAttemptingPlacement.canPlaceBlockAt(world, placeBlockAt)) {
			if(world.mayPlace(blockAttemptingPlacement, placeBlockAt, false, side, player)) {
				if(!player.capabilities.isCreativeMode) {
					int cost = 1;
					int distance = (int) player.getDistance(placeBlockAt.getX(), placeBlockAt.getY(), placeBlockAt.getZ());
					for(; distance > Settings.Items.SojournerStaff.tilePerCostMultiplier; distance -= Settings.Items.SojournerStaff.tilePerCostMultiplier) {
						cost++;
					}
					//noinspection ConstantConditions
					if(!removeItemFromInternalStorage(stack, blockAttemptingPlacement.getRegistryName().toString(), cost, player))
						return EnumActionResult.FAIL;
				}
				IBlockState torchBlockState = attemptSide(world, placeBlockAt, side, blockAttemptingPlacement, player, hand);
				if(placeBlockAt(stack, player, world, placeBlockAt, torchBlockState)) {
					blockAttemptingPlacement.onBlockAdded(world, placeBlockAt, torchBlockState);
					double gauss = 0.5D + world.rand.nextFloat() / 2;
					world.spawnParticle(EnumParticleTypes.SPELL_MOB, placeBlockAt.getX() + 0.5D, placeBlockAt.getY() + 0.5D, placeBlockAt.getZ() + 0.5D, gauss, gauss, 0.0F);
					SoundType soundType = blockAttemptingPlacement.getSoundType(torchBlockState, world, placeBlockAt, player);
					world.playSound(null, placeBlockAt, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	private IBlockState attemptSide(World world, BlockPos pos, EnumFacing side, Block block, EntityPlayer player, EnumHand hand) {
		return block.getStateForPlacement(world, pos, side, pos.getX(), pos.getY(), pos.getZ(), 0, player, hand);
	}

	//a longer ranged version of "getMovingObjectPositionFromPlayer" basically
	private RayTraceResult getBlockTarget(World world, EntityPlayer player) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + (double) (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3d vec3 = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = Settings.Items.SojournerStaff.maxRange;
		Vec3d vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.rayTraceBlocks(vec3, vec31, true, false, false);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack ist = player.getHeldItem(hand);
		//calls onItemUse so all of the functionality we'd normally have to do preventative checks on gets handled there.
		if(!player.isSneaking()) {
			RayTraceResult mop = this.getBlockTarget(world, player);
			if(mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
				placeTorch(player, world, mop.getBlockPos(), hand, mop.sideHit, ist);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Nonnull
	@Override
	protected RayTraceResult rayTrace(World world, EntityPlayer player, boolean useLiquids) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		double d0 = player.posX;
		double d1 = player.posY + (double) player.getEyeHeight();
		double d2 = player.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 32.0D;
		if(player instanceof net.minecraft.entity.player.EntityPlayerMP) {
			d3 = ((net.minecraft.entity.player.EntityPlayerMP) player).interactionManager.getBlockReachDistance();
		}
		Vec3d vec3d1 = vec3d.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
		//noinspection ConstantConditions
		return world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

	private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState torchBlockState) {
		if(!world.setBlockState(pos, torchBlockState, 3))
			return false;

		if(torchBlockState.getBlock() == Blocks.TORCH) {
			//noinspection deprecation
			Blocks.TORCH.neighborChanged(torchBlockState, world, pos, torchBlockState.getBlock(), pos);
			Blocks.TORCH.onBlockPlacedBy(world, pos, torchBlockState, player, stack);
		}

		return true;
	}
}
