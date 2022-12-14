package me.doinkythederp.lanextender.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.alexdlaird.ngrok.protocol.Tunnel;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.doinkythederp.lanextender.WorldPublisher;
import me.doinkythederp.lanextender.config.LANExtenderConfig;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Shadow
    private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "openToLan")
    private void afterOpenToLan(@Nullable GameMode gameMode,
            boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> info) {
        // We check before starting ngrok because
        // A. the user might have used the /publish command instead
        // B. the user might have not checked the "yes, publish server" checkbox on the
        // LAN screen
        if (LANExtenderMod.lanServersShouldPublish()) {
            new Thread(() -> {
                final ChatHud chat = this.client.inGameHud.getChatHud();
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

    @Inject(at = @At("RETURN"), method = "stop")
    private void afterStop(boolean joinServerThread, CallbackInfo info) {
        LANExtenderMod.publisher.closePort();
    }
}
