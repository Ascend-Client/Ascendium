package io.github.betterclient.ascendium.mixin.v2612.reload;

import net.minecraft.server.packs.resources.SimpleReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Mixin(SimpleReloadInstance.class)
@SuppressWarnings("all")
public class MixinSimpleResourceReload<S> {
    @Redirect(method = "prepareTasks", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean onAdd(List<CompletableFuture<S>> instance, Object ee) {
        CompletableFuture<S> e = (CompletableFuture<S>) ee;
        new Thread(() -> {
            S result;
            try {
                result = e.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            instance.add(CompletableFuture.completedFuture(result));
        }).start();
        return false;
    }
}