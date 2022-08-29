package me.doinkythederp.lanextender.config;

import com.github.alexdlaird.ngrok.protocol.Region;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class LANExtenderModMenuIntegration implements ModMenuApi {
    private static final Text JAPAN = new TranslatableText("region.lan_extender.japan");
    private static final Text INDIA = new TranslatableText("region.lan_extender.india");
    private static final Text AUSTRALIA = new TranslatableText("region.lan_extender.australia");
    private static final Text ASIA_PACIFIC = new TranslatableText("region.lan_extender.asia_pacific");
    private static final Text EUROPE = new TranslatableText("region.lan_extender.europe");
    private static final Text SOUTH_AMERICA = new TranslatableText("region.lan_extender.south_america");
    private static final Text NORTH_AMERICA = new TranslatableText("region.lan_extender.north_america");

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            final var config = LANExtenderConfig.getInstance();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("title.lan_extender.config"))
                    .setSavingRunnable(() -> {
                        LANExtenderConfig.saveConfig();
                        LANExtenderMod.publisher.restartClient(config.authToken, config.region);
                    });

            // #region General
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

            general.addEntry(builder.entryBuilder()
                    .startEnumSelector(
                            new TranslatableText("option.lan_extender.region"),
                            Region.class, config.region)
                    .setDefaultValue(Region.US)
                    .setEnumNameProvider(
                            LANExtenderModMenuIntegration::regionToText)
                    .setTooltip(
                            new TranslatableText("tooltip.lan_extender.region"))
                    .setSaveConsumer(value -> {
                        config.region = value;
                    }).build());

            general.addEntry(builder.entryBuilder()
                    .startBooleanToggle(
                            new TranslatableText("option.lan_extender.copy_on_publish"),
                            config.copyAddressOnPublish)
                    .setDefaultValue(true)
                    .setTooltip(
                            new TranslatableText("tooltip.lan_extender.copy_on_publish"))
                    .setSaveConsumer(value -> {
                        config.copyAddressOnPublish = value;
                    }).build());

            // #endregion

            return builder.build();

        };
    }

    private static Text regionToText(Enum<Region> region) {
        switch ((Region) region) {
            case US:
                return NORTH_AMERICA;
            case SA:
                return SOUTH_AMERICA;
            case EU:
                return EUROPE;
            case AU:
                return AUSTRALIA;
            case AP:
                return ASIA_PACIFIC;
            case IN:
                return INDIA;
            case JP:
                return JAPAN;
            default:
                throw new AssertionError("Invalid region " + region);
        }
    }
}
