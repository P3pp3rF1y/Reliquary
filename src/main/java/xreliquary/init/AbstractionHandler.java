package xreliquary.init;

import com.google.common.reflect.ClassPath;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Level;
import xreliquary.lib.Reference;
import xreliquary.util.LogHelper;

public class AbstractionHandler {

    private static final String blocksPath = "xreliquary.blocks";
    private static final String itemsPath = "xreliquary.items";

    public static void init() {
        try {
            init(blocksPath);
            init(itemsPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Add support for ItemBlocks, for blocks.
    // TODO: Search subclasses.
    private static void init(String packageName) throws Exception {
        // Gets the classpath, and searches it for all classes in packageName.
        ClassPath classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
        for(ClassPath.ClassInfo info : classPath.getTopLevelClasses(packageName)) {
            Class objClass = Class.forName(info.getName());
            if(objClass.isAnnotationPresent(XRInit.class)) {
                Object obj = objClass.newInstance();

                // We've gotten the object, and confirmed it uses @XRInit, now let's check it for compatible types.
                if(obj instanceof Item) {
                    Item item = (Item) obj;
                    GameRegistry.registerItem(item, item.getUnlocalizedName());
                } else if(obj instanceof Block) {
                    Block block = (Block) obj;
                    GameRegistry.registerBlock(block, block.getUnlocalizedName());
                } else {
                    LogHelper.log(Level.WARN, "Class '" + info.getName() + "' is not a Block or an Item! You shouldn't be calling @XRInit on this! Ignoring!");
                }
            }
        }
    }

    public static Block getBlock(String blockName) {
        return Block.getBlockFromName(Reference.MOD_ID + ":" + blockName);
    }

    public static Item getItem(String itemName) {
        return (Item) Item.itemRegistry.getObject(itemName);
    }

}
