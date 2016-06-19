package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import xreliquary.Reliquary;
import xreliquary.entities.shot.*;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModItems;
import xreliquary.init.ModSounds;
import xreliquary.items.util.handgun.HandgunData;
import xreliquary.items.util.handgun.IHandgunData;
import xreliquary.network.PacketHandgunDataSync;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketPlayerHandgunDataSync;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ItemHandgun extends ItemBase {

	public ItemHandgun() {
		super(Names.handgun);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new ICapabilitySerializable<NBTTagCompound>() {

			private IHandgunData handgunData = new HandgunData();

			@Override
			public NBTTagCompound serializeNBT() {
				return handgunData.serializeNBT();
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt) {
				handgunData.deserializeNBT(nbt);
			}

			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				if(capability == ModCapabilities.HANDGUN_DATA_CAPABILITY)
					return true;
				return false;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if(capability == ModCapabilities.HANDGUN_DATA_CAPABILITY)
					return (T) handgunData;
				return null;
			}
		};
	}

	public short getBulletCount(ItemStack handgun) {
		return getBulletCount(handgun, null);
	}

	public short getBulletCount(ItemStack handgun, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			return data.getBulletCount();
		}
		return 0;
	}

	private IHandgunData getHandgunData(ItemStack handgun, EntityPlayer player) {
		if(player != null && player.isHandActive()) {
			EnumHand hand = EnumHand.OFF_HAND;
			if(player.getHeldItemMainhand() == handgun)
				hand = EnumHand.MAIN_HAND;

			return player.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, hand == EnumHand.MAIN_HAND ? EnumFacing.EAST : EnumFacing.WEST);
		}

		return handgun.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, null);
	}

	public void setBulletCount(ItemStack handgun, short bulletCount, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			data.setBulletCount(bulletCount);
		}
	}

	public short getBulletType(ItemStack handgun) {
		return getBulletType(handgun, null);
	}

	public short getBulletType(ItemStack handgun, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			return data.getBulletType();
		}
		return 0;
	}

	public void setBulletType(ItemStack handgun, short bulletType) {
		setBulletType(handgun, bulletType, null);
	}

	public void setBulletType(ItemStack handgun, short bulletType, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			data.setBulletType(bulletType);
		}
	}

	public boolean isInCooldown(ItemStack handgun) {
		return isInCooldown(handgun, null);
	}

	public boolean isInCooldown(ItemStack handgun, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			return data.isInCoolDown();
		}
		return false;
	}

	public void setInCooldown(ItemStack handgun, boolean inCooldown) {
		setInCooldown(handgun, inCooldown, null);
	}

	public void setInCooldown(ItemStack handgun, boolean inCooldown, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			data.setInCoolDown(inCooldown);
		}
	}

	public long getCooldown(ItemStack handgun) {
		return getCooldown(handgun, null);
	}

	public long getCooldown(ItemStack handgun, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			return data.getCoolDownTime();
		}
		return 0;
	}

	public void setCooldown(ItemStack handgun, long coolDownTime, EntityPlayer player) {
		IHandgunData data = getHandgunData(handgun, player);

		if(data != null) {
			data.setCoolDownTime(coolDownTime);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if(oldStack == null || newStack == null)
			return true;

		if(oldStack.getItem() == ModItems.handgun && newStack.getItem() == ModItems.handgun)
			return false;

		return true;
	}

	@Override
	public void onUpdate(ItemStack handgun, World worldObj, Entity entity, int slotNumber, boolean isSelected) {
		if(worldObj.isRemote)
			return;

		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		if(isInCooldown(handgun, player) && (isCooldownOver(worldObj, handgun, player) || !isValidCooldownTime(worldObj, handgun, player))) {
			setInCooldown(handgun, false, player);
		}

		if(handgun == player.getHeldItemMainhand()) {
			if (player.isHandActive()) {
				PacketHandler.networkWrapper.sendTo(new PacketPlayerHandgunDataSync(EnumHand.MAIN_HAND, getBulletCount(handgun, player), getBulletType(handgun, player), getCooldown(handgun, player), isInCooldown(handgun, player)), (EntityPlayerMP) player);
			} else {
				PacketHandler.networkWrapper.sendTo(new PacketHandgunDataSync(EnumHand.MAIN_HAND, getBulletCount(handgun), getBulletType(handgun)), (EntityPlayerMP) player);
			}
		} else if(handgun == player.getHeldItemOffhand()) {
			if (player.isHandActive()) {
				PacketHandler.networkWrapper.sendTo(new PacketPlayerHandgunDataSync(EnumHand.OFF_HAND, getBulletCount(handgun, player), getBulletType(handgun, player), getCooldown(handgun, player), isInCooldown(handgun, player)), (EntityPlayerMP) player);
			} else {
				PacketHandler.networkWrapper.sendTo(new PacketHandgunDataSync(EnumHand.OFF_HAND, getBulletCount(handgun), getBulletType(handgun)), (EntityPlayerMP) player);
			}
		}
	}

	private boolean isCooldownOver(World worldObj, ItemStack handgun) {
		return isCooldownOver(worldObj, handgun, null);
	}

	private boolean isCooldownOver(World worldObj, ItemStack handgun, EntityPlayer player) {
		return getCooldown(handgun, player) < worldObj.getWorldTime() && worldObj.getWorldTime() - getCooldown(handgun, player) < 12000;
	}

	private boolean isValidCooldownTime(World worldObj, ItemStack handgun, EntityPlayer player) {
		return Math.min(Math.abs(worldObj.getWorldTime() - getCooldown(handgun, player)), Math.abs(worldObj.getWorldTime() - 23999 - getCooldown(handgun, player)))
				<= Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Math.max(Reference.HANDGUN_COOLDOWN_SKILL_OFFSET, Reference.HANDGUN_RELOAD_SKILL_OFFSET);
	}

	private boolean secondHandgunCooledEnough(World world, EntityPlayer player, EnumHand hand) {
		ItemStack secondHandgun;

		if(hand == EnumHand.MAIN_HAND) {
			secondHandgun = player.getHeldItemOffhand();
		} else {
			secondHandgun = player.getHeldItemMainhand();
		}
		if(!isInCooldown(secondHandgun, player))
			return true;

		if((getCooldown(secondHandgun, player) - world.getWorldTime()) < (getPlayerReloadDelay(player) / 2))
			return true;

		return false;
	}

	private boolean hasHandgunInSecondHand(EntityPlayer player, EnumHand hand) {
		if(hand == EnumHand.MAIN_HAND)
			return player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == ModItems.handgun;

		return (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == ModItems.handgun);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack handgun, World worldObj, EntityPlayer player, EnumHand hand) {
		if (worldObj.isRemote)
			return new ActionResult<>(EnumActionResult.PASS, handgun);

		if((hasFilledMagazine(player) && getBulletCount(handgun) == 0) || (getBulletCount(handgun) > 0)) {
			player.setActiveHand(hand);

			copyDataToPlayer(handgun, player);

			if (getBulletCount(handgun, player) <= 0)
				setReloadCooldown(handgun, player);

			return new ActionResult<>(EnumActionResult.SUCCESS, handgun);
		}
		return new ActionResult<>(EnumActionResult.PASS, handgun);
	}

	@Override
	public void onUsingTick(ItemStack handgun, EntityLivingBase entity, int unadjustedCount) {
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		//you can't reload if you don't have any full mags left, so the rest of the method doesn't fire at all.
		if(getBulletCount(handgun, player) <= 0 && !isInCooldown(handgun, player) && hasFilledMagazine(player)) {
			setCooldown(handgun, player.worldObj.getWorldTime() + 12, player);
			setInCooldown(handgun, true, player);
			setBulletType(handgun, getMagazineTypeAndRemoveOne(player), player);
			if(getBulletType(handgun, player) != 0) {
				player.swingArm(player.getActiveHand());
				this.spawnEmptyMagazine(player);
				setBulletCount(handgun, (short) 8, player);
				player.worldObj.playSound(null, player.getPosition(), ModSounds.xload, SoundCategory.PLAYERS, 0.25F, 1.0F);
			}
			return;
		}

		//loaded and ready to fire
		if(!isInCooldown(handgun, player) && getBulletCount(handgun, player) > 0 && (!hasHandgunInSecondHand(player, player.getActiveHand()) || secondHandgunCooledEnough(player.worldObj, player, player.getActiveHand()))) {
			setCooldown(handgun, player.worldObj.getWorldTime() + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_COOLDOWN_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM), player);
			setInCooldown(handgun, true, player);

			fireBullet(handgun, player.worldObj, player, handgun == player.getHeldItemMainhand() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

			if (getBulletCount(handgun, player) <= 0) {
				setReloadCooldown(handgun, player);
			}
		}
	}

	private void setReloadCooldown(ItemStack handgun, EntityPlayer player) {
		setCooldown(handgun, player.worldObj.getWorldTime() + getPlayerReloadDelay(player), player);
		setInCooldown(handgun, true, player);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack handgun, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (worldIn.isRemote)
			return;

		if(!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		copyDataFromPlayer(handgun, player);

		if(getBulletCount(handgun) == 0) {
			setBulletType(handgun, (short) 0);
		}
	}

	private void copyDataToPlayer(ItemStack handgun, EntityPlayer player) {
		if (player.worldObj.isRemote)
			return;

		IHandgunData handgunData = handgun.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, null);

		if(handgunData != null) {
			EnumFacing facing = EnumFacing.WEST;

			if(player.getHeldItemMainhand() == handgun) {
				facing = EnumFacing.EAST;
			}

			IHandgunData playerData = player.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, facing);

			if(playerData != null) {
				playerData.deserializeNBT(handgunData.serializeNBT());
			}
		}

	}

	private void copyDataFromPlayer(ItemStack handgun, EntityPlayer player) {
		if (player.worldObj.isRemote)
			return;

		IHandgunData handgunData = handgun.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, null);

		if(handgunData != null) {
			EnumFacing facing = EnumFacing.WEST;

			if(player.getHeldItemMainhand() == handgun) {
				facing = EnumFacing.EAST;
			}

			IHandgunData playerData = player.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, facing);

			if(playerData != null) {
				handgunData.deserializeNBT(playerData.serializeNBT());
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack handgun) {
		return 6000;
	}

	private void fireBullet(ItemStack handgun, World worldObj, EntityPlayer player, EnumHand hand) {
		if(!worldObj.isRemote) {
			switch(getBulletType(handgun, player)) {
				case 0:
					return;
				case Reference.NEUTRAL_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityNeutralShot(worldObj, player, hand));
					break;
				case Reference.EXORCISM_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityExorcismShot(worldObj, player, hand));
					break;
				case Reference.BLAZE_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityBlazeShot(worldObj, player, hand));
					break;
				case Reference.ENDER_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityEnderShot(worldObj, player, hand));
					break;
				case Reference.CONCUSSIVE_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityConcussiveShot(worldObj, player, hand));
					break;
				case Reference.BUSTER_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityBusterShot(worldObj, player, hand));
					break;
				case Reference.SEEKER_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntitySeekerShot(worldObj, player, hand));
					break;
				case Reference.SAND_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntitySandShot(worldObj, player, hand));
					break;
				case Reference.STORM_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityStormShot(worldObj, player, hand));
					break;
			}

			worldObj.playSound(null, player.getPosition(), ModSounds.xshot, SoundCategory.PLAYERS, 0.5F, 1.2F);

			setBulletCount(handgun, (short) (getBulletCount(handgun, player) - 1), player);
			if(getBulletCount(handgun, player) == 0) {
				setBulletType(handgun, (short) 0, player);
			}
			spawnCasing(player);
		}
	}

	private void spawnEmptyMagazine(EntityPlayer player) {
		if(!player.inventory.addItemStackToInventory(new ItemStack(ModItems.magazine, 1, 0))) {
			player.entityDropItem(new ItemStack(ModItems.magazine, 1, 0), 0.1F);
		}
	}

	private void spawnCasing(EntityPlayer player) {
		if(!player.inventory.addItemStackToInventory(new ItemStack(ModItems.bullet, 1, 0))) {
			player.entityDropItem(new ItemStack(ModItems.bullet, 1, 0), 0.1F);
		}
	}

	private boolean hasFilledMagazine(EntityPlayer player) {
		for(ItemStack ist : player.inventory.mainInventory) {
			if(ist == null) {
				continue;
			}
			if(ist.getItem() == ModItems.magazine && ist.getItemDamage() != 0)
				return true;
		}
		return false;
	}

	private short getMagazineTypeAndRemoveOne(EntityPlayer player) {
		short bulletFound = 0;
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if(player.inventory.mainInventory[slot].getItem() == ModItems.magazine && player.inventory.mainInventory[slot].getItemDamage() != 0) {
				bulletFound = (short) player.inventory.mainInventory[slot].getItemDamage();
				player.inventory.decrStackSize(slot, 1);
				return bulletFound;
			}
		}
		return bulletFound;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	private int getPlayerReloadDelay(EntityPlayer player) {
		return Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM);
	}

}
