package me.doinkythederp.ngrokr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

@Mixin(OpenToLanScreen.class)
public abstract class OpenToLANScreenMixin {
    private static final Text checkMessage = Text.translatable("lanServer.publish");

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        var lanScreen = (ScreenAccessor) (Object) this;
        int messageWidth = lanScreen.getTextRenderer().getWidth(checkMessage);
        lanScreen.invokeAddDrawableChild(
                new CheckboxWidget((lanScreen.getWidth() - messageWidth) / 2 - 8, 128, messageWidth + 24, 20,
                        checkMessage, false));
    }
}
