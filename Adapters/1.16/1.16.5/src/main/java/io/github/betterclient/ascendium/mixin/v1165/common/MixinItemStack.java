package io.github.betterclient.ascendium.mixin.v1165.common;

import io.github.betterclient.ascendium.bridge.IdentifierBridge;
import io.github.betterclient.ascendium.bridge.ItemStackBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackBridge {
    @Shadow public abstract int getCount();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract int getDamage();
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
        BakedModel model = renderer.getModels().getModel((ItemStack) (Object) this);

        Sprite sprite = model.getSprite();
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

