package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;

@Optional.Interface(iface = "baubles.api.IBauble", modid = Compatibility.MOD_ID.BAUBLES, striprefs = true)
public class ItemTwilightCloak extends ItemToggleable/* implements IBauble*/ {

	public ItemTwilightCloak() {
		super(Names.Items.TWILIGHT_CLOAK);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void onUpdate(ItemStack twilightCloak, World world, Entity e, int i, boolean f) {
		if(!(e instanceof EntityPlayer))
			return;

		updateInvisibility(twilightCloak, (EntityPlayer) e);
	}

	private void updateInvisibility(ItemStack twilightCloak, EntityPlayer player) {
		if(!this.isEnabled(twilightCloak))
			return;

		//toggled effect, makes player invisible based on light level (configurable)
		int playerX = MathHelper.floor(player.posX);
		int playerY = MathHelper.floor(player.getEntityBoundingBox().minY);
		int playerZ = MathHelper.floor(player.posZ);

		if(player.world.getLightFromNeighbors(new BlockPos(playerX, playerY, playerZ)) > Settings.TwilightCloak.maxLightLevel)
			return;

		//checks if the effect would do anything. Literally all this does is make the player invisible. It doesn't interfere with mob AI.
		//for that, we're attempting to use an event handler.
		PotionEffect quickInvisibility = new PotionEffect(MobEffects.INVISIBILITY, 2, 0, false, false);
		player.addPotionEffect(quickInvisibility);
	}

/* TODO Baubles
	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onWornTick(ItemStack twilightCloak, EntityLivingBase player) {
		updateInvisibility(twilightCloak, (EntityPlayer) player);
	}
*/

	@SubscribeEvent
	public void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		doTwilightCloakCheck(event);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		doTwilightCloakCheck(event);
	}

	private void doTwilightCloakCheck(LivingEvent event) {
		if(event.getEntity() instanceof EntityLiving) {
			EntityLiving entityLiving = ((EntityLiving) event.getEntity());
			if(entityLiving.getAttackTarget() == null)
				return;
			if(!(entityLiving.getAttackTarget() instanceof EntityPlayer))
				return;
			EntityPlayer player = (EntityPlayer) entityLiving.getAttackTarget();
			if(!InventoryHelper.playerHasItem(player, this, true))
				return;

			//toggled effect, makes player invisible based on light level (configurable)
			if(player.world.getLightFromNeighbors(player.getPosition()) > Settings.TwilightCloak.maxLightLevel)
				return;
			if(event.getEntity() instanceof EntityLiving) {
				((EntityLiving) event.getEntity()).setAttackTarget(null);
			}
		}
	}
}
