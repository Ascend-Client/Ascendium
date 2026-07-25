package io.github.betterclient.ascendium.mixin.v2612.common;

import io.github.betterclient.ascendium.bridge.BoundingBox;
import io.github.betterclient.ascendium.bridge.EntityBridge;
import io.github.betterclient.ascendium.bridge.Pos3D;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityBridge {

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow private int id;

    @Shadow
    private AABB bb;

    @Shadow
    public abstract Vec3 getEyePosition(float partialTickTime);

    @Shadow
    public abstract Vec3 getViewVector(float a);

    @Override
    public @NotNull Pos3D getPos() {
        return new Pos3D(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public @NotNull BoundingBox getBox() {
        AABB bb = this.bb;
        return new BoundingBox(new Pos3D(bb.minX, bb.minY, bb.minZ), new Pos3D(bb.maxX, bb.maxY, bb.maxZ));
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public @NotNull Pos3D getCameraPosVec(int i) {
        Vec3 vec = this.getEyePosition((float) i);
        return new Pos3D(vec.x, vec.y, vec.z);
    }

    @Override
    public @NotNull Pos3D getRotationVec(int i) {
        Vec3 vec = this.getViewVector((float) i);
        return new Pos3D(vec.x, vec.y, vec.z);
    }

    @Override
    public @NotNull String getName() {
        return ((Entity) (Object) this).getName().getString();
    }
}