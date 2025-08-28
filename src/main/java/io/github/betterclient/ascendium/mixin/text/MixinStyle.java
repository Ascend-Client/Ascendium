package io.github.betterclient.ascendium.mixin.text;

import io.github.betterclient.ascendium.ClickEventBridge;
import io.github.betterclient.ascendium.TextStyleBridge;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Style.class)
public abstract class MixinStyle implements TextStyleBridge {
    private Style thiz = (Style) (Object) this;

    public int getColor() {
        return thiz.getColor().getRgb();
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
