package reliquary.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.CustomDisplayIngredient;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.item.AlkahestryTomeItem;

import java.util.List;

public class AlkahestryDrainRecipe implements CraftingRecipe {
	private final int chargeToDrain;
	private final ItemStackTemplate result;
	private final Ingredient tomeIngredient;

	public AlkahestryDrainRecipe(int chargeToDrain, ItemStackTemplate result) {
		this.chargeToDrain = chargeToDrain;
		this.result = result;
		tomeIngredient = CustomDisplayIngredient.of(
				Ingredient.of(ModItems.ALKAHESTRY_TOME.get()),
				new SlotDisplay.ItemStackSlotDisplay(new ItemStackTemplate(ModItems.ALKAHESTRY_TOME, DataComponentPatch.builder().set(ModDataComponents.CHARGE.get(), AlkahestryTomeItem.getChargeLimit()).build()))
		);
		AlkahestryRecipeRegistry.setDrainRecipe(this);
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		boolean hasTome = false;
		ItemStack tome = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.isEmpty()) {
				continue;
			}
			if (!hasTome && stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				hasTome = true;
				tome = stack;
			} else {
				return false;
			}
		}

		return hasTome && AlkahestryTomeItem.getCharge(tome) > 0;
	}

	public ItemStack getResultItem() {
		return result.create();
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.create(List.of(tomeIngredient));
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack tome = getTome(inv).copy();

		int charge = AlkahestryTomeItem.getCharge(tome);
		ItemStack ret = result.create();
		ret.setCount(Math.min(ret.getMaxStackSize(), charge / chargeToDrain));

		return ret;
	}

	private ItemStack getTome(CraftingInput inv) {
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
		NonNullList<ItemStack> ret = CraftingRecipe.super.getRemainingItems(inv);
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack tome = stack.copy();
				int charge = AlkahestryTomeItem.getCharge(tome);
				int itemCount = Math.min(result.create().getMaxStackSize(), charge / chargeToDrain);
				ModItems.ALKAHESTRY_TOME.get().useCharge(tome, itemCount * chargeToDrain);
				ret.set(slot, tome);
			}
		}

		return ret;
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return ModItems.ALKAHESTRY_DRAIN_SERIALIZER.get();
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public String group() {
		return "";
	}

	private ItemStackTemplate getResult() {
		return result;
	}

	private Integer getChargeToDrain() {
		return chargeToDrain;
	}

	public static final MapCodec<AlkahestryDrainRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							Codec.INT.fieldOf("charge").forGetter(recipe -> recipe.chargeToDrain),
							ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
					)
					.apply(instance, AlkahestryDrainRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AlkahestryDrainRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.INT,
				AlkahestryDrainRecipe::getChargeToDrain,
				ItemStackTemplate.STREAM_CODEC,
				AlkahestryDrainRecipe::getResult,
				AlkahestryDrainRecipe::new
		);
}
