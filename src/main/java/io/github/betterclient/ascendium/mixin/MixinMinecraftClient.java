package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.*;
import io.github.betterclient.ascendium.util.BridgedScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
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

    @Shadow @Final private Window window;

    @Shadow @Final public Mouse mouse;

    @Shadow private static int currentFps;

    @Shadow @Final public TextRenderer textRenderer;

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

    @Override
    public @NotNull WindowBridge getWindow() {
        return (WindowBridge) (Object) this.window;
    }

    @Override
    public @NotNull MouseBridge getMouse() {
        return (MouseBridge) this.mouse;
    }

    @Override
    public @NotNull int getFps() {
        return currentFps;
    }

    @Override
    public @NotNull TextRendererBridge getTextRenderer() {
        return (TextRendererBridge) this.textRenderer;
    }
}