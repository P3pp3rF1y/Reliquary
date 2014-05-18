package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@XRInit
public class ItemKrakenShellFragment extends ItemBase {

        public ItemKrakenShellFragment() {
            super(Reference.MOD_ID, Names.kraken_shell_fragment);
            this.setCreativeTab(Reliquary.CREATIVE_TAB);
            this.setMaxDamage(0);
            this.setMaxStackSize(64);
            canRepair = false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean hasEffect(ItemStack stack) {
            return true;
        }
}
