package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.init.ModFluids;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.XpHelper;

import java.util.List;

public class ItemHeroMedallion extends ItemToggleable implements IFluidContainerItem {

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
		return EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return NBTHelper.getBoolean("enabled", stack);
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("experience", String.valueOf(NBTHelper.getInteger("experience", ist))), ist, list);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GREEN + "XP"), ist, list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
	}

	private int getExperienceMinimum() {
		return Settings.HeroMedallion.experienceLevelMinimum;
	}

	private int getExperienceMaximum() {
		return Settings.HeroMedallion.experienceLevelMaximum;
	}

	// this drains experience beyond level specified in configs
	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if(!this.isEnabled(ist))
			return;
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;
			// in order to make this stop at a specific level, we will need to do
			// a preemptive check for a specific level.
			for(int levelLoop = 0; levelLoop <= Math.sqrt(!player.capabilities.isCreativeMode ? player.experienceLevel : 30); ++levelLoop) {
				if((player.experienceLevel > getExperienceMinimum() || player.experience > 0F || player.capabilities.isCreativeMode) && getExperience(ist) < Integer.MAX_VALUE) {
					if(!player.capabilities.isCreativeMode)
						decreasePlayerExperience(player);
					increaseMedallionExperience(ist);
				}
			}
		}
	}

	//TODO look into this comment
	// I'm not 100% this is needed. You may be able to avoid this whole call by
	// using the method in the player class, might be worth testing
	// (player.addExperience(-1)?)
	public void decreasePlayerExperience(EntityPlayer player) {
		if(player.experience - (1.0F / (float) player.xpBarCap()) <= 0 && player.experienceLevel > getExperienceMinimum()) {
			decreasePlayerLevel(player);
			return;
		}
		player.experience -= Math.min(1.0F / (float) player.xpBarCap(), player.experience);
		player.experienceTotal -= Math.min(1, player.experienceTotal);
	}

	public void decreaseMedallionExperience(ItemStack ist) {
		setExperience(ist, getExperience(ist) - 1);
	}

	public void decreasePlayerLevel(EntityPlayer player) {
		player.experience = 1.0F - (1.0F / (float) player.xpBarCap());
		player.experienceTotal -= Math.min(1, player.experienceTotal);
		player.experienceLevel -= 1;
	}

	public void increasePlayerExperience(EntityPlayer player) {
		player.addExperience(1);
	}

	public void increaseMedallionExperience(ItemStack ist) {
		setExperience(ist, getExperience(ist) + 1);
	}

	public int getExperience(ItemStack stack) {
		return NBTHelper.getInteger("experience", stack);
	}

	public void setExperience(ItemStack stack, int i) {
		NBTHelper.setInteger("experience", stack, i);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		if(world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, ist);
		if(player.isSneaking())
			return super.onItemRightClick(ist, world, player, hand);
		//turn it on/off.

		int playerLevel = player.experienceLevel;
		while(player.experienceLevel < getExperienceMaximum() && playerLevel == player.experienceLevel && (getExperience(ist) > 0 || player.capabilities.isCreativeMode)) {
			increasePlayerExperience(player);
			if(!player.capabilities.isCreativeMode)
				decreaseMedallionExperience(ist);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, ist);
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		if(this.isEnabled(container))
			return null;

		return new FluidStack(ModFluids.fluidXpJuice, XpHelper.experienceToLiquid(getExperience(container)));
	}

	@Override
	public int getCapacity(ItemStack container) {
		return Integer.MAX_VALUE;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if(resource.getFluid() != ModFluids.fluidXpJuice)
			return 0;

		if(doFill) {
			setExperience(container, getExperience(container) + XpHelper.liquidToExperience(resource.amount));
		}

		return resource.amount;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		int experienceToRemove = Math.min(XpHelper.liquidToExperience(maxDrain), getExperience(container));

		if(doDrain) {
			setExperience(container, getExperience(container) - experienceToRemove);
		}

		return new FluidStack(ModFluids.fluidXpJuice, XpHelper.experienceToLiquid(experienceToRemove));
	}
}
