package xreliquary.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import xreliquary.items.alkahestry.AlkahestryChargingRecipe;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryDrainRecipe;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.Arrays;

public class XRRecipes {

	//this version of the addRecipe method checks first to see if the recipe is disabled in our automated recipe-disabler config
	//if any component of the item is in the recipe disabler list, it will ALSO block the recipe automatically.
	//override disabler forces the recipe to evaluate anyway. This occurs for items that don't fall into XR scope, and thus shouldn't be evaluated.
	public static void addRecipe(boolean isShapeless, boolean overrideDisabler, ItemStack result, Object... params) {
		//TODO remove overrideDisabler - people should know what they're doing when disabling blocks and items
		if(result.getItem() == null || result.getItem().getRegistryName() == null || Arrays.asList(params).contains(null))
			return;

		ResourceLocation rl = result.getItem().getRegistryName();
		if(Settings.disabledItemsBlocks.contains(rl.getResourcePath()))
			return;

		for(Object o : params) {
			if(!(o instanceof Block || o instanceof Item || o instanceof ItemStack))
				continue;

			if(o instanceof Block) {
				rl = ((Block) o).getRegistryName();
			} else if(o instanceof Item && ((Item) o).getRegistryName() != null) {
				rl = ((Item) o).getRegistryName();
			} else if(o instanceof ItemStack) {
				rl = ((ItemStack) o).getItem().getRegistryName();
			}
			if(Settings.disabledItemsBlocks.contains(rl.getResourcePath()))
				return;
		}

		if(!isShapeless)
			GameRegistry.addRecipe(result, params);
		else
			GameRegistry.addShapelessRecipe(result, params);
	}

	public static void init() {
		// tome and alkahestry recipes
		GameRegistry.addRecipe(new AlkahestryDrainRecipe());
		GameRegistry.addRecipe(new AlkahestryChargingRecipe());
		GameRegistry.addRecipe(new AlkahestryCraftingRecipe());

		RecipeSorter.register(Reference.MOD_ID + ":alkahest_crafting", AlkahestryCraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
		RecipeSorter.register(Reference.MOD_ID + ":alkahest_charge", AlkahestryChargingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_crafting");
		RecipeSorter.register(Reference.MOD_ID + ":alkahest_drain", AlkahestryDrainRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_charge");

		//misc recipes
		//frozen cores to make packed ice.
		addRecipe(true, true, new ItemStack(Blocks.packed_ice, 1, 0), Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, FROZEN_CORE);

		//apothecary mortar recipe
		addRecipe(false, false, new ItemStack(ModBlocks.apothecaryMortar, 1, 0), "gng", "ngn", "nnn", 'n', Blocks.quartz_block, 'g', CREEPER_GLAND);

		//apothecary cauldron recipe
		addRecipe(false, false, new ItemStack(ModBlocks.apothecaryCauldron, 1, 0), "gng", "ici", "nmn", 'g', CREEPER_GLAND, 'n', NEBULOUS_HEART, 'i', INFERNAL_CLAW, 'c', Items.cauldron, 'm', MOLTEN_CORE);

		//alkahestry tome
		if(Settings.EasyModeRecipes.alkahestryTome)
			addRecipe(true, false, new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage()), Items.book, ModItems.witchHat, MOLTEN_CORE, WITHER_SKULL);
		else
			addRecipe(true, false, new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage()), MOLTEN_CORE, ModItems.witchHat, STORM_EYE, CREEPER_GLAND, Items.book, SLIME_PEARL, CHELICERAE, WITHER_SKULL, NEBULOUS_HEART);

		//glowstone altar
		if(Settings.EasyModeRecipes.altar)
			addRecipe(true, false, new ItemStack(ModBlocks.alkahestryAltar, 1), Blocks.obsidian, Blocks.redstone_lamp, NEBULOUS_HEART, CREEPER_GLAND);
		else
			addRecipe(false, false, new ItemStack(ModBlocks.alkahestryAltar, 1), "dnd", "olo", "dgd", 'd', Items.glowstone_dust, 'n', NEBULOUS_HEART, 'o', Blocks.obsidian, 'l', Blocks.redstone_lamp, 'g', CREEPER_GLAND);

		//fertile_lilypad
		addRecipe(true, false, new ItemStack(ModBlocks.fertileLilypad, 1), FERTILE_ESSENCE, FERTILE_ESSENCE, FERTILE_ESSENCE, Blocks.waterlily);

		//wraith node
		addRecipe(true, false, new ItemStack(ModBlocks.wraithNode, 1), NEBULOUS_HEART, Items.emerald);

		//interdiction torch
		if(Settings.EasyModeRecipes.interdictionTorch)
			addRecipe(false, false, new ItemStack(ModBlocks.interdictionTorch, 4, 0), "bm", "nr", 'b', BAT_WING, 'm', MOLTEN_CORE, 'n', NEBULOUS_HEART, 'r', Items.blaze_rod);
		else
			addRecipe(false, false, new ItemStack(ModBlocks.interdictionTorch, 4, 0), " n ", "mdm", "bwb", 'n', NEBULOUS_HEART, 'm', MOLTEN_CORE, 'd', Items.diamond, 'b', Items.blaze_rod, 'w', BAT_WING);

		// glowy bread
		addRecipe(true, false, new ItemStack(ModItems.glowingBread, 3), Items.bread, Items.bread, Items.bread, ModItems.glowingWater);

		//fertile essence
		if(Settings.EasyModeRecipes.fertileEssence)
			addRecipe(true, false, FERTILE_ESSENCE, RIB_BONE, CREEPER_GLAND, new ItemStack(Items.dye, 1, Reference.GREEN_DYE_META), SLIME_PEARL);
		else
			addRecipe(false, false, FERTILE_ESSENCE, "gbg", "scs", "gbg", 'g', CREEPER_GLAND, 'b', RIB_BONE, 's', SLIME_PEARL, 'c', new ItemStack(Items.dye, 1, Reference.GREEN_DYE_META));

		// bullets...
		// empty cases back into nuggets
		addRecipe(true, true, new ItemStack(Items.gold_nugget, 1), bullet(1, 0), bullet(1, 0), bullet(1, 0), bullet(1, 0));
		// neutral
		addRecipe(true, false, bullet(8, 1), Items.flint, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
		// exorcist
		addRecipe(true, false, bullet(8, 2), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), ZOMBIE_HEART);
		// blaze
		addRecipe(true, false, bullet(8, 3), Items.blaze_powder, Items.blaze_rod, Items.gold_nugget, Items.gold_nugget);
		// ender
		addRecipe(true, false, bullet(8, 4), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), NEBULOUS_HEART);
		// concussive
		addRecipe(true, false, bullet(8, 5), Items.slime_ball, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
		// buster
		addRecipe(true, false, bullet(8, 6), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), CREEPER_GLAND);
		// seeker, the only thing with an easy mode recipe
		if(Settings.EasyModeRecipes.seekerShot)
			addRecipe(true, false, bullet(8, 7), LAPIS, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
		else
			addRecipe(true, false, bullet(4, 7), LAPIS, SLIME_PEARL, Items.gold_nugget, Items.gunpowder);
		// sand
		addRecipe(true, false, bullet(8, 8), Blocks.sandstone, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
		// storm
		addRecipe(true, false, bullet(8, 9), CREEPER_GLAND, CREEPER_GLAND, Items.gold_nugget, Items.gold_nugget, Items.gunpowder);
		// frozen shot TODO
		// venom shot TODO
		// fertile shot TODO
		// rage shot TODO
		// traitor shot TODO
		// calm shot TODO
		// molten shot TODO

		// magazines...
		addRecipe(false, false, magazine(5, 0), "i i", "igi", "sis", 's', Blocks.stone, 'i', Items.iron_ingot, 'g', Blocks.glass);

		// neutral
		addRecipe(true, false, magazine(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), magazine(1, 0));
		// exorcist
		addRecipe(true, false, magazine(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), bullet(1, 2), magazine(1, 0));
		// blaze
		addRecipe(true, false, magazine(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), bullet(1, 3), magazine(1, 0));
		// ender
		addRecipe(true, false, magazine(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), bullet(1, 4), magazine(1, 0));
		// venom
		addRecipe(true, false, magazine(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), magazine(1, 0));
		// buster
		addRecipe(true, false, magazine(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), bullet(1, 6), magazine(1, 0));
		// seeker
		addRecipe(true, false, magazine(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), magazine(1, 0));
		// sand
		addRecipe(true, false, magazine(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), bullet(1, 8), magazine(1, 0));
		// storm
		addRecipe(true, false, magazine(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), bullet(1, 9), magazine(1, 0));

		// gunpart 0 = grip, 1 = barrel, 2 = mechanism
		addRecipe(false, false, gunPart(1, 0), "iii", "imi", "ici", 'i', Items.iron_ingot, 'c', magazine(1, 0), 'm', Items.magma_cream);
		addRecipe(false, false, gunPart(1, 1), "iii", "eme", "iii", 'i', Items.iron_ingot, 'e', NEBULOUS_HEART, 'm', Items.magma_cream);
		addRecipe(false, false, gunPart(1, 2), "iib", "rmi", "iii", 'i', Items.iron_ingot, 'b', Blocks.stone_button, 'r', Items.blaze_rod, 'm', MOLTEN_CORE);

		// handgun
		addRecipe(false, false, new ItemStack(ModItems.handgun, 1, 0), "bim", "isi", "igi", 'i', Items.iron_ingot, 'b', gunPart(1, 1), 'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', SLIME_PEARL);

		// fortune coin
		if(Settings.EasyModeRecipes.fortuneCoin)
			addRecipe(true, false, new ItemStack(ModItems.fortuneCoin, 1), NEBULOUS_HEART, Items.gold_nugget, SLIME_PEARL, BAT_WING);
		else
			addRecipe(false, false, new ItemStack(ModItems.fortuneCoin, 1), "ege", "gng", "ege", 'e', Items.ender_eye, 'g', Items.gold_ingot, 'n', NEBULOUS_HEART);

		// cross of mercy
		addRecipe(false, false, new ItemStack(ModItems.mercyCross, 1), "wgr", "glg", "sgz", 'w', WITHER_RIB, 'g', Items.gold_ingot, 'r', RIB_BONE, 'l', Items.leather, 's', new ItemStack(Items.skull, 1, 1), 'z', ZOMBIE_HEART);

		// holy hand grenade
		addRecipe(true, false, new ItemStack(ModItems.holyHandGrenade, 4), ModItems.glowingWater, Items.gold_nugget, Blocks.tnt, CREEPER_GLAND);

		// sojourner's staff
		if(Settings.EasyModeRecipes.sojournerStaff)
			addRecipe(true, false, new ItemStack(ModItems.sojournerStaff, 1), MOLTEN_CORE, Items.gold_ingot, Items.blaze_rod, EMPTY_VOID_TEAR);
		else
			addRecipe(false, false, new ItemStack(ModItems.sojournerStaff, 1), "gcm", "itc", "big", 'g', Items.gold_nugget, 'c', Items.magma_cream, 'm', MOLTEN_CORE, 'i', Items.gold_ingot, 't', ModItems.infernalTear, 'b', Items.blaze_rod);

		// lantern of paranoia
		if(Settings.EasyModeRecipes.lanternOfParanoia)
			addRecipe(false, false, new ItemStack(ModItems.lanternOfParanoia, 1), "isi", "gmg", " i ", 'i', Items.iron_ingot, 's', SLIME_PEARL, 'g', Blocks.glass, 'm', MOLTEN_CORE);
		else
			addRecipe(false, false, new ItemStack(ModItems.lanternOfParanoia, 1), "imi", "gtg", "ili", 'i', Items.iron_ingot, 'm', MOLTEN_CORE, 'g', Blocks.glass, 't', ModBlocks.interdictionTorch, 'l', CREEPER_GLAND);

		// midas touchstone
		addRecipe(true, false, new ItemStack(ModItems.midasTouchstone, 1, 0), Blocks.anvil, Blocks.gold_block, Blocks.gold_block, MOLTEN_CORE, MOLTEN_CORE, MOLTEN_CORE, CREEPER_GLAND, CREEPER_GLAND, EMPTY_VOID_TEAR);

		// emperor's chalice
		if(Settings.EasyModeRecipes.emperorChalice)
			addRecipe(true, false, new ItemStack(ModItems.emperorChalice, 1, 0), Items.emerald, Items.gold_ingot, Items.bucket, EMPTY_VOID_TEAR);
		else
			addRecipe(false, false, new ItemStack(ModItems.emperorChalice, 1, 0), "ses", "ivi", "lbl", 's', SLIME_PEARL, 'e', Items.emerald, 'i', Items.gold_ingot, 'v', EMPTY_VOID_TEAR, 'l', LAPIS, 'b', Items.bucket);

		// infernal chalice
		if(Settings.EasyModeRecipes.infernalChalice)
			addRecipe(true, false, new ItemStack(ModItems.infernalChalice, 1), ModItems.infernalClaws, ModItems.emperorChalice, ModItems.infernalTear, MOLTEN_CORE);
		else
			addRecipe(false, false, new ItemStack(ModItems.infernalChalice, 1), "imi", "wcw", "mtm", 'i', ModItems.infernalClaws, 'm', MOLTEN_CORE, 'w', WITHER_RIB, 'c', ModItems.emperorChalice, 't', ModItems.infernalTear);

		// salamander's eye
		if(Settings.EasyModeRecipes.salamanderEye)
			addRecipe(true, false, new ItemStack(ModItems.salamanderEye, 1), Items.ender_eye, MOLTEN_CORE, FROZEN_CORE, NEBULOUS_HEART);
		else
			addRecipe(false, false, new ItemStack(ModItems.salamanderEye, 1), "fnm", "geg", "mnf", 'f', FROZEN_CORE, 'n', NEBULOUS_HEART, 'm', MOLTEN_CORE, 'g', Items.ghast_tear, 'e', Items.ender_eye);

		// ice rod
		if(Settings.EasyModeRecipes.iceMagusRod)
			addRecipe(false, false, new ItemStack(ModItems.iceMagusRod, 1, 0), " df", " vd", "i  ", 'd', Items.diamond, 'f', FROZEN_CORE, 'i', Items.iron_ingot, 'v', EMPTY_VOID_TEAR);
		else
			addRecipe(false, false, new ItemStack(ModItems.iceMagusRod, 1, 0), "fdf", "ptd", "ipf", 'f', FROZEN_CORE, 'd', Items.diamond, 'p', Blocks.packed_ice, 't', EMPTY_VOID_TEAR, 'i', Items.iron_ingot);

		//glacial staff
		if(Settings.EasyModeRecipes.glacialStaff)
			addRecipe(true, false, new ItemStack(ModItems.glacialStaff, 1, 0), ModItems.iceMagusRod, EMPTY_VOID_TEAR, FROZEN_CORE, ModItems.shearsOfWinter);
		else
			addRecipe(false, false, new ItemStack(ModItems.glacialStaff, 1, 0), "fds", "fvd", "iff", 'f', FROZEN_CORE, 'd', Items.diamond, 's', ModItems.shearsOfWinter, 'v', EMPTY_VOID_TEAR, 'i', ModItems.iceMagusRod);

		// ender staff
		if(Settings.EasyModeRecipes.enderStaff)
			addRecipe(false, false, new ItemStack(ModItems.enderStaff, 1, 0), " be", "nvb", "sn ", 'v', ModItems.emptyVoidTear, 'e', Items.ender_eye, 's', Items.stick, 'n', NEBULOUS_HEART, 'b', BAT_WING);
		else
			addRecipe(false, false, new ItemStack(ModItems.enderStaff, 1, 0), "nbe", "nvb", "rnn", 'n', NEBULOUS_HEART, 'b', BAT_WING, 'e', Items.ender_eye, 'v', EMPTY_VOID_TEAR, 'r', Items.blaze_rod);

		// rending gale
		if(Settings.EasyModeRecipes.rendingGale)
			addRecipe(false, false, new ItemStack(ModItems.rendingGale, 1, 0), " be", "gvb", "sg ", 'b', BAT_WING, 'e', STORM_EYE, 'g', Items.gold_ingot, 'v', EMPTY_VOID_TEAR, 's', Items.stick);
		else
			addRecipe(false, false, new ItemStack(ModItems.rendingGale, 1, 0), "ebe", "fvb", "rfe", 'e', STORM_EYE, 'b', BAT_WING, 'f', ModItems.angelicFeather, 'v', EMPTY_VOID_TEAR, 'r', Items.blaze_rod);

		// harvest rod
		addRecipe(false, false, new ItemStack(ModItems.harvestRod, 1, 0), " rf", "vtr", "sv ", 'r', ROSE_BUSH, 'f', FERTILE_ESSENCE, 'v', Blocks.vine, 't', EMPTY_VOID_TEAR, 's', Items.stick);

		// pyromancer staff
		if(Settings.EasyModeRecipes.pyromancerStaff)
			addRecipe(true, false, new ItemStack(ModItems.pyromancerStaff, 1, 0), ModItems.infernalClaws, Items.blaze_rod, ModItems.infernalTear, ModItems.salamanderEye);
		else
			addRecipe(false, false, new ItemStack(ModItems.pyromancerStaff, 1, 0), "mcs", "mic", "rmm", 'm', MOLTEN_CORE, 'c', ModItems.infernalClaws, 's', ModItems.salamanderEye, 'i', ModItems.infernalTear, 'r', Items.blaze_rod);

		// serpent staff
		if(Settings.EasyModeRecipes.serpentStaff)
			addRecipe(false, false, new ItemStack(ModItems.serpentStaff, 1), " ce", " kc", "s  ", 'c', CHELICERAE, 'e', Items.ender_eye, 'k', SHELL_FRAGMENT, 'b', Items.stick);
		else
			addRecipe(false, false, new ItemStack(ModItems.serpentStaff, 1), "coe", "pko", "bpc", 'c', CHELICERAE, 'o', Blocks.obsidian, 'e', Items.ender_eye, 'p', SLIME_PEARL, 'k', SHELL_FRAGMENT, 'b', Items.blaze_rod);

		// rod of lyssa
		if(Settings.EasyModeRecipes.rodOfLyssa)
			addRecipe(true, false, new ItemStack(ModItems.rodOfLyssa, 1, 0), INFERNAL_CLAW, BAT_WING, NEBULOUS_HEART, Items.fishing_rod);
		else
			addRecipe(false, false, new ItemStack(ModItems.rodOfLyssa, 1, 0), " br", "nms", "r i", 'b', BAT_WING, 'r', Items.blaze_rod, 'n', NEBULOUS_HEART, 'm', MOLTEN_CORE, 's', Items.string, 'i', INFERNAL_CLAW);

		// shears of winter
		addRecipe(true, false, new ItemStack(ModItems.shearsOfWinter, 1, 0), FROZEN_CORE, Items.shears, Items.diamond, Items.diamond);

		// magicbane
		if(Settings.EasyModeRecipes.magicBane)
			addRecipe(false, false, new ItemStack(ModItems.magicbane, 1, 0), "ng", "in", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'n', NEBULOUS_HEART);
		else
			addRecipe(false, false, new ItemStack(ModItems.magicbane, 1, 0), "een", "nge", "ine", 'e', Items.ender_eye, 'n', NEBULOUS_HEART, 'g', Items.gold_ingot, 'i', Items.iron_ingot);

		// witherless rose
		addRecipe(false, false, new ItemStack(ModItems.witherlessRose, 1), "fnf", "nrn", "fnf", 'f', FERTILE_ESSENCE, 'n', Items.nether_star, 'r', ROSE_BUSH);

		// crimson cloth
		addRecipe(true, false, ingredient(1, Reference.CLOTH_INGREDIENT_META), new ItemStack(Blocks.wool, 1, Reference.RED_WOOL_META), new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META), NEBULOUS_HEART, NEBULOUS_HEART);

		// cloak
		addRecipe(false, false, new ItemStack(ModItems.twilightCloak, 1), "ici", "bcb", "bcb", 'i', Items.iron_ingot, 'b', BLACK_WOOL, 'c', ingredient(1, Reference.CLOTH_INGREDIENT_META));

		// void tear
		if(Settings.EasyModeRecipes.voidTear)
			addRecipe(true, false, EMPTY_VOID_TEAR, Items.ghast_tear, NEBULOUS_HEART, SLIME_PEARL, LAPIS);
		else
			addRecipe(false, false, EMPTY_VOID_TEAR, "lel", "pgp", "lnl", 'l', LAPIS, 'e', Items.ender_pearl, 'p', SLIME_PEARL, 'g', Items.ghast_tear, 'n', NEBULOUS_HEART);

		// infernal tear
		if(Settings.EasyModeRecipes.infernalTear)
			addRecipe(true, false, new ItemStack(ModItems.infernalTear, 1, 0), EMPTY_VOID_TEAR, ModItems.witchHat, MOLTEN_CORE, INFERNAL_CLAW);
		else
			addRecipe(false, false, new ItemStack(ModItems.infernalTear, 1, 0), "php", "mtm", "pcp", 'p', Items.blaze_powder, 'h', ModItems.witchHat, 'm', MOLTEN_CORE, 't', EMPTY_VOID_TEAR, 'c', INFERNAL_CLAW);

		// angelic feather
		if(Settings.EasyModeRecipes.angelicFeather)
			addRecipe(true, false, new ItemStack(ModItems.angelicFeather, 1), Items.feather, NEBULOUS_HEART, BAT_WING, FERTILE_ESSENCE);
		else
			addRecipe(false, false, new ItemStack(ModItems.angelicFeather, 1), "dgd", "bfb", "ene", 'd', Items.glowstone_dust, 'g', Items.gold_ingot, 'b', BAT_WING, 'e', FERTILE_ESSENCE, 'n', NEBULOUS_HEART, 'f', Items.feather);

		// phoenix down
		addRecipe(true, false, new ItemStack(ModItems.phoenixDown, 1), ModItems.angelheartVial, ModItems.angelheartVial, ModItems.angelheartVial, ModItems.angelicFeather);

		// infernal claw
		addRecipe(true, false, INFERNAL_CLAW, Items.leather, MOLTEN_CORE, RIB_BONE, SLIME_PEARL);

		// infernal claws
		if(Settings.EasyModeRecipes.infernalClaws)
			addRecipe(true, false, new ItemStack(ModItems.infernalClaws, 1), INFERNAL_CLAW, INFERNAL_CLAW, INFERNAL_CLAW, SLIME_PEARL);
		else
			addRecipe(false, false, new ItemStack(ModItems.infernalClaws, 1), "ccc", "cpc", "mlm", 'c', INFERNAL_CLAW, 'p', SLIME_PEARL, 'm', MOLTEN_CORE, 'l', Items.leather);

		// kraken shell fragment
		addRecipe(true, false, SHELL_FRAGMENT, SQUID_BEAK, SQUID_BEAK, SQUID_BEAK, SLIME_PEARL);

		// kraken shell
		if(Settings.EasyModeRecipes.krakenShell)
			addRecipe(true, false, new ItemStack(ModItems.krakenShell, 1, 0), SHELL_FRAGMENT, SHELL_FRAGMENT, SHELL_FRAGMENT, NEBULOUS_HEART);
		else
			addRecipe(false, false, new ItemStack(ModItems.krakenShell, 1, 0), "nfn", "epe", "fnf", 'n', NEBULOUS_HEART, 'f', SHELL_FRAGMENT, 'e', Items.ender_eye, 'p', SLIME_PEARL);

		// hero medallion - any meta fortune coin for people with "grandfathered" fortune coins (pre-Enabled NBT used item damage)
		if(Settings.EasyModeRecipes.heroMedallion)
			addRecipe(true, false, new ItemStack(ModItems.heroMedallion, 1), NEBULOUS_HEART, new ItemStack(ModItems.fortuneCoin, 1, -1), ModItems.witchHat, ModItems.infernalTear);
		else
			addRecipe(false, false, new ItemStack(ModItems.heroMedallion, 1), "nie", "iti", "fin", 'n', NEBULOUS_HEART, 'i', Items.iron_ingot, 'e', Items.ender_eye, 't', ModItems.infernalTear, 'f', new ItemStack(ModItems.fortuneCoin, 1, -1));

		// destruction catalyst
		if(Settings.EasyModeRecipes.destructionCatalyst)
			addRecipe(true, false, new ItemStack(ModItems.destructionCatalyst, 1, 0), Items.flint_and_steel, MOLTEN_CORE, CREEPER_GLAND, ModItems.infernalTear);
		else
			addRecipe(false, false, new ItemStack(ModItems.destructionCatalyst, 1, 0), "tmc", "gim", "fgt", 't', Blocks.tnt, 'm', MOLTEN_CORE, 'c', CREEPER_GLAND, 'g', Items.gold_ingot, 'i', ModItems.infernalTear, 'f', Items.flint_and_steel);

		// nian zhu heart pearls
		addRecipe(false, false, heartPearl(Reference.ZOMBIE_ZHU_META), "ppp", "sts", "ppp", 'p', ZOMBIE_HEART, 's', Items.rotten_flesh, 't', Items.bone);
		addRecipe(false, false, heartPearl(Reference.SKELETON_ZHU_META), "ppp", "sts", "ppp", 'p', RIB_BONE, 's', Items.bone, 't', Items.flint);
		addRecipe(false, false, heartPearl(Reference.WITHER_SKELETON_ZHU_META), "ppp", "sts", "ppp", 'p', WITHER_RIB, 's', Items.bone, 't', WITHER_SKULL);
		addRecipe(false, false, heartPearl(Reference.CREEPER_ZHU_META), "ppp", "sts", "ppp", 'p', CREEPER_GLAND, 's', Items.gunpowder, 't', Items.bone);

		// nian zhu actual items
		addRecipe(true, false, nianZhu(Reference.ZOMBIE_ZHU_META), Items.string, heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META), heartPearl(Reference.ZOMBIE_ZHU_META));
		addRecipe(true, false, nianZhu(Reference.SKELETON_ZHU_META), Items.string, heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META), heartPearl(Reference.SKELETON_ZHU_META));
		addRecipe(true, false, nianZhu(Reference.WITHER_SKELETON_ZHU_META), Items.string, heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META), heartPearl(Reference.WITHER_SKELETON_ZHU_META));
		addRecipe(true, false, nianZhu(Reference.CREEPER_ZHU_META), Items.string, heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META), heartPearl(Reference.CREEPER_ZHU_META));

		/* potions and splash potions */

		// empty vial
		addRecipe(false, false, new ItemStack(ModItems.potion, 1, 0), "g g", "g g", " g ", 'g', Blocks.glass_pane);

		//non-standard potion list.

		// glowing water
		addRecipe(false, false, new ItemStack(ModItems.glowingWater, 5), "gbg", "gdg", "ngp", 'g', Blocks.glass_pane, 'b', Items.water_bucket, 'd', Items.glowstone_dust, 'p', Items.gunpowder, 'n', Items.nether_wart);

		// angelheart vial
		addRecipe(false, false, new ItemStack(ModItems.angelheartVial, 5), "gbg", "gcg", "fgf", 'g', Blocks.glass_pane, 'b', Items.milk_bucket, 'c', INFERNAL_CLAW, 'f', FERTILE_ESSENCE);

		// attraction
		addRecipe(false, false, new ItemStack(ModItems.attractionPotion, 5), "gbg", "gfg", "rgc", 'g', Blocks.glass_pane, 'b', Items.water_bucket, 'f', FERTILE_ESSENCE, 'r', new ItemStack(Items.dye, 1, Reference.RED_DYE_META), 'c', new ItemStack(Items.dye, 1, Reference.BROWN_DYE_META));

		// fertility
		addRecipe(false, false, new ItemStack(ModItems.fertilePotion, 5), "gbg", "gfg", "cgy", 'g', Blocks.glass_pane, 'b', Items.water_bucket, 'f', FERTILE_ESSENCE, 'c', new ItemStack(Items.dye, 1, Reference.GREEN_DYE_META), 'y', new ItemStack(Items.dye, 1, Reference.YELLOW_DYE_META));

		//pedestal
		addRecipe(false, false, new ItemStack(ModBlocks.pedestal), "dqd", " q ", "dqd", 'd', Items.diamond, 'q', Blocks.quartz_block);

		//passive pedestal
		for(int i = 0; i < 16; i++) {
			addRecipe(false, false, ModBlocks.pedestalPassive.getColoredItemBlockStack(1, EnumDyeColor.byMetadata(i)), " c ", "gqg", "sss", 'c', new ItemStack(Blocks.carpet, 1, i), 'g', Items.gold_nugget, 'q', Blocks.quartz_block, 's', new ItemStack(Blocks.stone_slab, 1, 7));
		}

		addMobDropUncraftingRecipes();

		if(Settings.dropCraftingRecipesEnabled)
			addMobDropCraftingRecipes();
	}

	private static void addMobDropCraftingRecipes() {
		addRecipe(false, false, RIB_BONE, "iii", "ibi", "iii", 'b', Items.bone, 'i', Items.iron_ingot);
		addRecipe(false, false, ingredient(4, Reference.WITHER_INGREDIENT_META), "d d", " s ", "d d", 'd', Items.diamond, 's', new ItemStack(Items.skull, 1, 1));
		addRecipe(false, false, CHELICERAE, "ggg", "gsg", "ggg", 's', Items.string, 'g', Items.gold_ingot);
		addRecipe(false, false, CREEPER_GLAND, "ggg", "gpg", "ggg", 'p', Items.gunpowder, 'g', Items.gold_ingot);
		addRecipe(false, false, SLIME_PEARL, "iii", "isi", "iii", 's', Items.slime_ball, 'i', Items.iron_ingot);
		addRecipe(false, false, BAT_WING, "ggg", "gfg", "ggg", 'f', Items.feather, 'g', Items.gold_ingot);
		addRecipe(false, false, ZOMBIE_HEART, "iii", "ifi", "iii", 'f', Items.rotten_flesh, 'i', Items.iron_ingot);
		addRecipe(false, false, MOLTEN_CORE, "ggg", "gmg", "ggg", 'm', Items.magma_cream, 'g', Items.gold_ingot);
		addRecipe(false, false, STORM_EYE, "ggg", "gcg", "ggg", 'c', CREEPER_GLAND, 'g', Items.gold_ingot);
		addRecipe(false, false, FROZEN_CORE, "gpg", "gsg", "gsg", 'p', Blocks.pumpkin, 's', Blocks.snow, 'g', Items.gold_ingot);
		addRecipe(false, false, NEBULOUS_HEART, "ggg", "geg", "ggg", 'e', Items.ender_pearl, 'g', Items.gold_ingot);
		addRecipe(false, false, SQUID_BEAK, "ggg", "gig", "ggg", 'i', Items.dye, 'g', Items.gold_ingot);
	}

	private static void addMobDropUncraftingRecipes() {

		addRecipe(true, false, new ItemStack(Items.bone, 5), RIB_BONE);
		addRecipe(true, false, new ItemStack(Items.skull, 1, 1), WITHER_RIB, WITHER_RIB, WITHER_RIB, new ItemStack(Items.skull));
		addRecipe(true, false, new ItemStack(Items.ender_pearl, 3), NEBULOUS_HEART);
		addRecipe(true, false, new ItemStack(Items.dye, 6), SQUID_BEAK);
		addRecipe(true, false, new ItemStack(Items.string, 6), CHELICERAE);
		addRecipe(true, false, new ItemStack(Items.gunpowder, 6), CREEPER_GLAND);
		addRecipe(true, false, new ItemStack(Items.slime_ball, 6), SLIME_PEARL);
		addRecipe(true, false, new ItemStack(Items.rotten_flesh, 6), ZOMBIE_HEART);
		addRecipe(true, false, new ItemStack(Items.magma_cream, 3), MOLTEN_CORE);
		addRecipe(true, false, new ItemStack(Items.gunpowder, 10), STORM_EYE);
		addRecipe(true, false, new ItemStack(Items.snowball, 5), FROZEN_CORE);
	}

	public static final ItemStack EMPTY_VOID_TEAR = new ItemStack(ModItems.emptyVoidTear, 1, 0);
	public static final ItemStack WITHER_SKULL = new ItemStack(Items.skull, 1, 1);
	public static final ItemStack ROSE_BUSH = new ItemStack(Blocks.double_plant, 1, 4);
	public static final ItemStack BLACK_WOOL = new ItemStack(Blocks.wool, 1, Reference.BLACK_WOOL_META);
	public static final ItemStack LAPIS = new ItemStack(Items.dye, 1, 4);

	public static ItemStack gunPart(int amount, int meta) {
		return new ItemStack(ModItems.gunPart, amount, meta);
	}

	public static ItemStack magazine(int amount, int meta) {
		return new ItemStack(ModItems.magazine, amount, meta);
	}

	public static ItemStack bullet(int amount, int meta) {
		return new ItemStack(ModItems.bullet, amount, meta);
	}

	public static ItemStack ingredient(int amount, int m) {
		return new ItemStack(ModItems.mobIngredient, amount, m);
	}

	public static final ItemStack NEBULOUS_HEART = ingredient(1, Reference.ENDER_INGREDIENT_META);
	public static final ItemStack CREEPER_GLAND = ingredient(1, Reference.CREEPER_INGREDIENT_META);
	public static final ItemStack SLIME_PEARL = ingredient(1, Reference.SLIME_INGREDIENT_META);
	public static final ItemStack BAT_WING = ingredient(1, Reference.BAT_INGREDIENT_META);
	public static final ItemStack RIB_BONE = ingredient(1, Reference.SKELETON_INGREDIENT_META);
	public static final ItemStack WITHER_RIB = ingredient(1, Reference.WITHER_INGREDIENT_META);
	public static final ItemStack STORM_EYE = ingredient(1, Reference.STORM_INGREDIENT_META);
	public static final ItemStack FERTILE_ESSENCE = ingredient(1, Reference.FERTILE_INGREDIENT_META);
	public static final ItemStack FROZEN_CORE = ingredient(1, Reference.FROZEN_INGREDIENT_META);
	public static final ItemStack MOLTEN_CORE = ingredient(1, Reference.MOLTEN_INGREDIENT_META);
	public static final ItemStack ZOMBIE_HEART = ingredient(1, Reference.ZOMBIE_INGREDIENT_META);
	public static final ItemStack INFERNAL_CLAW = ingredient(1, Reference.CLAW_INGREDIENT_META);
	public static final ItemStack SHELL_FRAGMENT = ingredient(1, Reference.SHELL_INGREDIENT_META);
	public static final ItemStack SQUID_BEAK = ingredient(1, Reference.SQUID_INGREDIENT_META);
	public static final ItemStack CHELICERAE = ingredient(1, Reference.SPIDER_INGREDIENT_META);

	public static ItemStack heartPearl(int meta) {
		return new ItemStack(ModItems.heartPearl, 1, meta);
	}

	public static ItemStack nianZhu(int meta) {
		return new ItemStack(ModItems.heartZhu, 1, meta);
	}

}
