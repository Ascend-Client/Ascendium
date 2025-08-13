package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.KeybindHelper;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

public class KeybindingHelper extends KeyBinding implements KeybindHelper {
    public Function0<Unit> onPressedAction = () -> Unit.INSTANCE;
    public Function0<Unit> onReleasedAction = () -> Unit.INSTANCE;
    public Function1<? super Integer, Unit> onKeyChangedAction = (key) -> Unit.INSTANCE;

    public KeybindingHelper(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (pressed) {
            onPressedAction.invoke();
        } else {
            onReleasedAction.invoke();
        }
    }

    @Override
    public void setBoundKey(InputUtil.Key boundKey) {
        super.setBoundKey(boundKey);
        onKeyChangedAction.invoke(boundKey.getCode());
    }

    @Override
    public void onPressed(@NotNull Function0<@NotNull Unit> action) {
        onPressedAction = action;
    }

    @Override
    public void onReleased(@NotNull Function0<@NotNull Unit> action) {
        onReleasedAction = action;
    }

    @Override
    public void onKeyChanged(@NotNull Function1<? super @NotNull Integer, @NotNull Unit> action) {
        onKeyChangedAction = action;
    }
}
