package com.murqin.nocroptrample.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.murqin.nocroptrample.NoCropTrampleMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration manager for the NoCropTrample mod.
 * <p>
 * Handles loading, saving, and accessing mod configuration values.
 * Config file is stored as JSON in the Fabric config directory.
 * </p>
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve(NoCropTrampleMod.MOD_ID + ".json");

    // Config values - encapsulated with getters/setters
    private static boolean preventPlayerTrampling = true;
    private static boolean preventMobTrampling = true;

    /**
     * Gets whether player trampling prevention is enabled.
     *
     * @return true if players cannot trample farmland, false otherwise
     */
    public static boolean isPreventPlayerTrampling() {
        return preventPlayerTrampling;
    }

    /**
     * Sets whether player trampling prevention is enabled.
     * Automatically saves the configuration after updating.
     *
     * @param value true to prevent players from trampling farmland, false otherwise
     */
    public static void setPreventPlayerTrampling(boolean value) {
        preventPlayerTrampling = value;
        save();
    }

    /**
     * Gets whether mob trampling prevention is enabled.
     *
     * @return true if mobs cannot trample farmland, false otherwise
     */
    public static boolean isPreventMobTrampling() {
        return preventMobTrampling;
    }

    /**
     * Sets whether mob trampling prevention is enabled.
     * Automatically saves the configuration after updating.
     *
     * @param value true to prevent mobs from trampling farmland, false otherwise
     */
    public static void setPreventMobTrampling(boolean value) {
        preventMobTrampling = value;
        save();
    }

    /**
     * Loads configuration from disk.
     * If the config file doesn't exist, creates a new one with default values.
     */
    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = new String(Files.readAllBytes(CONFIG_PATH), java.nio.charset.StandardCharsets.UTF_8);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    preventPlayerTrampling = data.preventPlayerTrampling;
                    preventMobTrampling = data.preventMobTrampling;
                }
                NoCropTrampleMod.LOGGER.info("Config loaded from {}", CONFIG_PATH);
            } catch (IOException e) {
                NoCropTrampleMod.LOGGER.error("Failed to load config, using defaults", e);
            }
        } else {
            save(); // Create default config
        }
    }

    /**
     * Saves the current configuration to disk.
     */
    public static void save() {
        try {
            ConfigData data = new ConfigData();
            data.preventPlayerTrampling = preventPlayerTrampling;
            data.preventMobTrampling = preventMobTrampling;

            Files.createDirectories(CONFIG_PATH.getParent());
            Files.write(CONFIG_PATH, GSON.toJson(data).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            NoCropTrampleMod.LOGGER.info("Config saved to {}", CONFIG_PATH);
        } catch (IOException e) {
            NoCropTrampleMod.LOGGER.error("Failed to save config", e);
        }
    }

    /**
     * Inner class for JSON serialization/deserialization.
     */
    private static class ConfigData {
        boolean preventPlayerTrampling = true;
        boolean preventMobTrampling = true;
    }
}
