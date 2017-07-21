package xreliquary.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xreliquary.init.ModPotions;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class PotionCure extends Potion {

	public PotionCure() {
		super(false, 15723850);
		this.setPotionName("xreliquary.potion.cure");
		this.setRegistryName(Reference.CURE);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	private static final Method START_CONVERTING = ReflectionHelper
			.findMethod(EntityZombieVillager.class, "startConverting", "func_190734_b", int.class);
	private static void startConverting(EntityZombieVillager zombieVillager, int conversionTime) {
		try {
			START_CONVERTING.invoke(zombieVillager, conversionTime);
		} catch (InvocationTargetException|IllegalAccessException e) {
			LogHelper.error("Error running startConverting on zombie villager", e);
		}
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entityLivingBase, int potency) {
		if (entityLivingBase instanceof EntityZombieVillager) {
			if (!((EntityZombieVillager) entityLivingBase).isConverting() && entityLivingBase.isPotionActive(MobEffects.WEAKNESS)) {
				startConverting((EntityZombieVillager) entityLivingBase, ((new Random()).nextInt(2401) + 3600) / (potency + 2));
				entityLivingBase.removePotionEffect(ModPotions.potionCure);
			}
		} else {
			entityLivingBase.removePotionEffect(ModPotions.potionCure);
		}
	}
}
