package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.*;
import io.github.betterclient.ascendium.event.EntityHitEvent;
import io.github.betterclient.ascendium.util.BridgedScreen;
import io.github.betterclient.ascendium.util.FakeItemStackBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements MinecraftBridge {
    @Shadow @Final public GameOptions options;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Shadow @Final private Window window;

    @Shadow @Final public Mouse mouse;

    @Shadow private static int currentFps;

    @Shadow @Final public TextRenderer textRenderer;

    @Shadow @Nullable public abstract ServerInfo getCurrentServerEntry();

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Final private ReloadableResourceManagerImpl resourceManager;

    @Shadow @Nullable public Screen currentScreen;

    @Shadow @Nullable public ClientWorld world;

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
    public int getPing() {
        return getCurrentServerEntry() == null ? -1 : ((int) getCurrentServerEntry().ping);
    }

    @Override
    public @NotNull String getServer() {
        return getCurrentServerEntry() == null ? "Singleplayer" : getCurrentServerEntry().address;
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"))
    public void attackSwing(CallbackInfoReturnable<Boolean> cir) {
        new EntityHitEvent((EntityBridge) this.player, (EntityBridge) ((EntityHitResult) this.crosshairTarget).getEntity()).broadcast();
    }

    @Override
    public @NotNull @Nullable PlayerBridge getPlayer() {
        return (PlayerBridge) this.player;
    }

    @Override
    public @Nullable RaycastResultBridge raycast(@NotNull EntityBridge entityBridge, @NotNull Pos3D camera, @NotNull Pos3D possibleHits, @NotNull BoundingBox box, int id, double d3) {
        EntityHitResult hitResult = ProjectileUtil.raycast(
                (Entity) entityBridge,
                new Vec3d(camera.getX(), camera.getY(), camera.getZ()),
                new Vec3d(possibleHits.getX(), possibleHits.getY(), possibleHits.getZ()),
                new Box(box.getStart().getX(), box.getStart().getY(), box.getStart().getZ(), box.getEnd().getX(), box.getEnd().getY(), box.getEnd().getZ()),
                entity1 -> entity1.getId() == id, d3
        );

        return hitResult == null ? null : new RaycastResultBridge(
                new Pos3D(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z),
                (EntityBridge) hitResult.getEntity()
        );
    }

    @Override
    public byte @Nullable [] loadResource(@NotNull IdentifierBridge identifier) {
        Optional<Resource> resource = this.resourceManager.getResource(Identifier.of(identifier.getNamespace(), identifier.getPath()));
        if (resource.isEmpty()) {
            return null;
        } else {
            try {
                InputStream inputStream = resource.get().getInputStream();
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
        this.setScreen(switch (mcScreen) {
            case SELECT_WORLD_SCREEN -> new SelectWorldScreen(this.currentScreen);
            case MULTIPLAYER_SCREEN -> new MultiplayerScreen(this.currentScreen);
            case REALMS_MAIN_SCREEN -> new RealmsMainScreen(this.currentScreen);
            case OPTIONS_SCREEN -> new OptionsScreen(this.currentScreen, this.options);
        });
    }

    @Override
    public boolean isWorldNull() {
        return this.world == null;
    }

    @Override
    public boolean isScreenNull() {
        return this.currentScreen == null;
    }

    @Override
    public @NotNull ItemStackBridge createItemStack(@NotNull IdentifierBridge item, int count, float durability) {
        return new FakeItemStackBridge(count, durability, item);
    }
}