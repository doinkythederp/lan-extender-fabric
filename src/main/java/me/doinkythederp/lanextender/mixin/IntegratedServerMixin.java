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
                try {
                    Tunnel tunnel = LANExtenderMod.publisher.publishPort(port);
                    chat.addMessage(
                            Text.translatable("message.lan_extender.world_published",
                                    WorldPublisher.getTunnelAddress(tunnel)));
                } catch (Exception e) {
                    LANExtenderMod.LOGGER.error("Failed to publish port:", e);
                    chat.addMessage(
                            Text.translatable("error.lan_extender.failed_to_publish"));
                }
            }, "LAN-Extender-Publisher").start();
        }
    }

    @Inject(at = @At("RETURN"), method = "stop")
    private void afterStop(boolean joinServerThread, CallbackInfo info) {
        LANExtenderMod.publisher.closePort();
    }
}
