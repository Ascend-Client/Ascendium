package io.github.betterclient.ascendium.mixin.render;

import io.github.betterclient.ascendium.BridgeKt;
import io.github.betterclient.ascendium.ui.minecraft.CustomMainMenu;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TitleScreen.class, priority = 2000)
public class MixinTitleScreen {
    @Inject(method = "init", at = @At(value = "HEAD"), cancellable = true)
    public void init(CallbackInfo ci) {
        BridgeKt.getMinecraft().openScreen(CustomMainMenu.INSTANCE);
        ci.cancel();
    }
}