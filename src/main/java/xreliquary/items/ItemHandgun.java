package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
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
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.List;

public class ItemHandgun extends ItemBase {

	public ItemHandgun() {
		super(Names.Items.HANDGUN);
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
				return capability == ModCapabilities.HANDGUN_DATA_CAPABILITY;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if(capability == ModCapabilities.HANDGUN_DATA_CAPABILITY)
					//noinspection unchecked
					return (T) handgunData;
				return null;
			}
		};
	}

	public short getBulletCount(ItemStack handgun) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			return data.getBulletCount();
		}
		return 0;
	}

	private IHandgunData getHandgunData(ItemStack handgun) {
		return handgun.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, null);
	}

	public void setBulletCount(ItemStack handgun, short bulletCount) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			data.setBulletCount(bulletCount);
		}
	}

	public short getBulletType(ItemStack handgun) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			return data.getBulletType();
		}
		return 0;
	}

	public void setBulletType(ItemStack handgun, short bulletType) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			data.setBulletType(bulletType);
		}
	}

	public boolean isInCooldown(ItemStack handgun) {
		IHandgunData data = getHandgunData(handgun);

		return data != null && data.isInCoolDown();
	}

	public void setInCooldown(ItemStack handgun, boolean inCooldown) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			data.setInCoolDown(inCooldown);
		}
	}

	public long getCooldown(ItemStack handgun) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			return data.getCoolDownTime();
		}
		return 0;
	}

	public void setCooldown(ItemStack handgun, long coolDownTime) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			data.setCoolDownTime(coolDownTime);
		}
	}

	public List<PotionEffect> getPotionEffects(ItemStack handgun) {
		IHandgunData data = getHandgunData(handgun);
		if(data != null) {
			return data.getPotionEffects();
		}
		return null;
	}

	public void setPotionEffects(ItemStack handgun, List<PotionEffect> potionEffects) {
		IHandgunData data = getHandgunData(handgun);

		if(data != null) {
			data.setPotionEffects(potionEffects);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack handgun) {
		if(getBulletCount(handgun) > 0)
			return EnumAction.NONE;
		else
			return EnumAction.BLOCK;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack == null || newStack == null || !(oldStack.getItem() == ModItems.handgun && newStack.getItem() == ModItems.handgun);

	}

	@Override
	public void onUpdate(ItemStack handgun, World worldObj, Entity entity, int slotNumber, boolean isSelected) {
		if(worldObj.isRemote)
			return;

		if(isInCooldown(handgun) && (isCooldownOver(worldObj, handgun) || !isValidCooldownTime(worldObj, handgun))) {
			setInCooldown(handgun, false);
		}

		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		if(handgun == player.getHeldItemMainhand()) {
			PacketHandler.networkWrapper.sendTo(new PacketHandgunDataSync(EnumHand.MAIN_HAND, getBulletCount(handgun), getBulletType(handgun), isInCooldown(handgun), getCooldown(handgun), getPotionEffects(handgun)), (EntityPlayerMP) player);
		} else if(handgun == player.getHeldItemOffhand()) {
			PacketHandler.networkWrapper.sendTo(new PacketHandgunDataSync(EnumHand.OFF_HAND, getBulletCount(handgun), getBulletType(handgun), isInCooldown(handgun), getCooldown(handgun), getPotionEffects(handgun)), (EntityPlayerMP) player);
		}
	}

	private boolean isCooldownOver(World worldObj, ItemStack handgun) {
		return getCooldown(handgun) < worldObj.getWorldTime() && worldObj.getWorldTime() - getCooldown(handgun) < 12000;
	}

	private boolean isValidCooldownTime(World worldObj, ItemStack handgun) {
		return Math.min(Math.abs(worldObj.getWorldTime() - getCooldown(handgun)), Math.abs(worldObj.getWorldTime() - 23999 - getCooldown(handgun))) <= getMaxItemUseDuration(handgun);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack handgun, World worldObj, EntityPlayer player, EnumHand hand) {
		if((hasFilledMagazine(player) && getBulletCount(handgun) == 0) || (getBulletCount(handgun) > 0 && (!hasHandgunInSecondHand(player, hand) || cooledMoreThanSecondHandgun(handgun, player, hand)))) {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, handgun);
		}
		return new ActionResult<>(EnumActionResult.PASS, handgun);
	}

	private boolean cooledMoreThanSecondHandgun(ItemStack handgun, EntityPlayer player, EnumHand hand) {
		if(!isInCooldown(handgun))
			return true;

		if(hand == EnumHand.MAIN_HAND)
			return !isInCooldown(player.getHeldItemOffhand()) && getCooldown(handgun) < getCooldown(player.getHeldItemOffhand());
		else
			return !isInCooldown(player.getHeldItemMainhand()) && getCooldown(handgun) < getCooldown(player.getHeldItemMainhand());
	}

	private boolean secondHandgunCooledEnough(World world, EntityPlayer player, EnumHand hand) {
		ItemStack secondHandgun;

		if(hand == EnumHand.MAIN_HAND) {
			secondHandgun = player.getHeldItemOffhand();
		} else {
			secondHandgun = player.getHeldItemMainhand();
		}
		return !isInCooldown(secondHandgun) || (getCooldown(secondHandgun) - world.getWorldTime()) < (getPlayerReloadDelay(player) / 2);

	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean hasHandgunInSecondHand(EntityPlayer player, EnumHand hand) {
		if(hand == EnumHand.MAIN_HAND)
			return player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == ModItems.handgun;

		return (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == ModItems.handgun);
	}

	@Override
	public void onUsingTick(ItemStack handgun, EntityLivingBase entity, int unadjustedCount) {
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
		int actualCount = unadjustedCount - maxUseOffset;
		actualCount -= 1;

		//you can't reload if you don't have any full mags left, so the rest of the method doesn't fire at all.
		if(!hasFilledMagazine(player) || actualCount == 0) {
			player.stopActiveHand();
			return;
		}

		//loaded and ready to fire
		if(!isInCooldown(handgun) && getBulletCount(handgun) > 0 && (!hasHandgunInSecondHand(player, player.getActiveHand()) || secondHandgunCooledEnough(player.worldObj, player, player.getActiveHand()))) {
			player.stopActiveHand();
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack handgun) {
		return this.getItemUseDuration();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack handgun, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if(!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		// fire bullet
		if(getBulletCount(handgun) > 0) {
			if(!isInCooldown(handgun)) {
				setCooldown(handgun, worldIn.getWorldTime() + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_COOLDOWN_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM));
				setInCooldown(handgun, true);

				fireBullet(handgun, worldIn, player, handgun == player.getHeldItemMainhand() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
			}
			return;
		}

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(handgun, player.worldObj.getWorldTime() + 12);
		setInCooldown(handgun, true);

		int slot = getMagazineSlot(player);
		if (slot != -1) {
			ItemStack magazine = player.inventory.mainInventory[slot];
			setBulletType(handgun, (short) magazine.getMetadata());
			setPotionEffects(handgun, PotionUtils.getFullEffectsFromItem(magazine));
		}

		if(getBulletType(handgun) != 0) {
			player.swingArm(player.getActiveHand());
			this.spawnEmptyMagazine(player);
			setBulletCount(handgun, (short) 8);
			player.worldObj.playSound(null, player.getPosition(), ModSounds.xload, SoundCategory.PLAYERS, 0.25F, 1.0F);
		}
		if(getBulletCount(handgun) == 0) {
			setBulletType(handgun, (short) 0);
			setPotionEffects(handgun, null);
		}
	}

	private int getItemUseDuration() {
		return Reference.HANDGUN_RELOAD_SKILL_OFFSET + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM;
	}

	private void fireBullet(ItemStack handgun, World worldObj, EntityPlayer player, EnumHand hand) {
		if(!worldObj.isRemote) {
			switch(getBulletType(handgun)) {
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

			setBulletCount(handgun, (short) (getBulletCount(handgun) - 1));
			if(getBulletCount(handgun) == 0) {
				setBulletType(handgun, (short) 0);
				setPotionEffects(handgun, null);
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

	private short getMagazineTypeAndRemoveOne(EntityPlayer player, List<PotionEffect> effects) {
		short bulletFound = 0;
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if(player.inventory.mainInventory[slot].getItem() == ModItems.magazine && player.inventory.mainInventory[slot].getItemDamage() != 0) {
				ItemStack magazine = player.inventory.mainInventory[slot];
				bulletFound = (short) magazine.getItemDamage();
				if (ModItems.magazine.isPotionAttached(magazine))
					effects = PotionUtils.getEffectsFromStack(magazine);
				player.inventory.decrStackSize(slot, 1);
				return bulletFound;
			}
		}
		return bulletFound;
	}

	private int getMagazineSlot(EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if(player.inventory.mainInventory[slot].getItem() == ModItems.magazine && player.inventory.mainInventory[slot].getItemDamage() != 0) {
				return slot;
			}
		}
		return -1;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	private int getPlayerReloadDelay(EntityPlayer player) {
		return Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_RELOAD_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM);
	}
}
