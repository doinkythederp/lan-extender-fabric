package me.doinkythederp.lanextender;

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
            unsavedAuthToken = Optional.of(LANExtenderMod.getNgrokToken().orElse(""));
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.lan_extender.config"))
                    .setSavingRunnable(() -> {
                        LANExtenderMod.setNgrokToken(unsavedAuthToken.get());
                        unsavedAuthToken = Optional.empty();
                    });
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.lan_extender.general"));
            general.addEntry(builder.entryBuilder()
                    .startStrField(Text.translatable("option.lan_extender.auth_token"),
                            LANExtenderMod.getNgrokToken().orElse(""))
                    .setDefaultValue("todo: change to something useful")
                    .setTooltip(Text.translatable("tooltip.lan_extender.auth_token"))
                    .setSaveConsumer(value -> {
                        unsavedAuthToken = Optional.of(value);
                    }).build());
            return builder.build();

        };
    }
}
