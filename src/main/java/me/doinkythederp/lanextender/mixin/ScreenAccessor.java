package me.doinkythederp.lanextender.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    public TextRenderer getTextRenderer();

    @Accessor
    public int getWidth();

    // addDrawableChild appears to crash outside of development mode so the
    // alternative is to use add the element to all 3 of the following lists

    @Accessor
    List<Drawable> getDrawables();

    @Accessor
    List<Element> getChildren();

    @Accessor
    List<Selectable> getSelectables();
}
