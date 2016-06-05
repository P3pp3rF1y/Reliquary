package xreliquary.items.util.handgun;

import net.minecraft.nbt.NBTTagCompound;

public class HandgunData implements IHandgunData {
	private boolean inCoolDown;
	private long coolDownTime;
	private short bulletCount;
	private short bulletType;

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("inCoolDown", inCoolDown);
		compound.setLong("coolDownTime", coolDownTime);
		compound.setShort("bulletCount", bulletCount);
		compound.setShort("bulletType", bulletType);

		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		inCoolDown = nbt.getBoolean("inCoolDown");
		coolDownTime = nbt.getLong("coolDownTime");
		bulletCount = nbt.getShort("bulletCount");
		bulletType = nbt.getShort("bulletType");
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
}
