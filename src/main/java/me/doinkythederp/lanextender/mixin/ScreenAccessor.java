package me.doinkythederp.lanextender.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    public TextRenderer getTextRenderer();

    @Accessor
    public int getWidth();

    @Accessor
    public int getHeight();

    @Accessor
    List<AbstractButtonWidget> getButtons();

    @Accessor
    List<Element> getChildren();
}
