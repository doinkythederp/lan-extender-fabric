package me.doinkythederp.lanextender.config;

import java.util.Optional;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;

public class LANExtenderModMenuIntegration implements ModMenuApi {
    private static Optional<String> unsavedAuthToken = Optional.empty();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            final var config = LANExtenderConfig.getInstance();
            unsavedAuthToken = Optional.of(config.authToken);
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.lan_extender.config"))
                    .setSavingRunnable(() -> {
                        config.authToken = unsavedAuthToken.get();
                        LANExtenderConfig.saveConfig();
                        unsavedAuthToken = Optional.empty();
                    });
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.lan_extender.general"));
            general.addEntry(builder.entryBuilder()
                    .startStrField(Text.translatable("option.lan_extender.auth_token"),
                            unsavedAuthToken.get())
                    .setDefaultValue("")
                    .setTooltip(Text.translatable("tooltip.lan_extender.auth_token"))
                    .setSaveConsumer(value -> {
                        unsavedAuthToken = Optional.of(value);
                    }).build());
            return builder.build();

        };
    }
}
