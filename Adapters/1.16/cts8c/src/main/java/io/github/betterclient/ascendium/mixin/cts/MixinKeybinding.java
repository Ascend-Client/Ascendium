package io.github.betterclient.ascendium.mixin.cts;

import io.github.betterclient.ascendium.bridge.KeybindingBridge;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeybinding implements KeybindingBridge {
    @Shadow private boolean pressed;

    @Shadow private InputUtil.Key boundKey;

    @Shadow @Final private static Map<String, Integer> categoryOrderMap;

    @Inject(method = "<init>(Ljava/lang/String;ILjava/lang/String;)V", at = @At("RETURN"))
    private void onInit(String translationKey, int code, String category, CallbackInfo ci) {
        //check if the category exists, if not, add it
        if (!categoryOrderMap.containsKey(category)) {
            categoryOrderMap.put(category, categoryOrderMap.size());
        }
    }

    @Override
    public boolean getButtonPressed() {
        return this.pressed;
    }

    @Override
    public @NotNull String getGetBoundKey() {
        return this.boundKey.getLocalizedText().getString();
    }
}
