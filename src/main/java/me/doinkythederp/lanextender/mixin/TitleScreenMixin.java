package me.doinkythederp.lanextender.mixin;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.doinkythederp.lanextender.MissingTokenWarningScreen;
import me.doinkythederp.lanextender.config.LANExtenderConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.screen.TitleScreen;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("TAIL"), method = "init()V")
    private void showSetupScreenIfAuthtokenMissing(CallbackInfo info) {
        if (MissingTokenWarningScreen.shouldSkipWarningCheck()) {
            return;
        }

        if (LANExtenderConfig.getInstance().authToken.isEmpty()) {
            LANExtenderMod.client.setScreen(new MissingTokenWarningScreen((TitleScreen) (Object) this));
        }
    }
}
