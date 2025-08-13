package io.github.betterclient.ascendium.mixin;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeybinding {
    @Shadow @Final private static Map<String, Integer> CATEGORY_ORDER_MAP;

    @Inject(method = "<init>(Ljava/lang/String;ILjava/lang/String;)V", at = @At("RETURN"))
    private void onInit(String translationKey, int code, String category, CallbackInfo ci) {
        //check if the category exists, if not, add it
        if (!CATEGORY_ORDER_MAP.containsKey(category)) {
            CATEGORY_ORDER_MAP.put(category, CATEGORY_ORDER_MAP.size());
        }
    }
}
