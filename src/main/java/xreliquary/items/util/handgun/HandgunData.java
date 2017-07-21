package xreliquary.items.util.handgun;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;

public class HandgunData implements IHandgunData {
	private boolean inCoolDown;
	private long coolDownTime;
	private short bulletCount;
	private short bulletType;
	private List<PotionEffect> potionEffects;

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("inCoolDown", inCoolDown);
		compound.setLong("coolDownTime", coolDownTime);
		compound.setShort("bulletCount", bulletCount);
		compound.setShort("bulletType", bulletType);
		XRPotionHelper.addPotionEffectsToCompoundTag(compound, potionEffects);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		inCoolDown = nbt.getBoolean("inCoolDown");
		coolDownTime = nbt.getLong("coolDownTime");
		bulletCount = nbt.getShort("bulletCount");
		bulletType = nbt.getShort("bulletType");
		potionEffects = XRPotionHelper.getPotionEffectsFromCompoundTag(nbt);
	}

	@Override
	public boolean isInCoolDown() {
		return inCoolDown;
	}

	@Override
	public void setInCoolDown(boolean inCoolDown) {
		this.inCoolDown = inCoolDown;
	}

	@Override
	public long getCoolDownTime() {
		return coolDownTime;
	}

	@Override
	public void setCoolDownTime(long time) {
		this.coolDownTime = time;
	}

	@Override
	public short getBulletCount() {
		return bulletCount;
	}

	@Override
	public void setBulletCount(short count) {
		this.bulletCount = count;
	}

	@Override
	public short getBulletType() {
		return bulletType;
	}

	@Override
	public void setBulletType(short type) {
		this.bulletType = type;
	}

	@Override
	public void setPotionEffects(List<PotionEffect> potionEffects) {
		this.potionEffects = potionEffects;
	}

	@Override
	public List<PotionEffect> getPotionEffects() {
		return potionEffects;
	}
}
