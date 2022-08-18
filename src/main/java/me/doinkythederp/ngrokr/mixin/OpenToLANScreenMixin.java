package me.doinkythederp.ngrokr.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.doinkythederp.ngrokr.NgrokrMod;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;

@Mixin(OpenToLanScreen.class)
public class OpenToLANScreenMixin {
    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        var lanScreen = (ScreenAccessor) (Object) this;
        int messageWidth = lanScreen.getTextRenderer().getWidth(NgrokrMod.checkboxMessage);
        NgrokrMod.publishCheckbox = Optional.of(
                new CheckboxWidget((lanScreen.getWidth() - messageWidth) / 2 - 8, 128, messageWidth + 24, 20,
                        NgrokrMod.checkboxMessage, false));
        lanScreen.invokeAddDrawableChild(NgrokrMod.publishCheckbox.get());
    }
}
