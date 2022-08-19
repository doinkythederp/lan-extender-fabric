package me.doinkythederp.lanextender.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    public TextRenderer getTextRenderer();

    @Accessor
    public int getWidth();

    @Invoker
    public <T extends Element & Drawable> T invokeAddDrawableChild(T drawableElement);
}
