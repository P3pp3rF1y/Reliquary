package reliquary.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class ReliquaryLootModifierProvider extends GlobalLootModifierProvider {

	ReliquaryLootModifierProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries, Reliquary.MOD_ID);
	}

	@Override
	protected void start() {
		ChestLootInjectSubProvider.LOOT_INJECTS.forEach((vanillaLootTable, injectLootTable) ->
				add(vanillaLootTable.location().getPath(), InjectLootModifier.chest(injectLootTable, vanillaLootTable)));

		EntityLootInjectSubProvider.LOOT_INJECTS.forEach((vanillaLootTable, injectLootTable) ->
				add(vanillaLootTable.location().getPath(), InjectLootModifier.entity(injectLootTable, vanillaLootTable)));
	}

	public static class InjectLootModifier extends LootModifier {
		public static final MapCodec<InjectLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> LootModifier.codecStart(inst).and(
				inst.group(
						ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(m -> m.lootTable),
						ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table_to_inject_into").forGetter(m -> m.lootTableToInjectInto)
				)
		).apply(inst, InjectLootModifier::new));
		private final ResourceKey<LootTable> lootTable;
		private final ResourceKey<LootTable> lootTableToInjectInto;

		protected InjectLootModifier(LootItemCondition[] conditions, ResourceKey<LootTable> lootTable, ResourceKey<LootTable> lootTableToInjectInto) {
			super(conditions);
			this.lootTable = lootTable;
			this.lootTableToInjectInto = lootTableToInjectInto;
		}

		protected static InjectLootModifier chest(ResourceKey<LootTable> lootTable, ResourceKey<LootTable> lootTableToInjectInto) {
			return new InjectLootModifier(new LootItemCondition[]{ChestLootEnabledCondition.builder().build(),
					LootTableIdCondition.builder(lootTableToInjectInto.location()).build()}, lootTable, lootTableToInjectInto);
		}

		protected static InjectLootModifier entity(ResourceKey<LootTable> lootTable, ResourceKey<LootTable> lootTableToInjectInto) {
			return new InjectLootModifier(new LootItemCondition[]{EntityLootEnabledCondition.builder().build(),
					LootTableIdCondition.builder(lootTableToInjectInto.location()).build()}, lootTable, lootTableToInjectInto);
		}

		@SuppressWarnings({"deprecation", "java:S1874"})
		// Need to call getRandomItemsRaw to skip neo calling modifyLoot event and causing infinite loop
		@Override
		protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
			context.getResolver().get(lootTable).ifPresent(extraTable -> {
				extraTable.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add));
			});
			return generatedLoot;
		}

		@Override
		public MapCodec<? extends IGlobalLootModifier> codec() {
			return ModItems.INJECT_LOOT.get();
		}
	}
}
