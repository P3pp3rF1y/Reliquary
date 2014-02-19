package xreliquary.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.LanguageHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMercyCross extends ItemSword {

	public ItemMercyCross() {
		super(ToolMaterial.GOLD);
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		canRepair = true;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		GameRegistry.registerItem(this, Reference.MOD_ID + ":" + Names.CROSS_NAME);
		this.setUnlocalizedName(Names.CROSS_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {

		itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		String value = LanguageHelper.getLocalization("item." + Names.CROSS_NAME + ".tooltip");
		for (String descriptionLine : value.split(";")) {
			if (descriptionLine != null && descriptionLine.length() > 0)
				list.add(descriptionLine);
		}
	}

    @Override
    public float func_150931_i() {
        return 0.0F;
    }

	private boolean isUndead(Entity mop) {
		return mop instanceof EntitySkeleton || mop instanceof EntityGhast || mop instanceof EntityWither || mop instanceof EntityZombie || mop instanceof EntityPigZombie;
	}

	/**
	 * Returns the strength of the stack against a given block. 1.0F base,
	 * (Quality+1)*2 if correct blocktype, 1.5F if sword
	 */
    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return block == Blocks.web ? 15.0F : 1.5F;
    }

    // TODO: Test if this actually works.
	@Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase monster, EntityLivingBase player) {
        if (isUndead(monster)) {
            monster.worldObj.spawnParticle("largeexplode", monster.posX, monster.posY + monster.height / 2, monster.posZ, 0.0F, 0.0F, 0.0F);
            monster.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), super.func_150931_i());
        } else {
            monster.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), super.func_150931_i() * 2);
        }
		return true;
	}
}
