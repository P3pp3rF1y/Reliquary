package xreliquary.items;

import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import xreliquary.entities.EntitySpecialSnowball;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIceRod extends ItemWithCapacity {
	protected ItemIceRod(int par1) {
		super(par1);
		this.setUnlocalizedName(Names.ICE_ROD_NAME);
		this.DEFAULT_TARGET_ITEM = new ItemStack(Item.snowball, 1, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	protected boolean isActive(ItemStack ist) {
		return true;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List infoList, boolean par4) {
		infoList.add("Consumes snowballs in inventory.");
		infoList.add("Makes snowballs a bit colder...");
		super.addInformation(ist, player, infoList, par4);
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		if (world.isRemote) return ist;
		if (ist.getItemDamage() == 0) return ist;
		if (ist.getItemDamage() < ist.getMaxDamage() - 1) {
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			world.spawnEntityInWorld(new EntitySpecialSnowball(world, player));
			decreaseQuantity(ist);
		}
		return ist;
	}

	@Override
	public boolean hitEntity(ItemStack ist, EntityLiving struckEntity, EntityLiving strikingEntity) {
		// it costs a snowball, but striking with the ice rod will cause
		// heavy slowdown and do decent damage vs. Fire immune
		if (ist.getItemDamage() + 1 < ist.getMaxDamage()) {
			if (struckEntity instanceof EntityLiving) {
				EntityLiving e = struckEntity;
				e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
			}
			if (strikingEntity instanceof EntityPlayer) {
				struckEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)strikingEntity), struckEntity.isImmuneToFire() ? 10 : 4);
			}
			decreaseQuantity(ist);
		}
		return true;
	}
}
