package xreliquary.items;

import net.minecraft.item.ItemStack;

public interface IVoidUpgradable {

        public abstract int getCapacity(ItemStack ist);
        
        public abstract boolean upgradeCapacity(ItemStack ist);
}
