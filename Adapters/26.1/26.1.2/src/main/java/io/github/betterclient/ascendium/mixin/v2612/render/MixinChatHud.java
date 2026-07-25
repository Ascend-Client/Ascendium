package io.github.betterclient.ascendium.mixin.v2612.render;

import io.github.betterclient.ascendium.bridge.TextBridge;
import io.github.betterclient.ascendium.event.ChatEvent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class MixinChatHud {
    @Inject(method = "addMessageToDisplayQueue", at = @At("HEAD"))
    public void onMessage(GuiMessage message, CallbackInfo ci) {
        new ChatEvent((TextBridge) message.content()).broadcast();
    }
}
