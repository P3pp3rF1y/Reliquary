package reliquary.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

public class ReliquaryLootModifierProvider extends GlobalLootModifierProvider {

	ReliquaryLootModifierProvider(PackOutput packOutput) {
		super(packOutput, Reference.MOD_ID);
	}

	@Override
	protected void start() {
		ChestLootInjectSubProvider.LOOT_INJECTS.forEach((vanillaLootTable, injectLootTable) ->
				add(vanillaLootTable.getPath(), InjectLootModifier.chest(injectLootTable, vanillaLootTable)));

		EntityLootInjectSubProvider.LOOT_INJECTS.forEach((vanillaLootTable, injectLootTable) ->
				add(vanillaLootTable.getPath(), InjectLootModifier.entity(injectLootTable, vanillaLootTable)));
	}

	public static class InjectLootModifier extends LootModifier {
		public static final Codec<InjectLootModifier> CODEC = RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).and(
				inst.group(
						ResourceLocation.CODEC.fieldOf("loot_table").forGetter(m -> m.lootTable),
						ResourceLocation.CODEC.fieldOf("loot_table_to_inject_into").forGetter(m -> m.lootTableToInjectInto)
				)
		).apply(inst, InjectLootModifier::new));
		private final ResourceLocation lootTable;
		private final ResourceLocation lootTableToInjectInto;

		protected InjectLootModifier(LootItemCondition[] conditions, ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
			super(conditions);
			this.lootTable = lootTable;
			this.lootTableToInjectInto = lootTableToInjectInto;
		}

		protected static InjectLootModifier chest(ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
			return new InjectLootModifier(new LootItemCondition[] {ChestLootEnabledCondition.builder().build(),
					LootTableIdCondition.builder(lootTableToInjectInto).build()}, lootTable, lootTableToInjectInto);
		}

		protected static InjectLootModifier entity(ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
			return new InjectLootModifier(new LootItemCondition[] {EntityLootEnabledCondition.builder().build(),
					LootTableIdCondition.builder(lootTableToInjectInto).build()}, lootTable, lootTableToInjectInto);
		}

		@Override
		protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
			LootTable table = context.getResolver().getLootTable(lootTable);
			table.getRandomItemsRaw(context, generatedLoot::add);
			return generatedLoot;
		}

		@Override
		public Codec<? extends IGlobalLootModifier> codec() {
			return ModItems.INJECT_LOOT.get();
		}
	}
}
