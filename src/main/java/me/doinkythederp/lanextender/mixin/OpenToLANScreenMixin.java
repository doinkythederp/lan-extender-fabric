package me.doinkythederp.lanextender.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.doinkythederp.lanextender.LANExtenderMod;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Mixin(OpenToLanScreen.class)
public abstract class OpenToLANScreenMixin {
    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        if (!LANExtenderMod.publisher.isReadyToPublish()) {
            return;
        }

        final var lanScreen = (ScreenAccessor) (Object) this;
        final Screen parent = ((OpenToLANScreenAccessor) (Object) this).getParent();
        final int messageWidth = lanScreen.getTextRenderer().getWidth(LANExtenderMod.checkboxMessage);
        final int checkboxWidth = 24;
        LANExtenderMod.publishCheckbox = Optional.of(
                new CheckboxWidget((lanScreen.getWidth() - messageWidth - checkboxWidth) / 2,
                        lanScreen.getHeight() - 56,
                        messageWidth + checkboxWidth, 20,
                        LANExtenderMod.checkboxMessage, false));

        // Alternative to addDrawableChild that doesn't crash in release mode
        CheckboxWidget checkbox = LANExtenderMod.publishCheckbox.get();
        lanScreen.getDrawables().add(checkbox);
        lanScreen.getChildren().add(checkbox);
        lanScreen.getSelectables().add(checkbox);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"), index = 4)
    private int injected(int y) {
        return y * 3;
    }
}
