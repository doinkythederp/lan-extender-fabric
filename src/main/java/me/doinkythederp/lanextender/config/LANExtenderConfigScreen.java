package me.doinkythederp.lanextender.config;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class LANExtenderConfigScreen {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void openConfigScreen(Screen parent) {
        final var config = LANExtenderConfig.getInstance();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("title.lan_extender.config"))
                .setSavingRunnable(() -> {
                    LANExtenderConfig.saveConfig();
                    LANExtenderMod.publisher.restartClient(config.authToken);
                });
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.lan_extender.general"));
        general.addEntry(builder.entryBuilder()
                .startStrField(
                        new TranslatableText("option.lan_extender.auth_token"),
                        config.authToken)
                .setDefaultValue("")
                .setTooltip(
                        new TranslatableText("tooltip.lan_extender.auth_token"))
                .setSaveConsumer(value -> {
                    config.authToken = value;
                }).build());
        client.openScreen(builder.build());
    }
}
