package io.github.betterclient.ascendium.mixin.common;

import io.github.betterclient.ascendium.ItemStackBridge;
import io.github.betterclient.ascendium.PlayerBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//Rest are implemented in MixinEntity
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements PlayerBridge {
    @Shadow @Final private PlayerInventory inventory;

    @Override
    public @NotNull ItemStackBridge getArmor(int i) {
        return (ItemStackBridge) (Object) this.inventory.armor.get(i);
    }

    @Override
    public @NotNull ItemStackBridge getMainHandItem() {
        return (ItemStackBridge) (Object) this.inventory.getMainHandStack();
    }
}
