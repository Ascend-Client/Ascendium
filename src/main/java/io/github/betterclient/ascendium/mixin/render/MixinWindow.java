package io.github.betterclient.ascendium.mixin.render;

import io.github.betterclient.ascendium.WindowBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
public class MixinWindow implements WindowBridge {
    @Shadow private int framebufferWidth;

    @Shadow private int framebufferHeight;

    @Shadow private double scaleFactor;

    @Shadow @Final private long handle;

    @Override
    public int getFbWidth() {
        return this.framebufferWidth;
    }

    @Override
    public int getFbHeight() {
        return this.framebufferHeight;
    }

    @Override
    public int getFbo() {
        return MinecraftClient.getInstance().getFramebuffer().fbo;
    }

    @Override
    public @NotNull double getScale() {
        return this.scaleFactor;
    }

    @Override
    public long getWindowHandle() {
        return this.handle;
    }
}