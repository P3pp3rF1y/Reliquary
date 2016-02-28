package xreliquary.util.potions;

import com.google.common.collect.HashMultimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Xeno on 11/8/2014.
 */
public class XRPotionHelper {

    public static boolean isItemEssence(ItemStack ist) {
        // essence not quite a thing just yet.
        return ist.getItem() instanceof ItemPotionEssence;
    }

    public static boolean isItemIngredient(ItemStack ist) {
        for (PotionIngredient ingredient : Settings.Potions.potionMap) {
            if (ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata()) {
                return true;
            }
        }
        return false;
    }

    public static int getPotionIdByName(String name) {
        for (Potion potion : Potion.potionTypes) {
            if (potion == null)
                continue;
            if (potion.getName().equals("potion." + name))
                return potion.getId();
        }
        return 0;
    }
    public static PotionIngredient getIngredient(ItemStack ist) {
        if (ist.getItem() instanceof ItemPotionEssence) {
            return new PotionEssence(ist.getTagCompound());
        }
        for (PotionIngredient ingredient : Settings.Potions.potionMap) {
            if (ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata())
                return ingredient;
        }
        return null;
    }

    private static int[] nonAugmentableEffects = new int[] {Potion.blindness.getId(), Potion.confusion.getId(), Potion.invisibility.getId(), Potion.nightVision.getId(), Potion.waterBreathing.getId()};

    public static boolean isAugmentablePotionEffect(PotionEffect effect) {
        for(int i=0;i<nonAugmentableEffects.length; i++) {
            if (nonAugmentableEffects[i] == effect.getPotionID())
                return false;
        }

        return true;
    }

    public static void addPotionInfo(PotionEssence essence, List list) {
        addPotionInfo(essence, list, true);
    }

    public static void addPotionInfo(PotionEssence essence, List list, boolean addEffectDescription) {
        if (essence.getEffects().size() > 0) {
            HashMultimap hashmultimap = HashMultimap.create();
            Iterator iterator1;

            if (essence.getEffects() != null && !essence.getEffects().isEmpty()) {
                iterator1 = essence.getEffects().iterator();

                while (iterator1.hasNext()) {
                    PotionEffect potioneffect = (PotionEffect) iterator1.next();
                    String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

                    if (map != null && map.size() > 0) {
                        Iterator iterator = map.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                            AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                            hashmultimap.put(((IAttribute) entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1);
                        }
                    }

                    if (potioneffect.getAmplifier() > 0) {
                        s1 = s1 + " " + (potioneffect.getAmplifier() + 1);
                    }

                    if (potioneffect.getDuration() > 20) {
                        s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
                    }

                    if (potion.isBadEffect()) {
                        list.add(EnumChatFormatting.RED + s1);
                    } else {
                        list.add(EnumChatFormatting.GRAY + s1);
                    }
                }
            } else {
                String s = StatCollector.translateToLocal("potion.empty").trim();
                list.add(EnumChatFormatting.GRAY + s);
            }

            if (!hashmultimap.isEmpty() && addEffectDescription) {
                list.add("");
                list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
                iterator1 = hashmultimap.entries().iterator();

                while (iterator1.hasNext()) {
                    Map.Entry entry1 = (Map.Entry) iterator1.next();
                    AttributeModifier attributemodifier2 = (AttributeModifier) entry1.getValue();
                    double d0 = attributemodifier2.getAmount();
                    double d1;

                    if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
                        d1 = attributemodifier2.getAmount();
                    } else {
                        d1 = attributemodifier2.getAmount() * 100.0D;
                    }

                    if (d0 > 0.0D) {
                        list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String) entry1.getKey())}));
                    } else if (d0 < 0.0D) {
                        d1 *= -1.0D;
                        list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String) entry1.getKey())}));
                    }
                }
            }
        }
    }
}
