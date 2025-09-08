package io.github.betterclient.ascendium.jetpack.element

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.JetpackContext
import io.github.betterclient.ascendium.jetpack.aware
import io.github.betterclient.ascendium.jetpack.mappings
import io.github.betterclient.ascendium.jetpack.remake
import io.github.betterclient.ascendium.jetpack.toStyleString
import kotlinx.html.button
import kotlinx.html.style
import kotlin.random.Random

inline fun JetpackContext.HTMLButton(
    modifier: HTMLModifier = HTMLModifier,
    crossinline content: JetpackContext.() -> Unit,
    noinline onClick: () -> Unit
) {
    val finalModifier = aware(modifier)

    button {
        val materialStyle = """
            position: relative;
            overflow: hidden;
            all: unset;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 10px 24px;
            border-radius: 20px;
            background-color: #6750A4;
            color: white;
            font-family: Roboto, sans-serif;
            font-weight: 500;
            font-size: 14px;
            line-height: 20px;
            letter-spacing: 0.1px;
            cursor: pointer;
            box-shadow: 0px 1px 2px rgba(0,0,0,0.3),
                        0px 2px 6px rgba(0,0,0,0.15);
            transition: box-shadow 0.2s, background-color 0.2s;
        """.trimIndent()

        style = materialStyle + finalModifier.elements.toStyleString()

        attributes["onmouseover"] =
            "this.style.boxShadow='0px 2px 4px rgba(0,0,0,0.4), 0px 4px 8px rgba(0,0,0,0.2)'"
        attributes["onmouseout"] =
            "this.style.boxShadow='0px 1px 2px rgba(0,0,0,0.3), 0px 2px 6px rgba(0,0,0,0.15)'"

        attributes["onmousedown"] = """
            var rect = this.getBoundingClientRect();
            var ripple = document.createElement('span');
            ripple.style.position = 'absolute';
            ripple.style.borderRadius = '50%';
            ripple.style.backgroundColor = 'rgba(255,255,255,0.3)';
            ripple.style.pointerEvents = 'none';
            var maxDistX = Math.max(event.clientX - rect.left, rect.right - event.clientX);
            var maxDistY = Math.max(event.clientY - rect.top, rect.bottom - event.clientY);
            var size = Math.sqrt(maxDistX*maxDistX + maxDistY*maxDistY) * 2;
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = (event.clientX - rect.left - size/2) + 'px';
            ripple.style.top = (event.clientY - rect.top - size/2) + 'px';
            ripple.style.transform = 'scale(0)';
            ripple.style.transition = 'transform 0.4s ease, opacity 0.8s ease';
            this.appendChild(ripple);
            requestAnimationFrame(() => {
                ripple.style.transform = 'scale(1)';
                ripple.style.opacity = '0';
            });
            setTimeout(() => ripple.remove(), 800);
        """.trimIndent()

        val clickHandler = "button_clicked_${Random.nextLong(0, Long.MAX_VALUE)}"
        mappings[clickHandler] = onClick
        attributes["onclick"] = "window.cefQuery({ request: '$clickHandler' })"

        remake(this@HTMLButton).content()
    }
}