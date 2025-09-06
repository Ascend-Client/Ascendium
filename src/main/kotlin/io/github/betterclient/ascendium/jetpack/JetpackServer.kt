package io.github.betterclient.ascendium.jetpack

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.browser.CefMessageRouter
import org.cef.callback.CefQueryCallback
import org.cef.handler.CefMessageRouterHandlerAdapter

val mappings = mutableMapOf<String, () -> Unit>()
class JetpackServer(private val block: (JetpackContext.() -> Unit)) {
    lateinit var browser: CefBrowser
    fun serve(fileName: String, browser: CefBrowser): ByteArray {
        if (!::browser.isInitialized) {
            this.browser = browser
        }
        if (fileName == "index.html") {
            return buildIndex().toByteArray()
        }
        mappings[fileName]?.invoke()
        return ByteArray(0)
    }

    fun buildIndex(): String {
        return createHTML(
            prettyPrint = true,
        ).html {
            head {
                title { +"Jetpack Server" }
                style {
                    unsafe {
                        +"""
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        """.trimIndent()
                    }
                }
            }
            body {
                JetpackContext(this@JetpackServer, this@html.consumer).block()
            }
        }.also { println(it) }
    }

    companion object {
        fun serve(block: JetpackContext.() -> Unit): ((fileName: String, browser: CefBrowser) -> ByteArray) {
            return JetpackServer(block)::serve
        }

        fun addQueryRouter(app: CefClient) {
            app.addMessageRouter(CefMessageRouter.create().also {
                it.addHandler(object : CefMessageRouterHandlerAdapter() {
                    override fun onQuery(
                        browser: CefBrowser?,
                        frame: CefFrame?,
                        queryId: Long,
                        request: String?,
                        persistent: Boolean,
                        callback: CefQueryCallback?
                    ): Boolean {
                        try {
                            if (mappings[request] != null) {
                                mappings[request]?.invoke()
                                callback!!.success("")
                                return true
                            } else {
                                callback!!.failure(404, "Unknown request: $request")
                                return true
                            }
                        } catch (e: Exception) {
                            callback!!.failure(500, "Internal error: ${e.message}")
                            return true
                        }
                    }
                }, true)
            })
        }
    }
}

open class JetpackContext(val server: JetpackServer, override val consumer: TagConsumer<*>) : BODY(mutableMapOf(), consumer)

fun FlowContent.remake(ctx: JetpackContext) =
    JetpackContext(ctx.server, this.consumer)