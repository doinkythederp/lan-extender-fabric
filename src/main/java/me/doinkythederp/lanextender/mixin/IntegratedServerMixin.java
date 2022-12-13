package me.doinkythederp.lanextender.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.doinkythederp.lanextender.LANExtenderMod;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
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
        // C. the world might already have been published by `automaticOpenToLan`
        if (LANExtenderMod.lanServersShouldPublish() && LANExtenderMod.publisher.isPublished()) {
            LANExtenderMod.publishToNgrok(port);
        }
    }

    @Inject(at = @At("RETURN"), method = "stop")
    private void afterStop(boolean joinServerThread, CallbackInfo info) {
        LANExtenderMod.publisher.closePort();
    }
}
