package io.github.betterclient.ascendium.mixin.text;

import io.github.betterclient.ascendium.TextBridge;
import io.github.betterclient.ascendium.event.ChatEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @Inject(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At("HEAD"))
    public void onMessage(ChatHudLine message, CallbackInfo ci) {
        new ChatEvent((TextBridge) message.content()).broadcast();
    }
}
