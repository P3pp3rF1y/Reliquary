package xreliquary.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import xreliquary.entities.LyssaBobberEntity;
import xreliquary.init.ModItems;
import xreliquary.util.NBTHelper;

public class RodOfLyssaItem extends ItemBase {
	public RodOfLyssaItem() {
		super(new Properties().maxStackSize(1));
	}

	public static int getHookEntityId(ItemStack stack) {
		return NBTHelper.getInt("hookEntityId", stack);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		int entityId = getHookEntityId(stack);
		if (entityId != 0 && world.getEntityByID(entityId) instanceof LyssaBobberEntity) {
			LyssaBobberEntity hook = (LyssaBobberEntity) world.getEntityByID(entityId);
			player.swingArm(hand);
			//noinspection ConstantConditions
			hook.handleHookRetraction(stack);
			setHookEntityId(stack, 0);
		} else {
			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {

				int lureLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LURE, stack);
				int luckOfTheSeaLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, stack);

				LyssaBobberEntity hook = new LyssaBobberEntity(world, player, lureLevel, luckOfTheSeaLevel);
				world.addEntity(hook);

				setHookEntityId(stack, hook.getEntityId());
			}

			player.swingArm(hand);
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	private void setHookEntityId(ItemStack stack, int entityId) {
		NBTHelper.putInt("hookEntityId", stack, entityId);
	}
}
