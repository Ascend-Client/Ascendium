package io.github.betterclient.ascendium.mixin.v1218;

import com.mojang.blaze3d.textures.GpuTextureView;
import io.github.betterclient.ascendium.bridge.WindowBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
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
        return this.framebufferWidth;
    }

    @Override
    public int getFbHeight() {
        return this.framebufferHeight;
    }

    @Override
    public int getFbo() {
        return ((GlTexture) MinecraftClient.getInstance().getFramebuffer().getColorAttachment()).getGlId();
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