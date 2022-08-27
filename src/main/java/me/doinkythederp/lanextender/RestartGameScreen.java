package me.doinkythederp.lanextender;

import me.doinkythederp.lanextender.config.LANExtenderConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.class_5489;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class RestartGameScreen extends Screen {
    private static final Text HEADER = new TranslatableText("title.lan_extender.restart_game.header")
            .formatted(Formatting.BOLD);
    private static final Text MESSAGE = new TranslatableText("info.lan_extender.restart_game.message");
    private static final Text NARRATED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);
    private final Screen parent;
    // private MultilineText textMessage = MultilineText.EMPTY;
    private class_5489 textMessage = class_5489.field_26528;

    public RestartGameScreen(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        // this.textMessage = MultilineText.create(textRenderer, message, width);
        this.textMessage = class_5489.method_30890(this.textRenderer, MESSAGE, this.width - 50);
        // int yOffset = (this.textMessage.count() + ...
        int yOffset = (this.textMessage.method_30887() + 1) * this.textRenderer.fontHeight * 2;

        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, ScreenTexts.CANCEL,
                buttonWidget -> {
                    this.client.openScreen(this.parent);
                }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, 100 + yOffset, 150, 20, ScreenTexts.PROCEED,
                buttonWidget -> {
                    this.client.stop();
                }));
    }

    @Override
    public String getNarrationMessage() {
        return NARRATED_TEXT.getString();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        MissingTokenWarningScreen.drawCenteredText(matrices, this.textRenderer, HEADER, this.width / 2, 30, 0xFFFFFF);
        // this.textMessage.drawWithShadow(matrices, width, startingY);
        this.textMessage.method_30888(matrices, this.width / 2, 70);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
