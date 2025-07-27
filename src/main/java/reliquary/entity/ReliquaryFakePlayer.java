package reliquary.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

@SuppressWarnings({"squid:S2160", "squid:MaximumInheritanceDepth"})
public class ReliquaryFakePlayer extends FakePlayer {
	private final NonNullList<ItemStack> fakePlayerHandInventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public ReliquaryFakePlayer(ServerLevel level) {
		this(level, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}

	private ReliquaryFakePlayer(ServerLevel level, GameProfile name) {
		super(level, name);
	}

	@Override
	public void tick() {
		if (level().isClientSide) {
			return;
		}

		for (int i = 0; i < 2; i++) {
			EquipmentSlot equipmentSlot = EquipmentSlot.values()[i];

			ItemStack oldStack = fakePlayerHandInventory.get(equipmentSlot.getIndex());
			ItemStack newStack = getItemBySlot(equipmentSlot);

			if (!ItemStack.matches(newStack, oldStack)) {
				if (!oldStack.isEmpty()) {
					AttributeMap attributeMap = getAttributes();
					oldStack.forEachModifier(equipmentSlot, (attribute, attributeModifier) -> {
						AttributeInstance attributeinstance = attributeMap.getInstance(attribute);
						if (attributeinstance != null) {
							attributeinstance.removeModifier(attributeModifier);
						}

						EnchantmentHelper.stopLocationBasedEffects(oldStack, this, equipmentSlot);
					});
				}

				if (!newStack.isEmpty()) {
					AttributeMap attributeMap = getAttributes();
					newStack.forEachModifier(equipmentSlot, (attribute, attributeModifier) -> {
						AttributeInstance attributeinstance = attributeMap.getInstance(attribute);
						if (attributeinstance != null) {
							attributeinstance.removeModifier(attributeModifier.id());
							attributeinstance.addTransientModifier(attributeModifier);
						}

						if (level() instanceof ServerLevel serverlevel) {
							EnchantmentHelper.runLocationChangedEffects(serverlevel, newStack, this, equipmentSlot);
						}
					});
				}

				setItemSlot(equipmentSlot, newStack.isEmpty() ? ItemStack.EMPTY : newStack);
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
