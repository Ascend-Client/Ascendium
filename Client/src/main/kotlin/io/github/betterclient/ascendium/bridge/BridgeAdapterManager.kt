package io.github.betterclient.ascendium.bridge

import net.fabricmc.loader.api.FabricLoader
import org.spongepowered.asm.mixin.Mixins

object BridgeAdapterManager {
    val activeAdapter by lazy {
        adapters[FabricLoader.getInstance().getModContainer("minecraft").get().metadata.version.friendlyString]
            ?: throw UnsupportedOperationException("This version of minecraft is not supported.")
    }

    fun initMixins() {
        adapters.map { it.value.mixinName }.distinct().forEach {
            Mixins.addConfiguration("ascendium.$it.mixins.json")
        }
    }

    fun shouldApply(className: String): Boolean {
        val adapter = activeAdapter

        val adapterVersion = mixinMapping.entries
            .firstOrNull { (mixinName, _) -> className.contains(mixinName) }
            ?.value(adapter)

        if (adapterVersion == null && adapter.applyElse.any { className.endsWith(it) }) {
            return true //apply or else
        }

        println("Apply $className with ${adapter.mixinName}, ${adapterVersion?.let { className.contains(it) }}")
        return adapterVersion?.let { className.contains(it) } ?: false
    }

    fun useBridgeUtil(adapterName: (adapter: BridgeAdapter) -> String, vararg parameters: Any?): Any {
        val adapter = activeAdapter
        val adapterName1 = adapterName(adapter)
        return Class.forName("io.github.betterclient.ascendium.util.$adapterName1").constructors[0].newInstance(
            *parameters
        )
    }
}

val `1_21_4` = BridgeAdapter(
    isObfuscated = true,
    mixinName = "1.21.4",
    keybindingBridgeAdapter = "V1214KeybindingHelper",
    screenBridgeAdapter = "Post120BridgedScreen",
    skiaRenderAdapter = "V1214SkiaRenderAdapter",
    openglTextureAdapter = "V1214OpenGLTextureAdapter",
    rawOpenGLTextureAdapter = "V1214RawOpenGLAdapter",
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

val `1_21_5` = `1_21_4`.copy(
    mixinName = "1.21.8",
    clickEventAdapter = "1218",
    simpleResourceReloadAdapter = "1218",
    playerEntityAdapter = "1218",

    offscreen = "RequireOffscreen",
    rawOpenGLTextureAdapter = "V1215RawOpenGLAdapter",
)

val `1_21_8` = `1_21_5`.copy(
    mixinName = "1.21.8",
    skiaRenderAdapter = "V1218SkiaRenderAdapter",
    openglTextureAdapter = "V1218OpenGLTextureAdapter",
    rawOpenGLTextureAdapter = "V1218RawOpenGLAdapter",

    splashOverlayAdapter = "1218",
    windowAdapter = "1218",
    screenAdapter = "1218",
    mouseAdapter = "1218",
    minecraftClientAdapter = "1218",
    applyElse = listOf(
        "1218.MixinCubeMapRenderer",
        "1218.MixinDrawContext"
    )
)

val `1_21_9` = `1_21_8`.copy(
    mixinName = "1.21.9",
    screenAdapter = "1219",
    keybindingAdapter = "1219",
    mouseAdapter = "1219",

    screenBridgeAdapter = "V1219BridgedScreen",
    keybindingBridgeAdapter = "V1219KeybindingHelper",
    applyElse = listOf(
        "1218.MixinCubeMapRenderer",
        "1219.MixinDrawContext",
        "1219.MixinKeybindingCategory"
    )
)

val `1_21_10` = `1_21_9`.copy()

val `1_21_11` = `1_21_10`.copy(
    mixinName = "1.21.11",
    rawOpenGLTextureAdapter = "V12111RawOpenGLAdapter",
    spriteAtlasTextureAdapter = "12111",
)

val `1_21_1` = `1_21_4`.copy(
    mixinName = "1.21.1",
    splashOverlayAdapter = "1211",
    rawOpenGLTextureAdapter = "V1211RawOpenGLAdapter"
)

val `1_20_6` = `1_21_1`.copy(
    mixinName = "1.20.6",
    inGameHudAdapter = "1206",
    rawOpenGLTextureAdapter = "V1206RawOpenGLAdapter"
)

val `1_20_4` = `1_20_6`.copy(
    mixinName = "1.20.4",
    chatHudAdapter = "1204"
)

val `1_20_1` = `1_20_4`.copy(
    screenBridgeAdapter = "V1201BridgedScreen"
)

val `1_19_4` = `1_20_1`.copy(
    mixinName = "1.19.4",
    splashOverlayAdapter = "1194",
    inGameHudAdapter = "1194",
    screenAdapter = "1194",
    unihexFontAdapter = "disable",

    screenBridgeAdapter = "Pre120BridgedScreen"
)

val `1_19_2` = `1_19_4`.copy(
    mixinName = "1.19.2",
    playerEntityAdapter = "1192",
    itemStackAdapter = "1192",
    spriteAtlasTextureAdapter = "1192",
    simpleResourceReloadAdapter = "disable",

    rawOpenGLTextureAdapter = "V1192RawOpenGLAdapter"
)

val `1_16_5` = `1_19_2`.copy(
    mixinName = "1.16.5",
    offscreen = "RequireOffscreen",
    screenBridgeAdapter = "V116BridgedScreen",
    rawOpenGLTextureAdapter = "V1165RawOpenGLAdapter",

    playerEntityAdapter = "1165",
    entityAdapter = "1165",
    itemStackAdapter = "1165",
    minecraftClientAdapter = "1165",
    keybindingAdapter = "1165",
    optionsAdapter = "1165",
    soundEngineAdapter = "1165",
    chatHudAdapter = "1165",
    splashOverlayAdapter = "1165" //actually splash screen
)

val cts8c = `1_16_5`.copy(
    mixinName = "cts8c",
    minecraftClientAdapter = "cts",
    optionsAdapter = "cts",
    keybindingAdapter = "cts",
    mouseAdapter = "cts",
    keybindingBridgeAdapter = "CTSKeybindingHelper"
)

//26.1.2 has to be a new root because it requires non obfuscated names + mojmap
val `26_1_2` = BridgeAdapter(
    mixinName = "26.1.2",
    isObfuscated = false,
    keybindingBridgeAdapter = "V2612KeybindingHelper",
    screenBridgeAdapter = "V2612BridgedScreen",
    skiaRenderAdapter = "DisabledSkiaAdapter",
    openglTextureAdapter = "V2612OpenGLTextureAdapter",
    rawOpenGLTextureAdapter = "V2612RawOpenGLAdapter",
    offscreen = "RequireOffscreen",

    minecraftClientAdapter = "2612",
    entityAdapter = "2612",
    itemStackAdapter = "2612",
    playerEntityAdapter = "2612",
    keybindingAdapter = "2612",
    optionsAdapter = "2612",
    simpleResourceReloadAdapter = "2612",
    soundEngineAdapter = "2612",
    spriteAtlasTextureAdapter = "2612",
    unihexFontAdapter = "2612",
    inGameHudAdapter = "2612",
    screenAdapter = "2612",
    splashOverlayAdapter = "2612",
    titleScreenAdapter = "2612",
    windowAdapter = "2612",
    chatHudAdapter = "2612",
    clickEventAdapter = "2612",
    styleAdapter = "2612",
    textAdapter = "2612",
    mouseAdapter = "2612",
    mainAdapter = "2612",

    applyElse = listOf(
        "2612.options.MixinKeybindingCategory"
    )
)

val `26_2` = `26_1_2`.copy(
    mixinName = "26.2",
    minecraftClientAdapter = "262",
    inGameHudAdapter = "262",

    openglTextureAdapter = "V262OpenGLTextureAdapter",
    rawOpenGLTextureAdapter = "V262RawOpenGLAdapter",
)

data class BridgeAdapter(
    val mixinName: String,
    val isObfuscated: Boolean,
    val keybindingBridgeAdapter: String,
    val screenBridgeAdapter: String,
    val skiaRenderAdapter: String,
    val openglTextureAdapter: String,
    val rawOpenGLTextureAdapter: String,
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
    val applyElse: List<String> = emptyList()
)

val adapters = mutableMapOf(
    "1.16.3-combat.8.c" to cts8c,
    "1.16.5" to `1_16_5`,
    "1.19.2" to `1_19_2`,
    "1.19.4" to `1_19_4`,
    "1.20.1" to `1_20_1`,
    "1.20.4" to `1_20_4`,
    "1.20.6" to `1_20_6`,
    "1.21.1" to `1_21_1`,
    "1.21.4" to `1_21_4`,
    "1.21.5" to `1_21_5`,
    "1.21.8" to `1_21_8`,
    "1.21.9" to `1_21_9`,
    "1.21.10" to `1_21_10`,
    "1.21.11" to `1_21_11`,
    "26.1.2" to `26_1_2`,
    "26.2" to `26_2`,
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