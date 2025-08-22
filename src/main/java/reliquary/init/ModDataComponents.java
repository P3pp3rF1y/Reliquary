package reliquary.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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
	private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Reliquary.MOD_ID);

	public static final Supplier<DataComponentType<ResourceLocation>> ENTITY_NAME = DATA_COMPONENT_TYPES.register("entity_name",
			() -> new DataComponentType.Builder<ResourceLocation>().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build()
	);

	public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_CONTENTS = DATA_COMPONENT_TYPES.register("fluid_contents",
			() -> new DataComponentType.Builder<SimpleFluidContent>().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<OversizedItemContainerContents>> OVERSIZED_ITEM_CONTAINER_CONTENTS = DATA_COMPONENT_TYPES.register("oversized_item_container_contents",
			() -> new DataComponentType.Builder<OversizedItemContainerContents>().persistent(OversizedItemContainerContents.CODEC).networkSynchronized(OversizedItemContainerContents.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<Map<Integer, Integer>>> PARTIAL_CHARGES = DATA_COMPONENT_TYPES.register("partial_charge",
			() -> new DataComponentType.Builder<Map<Integer, Integer>>().persistent(ChargeableItem.PARTIAL_CHARGES_CODEC).networkSynchronized(ChargeableItem.PARTIAL_CHARGES_STREAM_CODEC).build());

	public static final Supplier<DataComponentType<BlockPos>> WARP_POSITION = DATA_COMPONENT_TYPES.register("warp_position",
			() -> new DataComponentType.Builder<BlockPos>().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<ResourceLocation>> WARP_DIMENSION = DATA_COMPONENT_TYPES.register("warp_dimension",
			() -> new DataComponentType.Builder<ResourceLocation>().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<Set<BlockPos>>> FROZEN_POSITIONS = DATA_COMPONENT_TYPES.register("frozen_positions",
			() -> new DataComponentType.Builder<Set<BlockPos>>().persistent(CodecHelper.setOf(BlockPos.CODEC)).networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new))).build());

	public static final Supplier<DataComponentType<Integer>> CHARGE = DATA_COMPONENT_TYPES.register("charge",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<Integer>> GUNPOWDER = DATA_COMPONENT_TYPES.register("gunpowder",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<EnderStaffItem.Mode>> ENDER_STAFF_MODE = DATA_COMPONENT_TYPES.register("ender_staff_mode",
			() -> new DataComponentType.Builder<EnderStaffItem.Mode>().persistent(EnderStaffItem.Mode.CODEC).networkSynchronized(EnderStaffItem.Mode.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<Boolean>> ENABLED = DATA_COMPONENT_TYPES.register("enabled",
			() -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

	public static final Supplier<DataComponentType<Integer>> SNOWBALLS = DATA_COMPONENT_TYPES.register("snowballs",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<Short>> BULLET_COUNT = DATA_COMPONENT_TYPES.register("bullet_count",
			() -> new DataComponentType.Builder<Short>().persistent(Codec.SHORT).networkSynchronized(ByteBufCodecs.SHORT).build());

	public static final Supplier<DataComponentType<ResourceLocation>> MAGAZINE_TYPE = DATA_COMPONENT_TYPES.register("magazine_type",
			() -> new DataComponentType.Builder<ResourceLocation>().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<Long>> COOLDOWN_TIME = DATA_COMPONENT_TYPES.register("cooldown_time",
			() -> new DataComponentType.Builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	public static final Supplier<DataComponentType<Byte>> PLANTABLE_INDEX = DATA_COMPONENT_TYPES.register("plantable_index",
			() -> new DataComponentType.Builder<Byte>().persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE).build());

	public static final Supplier<DataComponentType<HarvestRodItem.Mode>> HARVEST_ROD_MODE = DATA_COMPONENT_TYPES.register("harvest_rod",
			() -> new DataComponentType.Builder<HarvestRodItem.Mode>().persistent(HarvestRodItem.Mode.CODEC).networkSynchronized(HarvestRodItem.Mode.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<Integer>> EXPERIENCE = DATA_COMPONENT_TYPES.register("experience",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<Integer>> DRAIN_XP_LEVELS = DATA_COMPONENT_TYPES.register("drain_xp_levels",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<Integer>> STOP_AT_XP_LEVEL = DATA_COMPONENT_TYPES.register("stop_at_xp_level",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<Integer>> GLOWSTONE = DATA_COMPONENT_TYPES.register("glowstone",
			() -> new DataComponentType.Builder<Integer>().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<PyromancerStaffItem.Mode>> PYROMANCER_STAFF_MODE = DATA_COMPONENT_TYPES.register("pyromancer_staff_mode",
			() -> new DataComponentType.Builder<PyromancerStaffItem.Mode>().persistent(PyromancerStaffItem.Mode.CODEC).networkSynchronized(PyromancerStaffItem.Mode.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<RendingGaleItem.Mode>> RENDING_GALE_MODE = DATA_COMPONENT_TYPES.register("rending_gale_mode",
			() -> new DataComponentType.Builder<RendingGaleItem.Mode>().persistent(RendingGaleItem.Mode.CODEC).networkSynchronized(RendingGaleItem.Mode.STREAM_CODEC).build());

	public static final Supplier<DataComponentType<Integer>> HOOK_ENTITY_ID = DATA_COMPONENT_TYPES.register("hook_entity_id",
			() -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

	public static final Supplier<DataComponentType<Byte>> TORCH_INDEX = DATA_COMPONENT_TYPES.register("torch_index",
			() -> new DataComponentType.Builder<Byte>().persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE).build());

	public static final Supplier<DataComponentType<VoidTearItem.Mode>> VOID_TEAR_MODE = DATA_COMPONENT_TYPES.register("void_tear_mode",
			() -> new DataComponentType.Builder<VoidTearItem.Mode>().persistent(VoidTearItem.Mode.CODEC).networkSynchronized(VoidTearItem.Mode.STREAM_CODEC).build());

	public static void register(IEventBus modBus) {
		DATA_COMPONENT_TYPES.register(modBus);
	}
}
