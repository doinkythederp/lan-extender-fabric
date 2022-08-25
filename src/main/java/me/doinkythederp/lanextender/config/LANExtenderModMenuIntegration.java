package me.doinkythederp.lanextender.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;

public class LANExtenderModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            final var config = LANExtenderConfig.getInstance();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.lan_extender.config"))
                    .setSavingRunnable(() -> {
                        LANExtenderConfig.saveConfig();
                        LANExtenderMod.publisher.restartClient(config.authToken);
                    });
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.lan_extender.general"));
            general.addEntry(builder.entryBuilder()
                    .startStrField(Text.translatable("option.lan_extender.auth_token"),
                            config.authToken)
                    .setDefaultValue("")
                    .setTooltip(Text.translatable("tooltip.lan_extender.auth_token"))
                    .setSaveConsumer(value -> {
                        config.authToken = value;
                    }).build());
            return builder.build();

        };
    }
}
