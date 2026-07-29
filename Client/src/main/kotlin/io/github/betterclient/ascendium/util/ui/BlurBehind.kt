package io.github.betterclient.ascendium.util.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.ResizableTexture
import io.github.betterclient.ascendium.bridge.createResizableTexture
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.bridge.vulkanChecker
import io.github.betterclient.ascendium.ui.bridge.SkiaRenderer
import io.github.betterclient.ascendium.util.let
import io.github.betterclient.ascendium.util.mutableLazy
import org.jetbrains.skia.*

object BlurBehind {
    val texture by lazy { generateTexture() }
    var activeWidth = minecraft.window.fbWidth
    var activeHeight = minecraft.window.fbHeight

    var activeTarget by mutableLazy { texture.toSkia() }

    var activeSkiaImage: Image? by mutableStateOf(null)
    @Volatile var freed: Boolean = false

    private val CACHE_DURATION_MS
        get() = (1000 / Ascendium.settings._blurFPS.value).toLong()

    private var lastCaptureTime: Long = 0L

    fun onFrameStart(renderer: SkiaRenderer) {
        if (!vulkanChecker.isVulkan) return

        val currentTime = System.currentTimeMillis()
        val isResized = minecraft.window.fbWidth != activeWidth || minecraft.window.fbHeight != activeHeight

        if (!isResized && activeSkiaImage != null && (currentTime - lastCaptureTime) < CACHE_DURATION_MS) {
            return
        }

        lastCaptureTime = currentTime

        if (isResized) {
            activeWidth = minecraft.window.fbWidth
            activeHeight = minecraft.window.fbHeight
            texture.resize(activeWidth, activeHeight)
            activeTarget = texture.toSkia()
        }

        if (freed) {
            texture.recreate()
            activeTarget = texture.toSkia()
            freed = false
        }

        minecraft.window.copyTextureTo(texture)

        val context = renderer.adapter.myContext

        val rawImage = activeTarget.let(
            ifLeft = { rt ->
                val surface = Surface.makeFromBackendRenderTarget(
                    context = context,
                    rt = rt,
                    origin = SurfaceOrigin.BOTTOM_LEFT,
                    colorFormat = SurfaceColorFormat.RGBA_8888,
                    colorSpace = ColorSpace.sRGB,
                    surfaceProps = null,
                ) ?: return

                val snapshot = surface.makeImageSnapshot()
                surface.close()
                snapshot
            },
            ifRight = { throw UnsupportedOperationException() }
        )

        val targetHeight = 180
        val targetWidth = ((rawImage.width.toFloat() / rawImage.height) * targetHeight).toInt().coerceAtLeast(1)

        val scaledSurface = Surface.makeRenderTarget(
            context,
            false,
            ImageInfo.makeN32Premul(targetWidth, targetHeight)
        )

        val blurPaint = Paint().apply {
            val blur = Ascendium.settings._blur.value.toFloat()
            imageFilter = ImageFilter.makeBlur(blur, blur, FilterTileMode.CLAMP)
        }

        scaledSurface.canvas.drawImageRect(
            rawImage,
            Rect.makeWH(rawImage.width.toFloat(), rawImage.height.toFloat()),
            Rect.makeWH(targetWidth.toFloat(), targetHeight.toFloat()),
            blurPaint
        )
        blurPaint.close()

        val snapshot = scaledSurface.makeImageSnapshot()
        scaledSurface.close()
        rawImage.close()

        val oldImage = activeSkiaImage
        activeSkiaImage = snapshot
        oldImage?.close()
    }

    private fun generateTexture(): ResizableTexture {
        activeWidth = minecraft.window.fbWidth
        activeHeight = minecraft.window.fbHeight

        return createResizableTexture(
            width = activeWidth,
            height = activeHeight,
        )
    }
}

fun Modifier.blurBackground(
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified
): Modifier = composed {
    var windowOffset by remember { mutableStateOf(Offset.Zero) }

    val skiaImage = if (enabled) BlurBehind.activeSkiaImage else null

    this
        .onGloballyPositioned { coordinates ->
            windowOffset = coordinates.positionInWindow()
        }
        .clip(shape)
        .drawBehind {
            if (enabled && skiaImage != null && !skiaImage.isClosed) {
                val window = minecraft.window
                val baseDensity = window.scale.toFloat() / 2f

                val screenWidthComposePx = (window.fbWidth / baseDensity) * density
                val screenHeightComposePx = (window.fbHeight / baseDensity) * density

                val leftRatio = (windowOffset.x / screenWidthComposePx).coerceIn(0f, 1f)
                val topRatio = (windowOffset.y / screenHeightComposePx).coerceIn(0f, 1f)
                val widthRatio = (size.width / screenWidthComposePx).coerceIn(0f, 1f)
                val heightRatio = (size.height / screenHeightComposePx).coerceIn(0f, 1f)

                val srcX = leftRatio * skiaImage.width
                val srcY = topRatio * skiaImage.height
                val srcWidth = (widthRatio * skiaImage.width).coerceAtLeast(1f)
                val srcHeight = (heightRatio * skiaImage.height).coerceAtLeast(1f)

                drawIntoCanvas { canvas ->
                    canvas.skiaCanvas.drawImageRect(
                        image = skiaImage,
                        src = Rect.makeXYWH(
                            srcX,
                            srcY,
                            srcWidth.coerceAtMost(skiaImage.width - srcX),
                            srcHeight.coerceAtMost(skiaImage.height - srcY)
                        ),
                        dst = Rect.makeWH(size.width, size.height)
                    )
                }
            }

            if (backgroundColor != Color.Unspecified) {
                drawRect(color = backgroundColor)
            }
        }
}