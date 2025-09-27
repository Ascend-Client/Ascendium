package io.github.betterclient.ascendium.mixin.v1165;

import io.github.betterclient.ascendium.bridge.TextBridge;
import io.github.betterclient.ascendium.event.ChatEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    public void onMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        new ChatEvent((TextBridge) message).broadcast();
    }
}
