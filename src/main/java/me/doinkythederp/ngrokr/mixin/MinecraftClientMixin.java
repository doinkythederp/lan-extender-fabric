package me.doinkythederp.ngrokr.mixin;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.doinkythederp.ngrokr.NgrokrMod;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "setScreen")
    private void clearLANScreenCheckbox(@Nullable net.minecraft.client.gui.screen.Screen screen, CallbackInfo info) {
        if (screen != null && NgrokrMod.publishCheckbox.isPresent()) {
            // Checkbox state should not persist after closing the Open To LAN screen.
            // This would mess up the /publish command, which should not start an ngrok
            // session.
            NgrokrMod.publishCheckbox = Optional.empty();
        }
    }
}
