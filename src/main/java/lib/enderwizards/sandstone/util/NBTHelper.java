package lib.enderwizards.sandstone.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelper {

    public static int getShort(String s, ItemStack ist) {
        NBTTagCompound tagCompound = getTag(ist);
        return tagCompound.getShort(s);
    }

    public static void setShort(String s, ItemStack ist, int i) {
        NBTTagCompound tagCompound = getTag(ist);
        tagCompound.setShort(s, (short) i);
        setTag(ist, tagCompound);
    }

    public static int getInteger(String s, ItemStack ist) {
        NBTTagCompound tagCompound = getTag(ist);
        return tagCompound.getInteger(s);
    }

    public static void setInteger(String s, ItemStack ist, int i) {
        NBTTagCompound tagCompound = getTag(ist);
        tagCompound.setInteger(s, i);
        setTag(ist, tagCompound);
    }

    public static boolean getBoolean(String s, ItemStack ist) {
        NBTTagCompound tagCompound = getTag(ist);
        return tagCompound.getBoolean(s);

    }

    public static void setBoolean(String s, ItemStack ist, boolean b) {
        NBTTagCompound tagCompound = getTag(ist);
        tagCompound.setBoolean(s, b);
        setTag(ist, tagCompound);
    }


    public static String getString(String s, ItemStack ist) {
        NBTTagCompound tagCompound = getTag(ist);
        return tagCompound.getString(s);

    }

    public static void setString(String s, ItemStack ist, String s1) {
        NBTTagCompound tagCompound = getTag(ist);
        tagCompound.setString(s, s1);
        setTag(ist, tagCompound);
    }

//    public static UUID getUUID(ItemStack ist) {
//        if (ist.getTagCompound() == null) ist.setTagCompound(new NBTTagCompound());
//        String stringUUID = ist.getTagCompound().getString("UUID");
//        if (stringUUID.equals("")) {
//            UUID newUUID = UUID.randomUUID();
//            stringUUID = newUUID.toString();
//            ist.getTagCompound().setString("UUID", stringUUID);
//        }
//        return UUID.fromString(ist.getTagCompound().getString("UUID"));
//    }

    public static NBTTagCompound getTag(ItemStack ist) {
        //UUID tagUUID = getUUID(ist);
        if (ist.getTagCompound() == null)
            resetTag(ist);
        //WorldDataHandler.<ItemData>get(ItemData.class).getTag(tagUUID);
        return ist.getTagCompound();
    }

    public static boolean hasKey(String s, ItemStack stack) {
        NBTTagCompound tag = getTag(stack);
        return tag.hasKey(s);
    }

    public static void setTag(ItemStack ist, NBTTagCompound nbt) {
        //UUID tagUUID = getUUID(ist);
        //WorldDataHandler.<ItemData>get(ItemData.class).setTag(tagUUID, nbt);
        ist.setTagCompound(nbt);
    }

    public static void resetTag(ItemStack ist) {
//        UUID tagUUID = getUUID(ist);
//        WorldDataHandler.<ItemData>get(ItemData.class).resetTag(tagUUID);
        setTag(ist, new NBTTagCompound());
    }

    public NBTTagList getTagList(String s, ItemStack ist) {
        //default for me, I'm usually getting a list of tag compounds.
        return getTagList(s, ist, 10);
    }

    public NBTTagList getTagList(String s, ItemStack ist, int type) {
        return getTag(ist).getTagList(s, type);
    }
}
