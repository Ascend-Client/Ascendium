package io.github.betterclient.ascendium.mixin.v1214.render;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.betterclient.ascendium.bridge.BridgeKt;
import io.github.betterclient.ascendium.bridge.ResizableTexture;
import io.github.betterclient.ascendium.bridge.WindowBridge;
import io.github.betterclient.ascendium.util.Either;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.vulkanmod.vulkan.texture.ImageUtil;
import net.vulkanmod.vulkan.texture.VulkanImage;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
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

    @Override
    public void copyTextureTo(@NotNull ResizableTexture texture) {
        if (BridgeKt.getVulkanChecker().isVulkan()) {
            if (texture.getId() instanceof Either.Right<?> right) {
                long vulkanImageId = (Long) right.getValue();
                VulkanImage dstImage = new VulkanImage(
                        "CopyTarget",
                        vulkanImageId,
                        VulkanImage.DefaultFormat,
                        1,
                        texture.getWidth(),
                        texture.getHeight(),
                        4,
                        7,
                        0L
                );
                ImageUtil.blitFramebuffer(
                        dstImage,
                        0, 0, this.framebufferWidth, this.framebufferHeight,
                        0, 0, texture.getWidth(), texture.getHeight()
                );
            }
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

                GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                GlStateManager._glDeleteFramebuffers(drawFbo);
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFbo);
            }
        }
    }
}