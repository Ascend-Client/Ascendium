package io.github.betterclient.ascendium.bridge

import net.fabricmc.loader.api.FabricLoader

object BridgeAdapterManager {
    fun shouldApply(className: String): Boolean {
        val adapter =
            adapters[FabricLoader.getInstance().getModContainer("minecraft").get().metadata.version.friendlyString]
        if (adapter == null) throw UnsupportedOperationException("This version of minecraft is not supported.")

        val adapterVersion = mixinMapping.entries
            .firstOrNull { (mixinName, _) -> className.contains(mixinName) }
            ?.value(adapter)

        if (adapterVersion == null && adapter.applyElse.any { className.endsWith(it) }) {
            return true //apply or else
        }

        return adapterVersion?.let { className.contains("v$it") } ?: false
    }

    fun useBridgeUtil(adapterName: (adapter: BridgeAdapter) -> String, vararg parameters: Any?): Any {
        val adapter =
            adapters[FabricLoader.getInstance().getModContainer("minecraft").get().metadata.version.friendlyString]
        if (adapter == null) throw UnsupportedOperationException("This version of minecraft is not supported.")
        val adapterName1 = adapterName(adapter)
        return Class.forName("io.github.betterclient.ascendium.util.$adapterName1").constructors[0].newInstance(
            *parameters
        )
    }
}

val `1_21_4` = BridgeAdapter(
    keybindingBridgeAdapter = "V1214KeybindingHelper",
    screenBridgeAdapter = "Post120BridgedScreen",
    skiaRenderAdapter = "V1214SkiaRenderAdapter",
    openglTextureAdapter = "V1214OpenGLTextureAdapter",
    offscreen = "DontRequireOffscreen",

    minecraftClientAdapter = "1214",
    entityAdapter = "1214",
    itemStackAdapter = "1214",
    playerEntityAdapter = "1214",
    keybindingAdapter = "1214",
    optionsAdapter = "1214",
    simpleResourceReloadAdapter = "1214",
    soundEngineAdapter = "1214",
    spriteAtlasTextureAdapter = "1214",
    unihexFontAdapter = "1214",
    inGameHudAdapter = "1214",
    screenAdapter = "1214",
    splashOverlayAdapter = "1214",
    titleScreenAdapter = "1214",
    windowAdapter = "1214",
    chatHudAdapter = "1214",
    clickEventAdapter = "1214",
    styleAdapter = "1214",
    textAdapter = "1214",
    mainAdapter = "1214",
    mouseAdapter = "1214"
)

val `1_21_8` = `1_21_4`.copy(
    skiaRenderAdapter = "V1218SkiaRenderAdapter",
    openglTextureAdapter = "V1218OpenGLTextureAdapter",
    offscreen = "RequireOffscreen",

    clickEventAdapter = "1218",
    splashOverlayAdapter = "1218",
    windowAdapter = "1218",
    simpleResourceReloadAdapter = "1218",
    playerEntityAdapter = "1218",
    screenAdapter = "1218",
    mouseAdapter = "1218",
    minecraftClientAdapter = "1218",
    applyElse = arrayOf(
        "1218.MixinCubeMapRenderer",
        "1218.MixinDrawContext"
    )
)

val `1_21_1` = `1_21_4`.copy(
    splashOverlayAdapter = "1211"
)

val `1_20_6` = `1_21_1`.copy(
    inGameHudAdapter = "1206"
)

val `1_20_4` = `1_20_6`.copy(
    chatHudAdapter = "1204"
)

val `1_20_1` = `1_20_4`.copy(
    screenAdapter = "V1201BridgedScreen"
)

val `1_19_4` = `1_20_1`.copy(
    splashOverlayAdapter = "1194",
    inGameHudAdapter = "1194",
    screenBridgeAdapter = "Pre120BridgedScreen"
)

val `1_19_2` = `1_19_4`.copy(
    playerEntityAdapter = "1192",
    itemStackAdapter = "1192",
    spriteAtlasTextureAdapter = "1192",
    simpleResourceReloadAdapter = "disable"
)

data class BridgeAdapter(
    val keybindingBridgeAdapter: String,
    val screenBridgeAdapter: String,
    val skiaRenderAdapter: String,
    val openglTextureAdapter: String,
    val offscreen: String,

    val minecraftClientAdapter: String,
    val entityAdapter: String,
    val itemStackAdapter: String,
    val playerEntityAdapter: String,
    val keybindingAdapter: String,
    val optionsAdapter: String,
    val simpleResourceReloadAdapter: String,
    val soundEngineAdapter: String,
    val spriteAtlasTextureAdapter: String,
    val unihexFontAdapter: String,
    val inGameHudAdapter: String,
    val screenAdapter: String,
    val splashOverlayAdapter: String,
    val titleScreenAdapter: String,
    val windowAdapter: String,
    val chatHudAdapter: String,
    val clickEventAdapter: String,
    val styleAdapter: String,
    val textAdapter: String,
    val mainAdapter: String,
    val mouseAdapter: String,
    val applyElse: Array<String> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BridgeAdapter

        if (keybindingBridgeAdapter != other.keybindingBridgeAdapter) return false
        if (screenBridgeAdapter != other.screenBridgeAdapter) return false
        if (skiaRenderAdapter != other.skiaRenderAdapter) return false
        if (openglTextureAdapter != other.openglTextureAdapter) return false
        if (offscreen != other.offscreen) return false
        if (minecraftClientAdapter != other.minecraftClientAdapter) return false
        if (entityAdapter != other.entityAdapter) return false
        if (itemStackAdapter != other.itemStackAdapter) return false
        if (playerEntityAdapter != other.playerEntityAdapter) return false
        if (keybindingAdapter != other.keybindingAdapter) return false
        if (optionsAdapter != other.optionsAdapter) return false
        if (simpleResourceReloadAdapter != other.simpleResourceReloadAdapter) return false
        if (soundEngineAdapter != other.soundEngineAdapter) return false
        if (spriteAtlasTextureAdapter != other.spriteAtlasTextureAdapter) return false
        if (unihexFontAdapter != other.unihexFontAdapter) return false
        if (inGameHudAdapter != other.inGameHudAdapter) return false
        if (screenAdapter != other.screenAdapter) return false
        if (splashOverlayAdapter != other.splashOverlayAdapter) return false
        if (titleScreenAdapter != other.titleScreenAdapter) return false
        if (windowAdapter != other.windowAdapter) return false
        if (chatHudAdapter != other.chatHudAdapter) return false
        if (clickEventAdapter != other.clickEventAdapter) return false
        if (styleAdapter != other.styleAdapter) return false
        if (textAdapter != other.textAdapter) return false
        if (mainAdapter != other.mainAdapter) return false
        if (mouseAdapter != other.mouseAdapter) return false
        if (!applyElse.contentEquals(other.applyElse)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keybindingBridgeAdapter.hashCode()
        result = 31 * result + screenBridgeAdapter.hashCode()
        result = 31 * result + skiaRenderAdapter.hashCode()
        result = 31 * result + openglTextureAdapter.hashCode()
        result = 31 * result + offscreen.hashCode()
        result = 31 * result + minecraftClientAdapter.hashCode()
        result = 31 * result + entityAdapter.hashCode()
        result = 31 * result + itemStackAdapter.hashCode()
        result = 31 * result + playerEntityAdapter.hashCode()
        result = 31 * result + keybindingAdapter.hashCode()
        result = 31 * result + optionsAdapter.hashCode()
        result = 31 * result + simpleResourceReloadAdapter.hashCode()
        result = 31 * result + soundEngineAdapter.hashCode()
        result = 31 * result + spriteAtlasTextureAdapter.hashCode()
        result = 31 * result + unihexFontAdapter.hashCode()
        result = 31 * result + inGameHudAdapter.hashCode()
        result = 31 * result + screenAdapter.hashCode()
        result = 31 * result + splashOverlayAdapter.hashCode()
        result = 31 * result + titleScreenAdapter.hashCode()
        result = 31 * result + windowAdapter.hashCode()
        result = 31 * result + chatHudAdapter.hashCode()
        result = 31 * result + clickEventAdapter.hashCode()
        result = 31 * result + styleAdapter.hashCode()
        result = 31 * result + textAdapter.hashCode()
        result = 31 * result + mainAdapter.hashCode()
        result = 31 * result + mouseAdapter.hashCode()
        result = 31 * result + applyElse.contentHashCode()
        return result
    }
}

val adapters = mutableMapOf(
    "1.19.2" to `1_19_2`,
    "1.19.4" to `1_19_4`,
    "1.20.1" to `1_20_1`,
    "1.20.4" to `1_20_4`,
    "1.20.6" to `1_20_6`,
    "1.21.1" to `1_21_1`,
    "1.21.4" to `1_21_4`,
    "1.21.8" to `1_21_8`
)

val mixinMapping = mapOf<String, (adapter: BridgeAdapter) -> String>(
    "MixinMinecraftClient" to { adapter -> adapter.minecraftClientAdapter },
    "MixinEntity" to { adapter -> adapter.entityAdapter },
    "MixinItemStack" to { adapter -> adapter.itemStackAdapter },
    "MixinPlayerEntity" to { adapter -> adapter.playerEntityAdapter },
    "MixinKeybinding" to { adapter -> adapter.keybindingAdapter },
    "MixinOptions" to { adapter -> adapter.optionsAdapter },
    "MixinSimpleResourceReload" to { adapter -> adapter.simpleResourceReloadAdapter },
    "MixinSoundEngine" to { adapter -> adapter.soundEngineAdapter },
    "MixinSpriteAtlasTexture" to { adapter -> adapter.spriteAtlasTextureAdapter },
    "MixinUnihexFont" to { adapter -> adapter.unihexFontAdapter },
    "MixinInGameHud" to { adapter -> adapter.inGameHudAdapter },
    "MixinScreen" to { adapter -> adapter.screenAdapter },
    "MixinSplashOverlay" to { adapter -> adapter.splashOverlayAdapter },
    "MixinTitleScreen" to { adapter -> adapter.titleScreenAdapter },
    "MixinWindow" to { adapter -> adapter.windowAdapter },
    "MixinChatHud" to { adapter -> adapter.chatHudAdapter },
    "MixinClickEvent" to { adapter -> adapter.clickEventAdapter },
    "MixinStyle" to { adapter -> adapter.styleAdapter },
    "MixinText" to { adapter -> adapter.textAdapter },
    "MixinMain" to { adapter -> adapter.mainAdapter },
    "MixinMouse" to { adapter -> adapter.mouseAdapter }
)