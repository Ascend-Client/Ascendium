package io.github.betterclient.ascendium.ui.minecraft

import io.github.betterclient.ascendium.jetpack.HTMLAlignment
import io.github.betterclient.ascendium.jetpack.HTMLArrangement
import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.JetpackServer
import io.github.betterclient.ascendium.jetpack.RawCssElement
import io.github.betterclient.ascendium.jetpack.element.HTMLBox
import io.github.betterclient.ascendium.jetpack.element.HTMLButton
import io.github.betterclient.ascendium.jetpack.element.HTMLColumn
import io.github.betterclient.ascendium.jetpack.element.HTMLImage
import io.github.betterclient.ascendium.jetpack.element.HTMLRow
import io.github.betterclient.ascendium.jetpack.element.HTMLText
import io.github.betterclient.ascendium.jetpack.modifier.background
import io.github.betterclient.ascendium.jetpack.modifier.borderRadius
import io.github.betterclient.ascendium.jetpack.modifier.fillMaxSize
import io.github.betterclient.ascendium.jetpack.modifier.fillMaxWidth
import io.github.betterclient.ascendium.jetpack.modifier.height
import io.github.betterclient.ascendium.jetpack.modifier.padding
import io.github.betterclient.ascendium.jetpack.modifier.size
import io.github.betterclient.ascendium.jetpack.modifier.zIndex
import org.cef.browser.CefBrowser
import java.awt.Color

object CustomMainMenuServer {
    fun serve() = JetpackServer.serve {
        HTMLBox(
            modifier = HTMLModifier
                .fillMaxSize()
                .background(Color.decode("#E0E0E0")),
            contentAlignment = HTMLAlignment.Center
        ) {
            HTMLBox(
                modifier = HTMLModifier
                    .size(width = 300, height = 400)
                    .background(Color.WHITE)
                    .borderRadius(16)
                    .then(RawCssElement("0px 4px 12px rgba(0,0,0,0.1)"))
            ) {
                HTMLImage(
                    src = "https://picsum.photos/300/150",
                    modifier = HTMLModifier
                        .fillMaxWidth()
                        .height(150)
                        .borderRadius(topStart = 16, topEnd = 16)
                        .align(HTMLAlignment.TopCenter)
                )

                HTMLColumn(
                    modifier = HTMLModifier
                        .fillMaxWidth()
                        .padding(24)
                        .align(HTMLAlignment.BottomCenter)
                        .zIndex(2),
                    horizontalAlignment = HTMLAlignment.CenterHorizontally,
                    verticalArrangement = HTMLArrangement.Center_
                ) {
                    HTMLRow(
                        modifier = HTMLModifier.padding(top = 20).fillMaxWidth(),
                        horizontalArrangement = HTMLArrangement.SpaceAround,
                        verticalAlignment = HTMLAlignment.CenterVertically
                    ) {
                        HTMLButton(onClick = {
                            println("I LOVE YOU!")
                        }, content = {
                            HTMLText("Click me")
                        })
                    }
                }
            }
        }
    }
}