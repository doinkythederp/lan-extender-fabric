package me.doinkythederp.lanextender;

import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.installer.NgrokInstaller;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

import static me.doinkythederp.lanextender.LANExtenderMod.LOGGER;

public class WorldPublisher {
    private static final Path NGROK_INSTALL_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("lan_extender")
            .resolve("ngrok");
    private static final Path NGROK_PATH = OperatingSystemDetector.isWindows()
            ? NGROK_INSTALL_PATH.resolveSibling("ngrok.exe")
            : NGROK_INSTALL_PATH;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    @Nullable
    private NgrokClient ngrokClient = null;
    private boolean ngrokInstalled = NGROK_PATH.toFile().exists();
    @Nullable
    private Integer publishedPort = null;

    public void restartClient(String authToken) {
        LOGGER.info("Starting ngrok client");
        this.installNgrokIfNeeded();

        if (ngrokClient != null && ngrokClient.getNgrokProcess().isRunning()) {
            client.openScreen(new RestartGameScreen(client.currentScreen));
            return;
        }

        JavaNgrokConfig ngrokConfig = new JavaNgrokConfig.Builder()
                .withAuthToken(authToken)
                .withNgrokPath(NGROK_PATH)
                .build();
        NgrokClient ngrokClient = new NgrokClient.Builder()
                .withJavaNgrokConfig(ngrokConfig)
                .build();

        this.ngrokClient = ngrokClient;

        if (publishedPort != null) {
            int port = publishedPort;
            publishedPort = null;
            try {
                Tunnel tunnel = this.publishPort(port);
                LANExtenderMod.client.inGameHud.getChatHud().addMessage(
                        new TranslatableText("message.lan_extender.address_changed", getTunnelAddress(tunnel)));
            } catch (Exception e) {
                LOGGER.error("Failed to re-publish port {}: {}", port, e);
            }
        }
    }

    private void installNgrokIfNeeded() {
        if (this.ngrokInstalled) {
            return;
        }

        LOGGER.info("Installing ngrok client");
        NgrokInstaller ngrokInstaller = new NgrokInstaller();
        try {
            ngrokInstaller.installNgrok(NGROK_INSTALL_PATH);
            this.ngrokInstalled = true;
        } catch (Exception e) {
            LOGGER.error("Failed to install ngrok client");
            throw e;
        }
        LOGGER.info("Finished installing ngrok.");
    }

    public boolean isReadyToPublish() {
        return this.ngrokClient != null && this.publishedPort == null
                && !this.ngrokClient.getJavaNgrokConfig().getAuthToken().isEmpty();
    }

    public Tunnel publishPort(int port) {
        if (publishedPort != null) {
            throw new IllegalStateException("Cannot publish port, already publishing another port");
        }
        if (!this.isReadyToPublish()) {
            throw new IllegalStateException("Not ready to publish port");
        }

        LOGGER.info("Publishing port {} to ngrok", port);

        this.publishedPort = port;
        CreateTunnel createTunnel = new CreateTunnel.Builder()
                .withProto(Proto.TCP)
                .withAddr(port)
                .build();
        Tunnel tunnel = this.ngrokClient.connect(createTunnel);
        LOGGER.info("Public URL is {}", tunnel.getPublicUrl());
        return tunnel;
    }

    public void closePort() {
        LOGGER.info("Unpublishing all ports");
        NgrokClient ngrok = this.ngrokClient;
        ngrok.getTunnels().forEach(tunnel -> ngrok.disconnect(tunnel.getPublicUrl()));
        publishedPort = null;
    }

    public static String getTunnelAddress(Tunnel tunnel) {
        return tunnel.getPublicUrl().replace("tcp://", "");
    }
}
