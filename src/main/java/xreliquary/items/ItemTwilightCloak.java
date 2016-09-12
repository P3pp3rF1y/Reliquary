package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class ItemTwilightCloak extends ItemToggleable {

	public ItemTwilightCloak() {
		super(Names.Items.TWILIGHT_CLOAK);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if(!(e instanceof EntityPlayer))
			return;
		if(!this.isEnabled(ist))
			return;
		EntityPlayer player = (EntityPlayer) e;

		//toggled effect, makes player invisible based on light level (configurable)
		int playerX = MathHelper.floor_double(player.posX);
		int playerY = MathHelper.floor_double(player.getEntityBoundingBox().minY);
		int playerZ = MathHelper.floor_double(player.posZ);

		if(player.worldObj.getLightFromNeighbors(new BlockPos(playerX, playerY, playerZ)) > Settings.TwilightCloak.maxLightLevel)
			return;

		//        if (Reliquary.CONFIG.getBool(Names.twilight_cloak, "only_works_at_night")) {
		//            long worldTime = player.worldObj.getWorldTime() % 24000;
		//            if (worldTime > 13187 && worldTime < 22812)
		//                return;
		//        }

		//checks if the effect would do anything. Literally all this does is make the player invisible. It doesn't interfere with mob AI.
		//for that, we're attempting to use an event handler.
		PotionEffect quickInvisibility = new PotionEffect(MobEffects.INVISIBILITY, 2, 0, false, false);
		player.addPotionEffect(quickInvisibility);
	}
}
