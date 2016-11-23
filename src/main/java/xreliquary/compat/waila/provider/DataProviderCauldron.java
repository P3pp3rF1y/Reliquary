package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModBlocks;
import xreliquary.util.LanguageHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataProviderCauldron extends CachedBodyDataProvider {
	@Nonnull
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(ModBlocks.apothecaryCauldron);
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBodyToCache(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> currenttip = new ArrayList<>();

		if(!(accessor.getBlock() instanceof BlockApothecaryCauldron && accessor.getTileEntity() instanceof TileEntityCauldron))
			return currenttip;

		TileEntityCauldron cauldron = (TileEntityCauldron) accessor.getTileEntity();

		if(cauldron == null || cauldron.potionEssence == null)
			return currenttip;

		if(!cauldron.hasNetherwart)
			currenttip.add(ChatFormatting.RED + LanguageHelper.getLocalization("waila.xreliquary.cauldron.missing_netherwart") + ChatFormatting.RESET);

		if(!cauldron.hasGunpowder && cauldron.hasDragonBreath)
			currenttip.add(ChatFormatting.RED + LanguageHelper.getLocalization("waila.xreliquary.cauldron.missing_gunpowder") + ChatFormatting.RESET);

		StringBuilder ingredients = new StringBuilder();
		if(cauldron.redstoneCount > 0) {
			ingredients.append(cauldron.redstoneCount);
			ingredients.append("x");
			ingredients.append(new ItemStack(Items.REDSTONE).getDisplayName());
			ingredients.append(" ");
		}

		if(cauldron.glowstoneCount > 0) {
			ingredients.append(cauldron.glowstoneCount);
			ingredients.append("x");
			ingredients.append(new ItemStack(Items.GLOWSTONE_DUST).getDisplayName());
		}

		currenttip.add(ingredients.toString());

		if (cauldron.hasDragonBreath) {
			currenttip.add(ChatFormatting.WHITE.toString() + cauldron.getLiquidLevel() + "x" + LanguageHelper.getLocalization("waila.xreliquary.cauldron.lingering") + ChatFormatting.RESET);
		}
		else if(cauldron.hasGunpowder) {
			currenttip.add(ChatFormatting.WHITE.toString() + cauldron.getLiquidLevel() + "x" + LanguageHelper.getLocalization("waila.xreliquary.cauldron.splash") + ChatFormatting.RESET);
		} else {
			currenttip.add(ChatFormatting.WHITE.toString() + cauldron.getLiquidLevel() + "x" + LanguageHelper.getLocalization("waila.xreliquary.cauldron.potion") + ChatFormatting.RESET);
		}

		addEffectsInfo(currenttip, cauldron.potionEssence);

		return currenttip;
	}

	private void addEffectsInfo(List<String> currenttip, PotionEssence essence) {
		List<String> effectLines = new ArrayList<>();

		XRPotionHelper.addPotionInfo(essence, effectLines);

		StringBuilder sb = new StringBuilder();
		int effectsInLine = 0;
		Iterator<String> iterator = effectLines.iterator();
		while(iterator.hasNext()) {
			effectsInLine++;
			String line = iterator.next();
			sb.append(line);

			//display 2 effects per line
			if(effectsInLine == 2) {
				effectsInLine = 0;
				currenttip.add(sb.toString());
				sb = new StringBuilder();
				continue;
			}
			if(iterator.hasNext())
				sb.append(", ");

		}
		if(sb.length() > 0)
			currenttip.add(sb.toString());
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return null;
	}
}
