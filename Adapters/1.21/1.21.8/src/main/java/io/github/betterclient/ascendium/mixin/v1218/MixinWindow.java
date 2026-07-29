package io.github.betterclient.ascendium.mixin.v1218;

import com.mojang.blaze3d.opengl.GlStateManager;
import io.github.betterclient.ascendium.bridge.BridgeKt;
import io.github.betterclient.ascendium.bridge.ResizableTexture;
import io.github.betterclient.ascendium.bridge.WindowBridge;
import io.github.betterclient.ascendium.util.Either;
import io.github.betterclient.ascendium.util.V1218SkiaRenderAdapterObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    @Override
    public void copyTextureTo(@NotNull ResizableTexture texture) {
        if (BridgeKt.getVulkanChecker().isVulkan()) {
            if (texture.getId() instanceof Either.Right<?> right) {
                try {
                    Class<?> vulkanImageClass = Class.forName("net.vulkanmod.vulkan.texture.VulkanImage");
                    Class<?> imageUtilClass = Class.forName("net.vulkanmod.vulkan.texture.ImageUtil");

                    Field defaultFormatField = vulkanImageClass.getDeclaredField("DefaultFormat");
                    defaultFormatField.setAccessible(true);
                    Object defaultFormat = defaultFormatField.get(null);

                    Constructor<?> vulkanImageConstructor = vulkanImageClass.getDeclaredConstructor(
                            String.class,
                            long.class,
                            defaultFormatField.getType(),
                            int.class,
                            int.class,
                            int.class,
                            int.class,
                            int.class,
                            long.class
                    );

                    Object dstImage = vulkanImageConstructor.newInstance(
                            "CopyTarget",
                            right.getValue(),
                            defaultFormat,
                            1,
                            texture.getWidth(),
                            texture.getHeight(),
                            4,
                            7,
                            0L
                    );

                    Method blitFramebufferMethod = imageUtilClass.getDeclaredMethod(
                            "blitFramebuffer",
                            vulkanImageClass,
                            int.class, int.class, int.class, int.class,
                            int.class, int.class, int.class, int.class
                    );
                    blitFramebufferMethod.setAccessible(true);

                    blitFramebufferMethod.invoke(
                            null,
                            dstImage,
                            0, 0, getFbWidth(), getFbHeight(),
                            0, 0, texture.getWidth(), texture.getHeight()
                    );

                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            if (texture.getId() instanceof Either.Left<?> left) {
                int glTextureId = (Integer) left.getValue();
                int srcTextureId = getFbo();

                int readFbo = GlStateManager.glGenFramebuffers();
                int drawFbo = GlStateManager.glGenFramebuffers();

                GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFbo);
                GlStateManager._glFramebufferTexture2D(
                        GL30.GL_READ_FRAMEBUFFER,
                        GL30.GL_COLOR_ATTACHMENT0,
                        GL11.GL_TEXTURE_2D,
                        srcTextureId,
                        0
                );

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
                GlStateManager._glDeleteFramebuffers(readFbo);
                GlStateManager._glDeleteFramebuffers(drawFbo);
            }
        }
    }
}