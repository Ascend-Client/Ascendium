package io.github.betterclient.ascendium.util;

import io.github.betterclient.ascendium.bridge.KeybindHelper;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V1219KeybindingHelper extends KeyBinding implements KeybindHelper {
    public static Map<String, Category> NAME_CATEGORY_MAP = new HashMap<>(); //THANK GOD THIS ISN'T AN ENUM
    public static final Map<Category, String> CATEGORY_NAME_MAP = new HashMap<>();

    public Function0<Unit> onPressedAction = () -> Unit.INSTANCE;
    public Function0<Unit> onReleasedAction = () -> Unit.INSTANCE;
    public Function1<? super Integer, Unit> onKeyChangedAction = (key) -> Unit.INSTANCE;

    public V1219KeybindingHelper(String translationKey, int code, String category) {
        super(translationKey, code, NAME_CATEGORY_MAP.computeIfAbsent(category.toLowerCase(), key ->
                Category.create(Identifier.ofVanilla(key)))
        );
        CATEGORY_NAME_MAP.put(this.getCategory(), category);
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
