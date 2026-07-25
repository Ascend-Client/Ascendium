package io.github.betterclient.ascendium.mixin.v2612.render;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.platform.Window;
import io.github.betterclient.ascendium.bridge.WindowBridge;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
public class MixinWindow implements WindowBridge {
    @Shadow private int framebufferWidth;

    @Shadow private int framebufferHeight;

    @Shadow private int guiScale;

    @Shadow @Final private long handle;

    @Override
    public int getFbWidth() {
        return (this.framebufferWidth);
    }

    @Override
    public int getFbHeight() {
        return (this.framebufferHeight);
    }

    @Override
    public int getFbo() {
        return ((GlTexture) Minecraft.getInstance().getMainRenderTarget().getColorTexture()).glId();
    }

    @Override
    public @NotNull double getScale() {
        return this.guiScale;
    }

    @Override
    public long getWindowHandle() {
        return this.handle;
    }
}