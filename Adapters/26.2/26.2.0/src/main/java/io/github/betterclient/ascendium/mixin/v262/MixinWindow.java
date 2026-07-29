package io.github.betterclient.ascendium.mixin.v262;

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
import org.lwjgl.vulkan.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

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
        return ((GlTexture) Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTexture()).glId();
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
        if (texture.getWidth() != getFbWidth() || texture.getHeight() != getFbHeight()) {
            texture.resize(getFbWidth(), getFbHeight());
        }

        if (BridgeKt.getVulkanChecker().isVulkan()) {
            RenderTarget mainTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
            GpuTexture srcTexture = mainTarget.getColorTexture();
            GpuTexture dstTexture = ((GpuTextureView) texture.getTextureView()).texture();
            if (srcTexture == null || dstTexture == null) return;
            RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(
                    srcTexture,
                    dstTexture,
                    0,
                    0, 0,
                    0, 0,
                    mainTarget.width,
                    mainTarget.height
            );
        } else {
            if (texture.getId() instanceof Either.Left<?> left) {
                int glTextureId = (Integer) left.getValue();
                int drawFbo = GlStateManager.glGenFramebuffers();
                int currentFbo = getFbo();

                GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, currentFbo);
                GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFbo);
                GlStateManager._glFramebufferTexture2D(
                        GL30.GL_DRAW_FRAMEBUFFER,
                        GL30.GL_COLOR_ATTACHMENT0,
                        GL11.GL_TEXTURE_2D,
                        glTextureId,
                        0
                );

                GlStateManager._glBlitFrameBuffer(
                        0, 0, this.framebufferWidth, this.framebufferHeight,
                        0, 0, texture.getWidth(), texture.getHeight(),
                        GL11.GL_COLOR_BUFFER_BIT,
                        GL11.GL_LINEAR
                );

                GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
                GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                GlStateManager._glDeleteFramebuffers(drawFbo);
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFbo);
            }
        }
    }
}