package xreliquary.items;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

public class MobCharmFragmentItem extends ItemBase {
	public MobCharmFragmentItem() {
		super(new Properties());
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (!isInGroup(group)) {
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
	public ITextComponent getDisplayName(ItemStack stack) {
		EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return super.getDisplayName(stack);
		}
		return new StringTextComponent(LanguageHelper.getLocalization(getTranslationKey(), entityType.getName().getString()));
	}
}
