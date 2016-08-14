package lib.enderwizards.sandstone.init;

import com.google.common.reflect.ClassPath;
import cpw.mods.fml.common.registry.GameRegistry;
import lib.enderwizards.sandstone.Sandstone;
import lib.enderwizards.sandstone.blocks.ICustomItemBlock;
import lib.enderwizards.sandstone.mod.ModRegistry;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Content takes care of initializing all of your blocks/items for you.
 * <p/>
 * Put the @ContentInit annotation on any block/item class (or subclass) within the blocksPath/itemsPath you assigned for your @SandstoneMod, and Content will take care of the rest.
 * Content will ignore any classes and subclasses without the annotation.
 *
 * @author TheMike
 * @author x3n0ph0b3
 */
public class Content {

    public static Content DEFAULT = new Content("libsandstone");

    //String list that contains all the object identities of blocks and items being added by the initialization routine
    //this allows the content handler to hold a static list of objects registered, which I can then use
    //to automate the recipe disabling process. Experimental. Sorry for mucking around in your library, Mike. XD
    public List<String> registeredObjectNames = new ArrayList<String>();

    private String modId;

    public Content(String modId) {
        this.modId = modId;
    }

    /**
     * Initializes Content to search within packageName for blocks/items.
     *
     * @param classLoader The ClassLoader from any class inside the mod. Most reliably, use the class with the @Mod annotation. This must come from a class within the mod's jar file.
     * @param packageName The full package name to search within. It doesn't recursively search in child packages.
     * @throws Exception
     */
    public void init(ClassLoader classLoader, String packageName) throws Exception {
        // Gets the classpath, and searches it for all classes in packageName.
        ClassPath classPath = ClassPath.from(classLoader);
        for (ClassPath.ClassInfo info : classPath.getTopLevelClasses(packageName)) {
            Class objClass = Class.forName(info.getName());
            checkAndRegister(objClass);
            for (Class subClass : objClass.getClasses()) {
                checkAndRegister(subClass);
            }
        }
    }

    private void checkAndRegister(Class objClass) throws Exception {
        if (objClass.isAnnotationPresent(ContentInit.class)) {
            Object obj = objClass.newInstance();

            // We've gotten the object, and confirmed it uses @XRInit, now let's check it for compatible types.
            if (obj instanceof Item) {
                Item item = (Item) obj;
                GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
                if (!registeredObjectNames.contains(ContentHelper.getIdent(item)))
                    registeredObjectNames.add(ContentHelper.getIdent(item));
            } else if (obj instanceof Block) {
                Block block = (Block) obj;
                if (obj instanceof ICustomItemBlock) {
                    GameRegistry.registerBlock(block, ((ICustomItemBlock) obj).getCustomItemBlock(), block.getUnlocalizedName().substring(5));
                    if (!registeredObjectNames.contains(ContentHelper.getIdent(block)))
                        registeredObjectNames.add(ContentHelper.getIdent(block));
                } else {
                    GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
                    if (!registeredObjectNames.contains(ContentHelper.getIdent(block)))
                        registeredObjectNames.add(ContentHelper.getIdent(block));
                }
            } else {
                Sandstone.LOGGER.warn("Class '" + objClass.getName() + "' is not a Block or an Item! You shouldn't be calling @ContentInit on this! Ignoring!");
            }
        }
    }

    public Block getBlock(String blockName) {
        String selection = blockName;
        if (!selection.contains(":"))
            selection = ModRegistry.getID(modId) + ":" + selection;
        if (selection.indexOf(":") == 0)
            selection = selection.substring(1);
        return (Block) Block.blockRegistry.getObject(selection);
    }

    public Item getItem(String itemName) {
        String selection = itemName;
        if (!selection.contains(":"))
            selection = ModRegistry.getID(modId) + ":" + selection;
        if (selection.indexOf(":") == 0)
            selection = selection.substring(1);
        return (Item) Item.itemRegistry.getObject(selection);
    }

    public Item getItemBlock(String blockName) {
        String selection = blockName;
        if (!selection.contains(":"))
            selection = ModRegistry.getID(modId) + ":" + selection;
        if (selection.indexOf(":") == 0)
            selection = selection.substring(1);
        return Item.getItemFromBlock((Block) Block.blockRegistry.getObject(selection));
    }

}
