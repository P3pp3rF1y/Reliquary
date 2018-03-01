package xreliquary.crafting;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import xreliquary.reference.Reference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

public class AlkahestryLoader {

	private static final String CONFIG_FOLDER = "config/" + Reference.MOD_ID + "/alkahestry_overrides";
	private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Method LOAD_CONSTANTS = ReflectionHelper.findMethod(JsonContext.class, "loadConstants", null, JsonObject[].class);
	private static Set<String> foldersLoaded = Sets.newHashSet();


	private static void loadConstants(JsonContext ctx, JsonObject[] jsons) {
		try {
			LOAD_CONSTANTS.invoke(ctx, (Object) jsons);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void loadRecipes() {
		createConfigFolder();
		loadAlkahestry(new File(CONFIG_FOLDER), "");
		loadAlkahestry(Loader.instance().activeModContainer().getSource(), "assets/" + Reference.MOD_ID + "/alkahestry");
	}

	private static void createConfigFolder() {
		(new File(CONFIG_FOLDER)).mkdirs();
	}

	private static void loadAlkahestry(File source, String base) {
		FileSystem fs = null;

		try {
			Path root = null;
			if (source.isFile()) {
				try {
					fs = FileSystems.newFileSystem(source.toPath(), null);
					root = fs.getPath("/" + base);
				}
				catch (IOException e) {
					FMLLog.log.error("Error loading FileSystem from jar: ", e);
				}
			} else if (source.isDirectory()) {
				root = source.toPath().resolve(base);
			}

			if (root == null || !Files.exists(root)) {
				return;
			}

			JsonContext ctx = loadJsonContext(root);

			loadRecipeFolder(root, "crafting", ctx);
			loadRecipeFolder(root, "charging", ctx);
			loadRecipeFolder(root, "drain", ctx);

		}
		finally {
			IOUtils.closeQuietly(fs);
		}
	}

	private static void loadRecipeFolder(Path root, String folderName, JsonContext ctx) {
		if (foldersLoaded.contains(folderName)) {
			return;
		}

		Iterator<Path> itr = null;

		Path folderRoot = root.resolve(folderName);

		try {
			itr = Files.walk(folderRoot).iterator();
		}
		catch (IOException e) {
			FMLLog.log.error("Error iterating filesystem for: {}", Reference.MOD_ID, e);
		}

		boolean recipeAdded = false;
		while (itr != null && itr.hasNext()) {
			recipeAdded |= loadRecipe(root, itr.next(), ctx);
		}

		if (recipeAdded) {
			foldersLoaded.add(folderName);
		}
	}

	private static boolean loadRecipe(Path root, Path file, JsonContext ctx) {
		String relative = root.relativize(file).toString();
		if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
			return false;

		String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
		ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(file);
			JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
			if (json.has("conditions") && !CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), ctx))
				return false;
			IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
			ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
		}
		catch (JsonParseException e) {
			FMLLog.log.error("Parsing error loading recipe {}", key, e);
			return false;
		}
		catch (IOException e) {
			FMLLog.log.error("Couldn't read recipe {} from {}", key, file, e);
			return false;
		}
		finally {
			IOUtils.closeQuietly(reader);
		}

		return true;
	}

	private static JsonContext loadJsonContext(Path root) {
		JsonContext ctx = new JsonContext(Reference.MOD_ID);

		Path fPath = root.resolve("_constants.json");
		if (fPath != null && Files.exists(fPath)) {
			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(fPath);
				JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
				loadConstants(ctx, json);
			}
			catch (IOException e) {
				FMLLog.log.error("Error loading _constants.json: ", e);
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		}

		return ctx;
	}
}
