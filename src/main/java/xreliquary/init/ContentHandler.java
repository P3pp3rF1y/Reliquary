package xreliquary.init;

import com.google.common.reflect.ClassPath;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Level;
import xreliquary.Reliquary;
import xreliquary.lib.Reference;
import xreliquary.util.LogHelper;

public class ContentHandler {

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

    private static void init(String packageName) throws Exception {
        // Gets the classpath, and searches it for all classes in packageName.
        ClassPath classPath = ClassPath.from(Reliquary.class.getClassLoader());
        for(ClassPath.ClassInfo info : classPath.getTopLevelClasses(packageName)) {
            Class objClass = Class.forName(info.getName());
            checkAndRegister(objClass);
            for(Class subClass : objClass.getClasses()) {
                checkAndRegister(subClass);
            }
        }
    }

    private static void checkAndRegister(Class objClass) throws Exception {
        if(objClass.isAnnotationPresent(XRInit.class)) {
            Object obj = objClass.newInstance();

            // We've gotten the object, and confirmed it uses @XRInit, now let's check it for compatible types.
            if(obj instanceof Item) {
                Item item = (Item) obj;
                GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
            } else if(obj instanceof Block) {
                Block block = (Block) obj;
                if(((XRInit) objClass.getAnnotation(XRInit.class)).itemBlock() != XRInit.class)
                    GameRegistry.registerBlock(block, ((XRInit) objClass.getAnnotation(XRInit.class)).itemBlock(), block.getUnlocalizedName().substring(5));
                else
                    GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
            } else {
                LogHelper.log(Level.WARN, "Class '" + objClass.getName() + "' is not a Block or an Item! You shouldn't be calling @XRInit on this! Ignoring!");
            }
        }
    }

    public static Block getBlock(String blockName) {
        String selection = blockName;
        if(!selection.contains("!"))
            selection = Reference.MOD_ID + ":" + selection;
        return (Block) Block.blockRegistry.getObject(selection);
    }

    public static Item getItem(String itemName) {
        String selection = itemName;
        if(!selection.contains("!"))
            selection = Reference.MOD_ID + ":" + selection;
        return (Item) Item.itemRegistry.getObject(selection);
    }

    public static Item getItemBlock(String blockName) {
        String selection = blockName;
        if(!selection.contains("!"))
            selection = Reference.MOD_ID + ":" + selection;
        return Item.getItemFromBlock((Block) Block.blockRegistry.getObject(selection));
    }

}
