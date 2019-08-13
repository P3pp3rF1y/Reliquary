package xreliquary.potions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import xreliquary.reference.Reference;
import xreliquary.util.MobHelper;

import javax.annotation.Nonnull;

public class PotionPacification extends Potion {

	public PotionPacification() {
		super(false, 0);
		this.setPotionName("xreliquary.potion.pacification");
		this.setIconIndex(0, 0);
		this.setRegistryName(Reference.MOD_ID, "pacification_potion");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entityLivingBase, int p_76394_2_) {
		if (entityLivingBase.world.isRemote || !(entityLivingBase instanceof EntityLiving))
			return;

		EntityLiving entityLiving = (EntityLiving) entityLivingBase;

		if (entityLiving.getAttackTarget() != null || entityLiving.getRevengeTarget() != null)
			MobHelper.resetTarget(entityLiving, true, true);
	}
}
