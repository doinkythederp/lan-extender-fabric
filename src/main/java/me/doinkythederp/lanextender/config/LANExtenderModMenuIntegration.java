package me.doinkythederp.lanextender.config;

import com.github.alexdlaird.ngrok.protocol.Region;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.string.StringController;
import me.doinkythederp.lanextender.LANExtenderMod;
import net.minecraft.client.gui.screen.Screen;
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
            return YetAnotherConfigLib.createBuilder()
                    .title(Text.translatable("title.lan_extender.config"))
                    .save(() -> {
                        LANExtenderConfig.saveConfig();
                    })
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable(
                                    "title.lan_extender.config"))
                            .option(Option.createBuilder(String.class)
                                    .name(Text.translatable(
                                            "option.lan_extender.auth_token"))
                                    .tooltip(Text.translatable(
                                            "tooltip.lan_extender.auth_token"))
                                    .binding(
                                            config.authToken,
                                            () -> config.authToken,
                                            newValue -> config.authToken = newValue)
                                    .controller(StringController::new)
                                    .build())
                            .build())
                    .build()
                    .generateScreen(parent);
            // ConfigBuilder builder = ConfigBuilder.create()
            // .setParentScreen(parent)
            // .setTitle(Text.translatable("title.lan_extender.config"))
            // .setSavingRunnable(() -> {
            // LANExtenderConfig.saveConfig();
            // LANExtenderMod.publisher.restartClient(config.authToken, config.region);
            // });

            // // #region General
            // ConfigCategory general =
            // builder.getOrCreateCategory(Text.translatable("category.lan_extender.general"));

            // general.addEntry(builder.entryBuilder()
            // .startStrField(Text.translatable("option.lan_extender.auth_token"),
            // config.authToken)
            // .setDefaultValue("")
            // .setTooltip(Text.translatable("tooltip.lan_extender.auth_token"))
            // .setSaveConsumer(value -> {
            // config.authToken = value;
            // }).build());

            // general.addEntry(builder.entryBuilder()
            // .startEnumSelector(Text.translatable("option.lan_extender.region"),
            // Region.class, config.region)
            // .setDefaultValue(Region.US)
            // .setEnumNameProvider(
            // LANExtenderModMenuIntegration::regionToText)
            // .setTooltip(Text.translatable("tooltip.lan_extender.region"))
            // .setSaveConsumer(value -> {
            // config.region = value;
            // }).build());

            // general.addEntry(builder.entryBuilder()
            // .startBooleanToggle(Text.translatable("option.lan_extender.copy_on_publish"),
            // config.copyAddressOnPublish)
            // .setDefaultValue(true)
            // .setTooltip(Text.translatable("tooltip.lan_extender.copy_on_publish"))
            // .setSaveConsumer(value -> {
            // config.copyAddressOnPublish = value;
            // }).build());

            // // #endregion

            // return builder.build();

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
