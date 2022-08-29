package me.doinkythederp.lanextender.config;

import org.jetbrains.annotations.Nullable;

import com.github.alexdlaird.ngrok.protocol.Region;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.doinkythederp.lanextender.LANExtenderMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;

public class LANExtenderModMenuIntegration implements ModMenuApi {
    private static final Text JAPAN = Text.translatable("region.lan_extender.japan");
    private static final Text INDIA = Text.translatable("region.lan_extender.india");
    private static final Text AUSTRALIA = Text.translatable("region.lan_extender.australia");
    private static final Text ASIA_PACIFIC = Text.translatable("region.lan_extender.asia_pacific");
    private static final Text EUROPE = Text.translatable("region.lan_extender.europe");
    private static final Text SOUTH_AMERICA = Text.translatable("region.lan_extender.south_america");
    private static final Text NORTH_AMERICA = Text.translatable("region.lan_extender.north_america");

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            final var config = LANExtenderConfig.getInstance();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.lan_extender.config"))
                    .setSavingRunnable(() -> {
                        LANExtenderConfig.saveConfig();
                        LANExtenderMod.publisher.restartClient(config.authToken, config.region);
                    });

            // #region General
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.lan_extender.general"));

            general.addEntry(builder.entryBuilder()
                    .startStrField(Text.translatable("option.lan_extender.auth_token"),
                            config.authToken)
                    .setDefaultValue("")
                    .setTooltip(Text.translatable("tooltip.lan_extender.auth_token"))
                    .setSaveConsumer(value -> {
                        config.authToken = value;
                    }).build());

            general.addEntry(builder.entryBuilder()
                    .startEnumSelector(Text.translatable("option.lan_extender.region"),
                            Region.class, config.region)
                    .setDefaultValue(Region.US)
                    .setEnumNameProvider(
                            LANExtenderModMenuIntegration::regionToText)
                    .setTooltip(Text.translatable("tooltip.lan_extender.region"))
                    .setSaveConsumer(value -> {
                        config.region = value;
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

    @Nullable
    private static Region stringToRegion(String region) {
        if (region.equals("us")) {
            return Region.US;
        } else if (region.equals(SOUTH_AMERICA.getString())) {
            return Region.SA;
        } else if (region.equals(EUROPE.getString())) {
            return Region.EU;
        } else if (region.equals(AUSTRALIA.getString())) {
            return Region.AU;
        } else if (region.equals(ASIA_PACIFIC.getString())) {
            return Region.AP;
        } else if (region.equals(INDIA.getString())) {
            return Region.IN;
        } else if (region.equals(JAPAN.getString())) {
            return Region.JP;
        } else {
            return null;
        }
    }
}
