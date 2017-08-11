package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
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
import xreliquary.items.util.FilteredItemHandlerProvider;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		return Settings.Items.EnderStaff.enderPearlCastCost;
	}

	private int getEnderStaffNodeWarpCost() {
		return Settings.Items.EnderStaff.enderPearlNodeWarpCost;
	}

	private int getEnderPearlWorth() {
		return Settings.Items.EnderStaff.enderPearlWorth;
	}

	private int getEnderPearlLimit() {
		return Settings.Items.EnderStaff.enderPearlLimit;
	}

	private int getNodeWarpCastTime() {
		return Settings.Items.EnderStaff.nodeWarpCastTime;
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
		return new FilteredItemHandlerProvider(new int[] {Settings.Items.EnderStaff.enderPearlLimit}, new Item[] {Items.ENDER_PEARL}, new int[] {Settings.Items.EnderStaff.enderPearlWorth});
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int slotNumber, boolean isSelected) {
		if(world.isRemote)
			return;

		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;

		if(!this.isEnabled(ist))
			return;
		if(getPearlCount(ist) + getEnderPearlWorth() <= getEnderPearlLimit()) {
			if(InventoryHelper.consumeItem(new ItemStack(Items.ENDER_PEARL), player)) {
				setPearlCount(ist, getPearlCount(ist) + getEnderPearlWorth());
			}
		}
	}

	private void setPearlCount(@Nonnull ItemStack ist, int count) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		filteredHandler.setTotalAmount(0, count);

	}

	private int getPearlCount(@Nonnull ItemStack staff) {
		return getPearlCount(staff, false);
	}

	public int getPearlCount(@Nonnull ItemStack staff, boolean isClient) {
		if (isClient) {
			return NBTHelper.getInteger("count", staff);
		}

		IItemHandler itemHandler = staff.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

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

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack ist = player.getHeldItem(hand);
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
						setPearlCount(ist, getPearlCount(ist) - getEnderStaffPearlCost());
				}
			} else {
				player.setActiveHand(hand);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	private void doWraithNodeWarpCheck(ItemStack stack, World world, EntityPlayer player) {
		if(getPearlCount(stack) < getEnderStaffNodeWarpCost() && !player.capabilities.isCreativeMode)
			return;

		if(stack.getTagCompound() != null && stack.getTagCompound().getInteger("dimensionID") != getDimension(world)) {
			if(!world.isRemote) {
				player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Out of range!"));
			}
		} else if(stack.getTagCompound() != null && world.getBlockState(new BlockPos(stack.getTagCompound().getInteger("nodeX" + getDimension(world)), stack.getTagCompound().getInteger("nodeY" + getDimension(world)), stack.getTagCompound().getInteger("nodeZ" + getDimension(world)))).getBlock() == ModBlocks.wraithNode) {
			if(canTeleport(world, stack.getTagCompound().getInteger("nodeX" + getDimension(world)), stack.getTagCompound().getInteger("nodeY" + getDimension(world)), stack.getTagCompound().getInteger("nodeZ" + getDimension(world)))) {
				teleportPlayer(world, stack.getTagCompound().getInteger("nodeX" + getDimension(world)), stack.getTagCompound().getInteger("nodeY" + getDimension(world)), stack.getTagCompound().getInteger("nodeZ" + getDimension(world)), player);
				//setCooldown(ist);
				if(!player.capabilities.isCreativeMode && !player.world.isRemote)
					setPearlCount(stack, getPearlCount(stack) - getEnderStaffNodeWarpCost());
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
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack ist, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		//added spacing here to make sure the tooltips didn't come out with weird punctuation derps.
		String charge = Integer.toString(getPearlCount(ist, true));
		String phrase = "Currently bound to ";
		String position = "";
		if(ist.getTagCompound() != null && ist.getTagCompound().getInteger("dimensionID") != getDimension(world)) {
			phrase = "Out of range!";
		} else if(ist.getTagCompound() != null && ist.getTagCompound().hasKey("nodeX" + getDimension(world)) && ist.getTagCompound().hasKey("nodeY" + getDimension(world)) && ist.getTagCompound().hasKey("nodeZ" + getDimension(world))) {
			position = "X: " + ist.getTagCompound().getInteger("nodeX" + getDimension(world)) + " Y: " + ist.getTagCompound().getInteger("nodeY" + getDimension(world)) + " Z: " + ist.getTagCompound().getInteger("nodeZ" + getDimension(world));
		} else {
			position = "nowhere.";
		}
		this.formatTooltip(ImmutableMap.of("phrase", phrase, "position", position, "charge", charge), ist, tooltip);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GREEN + Items.ENDER_PEARL.getItemStackDisplayName(new ItemStack(Items.ENDER_PEARL))), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		// if right clicking on a wraith node, bind the eye to that wraith node.
		if((stack.getTagCompound() == null || !(stack.getTagCompound().hasKey("dimensionID"))) && world.getBlockState(pos).getBlock() == ModBlocks.wraithNode) {
			setWraithNode(stack, pos, getDimension(world));

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

	private int getDimension(@Nullable World world) {
		return world != null ? world.provider.getDimension() : 0;
	}

	private void setWraithNode(ItemStack eye, BlockPos pos, int dimensionID) {
		NBTHelper.setInteger("nodeX" + dimensionID, eye, pos.getX());
		NBTHelper.setInteger("nodeY" + dimensionID, eye, pos.getY());
		NBTHelper.setInteger("nodeZ" + dimensionID, eye, pos.getZ());
		NBTHelper.setInteger("dimensionID", eye, dimensionID);
	}

	@Nullable
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack staff) {
		NBTTagCompound nbt = super.getNBTShareTag(staff);
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger("count", getPearlCount(staff));

		return nbt;
	}
}
