package io.github.betterclient.ascendium.mixin.v2612.common;

import io.github.betterclient.ascendium.bridge.ItemStackBridge;
import io.github.betterclient.ascendium.bridge.PlayerBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//Rest are implemented in MixinEntity
@Mixin(Player.class)
public abstract class MixinPlayerEntity implements PlayerBridge {
    @Shadow @Final private Inventory inventory;

    @Override
    public @NotNull ItemStackBridge getArmor(int i) {
        return (ItemStackBridge) (Object) this.inventory.getItem(36 + i);
    }

    @Override
    public @NotNull ItemStackBridge getMainHandItem() {
        return (ItemStackBridge) (Object) this.inventory.getSelectedItem();
    }

    @Override
    public String getBiome() {
        Minecraft client = Minecraft.getInstance();
        BlockPos blockPos = ((Player) (Object) this).getOnPos();

        Holder<Biome> biome = client.level.getBiome(blockPos);
        return biome.unwrap().map(biomeKey -> biomeKey.identifier().toString(), biome_ -> "[unregistered " + biome_ + "]");
    }

    @Override
    public String getFacing() {
        Direction direction = ((Player) (Object) this).getDirection();
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
        return ((Entity)(Object)(this)).getYRot();
    }

    @Override
    public float getPPitch() {
        return ((Entity)(Object)(this)).getXRot();
    }
}
