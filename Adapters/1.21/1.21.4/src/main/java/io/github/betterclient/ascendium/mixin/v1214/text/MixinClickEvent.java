package io.github.betterclient.ascendium.mixin.v1214.text;

import io.github.betterclient.ascendium.bridge.ClickEventActionBridge;
import io.github.betterclient.ascendium.bridge.ClickEventBridge;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClickEvent.class)
public class MixinClickEvent implements ClickEventBridge {
    @Shadow @Final private ClickEvent.Action action;

    @Shadow @Final private String value;

    @Override
    public @NotNull ClickEventActionBridge getAction() {
        return switch (action) {
            case OPEN_URL -> ClickEventActionBridge.OPEN_URL;
            case OPEN_FILE -> ClickEventActionBridge.OPEN_FILE;
            case RUN_COMMAND -> ClickEventActionBridge.RUN_COMMAND;
            case SUGGEST_COMMAND -> ClickEventActionBridge.SUGGEST_COMMAND;
            case CHANGE_PAGE -> ClickEventActionBridge.CHANGE_PAGE;
            case COPY_TO_CLIPBOARD -> ClickEventActionBridge.COPY_TO_CLIPBOARD;
        };
    }

    @Override
    public @NotNull String getBridgeValue() {
        return this.value;
    }
}
