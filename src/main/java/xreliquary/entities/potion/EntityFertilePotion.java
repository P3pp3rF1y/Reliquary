package xreliquary.entities.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Colors;

public class EntityFertilePotion extends EntityThrownPotion {
	@SuppressWarnings("unused")
	public EntityFertilePotion(World world) {
		super(world);
	}

	public EntityFertilePotion(World world, EntityPlayer player) {
		super(world, player);
	}

	public EntityFertilePotion(World world, double x, double y, double z) {
		super(world, x, y, z);
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
		for(int xD = -1; xD <= 1; xD++) {
			for(int yD = -2; yD <= 1; yD++) {
				for(int zD = -1; zD <= 1; zD++) {
					ItemDye.applyBonemeal(new ItemStack(Items.DYE, 1, 15), world, new BlockPos(x + xD, y + yD, z + zD));
				}
			}
		}
	}

	@Override
	void doLivingSplashEffect(EntityLivingBase e) {
		// overridden because fertility potion have no effect on living
		// entities.
	}

	@Override
	int getColor() {
		return Colors.get(Colors.LIGHT_GRAY);
	}
}
