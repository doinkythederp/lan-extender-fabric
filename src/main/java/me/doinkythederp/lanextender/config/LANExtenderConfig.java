package me.doinkythederp.lanextender.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import static me.doinkythederp.lanextender.LANExtenderMod.LOGGER;

public class LANExtenderConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("LANExtenderConfig.json");
    private static final Path OLD_CONFIG_PATH = CONFIG_PATH.resolveSibling("LANExtenderAuthToken.txt");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Nullable
    private static LANExtenderConfig CONFIG_INSTANCE = null;

    public String authToken = "";
    public boolean hideAuthTokenMissingWarning = false;

    public static LANExtenderConfig getInstance() {
        if (CONFIG_INSTANCE == null) {
            loadConfig();
        }
        return CONFIG_INSTANCE;
    }

    public static void loadConfig() {
        LOGGER.info("Loading configuration file");

        try {
            LANExtenderConfig config = readConfig();
            if (config != null) {
                CONFIG_INSTANCE = config;
                return;
            }

            CONFIG_INSTANCE = new LANExtenderConfig();

            @Nullable
            String oldConfig = readOldConfig();
            if (oldConfig != null) {
                if (CONFIG_INSTANCE.authToken.isEmpty()) {
                    CONFIG_INSTANCE.authToken = oldConfig;
                }
                Files.deleteIfExists(OLD_CONFIG_PATH);
            }

            writeConfig(CONFIG_INSTANCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        LOGGER.info("Saving configuration file");

        if (CONFIG_INSTANCE == null) {
            return;
        }

        try {
            writeConfig(CONFIG_INSTANCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static String readOldConfig() throws IOException {
        if (!Files.isRegularFile(OLD_CONFIG_PATH)) {
            return null;
        }

        try (BufferedReader br = Files.newBufferedReader(OLD_CONFIG_PATH)) {
            return br.readLine();
        } catch (IOException e) {
            LOGGER.warn("Failed to parse authtoken file");
            return null;
        }
    }

    @Nullable
    private static LANExtenderConfig readConfig() throws IOException {
        if (!Files.isRegularFile(CONFIG_PATH)) {
            LOGGER.warn("Config file not readable");
            return null;
        }

        try (BufferedReader br = Files.newBufferedReader(CONFIG_PATH)) {
            return GSON.fromJson(br, LANExtenderConfig.class);
        } catch (IOException e) {
            LOGGER.error("Failed to parse config file");
            throw e;
        }
    }

    private static void writeConfig(LANExtenderConfig config) throws IOException {
        try (BufferedWriter br = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, br);
        } catch (IOException e) {
            LOGGER.error("Failed to write config file");
            throw e;
        }
    }
}
