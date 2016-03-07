package xreliquary.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

public class EntityXRFakePlayer extends FakePlayer {
	private final ItemStack[] previousEquipment = new ItemStack[5];
	private BaseAttributeMap attributeMap;
	private static final String FAKE_PLAYER_USERNAME = "reliquary_pedestal_fake_player";

	public EntityXRFakePlayer(WorldServer world) {
		this(world, new GameProfile(UUID.nameUUIDFromBytes(FAKE_PLAYER_USERNAME.getBytes()), FAKE_PLAYER_USERNAME));
	}
	public EntityXRFakePlayer(WorldServer world, GameProfile name) {
		super(world, name);
		FakePlayerFactory.get(world, name);
	}

	@Override
	public BaseAttributeMap getAttributeMap() {
		return attributeMap;
	}

	@Override
	public void onUpdate() {
		if (worldObj.isRemote)
			return;

		for (int j = 0; j < 5; ++j)
		{
			ItemStack itemstack = this.previousEquipment[j];
			ItemStack itemstack1 = this.getEquipmentInSlot(j);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
			{
				((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), j, itemstack1));

				attributeMap = super.getAttributeMap();

				if (itemstack != null)
				{
					this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
				}

				if (itemstack1 != null)
				{
					this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
				}

				this.previousEquipment[j] = itemstack1 == null ? null : itemstack1.copy();
			}
		}
	}

}
