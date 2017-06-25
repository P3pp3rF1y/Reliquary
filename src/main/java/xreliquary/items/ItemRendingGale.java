package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
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

import javax.annotation.Nonnull;
import java.util.List;

public class ItemRendingGale extends ItemToggleable {
	public ItemRendingGale() {
		super(Names.Items.RENDING_GALE);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List<String> list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;

		this.formatTooltip(ImmutableMap.of("charge", Integer.toString(getFeatherCount(ist))), ist, list);

		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.WHITE + Items.FEATHER.getItemStackDisplayName(new ItemStack(Items.FEATHER))), list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, list);
	}

	private static int getChargeLimit() {
		return Settings.RendingGale.chargeLimit;
	}

	public static int getChargeCost() {
		return Settings.RendingGale.castChargeCost;
	}

	private static int getFeathersWorth() {
		return Settings.RendingGale.chargeFeatherWorth;
	}

	public static int getBoltChargeCost() {
		return Settings.RendingGale.boltChargeCost;
	}

	private static int getBoltTargetRange() {
		return Settings.RendingGale.blockTargetRange;
	}

	private static int getRadialPushRadius() {
		return Settings.RendingGale.pushPullRadius;
	}

	private static boolean canPushProjectiles() {
		return Settings.RendingGale.canPushProjectiles;
	}

	private void attemptFlight(EntityLivingBase entityLiving) {
		if(!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		Vec3d lookVec = player.getLook(0.66F);

		double x = lookVec.x;
		double y = lookVec.y;
		double z = lookVec.z;

		RayTraceResult rayTrace =  this.rayTrace(player.world, player, true);

		double slowDownFactor = 1.0;

		//noinspection ConstantConditions
		if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
			double distance = player.getPosition().distanceSq(rayTrace.getBlockPos());
			if(distance < 20) {
				slowDownFactor = distance / 20;
			}
		}

		player.motionX = x * slowDownFactor;
		player.motionY = y * slowDownFactor;
		player.motionZ = z * slowDownFactor;

		player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);

		player.fallDistance = 0.0F;

	}

	@Override
	public void onUpdate(ItemStack rendingGale, World world, Entity e, int slotNumber, boolean isSelected) {
		if(world.isRemote || !(e instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) e;

		if(this.isEnabled(rendingGale)) {
			if(getFeatherCount(rendingGale) + getFeathersWorth() <= getChargeLimit()) {
				if(InventoryHelper.consumeItem(new ItemStack(Items.FEATHER), player)) {
					setFeatherCount(rendingGale, getFeatherCount(rendingGale) + getFeathersWorth());
				}
			}
		}

		if(player.getHeldItemMainhand() == rendingGale) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(EnumHand.MAIN_HAND, getItemHandlerNBT(rendingGale)), (EntityPlayerMP) player);
		} else if(player.getHeldItemOffhand() == rendingGale) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(EnumHand.OFF_HAND, getItemHandlerNBT(rendingGale)), (EntityPlayerMP) player);
		} else if(player.inventory.getStackInSlot(slotNumber).getItem() == ModItems.rendingGale) {
			PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(rendingGale)), (EntityPlayerMP) player);
		}
	}

	private NBTTagCompound getItemHandlerNBT(ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return null;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.serializeNBT();
	}

	public String getMode(ItemStack ist) {
		if(NBTHelper.getString("mode", ist).equals("")) {
			setMode(ist, "flight");
		}
		return NBTHelper.getString("mode", ist);
	}

	private void setMode(ItemStack ist, String s) {
		NBTHelper.setString("mode", ist, s);
	}

	private void cycleMode(ItemStack ist, boolean isRaining) {
		if(isFlightMode(ist))
			setMode(ist, "push");
		else if(isPushMode(ist))
			setMode(ist, "pull");
		else if(isPullMode(ist) && isRaining)
			setMode(ist, "bolt");
		else
			setMode(ist, "flight");
	}

	private boolean isPullMode(ItemStack ist) {
		return getMode(ist).equals("pull");
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
		if(entityLiving.world.isRemote)
			return false;
		if(!(entityLiving instanceof EntityPlayer))
			return false;
		EntityPlayer player = (EntityPlayer) entityLiving;
		if(player.isSneaking()) {
			cycleMode(ist, player.world.isRaining());
			return true;
		}
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FilteredItemHandlerProvider(new int[] {Settings.RendingGale.chargeLimit}, new Item[] {Items.FEATHER}, new int[] {Settings.RendingGale.chargeFeatherWorth});
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 6000;
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		return EnumAction.BLOCK;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack ist = player.getHeldItem(hand);
		if(player.isSneaking()) {
			super.onItemRightClick(world, player, hand);
		} else {
			player.setActiveHand(hand);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, ist);
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int count) {
		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		int totalCost = -1;
		int ticksInUse = getMaxItemUseDuration(ist) - count;

		if(ticksInUse < 0) {
			player.stopActiveHand();
			return;
		}

		if(!player.capabilities.isCreativeMode) {
			if(isFlightMode(ist)) {
				totalCost = ticksInUse * getChargeCost();
			} else if(isBoltMode(ist)) {
				totalCost = (ticksInUse / 8) * getBoltChargeCost();
			}
		}

		if(getFeatherCount(ist) <= totalCost) {
			player.stopActiveHand();
			return;
		}

		if(isFlightMode(ist)) {
			attemptFlight(player);
			spawnFlightParticles(player.world, player.posX, player.posY + player.getEyeHeight(), player.posZ, player);
		} else if(isPushMode(ist)) {
			doRadialPush(player.world, player.posX, player.posY, player.posZ, player, false);
		} else if(isPullMode(ist)) {
			doRadialPush(player.world, player.posX, player.posY, player.posZ, player, true);
		} else if(isBoltMode(ist)) {

			RayTraceResult mop = this.getCycloneBlockTarget(player.world, player);

			if(mop != null) {
				if(ticksInUse % 8 == 0) {
					int attemptedY = mop.getBlockPos().getY();
					if(!player.world.isRainingAt(mop.getBlockPos())) {
						attemptedY++;
					}
					if(!player.world.isRemote && player.world.isRainingAt(new BlockPos(mop.getBlockPos().getX(), attemptedY, mop.getBlockPos().getZ()))) {
						player.world.addWeatherEffect(new EntityLightningBolt(player.world, (double) mop.getBlockPos().getX(), (double) mop.getBlockPos().getY(), (double) mop.getBlockPos().getZ(), false));
					}
				}
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if(!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		if(worldIn.isRemote)
			return;

		int ticksInUse = getMaxItemUseDuration(stack) - timeLeft;

		if(ticksInUse > 0) {
			if(isFlightMode(stack)) {
				if(!player.capabilities.isCreativeMode)
					setFeatherCount(stack, (getFeatherCount(stack) - (getChargeCost() * ticksInUse)) > 0 ? (getFeatherCount(stack) - (getChargeCost() * ticksInUse)) : 0);
			} else if(isBoltMode(stack)) {
				if(!player.capabilities.isCreativeMode)
					setFeatherCount(stack, (getFeatherCount(stack) - (getBoltChargeCost() * (ticksInUse / 8))) > 0 ? (getFeatherCount(stack) - (getBoltChargeCost() * (ticksInUse / 8))) : 0);
			}
		}
	}

	private boolean isPushMode(ItemStack ist) {
		return getMode(ist).equals("push");
	}

	public boolean isFlightMode(ItemStack ist) {
		return getMode(ist).equals("flight");
	}

	public boolean hasFlightCharge(EntityPlayer player, ItemStack ist) {
		int remainingCharge = getFeatherCount(ist);
		if(player.isHandActive()) {
			remainingCharge -= (player.getItemInUseMaxCount() - player.getItemInUseCount()) * getChargeCost();
		}

		return remainingCharge > 0;
	}

	public boolean isBoltMode(ItemStack stack) {
		return getMode(stack).equals("bolt");
	}

	//a longer ranged version of "getMovingObjectPositionFromPlayer" basically
	private RayTraceResult getCycloneBlockTarget(World world, EntityPlayer player) {
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
		double d3 = (double) getBoltTargetRange();
		Vec3d vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.rayTraceBlocks(vec3, vec31, true, false, false);
	}

	public int getFeatherCount(ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return 0;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.getTotalAmount(0);
	}

	public void setFeatherCount(ItemStack ist, int featherCount) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		filteredHandler.setTotalAmount(0, featherCount);
	}

	public void doRadialPush(World world, double posX, double posY, double posZ, EntityPlayer player, boolean pull) {
		//push effect free at the moment, if you restore cost, remember to change this to getFeatherCount
		spawnRadialHurricaneParticles(world, posX, posY, posZ, player, pull);
		if(world.isRemote)
			return;

		double lowerX = posX - getRadialPushRadius();
		double lowerY = posY - (double) getRadialPushRadius() / 5D;
		double lowerZ = posZ - getRadialPushRadius();
		double upperX = posX + getRadialPushRadius();
		double upperY = posY + (double) getRadialPushRadius() / 2D;
		double upperZ = posZ + getRadialPushRadius();

		List<String> entitiesThatCanBePushed = Settings.RendingGale.entitiesThatCanBePushed;
		List<String> projectilesThatCanBePushed = Settings.RendingGale.projectilesThatCanBePushed;

		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));

		for(Entity entity : entities) {
			String entityName = EntityList.getEntityString(entity);
			if(entitiesThatCanBePushed.contains(entityName) || (!pull && canPushProjectiles() && projectilesThatCanBePushed.contains(entityName))) {
				double distance = getDistanceToEntity(posX, posY, posZ, entity);
				if(distance >= getRadialPushRadius())
					continue;

				if(entity.equals(player))
					continue;
				Vec3d pushVector;
				if(pull) {
					pushVector = new Vec3d(posX - entity.posX, posY - entity.posY, posZ - entity.posZ);
				} else {
					pushVector = new Vec3d(entity.posX - posX, entity.posY - posY, entity.posZ - posZ);
				}
				pushVector = pushVector.normalize();
				entity.move(MoverType.PLAYER, 0.0D, 0.2D, 0.0D);
				entity.move(MoverType.PLAYER, pushVector.x, Math.min(pushVector.y, 0.1D) * 1.5D, pushVector.z);
			}
		}
	}

	private float getDistanceToEntity(double posX, double posY, double posZ, Entity entityIn) {
		float f = (float) (posX - entityIn.posX);
		float f1 = (float) (posY - entityIn.posY);
		float f2 = (float) (posZ - entityIn.posZ);
		return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
	}

	private void spawnFlightParticles(World world, double x, double y, double z, EntityPlayer player) {
		Vec3d lookVector = player.getLookVec();
		double factor = (player.motionX / lookVector.x + player.motionY / lookVector.y + player.motionZ / lookVector.z) / 3d;

		//spawn a whole mess of particles every tick.
		for(int i = 0; i < 8 * factor; ++i) {
			float randX = 10F * (itemRand.nextFloat() - 0.5F);
			float randY = 10F * (itemRand.nextFloat() - 0.5F);
			float randZ = 10F * (itemRand.nextFloat() - 0.5F);

			world.spawnParticle(EnumParticleTypes.BLOCK_DUST, x + randX + lookVector.x * 20 * factor, y + randY + lookVector.y * 20 * factor, z + randZ + lookVector.z * 20 * factor, -lookVector.x * 5 * factor, -lookVector.y * 5 * factor, -lookVector.z * 5 * factor, Block.getStateId(Blocks.SNOW_LAYER.getDefaultState()));
		}
	}

	private void spawnRadialHurricaneParticles(World world, double posX, double posY, double posZ, EntityPlayer player, boolean pull) {
		//spawn a whole mess of particles every tick.
		for(int i = 0; i < 3; ++i) {
			float randX = world.rand.nextFloat() - 0.5F;
			float randZ = world.rand.nextFloat() - 0.5F;
			float motX = randX * 10F;
			float motZ = randZ * 10F;
			if(pull) {
				randX *= 10F;
				randZ *= 10F;
				motX *= -1F;
				motZ *= -1F;
			}

			double posYAdjusted = player == null ? posY : (posY + player.getEyeHeight()) - (player.height / 2);

			world.spawnParticle(EnumParticleTypes.BLOCK_DUST, posX + randX, posYAdjusted, posZ + randZ, motX, 0.0D, motZ, Block.getStateId(Blocks.SNOW_LAYER.getDefaultState()));
		}
	}
}
