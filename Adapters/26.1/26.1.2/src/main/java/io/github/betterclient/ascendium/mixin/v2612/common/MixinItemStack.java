package io.github.betterclient.ascendium.mixin.v2612.common;

import io.github.betterclient.ascendium.bridge.IdentifierBridge;
import io.github.betterclient.ascendium.bridge.ItemStackBridge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackBridge {
    @Shadow public abstract int getCount();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract Item getItem();

    @Shadow
    public abstract int getDamageValue();

    @Override
    public int getItemCount() {
        return this.getCount();
    }

    public @NotNull IdentifierBridge getItemIdentifier() {
        Item item = this.getItem();

        if (item instanceof BlockItem) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(((BlockItem) item).getBlock());
            return new IdentifierBridge(blockId.getNamespace(), "textures/block/" + blockId.getPath() + ".png");
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        String path = itemId.getPath();

        if (path.equals("enchanted_golden_apple")) {
            path = "golden_apple";
        }

        return new IdentifierBridge(itemId.getNamespace(), "textures/item/" + path + ".png");
    }

    @Override
    public float getDurability() {
        int max = this.getMaxDamage();
        if (max <= 0) return 1.0f;
        return 1.0f - ((float) this.getDamageValue() / (float) max);
    }
}

