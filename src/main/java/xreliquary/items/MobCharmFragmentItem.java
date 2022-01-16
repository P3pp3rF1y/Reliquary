package xreliquary.items;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

public class MobCharmFragmentItem extends ItemBase {
	public MobCharmFragmentItem() {
		super(new Properties());
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!allowdedIn(group)) {
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
	public Component getName(ItemStack stack) {
		EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return super.getName(stack);
		}
		return new TextComponent(LanguageHelper.getLocalization(getDescriptionId(), entityType.getDescription().getString()));
	}
}
