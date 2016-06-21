package xreliquary.items.util.handgun;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IHandgunData extends INBTSerializable<NBTTagCompound> {
	boolean isInCoolDown();
	void setInCoolDown(boolean inCoolDown);
	long getCoolDownTime();
	void setCoolDownTime(long time);
	short getBulletCount();
	void setBulletCount(short count);
	short getBulletType();
	void setBulletType(short type);
}
