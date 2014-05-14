package xreliquary.entities;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.*;
public class EntityEnderStaffProjectile extends EntityEnderPearl {
    public EntityEnderStaffProjectile(World world)
    {
        super(world);
    }

    public EntityEnderStaffProjectile(World world, EntityPlayer entityPlayer)
    {
        super(world, entityPlayer);
    }

    public EntityEnderStaffProjectile(World world, double d, double d1, double d2)
    {
        super(world, d, d1, d2);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        // flies slightly farther than a normal projectile;
        //stolen from the "special" snowball.
        return 0.01F;
    }

    protected void onThrowableCollision(MovingObjectPosition movingobjectposition)
    {
        if (movingobjectposition.entityHit != null)
        {
            if (!movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0));
        }
        for (int i = 0; i < 32; i++)
        {
            worldObj.spawnParticle("portal", posX, posY + rand.nextDouble() * 2D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
        }

        if (!worldObj.isRemote)
        {
            //zombies are too stupid to bend the fabric of space and time.
            if (this.getThrower() != null && getThrower() instanceof EntityPlayer)
            {
                //lazy teleportation. This should more appropriately do a quick scan for "safe" positions to place a player.
                //for now I'm operating under the [incorrect] assumption that the pearl will always land somewhere safe
                //and the player needs to learn to teleport properly if they wind up crushing themselves.
                getThrower().setPositionAndUpdate(this.posX, this.posY, this.posZ);
                //allows you to defy whatever gravity you were being affected by whilst casting the ender pearl.
                //doing so in midair "cheats" physics and prevents you from taking some (if not all) of the fall damage.
                getThrower().fallDistance = 0.0F;
            }
            this.setDead();
        }
    }
}
