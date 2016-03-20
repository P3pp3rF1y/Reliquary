package xreliquary.client.particle;

import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCauldronSteamFX extends EntityReddustFX {
	float reddustParticleScale;
	private static final String __OBFID = "CL_00000923";

	public EntityCauldronSteamFX(World world, double x, double y, double z, double xMot, double yMot, double zMot, float red, float green, float blue) {
		this(world, x, y, z, 1.0F, red, green, blue);
		this.xSpeed += xMot;
		this.ySpeed += yMot;
		this.zSpeed += zMot;
	}

	public EntityCauldronSteamFX(World world, double x, double y, double z, float sizeAndLifespanLoL, float red, float green, float blue) {
		super(world, x, y, z, sizeAndLifespanLoL, red, green, blue);
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
		this.moveEntity(this.xSpeed, this.ySpeed, this.zSpeed);

		if(this.posY == this.prevPosY) {
			this.xSpeed *= 1.1D;
			this.zSpeed *= 1.1D;
		}

		this.xSpeed *= 0.9599999785423279D;
		this.ySpeed *= 0.9599999785423279D;
		this.zSpeed *= 0.9599999785423279D;

		if(this.isCollided) {
			this.xSpeed *= 0.699999988079071D;
			this.zSpeed *= 0.699999988079071D;
		}
	}
}