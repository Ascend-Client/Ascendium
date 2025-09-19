package io.github.betterclient.ascendium.mixin.v1192;

import io.github.betterclient.ascendium.bridge.IdentifierBridge;
import io.github.betterclient.ascendium.bridge.ItemStackBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackBridge {
    @Shadow public abstract int getCount();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract int getDamage();
    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean isEmpty();

    @Override
    public int getItemCount() {
        return this.getCount();
    }

    public @NotNull IdentifierBridge getItemIdentifier() {
        if (this.isEmpty()) {
            return new IdentifierBridge("minecraft", "textures/item/air.png");
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        BakedModel model = renderer.getModel((ItemStack) (Object)this, null, null, 0);

        Random random = Random.create();
        List<BakedQuad> quads = model.getQuads(null, null, random);

        if (quads.isEmpty()) {
            for (Direction direction : Direction.values()) {
                List<BakedQuad> faceQuads = model.getQuads(null, direction, random);
                if (!faceQuads.isEmpty()) {
                    quads = faceQuads;
                    break;
                }
            }
        }

        Sprite sprite;
        if (!quads.isEmpty()) {
            sprite = quads.getFirst().getSprite();
        } else {
            sprite = model.getParticleSprite();
        }

        Identifier spriteId = sprite.getId();
        String texturePath = "textures/" + spriteId.getPath() + ".png";

        return new IdentifierBridge(spriteId.getNamespace(), texturePath);
    }

    @Override
    public float getDurability() {
        int max = this.getMaxDamage();
        if (max <= 0) return 1.0f;
        return 1.0f - ((float) this.getDamage() / (float) max);
    }
}

