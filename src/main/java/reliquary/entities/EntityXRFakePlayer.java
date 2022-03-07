package reliquary.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

@SuppressWarnings({"squid:S2160", "squid:MaximumInheritanceDepth"})
public class EntityXRFakePlayer extends FakePlayer {
	private final NonNullList<ItemStack> fakePlayerHandInventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public EntityXRFakePlayer(ServerLevel world) {
		this(world, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}

	private EntityXRFakePlayer(ServerLevel world, GameProfile name) {
		super(world, name);
	}

	@Override
	public void tick() {
		if (level.isClientSide) {
			return;
		}

		for (int i = 0; i < 2; i++) {
			EquipmentSlot entityEquipmentSlot = EquipmentSlot.values()[i];

			ItemStack itemstack = fakePlayerHandInventory.get(entityEquipmentSlot.getIndex());
			ItemStack itemstack1 = getItemBySlot(entityEquipmentSlot);

			if (!ItemStack.matches(itemstack1, itemstack)) {
				if (!itemstack.isEmpty()) {
					getAttributes().removeAttributeModifiers(itemstack.getAttributeModifiers(entityEquipmentSlot));
				}

				if (!itemstack1.isEmpty()) {
					getAttributes().addTransientAttributeModifiers(itemstack1.getAttributeModifiers(entityEquipmentSlot));
				}

				setItemSlot(entityEquipmentSlot, itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1);
				break;
			}
		}

		//finish previous swing or cool down caused by change of weapons
		attackStrengthTicker = (int) getCurrentItemAttackStrengthDelay();
	}

	@Override
	protected void onEffectAdded(MobEffectInstance effect, @Nullable Entity entity) {
		//noop
	}

	@Override
	protected void onEffectUpdated(MobEffectInstance effect, boolean updateAttributes, @Nullable Entity entity) {
		//noop
	}


	@Override
	protected void onEffectRemoved(MobEffectInstance effect) {
		//noop
	}

	@Override
	public Vec3 position() {
		return position;
	}

	@Override
	public BlockPos blockPosition() {
		return blockPosition;
	}
}
