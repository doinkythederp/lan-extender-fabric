package me.doinkythederp.lanextender.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.TranslatableText;

public class LANExtenderModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
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
            return builder.build();

        };
    }
}
