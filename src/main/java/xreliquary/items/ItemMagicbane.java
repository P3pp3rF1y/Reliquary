package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

@ContentInit
public class ItemMagicbane extends ItemSword {

    public ItemMagicbane() {
        super(ToolMaterial.GOLD);
        this.setMaxDamage(16);
        this.setMaxStackSize(1);
        canRepair = true;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setUnlocalizedName(Names.magicbane);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        String value = LanguageHelper.getLocalization("item." + Names.magicbane + ".tooltip");
        for (String descriptionLine : value.split(";")) {
            if (descriptionLine != null && descriptionLine.length() > 0)
                list.add(descriptionLine);
        }
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base,
     * (Quality+1)*2 if correct blocktype, 1.5F if sword
     */
    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return block == Blocks.web ? 15.0F : 1.5F;
    }

    /**
     * Current implementations of this method in child classes do not use the
     * entry argument beside ev. They just raise the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase e, EntityLivingBase par3EntityLivingBase) {
        if (e != null) {
            int random = e.worldObj.rand.nextInt(16);
            switch (random) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    e.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 2));
                case 5:
                case 6:
                case 7:
                case 8:
                    e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
                    break;
                case 9:
                case 10:
                case 11:
                    e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));
                    break;
                case 12:
                case 13:
                    e.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 2));
                    e.addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 2));
                    break;
                case 14:
                    e.addPotionEffect(new PotionEffect(Potion.wither.id, 100, 2));
                    e.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 2));
                    break;
                default:
                    break;
            }
        }
        if (par3EntityLivingBase instanceof EntityPlayer) {
            NBTTagList enchants = par1ItemStack.getEnchantmentTagList();
            int bonus = 0;
            if (enchants != null) {
                for (int enchant = 0; enchant < enchants.tagCount(); enchant++) {
                    bonus += enchants.getCompoundTagAt(enchant).getShort("lvl");
                }
            }
            e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) par3EntityLivingBase), bonus + 4);
        }
        par1ItemStack.damageItem(1, par3EntityLivingBase);
        return true;
    }
}
