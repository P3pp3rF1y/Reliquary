package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCondensedFertility extends EntityThrownPotion {
	public EntityCondensedFertility(World par1World) {
		super(par1World);
	}

	public EntityCondensedFertility(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@SideOnly(Side.CLIENT)
	public EntityCondensedFertility(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityCondensedFertility(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	boolean hasLivingEntityEffect() {
		return false;
	}

	// fertility is one of the only potion that has this effect, the rest of
	// them will be mostly empty
	@Override
	void doGroundSplashEffect() {
		int x = (int) (posX + 0.5);
		int y = (int) (posY + 0.5);
		int z = (int) (posZ + 0.5);
		// applies bonemeal to every block it finds in a 3x3 area.
		for (int xD = -1; xD <= 1; xD++) {
			for (int yD = -2; yD <= 1; yD++) {
				for (int zD = -1; zD <= 1; zD++) {
					if (this.getThrower() instanceof EntityPlayer) {
						Items.dye.onItemUse(new ItemStack(Items.dye, 1, 15), (EntityPlayer) this.getThrower(), worldObj, x + xD, y + yD, z + zD, 1, 0F, 0F, 0F);
					}
				}
			}
		}
	}

	@Override
	void doLivingSplashEffect(EntityLiving e) {
		// overridden because fertility potion have no effect on living
		// entities.
	}

	@Override
	float getRed() {
		return 1F;
	}

	@Override
	float getGreen() {
		return 0F;
	}

	@Override
	float getBlue() {
		return 0F;
	}
}
