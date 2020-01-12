package xreliquary.potions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xreliquary.init.ModPotions;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class PotionCure extends Effect {

	public PotionCure() {
		super(EffectType.BENEFICIAL, 15723850);
		setRegistryName(Reference.CURE);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	private static final Method START_CONVERTING = ObfuscationReflectionHelper
			.findMethod(ZombieVillagerEntity.class, "func_191991_a", UUID.class, int.class);

	private static void startConverting(ZombieVillagerEntity zombieVillager, int conversionTime) {
		try {
			START_CONVERTING.invoke(zombieVillager, null, conversionTime);
		}
		catch (InvocationTargetException | IllegalAccessException e) {
			LogHelper.error("Error running startConverting on zombie villager", e);
		}
	}

	@Override
	public void performEffect(LivingEntity entityLivingBase, int potency) {
		if (entityLivingBase instanceof ZombieVillagerEntity) {
			if (!((ZombieVillagerEntity) entityLivingBase).isConverting() && entityLivingBase.isPotionActive(Effects.WEAKNESS)) {
				startConverting((ZombieVillagerEntity) entityLivingBase, (entityLivingBase.world.rand.nextInt(2401) + 3600) / (potency + 2));
				entityLivingBase.removePotionEffect(ModPotions.potionCure);
			}
		} else {
			entityLivingBase.removePotionEffect(ModPotions.potionCure);
		}
	}
}
