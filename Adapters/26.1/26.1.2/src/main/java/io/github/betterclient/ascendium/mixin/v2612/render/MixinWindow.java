package io.github.betterclient.ascendium.mixin.v2612.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.github.betterclient.ascendium.bridge.BridgeKt;
import io.github.betterclient.ascendium.bridge.ResizableTexture;
import io.github.betterclient.ascendium.bridge.WindowBridge;
import io.github.betterclient.ascendium.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.OptionalInt;

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

    @Override
    public void copyTextureTo(@NotNull ResizableTexture texture) {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView srcView = mainTarget.getColorTextureView();
        GpuTextureView dstView = (GpuTextureView) texture.getTextureView();

        if (srcView == null || dstView == null) return;

        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "Copy & Scale ResizableTexture",
                dstView,
                OptionalInt.empty()
        )) {
            renderPass.setPipeline(RenderPipelines.TRACY_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);

            renderPass.bindTexture(
                    "InSampler",
                    srcView,
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
            );

            renderPass.draw(0, 3);
        }
    }
}