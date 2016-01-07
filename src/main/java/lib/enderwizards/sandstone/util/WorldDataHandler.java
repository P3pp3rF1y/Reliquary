package lib.enderwizards.sandstone.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by chylex for hee. Borrowed for saving NBT data.
 */
public final class WorldDataHandler {
        private static WorldDataHandler instance;

        public static void register(){
            if (instance == null) MinecraftForge.EVENT_BUS.register(instance = new WorldDataHandler());
        }

        public static <T> T get(Class<? extends WorldSaveFile> cls){
            WorldSaveFile savefile = instance.cache.get(cls);

            if (savefile == null){
                try{
                    instance.cache.put(cls,savefile = cls.newInstance());

                    File file = new File(instance.worldSaveDir,savefile.filename);

                    if (file.exists()){
                        try{
                            savefile.loadFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(file)));
                        }catch(IOException ioe){ //uh.. do something with this exception I guess
                        }
                    }
                    else savefile.loadFromNBT(new NBTTagCompound());
                }catch(Exception e){
                    throw new RuntimeException("Could not construct a new instance of WorldSaveFile - " + cls.getName(),e);
                }
            }


            return (T)savefile;
        }

        public static void forceSave(){
            instance.saveModified();
        }

        private final Map<Class<? extends WorldSaveFile>,WorldSaveFile> cache = new IdentityHashMap<Class<? extends WorldSaveFile>,WorldSaveFile>();
        private File worldSaveDir;
        private String worldIdentifier = "";

        private WorldDataHandler(){}

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load e){
            if (e.world.isRemote)return;

            String id = e.world.getSaveHandler().getWorldDirectoryName() + e.world.getWorldInfo().getWorldName() + e.world.getWorldInfo().getSeed();

            if (!worldIdentifier.equals(id)){
                cache.clear();
                worldIdentifier = id;

                File root = DimensionManager.getCurrentSaveRootDirectory();

                if (root != null){
                    worldSaveDir = new File(root,"libSandStoneData");

                    if (!worldSaveDir.exists())worldSaveDir.mkdirs();
                }
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save e){
            saveModified();
        }

        private void saveModified(){
            if (worldSaveDir == null)return;

            for(WorldSaveFile savefile:cache.values()){
                if (savefile.wasModified()){
                    NBTTagCompound nbt = new NBTTagCompound();
                    savefile.saveToNBT(nbt);

                    try{
                        CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(new File(worldSaveDir, savefile.filename)));
                    } catch(Exception ex) { //uh.. do something with this exception I guess
                    }
                }
            }
        }
}
