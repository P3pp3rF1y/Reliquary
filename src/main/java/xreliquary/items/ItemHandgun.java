package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.shot.*;
import xreliquary.init.ModItems;
import xreliquary.init.ModSounds;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.util.NBTHelper;

public class ItemHandgun extends ItemBase {

	public ItemHandgun() {
		super(Names.handgun);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	public int getBulletCount(ItemStack ist) {
		return NBTHelper.getShort("bulletCount", ist);
	}

	public void setBulletCount(ItemStack ist, int i) {
		NBTHelper.setShort("bulletCount", ist, i);
	}

	public int getBulletType(ItemStack ist) {
		return NBTHelper.getShort("bulletType", ist);
	}

	public void setBulletType(ItemStack ist, int i) {
		NBTHelper.setShort("bulletType", ist, i);
	}

	public void setLastFiredShotType(ItemStack ist, int i) {
		NBTHelper.setShort("lastFiredShot", ist, i);
	}

	public boolean isInCooldown(ItemStack ist) {
		return NBTHelper.getBoolean("inCooldown", ist);
	}

	public void setInCooldown(ItemStack ist, boolean inCooldown) {
		NBTHelper.setBoolean("inCooldown", ist, inCooldown);
	}

	public long getCooldown(ItemStack ist) {
		return NBTHelper.getLong("cooldownTime", ist);
	}

	public void setCooldown(ItemStack ist, long i) {
		NBTHelper.setLong("cooldownTime", ist, i);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		if (getBulletCount(stack)> 0)
			return EnumAction.NONE;
		else
			return EnumAction.BLOCK;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (oldStack == null || newStack == null)
			return true;

		if (oldStack.getItem() == ModItems.handgun && newStack.getItem() == ModItems.handgun)
			return false;

		return true;
	}

	@Override
	public void onUpdate(ItemStack ist, World worldObj, Entity e, int i, boolean flag) {
		if(!worldObj.isRemote) {
			if(isInCooldown(ist) && (isCooldownOver(worldObj, ist) || !isValidCooldownTime(worldObj, ist))) {
				setInCooldown(ist, false);
			}
		}
	}

	private boolean isCooldownOver(World worldObj, ItemStack ist) {
		return getCooldown(ist) < worldObj.getWorldTime() && worldObj.getWorldTime() - getCooldown(ist) < 12000;
	}

	private boolean isValidCooldownTime(World worldObj, ItemStack ist) {
		return Math.min(Math.abs(worldObj.getWorldTime() - getCooldown(ist)), Math.abs(worldObj.getWorldTime() - 23999 - getCooldown(ist))) <= getMaxItemUseDuration(ist);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World worldObj, EntityPlayer player, EnumHand hand) {
		if(!isInCooldown(ist)) {
			if(!(getBulletCount(ist) > 0) && !(getBulletType(ist) > 0)) {
				player.setActiveHand(hand);
			} else {
				if(!worldObj.isRemote) {
					setCooldown(ist, worldObj.getWorldTime() + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + Reference.HANDGUN_COOLDOWN_SKILL_OFFSET - Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM));
					setInCooldown(ist, true);

					fireBullet(ist, worldObj, player);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, ist);
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int unadjustedCount) {
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
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return this.getItemUseDuration();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		if(!hasFilledMagazine(player)) {
			//arbitrary "feels good" cooldown for after the reload - this one just plays so you can't "fail" at reloading too fast.
			setCooldown(stack, player.worldObj.getWorldTime() + 12);
			setInCooldown(stack, true);
			return;
		}

		int maxUseOffset = getItemUseDuration() - getPlayerReloadDelay(player);
		int actualCount = timeLeft - maxUseOffset;

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(stack, player.worldObj.getWorldTime() + 12);
		setInCooldown(stack, true);
		setBulletType(stack, getMagazineTypeAndRemoveOne(player));
		if(getBulletType(stack) != 0) {
			player.swingArm(player.getActiveHand());
			this.spawnEmptyMagazine(player);
			setBulletCount(stack, 8);
			player.worldObj.playSound(null, player.getPosition(), ModSounds.xload, SoundCategory.PLAYERS, 0.25F, 1.0F);
		}
		if(getBulletCount(stack) == 0) {
			setBulletType(stack, 0);
		}
	}

	private int getItemUseDuration() {
		return Reference.HANDGUN_RELOAD_SKILL_OFFSET + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM;
	}

	private void fireBullet(ItemStack ist, World worldObj, EntityPlayer player) {
		if(!worldObj.isRemote) {
			switch(getBulletType(ist)) {
				case 0:
					return;
				case Reference.NEUTRAL_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityNeutralShot(worldObj, player));
					break;
				case Reference.EXORCISM_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityExorcismShot(worldObj, player));
					break;
				case Reference.BLAZE_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityBlazeShot(worldObj, player));
					break;
				case Reference.ENDER_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityEnderShot(worldObj, player));
					break;
				case Reference.CONCUSSIVE_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityConcussiveShot(worldObj, player));
					break;
				case Reference.BUSTER_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityBusterShot(worldObj, player));
					break;
				case Reference.SEEKER_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntitySeekerShot(worldObj, player));
					break;
				case Reference.SAND_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntitySandShot(worldObj, player));
					break;
				case Reference.STORM_SHOT_INDEX:
					worldObj.spawnEntityInWorld(new EntityStormShot(worldObj, player));
					break;
			}

			worldObj.playSound(null, player.getPosition(), ModSounds.xshot, SoundCategory.PLAYERS, 0.2F, 1.2F);

			//prevents the gun from forgetting that it fired a certain type of shot.
			setLastFiredShotType(ist, getBulletType(ist));

			setBulletCount(ist, getBulletCount(ist) - 1);
			if(getBulletCount(ist) == 0) {
				setBulletType(ist, 0);
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

	private int getMagazineTypeAndRemoveOne(EntityPlayer player) {
		int bulletFound = 0;
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if(player.inventory.mainInventory[slot].getItem() == ModItems.magazine && player.inventory.mainInventory[slot].getItemDamage() != 0) {
				bulletFound = player.inventory.mainInventory[slot].getItemDamage();
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
