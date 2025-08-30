package io.github.betterclient.ascendium.mixin.reload;

import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Mixin(SimpleResourceReload.class)
@SuppressWarnings("all")
public class MixinSimpleResourceReload {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public <E> boolean onListAdd(List<E> instance, E ee) {
        CompletableFuture<?> e = (CompletableFuture<?>) ee;

        new Thread(() -> {
            try {
                Object result = e.get();
                instance.add((E) CompletableFuture.completedFuture(result));
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }).start();

        return true;
    }
}