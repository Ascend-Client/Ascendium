package io.github.betterclient.ascendium.mixin.v2612.common;

import io.github.betterclient.ascendium.bridge.ClickEventBridge;
import io.github.betterclient.ascendium.bridge.TextStyleBridge;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Style.class)
public abstract class MixinStyle implements TextStyleBridge {
    private Style thiz = (Style) (Object) this;

    public int getColor() {
        return thiz.getColor().getValue();
    }

    @Override
    public boolean getBold() {
        return thiz.isBold();
    }

    @Override
    public boolean getItalic() {
        return thiz.isItalic();
    }

    @Override
    public @NotNull ClickEventBridge getClickEvent() {
        return (ClickEventBridge) thiz.getClickEvent();
    }
}
