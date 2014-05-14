package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		this.formatTooltip(ImmutableMap.of("experience", String.valueOf(NBTHelper.getShort("experience", stack))), stack, list);
	}

    @SideOnly(Side.CLIENT)
    private IIcon iconOverlay;

    //next two methods are an imitation of the coin of fortune, similar On/Off effects on the two "coins"
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

	// below is an excerpt of the player class to show some of the formulas involved.
//	public void addExperience(int par1)    {	
		//simply prevents the value being added to experience from exceeding the int cap
//        int j = Integer.MAX_VALUE - this.experienceTotal;
//        if (par1 > j) par1 = j;
		//here is where the experience bar is increased, as a fraction of the bar.
//        this.experience += (float)par1 / (float)this.xpBarCap();  //xpBarCap is a really weird formula
		//if experience >= 1.0, it means the bar is full
		//it would be extremely unusual for this to fire twice, but I guess it's theoretically possible
//        for (this.experienceTotal += par1; this.experience >= 1.0F; this.experience /= (float)this.xpBarCap()) {
//            this.experience = (this.experience - 1.0F) * (float)this.xpBarCap();
//            this.addExperienceLevel(1);
//        }
//    }

    //this drains experience beyond level thirty
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        //1 for on, 0 for off. Pretty straightforward.
        if (ist.getItemDamage() == 0) return;
		if (e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e;
			//in order to make this stop at 30, we will need to do a preemptive check for level 30.
			if (player.experienceLevel < 30) { 
				if (getExperience(ist) > 0) {
                    increasePlayerExperience(player);
                    decreaseMedallionExperience(ist);
                }

			} else {
				if (player.experienceTotal > 0 && getExperience(ist) < Integer.MAX_VALUE){
					decreasePlayerExperience(player);
                    increaseMedallionExperience(ist);
                }
            }
		}
	}

    //I'm not 100% this is needed. You may be able to avoid this whole call by
    //using the method in the player class, might be worth testing (player.addExperience(-1)?)
	public void decreasePlayerExperience(EntityPlayer player) {
		if (player.experience == 0 && player.experienceLevel > 30){
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
		player.experienceLevel -= 1;
		player.experience = 1.0F - (1.0F / (float)player.xpBarCap());
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
