package xreliquary.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EntityXRFakePlayer extends FakePlayer {
	private AbstractAttributeMap attributeMap;
	private final NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public EntityXRFakePlayer(WorldServer world) {
		this(world, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}

	private EntityXRFakePlayer(WorldServer world, GameProfile name) {
		super(world, name);
		connection = new FakeNetHandlerPlayServer(this);
	}

	@Nonnull
	@Override
	public AbstractAttributeMap getAttributeMap() {
		if(attributeMap == null)
			attributeMap = new AttributeMap();
		return attributeMap;
	}

	@Override
	public void onUpdate() {
		if(this.world.isRemote)
			return;

		for(int i = 0; i < 2; i++) {
			EntityEquipmentSlot entityEquipmentSlot = EntityEquipmentSlot.values()[i];

			ItemStack itemstack = this.handInventory.get(entityEquipmentSlot.getIndex());
			ItemStack itemstack1 = this.getItemStackFromSlot(entityEquipmentSlot);

			if(!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				if(!itemstack.isEmpty()) {
					this.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityEquipmentSlot));
				}

				if(!itemstack1.isEmpty()) {
					this.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(entityEquipmentSlot));
				}

				this.setItemStackToSlot(entityEquipmentSlot, itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1);
				break;
			}
		}

		//finish previous swing or cool down caused by change of weapons
		this.ticksSinceLastSwing = (int) this.getCooldownPeriod();
	}

	@Override
	protected void onNewPotionEffect(PotionEffect id) {
	}

	@Override
	protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
	}

	@Override
	protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
	}
}
