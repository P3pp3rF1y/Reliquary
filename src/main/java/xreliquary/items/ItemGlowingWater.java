package xreliquary.items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;


public class ItemGlowingWater extends ItemBase {

    public ItemGlowingWater() {
        super(Names.glowing_water);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        canRepair = false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack ist) {
        return new ItemStack(ModItems.potion, 1, 0);
    }

    // returns an empty vial when used in crafting recipes.
    @Override
    public boolean hasContainerItem(ItemStack ist) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        world.playSound(null, player.getPosition(), SoundEvents.entity_arrow_shoot, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        EntityGlowingWater glowingWater = new EntityGlowingWater(world, player);
        glowingWater.func_184538_a(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.7F, 1.0F);
        world.spawnEntityInWorld(glowingWater);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
