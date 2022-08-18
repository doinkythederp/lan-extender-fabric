package me.doinkythederp.ngrokr.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.alexdlaird.ngrok.protocol.Tunnel;

import me.doinkythederp.ngrokr.NgrokrMod;

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
        if (NgrokrMod.lanServersShouldPublish()) {
            NgrokrMod.LOGGER.info("Starting ngrok tunnel through {}…", port);
            ChatHud chat = this.client.inGameHud.getChatHud();

            if (!NgrokrMod.getNgrokToken().isPresent()) {
                // TODO: explain how to add an ngrok auth token
                // Right now you have to put it in the NgrokrAuthToken.txt config file
                chat.addMessage(Text.literal("Ngrokr requires an ngrok authentication token to publish servers."));
                return;
            }

            try {
                Tunnel tunnel = NgrokrMod.publishPort(port);
                chat.addMessage(Text
                        .literal("Others can join your world using the following server address: "
                                + tunnel.getPublicUrl().replace("tcp://", "")));
            } catch (Exception e) {
                NgrokrMod.LOGGER.error(e.getClass().getSimpleName() + ": "
                        + e.getMessage());
                chat.addMessage(
                        Text.literal("Failed to publish LAN server. Is your ngrok authentication token valid?"));
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "stop")
    private void afterStop(boolean joinServerThread, CallbackInfo info) {
        NgrokrMod.disconnectTunnel();
    }
}
