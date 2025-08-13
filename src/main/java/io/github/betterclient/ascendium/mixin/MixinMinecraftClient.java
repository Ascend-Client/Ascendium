package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.Ascendium;
import io.github.betterclient.ascendium.BridgeScreen;
import io.github.betterclient.ascendium.MinecraftBridge;
import io.github.betterclient.ascendium.OptionsBridge;
import io.github.betterclient.ascendium.util.BridgedScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements MinecraftBridge {
    @Shadow @Final public GameOptions options;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Override
    public @NotNull OptionsBridge getGameOptions() {
        return (OptionsBridge) this.options;
    }

    @Override
    public void openScreen(@NotNull BridgeScreen screen) {
        this.setScreen(new BridgedScreen(screen));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(RunArgs args, CallbackInfo ci) {
        Ascendium.INSTANCE.start();
    }
}