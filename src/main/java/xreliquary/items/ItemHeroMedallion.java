package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;

import java.util.List;

@ContentInit
public class ItemHeroMedallion extends ItemToggleable {

    public ItemHeroMedallion() {
        super(Names.hero_medallion);
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
    public boolean hasEffect(ItemStack stack, int pass) {
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

    private int getExperienceMinimum() {
        return Reliquary.CONFIG.getInt(Names.hero_medallion, "experience_level_minimum");
    }

    private int getExperienceMaximum() {
        return Reliquary.CONFIG.getInt(Names.hero_medallion, "experience_level_maximum");
    }

    // this drains experience beyond level specified in configs
    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (!this.isEnabled(ist))
            return;
        if (e instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e;
            // in order to make this stop at a specific level, we will need to do
            // a preemptive check for a specific level.
            if ((player.experienceLevel > getExperienceMinimum() || player.experience > 0F) && getExperience(ist) < Integer.MAX_VALUE) {
                decreasePlayerExperience(player);
                increaseMedallionExperience(ist);
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int unadjustedCount) {
        if (player.experienceLevel < getExperienceMaximum()) {
            if (getExperience(ist) > 0) {
                increasePlayerExperience(player);
                decreaseMedallionExperience(ist);
            }
        }
    }

    // I'm not 100% this is needed. You may be able to avoid this whole call by
    // using the method in the player class, might be worth testing
    // (player.addExperience(-1)?)
    public void decreasePlayerExperience(EntityPlayer player) {
        if (player.experience - (1.0F / (float) player.xpBarCap()) <= 0 && player.experienceLevel > getExperienceMinimum()) {
            decreasePlayerLevel(player);
            return;
        }
        player.experience -= 1.0F / (float) player.xpBarCap();
        player.experienceTotal -= 1;
    }

    public void decreaseMedallionExperience(ItemStack ist) {
        setExperience(ist, getExperience(ist) - 1);
    }

    public void decreasePlayerLevel(EntityPlayer player) {
        player.experience = 1.0F - (1.0F / (float) player.xpBarCap());
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
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        // may be unnecessary. Not sure, but I think trying to grab unavailable
        // keys defaults to 0.
        if (!stack.stackTagCompound.hasKey("experience"))
            stack.stackTagCompound.setInteger("experience", 0);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (world.isRemote)
            return ist;
        if (this.isEnabled(ist))
            return ist;
        player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
        return ist;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 250;
    }
}
