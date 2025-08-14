package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.WindowBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
public class MixinWindow implements WindowBridge {
    @Shadow private int framebufferWidth;

    @Shadow private int framebufferHeight;

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
}