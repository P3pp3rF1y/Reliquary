package xreliquary.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import xreliquary.crafting.*;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.Arrays;

public class XRRecipes {

	//this version of the addRecipe method checks first to see if the recipe is disabled in our automated recipe-disabler config
	//if any component of the item is in the recipe disabler list, it will ALSO block the recipe automatically.
	//override disabler forces the recipe to evaluate anyway. This occurs for items that don't fall into XR scope, and thus shouldn't be evaluated.
	public static void addRecipe(boolean isShapeless, boolean overrideDisabler, ItemStack result, Object... params) {
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
		addTomeRecipeHandlers();

		// mob charm data fix and repair recipes
		addMobCharmRecipeHandlers();

		// lingering potion related recipes - arrows / shots
		addLingeringPotionRecipes();

		//misc recipes
		//frozen cores to make packed ice.
		addRecipe(true, true, new ItemStack(Blocks.PACKED_ICE, 1, 0), Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, FROZEN_CORE);

		//apothecary mortar recipe
		addRecipe(false, false, new ItemStack(ModBlocks.apothecaryMortar, 1, 0), "gng", "ngn", "nnn", 'n', Blocks.QUARTZ_BLOCK, 'g', CREEPER_GLAND);

		//apothecary cauldron recipe
		addRecipe(false, false, new ItemStack(ModBlocks.apothecaryCauldron, 1, 0), "gng", "ici", "nmn", 'g', CREEPER_GLAND, 'n', NEBULOUS_HEART, 'i', INFERNAL_CLAW, 'c', Items.CAULDRON, 'm', MOLTEN_CORE);

		//alkahestry tome
		if(Settings.EasyModeRecipes.alkahestryTome)
			addRecipe(true, false, new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage()), Items.BOOK, ModItems.witchHat, MOLTEN_CORE, WITHER_SKULL);
		else
			addRecipe(true, false, new ItemStack(ModItems.alkahestryTome, 1, ModItems.alkahestryTome.getMaxDamage()), MOLTEN_CORE, ModItems.witchHat, STORM_EYE, CREEPER_GLAND, Items.BOOK, SLIME_PEARL, CHELICERAE, WITHER_SKULL, NEBULOUS_HEART);

		//glowstone altar
		if(Settings.EasyModeRecipes.altar)
			addRecipe(true, false, new ItemStack(ModBlocks.alkahestryAltar, 1), Blocks.OBSIDIAN, Blocks.REDSTONE_LAMP, NEBULOUS_HEART, CREEPER_GLAND);
		else
			addRecipe(false, false, new ItemStack(ModBlocks.alkahestryAltar, 1), "dnd", "olo", "dgd", 'd', Items.GLOWSTONE_DUST, 'n', NEBULOUS_HEART, 'o', Blocks.OBSIDIAN, 'l', Blocks.REDSTONE_LAMP, 'g', CREEPER_GLAND);

		//fertile_lilypad
		addRecipe(true, false, new ItemStack(ModBlocks.fertileLilypad, 1), FERTILE_ESSENCE, FERTILE_ESSENCE, FERTILE_ESSENCE, Blocks.WATERLILY);

		//wraith node
		addRecipe(true, false, new ItemStack(ModBlocks.wraithNode, 1), NEBULOUS_HEART, Items.EMERALD);

		//interdiction torch
		if(Settings.EasyModeRecipes.interdictionTorch)
			addRecipe(false, false, new ItemStack(ModBlocks.interdictionTorch, 4, 0), "bm", "nr", 'b', BAT_WING, 'm', MOLTEN_CORE, 'n', NEBULOUS_HEART, 'r', Items.BLAZE_ROD);
		else
			addRecipe(false, false, new ItemStack(ModBlocks.interdictionTorch, 4, 0), " n ", "mdm", "bwb", 'n', NEBULOUS_HEART, 'm', MOLTEN_CORE, 'd', Items.DIAMOND, 'b', Items.BLAZE_ROD, 'w', BAT_WING);

		// glowy bread
		addRecipe(true, false, new ItemStack(ModItems.glowingBread, 3), Items.BREAD, Items.BREAD, Items.BREAD, ModItems.glowingWater);

		//fertile essence
		if(Settings.EasyModeRecipes.fertileEssence)
			addRecipe(true, false, FERTILE_ESSENCE, RIB_BONE, CREEPER_GLAND, new ItemStack(Items.DYE, 1, Reference.GREEN_DYE_META), SLIME_PEARL);
		else
			addRecipe(false, false, FERTILE_ESSENCE, "gbg", "scs", "gbg", 'g', CREEPER_GLAND, 'b', RIB_BONE, 's', SLIME_PEARL, 'c', new ItemStack(Items.DYE, 1, Reference.GREEN_DYE_META));

		// bullets...
		// empty cases back into nuggets
		addRecipe(true, true, new ItemStack(Items.GOLD_NUGGET, 1), bullet(1, 0), bullet(1, 0), bullet(1, 0), bullet(1, 0));
		// neutral
		addRecipe(true, false, bullet(8, 1), Items.FLINT, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GUNPOWDER);
		// exorcist
		addRecipe(true, false, bullet(8, 2), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), bullet(1, 1), ZOMBIE_HEART);
		// blaze
		addRecipe(true, false, bullet(8, 3), Items.BLAZE_POWDER, Items.BLAZE_ROD, Items.GOLD_NUGGET, Items.GOLD_NUGGET);
		// ender
		addRecipe(true, false, bullet(8, 4), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), bullet(1, 7), NEBULOUS_HEART);
		// concussive
		addRecipe(true, false, bullet(8, 5), Items.SLIME_BALL, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GUNPOWDER);
		// buster
		addRecipe(true, false, bullet(8, 6), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), bullet(1, 5), CREEPER_GLAND);
		// seeker, the only thing with an easy mode recipe
		if(Settings.EasyModeRecipes.seekerShot)
			addRecipe(true, false, bullet(8, 7), LAPIS, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GUNPOWDER);
		else
			addRecipe(true, false, bullet(4, 7), LAPIS, SLIME_PEARL, Items.GOLD_NUGGET, Items.GUNPOWDER);
		// sand
		addRecipe(true, false, bullet(8, 8), Blocks.SANDSTONE, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GUNPOWDER);
		// storm
		addRecipe(true, false, bullet(8, 9), CREEPER_GLAND, CREEPER_GLAND, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GUNPOWDER);
		// frozen shot TODO
		// venom shot TODO
		// fertile shot TODO
		// rage shot TODO
		// traitor shot TODO
		// calm shot TODO
		// molten shot TODO

		// magazines...
		addRecipe(false, false, magazine(5, 0), "i i", "igi", "sis", 's', Blocks.STONE, 'i', Items.IRON_INGOT, 'g', Blocks.GLASS);

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
		addRecipe(false, false, gunPart(1, 0), "iii", "imi", "ici", 'i', Items.IRON_INGOT, 'c', magazine(1, 0), 'm', Items.MAGMA_CREAM);
		addRecipe(false, false, gunPart(1, 1), "iii", "eme", "iii", 'i', Items.IRON_INGOT, 'e', NEBULOUS_HEART, 'm', Items.MAGMA_CREAM);
		addRecipe(false, false, gunPart(1, 2), "iib", "rmi", "iii", 'i', Items.IRON_INGOT, 'b', Blocks.STONE_BUTTON, 'r', Items.BLAZE_ROD, 'm', MOLTEN_CORE);

		// handgun
		addRecipe(false, false, new ItemStack(ModItems.handgun, 1, 0), "bim", "isi", "igi", 'i', Items.IRON_INGOT, 'b', gunPart(1, 1), 'm', gunPart(1, 2), 'g', gunPart(1, 0), 's', SLIME_PEARL);

		// fortune coin
		if(Settings.EasyModeRecipes.fortuneCoin)
			addRecipe(true, false, new ItemStack(ModItems.fortuneCoin, 1), NEBULOUS_HEART, Items.GOLD_NUGGET, SLIME_PEARL, BAT_WING);
		else
			addRecipe(false, false, new ItemStack(ModItems.fortuneCoin, 1), "ege", "gng", "ege", 'e', Items.ENDER_EYE, 'g', Items.GOLD_INGOT, 'n', NEBULOUS_HEART);

		// cross of mercy
		addRecipe(false, false, new ItemStack(ModItems.mercyCross, 1), "wgr", "glg", "sgz", 'w', WITHER_RIB, 'g', Items.GOLD_INGOT, 'r', RIB_BONE, 'l', Items.LEATHER, 's', new ItemStack(Items.SKULL, 1, 1), 'z', ZOMBIE_HEART);

		// holy hand grenade
		addRecipe(true, false, new ItemStack(ModItems.holyHandGrenade, 4), ModItems.glowingWater, Items.GOLD_NUGGET, Blocks.TNT, CREEPER_GLAND);

		// sojourner's staff
		if(Settings.EasyModeRecipes.sojournerStaff)
			addRecipe(true, false, new ItemStack(ModItems.sojournerStaff, 1), MOLTEN_CORE, Items.GOLD_INGOT, Items.BLAZE_ROD, EMPTY_VOID_TEAR);
		else
			addRecipe(false, false, new ItemStack(ModItems.sojournerStaff, 1), "gcm", "itc", "big", 'g', Items.GOLD_NUGGET, 'c', Items.MAGMA_CREAM, 'm', MOLTEN_CORE, 'i', Items.GOLD_INGOT, 't', ModItems.infernalTear, 'b', Items.BLAZE_ROD);

		// lantern of paranoia
		if(Settings.EasyModeRecipes.lanternOfParanoia)
			addRecipe(false, false, new ItemStack(ModItems.lanternOfParanoia, 1), "isi", "gmg", " i ", 'i', Items.IRON_INGOT, 's', SLIME_PEARL, 'g', Blocks.GLASS, 'm', MOLTEN_CORE);
		else
			addRecipe(false, false, new ItemStack(ModItems.lanternOfParanoia, 1), "imi", "gtg", "ili", 'i', Items.IRON_INGOT, 'm', MOLTEN_CORE, 'g', Blocks.GLASS, 't', ModBlocks.interdictionTorch, 'l', CREEPER_GLAND);

		// midas touchstone
		addRecipe(true, false, new ItemStack(ModItems.midasTouchstone, 1, 0), Blocks.ANVIL, Blocks.GOLD_BLOCK, Blocks.GOLD_BLOCK, MOLTEN_CORE, MOLTEN_CORE, MOLTEN_CORE, CREEPER_GLAND, CREEPER_GLAND, EMPTY_VOID_TEAR);

		// emperor's chalice
		if(Settings.EasyModeRecipes.emperorChalice)
			addRecipe(true, false, new ItemStack(ModItems.emperorChalice, 1, 0), Items.EMERALD, Items.GOLD_INGOT, Items.BUCKET, EMPTY_VOID_TEAR);
		else
			addRecipe(false, false, new ItemStack(ModItems.emperorChalice, 1, 0), "ses", "ivi", "lbl", 's', SLIME_PEARL, 'e', Items.EMERALD, 'i', Items.GOLD_INGOT, 'v', EMPTY_VOID_TEAR, 'l', LAPIS, 'b', Items.BUCKET);

		// infernal chalice
		if(Settings.EasyModeRecipes.infernalChalice)
			addRecipe(true, false, new ItemStack(ModItems.infernalChalice, 1), ModItems.infernalClaws, ModItems.emperorChalice, ModItems.infernalTear, MOLTEN_CORE);
		else
			addRecipe(false, false, new ItemStack(ModItems.infernalChalice, 1), "imi", "wcw", "mtm", 'i', ModItems.infernalClaws, 'm', MOLTEN_CORE, 'w', WITHER_RIB, 'c', ModItems.emperorChalice, 't', ModItems.infernalTear);

		// salamander's eye
		if(Settings.EasyModeRecipes.salamanderEye)
			addRecipe(true, false, new ItemStack(ModItems.salamanderEye, 1), Items.ENDER_EYE, MOLTEN_CORE, FROZEN_CORE, NEBULOUS_HEART);
		else
			addRecipe(false, false, new ItemStack(ModItems.salamanderEye, 1), "fnm", "geg", "mnf", 'f', FROZEN_CORE, 'n', NEBULOUS_HEART, 'm', MOLTEN_CORE, 'g', Items.GHAST_TEAR, 'e', Items.ENDER_EYE);

		// ice rod
		if(Settings.EasyModeRecipes.iceMagusRod)
			addRecipe(false, false, new ItemStack(ModItems.iceMagusRod, 1, 0), " df", " vd", "i  ", 'd', Items.DIAMOND, 'f', FROZEN_CORE, 'i', Items.IRON_INGOT, 'v', EMPTY_VOID_TEAR);
		else
			addRecipe(false, false, new ItemStack(ModItems.iceMagusRod, 1, 0), "fdf", "ptd", "ipf", 'f', FROZEN_CORE, 'd', Items.DIAMOND, 'p', Blocks.PACKED_ICE, 't', EMPTY_VOID_TEAR, 'i', Items.IRON_INGOT);

		//glacial staff
		if(Settings.EasyModeRecipes.glacialStaff)
			addRecipe(true, false, new ItemStack(ModItems.glacialStaff, 1, 0), ModItems.iceMagusRod, EMPTY_VOID_TEAR, FROZEN_CORE, ModItems.shearsOfWinter);
		else
			addRecipe(false, false, new ItemStack(ModItems.glacialStaff, 1, 0), "fds", "fvd", "iff", 'f', FROZEN_CORE, 'd', Items.DIAMOND, 's', ModItems.shearsOfWinter, 'v', EMPTY_VOID_TEAR, 'i', ModItems.iceMagusRod);

		// ender staff
		if(Settings.EasyModeRecipes.enderStaff)
			addRecipe(false, false, new ItemStack(ModItems.enderStaff, 1, 0), " be", "nvb", "sn ", 'v', ModItems.emptyVoidTear, 'e', Items.ENDER_EYE, 's', Items.STICK, 'n', NEBULOUS_HEART, 'b', BAT_WING);
		else
			addRecipe(false, false, new ItemStack(ModItems.enderStaff, 1, 0), "nbe", "nvb", "rnn", 'n', NEBULOUS_HEART, 'b', BAT_WING, 'e', Items.ENDER_EYE, 'v', EMPTY_VOID_TEAR, 'r', Items.BLAZE_ROD);

		// rending gale
		if(Settings.EasyModeRecipes.rendingGale)
			addRecipe(false, false, new ItemStack(ModItems.rendingGale, 1, 0), " be", "gvb", "sg ", 'b', BAT_WING, 'e', STORM_EYE, 'g', Items.GOLD_INGOT, 'v', EMPTY_VOID_TEAR, 's', Items.STICK);
		else
			addRecipe(false, false, new ItemStack(ModItems.rendingGale, 1, 0), "ebe", "fvb", "rfe", 'e', STORM_EYE, 'b', BAT_WING, 'f', ModItems.angelicFeather, 'v', EMPTY_VOID_TEAR, 'r', Items.BLAZE_ROD);

		// harvest rod
		addRecipe(false, false, new ItemStack(ModItems.harvestRod, 1, 0), " rf", "vtr", "sv ", 'r', ROSE_BUSH, 'f', FERTILE_ESSENCE, 'v', Blocks.VINE, 't', EMPTY_VOID_TEAR, 's', Items.STICK);

		// pyromancer staff
		if(Settings.EasyModeRecipes.pyromancerStaff)
			addRecipe(true, false, new ItemStack(ModItems.pyromancerStaff, 1, 0), ModItems.infernalClaws, Items.BLAZE_ROD, ModItems.infernalTear, ModItems.salamanderEye);
		else
			addRecipe(false, false, new ItemStack(ModItems.pyromancerStaff, 1, 0), "mcs", "mic", "rmm", 'm', MOLTEN_CORE, 'c', ModItems.infernalClaws, 's', ModItems.salamanderEye, 'i', ModItems.infernalTear, 'r', Items.BLAZE_ROD);

		// serpent staff
		if(Settings.EasyModeRecipes.serpentStaff)
			addRecipe(false, false, new ItemStack(ModItems.serpentStaff, 1), " ce", " kc", "s  ", 'c', CHELICERAE, 'e', Items.ENDER_EYE, 'k', SHELL_FRAGMENT, 'b', Items.STICK);
		else
			addRecipe(false, false, new ItemStack(ModItems.serpentStaff, 1), "coe", "pko", "bpc", 'c', CHELICERAE, 'o', Blocks.OBSIDIAN, 'e', Items.ENDER_EYE, 'p', SLIME_PEARL, 'k', SHELL_FRAGMENT, 'b', Items.BLAZE_ROD);

		// rod of lyssa
		if(Settings.EasyModeRecipes.rodOfLyssa)
			addRecipe(true, false, new ItemStack(ModItems.rodOfLyssa, 1, 0), INFERNAL_CLAW, BAT_WING, NEBULOUS_HEART, Items.FISHING_ROD);
		else
			addRecipe(false, false, new ItemStack(ModItems.rodOfLyssa, 1, 0), " br", "nms", "r i", 'b', BAT_WING, 'r', Items.BLAZE_ROD, 'n', NEBULOUS_HEART, 'm', MOLTEN_CORE, 's', Items.STRING, 'i', INFERNAL_CLAW);

		// shears of winter
		addRecipe(true, false, new ItemStack(ModItems.shearsOfWinter, 1, 0), FROZEN_CORE, Items.SHEARS, Items.DIAMOND, Items.DIAMOND);

		// magicbane
		if(Settings.EasyModeRecipes.magicBane)
			addRecipe(false, false, new ItemStack(ModItems.magicbane, 1, 0), "ng", "in", 'g', Items.GOLD_INGOT, 'i', Items.IRON_INGOT, 'n', NEBULOUS_HEART);
		else
			addRecipe(false, false, new ItemStack(ModItems.magicbane, 1, 0), "een", "nge", "ine", 'e', Items.ENDER_EYE, 'n', NEBULOUS_HEART, 'g', Items.GOLD_INGOT, 'i', Items.IRON_INGOT);

		// witherless rose
		addRecipe(false, false, new ItemStack(ModItems.witherlessRose, 1), "fnf", "nrn", "fnf", 'f', FERTILE_ESSENCE, 'n', Items.NETHER_STAR, 'r', ROSE_BUSH);

		// crimson cloth
		addRecipe(true, false, ingredient(1, Reference.CLOTH_INGREDIENT_META), new ItemStack(Blocks.WOOL, 1, Reference.RED_WOOL_META), new ItemStack(Blocks.WOOL, 1, Reference.BLACK_WOOL_META), NEBULOUS_HEART, NEBULOUS_HEART);

		// cloak
		addRecipe(false, false, new ItemStack(ModItems.twilightCloak, 1), "ici", "bcb", "bcb", 'i', Items.IRON_INGOT, 'b', BLACK_WOOL, 'c', ingredient(1, Reference.CLOTH_INGREDIENT_META));

		// void tear
		if(Settings.EasyModeRecipes.voidTear)
			addRecipe(true, false, EMPTY_VOID_TEAR, Items.GHAST_TEAR, NEBULOUS_HEART, SLIME_PEARL, LAPIS);
		else
			addRecipe(false, false, EMPTY_VOID_TEAR, "lel", "pgp", "lnl", 'l', LAPIS, 'e', Items.ENDER_PEARL, 'p', SLIME_PEARL, 'g', Items.GHAST_TEAR, 'n', NEBULOUS_HEART);

		// infernal tear
		if(Settings.EasyModeRecipes.infernalTear)
			addRecipe(true, false, new ItemStack(ModItems.infernalTear, 1, 0), EMPTY_VOID_TEAR, ModItems.witchHat, MOLTEN_CORE, INFERNAL_CLAW);
		else
			addRecipe(false, false, new ItemStack(ModItems.infernalTear, 1, 0), "php", "mtm", "pcp", 'p', Items.BLAZE_POWDER, 'h', ModItems.witchHat, 'm', MOLTEN_CORE, 't', EMPTY_VOID_TEAR, 'c', INFERNAL_CLAW);

		// angelic feather
		if(Settings.EasyModeRecipes.angelicFeather)
			addRecipe(true, false, new ItemStack(ModItems.angelicFeather, 1), Items.FEATHER, NEBULOUS_HEART, BAT_WING, FERTILE_ESSENCE);
		else
			addRecipe(false, false, new ItemStack(ModItems.angelicFeather, 1), "dgd", "bfb", "ene", 'd', Items.GLOWSTONE_DUST, 'g', Items.GOLD_INGOT, 'b', BAT_WING, 'e', FERTILE_ESSENCE, 'n', NEBULOUS_HEART, 'f', Items.FEATHER);

		// phoenix down
		addRecipe(true, false, new ItemStack(ModItems.phoenixDown, 1), ModItems.angelheartVial, ModItems.angelheartVial, ModItems.angelheartVial, ModItems.angelicFeather);

		// infernal claw
		addRecipe(true, false, INFERNAL_CLAW, Items.LEATHER, MOLTEN_CORE, RIB_BONE, SLIME_PEARL);

		// infernal claws
		if(Settings.EasyModeRecipes.infernalClaws)
			addRecipe(true, false, new ItemStack(ModItems.infernalClaws, 1), INFERNAL_CLAW, INFERNAL_CLAW, INFERNAL_CLAW, SLIME_PEARL);
		else
			addRecipe(false, false, new ItemStack(ModItems.infernalClaws, 1), "ccc", "cpc", "mlm", 'c', INFERNAL_CLAW, 'p', SLIME_PEARL, 'm', MOLTEN_CORE, 'l', Items.LEATHER);

		// kraken shell fragment
		addRecipe(true, false, SHELL_FRAGMENT, SQUID_BEAK, SQUID_BEAK, SQUID_BEAK, SLIME_PEARL);

		// kraken shell
		if(Settings.EasyModeRecipes.krakenShell)
			addRecipe(true, false, new ItemStack(ModItems.krakenShell, 1, 0), SHELL_FRAGMENT, SHELL_FRAGMENT, SHELL_FRAGMENT, NEBULOUS_HEART);
		else
			addRecipe(false, false, new ItemStack(ModItems.krakenShell, 1, 0), "nfn", "epe", "fnf", 'n', NEBULOUS_HEART, 'f', SHELL_FRAGMENT, 'e', Items.ENDER_EYE, 'p', SLIME_PEARL);

		// hero medallion - any meta fortune coin for people with "grandfathered" fortune coins (pre-Enabled NBT used item damage)
		if(Settings.EasyModeRecipes.heroMedallion)
			addRecipe(true, false, new ItemStack(ModItems.heroMedallion, 1), NEBULOUS_HEART, new ItemStack(ModItems.fortuneCoin, 1, -1), ModItems.witchHat, ModItems.infernalTear);
		else
			addRecipe(false, false, new ItemStack(ModItems.heroMedallion, 1), "nie", "iti", "fin", 'n', NEBULOUS_HEART, 'i', Items.IRON_INGOT, 'e', Items.ENDER_EYE, 't', ModItems.infernalTear, 'f', new ItemStack(ModItems.fortuneCoin, 1, -1));

		// destruction catalyst
		if(Settings.EasyModeRecipes.destructionCatalyst)
			addRecipe(true, false, new ItemStack(ModItems.destructionCatalyst, 1, 0), Items.FLINT_AND_STEEL, MOLTEN_CORE, CREEPER_GLAND, ModItems.infernalTear);
		else
			addRecipe(false, false, new ItemStack(ModItems.destructionCatalyst, 1, 0), "tmc", "gim", "fgt", 't', Blocks.TNT, 'm', MOLTEN_CORE, 'c', CREEPER_GLAND, 'g', Items.GOLD_INGOT, 'i', ModItems.infernalTear, 'f', Items.FLINT_AND_STEEL);

		// mob charm fragments
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.ZOMBIE_META), "ppp", "sts", "ppp", 'p', ZOMBIE_HEART, 's', Items.ROTTEN_FLESH, 't', Items.BONE);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.SKELETON_META), "ppp", "sts", "ppp", 'p', RIB_BONE, 's', Items.BONE, 't', Items.FLINT);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.WITHER_SKELETON_META), "ppp", "sts", "ppp", 'p', WITHER_RIB, 's', Items.BONE, 't', WITHER_SKULL);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.CREEPER_META), "ppp", "sts", "ppp", 'p', CREEPER_GLAND, 's', Items.GUNPOWDER, 't', Items.BONE);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.WITCH_META), "ppp", "sts", "ppp", 'p', ModItems.witchHat, 's', Items.GLASS_BOTTLE, 't', Items.SPIDER_EYE);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.ZOMBIE_PIGMAN_META), "ppp", "sts", "ppp", 'p', ZOMBIE_HEART, 's', Items.ROTTEN_FLESH, 't', Items.GOLDEN_SWORD);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.CAVE_SPIDER_META), "ppp", "sts", "ppp", 'p', CHELICERAE, 's', Items.STRING, 't', PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.POISON));
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.SPIDER_META), "ppp", "sts", "ppp", 'p', CHELICERAE, 's', Items.STRING, 't', Items.SPIDER_EYE);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.ENDERMAN_META), "ppp", "sts", "ppp", 'p', NEBULOUS_HEART, 's', Items.ENDER_PEARL, 't', NEBULOUS_HEART);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.BLAZE_META), "ppp", "sts", "ppp", 'p', MOLTEN_CORE, 's', Items.BLAZE_ROD, 't', Items.BLAZE_POWDER);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.GHAST_META), "ppp", "sts", "ppp", 'p', Items.GHAST_TEAR, 's', Items.GUNPOWDER, 't', CREEPER_GLAND);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.MAGMA_CUBE_META), "ppp", "sss", "ppp", 'p', MOLTEN_CORE, 's', Items.MAGMA_CREAM);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.SLIME_META), "ppp", "sss", "ppp", 'p', SLIME_PEARL, 's', Items.SLIME_BALL);
		addRecipe(false, false, charmFragment(Reference.MOB_CHARM.GUARDIAN_META), "ppp", "sts", "ppp", 'p', GUARDIAN_SPIKE, 's', Items.PRISMARINE_SHARD, 't', Items.FISH);

		// mob charm actual items
		addMobCharmRecipe(Reference.MOB_CHARM.ZOMBIE_META);
		addMobCharmRecipe(Reference.MOB_CHARM.SKELETON_META);
		addMobCharmRecipe(Reference.MOB_CHARM.WITHER_SKELETON_META);
		addMobCharmRecipe(Reference.MOB_CHARM.CREEPER_META);
		addMobCharmRecipe(Reference.MOB_CHARM.WITCH_META);
		addMobCharmRecipe(Reference.MOB_CHARM.ZOMBIE_PIGMAN_META);
		addMobCharmRecipe(Reference.MOB_CHARM.CAVE_SPIDER_META);
		addMobCharmRecipe(Reference.MOB_CHARM.SPIDER_META);
		addMobCharmRecipe(Reference.MOB_CHARM.ENDERMAN_META);
		addMobCharmRecipe(Reference.MOB_CHARM.BLAZE_META);
		addMobCharmRecipe(Reference.MOB_CHARM.GHAST_META);
		addMobCharmRecipe(Reference.MOB_CHARM.MAGMA_CUBE_META);
		addMobCharmRecipe(Reference.MOB_CHARM.SLIME_META);
		addMobCharmRecipe(Reference.MOB_CHARM.GUARDIAN_META);

		// mob charm belt
		addRecipe(false, false, new ItemStack(ModItems.mobCharmBelt), "lll", "f f", "fff", 'l', Items.LEATHER, 'f', new ItemStack(ModItems.mobCharmFragment, 1, OreDictionary.WILDCARD_VALUE));

		/* potions and splash potions */

		// empty vial
		addRecipe(false, false, new ItemStack(ModItems.potion, 1, 0), "g g", "g g", " g ", 'g', Blocks.GLASS_PANE);

		//non-standard potion list.

		// glowing water
		addRecipe(false, false, new ItemStack(ModItems.glowingWater, 5), "gbg", "gdg", "ngp", 'g', Blocks.GLASS_PANE, 'b', Items.WATER_BUCKET, 'd', Items.GLOWSTONE_DUST, 'p', Items.GUNPOWDER, 'n', Items.NETHER_WART);

		// angelheart vial
		addRecipe(false, false, new ItemStack(ModItems.angelheartVial, 5), "gbg", "gcg", "fgf", 'g', Blocks.GLASS_PANE, 'b', Items.MILK_BUCKET, 'c', INFERNAL_CLAW, 'f', FERTILE_ESSENCE);

		// attraction
		addRecipe(false, false, new ItemStack(ModItems.attractionPotion, 5), "gbg", "gfg", "rgc", 'g', Blocks.GLASS_PANE, 'b', Items.WATER_BUCKET, 'f', FERTILE_ESSENCE, 'r', new ItemStack(Items.DYE, 1, Reference.RED_DYE_META), 'c', new ItemStack(Items.DYE, 1, Reference.BROWN_DYE_META));

		// fertility
		addRecipe(false, false, new ItemStack(ModItems.fertilePotion, 5), "gbg", "gfg", "cgy", 'g', Blocks.GLASS_PANE, 'b', Items.WATER_BUCKET, 'f', FERTILE_ESSENCE, 'c', new ItemStack(Items.DYE, 1, Reference.GREEN_DYE_META), 'y', new ItemStack(Items.DYE, 1, Reference.YELLOW_DYE_META));

		//pedestal
		for(int i = 0; i < 16; i++) {
			addRecipe(false, false, new ItemStack(ModBlocks.pedestal, 1, i), "d d", " p ", "d d", 'd', Items.DIAMOND, 'p', new ItemStack(ModBlocks.pedestalPassive, 1, i));
		}
		//passive pedestal
		for(int i = 0; i < 16; i++) {
			addRecipe(false, false, new ItemStack(ModBlocks.pedestalPassive, 1, i), " c ", "gqg", "sss", 'c', new ItemStack(Blocks.CARPET, 1, i), 'g', Items.GOLD_NUGGET, 'q', Blocks.QUARTZ_BLOCK, 's', new ItemStack(Blocks.STONE_SLAB, 1, 7));
		}

		addMobDropUncraftingRecipes();

		if(Settings.dropCraftingRecipesEnabled)
			addMobDropCraftingRecipes();
	}

	private static void addLingeringPotionRecipes() {
		GameRegistry.addRecipe(new XRPotionArrowsRecipe());

		RecipeSorter.register(Reference.MOD_ID + ":tipped_arrows", MobCharmRepairRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

	}

	private static void addTomeRecipeHandlers() {
		if(ModItems.alkahestryTome.getRegistryName() == null || Settings.disabledItemsBlocks.contains(ModItems.alkahestryTome.getRegistryName().getResourcePath()))
			return;

		GameRegistry.addRecipe(new AlkahestryDrainRecipe());
		GameRegistry.addRecipe(new AlkahestryChargingRecipe());
		GameRegistry.addRecipe(new AlkahestryCraftingRecipe());

		RecipeSorter.register(Reference.MOD_ID + ":alkahest_crafting", AlkahestryCraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
		RecipeSorter.register(Reference.MOD_ID + ":alkahest_charge", AlkahestryChargingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_crafting");
		RecipeSorter.register(Reference.MOD_ID + ":alkahest_drain", AlkahestryDrainRecipe.class, RecipeSorter.Category.SHAPELESS, "before:" + Reference.MOD_ID + ":alkahest_charge");
	}

	private static void addMobCharmRecipeHandlers() {
		GameRegistry.addRecipe(new MobCharmDataFixRecipe());
		GameRegistry.addRecipe(new MobCharmRepairRecipe());

		RecipeSorter.register(Reference.MOD_ID + ":mob_charm_data_fix", MobCharmDataFixRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shaped");
		RecipeSorter.register(Reference.MOD_ID + ":mob_charm_repair", MobCharmRepairRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	private static void addMobCharmRecipe(byte type) {
		ItemStack mobCharm = mobCharm(type);
		ItemStack fragment = charmFragment(type);

		addRecipe(false, false, mobCharm, "flf", "fsf", "f f", 'f', fragment, 'l', Items.LEATHER, 's', Items.STRING);
	}

	private static void addMobDropCraftingRecipes() {
		addRecipe(false, false, RIB_BONE, "iii", "ibi", "iii", 'b', Items.BONE, 'i', Items.IRON_INGOT);
		addRecipe(false, false, ingredient(4, Reference.WITHER_INGREDIENT_META), "d d", " s ", "d d", 'd', Items.DIAMOND, 's', new ItemStack(Items.SKULL, 1, 1));
		addRecipe(false, false, CHELICERAE, "ggg", "gsg", "ggg", 's', Items.STRING, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, CREEPER_GLAND, "ggg", "gpg", "ggg", 'p', Items.GUNPOWDER, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, SLIME_PEARL, "iii", "isi", "iii", 's', Items.SLIME_BALL, 'i', Items.IRON_INGOT);
		addRecipe(false, false, BAT_WING, "ggg", "gfg", "ggg", 'f', Items.FEATHER, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, ZOMBIE_HEART, "iii", "ifi", "iii", 'f', Items.ROTTEN_FLESH, 'i', Items.IRON_INGOT);
		addRecipe(false, false, MOLTEN_CORE, "ggg", "gmg", "ggg", 'm', Items.MAGMA_CREAM, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, STORM_EYE, "ggg", "gcg", "ggg", 'c', CREEPER_GLAND, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, FROZEN_CORE, "gpg", "gsg", "gsg", 'p', Blocks.PUMPKIN, 's', Blocks.SNOW, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, NEBULOUS_HEART, "ggg", "geg", "ggg", 'e', Items.ENDER_PEARL, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, SQUID_BEAK, "ggg", "gig", "ggg", 'i', Items.DYE, 'g', Items.GOLD_INGOT);
		addRecipe(false, false, GUARDIAN_SPIKE, "ggg", "gpg", "ggg", 'p', Items.PRISMARINE_SHARD, 'g', Items.GOLD_INGOT);
	}

	private static void addMobDropUncraftingRecipes() {

		addRecipe(true, false, new ItemStack(Items.BONE, 5), RIB_BONE);
		addRecipe(true, false, new ItemStack(Items.SKULL, 1, 1), WITHER_RIB, WITHER_RIB, WITHER_RIB, new ItemStack(Items.SKULL));
		addRecipe(true, false, new ItemStack(Items.ENDER_PEARL, 3), NEBULOUS_HEART);
		addRecipe(true, false, new ItemStack(Items.DYE, 6), SQUID_BEAK);
		addRecipe(true, false, new ItemStack(Items.STRING, 6), CHELICERAE);
		addRecipe(true, false, new ItemStack(Items.GUNPOWDER, 6), CREEPER_GLAND);
		addRecipe(true, false, new ItemStack(Items.SLIME_BALL, 6), SLIME_PEARL);
		addRecipe(true, false, new ItemStack(Items.ROTTEN_FLESH, 6), ZOMBIE_HEART);
		addRecipe(true, false, new ItemStack(Items.MAGMA_CREAM, 3), MOLTEN_CORE);
		addRecipe(true, false, new ItemStack(Items.GUNPOWDER, 10), STORM_EYE);
		addRecipe(true, false, new ItemStack(Items.SNOWBALL, 5), FROZEN_CORE);
		addRecipe(true, false, new ItemStack(Items.PRISMARINE_SHARD, 5), GUARDIAN_SPIKE);
	}

	public static final ItemStack EMPTY_VOID_TEAR = new ItemStack(ModItems.emptyVoidTear, 1, 0);
	public static final ItemStack WITHER_SKULL = new ItemStack(Items.SKULL, 1, 1);
	public static final ItemStack ROSE_BUSH = new ItemStack(Blocks.DOUBLE_PLANT, 1, 4);
	public static final ItemStack BLACK_WOOL = new ItemStack(Blocks.WOOL, 1, Reference.BLACK_WOOL_META);
	public static final ItemStack LAPIS = new ItemStack(Items.DYE, 1, 4);

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
	public static final ItemStack GUARDIAN_SPIKE = ingredient(1, Reference.GUARDIAN_INGREDIENT_META);

	public static ItemStack charmFragment(int meta) {
		return new ItemStack(ModItems.mobCharmFragment, 1, meta);
	}

	public static ItemStack mobCharm(byte meta) {
		ItemStack mobCharm = new ItemStack(ModItems.mobCharm);

		ModItems.mobCharm.setType(mobCharm, meta);

		return mobCharm;
	}

}
