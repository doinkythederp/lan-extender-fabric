package me.doinkythederp.lanextender;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MissingTokenWarningScreen extends WarningScreen {
    private static boolean warningHasBeenShown = false;

    public static boolean shouldSkipWarningCheck() {
        return warningHasBeenShown /* || options.skipMissingTokenWarning */;
    }

    private static final Text HEADER = Text.translatable("warning.lan_extender.missing_authtoken.header")
            .formatted(Formatting.BOLD);
    private static final Text MESSAGE = Text.translatable("warning.lan_extender.missing_authtoken.message");
    private static final Text CHECK_MESSAGE = Text.translatable("warning.lan_extender.missing_authtoken.check");
    private static final Text SETUP_GUIDE = Text.translatable("warning.lan_extender.missing_authtoken.setup_guide");
    private static final Text NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);
    private final Screen parent;

    private static final ContactInformation contactInfo = FabricLoader.getInstance()
            .getModContainer("lan_extender").get()
            .getMetadata().getContact();

    public MissingTokenWarningScreen(Screen parent) {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
        this.parent = parent;
    }

    @Override
    protected void initButtons(int yOffset) {
        this.addDrawableChild(
                new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, SETUP_GUIDE, buttonWidget -> {
                    Util.getOperatingSystem().open(contactInfo.get("sources").get() + "#setup");
                }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, 100 + yOffset, 150, 20, ScreenTexts.PROCEED,
                buttonWidget -> {
                    if (this.checkbox.isChecked()) {
                        // this.client.options.skipMultiplayerWarning = true;
                        // this.client.options.write();
                    }
                    warningHasBeenShown = true;
                    this.client.setScreen(this.parent);
                }));
    }
}
