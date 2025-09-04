package io.github.betterclient.ascendium.mixin.v1218;

import io.github.betterclient.ascendium.bridge.WindowBridge;
import io.github.betterclient.ascendium.util.V1218SkiaRenderAdapterKt;
import io.github.betterclient.ascendium.util.V1218SkiaRenderAdapterObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
public class MixinWindow implements WindowBridge {
    @Shadow private int framebufferWidth;

    @Shadow private int framebufferHeight;

    @Shadow private int scaleFactor;

    @Shadow @Final private long handle;

    @Override
    public int getFbWidth() {
        return (int) (this.framebufferWidth / V1218SkiaRenderAdapterObject.Companion.getUI_SCALE());
    }

    @Override
    public int getFbHeight() {
        return (int) (this.framebufferHeight / V1218SkiaRenderAdapterObject.Companion.getUI_SCALE());
    }

    @Override
    public int getFbo() {
        return ((GlTexture) MinecraftClient.getInstance().getFramebuffer().getColorAttachment()).getGlId();
    }

    @Override
    public @NotNull double getScale() {
        return this.scaleFactor / V1218SkiaRenderAdapterObject.Companion.getUI_SCALE();
    }

    @Override
    public long getWindowHandle() {
        return this.handle;
    }
}