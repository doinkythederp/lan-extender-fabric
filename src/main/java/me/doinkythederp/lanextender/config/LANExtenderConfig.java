package me.doinkythederp.lanextender.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import static me.doinkythederp.lanextender.LANExtenderMod.LOGGER;

public class LANExtenderConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("LANExtenderConfig.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Optional<LANExtenderConfig> CONFIG_INSTANCE;

    public String authToken = "";
    public boolean hideAuthTokenMissingWarning = false;

    public static LANExtenderConfig getInstance() {
        if (CONFIG_INSTANCE.isEmpty()) {
            loadConfig();
        }
        return CONFIG_INSTANCE.get();
    }

    public static void loadConfig() {
        LOGGER.info("Loading configuration file");

        try {
            // TODO: fall back to LANExtenderAuthToken.txt
            Optional<LANExtenderConfig> config = readConfig();
            if (config.isPresent()) {
                CONFIG_INSTANCE = config;
                return;
            }

            CONFIG_INSTANCE = Optional.of(new LANExtenderConfig());
            writeConfig(CONFIG_INSTANCE.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        LOGGER.info("Saving configuration file");

        if (CONFIG_INSTANCE.isEmpty()) {
            return;
        }

        try {
            writeConfig(CONFIG_INSTANCE.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<LANExtenderConfig> readConfig() throws IOException {
        if (!Files.isRegularFile(CONFIG_PATH)) {
            LOGGER.warn("Config file not readable");
            return Optional.empty();
        }

        try (BufferedReader br = Files.newBufferedReader(CONFIG_PATH)) {
            return Optional.of(GSON.fromJson(br, LANExtenderConfig.class));
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
