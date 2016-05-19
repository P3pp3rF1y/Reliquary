package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import xreliquary.blocks.BlockApothecaryMortar;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataProviderMortar extends CachedBodyDataProvider {
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBodyToCache(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        if (! (accessor.getBlock() instanceof BlockApothecaryMortar && accessor.getTileEntity() instanceof TileEntityMortar))
            return currenttip;

        TileEntityMortar mortar = (TileEntityMortar) accessor.getTileEntity();

        if (mortar == null)
            return currenttip;

        ItemStack[] ingredients = mortar.getItemStacks();

        if(ingredients[0] == null)
            return currenttip;

        String ingredient1 = ingredients[0].getDisplayName();
        String ingredient2 = ingredients[1] == null ? "" : ", " + ingredients[1].getDisplayName();
        String ingredient3 = ingredients[2] == null ? "" : ", " + ingredients[2].getDisplayName();

        currenttip.add(ingredient1 + ingredient2 + ingredient3);

        if(ingredients[1] == null)
            return currenttip;

        List<PotionIngredient> potionIngredients = new ArrayList<PotionIngredient>();
        potionIngredients.add(XRPotionHelper.getIngredient(ingredients[0]));
        potionIngredients.add(XRPotionHelper.getIngredient(ingredients[1]));
        if (ingredients[2] != null)
            potionIngredients.add(XRPotionHelper.getIngredient(ingredients[2]));

        PotionEssence essence = new PotionEssence(potionIngredients.toArray(new PotionIngredient[potionIngredients.size()]));
        if (essence.effects.size() > 0) {
            currenttip.add(ChatFormatting.WHITE + I18n.translateToLocal("waila.xreliquary.mortar.result") + ChatFormatting.RESET);

            List<String> effectLines = new ArrayList<>();

            XRPotionHelper.addPotionInfo(essence, effectLines, false);

            StringBuilder sb = new StringBuilder();
            int effectsInLine = 0;
            Iterator<String> iterator = effectLines.iterator();
            while (iterator.hasNext()) {
                effectsInLine++;
                String line = iterator.next();
                sb.append(line);

                //display 2 effects per line
                if (effectsInLine == 2) {
                    effectsInLine = 0;
                    currenttip.add(sb.toString());
                    sb = new StringBuilder();
                    continue;
                }
                if (iterator.hasNext())
                    sb.append(", ");

            }
            if(sb.length() > 0)
                currenttip.add(sb.toString());
        }
        return currenttip;
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
