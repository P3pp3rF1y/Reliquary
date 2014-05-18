package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.event.ClientEventHandler;
import xreliquary.init.XRInit;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@XRInit
public class ItemTwilightCloak extends ItemBase {

	public ItemTwilightCloak() {
		super(Reference.MOD_ID, Names.twilight_cloak);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		//this.setMaxDamage(2401);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}
	@SideOnly(Side.CLIENT)
	private IIcon iconOverlay;

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.twilight_cloak_overlay);
	}

	@Override
	public IIcon getIcon(ItemStack itemStack, int renderPass) {
		if (renderPass != 1)
			return this.itemIcon;
		else
			return iconOverlay;
	}

    //should probably be replaced by the new overlay system (the one the eyes use)
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (renderPass == 1) {
			int i = ClientEventHandler.getTime();
			i %= 87;
			if (i > 43) {
				i = 87 - i;
			}
			i = (int) (i * 255F / 43F);
			String red = Integer.toHexString(i);
			return Integer.parseInt(String.format("%s%s%s", red, "00", "00"), 16);
		} else
			return Integer.parseInt(Colors.DARKEST, 16);
    }

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if (!(e instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) e;
        //always on for now, takes effect only at night, or low light (configurable)
        //if (ist.getItemDamage() == 0) return;
        if (world.getWorldTime() % 24000 < 12000 && player.worldObj.getBlockLightValue((int)Math.floor(player.posX), (int)Math.floor(player.posY), (int)Math.floor(player.posZ)) > Reliquary.PROXY.twilightCloakLightThreshold) return;
        //checks if the effect would do anything.
        PotionEffect invisForFiveTicks = new PotionEffect(Potion.invisibility.id, 5, 0);
        if (!player.isPotionApplicable(invisForFiveTicks)) return;
        player.addPotionEffect(invisForFiveTicks);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		//ist.setItemDamage(ist.getItemDamage() == 0 ? 1 : 0);
		return ist;
	}

}
