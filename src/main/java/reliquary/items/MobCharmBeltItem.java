package reliquary.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import reliquary.common.gui.MobCharmBeltMenu;
import reliquary.items.util.ICuriosItem;
import reliquary.reference.Settings;

public class MobCharmBeltItem extends ItemBase implements ICuriosItem {
	private static final String SLOTS_TAG = "Slots";

	public MobCharmBeltItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public ICuriosItem.Type getCuriosType() {
		return ICuriosItem.Type.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity player) {
		//noop
	}

	@Override
	public void onEquipped(String identifier, LivingEntity player) {
		if (player.level.isClientSide) {
			player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1F, 1F);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isCrouching()) {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}

		if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
			NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((w, p, pl) -> new MobCharmBeltMenu(w, p, stack), stack.getHoverName()), buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	public ItemStack getMobCharmInSlot(ItemStack belt, int slotIndex) {
		CompoundTag nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return ItemStack.EMPTY;
		}

		ListTag mobCharms = nbt.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		if (mobCharms.size() <= slotIndex) {
			return ItemStack.EMPTY;
		}

		return ItemStack.of((CompoundTag) mobCharms.get(slotIndex));
	}

	public void putMobCharmInSlot(ItemStack belt, int slotIndex, ItemStack mobCharm) {
		if (mobCharm.isEmpty()) {
			removeMobCharmInSlot(belt, slotIndex);
			return;
		}

		CompoundTag mobCharmNbt = mobCharm.save(new CompoundTag());
		CompoundTag nbt = belt.getTag();
		ListTag mobCharms;
		if (nbt == null) {
			nbt = new CompoundTag();
			mobCharms = new ListTag();
		} else {
			mobCharms = nbt.getList(SLOTS_TAG, Tag.TAG_COMPOUND);
		}

		if (mobCharms.size() < slotIndex) {
			return;
		}

		if (mobCharms.size() == slotIndex) {
			mobCharms.add(mobCharmNbt);
		} else {
			mobCharms.set(slotIndex, mobCharmNbt);
		}
		nbt.put(SLOTS_TAG, mobCharms);
		belt.setTag(nbt);
	}

	public void removeMobCharmInSlot(ItemStack belt, int slotIndex) {
		CompoundTag nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return;
		}

		ListTag mobCharms = nbt.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		if (mobCharms.size() <= slotIndex) {
			return;
		}

		mobCharms.remove(slotIndex);
	}

	public int getCharmCount(ItemStack belt) {
		CompoundTag nbt = belt.getTag();

		if (nbt == null) {
			return 0;
		}

		ListTag mobCharms = nbt.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		return mobCharms.size();
	}

	public boolean hasCharm(ItemStack belt, String entityRegistryName) {
		CompoundTag nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return false;
		}

		ListTag mobCharms = nbt.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		for (int i = mobCharms.size() - 1; i >= 0; i--) {
			ItemStack charmStack = ItemStack.of(mobCharms.getCompound(i));

			if (MobCharmItem.getEntityRegistryName(charmStack).equals(entityRegistryName)) {
				return true;
			}
		}

		return false;
	}

	ItemStack damageCharm(Player player, ItemStack belt, String entityRegistryName) {
		CompoundTag nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return ItemStack.EMPTY;
		}

		ListTag mobCharms = nbt.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		for (int i = mobCharms.size() - 1; i >= 0; i--) {
			ItemStack charmStack = ItemStack.of(mobCharms.getCompound(i));

			if (MobCharmItem.isCharmFor(charmStack, entityRegistryName)) {
				charmStack.hurtAndBreak(Settings.COMMON.items.mobCharm.damagePerKill.get(), player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
				if (charmStack.isEmpty()) {
					removeMobCharmInSlot(belt, i);
					return ItemStack.EMPTY;
				} else {
					mobCharms.set(i, charmStack.save(new CompoundTag()));
					return charmStack;
				}
			}
		}

		return ItemStack.EMPTY;
	}
}
