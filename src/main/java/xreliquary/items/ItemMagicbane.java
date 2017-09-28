package xreliquary.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMagicbane extends ItemSword {

	public ItemMagicbane() {
		super(ToolMaterial.GOLD);
		this.setMaxDamage(16);
		this.setMaxStackSize(1);
		canRepair = true;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setUnlocalizedName(Names.Items.MAGICBANE);
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
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void addInformation(ItemStack magicBane, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(magicBane) + ".tooltip", tooltip);
	}

	/**
	 * Returns the strength of the stack against a given block. 1.0F base,
	 * (Quality+1)*2 if correct blocktype, 1.5F if sword
	 */
	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState blockState) {
		return blockState.getBlock() == Blocks.WEB ? 15.0F : 1.5F;
	}

	/**
	 * Current implementations of this method in child classes do not use the
	 * entry argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
		if(target != null) {
			int random = target.world.rand.nextInt(16);
			switch(random) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 2));
				case 5:
				case 6:
				case 7:
				case 8:
					target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
					break;
				case 9:
				case 10:
				case 11:
					target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
					break;
				case 12:
				case 13:
					target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 2));
					target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 2));
					break;
				case 14:
					target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 2));
					target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 2));
					break;
				default:
					break;
			}
			if(attacker instanceof EntityPlayer) {
				NBTTagList enchants = stack.getEnchantmentTagList();
				int bonus = 0;
				for(int enchant = 0; enchant < enchants.tagCount(); enchant++) {
					bonus += enchants.getCompoundTagAt(enchant).getShort("lvl");
				}
				target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), bonus + 4);
			}
			stack.damageItem(1, attacker);
		}
		return true;
	}
}
