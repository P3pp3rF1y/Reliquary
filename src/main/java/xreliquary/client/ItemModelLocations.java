package xreliquary.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.ArrayList;

public class ItemModelLocations {
    private static ItemModelLocations instance;

    public static ItemModelLocations getInstance() {
        if (instance == null) {
            instance = new ItemModelLocations();
        }

        return instance;
    }

    private ArrayList<ModelResourceLocation> models = new ArrayList<>();

    public static int INFERNAL_TEAR = 0;
    public static int INFERNAL_TEAR_EMPTY = 1;
    public static int ROD_OF_LYSSA = 2;
    public static int ROD_OF_LYSSA_CAST = 3;

    private ItemModelLocations() {
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.infernal_tear, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.infernal_tear_empty, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.rod_of_lyssa, "inventory"));
        models.add(new ModelResourceLocation(Reference.DOMAIN + Names.rod_of_lyssa + "_cast", "inventory"));
    }

    public ModelResourceLocation getModel(int modelIndex) {
        return models.get(modelIndex);
    }
}
