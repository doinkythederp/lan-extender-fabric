package me.doinkythederp.lanextender.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.doinkythederp.lanextender.MissingTokenWarningScreen;
import me.doinkythederp.lanextender.config.LANExtenderConfig;
import me.doinkythederp.lanextender.config.LANExtenderConfigScreen;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.TranslatableText;

@Mixin(OpenToLanScreen.class)
public abstract class OpenToLANScreenMixin {
    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo info) {
        if (!LANExtenderMod.publisher.isReadyToPublish()) {
            return;
        }

        final ScreenAccessor lanScreen = (ScreenAccessor) (Object) this;
        final int messageWidth = lanScreen.getTextRenderer().getWidth(LANExtenderMod.checkboxMessage);
        final int checkboxWidth = 24;
        LANExtenderMod.publishCheckbox = new CheckboxWidget((lanScreen.getWidth() - messageWidth - checkboxWidth) / 2,
                128,
                messageWidth + checkboxWidth, 20,
                LANExtenderMod.checkboxMessage, false);

        CheckboxWidget checkbox = LANExtenderMod.publishCheckbox;
        lanScreen.getButtons().add(checkbox);
        lanScreen.getChildren().add(checkbox);

        ButtonWidget configButton = new ButtonWidget(lanScreen.getWidth() / 2 - (150 / 2), lanScreen.getHeight() - 50,
                150, 20,
                new TranslatableText("button.lan_extender.config"), buttonWidget -> {
                    LANExtenderConfigScreen.openConfigScreen((Screen) (Object) this);
                });
        lanScreen.getButtons().add(configButton);
        lanScreen.getChildren().add(configButton);
    }
}
