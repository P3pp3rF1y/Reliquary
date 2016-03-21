package xreliquary.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class EntityXRFakePlayer extends FakePlayer {
	private AbstractAttributeMap attributeMap;
	private final ItemStack[] handInventory = new ItemStack[2];
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public EntityXRFakePlayer(WorldServer world) {
		this(world, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}

	public EntityXRFakePlayer(WorldServer world, GameProfile name) {
		super(world, name);
	}

	@Override
	public AbstractAttributeMap getAttributeMap() {
		if(attributeMap == null)
			attributeMap = new AttributeMap();
		return attributeMap;
	}

	@Override
	public void onUpdate() {
		if(this.worldObj.isRemote)
			return;

		for(int i = 0; i < 2; i++) {
			EntityEquipmentSlot entityEquipmentSlot = i == 0 ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;

			ItemStack itemstack = this.handInventory[entityEquipmentSlot.getIndex()];
			ItemStack itemstack1 = this.getItemStackFromSlot(entityEquipmentSlot);

			if(!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				if(itemstack != null) {
					this.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityEquipmentSlot));
				}

				if(itemstack1 != null) {
					this.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(entityEquipmentSlot));
				}

				this.handInventory[entityEquipmentSlot.getIndex()] = itemstack1 == null ? null : itemstack1.copy();
				break;
			}
		}
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
