package me.doinkythederp.lanextender;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static WorldPublisher publisher = new WorldPublisher();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LANExtenderConfig.loadConfig();

        var config = LANExtenderConfig.getInstance();
        new Thread(() -> {
            try {
                publisher.restartClient(config.authToken, config.region);
                LOGGER.info("LAN Extender is initialized. Have fun sharing your worlds!");
            } catch (Exception e) {
                LOGGER.error("Failed to start ngrok client: {}", e);
            }
        }, "LAN-Extender-Initializer").start();
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

    /**
     * Uses the WorldPublisher to publish a port, handling errors and posting status
     * messages in chat. Also copies the address to the clipboard if neccesary.
     * <br />
     * <br />
     * Note: Not sync - returns immediately.
     */
    public static void publishToNgrok(int port) {
        new Thread(() -> {
            var client = MinecraftClient.getInstance();
            final ChatHud chat = client.inGameHud.getChatHud();
            final var config = LANExtenderConfig.getInstance();
            try {
                Tunnel tunnel = LANExtenderMod.publisher.publishPort(port);
                String tunnelAddress = WorldPublisher.getTunnelAddress(tunnel);
                if (config.copyAddressOnPublish) {
                    client.keyboard.setClipboard(tunnelAddress);
                }

                chat.addMessage(
                        Text.translatable(
                                config.copyAddressOnPublish ? "message.lan_extender.world_published_copied"
                                        : "message.lan_extender.world_published",
                                Text.literal(tunnelAddress).formatted(Formatting.GREEN)));
            } catch (Exception e) {
                LANExtenderMod.LOGGER.error("Failed to publish port:", e);
                chat.addMessage(
                        Text.translatable("error.lan_extender.failed_to_publish").formatted(Formatting.RED));
            }
        }, "LAN-Extender-Publisher").start();
    }
}
