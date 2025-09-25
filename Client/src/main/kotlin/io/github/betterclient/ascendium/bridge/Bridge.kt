package io.github.betterclient.ascendium.bridge

import org.jetbrains.skia.Rect
import java.awt.image.BufferedImage
import kotlin.math.sqrt

//globally accessible client!
inline val minecraft: MinecraftBridge
    get() {
        return try {
            Class.forName("net.minecraft.class_310").getMethod("method_1551").invoke(null) as MinecraftBridge
        } catch (_: Exception) {
            Class.forName("net.minecraft.client.MinecraftClient").getMethod("getInstance").invoke(null) as MinecraftBridge
        }
    }

fun createOpenGLTexture(): TextureBridge {
    return BridgeAdapterManager.useBridgeUtil({ it.openglTextureAdapter }) as TextureBridge
}

inline val requireChromium: Boolean
    get() = (BridgeAdapterManager.useBridgeUtil({ it.chromium }) as RequiresChromium).does

interface MinecraftBridge {
    val screen: MCScreen?
    val isWorldNull: Boolean
    val server: String
    val ping: Int
    val fps: Int
    val mouse: MouseBridge
    val gameOptions: OptionsBridge
    val window: WindowBridge
    val player: PlayerBridge

    fun openScreen(screen: BridgeScreen)
    fun setScreen(mcScreen: MCScreen)
    fun raycast(entityBridge: EntityBridge, camera: Pos3D, possibleHits: Pos3D, box: BoundingBox, id: Int, d3: Double): RaycastResultBridge?
    fun loadResource(identifier: IdentifierBridge): ByteArray?
}

class RaycastResultBridge(val pos: Pos3D, val entity: EntityBridge?)
enum class MCScreen {
    SELECT_WORLD_SCREEN,
    MULTIPLAYER_SCREEN,
    REALMS_MAIN_SCREEN,
    OPTIONS_SCREEN,
    CHAT_SCREEN,
    OTHER_SCREEN
}

interface EntityBridge {
    fun getPos(): Pos3D
    fun getBox(): BoundingBox
    fun getID(): Int
    fun getCameraPosVec(i: Int): Pos3D
    fun getRotationVec(i: Int): Pos3D
    val name: String
}

interface PlayerBridge : EntityBridge {
    fun getArmor(i: Int): ItemStackBridge
    fun getMainHandItem(): ItemStackBridge
    val pYaw: Float
    val pPitch: Float
    val biome: String
    val facing: String
}

interface ItemStackBridge {
    val itemCount: Int
    val itemIdentifier: IdentifierBridge
    val durability: Float //0..1
}

data class IdentifierBridge(val namespace: String, val path: String)

class Pos3D(val x: Double, val y: Double, val z: Double) {
    fun add(x: Double, y: Double, z: Double) = Pos3D(x + this.x, y + this.y, z + this.z)
    fun distanceTo(other: Pos3D): Double {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun multiply(d: Double) = Pos3D(x * d, y * d, z * d)
}

class BoundingBox(val start: Pos3D, val end: Pos3D) {
    fun stretch(by: Pos3D): BoundingBox {
        val newStart = Pos3D(
            start.x + minOf(by.x, 0.0),
            start.y + minOf(by.y, 0.0),
            start.z + minOf(by.z, 0.0)
        )
        val newEnd = Pos3D(
            end.x + maxOf(by.x, 0.0),
            end.y + maxOf(by.y, 0.0),
            end.z + maxOf(by.z, 0.0)
        )
        return BoundingBox(newStart, newEnd)
    }

    fun expand(x: Double, y: Double, z: Double): BoundingBox {
        return BoundingBox(
            Pos3D(start.x - x, start.y - y, start.z - z),
            Pos3D(end.x + x, end.y + y, end.z + z)
        )
    }
}

interface WindowBridge {
    val windowHandle: Long
    val scale: Double
    val fbWidth: Int
    val fbHeight: Int
    val fbo: Int
}

interface MouseBridge {
    //renamed to xPos to avoid conflict with Minecraft's Mouse class
    val xPos: Int
    val yPos: Int
}

interface OptionsBridge {
    fun addKeybinding(defaultKey: Int, name: String, category: String): KeybindHelper

    val keyForward: KeybindingBridge
    val keyBackward: KeybindingBridge
    val keyLeft: KeybindingBridge
    val keyRight: KeybindingBridge
    val keyAttack: KeybindingBridge
    val keyUse: KeybindingBridge
    val keyJump: KeybindingBridge
}

interface KeybindingBridge {
    val buttonPressed: Boolean
    val getBoundKey: String
}

interface KeybindHelper {
    fun onPressed(action: () -> Unit)
    fun onReleased(action: () -> Unit)
    fun onKeyChanged(action: (Int) -> Unit)
}

open class BridgeScreen {
    open var width: Int = 0
    open var height: Int = 0
    open var renderUtil: RenderUtilBridge = NullRenderUtilBridge

    open fun render(mouseX: Int, mouseY: Int) {}
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

interface TextBridge {
    val style: TextStyleBridge
    val text: String
    val bridgedSiblings: List<TextBridge> //crash
}

interface TextStyleBridge {
    val color: Int
    val bold: Boolean
    val italic: Boolean
    val clickEvent: ClickEventBridge
}

interface ClickEventBridge {
    val action: ClickEventActionBridge?
    val bridgeValue: String
}

enum class ClickEventActionBridge {
    OPEN_URL,
    OPEN_FILE,
    RUN_COMMAND,
    SUGGEST_COMMAND,
    CHANGE_PAGE,
    COPY_TO_CLIPBOARD
}

interface RenderUtilBridge {
    fun text(str: String, x: Int, y: Int, color: Int)
    fun rect(rect: Rect, color: Int)
}

object NullRenderUtilBridge : RenderUtilBridge {
    override fun text(str: String, x: Int, y: Int, color: Int) {}
    override fun rect(rect: Rect, color: Int) {}
}

interface TextureBridge {
    fun update(image: BufferedImage)
    fun blit()
}

interface RequiresChromium { val does: Boolean }