package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.XpHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemFortuneCoin extends ItemBauble implements IPedestalActionItem {

	public ItemFortuneCoin() {
		super(Names.Items.FORTUNE_COIN);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

/* TODO readd with baubles
	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
		if(player.world.isRemote)
			player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 2.2F));

	}
*/

	@Nonnull
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
					world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
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
		List<EntityItem> iList = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		for(EntityItem item : iList) {
			//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
			if(item.getEntityData().getBoolean("PreventRemoteMovement")) {
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

	private void teleportEntityToPlayer(Entity item, EntityPlayer player) {
		player.world.spawnParticle(EnumParticleTypes.SPELL_MOB, item.posX + 0.5D + player.world.rand.nextGaussian() / 8, item.posY + 0.2D, item.posZ + 0.5D + player.world.rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
		player.getLookVec();
		double x = player.posX + player.getLookVec().xCoord * 0.2D;
		double y = player.posY;
		double z = player.posZ + player.getLookVec().zCoord * 0.2D;
		item.setPosition(x, y, z);
		if(!disabledAudio()) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.8F));
		}
	}

	private boolean checkForRoom(@Nonnull ItemStack stackToPickup, EntityPlayer player) {
		int remaining = stackToPickup.getCount();
		for(ItemStack inventoryStack : player.inventory.mainInventory) {
			if(inventoryStack.isEmpty())
				return true;

			if(inventoryStack.getItem() == stackToPickup.getItem() && inventoryStack.getItemDamage() == stackToPickup.getItemDamage()) {
				if(inventoryStack.getCount() + remaining <= inventoryStack.getMaxStackSize())
					return true;
				else {
					remaining -= (inventoryStack.getMaxStackSize() - inventoryStack.getCount());
				}
			} else if(inventoryStack.getItem() == ModItems.filledVoidTear && ModItems.filledVoidTear.isEnabled(inventoryStack) && ModItems.filledVoidTear.canAbsorbStack(stackToPickup, inventoryStack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int count) {
		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		scanForEntitiesInRange(player.world, player, getLongRangePullDistance());
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

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(player.isSneaking()) {
			if(!disabledAudio()) {
				NBTHelper.setShort("soundTimer", stack, 6);
			}
			NBTHelper.setBoolean("enabled", stack, !isEnabled(stack));
		} else {
			player.setActiveHand(hand);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

/*	TODO readd with baubles
	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		this.onUpdate(stack, player.world, player, 0, false);
	}
*/

	private boolean disabledAudio() {
		return Settings.FortuneCoin.disableAudio;
	}

	@Override
	public void update(@Nonnull ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		if(world.isRemote)
			return;

		if(isEnabled(stack)) {
			BlockPos pos = pedestal.getBlockPos();
			double d = getStandardPullDistance();

			List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for(EntityItem entityItem : entities) {

				//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
				if(entityItem.getEntityData().getBoolean("PreventRemoteMovement")) {
					continue;
				}

				int numberAdded = pedestal.addToConnectedInventory(entityItem.getEntityItem().copy());
				if(numberAdded > 0) {
					entityItem.getEntityItem().setCount(entityItem.getEntityItem().getCount() - numberAdded);

					if(entityItem.getEntityItem().getCount() <= 0)
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
						world.spawnEntity(new EntityXPOrb(world, pos.getX(), pos.getY(), pos.getZ(), XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
					}
				} else {
					pedestal.setActionCoolDown(20);
				}
			}
		}
	}

	@Override
	public void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(@Nonnull ItemStack stack, IPedestal pedestal) {
	}
}
