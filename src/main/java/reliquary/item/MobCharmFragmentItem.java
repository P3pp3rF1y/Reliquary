package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModDataComponents;

import java.util.function.Consumer;

public class MobCharmFragmentItem extends ItemBase {
	public MobCharmFragmentItem() {
		super(new Properties());
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		for (ResourceLocation entityRegistryName : MobCharmRegistry.getRegisteredNames()) {
			itemConsumer.accept(getStackFor(entityRegistryName));
		}
	}

	public ItemStack getStackFor(EntityType<?> entityType) {
		return getStackFor(EntityType.getKey(entityType));
	}

	public ItemStack getStackFor(ResourceLocation entityRegistryName) {
		ItemStack ret = new ItemStack(this);
		ret.set(ModDataComponents.ENTITY_NAME.get(), entityRegistryName);
		return ret;
	}

	public static ResourceLocation getEntityRegistryName(ItemStack charm) {
		return charm.getOrDefault(ModDataComponents.ENTITY_NAME.get(), BuiltInRegistries.ENTITY_TYPE.getDefaultKey());
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		ResourceLocation entityEggRegistryName = getEntityRegistryName(stack);
		return BuiltInRegistries.ENTITY_TYPE.getOptional(entityEggRegistryName)
				.map(entityType -> Component.translatable(getDescriptionId(), entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
				.orElseGet(() -> super.getName(stack));
	}
}
