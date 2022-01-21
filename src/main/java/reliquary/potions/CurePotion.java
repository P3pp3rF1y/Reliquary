package reliquary.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import reliquary.init.ModPotions;
import reliquary.util.LogHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class CurePotion extends MobEffect {

	public CurePotion() {
		super(MobEffectCategory.BENEFICIAL, 15723850);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	private static final Method START_CONVERTING = ObfuscationReflectionHelper
			.findMethod(ZombieVillager.class, "startConverting", UUID.class, int.class);

	private static void startConverting(ZombieVillager zombieVillager, int conversionTime) {
		try {
			START_CONVERTING.invoke(zombieVillager, null, conversionTime);
		}
		catch (InvocationTargetException | IllegalAccessException e) {
			LogHelper.error("Error running startConverting on zombie villager", e);
		}
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBase, int potency) {
		if (entityLivingBase instanceof ZombieVillager zombieVillager) {
			if (!zombieVillager.isConverting() && entityLivingBase.hasEffect(MobEffects.WEAKNESS)) {
				startConverting(zombieVillager, (entityLivingBase.level.random.nextInt(2401) + 3600) / (potency + 2));
				entityLivingBase.removeEffect(ModPotions.CURE_POTION.get());
			}
		} else {
			entityLivingBase.removeEffect(ModPotions.CURE_POTION.get());
		}
	}
}
