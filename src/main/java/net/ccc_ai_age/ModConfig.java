package net.ccc_ai_age;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

/**
 * Standard mod configuration management using JSON format.
 */
public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ccc-ai-age.json");

	public boolean enableAutoOllamaStart = false;
	public boolean enableAutoDownloads = false;

	private static ModConfig instance;

	public static ModConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public static void load() {
		File file = CONFIG_PATH.toFile();
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				instance = GSON.fromJson(reader, ModConfig.class);
				if (instance != null) {
					return;
				}
			} catch (Exception e) {
				CCCAIAge.LOGGER.error("[CC:C AI Age] Failed to load config: {}", e.getMessage());
			}
		}
		instance = new ModConfig();
		save();
	}

	public static void save() {
		try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
			GSON.toJson(get(), writer);
		} catch (Exception e) {
			CCCAIAge.LOGGER.error("[CC:C AI Age] Failed to save config: {}", e.getMessage());
		}
	}
}
