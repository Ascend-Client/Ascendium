package io.github.betterclient.ascendium.mixin.v262;

import com.mojang.blaze3d.platform.Window;
import com.mojang.realmsclient.RealmsMainScreen;
import io.github.betterclient.ascendium.Ascendium;
import io.github.betterclient.ascendium.bridge.*;
import io.github.betterclient.ascendium.event.EntityHitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient implements MinecraftBridge {
    @Shadow @Final public Options options;

    @Shadow @Final private Window window;

    @Shadow @Final public MouseHandler mouseHandler;

    @Shadow private static int fps;

    @Shadow @Nullable public abstract ServerData getCurrentServer();

    @Shadow @Nullable public HitResult hitResult;

    @Shadow @Final private ReloadableResourceManager resourceManager;

    @Shadow @Nullable public ClientLevel level;

    @Shadow
    @org.jspecify.annotations.Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    public Gui gui;

    @Override
    public @NotNull OptionsBridge getGameOptions() {
        return (OptionsBridge) this.options;
    }

    @Override
    public void openScreen(@NotNull BridgeScreen screen) {
        this.gui.setScreen((Screen) BridgeAdapterManager.INSTANCE.useBridgeUtil(BridgeAdapter::getScreenBridgeAdapter, screen));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(GameConfig gameConfig, CallbackInfo ci) {
        Ascendium.INSTANCE.start();
    }

    @Override
    public @NotNull WindowBridge getWindow() {
        return (WindowBridge) (Object) this.window;
    }

    @Override
    public @NotNull MouseBridge getMouse() {
        return (MouseBridge) this.mouseHandler;
    }

    @Override
    public @NotNull int getAfps() {
        return fps;
    }

    @Override
    public int getPing() {
        return getCurrentServer() == null ? -1 : ((int) getCurrentServer().ping);
    }

    @Override
    public @NotNull String getServer() {
        return getCurrentServer() == null ? "Singleplayer" : getCurrentServer().ip;
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V"))
    public void attackSwing(CallbackInfoReturnable<Boolean> cir) {
        new EntityHitEvent((EntityBridge) this.player, (EntityBridge) ((EntityHitResult) this.hitResult).getEntity()).broadcast();
    }

    @Override
    public @NotNull @Nullable PlayerBridge getPlayer() {
        return (PlayerBridge) this.player;
    }

    @Override
    public @Nullable RaycastResultBridge raycast(@NotNull EntityBridge entityBridge, @NotNull Pos3D camera, @NotNull Pos3D possibleHits, @NotNull BoundingBox box, int id, double d3) {
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                (Entity) entityBridge,
                new Vec3(camera.getX(), camera.getY(), camera.getZ()),
                new Vec3(possibleHits.getX(), possibleHits.getY(), possibleHits.getZ()),
                new AABB(box.getStart().getX(), box.getStart().getY(), box.getStart().getZ(), box.getEnd().getX(), box.getEnd().getY(), box.getEnd().getZ()),
                entity1 -> entity1.getId() == id, d3
        );

        return hitResult == null ? null : new RaycastResultBridge(
                new Pos3D(hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z),
                (EntityBridge) hitResult.getEntity()
        );
    }

    @Override
    public byte @Nullable [] loadResource(@NotNull IdentifierBridge identifier) {
        Optional<Resource> resource = this.resourceManager.getResource(Identifier.fromNamespaceAndPath(identifier.getNamespace(), identifier.getPath()));
        if (resource.isEmpty()) {
            return null;
        } else {
            try {
                InputStream inputStream = resource.get().open();
                byte[] bytes = inputStream.readAllBytes();
                inputStream.close();
                return bytes;
            } catch (IOException e) {
                return null;
            }
        }
    }

    @Override
    public void setScreen(@NotNull MCScreen mcScreen) {
        this.gui.setScreen(switch (mcScreen) {
            case SELECT_WORLD_SCREEN -> new SelectWorldScreen(this.gui.screen());
            case MULTIPLAYER_SCREEN -> new JoinMultiplayerScreen(this.gui.screen());
            case REALMS_MAIN_SCREEN -> new RealmsMainScreen(this.gui.screen());
            case OPTIONS_SCREEN -> new OptionsScreen(this.gui.screen(), this.options, false);
            case CHAT_SCREEN -> new ChatScreen("", false);
            case OTHER_SCREEN -> null;
        });
    }

    @Override
    public boolean isWorldNull() {
        return this.level == null;
    }

    @Override
    public @Nullable MCScreen getScreen() {
        return switch (this.gui.screen()) {
            case SelectWorldScreen ignored -> MCScreen.SELECT_WORLD_SCREEN;
            case JoinMultiplayerScreen ignored -> MCScreen.MULTIPLAYER_SCREEN;
            case RealmsMainScreen ignored -> MCScreen.REALMS_MAIN_SCREEN;
            case OptionsScreen ignored -> MCScreen.OPTIONS_SCREEN;
            case ChatScreen ignored -> MCScreen.CHAT_SCREEN;
            case null -> null;
            default -> MCScreen.OTHER_SCREEN;
        };
    }
}