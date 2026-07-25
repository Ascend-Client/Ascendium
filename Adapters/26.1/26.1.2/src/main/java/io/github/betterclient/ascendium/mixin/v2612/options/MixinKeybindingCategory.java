package io.github.betterclient.ascendium.mixin.v2612.options;

import io.github.betterclient.ascendium.util.V2612KeybindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.Category.class)
public class MixinKeybindingCategory {
    @Inject(method = "label", at = @At("HEAD"), cancellable = true)
    public void onGetLabel(CallbackInfoReturnable<Component> cir) {
        String name = V2612KeybindingHelper.CATEGORY_NAME_MAP.get(this);
        if (name != null) {
            cir.setReturnValue(Component.literal(name));
            cir.cancel();
        }
    }
}