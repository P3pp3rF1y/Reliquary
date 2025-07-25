package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.neoforge.common.Tags;
import reliquary.Reliquary;

import java.util.Map;
import java.util.function.Consumer;

public class WitchHatItem extends ArmorItem implements ICreativeTabItemGenerator {
	public static final ResourceKey<EquipmentAsset> EQUIPMENT_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, Reliquary.getRL("witch_hat"));

	private static final ArmorMaterial ARMOR_MATERIAL = new ArmorMaterial(
			10, Map.of(ArmorType.HELMET, 0), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0, 0, Tags.Items.LEATHERS, EQUIPMENT_ASSET);

	public WitchHatItem(Properties properties) {
		super(ARMOR_MATERIAL, ArmorType.HELMET, properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withStyle(ChatFormatting.YELLOW);
	}
}
