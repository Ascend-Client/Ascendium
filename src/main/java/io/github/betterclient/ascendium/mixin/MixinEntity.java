package io.github.betterclient.ascendium.mixin;

import io.github.betterclient.ascendium.BoundingBox;
import io.github.betterclient.ascendium.EntityBridge;
import io.github.betterclient.ascendium.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityBridge {

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow private Box boundingBox;

    @Shadow private int id;

    @Shadow public abstract Vec3d getCameraPosVec(float tickDelta);

    @Shadow public abstract Vec3d getRotationVec(float tickDelta);

    @Override
    public @NotNull Pos3D getPos() {
        return new Pos3D(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public @NotNull BoundingBox getBox() {
        Box bb = this.boundingBox;
        return new BoundingBox(new Pos3D(bb.minX, bb.minY, bb.minZ), new Pos3D(bb.maxX, bb.maxY, bb.maxZ));
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public @NotNull Pos3D getCameraPosVec(int i) {
        Vec3d vec = this.getCameraPosVec((float) i);
        return new Pos3D(vec.x, vec.y, vec.z);
    }

    @Override
    public @NotNull Pos3D getRotationVec(int i) {
        Vec3d vec = this.getRotationVec((float) i);
        return new Pos3D(vec.x, vec.y, vec.z);
    }
}