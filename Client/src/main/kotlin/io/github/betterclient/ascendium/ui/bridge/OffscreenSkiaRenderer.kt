package io.github.betterclient.ascendium.ui.bridge

import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.Logger
import io.github.betterclient.ascendium.bridge.createRawOpenGLRenderer
import io.github.betterclient.ascendium.bridge.minecraft
import org.jetbrains.skia.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.system.exitProcess

class OffscreenSkiaRenderer : SkiaRenderAdapter {
    @Volatile var threadStarted: Boolean = false
    @Volatile var task: (Canvas) -> Unit = {}
    val tasks = CopyOnWriteArrayList<() -> Unit>()
    var glTexture by AtomicInteger(0).value0
    val renderer = createRawOpenGLRenderer()
    @Volatile var lastCall = System.currentTimeMillis()

    override fun withSkia(block: (Canvas) -> Unit) {
        if (!threadStarted) {
            Thread(::offscreenThread).apply {
                isDaemon = true
            }.start()
            threadStarted = true
        }

        task = block

        renderer.render(glTexture)
        lastCall = System.currentTimeMillis()
    }

    override fun task(block: () -> Unit) {
        tasks.add(block)
    }

    fun offscreenThread() {
        var lastW = minecraft.window.fbWidth
        var lastH = minecraft.window.fbHeight

        val windowHandle = createGL()
        val dc = DirectContext.makeGL()
        var surface = createSkiaSurface(lastW, lastH, dc)
        this.glTexture = surface.textureID
        var compatibilityMode = Ascendium.settings.uiBackend != "Offscreen" //if true, we want to use GL11.glFinish()

        while (true) {
            tasks.removeAll { it(); true }

            if (lastCall + 5000 < System.currentTimeMillis()) {
                //hasn't been called in 5 seconds, assume unused and yield
                Thread.yield()
                Thread.sleep(5)
                continue
            }

            tasks.removeAll { it(); true }

            if (lastW != minecraft.window.fbWidth || lastH != minecraft.window.fbHeight) {
                lastW = minecraft.window.fbWidth
                lastH = minecraft.window.fbHeight

                surface.delete()
                surface = createSkiaSurface(lastW, lastH, dc)

                this.glTexture = surface.textureID
                compatibilityMode = Ascendium.settings.uiBackend != "Offscreen"
            }

            tasks.removeAll { it(); true }

            val canvas = surface.surface.canvas

            dc.resetGLAll()
            canvas.clear(Color.TRANSPARENT)
            task(canvas)

            tasks.removeAll { it(); true }

            if(compatibilityMode) {
                dc.flush()
                GL11.glFinish()
            } else {
                dc.flushAndSubmit(surface.surface, syncCpu = true)
            }

            this.glTexture = surface.textureID
        }
    }

    private fun createSkiaSurface(width: Int, height: Int, dc: DirectContext): SkiaSurface {
        val (textureID, fboID) = createTextures(width, height)
        val target = BackendRenderTarget.makeGL(
            width, height,
            0, 8,
            fboID, FramebufferFormat.GR_GL_RGBA8
        )

        val surface = Surface.makeFromBackendRenderTarget(
            dc,
            target,
            SurfaceOrigin.TOP_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB
        )!!

        return SkiaSurface(
            textureID, fboID, target, surface
        )
    }

    fun createGL(): Long {
        GLFWErrorCallback.create { i, s -> Logger.error("GLFW Error: ${GLFWErrorCallback.getDescription(s)} $i") }.set()

        //offscreen
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        //ver 4.6
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)

        //compat
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)

        //share textures and stuff with the main handle
        val handle = glfwCreateWindow(1, 1, "Offscreen context", 0L, minecraft.window.windowHandle)
        if (handle == 0L) {
            glfwTerminate()
            Logger.error("Failed to create offscreen window")
            exitProcess(0)
        }

        glfwMakeContextCurrent(handle)
        GL.createCapabilities()

        return handle
    }

    fun createTextures(width: Int, height: Int): Pair<Int, Int> {
        val textureID = GL11.glGenTextures()
        val fboID = GL30.glGenFramebuffers()

        //bind tex to fbo
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
            width, height, 0,
            GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0L
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID)
        GL30.glFramebufferTexture2D(
            GL30.GL_FRAMEBUFFER,
            GL30.GL_COLOR_ATTACHMENT0,
            GL11.GL_TEXTURE_2D,
            textureID,
            0
        )

        //unbind
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)

        return textureID to fboID
    }
}

//fuck you
val AtomicInteger.value0 get() = object : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>) = this@value0.get()
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) = this@value0.set(value)
}

data class SkiaSurface(
    val textureID: Int,
    val fboID: Int,
    val renderTarget: BackendRenderTarget,
    val surface: Surface
) {
    fun delete() {
        GL11.glDeleteTextures(textureID)
        GL30.glDeleteFramebuffers(fboID)
        renderTarget.close()
        surface.close()
    }
}