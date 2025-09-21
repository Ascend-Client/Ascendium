package io.github.betterclient.ascendium.mixin.v1218;

import io.github.betterclient.ascendium.bridge.ItemStackBridge;
import io.github.betterclient.ascendium.bridge.PlayerBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
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
        return (ItemStackBridge) (Object) this.inventory.getStack(36 + i);
    }

    @Override
    public @NotNull ItemStackBridge getMainHandItem() {
        return (ItemStackBridge) (Object) this.inventory.getSelectedStack();
    }

    @Override
    public String getBiome() {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos blockPos = ((PlayerEntity) (Object) this).getBlockPos();

        RegistryEntry<Biome> biome = client.world.getBiome(blockPos);
        return biome.getKeyOrValue().map(biomeKey -> biomeKey.getValue().toString(), biome_ -> "[unregistered " + biome_ + "]");
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

    @Override
    public float getPYaw() {
        return ((Entity)(Object)(this)).getYaw();
    }

    @Override
    public float getPPitch() {
        return ((Entity)(Object)(this)).getPitch();
    }
}
