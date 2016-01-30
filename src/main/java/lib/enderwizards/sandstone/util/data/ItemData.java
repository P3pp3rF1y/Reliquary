package lib.enderwizards.sandstone.util.data;

import lib.enderwizards.sandstone.util.misc.Duo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import lib.enderwizards.sandstone.util.WorldSaveFile;

import java.util.*;

/**
 * Created by Xeno on 10/19/2014.
 */
public class ItemData extends WorldSaveFile {
    private List<Duo<UUID, NBTTagCompound>> itemMap = new ArrayList<Duo<UUID, NBTTagCompound>>();

    public ItemData() {
        super("itemdata.nbt");
    }

    @Override
    protected void onSave(NBTTagCompound nbt) {
        //NBTTagList tagList = nbt.getTagList("Items", 10);
        NBTTagList itemsToSave = new NBTTagList();
        for (Duo<UUID, NBTTagCompound> item : itemMap) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setString("UUID", item.one.toString());
            itemTag.setTag("NBTTag", item.two);
            itemsToSave.appendTag(itemTag);
        }
        nbt.setTag("Items", itemsToSave);
    }

    @Override
    protected void onLoad(NBTTagCompound nbt) {
        if (nbt.getTagList("Items", 10) == null)
            nbt.setTag("Items", new NBTTagList());
        NBTTagList itemsToLoad = nbt.getTagList("Items", 10);
        for (int i = 0; i < itemsToLoad.tagCount(); ++i) {
            NBTTagCompound itemTag = itemsToLoad.getCompoundTagAt(i);
            Duo<UUID, NBTTagCompound> duoTag = new Duo(UUID.fromString(itemTag.getString("UUID")), itemTag.getTag("NBTTag"));
            itemMap.add(duoTag);
        }
    }

    public NBTTagCompound getTag(UUID itemUUID) {
        for (Duo<UUID, NBTTagCompound> itemTag : itemMap) {
            if (itemTag.one.equals(itemUUID))
                return itemTag.two;
        }
        //didn't find the tag, make a new one.
        NBTTagCompound emptyCompound = new NBTTagCompound();
        setTag(itemUUID, emptyCompound);
        return emptyCompound;
    }

    public void setTag(UUID itemUUID, NBTTagCompound nbt) {
        resetTag(itemUUID);
        itemMap.add(new Duo<UUID, NBTTagCompound>(itemUUID, nbt));
        this.setModified();
    }

    public void resetTag(UUID itemUUID) {
        List<Duo<UUID, NBTTagCompound>> listSlatedForRemoval = new ArrayList<Duo<UUID, NBTTagCompound>>();
        for (Duo<UUID, NBTTagCompound> itemTag : itemMap) {
            if (itemTag.one.equals(itemUUID)) {
                listSlatedForRemoval.add(itemTag);
                break;
            }
        }
        for (Duo<UUID, NBTTagCompound> removeThisTag : listSlatedForRemoval)
            itemMap.remove(removeThisTag);
    }
}
