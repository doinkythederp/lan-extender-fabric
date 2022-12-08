package me.doinkythederp.lanextender.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(OpenToLanScreen.class)
public interface OpenToLANScreenAccessor {
    @Accessor
    public Screen getParent();
}
