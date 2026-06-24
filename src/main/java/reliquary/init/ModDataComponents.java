package reliquary.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;
import reliquary.Reliquary;
import reliquary.item.*;
import reliquary.item.component.OversizedItemContainerContents;
import reliquary.util.CodecHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ModDataComponents {
	private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,
			Reliquary.MOD_ID);

	public static final Supplier<DataComponentType<ResourceLocation>> ENTITY_NAME = DATA_COMPONENT_TYPES.registerComponentType("entity_name",
			builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));

	public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_CONTENTS = DATA_COMPONENT_TYPES.registerComponentType("fluid_contents",
			builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

	public static final Supplier<DataComponentType<OversizedItemContainerContents>> OVERSIZED_ITEM_CONTAINER_CONTENTS = DATA_COMPONENT_TYPES
			.registerComponentType("oversized_item_container_contents",
					builder -> builder.persistent(OversizedItemContainerContents.CODEC).networkSynchronized(OversizedItemContainerContents.STREAM_CODEC));

	public static final Supplier<DataComponentType<Map<Integer, Integer>>> PARTIAL_CHARGES = DATA_COMPONENT_TYPES.registerComponentType("partial_charge",
			builder -> builder.persistent(ChargeableItem.PARTIAL_CHARGES_CODEC).networkSynchronized(ChargeableItem.PARTIAL_CHARGES_STREAM_CODEC));

	public static final Supplier<DataComponentType<BlockPos>> WARP_POSITION = DATA_COMPONENT_TYPES.registerComponentType("warp_position",
			builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

	public static final Supplier<DataComponentType<ResourceLocation>> WARP_DIMENSION = DATA_COMPONENT_TYPES.registerComponentType("warp_dimension",
			builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));

	public static final Supplier<DataComponentType<Set<BlockPos>>> FROZEN_POSITIONS = DATA_COMPONENT_TYPES.registerComponentType("frozen_positions",
			builder -> builder.persistent(CodecHelper.setOf(BlockPos.CODEC))
					.networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new))));

	public static final Supplier<DataComponentType<Integer>> CHARGE = DATA_COMPONENT_TYPES.registerComponentType("charge",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<Integer>> GUNPOWDER = DATA_COMPONENT_TYPES.registerComponentType("gunpowder",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<EnderStaffItem.Mode>> ENDER_STAFF_MODE = DATA_COMPONENT_TYPES.registerComponentType("ender_staff_mode",
			builder -> builder.persistent(EnderStaffItem.Mode.CODEC).networkSynchronized(EnderStaffItem.Mode.STREAM_CODEC));

	public static final Supplier<DataComponentType<Boolean>> ENABLED = DATA_COMPONENT_TYPES.registerComponentType("enabled",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

	public static final Supplier<DataComponentType<Integer>> SNOWBALLS = DATA_COMPONENT_TYPES.registerComponentType("snowballs",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<Short>> BULLET_COUNT = DATA_COMPONENT_TYPES.registerComponentType("bullet_count",
			builder -> builder.persistent(Codec.SHORT).networkSynchronized(ByteBufCodecs.SHORT));

	public static final Supplier<DataComponentType<ResourceLocation>> MAGAZINE_TYPE = DATA_COMPONENT_TYPES.registerComponentType("magazine_type",
			builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));

	public static final Supplier<DataComponentType<Long>> COOLDOWN_TIME = DATA_COMPONENT_TYPES.registerComponentType("cooldown_time",
			builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

	public static final Supplier<DataComponentType<Byte>> PLANTABLE_INDEX = DATA_COMPONENT_TYPES.registerComponentType("plantable_index",
			builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE));

	public static final Supplier<DataComponentType<HarvestRodItem.Mode>> HARVEST_ROD_MODE = DATA_COMPONENT_TYPES.registerComponentType("harvest_rod",
			builder -> builder.persistent(HarvestRodItem.Mode.CODEC).networkSynchronized(HarvestRodItem.Mode.STREAM_CODEC));

	public static final Supplier<DataComponentType<Integer>> EXPERIENCE = DATA_COMPONENT_TYPES.registerComponentType("experience",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<Integer>> DRAIN_XP_LEVELS = DATA_COMPONENT_TYPES.registerComponentType("drain_xp_levels",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<Integer>> STOP_AT_XP_LEVEL = DATA_COMPONENT_TYPES.registerComponentType("stop_at_xp_level",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<Integer>> GLOWSTONE = DATA_COMPONENT_TYPES.registerComponentType("glowstone",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<PyromancerStaffItem.Mode>> PYROMANCER_STAFF_MODE = DATA_COMPONENT_TYPES.registerComponentType(
			"pyromancer_staff_mode", builder -> builder.persistent(PyromancerStaffItem.Mode.CODEC).networkSynchronized(PyromancerStaffItem.Mode.STREAM_CODEC));

	public static final Supplier<DataComponentType<RendingGaleItem.Mode>> RENDING_GALE_MODE = DATA_COMPONENT_TYPES.registerComponentType("rending_gale_mode",
			builder -> builder.persistent(RendingGaleItem.Mode.CODEC).networkSynchronized(RendingGaleItem.Mode.STREAM_CODEC));

	public static final Supplier<DataComponentType<Integer>> HOOK_ENTITY_ID = DATA_COMPONENT_TYPES.registerComponentType("hook_entity_id",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

	public static final Supplier<DataComponentType<Byte>> TORCH_INDEX = DATA_COMPONENT_TYPES.registerComponentType("torch_index",
			builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE));

	public static final Supplier<DataComponentType<VoidTearItem.Mode>> VOID_TEAR_MODE = DATA_COMPONENT_TYPES.registerComponentType("void_tear_mode",
			builder -> builder.persistent(VoidTearItem.Mode.CODEC).networkSynchronized(VoidTearItem.Mode.STREAM_CODEC));

	public static void register(IEventBus modBus) {
		DATA_COMPONENT_TYPES.register(modBus);
	}
}
