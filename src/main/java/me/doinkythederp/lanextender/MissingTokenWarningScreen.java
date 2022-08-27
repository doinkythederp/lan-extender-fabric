package me.doinkythederp.lanextender;

import me.doinkythederp.lanextender.config.LANExtenderConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MissingTokenWarningScreen extends Screen {
    private static boolean warningHasBeenShown = false;

    public static boolean shouldSkipWarningCheck() {
        var config = LANExtenderConfig.getInstance();
        return warningHasBeenShown || config.hideAuthTokenMissingWarning;
    }

    private static final Text HEADER = new TranslatableText("warning.lan_extender.missing_authtoken.header")
            .formatted(Formatting.BOLD);
    private static final Text MESSAGE = new TranslatableText("warning.lan_extender.missing_authtoken.message");
    private static final Text CHECK_MESSAGE = new TranslatableText("warning.lan_extender.missing_authtoken.check");
    private static final Text SETUP_GUIDE = new TranslatableText("warning.lan_extender.missing_authtoken.setup_guide");
    private static final Text NARRATED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);
    private final Screen parent;
    private CheckboxWidget checkbox;
    private MultilineText textMessage = MultilineText.EMPTY;

    private static final ContactInformation contactInfo = FabricLoader.getInstance()
            .getModContainer("lan_extender").get()
            .getMetadata().getContact();

    public MissingTokenWarningScreen(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.textMessage = MultilineText.create(this.textRenderer, (StringVisitable) MESSAGE, this.width - 50);
        int yOffset = (this.textMessage.count() + 1) * this.textRenderer.fontHeight * 2;

        this.checkbox = this.addDrawableChild(
                new CheckboxWidget(this.width / 2 - 155 + 80, 76 + yOffset, 150, 20, CHECK_MESSAGE, false));
        this.addDrawableChild(
                new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, SETUP_GUIDE, buttonWidget -> {
                    Util.getOperatingSystem().open(contactInfo.get("sources").get() + "#setup");
                }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, 100 + yOffset, 150, 20, ScreenTexts.PROCEED,
                buttonWidget -> {
                    if (this.checkbox.isChecked()) {
                        var config = LANExtenderConfig.getInstance();
                        config.hideAuthTokenMissingWarning = true;
                        LANExtenderConfig.saveConfig();
                    }
                    warningHasBeenShown = true;
                    this.client.openScreen(this.parent);
                }));
    }

    @Override
    public Text getNarratedTitle() {
        return NARRATED_TEXT;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        drawTextWithShadow(matrices, this.textRenderer, HEADER, 25, 30, 0xFFFFFF);
        this.textMessage.drawWithShadow(matrices, 25, 70, this.textRenderer.fontHeight * 2, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
