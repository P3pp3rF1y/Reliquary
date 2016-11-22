package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.entities.EntityEnderStaffProjectile;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.util.FilteredItemHandlerProvider;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketItemHandlerSync;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemEnderStaff extends ItemToggleable {

	public ItemEnderStaff() {
		super(Names.Items.ENDER_STAFF);
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

	private int getEnderStaffPearlCost() {
		return Settings.EnderStaff.enderPearlCastCost;
	}

	private int getEnderStaffNodeWarpCost() {
		return Settings.EnderStaff.enderPearlNodeWarpCost;
	}

	private int getEnderPearlWorth() {
		return Settings.EnderStaff.enderPearlWorth;
	}

	private int getEnderPearlLimit() {
		return Settings.EnderStaff.enderPearlLimit;
	}

	private int getNodeWarpCastTime() {
		return Settings.EnderStaff.nodeWarpCastTime;
	}

	public String getMode(ItemStack ist) {
		if(NBTHelper.getString("mode", ist).equals("")) {
			setMode(ist, "cast");
		}
		return NBTHelper.getString("mode", ist);
	}

	private void setMode(ItemStack ist, String s) {
		NBTHelper.setString("mode", ist, s);
	}

	private void cycleMode(ItemStack ist) {
		if(getMode(ist).equals("cast"))
			setMode(ist, "long_cast");
		else if(getMode(ist).equals("long_cast"))
			setMode(ist, "node_warp");
		else
			setMode(ist, "cast");
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
		if(entityLiving.world.isRemote)
			return true;
		if(!(entityLiving instanceof EntityPlayer))
			return true;
		EntityPlayer player = (EntityPlayer) entityLiving;
		if(player.isSneaking()) {
			cycleMode(ist);
		}
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FilteredItemHandlerProvider(new int[] {Settings.EnderStaff.enderPearlLimit}, new Item[] {Items.ENDER_PEARL}, new int[] {Settings.EnderStaff.enderPearlWorth});
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int slotNumber, boolean isSelected) {
		//TODO remove backwards compatibility in the future
		if(ist.getTagCompound() != null && ist.getTagCompound().hasKey("ender_pearls")) {
			IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

			if(itemHandler instanceof FilteredItemStackHandler) {
				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				if(ist.getTagCompound().hasKey("ender_pearls")) {
					filteredHandler.setTotalAmount(0, NBTHelper.getInteger("ender_pearls", ist));
					ist.getTagCompound().removeTag("ender_pearls");
				}
			}
		}

		if(world.isRemote)
			return;

		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;

		if(player.inventory.getStackInSlot(slotNumber).getItem() == ModItems.enderStaff && isSelected) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
		} else if(player.getHeldItemOffhand().getItem() == ModItems.enderStaff) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(EnumHand.OFF_HAND, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
		}

		if(!this.isEnabled(ist))
			return;
		if(getPearlCount(ist) + getEnderPearlWorth() <= getEnderPearlLimit()) {
			if(InventoryHelper.consumeItem(new ItemStack(Items.ENDER_PEARL), player)) {
				setPearlCount(ist, player, slotNumber, getPearlCount(ist) + getEnderPearlWorth());
			}
		}
	}

	private void setPearlCount(@Nonnull ItemStack ist, EntityPlayer player, EnumHand hand, int count) {
		setPearlCount(ist, count);
		PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(hand, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
	}

	private void setPearlCount(@Nonnull ItemStack ist, EntityPlayer player, int slotNumber, int count) {
		setPearlCount(ist, count);
		PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(ist)), (EntityPlayerMP) player);
	}

	private void setPearlCount(@Nonnull ItemStack ist, int count) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		filteredHandler.setTotalAmount(0, count);

	}

	private NBTTagCompound getItemHandlerNBT(@Nonnull ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return null;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.serializeNBT();
	}

	public int getPearlCount(@Nonnull ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return 0;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.getTotalAmount(0);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int unadjustedCount) {
		if(!(entityLivingBase instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLivingBase;

		for(int particles = 0; particles < 2; particles++) {
			player.world.spawnParticle(EnumParticleTypes.PORTAL, player.posX, player.posY, player.posZ, player.world.rand.nextGaussian(), player.world.rand.nextGaussian(), player.world.rand.nextGaussian());
		}
		if(unadjustedCount == 1) {
			player.stopActiveHand();
		}
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return this.getNodeWarpCastTime();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if(!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		if(timeLeft == 1) {
			doWraithNodeWarpCheck(stack, player.world, player);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		if(!player.isSneaking()) {
			if(getMode(ist).equals("cast") || getMode(ist).equals("long_cast")) {
				if(player.isSwingInProgress)
					return new ActionResult<>(EnumActionResult.FAIL, ist);
				player.swingArm(hand);
				if(getPearlCount(ist) < getEnderStaffPearlCost() && !player.capabilities.isCreativeMode)
					return new ActionResult<>(EnumActionResult.FAIL, ist);
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				if(!player.world.isRemote) {
					EntityEnderStaffProjectile enderStaffProjectile = new EntityEnderStaffProjectile(player.world, player, !getMode(ist).equals("long_cast"));
					enderStaffProjectile.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
					player.world.spawnEntity(enderStaffProjectile);
					if(!player.capabilities.isCreativeMode)
						setPearlCount(ist, player, hand, getPearlCount(ist) - getEnderStaffPearlCost());
				}
			} else {
				player.setActiveHand(hand);
			}
		}
		return super.onItemRightClick(ist, world, player, hand);
	}

	private ItemStack doWraithNodeWarpCheck(ItemStack stack, World world, EntityPlayer player) {
		if(getPearlCount(stack) < getEnderStaffNodeWarpCost() && !player.capabilities.isCreativeMode)
			return stack;

		if(stack.getTagCompound() != null && stack.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
			if(!world.isRemote) {
				player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Out of range!"));
			}
		} else if(stack.getTagCompound() != null && RegistryHelper.blocksEqual(world.getBlockState(new BlockPos(stack.getTagCompound().getInteger("nodeX" + getWorld(player)), stack.getTagCompound().getInteger("nodeY" + getWorld(player)), stack.getTagCompound().getInteger("nodeZ" + getWorld(player)))).getBlock(), ModBlocks.wraithNode)) {
			if(canTeleport(world, stack.getTagCompound().getInteger("nodeX" + getWorld(player)), stack.getTagCompound().getInteger("nodeY" + getWorld(player)), stack.getTagCompound().getInteger("nodeZ" + getWorld(player)))) {
				teleportPlayer(world, stack.getTagCompound().getInteger("nodeX" + getWorld(player)), stack.getTagCompound().getInteger("nodeY" + getWorld(player)), stack.getTagCompound().getInteger("nodeZ" + getWorld(player)), player);
				//setCooldown(ist);
				if(!player.capabilities.isCreativeMode && !player.world.isRemote)
					setPearlCount(stack, player, player.getActiveHand(), getPearlCount(stack) - getEnderStaffNodeWarpCost());
			}
		} else if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("dimensionID")) {
			stack.getTagCompound().removeTag("dimensionID");
			stack.getTagCompound().removeTag("nodeX");
			stack.getTagCompound().removeTag("nodeY");
			stack.getTagCompound().removeTag("nodeZ");
			stack.getTagCompound().removeTag("cooldown");
			if(!world.isRemote) {
				player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Node dosen't exist!"));
			} else {
				player.playSound(SoundEvents.ENTITY_ENDERMEN_DEATH, 1.0f, 1.0f);
			}
		}
		return stack;
	}

	private boolean canTeleport(World world, int x, int y, int z) {
		return !(!world.isAirBlock(new BlockPos(x, y + 1, z)) || !world.isAirBlock(new BlockPos(x, y + 2, z)));
	}

	private void teleportPlayer(World world, int x, int y, int z, EntityPlayer player) {
		player.setPositionAndUpdate(x + 0.5, y + 0.875, z + 0.5);
		player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
		for(int particles = 0; particles < 2; particles++) {
			world.spawnParticle(EnumParticleTypes.PORTAL, player.posX, player.posY, player.posZ, world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
		}
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List<String> list, boolean flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		//added spacing here to make sure the tooltips didn't come out with weird punctuation derps.
		String charge = Integer.toString(getPearlCount(ist));
		String phrase = "Currently bound to ";
		String position = "";
		if(ist.getTagCompound() != null && ist.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
			phrase = "Out of range!";
		} else if(ist.getTagCompound() != null && ist.getTagCompound().hasKey("nodeX" + getWorld(player)) && ist.getTagCompound().hasKey("nodeY" + getWorld(player)) && ist.getTagCompound().hasKey("nodeZ" + getWorld(player))) {
			position = "X: " + ist.getTagCompound().getInteger("nodeX" + getWorld(player)) + " Y: " + ist.getTagCompound().getInteger("nodeY" + getWorld(player)) + " Z: " + ist.getTagCompound().getInteger("nodeZ" + getWorld(player));
		} else {
			position = "nowhere.";
		}
		this.formatTooltip(ImmutableMap.of("phrase", phrase, "position", position, "charge", charge), ist, list);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GREEN + Items.ENDER_PEARL.getItemStackDisplayName(new ItemStack(Items.ENDER_PEARL))), list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, list);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		// if right clicking on a wraith node, bind the eye to that wraith node.
		if((stack.getTagCompound() == null || !(stack.getTagCompound().hasKey("dimensionID"))) && RegistryHelper.blocksEqual(world.getBlockState(pos).getBlock(), ModBlocks.wraithNode)) {
			setWraithNode(stack, pos, Integer.valueOf(getWorld(player)), player);

			player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
			for(int particles = 0; particles < 12; particles++) {
				world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
			}
			//setCooldown(stack);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.PASS;
		}
	}

	private void setWraithNode(ItemStack eye, BlockPos pos, int dimensionID, EntityPlayer player) {
		NBTHelper.setInteger("nodeX" + getWorld(player), eye, pos.getX());
		NBTHelper.setInteger("nodeY" + getWorld(player), eye, pos.getY());
		NBTHelper.setInteger("nodeZ" + getWorld(player), eye, pos.getZ());
		NBTHelper.setInteger("dimensionID", eye, dimensionID);
	}

	private String getWorld(EntityPlayer player) {
		return Integer.valueOf(player.world.provider.getDimension()).toString();
	}
}
