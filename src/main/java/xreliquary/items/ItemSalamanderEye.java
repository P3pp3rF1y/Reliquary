package xreliquary.items;

import java.util.Iterator;
import java.util.List;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ContentInit
public class ItemSalamanderEye extends ItemBase {

	public ItemSalamanderEye() {
		super(Names.salamander_eye);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

    protected ItemSalamanderEye(String name) {
        super(name);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack, int pass) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
	}

	@Override
	public IIcon getIcon(ItemStack itemStack, int renderPass) {
			return this.itemIcon;
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public int getColorFromItemStack(ItemStack
	 * itemStack, int renderPass) { if (renderPass == 0) return
	 * Integer.parseInt(Constants.WHITE, 16); else { return getColor(itemStack);
	 * } }
	 * 
	 * public int getColor(ItemStack itemStack) {
	 * 
	 * switch (itemStack.getItemDamage()) { case (1): return
	 * Integer.parseInt(Constants.GRAY); case (2): return
	 * Integer.parseInt(Constants.YELLOW); case (3): return
	 * Integer.parseInt(Constants.RED); case (4): return
	 * Integer.parseInt(Constants.PURPLE); case (5): return
	 * Integer.parseInt(Constants.GREEN); case (6): return
	 * Integer.parseInt(Constants.DARK_GRAY); case (7): return
	 * Integer.parseInt(Constants.TEAL); case (8): return
	 * Integer.parseInt(Constants.BEIGE); case (9): return
	 * Integer.parseInt(Constants.BLUE); } return
	 * Integer.parseInt(Constants.WHITE); }
	 */

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		// handleEyeEffect(ist);
		if (!(e instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) e;
		if (player.getCurrentEquippedItem() == null)
			return;
		if (player.getCurrentEquippedItem().getItem() instanceof ItemSalamanderEye) {
			doFireballEffect(player);
			doExtinguishEffect(player);
		}
	}

	private void doExtinguishEffect(EntityPlayer player) {
		if (player.isBurning()) {
			player.extinguish();
		}
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		for (int xOff = -3; xOff <= 3; xOff++) {
			for (int yOff = -3; yOff <= 3; yOff++) {
				for (int zOff = -3; zOff <= 3; zOff++)
					if (ContentHelper.getIdent(player.worldObj.getBlock(x + xOff, y + yOff, z + zOff)).equals(ContentHelper.getIdent(Blocks.fire))) {
						player.worldObj.setBlock(x + xOff, y + yOff, z + zOff, Blocks.air);
						player.worldObj.playSoundEffect(x + xOff + 0.5D, y + yOff + 0.5D, z + zOff + 0.5D, "random.fizz", 0.5F, 2.6F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8F);
					}
			}
		}
	}

	private void doFireballEffect(EntityPlayer player) {
		List ghastFireballs = player.worldObj.getEntitiesWithinAABB(EntityLargeFireball.class, AxisAlignedBB.getBoundingBox(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5));
		Iterator fire1 = ghastFireballs.iterator();
		while (fire1.hasNext()) {
			EntityLargeFireball fireball = (EntityLargeFireball) fire1.next();
			if (player.getDistanceToEntity(fireball) < 4) {
				fireball.setDead();
			}
			fireball.attackEntityFrom(DamageSource.causePlayerDamage(player), 1);
			player.worldObj.playSoundEffect(fireball.posX, fireball.posY, fireball.posZ, "random.fizz", 0.5F, 2.6F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8F);
		}
		List blazeFireballs = player.worldObj.getEntitiesWithinAABB(EntitySmallFireball.class, AxisAlignedBB.getBoundingBox(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));
		Iterator fire2 = blazeFireballs.iterator();
		while (fire2.hasNext()) {
			EntitySmallFireball fireball = (EntitySmallFireball) fire2.next();
			for (int particles = 0; particles < 4; particles++) {
				player.worldObj.spawnParticle("reddust", fireball.posX, fireball.posY, fireball.posZ, 0.0D, 1.0D, 1.0D);
			}
			player.worldObj.playSoundEffect(fireball.posX, fireball.posY, fireball.posZ, "random.fizz", 0.5F, 2.6F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8F);
			fireball.setDead();
		}
	}
}
