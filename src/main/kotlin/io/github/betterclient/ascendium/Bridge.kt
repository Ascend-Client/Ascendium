package io.github.betterclient.ascendium

import net.minecraft.client.MinecraftClient

object Bridge {
    val client: MinecraftBridge
        get() = MinecraftClient.getInstance() as MinecraftBridge
}

interface MinecraftBridge {
    val fps: Int
    val textRenderer: TextRendererBridge
    val mouse: MouseBridge
    val gameOptions: OptionsBridge
    val window: WindowBridge
    fun openScreen(screen: BridgeScreen)
}

interface WindowBridge {
    val windowHandle: Long
    val scale: Double
    val fbWidth: Int
    val fbHeight: Int
    val fbo: Int
}

interface TextRendererBridge {
    fun getWidth(text: String): Int
    fun getFontHeight(): Int
}

interface MouseBridge {
    //renamed to xPos to avoid conflict with Minecraft's Mouse class
    val xPos: Int
    val yPos: Int
}

interface OptionsBridge {
    fun addKeybinding(defaultKey: Int, name: String, category: String): KeybindHelper
}

interface KeybindHelper {
    fun onPressed(action: () -> Unit)
    fun onReleased(action: () -> Unit)
    fun onKeyChanged(action: (Int) -> Unit)
}

open class BridgeScreen {
    open var width: Int = 0
    open var height: Int = 0

    open fun render(renderer: BridgeRenderer, mouseX: Int, mouseY: Int) {}
    open fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {}
    open fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {}
    open fun mouseScrolled(mouseX: Int, mouseY: Int, scrollX: Double, scrollY: Double) {}
    open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {}
    open fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {}
    open fun charTyped(chr: Char, modifiers: Int) {}
    open fun close() {}
    open fun init() {}
    open fun shouldCloseOnEsc() = true
    open fun shouldRenderBackground() = true
}

interface BridgeRenderer {
    fun drawText(text: String, x: Int, y: Int, color: Int, scale: Float = 1.0f)
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int)
    fun getTextWidth(text: String): Int
}