package me.doinkythederp.ngrokr;

import java.util.Optional;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;

public class NgrokrModMenuIntegration implements ModMenuApi {
    private static Optional<String> unsavedAuthToken = Optional.empty();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            unsavedAuthToken = Optional.of(NgrokrMod.getNgrokToken().orElse(""));
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.ngrokr.config"))
                    .setSavingRunnable(() -> {
                        NgrokrMod.setNgrokToken(unsavedAuthToken.get());
                        unsavedAuthToken = Optional.empty();
                    });
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.ngrokr.general"));
            general.addEntry(builder.entryBuilder()
                    .startStrField(Text.translatable("option.ngrokr.auth_token"), NgrokrMod.getNgrokToken().orElse(""))
                    .setDefaultValue("todo: change to something useful")
                    .setTooltip(Text.translatable("tooltip.ngrokr.auth_token"))
                    .setSaveConsumer(value -> {
                        unsavedAuthToken = Optional.of(value);
                    }).build());
            return builder.build();

        };
    }
}
