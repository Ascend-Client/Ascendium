package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.datlag.kcef.KCEF
import dev.datlag.kcef.KCEFBrowser
import dev.datlag.kcef.KCEFClient
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.cef.browser.CefRendering
import java.awt.Dimension
import java.io.File
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

class OffscreenBrowser() {
    val client = CHROME!! //guaranteed to be non-null
    var browser: KCEFBrowser = client.createBrowser(
            "https://youtube.com",
            CefRendering.OFFSCREEN,
            true
        )
    val owner = JFrame()

    init {
        val width = 800
        val height = 800
        SwingUtilities.invokeLater {
            owner.isFocusable = true
            owner.size = Dimension(width, height)
            owner.contentPane.add(browser.uiComponent)
            owner.isVisible = true
            browser.wasResized(width, height)
        }
    }

    suspend fun getBuffer(): ImageBitmap? {
        val image = browser.createScreenshot(false).await()
        return image.toComposeImageBitmap()
    }

    fun close() {
        browser.dispose()
        owner.dispose()
    }

    companion object {
        var CHROME: KCEFClient? = null

        fun init() {
            Thread {
                val installDir = File(".ascendium/chromium", "kcef-bundle")

                runBlocking {
                    KCEF.init(builder = {
                        addArgs(
                            "--disable-gpu",
                            "--disable-gpu-compositing",
                            "--in-process-gpu",
                            "--no-sandbox"
                        )

                        installDir(installDir)

                        progress {
                            onDownloading {
                                println(it)
                            }
                            onInitialized {
                                CHROME = KCEF.newClientBlocking()
                            }
                        }

                        settings {
                            browserSubProcessPath = File(installDir, "jcef_helper").absolutePath
                            noSandbox = true
                            cachePath = File(".ascendium/chromium", "cache").absolutePath
                            resourcesDirPath = installDir.absolutePath
                            localesDirPath = File(installDir, "locales").absolutePath
                        }
                    }, onError = {
                        it?.printStackTrace()
                    }, onRestartRequired = {
                        println("Restart required by KCEF.")
                        JOptionPane.showMessageDialog(null, "KCEF requires an application restart to continue.")
                        exitProcess(0)
                    })
                }
            }.start()
        }
    }
}