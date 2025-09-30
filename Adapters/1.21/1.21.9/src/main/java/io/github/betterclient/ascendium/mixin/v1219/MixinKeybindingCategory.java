package io.github.betterclient.ascendium.mixin.v1219;

import io.github.betterclient.ascendium.util.V1219KeybindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.Category.class)
public class MixinKeybindingCategory {
    @Inject(method = "getLabel", at = @At("HEAD"), cancellable = true)
    public void onGetLabel(CallbackInfoReturnable<Text> cir) {
        String name = V1219KeybindingHelper.CATEGORY_NAME_MAP.get(this);
        if (name != null) {
            cir.setReturnValue(Text.literal(name));
            cir.cancel();
        }
    }
}