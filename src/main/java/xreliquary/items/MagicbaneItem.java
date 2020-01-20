package xreliquary.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.Reliquary;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class MagicbaneItem extends SwordItem {
	public MagicbaneItem() {
		super(ItemTier.GOLD, 3, -2.4f, new Properties().maxDamage(16).setNoRepair().group(Reliquary.ITEM_GROUP).rarity(Rarity.EPIC));
		setRegistryName(new ResourceLocation(Reference.MOD_ID, "magicbane"));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void addInformation(ItemStack magicBane, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip", tooltip);
	}

	/**
	 * Returns the strength of the stack against a given block. 1.0F base,
	 * (Quality+1)*2 if correct blocktype, 1.5F if sword
	 */
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState blockState) {
		return blockState.getBlock() == Blocks.COBWEB ? 15.0F : 1.5F;
	}

	/**
	 * Current implementations of this method in child classes do not use the
	 * entry argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (target != null) {
			int random = target.world.rand.nextInt(16);
			switch (random) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 2));
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
					target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
					break;
				case 12:
				case 13:
					target.addPotionEffect(new EffectInstance(Effects.POISON, 100, 2));
					target.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 2));
					break;
				case 14:
					target.addPotionEffect(new EffectInstance(Effects.WITHER, 100, 2));
					target.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100, 2));
					break;
				default:
					break;
			}
			if (attacker instanceof PlayerEntity) {
				ListNBT enchants = stack.getEnchantmentTagList();
				int bonus = 0;
				for (int enchant = 0; enchant < enchants.size(); enchant++) {
					bonus += enchants.getCompound(enchant).getShort("lvl");
				}
				target.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) attacker), bonus + 4f);
			}
			stack.damageItem(1, attacker, e -> e.sendBreakAnimation(EquipmentSlotType.MAINHAND));
		}
		return true;
	}
}
