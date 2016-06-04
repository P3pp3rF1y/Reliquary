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

	public int getBulletCount(ItemStack handgun) {
		return NBTHelper.getShort("bulletCount", handgun);
	}

	public void setBulletCount(ItemStack handgun, int i) {
		NBTHelper.setShort("bulletCount", handgun, i);
	}

	public int getBulletType(ItemStack handgun) {
		return NBTHelper.getShort("bulletType", handgun);
	}

	public void setBulletType(ItemStack handgun, int i) {
		NBTHelper.setShort("bulletType", handgun, i);
	}

	public void setLastFiredShotType(ItemStack handgun, int i) {
		NBTHelper.setShort("lastFiredShot", handgun, i);
	}

	public boolean isInCooldown(ItemStack handgun) {
		return NBTHelper.getBoolean("inCooldown", handgun);
	}

	public void setInCooldown(ItemStack handgun, boolean inCooldown) {
		NBTHelper.setBoolean("inCooldown", handgun, inCooldown);
	}

	public long getCooldown(ItemStack ist) {
		return NBTHelper.getLong("cooldownTime", ist);
	}

	public void setCooldown(ItemStack ist, long i) {
		NBTHelper.setLong("cooldownTime", ist, i);
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
		if(oldStack == null || newStack == null)
			return true;

		if(oldStack.getItem() == ModItems.handgun && newStack.getItem() == ModItems.handgun)
			return false;

		return true;
	}

	@Override
	public void onUpdate(ItemStack handgun, World worldObj, Entity e, int i, boolean flag) {
		if(!worldObj.isRemote) {
			if(isInCooldown(handgun) && (isCooldownOver(worldObj, handgun) || !isValidCooldownTime(worldObj, handgun))) {
				setInCooldown(handgun, false);
			}
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
		if (!isInCooldown(handgun))
			return true;

		if (hand == EnumHand.MAIN_HAND)
			return getCooldown(handgun) < getCooldown(player.getHeldItemOffhand());
		else
			return getCooldown(handgun) < getCooldown(player.getHeldItemMainhand());
	}

	private boolean secondHandgunCooledEnough(World world, EntityPlayer player, EnumHand hand) {
		ItemStack secondHandgun;

		if(hand == EnumHand.MAIN_HAND) {
			secondHandgun = player.getHeldItemOffhand();
		} else {
			secondHandgun = player.getHeldItemMainhand();
		}
		if(!isInCooldown(secondHandgun))
			return true;

		if((getCooldown(secondHandgun) - world.getWorldTime()) < (getPlayerReloadDelay(player) / 2))
			return true;

		return false;
	}

	private boolean hasHandgunInSecondHand(EntityPlayer player, EnumHand hand) {
		if(hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == ModItems.handgun)
			return true;

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

				fireBullet(handgun, worldIn, player);
			}
			return;
		}

		//arbitrary "feels good" cooldown for after the reload - this is to prevent accidentally discharging the weapon immediately after reload.
		setCooldown(handgun, player.worldObj.getWorldTime() + 12);
		setInCooldown(handgun, true);
		setBulletType(handgun, getMagazineTypeAndRemoveOne(player));
		if(getBulletType(handgun) != 0) {
			player.swingArm(player.getActiveHand());
			this.spawnEmptyMagazine(player);
			setBulletCount(handgun, 8);
			player.worldObj.playSound(null, player.getPosition(), ModSounds.xload, SoundCategory.PLAYERS, 0.25F, 1.0F);
		}
		if(getBulletCount(handgun) == 0) {
			setBulletType(handgun, 0);
		}
	}

	private int getItemUseDuration() {
		return Reference.HANDGUN_RELOAD_SKILL_OFFSET + Reference.PLAYER_HANDGUN_SKILL_MAXIMUM;
	}

	private void fireBullet(ItemStack handgun, World worldObj, EntityPlayer player) {
		if(!worldObj.isRemote) {
			switch(getBulletType(handgun)) {
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
			setLastFiredShotType(handgun, getBulletType(handgun));

			setBulletCount(handgun, getBulletCount(handgun) - 1);
			if(getBulletCount(handgun) == 0) {
				setBulletType(handgun, 0);
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
