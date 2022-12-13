package me.doinkythederp.lanextender.mixin;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.doinkythederp.lanextender.config.LANExtenderConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void automaticOpenToLan(CallbackInfo info) {
        final var client = MinecraftClient.getInstance();
        if (!client.isIntegratedServerRunning())
            return;
        final var config = LANExtenderConfig.getInstance();
        if (config.autoPublishWorlds) {
            final var server = client.getServer();
            final ChatHud chat = client.inGameHud.getChatHud();

            final GameMode gameMode = server.getDefaultGameMode();
            final boolean commandsAllowed = server.getSaveProperties().areCommandsAllowed();
            int port = config.autoPublishPort;
            if (port == 0) {
                port = NetworkUtils.findLocalPort();
            }

            // first, we need to open a port
            final boolean didOpenToLanSucceed = server.openToLan(
                    gameMode,
                    commandsAllowed, port);
            final Text message = didOpenToLanSucceed
                    ? PublishCommand.getStartedText(port)
                    : Text.translatable("commands.publish.failed");

            chat.addMessage(message);
            client.updateWindowTitle();

            // now, we can publish to Ngrok
            LANExtenderMod.publishToNgrok(port);
        }
    }
}
