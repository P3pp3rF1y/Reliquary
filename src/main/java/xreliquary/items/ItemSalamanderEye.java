package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemSalamanderEye extends ItemBase {

	public ItemSalamanderEye() {
		super(Names.Items.SALAMANDER_EYE);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if(world.isRemote || !(e instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) e;

		if(player.getHeldItem(EnumHand.MAIN_HAND).getItem() == this || player.getHeldItem(EnumHand.OFF_HAND).getItem() == this) {
			doFireballEffect(player);
			doExtinguishEffect(player);
		}
	}

	private void doExtinguishEffect(EntityPlayer player) {
		if(player.isBurning()) {
			player.extinguish();
		}
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		for(int xOff = -3; xOff <= 3; xOff++) {
			for(int yOff = -3; yOff <= 3; yOff++) {
				for(int zOff = -3; zOff <= 3; zOff++)
					if(player.world.getBlockState(new BlockPos(x + xOff, y + yOff, z + zOff)).getBlock() == Blocks.FIRE) {
						player.world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), Blocks.AIR.getDefaultState());
						player.world.playSound(x + xOff + 0.5D, y + yOff + 0.5D, z + zOff + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.8F, false);
					}
			}
		}
	}

	private void doFireballEffect(EntityPlayer player) {
		List<EntityLargeFireball> ghastFireballs = player.world.getEntitiesWithinAABB(EntityLargeFireball.class, new AxisAlignedBB(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5));
		for(EntityLargeFireball fireball : ghastFireballs) {
			if(player.getDistanceToEntity(fireball) < 4) {
				fireball.setDead();
			}
			fireball.attackEntityFrom(DamageSource.causePlayerDamage(player), 1);
			player.world.playSound(fireball.posX, fireball.posY, fireball.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.8F, false);
		}
		List<EntitySmallFireball> blazeFireballs = player.world.getEntitiesWithinAABB(EntitySmallFireball.class, new AxisAlignedBB(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));
		for(EntitySmallFireball fireball : blazeFireballs) {
			for(int particles = 0; particles < 4; particles++) {
				player.world.spawnParticle(EnumParticleTypes.REDSTONE, fireball.posX, fireball.posY, fireball.posZ, 0.0D, 1.0D, 1.0D);
			}
			player.world.playSound(fireball.posX, fireball.posY, fireball.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 0.5F, 2.6F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.8F, false);
			fireball.setDead();
		}
	}
}
