package xreliquary.items;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMercyCross extends ItemSword {

	public ItemMercyCross() {
		super(ToolMaterial.GOLD);
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		canRepair = true;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setUnlocalizedName(Names.Items.MERCY_CROSS);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void addInformation(ItemStack cross, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(cross) + ".tooltip", tooltip);
	}

	@Override
	public float getDamageVsEntity() {
		return 0.0F;
	}

	/**
	 * Returns the strength of the stack against a given block. 1.0F base,
	 * (Quality+1)*2 if correct blocktype, 1.5F if sword
	 */
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState blockState) {
		return blockState.getBlock() == Blocks.WEB ? 15.0F : 1.5F;
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if(equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) 6, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0));
		}

		return multimap;
	}

	public void updateAttackDamageModifier(EntityLivingBase target, EntityPlayer player) {
		double dmg = isUndead(target) ? 12 : 6;
		IAttributeInstance attackAttribute = player.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName());

		//noinspection ConstantConditions
		if(attackAttribute != null &&
				(attackAttribute.getModifier(ATTACK_DAMAGE_MODIFIER) == null || attackAttribute.getModifier(ATTACK_DAMAGE_MODIFIER).getAmount() != dmg)) {
			attackAttribute.removeModifier(ATTACK_DAMAGE_MODIFIER);
			attackAttribute.applyModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", dmg, 0));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity monster) {
		if(monster instanceof EntityLiving) {
			if(isUndead((EntityLiving) monster)) {
				monster.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, monster.posX + (itemRand.nextFloat() - 0.5F), monster.posY + (itemRand.nextFloat() - 0.5F) + (monster.height / 2), monster.posZ + (itemRand.nextFloat() - 0.5F), 0.0F, 0.0F, 0.0F);
			}
		}
		return super.onLeftClickEntity(stack, player, monster);
	}

	private boolean isUndead(EntityLivingBase e) {
		return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}
}
