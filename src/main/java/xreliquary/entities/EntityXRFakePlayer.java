package xreliquary.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

public class EntityXRFakePlayer extends FakePlayer {
	private BaseAttributeMap attributeMap;
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public EntityXRFakePlayer(WorldServer world) {
		this(world, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}
	public EntityXRFakePlayer(WorldServer world, GameProfile name) {
		super(world, name);
	}

	@Override
	public BaseAttributeMap getAttributeMap() {
		if (attributeMap == null)
			attributeMap = new ServersideAttributeMap();
		return attributeMap;
	}

	@Override
	public void onUpdate() {
		ItemStack equippedItem = this.getCurrentEquippedItem();

		if (equippedItem == null)
			return;

		attributeMap.removeAttributeModifiers(equippedItem.getAttributeModifiers());
		attributeMap.applyAttributeModifiers(equippedItem.getAttributeModifiers());
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
