package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;

@XRInit
public class ItemHeroMedallion extends ItemBase {

	public ItemHeroMedallion() {
		super(Reference.MOD_ID, Names.hero_medallion);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return stack.getItemDamage() == 1;
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
        iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.hero_medallion_overlay);
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int renderPass) {
        if (itemStack.getItemDamage() == 0 || renderPass != 1)
            return this.itemIcon;
        else
            return iconOverlay;
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		this.formatTooltip(ImmutableMap.of("experience", String.valueOf(NBTHelper.getShort("experience", stack))), stack, list);
	}

    //this drains experience beyond level thirty
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        //1 for on, 0 for off. Pretty straightforward.
        if (ist.getItemDamage() == 0) return;
		if (e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e;
			//in order to make this stop at 30, we will need to do a preemptive check for level 30.
			if (player.experienceLevel < Reliquary.PROXY.heroMedallionLevelThreshold) {
				if (getExperience(ist) > 0) {
                    increasePlayerExperience(player);
                    decreaseMedallionExperience(ist);
                }

			} else {
				if ((player.experienceLevel > Reliquary.PROXY.heroMedallionLevelThreshold || player.experience > 0F) && getExperience(ist) < Integer.MAX_VALUE){
					decreasePlayerExperience(player);
                    increaseMedallionExperience(ist);
                }
            }
		}
	}

    //I'm not 100% this is needed. You may be able to avoid this whole call by
    //using the method in the player class, might be worth testing (player.addExperience(-1)?)
	public void decreasePlayerExperience(EntityPlayer player) {
		if (player.experience - (1.0F / (float)player.xpBarCap()) <= 0 && player.experienceLevel > Reliquary.PROXY.heroMedallionLevelThreshold){
			decreasePlayerLevel(player);
            return;
        }
		player.experience -= 1.0F / (float)player.xpBarCap();
		player.experienceTotal -= 1;				
	}

    public void decreaseMedallionExperience(ItemStack ist) {
        setExperience(ist, getExperience(ist) - 1);
    }
	
	public void decreasePlayerLevel(EntityPlayer player) {
        player.experience = 1.0F - (1.0F / (float)player.xpBarCap());
        player.experienceTotal -= 1;
		player.experienceLevel -= 1;
	}
	
	public void increasePlayerExperience(EntityPlayer player) {
		player.addExperience(1);
	}

    public void increaseMedallionExperience(ItemStack ist) {
        setExperience(ist, getExperience(ist) + 1);
    }
	
	public int getExperience(ItemStack stack) {
		ensureTagCompound(stack);
		return stack.stackTagCompound.getInteger("experience");
	}
	
	public void setExperience(ItemStack stack, int i) {
		ensureTagCompound(stack);
		stack.stackTagCompound.setInteger("experience", i);
	}

    public void ensureTagCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		//may be unnecessary. Not sure, but I think trying to grab unavailable keys defaults to 0.
		if (!stack.stackTagCompound.hasKey("experience")) stack.stackTagCompound.setInteger("experience", 0);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (world.isRemote)
            return ist;
        //if we wanted to add some special functionality for sneaking (copied directly from coin)
        //if (player.isSneaking()) {
            //player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
        //} else {

        //if we wanted sound effects. There's none for now.
        //if (!Reliquary.PROXY.disableCoinAudio) {
        //    NBTHelper.setShort("soundTimer", ist, 6);
        //}
        ist.setItemDamage(ist.getItemDamage() == 0 ? 1 : 0);
        //}
        return ist;
	}

}
