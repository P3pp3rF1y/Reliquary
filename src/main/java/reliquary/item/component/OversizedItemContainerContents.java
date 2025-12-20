package reliquary.item.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.util.CodecHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;

public final class OversizedItemContainerContents {
	private static final int NO_SLOT = -1;
	public static final int MAX_SIZE = 256;
	public static final OversizedItemContainerContents EMPTY = new OversizedItemContainerContents(NonNullList.create());
	public static final Codec<OversizedItemContainerContents> CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, OversizedItemContainerContents> STREAM_CODEC;
	private final NonNullList<ItemStack> items;
	private final int hashCode;

	private OversizedItemContainerContents(NonNullList<ItemStack> items) {
		if (items.size() > MAX_SIZE) {
			throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is " + MAX_SIZE);
		} else {
			this.items = items;
			this.hashCode = ItemStack.hashStackList(items);
		}
	}

	private OversizedItemContainerContents(int p_331689_) {
		this(NonNullList.withSize(p_331689_, ItemStack.EMPTY));
	}

	private OversizedItemContainerContents(List<ItemStack> items) {
		this(items.size());

		for (int i = 0; i < items.size(); ++i) {
			this.items.set(i, items.get(i));
		}

	}

	private static OversizedItemContainerContents fromSlots(List<Slot> slots) {
		OptionalInt maxIndex = slots.stream().mapToInt(Slot::index).max();
		if (maxIndex.isEmpty()) {
			return EMPTY;
		} else {
			OversizedItemContainerContents contents = new OversizedItemContainerContents(maxIndex.getAsInt() + 1);
			slots.forEach((slot) -> contents.items.set(slot.index(), slot.item()));
			return contents;
		}
	}

	public static OversizedItemContainerContents fromSize(int size) {
		return new OversizedItemContainerContents(size);
	}

	public static OversizedItemContainerContents fromItems(List<ItemStack> p_340879_) {
		int i = findLastNonEmptySlot(p_340879_);
		if (i == NO_SLOT) {
			return EMPTY;
		} else {
			OversizedItemContainerContents contents = new OversizedItemContainerContents(i + 1);

			for (int index = 0; index <= i; ++index) {
				contents.items.set(index, p_340879_.get(index).copy());
			}

			return contents;
		}
	}

	private static int findLastNonEmptySlot(List<ItemStack> items) {
		for (int i = items.size() - 1; i >= 0; --i) {
			if (!items.get(i).isEmpty()) {
				return i;
			}
		}

		return NO_SLOT;
	}

	private List<Slot> asSlots() {
		List<Slot> list = new ArrayList<>();

		for (int i = 0; i < this.items.size(); ++i) {
			ItemStack itemstack = this.items.get(i);
			if (!itemstack.isEmpty()) {
				list.add(new Slot(i, itemstack));
			}
		}

		return list;
	}

	public void copyInto(NonNullList<ItemStack> p_330513_) {
		for (int i = 0; i < p_330513_.size(); ++i) {
			ItemStack itemstack = i < this.items.size() ? this.items.get(i) : ItemStack.EMPTY;
			p_330513_.set(i, itemstack.copy());
		}

	}

	public ItemStack copyOne() {
		return this.items.isEmpty() ? ItemStack.EMPTY : this.items.getFirst().copy();
	}

	public Stream<ItemStack> stream() {
		return this.items.stream().map(ItemStack::copy);
	}

	public Stream<ItemStack> nonEmptyStream() {
		return this.items.stream().filter((item) -> !item.isEmpty()).map(ItemStack::copy);
	}

	public Iterable<ItemStack> nonEmptyItems() {
		return Iterables.filter(this.items, item -> !item.isEmpty());
	}

	public Iterable<ItemStack> nonEmptyItemsCopy() {
		return Iterables.transform(this.nonEmptyItems(), ItemStack::copy);
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else {
			if (other instanceof OversizedItemContainerContents contents) {
				return ItemStack.listMatches(this.items, contents.items);
			}

			return false;
		}
	}

	public int hashCode() {
		return this.hashCode;
	}

	public int getSlots() {
		return this.items.size();
	}

	public ItemStack getStackInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.items.get(slot).copy();
	}

	public int getCountInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.items.get(slot).getCount();
	}

	public Item getStackInSlotItem(int slot) {
		this.validateSlotIndex(slot);
		return this.items.get(slot).getItem();
	}

	private void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= this.getSlots()) {
			throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
		}
	}

	static {
		CODEC = Slot.CODEC.sizeLimitedListOf(MAX_SIZE).xmap(OversizedItemContainerContents::fromSlots, OversizedItemContainerContents::asSlots);
		STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(MAX_SIZE)).map(OversizedItemContainerContents::new, (contents) -> contents.items);
	}

	record Slot(int index, ItemStack item) {
		public static final Codec<Slot> CODEC = RecordCodecBuilder.create((instance) ->
				instance.group(
						Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
						CodecHelper.OVERSIZED_ITEM_STACK_CODEC.orElse(ItemStack.EMPTY).fieldOf("item").forGetter(Slot::item)
				).apply(instance, Slot::new));
	}
}
