package xreliquary.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import xreliquary.init.ModItems;
import xreliquary.items.PotionItem;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class LingeringPotionIngredient extends Ingredient {
	public static final IIngredientSerializer<? extends Ingredient> SERIALIZER = new Serializer();

	private LingeringPotionIngredient() {
		super(Stream.of());
	}

	@Override
	public JsonElement serialize() {
		JsonObject json = new JsonObject();
		json.addProperty("type", SERIALIZER.toString());
		return json;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null) {
			return false;
		}
		return input.getItem() == ModItems.POTION && PotionItem.isLingering(input);
	}

	private static class Serializer implements IIngredientSerializer<LingeringPotionIngredient> {
		@Override
		public LingeringPotionIngredient parse(PacketBuffer buffer) {
			return new LingeringPotionIngredient();
		}

		@Override
		public LingeringPotionIngredient parse(JsonObject json) {
			return new LingeringPotionIngredient();
		}

		@Override
		public void write(PacketBuffer buffer, LingeringPotionIngredient ingredient) {
			//noop
		}
	}
}
