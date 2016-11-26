package xreliquary.entities.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Colors;

public class EntityAttractionPotion extends EntityThrownPotion {
	@SuppressWarnings("unused")
	public EntityAttractionPotion(World world) {
		super(world);
	}

	public EntityAttractionPotion(World world, EntityPlayer player) {
		super(world, player);
	}

	public EntityAttractionPotion(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	boolean hasLivingEntityEffect() {
		return true;
	}

	@Override
	void doGroundSplashEffect() {
		// none
	}

	@Override
	void doLivingSplashEffect(EntityLivingBase el) {
		if(!(el instanceof EntityAnimal))
			return;
		EntityAnimal e = (EntityAnimal) el;
		if(e.getGrowingAge() != 0)
			return;
		if (this.getThrower() instanceof EntityPlayer) {
			e.setInLove((EntityPlayer) this.getThrower());
		}

		for(int var3 = 0; var3 < 7; ++var3) {
			double var4 = rand.nextGaussian() * 0.02D;
			double var6 = rand.nextGaussian() * 0.02D;
			double var8 = rand.nextGaussian() * 0.02D;
			world.spawnParticle(EnumParticleTypes.HEART, e.posX + rand.nextFloat() * e.width * 2.0F - e.width, e.posY + 0.5D + rand.nextFloat() * e.height, e.posZ + rand.nextFloat() * e.width * 2.0F - e.width, var4, var6, var8);
		}
	}

	@Override
	int getColor() {
		return Colors.get(Colors.RED);
	}

}
