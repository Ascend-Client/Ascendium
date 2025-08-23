package io.github.betterclient.ascendium.mixin.common;

import io.github.betterclient.ascendium.IdentifierBridge;
import io.github.betterclient.ascendium.ItemStackBridge;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackBridge {
    @Shadow public abstract int getCount();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract int getDamage();
    @Shadow public abstract Item getItem();

    @Override
    public int getItemCount() {
        return this.getCount();
    }

    public @NotNull IdentifierBridge getItemIdentifier() {
        Item item = this.getItem();

        if (item instanceof BlockItem) {
            Identifier blockId = Registries.BLOCK.getId(((BlockItem) item).getBlock());
            return new IdentifierBridge(blockId.getNamespace(), "textures/block/" + blockId.getPath() + ".png");
        }

        Identifier itemId = Registries.ITEM.getId(item);
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
        return 1.0f - ((float) this.getDamage() / (float) max);
    }
}

