package reliquary.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import reliquary.entity.LyssaHook;
import reliquary.init.ModDataComponents;

public class RodOfLyssaItem extends ItemBase {
	public RodOfLyssaItem() {
		super(new Properties().stacksTo(1));
	}

	public static int getHookEntityId(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.HOOK_ENTITY_ID, 0);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int entityId = getHookEntityId(stack);
		if (entityId != 0 && level.getEntity(entityId) instanceof LyssaHook hook) {
			player.swing(hand);
			hook.handleHookRetraction(stack);
			setHookEntityId(stack, 0);
		} else {
			level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

			if (!level.isClientSide) {

				HolderLookup.RegistryLookup<Enchantment> registrylookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
				int lureLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(Enchantments.LURE), player);
				int luckOfTheSeaLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(Enchantments.LUCK_OF_THE_SEA), player);

				LyssaHook hook = new LyssaHook(level, player, lureLevel, luckOfTheSeaLevel);
				level.addFreshEntity(hook);

				setHookEntityId(stack, hook.getId());
			}

			player.swing(hand);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	private void setHookEntityId(ItemStack stack, int entityId) {
		stack.set(ModDataComponents.HOOK_ENTITY_ID, entityId);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(itemAbility);
	}
}
