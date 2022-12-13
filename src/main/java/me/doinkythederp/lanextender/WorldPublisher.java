package me.doinkythederp.lanextender;

import java.nio.file.Path;
import java.util.Optional;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.installer.NgrokInstaller;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Region;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import me.doinkythederp.lanextender.config.LANExtenderConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.doinkythederp.lanextender.LANExtenderMod.LOGGER;

/** Handles creating, closing, and restarting the Ngrok client */
public class WorldPublisher {
    private static final Path NGROK_INSTALL_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("lan_extender")
            .resolve("ngrok");
    private static final Path NGROK_PATH = OperatingSystemDetector.isWindows()
            ? NGROK_INSTALL_PATH.resolveSibling("ngrok.exe")
            : NGROK_INSTALL_PATH;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private Optional<NgrokClient> ngrokClient = Optional.empty();
    private boolean ngrokInstalled = NGROK_PATH.toFile().exists();
    private Optional<Integer> publishedPort = Optional.empty();

    public boolean isPublished() {
        return publishedPort.isPresent();
    }

    public Optional<Integer> getPublishedPort() {
        return publishedPort;
    }

    public void restartClient(String authToken, Region region) {
        LOGGER.info("Starting ngrok client");
        this.installNgrokIfNeeded();

        if (ngrokClient.isPresent()) {
            ngrokClient.get().kill();
        }

        var ngrokConfig = new JavaNgrokConfig.Builder()
                .withAuthToken(authToken)
                .withNgrokPath(NGROK_PATH)
                .withRegion(region)
                .build();
        NgrokClient ngrokClient = new NgrokClient.Builder()
                .withJavaNgrokConfig(ngrokConfig)
                .build();

        this.ngrokClient = Optional.of(ngrokClient);

        if (publishedPort.isPresent()) {
            this.republishPort();
        }
    }

    private void republishPort() {
        final var config = LANExtenderConfig.getInstance();
        final ChatHud chat = LANExtenderMod.client.inGameHud.getChatHud();
        int port = publishedPort.get();
        publishedPort = Optional.empty();
        if (!this.isReadyToPublish()) {
            LOGGER.info("Cannot publish in this state: use a non-empty authtoken");
            publishedPort = Optional.of(port);
            return;
        }
        try {
            Tunnel tunnel = this.publishPort(port);
            String tunnelAddress = getTunnelAddress(tunnel);
            if (config.copyAddressOnPublish) {
                client.keyboard.setClipboard(tunnelAddress);
            }

            chat.addMessage(
                    Text.translatable(config.copyAddressOnPublish ? "message.lan_extender.address_changed_copied"
                            : "message.lan_extender.address_changed",
                            Text.literal(tunnelAddress).formatted(Formatting.GREEN)));
        } catch (Exception e) {
            LOGGER.error("Failed to re-publish port {}: {}", port, e);
            chat.addMessage(
                    Text.translatable("error.lan_extender.failed_to_publish").formatted(Formatting.RED));
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
        this.ngrokClient.get().kill();
        publishedPort = Optional.empty();
    }

    public static String getTunnelAddress(Tunnel tunnel) {
        return tunnel.getPublicUrl().replace("tcp://", "");
    }
}
