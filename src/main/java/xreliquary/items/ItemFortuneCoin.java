package xreliquary.items;

import baubles.api.BaubleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.XpHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemFortuneCoin extends ItemBauble implements IPedestalActionItem {

	public ItemFortuneCoin() {
		super(Names.Items.FORTUNE_COIN);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
		if(player.worldObj.isRemote)
			player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2.2F));

	}

	@SuppressWarnings("NullableProblems")
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return isEnabled(stack);
	}

	private boolean isEnabled(ItemStack stack) {
		return NBTHelper.getBoolean("enabled", stack);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity entity, int i, boolean f) {
		if(world.isRemote)
			return;
		if(!disabledAudio())
			if(NBTHelper.getShort("soundTimer", ist) > 0) {
				if(NBTHelper.getShort("soundTimer", ist) % 2 == 0) {
					world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
				}
				NBTHelper.setShort("soundTimer", ist, NBTHelper.getShort("soundTimer", ist) - 1);
			}
		if(!isEnabled(ist))
			return;
		EntityPlayer player = null;
		if(entity instanceof EntityPlayer) {
			player = (EntityPlayer) entity;
		}
		if(player == null || player.isSpectator())
			return;
		scanForEntitiesInRange(world, player, getStandardPullDistance());
	}

	private void scanForEntitiesInRange(World world, EntityPlayer player, double d) {
		List<BlockPos> disablePositions = getDisablePositions(world, player.getPosition());
		List<EntityItem> iList = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		for(EntityItem item : iList) {
			//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
			if(!canPickupItem(item, disablePositions)) {
				continue;
			}

			if(!checkForRoom(item.getEntityItem(), player)) {
				continue;
			}

			item.setPickupDelay(0);
			if(player.getDistanceToEntity(item) < 1.5D) {
				continue;
			}
			teleportEntityToPlayer(item, player);
			break;
		}
		List<EntityXPOrb> iList2 = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		for(EntityXPOrb item : iList2) {
			if(player.xpCooldown > 0) {
				player.xpCooldown = 0;
			}
			if(player.getDistanceToEntity(item) < 1.5D) {
				continue;
			}
			teleportEntityToPlayer(item, player);
			break;
		}
	}

	private boolean canPickupItem(EntityItem item, List<BlockPos> disablePositions) {
		if (item.getEntityData().getBoolean("PreventRemoteMovement"))
			return false;
		if (isInDisabledRange(item, disablePositions))
			return false;
		return true;
	}

	private boolean isInDisabledRange(EntityItem item, List<BlockPos> disablePositions) {
		for (BlockPos disablePos : disablePositions) {
			if (Math.abs(item.getPosition().getX() - disablePos.getX()) < 5
				&& Math.abs(item.getPosition().getY() - disablePos.getY()) < 5
				&& Math.abs(item.getPosition().getZ() - disablePos.getZ()) < 5)
				return true;
		}
		return false;
	}
	
	private List<BlockPos> getDisablePositions(World world, BlockPos coinPos) {
		List<BlockPos> disablePositions = new ArrayList<>();
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(world.provider.getDimension(), coinPos, 10);
		
		for (BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityPedestal) {
				TileEntityPedestal pedestal = (TileEntityPedestal) te;
				
				if (pedestal.switchedOn()) {
					ItemStack stack = pedestal.getStackInSlot(0);
					if (stack != null && stack.getItem() == this && !isEnabled(stack)) {
						disablePositions.add(pos);
					}
				}
			}
		}
		return disablePositions;
	}

	private void teleportEntityToPlayer(Entity item, EntityPlayer player) {
		player.worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, item.posX + 0.5D + player.worldObj.rand.nextGaussian() / 8, item.posY + 0.2D, item.posZ + 0.5D + player.worldObj.rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
		player.getLookVec();
		double x = player.posX + player.getLookVec().xCoord * 0.2D;
		double y = player.posY;
		double z = player.posZ + player.getLookVec().zCoord * 0.2D;
		item.setPosition(x, y, z);
		if(!disabledAudio()) {
			player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
		}
	}

	private boolean checkForRoom(ItemStack stackToPickup, EntityPlayer player) {
		int remaining = stackToPickup.stackSize;
		for(ItemStack inventoryStack : player.inventory.mainInventory) {
			if(inventoryStack == null) {
				continue;
			}
			if(inventoryStack.getItem() == stackToPickup.getItem() && inventoryStack.getItemDamage() == stackToPickup.getItemDamage()) {
				if(inventoryStack.stackSize + remaining <= inventoryStack.getMaxStackSize())
					return true;
				else {
					int count = inventoryStack.stackSize;
					while(count < inventoryStack.getMaxStackSize()) {
						count++;
						remaining--;
						if(remaining == 0)
							return true;
					}
				}
			} else if(inventoryStack.getItem() == ModItems.filledVoidTear && ModItems.filledVoidTear.isEnabled(inventoryStack) && ModItems.filledVoidTear.canAbsorbStack(stackToPickup, inventoryStack)) {
				return true;
			}
		}

		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				return true;
		}
		return false;
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int count) {
		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		scanForEntitiesInRange(player.worldObj, player, getLongRangePullDistance());
	}

	private double getLongRangePullDistance() {
		return (double) Settings.FortuneCoin.longRangePullDistance;
	}

	private double getStandardPullDistance() {
		return (double) Settings.FortuneCoin.standardPullDistance;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking()) {
			if(!disabledAudio()) {
				NBTHelper.setShort("soundTimer", ist, 6);
			}
			NBTHelper.setBoolean("enabled", ist, !isEnabled(ist));
		} else {
			player.setActiveHand(hand);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, ist);
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	private boolean disabledAudio() {
		return Settings.FortuneCoin.disableAudio;
	}

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		if(world.isRemote)
			return;

		if(isEnabled(stack)) {
			BlockPos pos = pedestal.getBlockPos();
			double d = getStandardPullDistance();

			List<BlockPos> disablePositions = getDisablePositions(world, pos);
			List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for(EntityItem entityItem : entities) {

				//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
				if(!canPickupItem(entityItem, disablePositions)) {
					continue;
				}

				int numberAdded = pedestal.addToConnectedInventory(entityItem.getEntityItem().copy());
				if(numberAdded > 0) {
					entityItem.getEntityItem().stackSize = entityItem.getEntityItem().stackSize - numberAdded;

					if(entityItem.getEntityItem().stackSize <= 0)
						entityItem.setDead();
				} else {
					pedestal.setActionCoolDown(20);
				}
			}

			List<EntityXPOrb> XPOrbs = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for(EntityXPOrb xpOrb : XPOrbs) {
				int amountToTransfer = XpHelper.experienceToLiquid(xpOrb.xpValue);
				int amountAdded = pedestal.fillConnectedTank(new FluidStack(ModFluids.fluidXpJuice, amountToTransfer));

				if(amountAdded > 0) {
					xpOrb.setDead();

					if(amountToTransfer > amountAdded) {
						world.spawnEntityInWorld(new EntityXPOrb(world, pos.getX(), pos.getY(), pos.getZ(), XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
					}
				} else {
					pedestal.setActionCoolDown(20);
				}
			}
		}
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
	}
}
