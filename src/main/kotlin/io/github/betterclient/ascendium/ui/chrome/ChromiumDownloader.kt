package io.github.betterclient.ascendium.ui.chrome

import dev.datlag.kcef.KCEF
import kotlinx.coroutines.runBlocking
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLifeSpanHandlerAdapter
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ChromiumDownloader {
    var chromiumDownloaded = false
    var app: CefApp? = null
    var client: CefClient? = null

    fun download() = Thread { runBlocking {
        if (chromiumDownloaded) return@runBlocking
        val installDir = File(".ascendium/chromium", "kcef-bundle")

        suspendCoroutine { continuation ->
            KCEF.initBlocking(builder = {
                addArgs()

                installDir(installDir)
                settings {
                    browserSubProcessPath = File(installDir, "jcef_helper").absolutePath
                    noSandbox = true
                    cachePath = File(".ascendium/chromium", "cache").absolutePath
                    resourcesDirPath = installDir.absolutePath
                    localesDirPath = File(installDir, "locales").absolutePath
                }

                progress {
                    onDownloading { percent -> }

                    onInitialized {
                        try {
                            app = KCEF::class.java.getDeclaredMethod("getCefApp").also { it.isAccessible = true }.invoke(KCEF) as CefApp
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (app != null) {
                            client = app!!.createClient().alsoAddPopupPrevention()
                            chromiumDownloaded = true
                            continuation.resume(Unit)
                        }
                    }
                }
            }, onError = {
                it?.printStackTrace()
                println("KCEF initialization failed.")
                continuation.resume(Unit)
            })
        }

        return@runBlocking
    } }.start()
}

fun CefClient.alsoAddPopupPrevention() = this.addLifeSpanHandler(object : CefLifeSpanHandlerAdapter() {
    override fun onBeforePopup(
        browser: CefBrowser,
        frame: CefFrame,
        target_url: String,
        target_frame_name: String
    ): Boolean {
        return true
    }
})