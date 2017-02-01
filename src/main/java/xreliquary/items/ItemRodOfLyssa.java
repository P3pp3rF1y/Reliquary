package xreliquary.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityLyssaHook;
import xreliquary.reference.Names;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;

public class ItemRodOfLyssa extends ItemBase {
	public ItemRodOfLyssa() {
		super(Names.Items.ROD_OF_LYSSA);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
		this.addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return entityIn == null ? 0.0F :
						((entityIn.getHeldItemMainhand() == stack || entityIn.getHeldItemOffhand() == stack) && getHookEntityId(stack) > 0 ? 1.0F : 0.0F);
			}
		});
	}

	/**
	 * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
	 * hands.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		int entityId = getHookEntityId(stack);
		if(entityId != 0 && world.getEntityByID(entityId) instanceof EntityLyssaHook) {
			EntityLyssaHook hook = (EntityLyssaHook) world.getEntityByID(entityId);
			player.swingArm(hand);
			//noinspection ConstantConditions
			hook.handleHookRetraction();
			setHookEntityId(stack, 0);
		} else {
			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if(!world.isRemote) {
				
				int lureLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LURE, stack);
				int luckOfTheSeaLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, stack);
				
				
				EntityLyssaHook hook = new EntityLyssaHook(world, player, lureLevel, luckOfTheSeaLevel);
				world.spawnEntity(hook);

				setHookEntityId(stack, hook.getEntityId());
			}

			player.swingArm(hand);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private void setHookEntityId(ItemStack stack, int entityId) {
		NBTHelper.setInteger("hookEntityId", stack, entityId);
	}

	private int getHookEntityId(ItemStack stack) {
		return NBTHelper.getInteger("hookEntityId", stack);
	}

}
