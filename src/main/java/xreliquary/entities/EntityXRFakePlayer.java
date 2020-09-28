package xreliquary.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

@SuppressWarnings({"squid:S2160", "squid:MaximumInheritanceDepth"})
public class EntityXRFakePlayer extends FakePlayer {
	private final NonNullList<ItemStack> fakePlayerHandInventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public EntityXRFakePlayer(ServerWorld world) {
		this(world, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}

	private EntityXRFakePlayer(ServerWorld world, GameProfile name) {
		super(world, name);
		connection = new FakeNetHandlerPlayServer(this);
	}

	@Override
	public void tick() {
		if (world.isRemote) {
			return;
		}

		for (int i = 0; i < 2; i++) {
			EquipmentSlotType entityEquipmentSlot = EquipmentSlotType.values()[i];

			ItemStack itemstack = fakePlayerHandInventory.get(entityEquipmentSlot.getIndex());
			ItemStack itemstack1 = getItemStackFromSlot(entityEquipmentSlot);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				if (!itemstack.isEmpty()) {
					getAttributeManager().removeModifiers(itemstack.getAttributeModifiers(entityEquipmentSlot));
				}

				if (!itemstack1.isEmpty()) {
					getAttributeManager().reapplyModifiers(itemstack1.getAttributeModifiers(entityEquipmentSlot));
				}

				setItemStackToSlot(entityEquipmentSlot, itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1);
				break;
			}
		}

		//finish previous swing or cool down caused by change of weapons
		ticksSinceLastSwing = (int) getCooldownPeriod();
	}

	@Override
	protected void onNewPotionEffect(EffectInstance id) {
		//noop
	}

	@Override
	protected void onChangedPotionEffect(EffectInstance id, boolean p_70695_2_) {
		//noop
	}

	@Override
	protected void onFinishedPotionEffect(EffectInstance effect) {
		//noop
	}
}
