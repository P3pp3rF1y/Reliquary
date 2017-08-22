package xreliquary.crafting.factories;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.BooleanSupplier;

public class OreExistsConditionFactory implements IConditionFactory{
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		return () -> !OreDictionary.getOres(JsonUtils.getString(json, "ore")).isEmpty();
	}
}
