package xreliquary.client.particle;

import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCauldronSteamFX extends ParticleRedstone {

	public EntityCauldronSteamFX(World world, double x, double y, double z, double yMot, float red, float green, float blue) {
		this(world, x, y, z, red, green, blue);
		this.motionX += 0D;
		this.motionY += yMot;
		this.motionZ += 0D;
	}

	private EntityCauldronSteamFX(World world, double x, double y, double z, float red, float green, float blue) {
		super(world, x, y, z, 1.0F, red, green, blue);
		this.particleRed = red;
		this.particleGreen = green;
		this.particleBlue = blue;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		this.setAlphaF((1F - ((float) this.particleAge / (float) this.particleMaxAge)) / 2F);
		this.move(this.motionX, this.motionY, this.motionZ);

		if(this.posY == this.prevPosY) {
			this.motionX *= 1.1D;
			this.motionZ *= 1.1D;
		}

		this.motionX *= 0.9599999785423279D;
		this.motionY *= 0.9599999785423279D;
		this.motionZ *= 0.9599999785423279D;

		if(this.isCollided) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
	}
}
