package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.items.util.fluid.FluidHandlerHeroMedallion;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.XpHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemHeroMedallion extends ItemToggleable implements IPedestalActionItem {

	public ItemHeroMedallion() {
		super(Names.Items.HERO_MEDALLION);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@SuppressWarnings("NullableProblems")
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
	public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("experience", String.valueOf(NBTHelper.getInteger("experience", ist))), ist, list);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GREEN + "XP"), list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, list);
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
				if((player.experienceLevel > getExperienceMinimum() || player.experience >= (1F / player.xpBarCap()) || player.capabilities.isCreativeMode) && getExperience(ist) < Settings.HeroMedallion.experienceLimit) {
					if(!player.capabilities.isCreativeMode)
						decreasePlayerExperience(player);
					increaseMedallionExperience(ist);
				}
			}
		}
	}

	private void decreasePlayerExperience(EntityPlayer player) {
		player.experience -= 1F / (float) player.xpBarCap();
		player.experienceTotal -= Math.min(1, player.experienceTotal);

		if(player.experience < 0F) {
			decreasePlayerLevel(player);
		}
	}

	private void decreaseMedallionExperience(ItemStack ist) {
		decreaseMedallionExperience(ist, 1);
	}

	private void decreaseMedallionExperience(ItemStack ist, int experience) {
		setExperience(ist, getExperience(ist) - experience);
	}

	private void decreasePlayerLevel(EntityPlayer player) {
		float experienceToRemove = -player.experience * player.xpBarCap();
		player.experienceLevel -= 1;
		player.experience = 1F - (experienceToRemove / player.xpBarCap());
	}

	private void increasePlayerExperience(EntityPlayer player) {
		player.addExperience(1);
	}

	private void increaseMedallionExperience(ItemStack ist) {
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

		RayTraceResult rayTraceResult = this.rayTrace(world, player, true);

		if(rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			int playerLevel = player.experienceLevel;
			while(player.experienceLevel < getExperienceMaximum() && playerLevel == player.experienceLevel && (getExperience(ist) > 0 || player.capabilities.isCreativeMode)) {
				increasePlayerExperience(player);
				if(!player.capabilities.isCreativeMode)
					decreaseMedallionExperience(ist);
			}
		} else {
			BlockPos hitPos = rayTraceResult.getBlockPos().add(rayTraceResult.sideHit.getDirectionVec());
			spawnXpOnGround(ist, world, hitPos);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, ist);
	}

	private void spawnXpOnGround(ItemStack ist, World world, BlockPos hitPos) {
		int xp = Math.min(Settings.HeroMedallion.experienceDrop, getExperience(ist));

		if(getExperience(ist) >= xp) {

			decreaseMedallionExperience(ist, xp);

			while(xp > 0) {
				int j = EntityXPOrb.getXPSplit(xp);
				xp -= j;
				world.spawnEntityInWorld(new EntityXPOrb(world, hitPos.getX(), hitPos.getY(), hitPos.getZ(), j));
			}
		}
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FluidHandlerHeroMedallion(stack);
	}

	private static final int RANGE = 5;
	private static final int REPAIR_STEP_XP = 5;
	private static final int COOLDOWN = 20;

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		List<BlockPos> posInRange = pedestal.getPedestalsInRange(RANGE);
		World world = pedestal.getTheWorld();

		for(BlockPos pedestalPos : posInRange) {
			IInventory pedestalInventory = (IInventory) world.getTileEntity(pedestalPos);
			if(pedestalInventory != null) {
				List<ItemStack> toRepair = getMendingItemsForRepair(pedestalInventory);

				for(ItemStack itemToRepair : toRepair) {
					int xpToRepair = Math.min(REPAIR_STEP_XP, getExperience(stack));
					int durabilityToRepair = Math.min(XpHelper.xpToDurability(xpToRepair), itemToRepair.getItemDamage());

					setExperience(stack, getExperience(stack) - XpHelper.durabilityToXp(durabilityToRepair));
					itemToRepair.setItemDamage(itemToRepair.getItemDamage() - durabilityToRepair);
				}
			}
		}
		pedestal.setActionCoolDown(COOLDOWN);
	}

	private List<ItemStack> getMendingItemsForRepair(IInventory inventory) {
		List<ItemStack> stacksToReturn = new ArrayList<>();

		for(int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);

			//only getting items that are more than 1 damaged to not waste xp
			if(stack != null && stack.isItemDamaged() && stack.getItemDamage() > 1 && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0) {
				stacksToReturn.add(stack);
			}
		}

		return stacksToReturn;
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		//nothing needed
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		//nothing needed
	}
}
