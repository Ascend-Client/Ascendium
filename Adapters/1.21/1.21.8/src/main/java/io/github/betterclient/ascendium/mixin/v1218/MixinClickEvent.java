package io.github.betterclient.ascendium.mixin.v1218;

import io.github.betterclient.ascendium.bridge.ClickEventActionBridge;
import io.github.betterclient.ascendium.bridge.ClickEventBridge;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClickEvent.class)
public interface MixinClickEvent extends ClickEventBridge {
    @Override
    default ClickEventActionBridge getAction() {
        return switch (((ClickEvent)this).getAction()) {
            case OPEN_URL -> ClickEventActionBridge.OPEN_URL;
            case OPEN_FILE -> ClickEventActionBridge.OPEN_FILE;
            case RUN_COMMAND -> ClickEventActionBridge.RUN_COMMAND;
            case SUGGEST_COMMAND -> ClickEventActionBridge.SUGGEST_COMMAND;
            case SHOW_DIALOG, CUSTOM -> null;
            case CHANGE_PAGE -> ClickEventActionBridge.CHANGE_PAGE;
            case COPY_TO_CLIPBOARD -> ClickEventActionBridge.COPY_TO_CLIPBOARD;
        };
    }

    default @NotNull String getBridgeValue() {
        ClickEvent self = (ClickEvent) this;

        return switch (self) {
            case ClickEvent.OpenUrl(java.net.URI uri) -> uri.toString();
            case ClickEvent.OpenFile(String path) -> path;
            case ClickEvent.RunCommand(String command) -> command;
            case ClickEvent.SuggestCommand(String command) -> command;
            case ClickEvent.ChangePage(int page) -> Integer.toString(page);
            case ClickEvent.CopyToClipboard(String value) -> value;
            default -> "";
        };
    }
}
