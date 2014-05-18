package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@XRInit
public class ItemClawsOfTheFiredrinker extends ItemBase {

        public ItemClawsOfTheFiredrinker() {
            super(Reference.MOD_ID, Names.claws_of_the_firedrinker);
            this.setCreativeTab(Reliquary.CREATIVE_TAB);
            this.setMaxDamage(0);
            this.setMaxStackSize(1);
            canRepair = false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean hasEffect(ItemStack stack) {
            return true;
        }

    //this item's effects are handled in events
}
