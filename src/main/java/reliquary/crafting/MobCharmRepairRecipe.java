package reliquary.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.item.MobCharmDefinition;
import reliquary.item.MobCharmRegistry;
import reliquary.reference.Config;

import java.util.Optional;

public class MobCharmRepairRecipe extends CustomRecipe {
	public static final MobCharmRepairRecipe INSTANCE = new MobCharmRepairRecipe(CraftingBookCategory.MISC);
	public static final MapCodec<MobCharmRepairRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, MobCharmRepairRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final RecipeSerializer<MobCharmRepairRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
	private static final int PER_FRAGMENT_MULTIPLIER = 6;

	public MobCharmRepairRecipe(CraftingBookCategory category) {
		super();
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack currentStack = inv.getItem(i);
			if (!currentStack.isEmpty()) {
				if (currentStack.getItem() == ModItems.MOB_CHARM.get()) {
					if (!mobCharm.isEmpty()) {
						return false;
					}
					mobCharm = currentStack;
					continue;
				}

				if (ingredient.isEmpty()) {
					ingredient = currentStack;
				} else {
					if (ingredient.getItem() != currentStack.getItem()) {
						return false;
					}
				}
				numberIngredients++;
			}
		}

		if (mobCharm.isEmpty()) {
			return false;
		}

		ItemStack finalIngredient = ingredient;
		Optional<MobCharmDefinition> cd = MobCharmRegistry.getCharmDefinitionFor(mobCharm);
		if (cd.isEmpty()) {
			return false;
		}
		MobCharmDefinition charmDefinition = cd.get();

		int repairMultiplier = charmDefinition.isDynamicallyCreated() ? PER_FRAGMENT_MULTIPLIER : 1;
		int durabilityRepaired = Config.COMMON.items.mobCharm.dropDurabilityRepair.get() * repairMultiplier;
		return mobCharm.getDamageValue() >= durabilityRepaired * (numberIngredients - 1) && charmDefinition.isRepairItem(finalIngredient);
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack currentStack = inv.getItem(i);
			if (!currentStack.isEmpty()) {
				if (currentStack.getItem() == ModItems.MOB_CHARM.get()) {
					mobCharm = currentStack;
					continue;
				}
				if (ingredient.isEmpty()) {
					ingredient = currentStack;
				}
				numberIngredients++;
			}
		}

		ItemStack resultingMobCharm = mobCharm.copy();

		resultingMobCharm.setDamageValue(Math.max(resultingMobCharm.getDamageValue() - (Config.COMMON.items.mobCharm.dropDurabilityRepair.get() * numberIngredients), 0));

		return resultingMobCharm;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
		return NonNullList.withSize(inv.size(), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<MobCharmRepairRecipe> getSerializer() {
		return SERIALIZER;
	}
}
