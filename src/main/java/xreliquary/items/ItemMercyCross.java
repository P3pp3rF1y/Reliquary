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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
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
	public float getDamageVsEntity(Entity par1Entity, ItemStack stack) {
		if (isUndead(par1Entity)) {
			par1Entity.worldObj.spawnParticle("largeexplode", par1Entity.posX, par1Entity.posY + par1Entity.height / 2, par1Entity.posZ, 0.0F, 0.0F, 0.0F);
		}
		return isUndead(par1Entity) ? super.getDamageVsEntity(par1Entity, stack) * 2 : super.getDamageVsEntity(par1Entity, stack) * 1;
	}

	private boolean isUndead(Entity mop) {
		return mop instanceof EntitySkeleton || mop instanceof EntityGhast || mop instanceof EntityWither || mop instanceof EntityZombie || mop instanceof EntityPigZombie;
	}

	/**
	 * Returns the strength of the stack against a given block. 1.0F base,
	 * (Quality+1)*2 if correct blocktype, 1.5F if sword
	 */
	@Override
	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
		return 1.5F;
	}

	/**
	 * Current implementations of this method in child classes do not use the
	 * entry argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
		par1ItemStack.damageItem(1, par3EntityLivingBase);
		return true;
	}
}
