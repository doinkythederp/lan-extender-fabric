package me.doinkythederp.lanextender;

import java.nio.file.Path;
import java.util.Optional;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.installer.NgrokInstaller;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import net.fabricmc.loader.api.FabricLoader;
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

    private Optional<NgrokClient> ngrokClient = Optional.empty();
    private boolean ngrokInstalled = NGROK_PATH.toFile().exists();
    private Optional<Integer> publishedPort = Optional.empty();

    public void restartClient(String authToken) {
        LOGGER.info("Starting ngrok client");
        this.installNgrokIfNeeded();

        if (ngrokClient.isPresent()) {
            ngrokClient.get().kill();
        }

        var ngrokConfig = new JavaNgrokConfig.Builder()
                .withAuthToken(authToken)
                .withNgrokPath(NGROK_PATH)
                .build();
        NgrokClient ngrokClient = new NgrokClient.Builder()
                .withJavaNgrokConfig(ngrokConfig)
                .build();

        this.ngrokClient = Optional.of(ngrokClient);

        if (publishedPort.isPresent()) {
            int port = publishedPort.get();
            publishedPort = Optional.empty();
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
        var ngrokInstaller = new NgrokInstaller();
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
        return this.ngrokClient.isPresent() && this.publishedPort.isEmpty()
                && !this.ngrokClient.get().getJavaNgrokConfig().getAuthToken().isEmpty();
    }

    public Tunnel publishPort(int port) {
        if (publishedPort.isPresent()) {
            throw new IllegalStateException("Cannot publish port, already publishing another port");
        }
        if (!this.isReadyToPublish()) {
            throw new IllegalStateException("Not ready to publish port");
        }

        LOGGER.info("Publishing port {} to ngrok", port);

        this.publishedPort = Optional.of(port);
        var createTunnel = new CreateTunnel.Builder()
                .withProto(Proto.TCP)
                .withAddr(port)
                .build();
        Tunnel tunnel = this.ngrokClient.get().connect(createTunnel);
        LOGGER.info("Public URL is {}", tunnel.getPublicUrl());
        return tunnel;
    }

    public void closePort() {
        LOGGER.info("Unpublishing all ports");
        var ngrok = this.ngrokClient.get();
        ngrok.getTunnels().forEach(tunnel -> ngrok.disconnect(tunnel.getPublicUrl()));
        publishedPort = Optional.empty();
    }

    public static String getTunnelAddress(Tunnel tunnel) {
        return tunnel.getPublicUrl().replace("tcp://", "");
    }
}
