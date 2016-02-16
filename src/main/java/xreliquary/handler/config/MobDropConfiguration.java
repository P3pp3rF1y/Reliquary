package xreliquary.handler.config;


import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

import java.util.HashMap;


public class MobDropConfiguration
{
	public static void loadMobDropProbabilities()
	{
		HashMap<String, Integer> drops = new HashMap<>(  );

		drops.put(Names.zombie_heart + "_base", ConfigurationHandler.getInt(Names.zombie_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.zombie_heart + "_looting", ConfigurationHandler.getInt(Names.zombie_heart + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.pigman_heart + "_base", ConfigurationHandler.getInt(Names.pigman_heart + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.pigman_heart + "_looting", ConfigurationHandler.getInt(Names.pigman_heart + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.rib_bone + "_base", ConfigurationHandler.getInt(Names.rib_bone + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.rib_bone + "_looting", ConfigurationHandler.getInt(Names.rib_bone + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.withered_rib + "_base", ConfigurationHandler.getInt(Names.withered_rib + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.withered_rib + "_looting", ConfigurationHandler.getInt(Names.withered_rib + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.spider_fangs + "_base", ConfigurationHandler.getInt(Names.spider_fangs + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.spider_fangs + "_looting", ConfigurationHandler.getInt(Names.spider_fangs + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.cave_spider_fangs + "_base", ConfigurationHandler.getInt(Names.cave_spider_fangs + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.cave_spider_fangs + "_looting", ConfigurationHandler.getInt(Names.cave_spider_fangs + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.blaze_molten_core + "_base", ConfigurationHandler.getInt(Names.blaze_molten_core + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.blaze_molten_core + "_looting", ConfigurationHandler.getInt(Names.blaze_molten_core + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.magma_cube_molten_core + "_base", ConfigurationHandler.getInt(Names.magma_cube_molten_core + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.magma_cube_molten_core + "_looting", ConfigurationHandler.getInt(Names.magma_cube_molten_core + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.frozen_core + "_base", ConfigurationHandler.getInt(Names.frozen_core + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.frozen_core + "_looting", ConfigurationHandler.getInt(Names.frozen_core + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.eye_of_the_storm + "_base", ConfigurationHandler.getInt(Names.eye_of_the_storm + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.eye_of_the_storm + "_looting", ConfigurationHandler.getInt(Names.eye_of_the_storm + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.bat_wing + "_base", ConfigurationHandler.getInt(Names.bat_wing + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.bat_wing + "_looting", ConfigurationHandler.getInt(Names.bat_wing + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.creeper_gland + "_base", ConfigurationHandler.getInt(Names.creeper_gland + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.creeper_gland + "_looting", ConfigurationHandler.getInt(Names.creeper_gland + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.ghast_gland + "_base", ConfigurationHandler.getInt(Names.ghast_gland + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.ghast_gland + "_looting", ConfigurationHandler.getInt(Names.ghast_gland + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.witch_hat + "_base", ConfigurationHandler.getInt(Names.witch_hat + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.witch_hat + "_looting", ConfigurationHandler.getInt(Names.witch_hat + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.squid_beak + "_base", ConfigurationHandler.getInt(Names.squid_beak + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.squid_beak + "_looting", ConfigurationHandler.getInt(Names.squid_beak + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.slime_pearl + "_base", ConfigurationHandler.getInt(Names.slime_pearl + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.slime_pearl + "_looting", ConfigurationHandler.getInt(Names.slime_pearl + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.ender_heart + "_base", ConfigurationHandler.getInt(Names.ender_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.ender_heart + "_looting", ConfigurationHandler.getInt(Names.ender_heart + "_looting", Names.mob_drop_probability, 5, 0, 100));

		Settings.MobDrops.mobDropProbabilities = drops;
		ConfigurationHandler.setCategoryTranslations(Names.mob_drop_probability, true);
	}
}
