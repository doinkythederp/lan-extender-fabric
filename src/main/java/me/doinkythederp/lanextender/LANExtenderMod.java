package me.doinkythederp.lanextender;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.installer.NgrokInstaller;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import me.doinkythederp.lanextender.config.LANExtenderConfig;

public class LANExtenderMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("lan_extender");
    public static final Text checkboxMessage = Text.translatable("lanServer.publish");
    public static Optional<CheckboxWidget> publishCheckbox = Optional.empty();
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static boolean ngrokInstalled = false;

    private static Path ngrokPath;
    private static Optional<NgrokClient> ngrokClient = Optional.empty();
    private static Optional<Integer> ngrokPort = Optional.empty();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ngrokPath = FabricLoader.getInstance().getGameDir().resolve("lan_extender").resolve("ngrok");

        LANExtenderConfig.loadConfig();

        if (ngrokPath.toFile().exists()) {
            ngrokInstalled = true;
            LOGGER.info("Ngrok is already installed.", ngrokPath);
            startNgrokClient();
        } else {
            LOGGER.info("Installing ngrok to `{}`…", ngrokPath);
            new Thread(() -> {
                var ngrokInstaller = new NgrokInstaller();
                try {
                    ngrokInstaller.installNgrok(ngrokPath);
                    ngrokInstalled = true;
                    LOGGER.info("Finished installing ngrok.");
                    startNgrokClient();
                } catch (Exception e) {
                    LOGGER.error("Failed to install ngrok: {}", e.getMessage());
                }
            }).start();
        }
    }

    private static void startNgrokClient() {
        if (!ngrokInstalled) {
            LOGGER.warn("Ngrok is not installed, publishing LAN servers is not available.");
            return;
        }

        final String ngrokToken = LANExtenderConfig.getInstance().authToken;
        if (ngrokToken.isEmpty()) {
            LOGGER.warn("No ngrok token found, publishing LAN servers is not available.");
            return;
        }
        if (ngrokClient.isPresent()) {
            LOGGER.debug("Stopping ngrok client!");
            disconnectTunnel();
            ngrokClient.get().kill();
        }

        Path ngrokBinaryPath = isWindows() ? ngrokPath.resolveSibling("ngrok.exe") : ngrokPath;

        var config = new JavaNgrokConfig.Builder()
                .withAuthToken(ngrokToken)
                .withNgrokPath(
                        ngrokBinaryPath)
                .build();
        ngrokClient = Optional.of(new NgrokClient.Builder()
                .withJavaNgrokConfig(config)
                .build());

        if (ngrokPort.isPresent()) {
            int port = ngrokPort.get();
            ngrokPort = Optional.empty();
            ChatHud chatHud = client.inGameHud.getChatHud();
            try {
                Tunnel tunnel = publishPort(port);
                chatHud.addMessage(
                        Text.literal("Your server is now available @ " + tunnel.getPublicUrl().replace("tcp://", "")));
            } catch (Exception e) {
                chatHud.addMessage(Text.translatable("error.lan_extender.failed_to_publish"));
            }

        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static Tunnel publishPort(int port) throws IllegalStateException {
        if (ngrokPort.isPresent()) {
            throw new IllegalStateException("Already publishing to port " + ngrokPort.get());
        }
        if (!ngrokClient.isPresent()) {
            throw new IllegalStateException("Ngrok client not ready");
        }
        ngrokPort = Optional.of(port);
        var tunnel = new CreateTunnel.Builder()
                .withProto(Proto.TCP)
                .withAddr(port)
                .build();
        return ngrokClient.get().connect(tunnel);
    }

    public static void disconnectTunnel() {
        if (!ngrokClient.isPresent()) {
            return;
        }

        List<Tunnel> tunnels;
        try {
            tunnels = ngrokClient.get().getTunnels();
        } catch (Exception e) {
            tunnels = new ArrayList<>();
        }

        tunnels.forEach(tunnel -> {
            LOGGER.debug("Disconnecting tunnel {}", tunnel.getName());
            ngrokClient.get().disconnect(tunnel.getPublicUrl());
        });
        ngrokPort = Optional.empty();
    }

    /**
     * Checks if the LAN server should be published using ngrok after
     * <code>MinecraftServer.openToLan</code> is called.
     *
     * @return Returns true if the publish checkbox was present and checked.
     */
    public static boolean lanServersShouldPublish() {
        return LANExtenderMod.publishCheckbox.isPresent() && LANExtenderMod.publishCheckbox.get().isChecked();
    }
}
