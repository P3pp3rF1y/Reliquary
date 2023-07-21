package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.util.NBTHelper;

public class MobCharmFragmentItem extends ItemBase {
	public MobCharmFragmentItem() {
		super(new Properties());
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!allowedIn(group)) {
			return;
		}
		for (String entityRegistryName : MobCharmRegistry.getRegisteredNames()) {
			items.add(getStackFor(entityRegistryName));
		}
	}

	public ItemStack getStackFor(String entityRegistryName) {
		ItemStack ret = new ItemStack(this);
		NBTHelper.putString("entity", ret, entityRegistryName);
		return ret;
	}

	public static String getEntityRegistryName(ItemStack charm) {
		return NBTHelper.getString("entity", charm);
	}

	public static ResourceLocation getEntityEggRegistryName(ItemStack charm) {
		return new ResourceLocation(getEntityRegistryName(charm));
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return super.getName(stack);
		}
		return Component.translatable(getDescriptionId(), entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN);
	}
}
