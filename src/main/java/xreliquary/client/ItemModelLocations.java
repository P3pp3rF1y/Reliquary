package xreliquary.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.ArrayList;

public class ItemModelLocations {
	private static ItemModelLocations instance;

	public static ItemModelLocations getInstance() {
		if(instance == null) {
			instance = new ItemModelLocations();
		}

		return instance;
	}

	private ArrayList<ModelResourceLocation> models = new ArrayList<>();

	public static int INFERNAL_TEAR = 0;
	public static int INFERNAL_TEAR_EMPTY = 1;
	public static int ROD_OF_LYSSA_CAST = 3;
	public static final int POTION = 4;
	public static final int POTION_SPLASH = 5;
	public static final int POTION_LINGERING = 6;

	private ItemModelLocations() {
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.INFERNAL_TEAR, "inventory"));
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.INFERNAL_TEAR_EMPTY, "inventory"));
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.ROD_OF_LYSSA, "inventory"));
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.ROD_OF_LYSSA + "_cast", "inventory"));
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.POTION, "inventory"));
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.POTION_SPLASH, "inventory"));
		models.add(new ModelResourceLocation(Reference.DOMAIN + Names.Items.POTION_LINGERING, "inventory"));
	}

	public ModelResourceLocation getModel(int modelIndex) {
		return models.get(modelIndex);
	}
}
