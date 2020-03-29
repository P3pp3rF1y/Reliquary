package xreliquary.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class MobCharmBeltItem extends ItemBase implements IBaubleItem {
	private static final String SLOTS_TAG = "Slots";

	public MobCharmBeltItem() {
		super(Names.Items.MOB_CHARM_BELT, new Properties().maxStackSize(1));
	}

	@Override
	public IBaubleItem.Type getBaubleType() {
		return IBaubleItem.Type.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity player) {
		//noop
	}

	@Override
	public void onEquipped(String identifier, LivingEntity player) {
		if(player.world.isRemote) {
			player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1F, 1F);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (player.isSneaking()) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}

		if (!world.isRemote && player instanceof ServerPlayerEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((w, p, pl) -> new ContainerMobCharmBelt(w, p, stack), stack.getDisplayName()), buf -> buf.writeBoolean(hand == Hand.MAIN_HAND));
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	public ItemStack getMobCharmInSlot(ItemStack belt, int slotIndex) {
		CompoundNBT nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return ItemStack.EMPTY;
		}

		ListNBT mobCharms = nbt.getList(SLOTS_TAG, Constants.NBT.TAG_COMPOUND);

		if (mobCharms.size() <= slotIndex) {
			return ItemStack.EMPTY;
		}

		return ItemStack.read((CompoundNBT) mobCharms.get(slotIndex));
	}

	public void putMobCharmInSlot(ItemStack belt, int slotIndex, ItemStack mobCharm) {
		if (mobCharm.isEmpty()) {
			removeMobCharmInSlot(belt, slotIndex);
			return;
		}

		CompoundNBT mobCharmNbt = mobCharm.write(new CompoundNBT());
		CompoundNBT nbt = belt.getTag();
		ListNBT mobCharms;
		if (nbt == null) {
			nbt = new CompoundNBT();
			mobCharms = new ListNBT();
		} else {
			mobCharms = nbt.getList(SLOTS_TAG, Constants.NBT.TAG_COMPOUND);
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
		CompoundNBT nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return;
		}

		ListNBT mobCharms = nbt.getList(SLOTS_TAG, Constants.NBT.TAG_COMPOUND);

		if (mobCharms.size() <= slotIndex) {
			return;
		}

		mobCharms.remove(slotIndex);
	}

	public int getCharmCount(ItemStack belt) {
		CompoundNBT nbt = belt.getTag();

		if (nbt == null) {
			return 0;
		}

		ListNBT mobCharms = nbt.getList(SLOTS_TAG, Constants.NBT.TAG_COMPOUND);

		return mobCharms.size();
	}

	public boolean hasCharm(ItemStack belt, String entityRegistryName) {
		CompoundNBT nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return false;
		}

		ListNBT mobCharms = nbt.getList(SLOTS_TAG, Constants.NBT.TAG_COMPOUND);

		for (int i = mobCharms.size() - 1; i >= 0; i--) {
			ItemStack charmStack = ItemStack.read(mobCharms.getCompound(i));

			if (MobCharmItem.getEntityRegistryName(charmStack).equals(entityRegistryName)) {
				return true;
			}
		}

		return false;
	}

	ItemStack damageCharm(PlayerEntity player, ItemStack belt, String entityRegistryName) {
		CompoundNBT nbt = belt.getTag();

		if (nbt == null || !nbt.contains(SLOTS_TAG)) {
			return ItemStack.EMPTY;
		}

		ListNBT mobCharms = nbt.getList(SLOTS_TAG, Constants.NBT.TAG_COMPOUND);

		for (int i = mobCharms.size() - 1; i >= 0; i--) {
			ItemStack charmStack = ItemStack.read(mobCharms.getCompound(i));

			if (MobCharmItem.isCharmFor(charmStack, entityRegistryName)) {
				charmStack.damageItem(Settings.COMMON.items.mobCharm.damagePerKill.get(), player, p -> p.sendBreakAnimation(EquipmentSlotType.CHEST));
				if (charmStack.isEmpty()) {
					removeMobCharmInSlot(belt, i);
					return ItemStack.EMPTY;
				} else {
					mobCharms.set(i, charmStack.write(new CompoundNBT()));
					return charmStack;
				}
			}
		}

		return ItemStack.EMPTY;
	}
}
