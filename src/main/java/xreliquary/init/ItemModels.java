package xreliquary.init;

import net.minecraft.client.resources.model.ModelResourceLocation;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.ArrayList;

public class ItemModels {
    private static ItemModels instance;

    public static ItemModels getInstance() {
        if (instance == null) {
            instance = new ItemModels();
        }

        return instance;
    }

    private ArrayList<ModelResourceLocation> models = new ArrayList<>();

    public static int INFERNAL_TEAR = 0;
    public static int INFERNAL_TEAR_EMPTY = 1;
    public static int ROD_OF_LYSSA = 2;
    public static int ROD_OF_LYSSA_CAST = 3;
    public static int VOID_TEAR = 4;
    public static int VOID_TEAR_EMPTY = 5;

    private ItemModels() {
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.infernal_tear, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.infernal_tear_empty, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.rod_of_lyssa, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.rod_of_lyssa + "_cast", "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.void_tear, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.void_tear_empty, "inventory"));
    }

    public ModelResourceLocation getModel(int modelIndex) {
        return models.get(modelIndex);
    }
}
