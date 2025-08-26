package io.github.betterclient.ascendium.mixin.render;

import io.github.betterclient.ascendium.Bridge;
import io.github.betterclient.ascendium.ui.minecraft.CustomMainMenu;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    /**
     * @author betterclient
     * @reason custom main menu
     */
    @Overwrite
    public void init() {
        Bridge.INSTANCE.getClient().openScreen(CustomMainMenu.INSTANCE);
    }
}