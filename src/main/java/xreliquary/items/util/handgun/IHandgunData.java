package xreliquary.items.util.handgun;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IHandgunData extends INBTSerializable<NBTTagCompound> {
	boolean isInCoolDown();
	void setInCoolDown(boolean inCoolDown);
	long getCoolDownTime();
	void setCoolDownTime(long time);
	short getBulletCount();
	void setBulletCount(short count);
	short getBulletType();
	void setBulletType(short type);
	void setPotionEffects(List<PotionEffect> potionEffects);
	List<PotionEffect> getPotionEffects();
}
