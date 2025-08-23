package io.github.betterclient.ascendium.mixin.common;

import io.github.betterclient.ascendium.IdentifierBridge;
import io.github.betterclient.ascendium.ItemStackBridge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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

    @Override
    public IdentifierBridge getItemIdentifier() {
        Identifier id = Registries.ITEM.getId(this.getItem());
        return new IdentifierBridge(id.getNamespace(), id.getPath());
    }

    @Override
    public float getDurability() {
        int max = this.getMaxDamage();
        if (max <= 0) return 1.0f;
        return 1.0f - ((float) this.getDamage() / (float) max);
    }
}

