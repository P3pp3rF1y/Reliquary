package xreliquary.items;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.common.gui.GUIHandler;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class ItemMobCharmBelt extends ItemBauble {
	private static final String SLOTS_TAG = "Slots";
	private static final String TYPE_TAG = "Type";
	private static final String DAMAGE_TAG = "Damage";

	public ItemMobCharmBelt() {
		super(Names.mob_charm_belt);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking())
			return new ActionResult<>(EnumActionResult.PASS, stack);

		player.openGui(Reliquary.INSTANCE, GUIHandler.MOB_CHARM_BELT, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	public ItemStack getMobCharmInSlot(ItemStack belt, int slotIndex) {
		NBTTagCompound nbt = belt.getTagCompound();

		if(nbt == null || !nbt.hasKey(SLOTS_TAG))
			return null;

		NBTTagList mobCharms = nbt.getTagList(SLOTS_TAG, 10);

		if(mobCharms.tagCount() <= slotIndex)
			return null;

		NBTTagCompound mobCharmNbt = (NBTTagCompound) mobCharms.get(slotIndex);

		if (!mobCharmNbt.hasKey(TYPE_TAG) || !mobCharmNbt.hasKey(DAMAGE_TAG)) {
			removeMobCharmInSlot(belt, slotIndex);
			return null;
		}

		ItemStack mobCharm = XRRecipes.mobCharm(mobCharmNbt.getByte(TYPE_TAG));
		mobCharm.setItemDamage(mobCharmNbt.getInteger(DAMAGE_TAG));

		return mobCharm;
	}

	public void putMobCharmInSlot(ItemStack belt, int slotIndex, ItemStack mobCharm) {
		if (mobCharm == null) {
			removeMobCharmInSlot(belt, slotIndex);
			return;
		}

		NBTTagCompound mobCharmNbt = new NBTTagCompound();
		mobCharmNbt.setByte(TYPE_TAG, ModItems.mobCharm.getType(mobCharm));
		mobCharmNbt.setInteger(DAMAGE_TAG, mobCharm.getItemDamage());

		NBTTagCompound nbt = belt.getTagCompound();
		NBTTagList mobCharms;
		if(nbt == null) {
			nbt = new NBTTagCompound();
			mobCharms = new NBTTagList();
		} else {
			mobCharms = nbt.getTagList(SLOTS_TAG, 10);
		}

		if(mobCharms.tagCount() < slotIndex)
			return;

		if(mobCharms.tagCount() == slotIndex) {
			mobCharms.appendTag(mobCharmNbt);
		} else {
			mobCharms.set(slotIndex, mobCharmNbt);
		}
		nbt.setTag(SLOTS_TAG, mobCharms);
		belt.setTagCompound(nbt);
	}

	public void removeMobCharmInSlot(ItemStack belt, int slotIndex) {
		NBTTagCompound nbt = belt.getTagCompound();

		if(nbt == null || !nbt.hasKey(SLOTS_TAG))
			return;

		NBTTagList mobCharms = nbt.getTagList(SLOTS_TAG, 10);

		if(mobCharms.tagCount() <= slotIndex)
			return;

		mobCharms.removeTag(slotIndex);
	}

	public int getCharmCount(ItemStack belt) {
		NBTTagCompound nbt = belt.getTagCompound();

		if(nbt == null)
			return 0;

		NBTTagList mobCharms = nbt.getTagList(SLOTS_TAG, 10);

		return mobCharms == null ? 0 : mobCharms.tagCount();
	}

	public boolean hasCharmType(ItemStack belt, byte type) {
		NBTTagCompound nbt = belt.getTagCompound();

		if(nbt == null || !nbt.hasKey(SLOTS_TAG))
			return false;

		NBTTagList mobCharms = nbt.getTagList(SLOTS_TAG, 10);

		for (int i=mobCharms.tagCount() - 1; i>=0; i--) {
			NBTTagCompound mobCharmNbt = (NBTTagCompound) mobCharms.get(i);

			if (!mobCharmNbt.hasKey(TYPE_TAG) || !mobCharmNbt.hasKey(DAMAGE_TAG)) {
				removeMobCharmInSlot(belt, i);
			}

			if (mobCharmNbt.getByte(TYPE_TAG) == type)
				return true;
		}

		return false;
	}

	public int damageCharmType(ItemStack belt, byte type) {
		NBTTagCompound nbt = belt.getTagCompound();

		if(nbt == null || !nbt.hasKey(SLOTS_TAG))
			return -1;

		NBTTagList mobCharms = nbt.getTagList(SLOTS_TAG, 10);

		for (int i=mobCharms.tagCount() - 1; i>=0; i--) {
			NBTTagCompound mobCharmNbt = (NBTTagCompound) mobCharms.get(i);

			if (!mobCharmNbt.hasKey(TYPE_TAG) || !mobCharmNbt.hasKey(DAMAGE_TAG)) {
				removeMobCharmInSlot(belt, i);
			}

			if (mobCharmNbt.getByte(TYPE_TAG) == type) {
				int damage = mobCharmNbt.getInteger(DAMAGE_TAG);
				if (damage + Settings.MobCharm.damagePerKill > ModItems.mobCharm.getMaxDamage()) {
					removeMobCharmInSlot(belt, i);
					return ModItems.mobCharm.getMaxDamage();
				} else {
					damage += Settings.MobCharm.damagePerKill;
					mobCharmNbt.setInteger(DAMAGE_TAG, damage);
					return damage;
				}
			}
		}

		return -1;

	}
}
