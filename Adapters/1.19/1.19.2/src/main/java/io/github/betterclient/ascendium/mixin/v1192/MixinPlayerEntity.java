package io.github.betterclient.ascendium.mixin.v1192;

import io.github.betterclient.ascendium.bridge.ItemStackBridge;
import io.github.betterclient.ascendium.bridge.PlayerBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

    @Override
    public String getBiome() {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos blockPos = ((PlayerEntity) (Object) this).getBlockPos();

        return client.world.getBiome(blockPos).getKey().map(biomeRegistryKey -> biomeRegistryKey.getValue().getPath()).orElse("plains");
    }

    @Override
    public String getFacing() {
        Direction direction = ((PlayerEntity) (Object) this).getHorizontalFacing();
        return switch (direction) {
            case NORTH -> "North";
            case SOUTH -> "South";
            case WEST -> "West";
            case EAST -> "East";
            default -> "Invalid";
        };
    }
}
